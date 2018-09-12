package ecdcpipeline

import ecdcpipeline.DefaultBuildNodeImages


class BuildNode {
  String image
  String shell

  BuildNode(String os, def images=DefaultBuildNodeImages.images) {
    images.each {
      key, value ->
        if (!value.containsKey('image')) {
          throw new IllegalArgumentException("'${key}' has no 'image' key")
        }

        if (!value.containsKey('sh')) {
          throw new IllegalArgumentException("'${key}' has no 'sh' key")
        }
    }

    if (!images.containsKey(os)) {
      throw new IllegalArgumentException("Invalid build node: ${os}")
    }

    this.image = images[os]['image']
    this.shell = images[os]['sh']
  }
}
