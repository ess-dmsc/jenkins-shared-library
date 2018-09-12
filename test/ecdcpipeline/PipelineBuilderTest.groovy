package ecdcpipeline

import ecdcpipeline.PipelineBuilder
import ecdcpipeline.ScriptStub


class PipelineBuilderTest extends GroovyTestCase {
  def script
  def pipelineBuilder

  void setUp() {
    script = new ScriptStub()
    pipelineBuilder = new PipelineBuilder(script)
  }

  void testCreateBuildersBadBuildNodesArgException() {
    def buildNodes = ['fail': 'wrong type']
    shouldFail(IllegalArgumentException.class) {
      def builders = pipelineBuilder.createBuilders(buildNodes)
    }
  }

  void testPipelineBuilderProperties() {
    assertEquals(pipelineBuilder.project, 'test-project')
    assertEquals(pipelineBuilder.branch, 'master')
    assertEquals(pipelineBuilder.buildNumber, '42')
    assertEquals(pipelineBuilder.baseContainerName, 'test-project-master-42')
  }
}
