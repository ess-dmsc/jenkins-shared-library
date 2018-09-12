package ecdcpipeline

import ecdcpipeline.BuildNode


class PipelineBuilder {
  private def script

  PipelineBuilder(script) {
    this.script = script
  }

  def createBuilders(buildNodes) {
    def builders = [:]

    buildNodes.each {
      name, buildNode ->

      // Check the argument types
      if (buildNode.getClass() != ecdcpipeline.BuildNode.class) {
        throw new IllegalArgumentException("'${name}' is not of type BuildNode")
      }

      builders[name] = {
        node('docker') {}
      }
    }

    return {

    }
  }
}
