package ecdcpipeline

import ecdcpipeline.DefaultBuildNodeImages


class BuildNode  implements Serializable  {
  String image
  String shell

  BuildNode(String image, String shell) {
    this.image = image
    this.shell = shell
  }

  static getDefaultBuildNode(String os) {
    def images = DefaultBuildNodeImages.images
    if (!images.containsKey(os)) {
      throw new IllegalArgumentException("'${os}' is not a valid default build node OS")
    }
    return new BuildNode(images[os]['image'], images[os]['shell'])
  }
}
