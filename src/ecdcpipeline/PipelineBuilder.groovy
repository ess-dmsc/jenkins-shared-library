package ecdcpipeline

import ecdcpipeline.BuildNode
import ecdcpipeline.Container
import ecdcpipeline.FailureNotifier


class PipelineBuilder implements Serializable {
  String project
  String branch
  String buildNumber
  String baseContainerName

  private def script
  private def buildNodes
  private FailureNotifier failureNotifier

  PipelineBuilder(script, buildNodes) {
    // Check the argument types
    buildNodes.each { key, buildNode ->
      if (buildNode.getClass() != ecdcpipeline.BuildNode.class) {
        throw new IllegalArgumentException("'${key}' is not of type BuildNode")
      }
    }

    this.script = script
    this.buildNodes = buildNodes

    def (org, project, branch) = "${script.env.JOB_NAME}".tokenize('/')
    this.project = project
    this.branch = branch
    this.buildNumber = script.env.BUILD_NUMBER
    this.baseContainerName = "${project}-${branch}-${buildNumber}"

    this.failureNotifier = new FailureNotifier(script.env.JOB_NAME)
  }

  def activateEmailFailureNotifications() {
    failureNotifier.activateNotificationChannel(FailureNotifier.EMAIL)
  }

  def activateSlackFailureNotifications() {
    failureNotifier.activateNotificationChannel(FailureNotifier.SLACK)
  }

  def createBuilders(Closure pipeline) {
    def builders = [:]
    buildNodes.each { key, buildNode ->
      builders[key] = createBuilder(pipeline, key, buildNode)
    }

    return builders
  }

  def stage(String name, Closure stageCommands) {
    try {
      script.stage(name, stageCommands)
    } catch(e) {
      def msg = "${project} failed in stage ${name}"
      throw e
    }
  }

  private def createBuilder(Closure pipeline, String key, BuildNode buildNode) {
    def containerName = "${baseContainerName}-${key}"
    def container = new Container(script, containerName, buildNode)

    def builder = {
      script.node('docker') {
        script.dir('code') {
          script.checkout(script.scm)
        }

        try {
          def image = script.docker.image(buildNode.image)
          image.run("\
            --name ${containerName} \
            --tty \
            --cpus=2 \
            --memory=4GB \
            --network=host \
            --env http_proxy=${script.env.http_proxy} \
            --env https_proxy=${script.env.https_proxy} \
            --env local_conan_server=${script.env.local_conan_server} \
          ")

          // Copy cloned repository to container
          script.sh("docker cp code ${containerName}:/home/jenkins/")

          pipeline(container)
        } finally {
          script.sh("docker stop ${containerName}")
          script.sh("docker rm -f ${containerName}")
        }
      }
    }

    return builder
  }
}
