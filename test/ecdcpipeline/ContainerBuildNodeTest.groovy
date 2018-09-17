package ecdcpipeline

import ecdcpipeline.ContainerBuildNode
import ecdcpipeline.DefaultContainerBuildNodeImages


class ContainerBuildNodeTest extends GroovyTestCase {
  void testContainerBuildNodeConstructor() {
    def bn = new ContainerBuildNode('essdmscdm/test-node:1.2.3', '/bin/test-shell -e')
    assertEquals(bn.image, 'essdmscdm/test-node:1.2.3')
    assertEquals(bn.shell, '/bin/test-shell -e')
  }

  void testGetDefaultContainerBuildNodeSuccess() {
    def bn = ContainerBuildNode.getDefaultContainerBuildNode('centos7')
    assertNotNull(bn.image)
    assertNotNull(bn.shell)
  }

  void testGetDefaultContainerBuildNodeException() {
    shouldFail(IllegalArgumentException.class) {
      def bn = ContainerBuildNode.getDefaultContainerBuildNode('fail')
    }
  }
}
