package ecdcpipeline

import ecdcpipeline.PipelineBuilder
import ecdcpipeline.ScriptStub
import ecdcpipeline.ContainerBuildNode


class PipelineBuilderTest extends GroovyTestCase {
  def script
  def containerContainerBuildNodes

  void setUp() {
    script = new ScriptStub()
    containerContainerBuildNodes = [
      'test': new ContainerBuildNode('repository/image:1.2.3', '/bin/sh')
    ]
  }

  void testCreateBuildersBadContainerBuildNodesArgException() {
    def badContainerBuildNodes = ['fail': 'not a ContainerBuildNode object']
    shouldFail(IllegalArgumentException.class) {
      def pipelineBuilder = new PipelineBuilder(script, badContainerBuildNodes)
    }
  }

  void testPipelineBuilderProperties() {
    def pipelineBuilder = new PipelineBuilder(script, containerContainerBuildNodes)
    assertEquals(pipelineBuilder.project, 'test-project')
    assertEquals(pipelineBuilder.branch, 'master')
    assertEquals(pipelineBuilder.buildNumber, '42')
    assertEquals(pipelineBuilder.baseContainerName, 'test-project-master-42')
  }
}
