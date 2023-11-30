package ecdcpipeline

import ecdcpipeline.ContainerBuildNode
import ecdcpipeline.Container
import ecdcpipeline.FailureNotifier

/**
 * Parallel pipeline with containers.
 */
class PipelineBuilder implements Serializable {

  /**
   * Repository name.
   */
  String project

  /**
   * Git branch name.
   */
  String branch

  /**
   * Jenkins build number.
   */
  String buildNumber

  /**
   * Base name for setting build node container names.
   */
  String baseContainerName

  /**
   * Number of cores available for building.
   */
  final numCpus = 8

  /**
   * Suggested value for make -j.
   */
  final numMakeJobs = 16

  private def failure_messages
  private def script
  private def containerBuildNodes
  private String hostMounts
  private FailureNotifier failureNotifier

  /**
   * <p></p>
   *
   * The branch name is used to generate the Docker container name; some
   * percent-encoded characters, like '/' and '#', are replaced with '_'.
   *
   * @throws IllegalArgumentException if build node keys have wrong type or
   *   JOB_NAME has wrong number of components.
   *
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   * @param containerBuildNodes map with string keys and {@link
   *   ContainerBuildNode} values
   * @param hostMounts string with host directories to be mounted read-only in
   *   container, with the form "src1:dst1,src2:dst2,..."
   */
  PipelineBuilder(script, containerBuildNodes, hostMounts = "") {
    // Check argument types
    containerBuildNodes.each { key, containerBuildNode ->
      if (containerBuildNode.getClass() != ecdcpipeline.ContainerBuildNode.class) {
        throw new IllegalArgumentException("'${key}' is not of type ContainerBuildNode")
      }
    }

    this.script = script
    this.containerBuildNodes = containerBuildNodes
    this.hostMounts = hostMounts

    def jobName = replacePercentEncodedChars(script.env.JOB_NAME)
    def jobNameElements = jobName.tokenize('/')
    if (jobNameElements.size() != 3) {
      def msg = "'${script.env.JOB_NAME}' is not a valid job name " \
        + "(expected org/project/branch)"
      throw new IllegalArgumentException(msg)
    }
    this.project = jobNameElements[1]
    this.branch = jobNameElements[2]

    this.failure_messages = Collections.synchronizedList([])
    this.buildNumber = script.env.BUILD_NUMBER
    this.baseContainerName = "${project}-${branch}-${buildNumber}"

    this.failureNotifier = new FailureNotifier(jobName)
  }

  /**
   * Activate email failure notifications.
   */
  def activateEmailFailureNotifications() {
    failureNotifier.activateNotificationChannel(FailureNotifier.EMAIL)
  }

  /**
   * Activate Slack failure notifications.
   */
  def activateSlackFailureNotifications() {
    failureNotifier.activateNotificationChannel(FailureNotifier.SLACK)
  }

  /**
   * Handle failures that occurred inside {@link stage} blocks.
   *
   * If failures occurred, the associated failure messages are sent using the
   * activated notification channels.
   */
  def handleFailureMessages() {
    String failureMessage = "The following failures were encountered:\n"
    this.failure_messages.eachWithIndex{message, index -> failureMessage += "${index+1}: ${message}\n"}
    failureNotifier.send(script, failureMessage)
  }

  /**
   * Create a map of builders to be passed to a Jenkins {@code parallel} step.
   *
   * Standard Jenkins pipeline steps are executed on the allocated build node
   * outside the container. To run commands with the container, call the {@link
   * Container} methods on the closure parameter.
   *
   * @param pipeline parameterised closure defined with curly braces and the
   *   parameter name before an arrow, where the parameter uses the {@link
   *   Container} interface
   *
   * @return Map of string keys and builder values
   */
  def createBuilders(Closure pipeline) {
    def builders = [:]
    containerBuildNodes.each { key, containerBuildNode ->
      builders[key] = createBuilder(pipeline, key, containerBuildNode)
    }

    return builders
  }

  /**
   * Abort build if commit message contains '[ci skip]' only skips working
   * branches, not PR or master builds.
   */
  def abortBuildOnMagicCommitMessage() {
      if (script.env.CHANGE_ID == null && script.env.BRANCH_NAME != 'master') {
        def r = script.sh(
          script: "git log -1 | grep '\\[ci skip\\]'",
          returnStatus: true
        )

        if (r == 0) {
          script.echo "Ignoring this build because of commit message"
          script.currentBuild.result = 'ABORTED'
          script.error('Build skipped')
        }
    }
  }

  /**
   * Create and archive BUILD_INFO file.
   *
   * If a file with this name exists in the current directory, build
   * information will be appended to it.
   */
  def archiveBuildInfo() {
    script.sh("""
      touch BUILD_INFO
      echo 'Repository: ${this.project}/${this.branch}' >> BUILD_INFO
      echo 'Commit: ${script.scm_vars.GIT_COMMIT}' >> BUILD_INFO
      echo 'Jenkins build: ${script.env.BUILD_NUMBER}' >> BUILD_INFO
    """)
    script.archiveArtifacts "BUILD_INFO"
  }

  /**
   * Get a Jenkins pipeline stage with automated failure messages.
   *
   * If an exception occurs inside the stage block, the messages are saved for
   * {@link #handleFailureMessages()}, identifying the stage name.
   *
   * @throws Exception if any command  in the block throws an exception
   *
   * @param name stage name
   * @param stageCommands block of commands between curly braces
   *
   */
  def stage(String name, Closure stageCommands) {
    try {
      script.stage(name, stageCommands)
    } catch(e) {
      def msg = "pipeline failed in stage ${name}"
      this.failure_messages.add(msg)
      throw e
    }
  }

  private def createBuilder(Closure pipeline, String key, ContainerBuildNode containerBuildNode) {
    def containerName = "${baseContainerName}-${key}"
    def container = new Container(script, key, containerName, containerBuildNode)

    def mountArgList = []
    if (this.hostMounts != "") {
      def hostMountList = this.hostMounts.tokenize(',')
      for (m in hostMountList) {
        def dirs = m.tokenize(':')
        def src = dirs[0]
        def dst = dirs[1]
        mountArgList += "--mount=type=bind,src=${src},dst=${dst},readonly"
      }
    }
    def mountArgs = mountArgList.join(' ')

    def builder = {
      script.node('docker') {
        try {
          def image = script.docker.image(containerBuildNode.image)
          image.run("\
            --name ${containerName} \
            --tty \
            --cpus=${numCpus} \
            --memory=6GB \
            --network=host \
            --env http_proxy=${script.env.http_proxy} \
            --env https_proxy=${script.env.https_proxy} \
            --env no_proxy=${script.env.no_proxy} \
            --env local_conan_server=${script.env.local_conan_server} \
            ${mountArgs} \
          ")

          container.setupLocalConanServer()
          pipeline(container)
        } finally {
          try {
            container.uploadAllConanPackages()
          } catch(e) {
            println("Failed to upload conan package binaries to local server")
          }
          script.sh("docker stop ${containerName}")
          script.sh("docker rm -f ${containerName}")
          script.cleanWs()
        }
      }
    }

    return builder
  }

  @NonCPS
  private def replacePercentEncodedChars(String s) {
    s = s.replace("%2F", "_")  // '/'
    s = s.replace("%23", "_")  // '#'
    return s
  }
}
