package ecdcpipeline

import ecdcpipeline.PipelineBuilder
import ecdcpipeline.ScriptStub
import ecdcpipeline.BuildNode


class PipelineBuilderTest extends GroovyTestCase {
  def script
  def buildNodes

  void setUp() {
    script = new ScriptStub()
    buildNodes = [
      'test': new BuildNode('repository/image:1.2.3', '/bin/sh')
    ]
  }

  void testCreateBuildersBadBuildNodesArgException() {
    def badBuildNodes = ['fail': 'not a BuildNode object']
    shouldFail(IllegalArgumentException.class) {
      def pipelineBuilder = new PipelineBuilder(script, badBuildNodes)
    }
  }

  void testPipelineBuilderProperties() {
    def pipelineBuilder = new PipelineBuilder(script, buildNodes)
    assertEquals(pipelineBuilder.project, 'test-project')
    assertEquals(pipelineBuilder.branch, 'master')
    assertEquals(pipelineBuilder.buildNumber, '42')
    assertEquals(pipelineBuilder.baseContainerName, 'test-project-master-42')
  }
}
