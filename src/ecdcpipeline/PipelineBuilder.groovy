package ecdcpipeline

import ecdcpipeline.BuildNode


class PipelineBuilder {
  private def script

  PipelineBuilder(script) {
    this.script = script
  }

  def createBuilder(buildNodes) {
    buildNodes.each {
      name, buildNode ->
        if (buildNode.getClass() != ecdcpipeline.BuildNode.class) {
          throw new IllegalArgumentException("'${name}' is not of type BuildNode")
        }
    }
    return {

    }
  }
}
