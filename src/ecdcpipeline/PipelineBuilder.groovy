package ecdcpipeline

import ecdcpipeline.BuildNode


class PipelineBuilder {
  String project
  String branch
  String buildNumber
  String baseContainerName
  private def script

  PipelineBuilder(script) {
    this.script = script

    def (org, project, branch) = "${script.env.JOB_NAME}".tokenize('/')
    this.project = project
    this.branch = branch

    this.buildNumber = script.env.BUILD_NUMBER

    this.baseContainerName = "${project}-${branch}-${buildNumber}"
  }

  def createBuilders(buildNodes) {
    def builders = [:]

    buildNodes.each {
      name, buildNode ->

      // Check the argument types
      if (buildNode.getClass() != ecdcpipeline.BuildNode.class) {
        throw new IllegalArgumentException("'${name}' is not of type BuildNode")
      }

      builders[name] = createBuilder(name, buildNode)
    }

    return builders
  }

  private def createBuilder(name, buildNode) {
    def containerName = "${baseContainerName}-${name}"
    return {
      script.node('docker') {
        script.dir('code') {
          script.checkout(script.scm)

          try {
            def image = script.docker.image(buildNode.image)
            def container = image.run("\
              --name ${containerName} \
              --tty \
              --cpus=2 \
              --memory=4GB \
              --network=host \
              --env http_proxy=${script.env.http_proxy} \
              --env https_proxy=${script.env.https_proxy} \
              --env local_conan_server=${script.env.local_conan_server} \
            ")
          } finally {
            script.sh("docker stop ${containerName}")
            script.sh("docker rm -f ${containerName}")
          }
        }
      }
    }
  }
}
