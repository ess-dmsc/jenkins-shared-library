package ecdcpipeline

import ecdcpipeline.BuildNode
import ecdcpipeline.DefaultBuildNodeImages


class BuildNodeTest extends GroovyTestCase {
  void testBuildNodeConstructor() {
    def bn = new BuildNode('essdmscdm/test-node:1.2.3', '/bin/test-shell -e')
    assertEquals(bn.image, 'essdmscdm/test-node:1.2.3')
    assertEquals(bn.shell, '/bin/test-shell -e')
  }

  void testCreateDefaultBuildNodeSuccess() {
    def bn = BuildNode.createDefaultBuildNode('centos7')
    assertNotNull(bn.image)
    assertNotNull(bn.shell)
  }

  void testCreateDefaultBuildNodeException() {
    shouldFail(IllegalArgumentException.class) {
      def bn = BuildNode.createDefaultBuildNode('fail')
    }
  }
}
