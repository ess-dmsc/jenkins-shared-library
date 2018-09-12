package ecdcpipeline

import ecdcpipeline.BuildNode


class PipelineBuilder {
  String project
  String branch
  private def script

  PipelineBuilder(script) {
    this.script = script

    def (org, project, branch) = "${script.env.JOB_NAME}".tokenize('/')
    this.project = project
    this.branch = branch
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
    return {
      script.node('docker') {
        script.dir('code') {
          script.checkout scm
          script.sh('pwd')
          script.sh('ls -la')
        }
      }
    }
  }
}
