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

  private def failure_messages
  private def script
  private def containerBuildNodes
  private String hostMounts
  private FailureNotifier failureNotifier

  /**
   * <p></p>
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

    def (org, project, branch) = "${script.env.JOB_NAME}".tokenize('/')
    this.project = project
    this.branch = branch
    this.failure_messages = Collections.synchronizedList([])
    this.buildNumber = script.env.BUILD_NUMBER
    this.baseContainerName = "${project}-${branch}-${buildNumber}"

    this.failureNotifier = new FailureNotifier(script.env.JOB_NAME)
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
   *
   */
  def abortBuildOnMagicCommitMessage() {
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
            --env local_conan_server=${script.env.local_conan_server} \
            ${mountArgs} \
          ")

          pipeline(container)
        } finally {
          try {
            container.setupConanUser()
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
}
