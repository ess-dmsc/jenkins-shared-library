package ecdcpipeline

import ecdcpipeline.PipelineBuilder
import ecdcpipeline.ScriptStub


class PipelineBuilderTest extends GroovyTestCase {
  def script

  void setUp() {
    script = new ScriptStub()
  }

  void testCreateBuilderBadBuildNodesArgException() {
    def pipelineBuilder = new PipelineBuilder(script)
    def buildNodes = ['fail': 'wrong type']

    shouldFail(IllegalArgumentException.class) {
      def builder = pipelineBuilder.createBuilder(buildNodes)
    }
  }
}
