package ecdcpipeline

import ecdcpipeline.BuildNode


class PipelineBuilderTest extends GroovyTestCase {
  void testBuildNodeConstructorException() {
    shouldFail(Exception.class) {
      def bn = new BuildNode('wrong')
    }
  }
}
