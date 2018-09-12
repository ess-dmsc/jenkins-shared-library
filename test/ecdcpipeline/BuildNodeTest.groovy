package ecdcpipeline

import ecdcpipeline.BuildNode


class BuildNodeTest extends GroovyTestCase {
  static images = [
    'test': [
      'image': 'essdmscdm/test-build-node:1.2.3',
      'sh': '/bin/test-shell -e'
    ]
  ]

  void testBuildNodeConstructorSuccess() {
    def bn = new BuildNode('test', images)
    assertEquals(bn.image, 'essdmscdm/test-build-node:1.2.3')
    assertEquals(bn.shell, '/bin/test-shell -e')
  }

  void testBuildNodeConstructorOSArgException() {
    shouldFail(IllegalArgumentException.class) {
      def bn = new BuildNode('wrong', images)
    }
  }

  void testBuildNodeConstructorImagesArgImageKeyException() {
    def bad_images = [
      'test': [
        'sh': '/bin/test-shell -e'
      ]
    ]

    shouldFail(IllegalArgumentException.class) {
      def bn = new BuildNode('test', bad_images)
    }
  }

  void testBuildNodeConstructorImagesArgShellKeyException() {
    def bad_images = [
      'test': [
        'image': 'essdmscdm/test-build-node:1.2.3'
      ]
    ]

    shouldFail(IllegalArgumentException.class) {
      def bn = new BuildNode('test', bad_images)
    }
  }
}
