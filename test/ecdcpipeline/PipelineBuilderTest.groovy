package ecdcpipeline

import ecdcpipeline.PipelineBuilder
import ecdcpipeline.ScriptStub
import ecdcpipeline.ContainerBuildNode


class PipelineBuilderTest extends GroovyTestCase {
  def script
  def containerBuildNodes

  void setUp() {
    script = new ScriptStub()
    containerBuildNodes = [
      'test': new ContainerBuildNode('repository/image:1.2.3', '/bin/sh')
    ]
  }

  void testCreateBuildersBadContainerBuildNodesArgException() {
    def badContainerBuildNodes = ['fail': 'not a ContainerBuildNode object']
    shouldFail(IllegalArgumentException.class) {
      def pipelineBuilder = new PipelineBuilder(script, badContainerBuildNodes)
    }
  }

  void testProperties() {
    def pipelineBuilder = new PipelineBuilder(script, containerBuildNodes)
    assertEquals(pipelineBuilder.project, 'test-project')
    assertEquals(pipelineBuilder.branch, 'master')
    assertEquals(pipelineBuilder.buildNumber, '42')
    assertEquals(pipelineBuilder.baseContainerName, 'test-project-master-42')
  }

  void testBadJobNameRaisesException() {
    script.Env.JOB_NAME = "way/too/many/components"
    shouldFail(IllegalArgumentException.class) {
      def pipelineBuilder = new PipelineBuilder(script, containerBuildNodes)
    }

    script.Env.JOB_NAME = "few/components"
    shouldFail(IllegalArgumentException.class) {
      def pipelineBuilder = new PipelineBuilder(script, containerBuildNodes)
    }
  }

  void testBranchNameWithSlashesIsChanged() {
    script.Env.JOB_NAME = "org/project/job%2Fname%2Fwith%2Fslashes"
    def pipelineBuilder = new PipelineBuilder(script, containerBuildNodes)
    assertEquals(pipelineBuilder.branch, "job_name_with_slashes")
  }
}
