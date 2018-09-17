package ecdcpipeline

import ecdcpipeline.DefaultContainerBuildNodeImages


class ContainerBuildNode  implements Serializable  {
  String image
  String shell

  ContainerBuildNode(String image, String shell) {
    this.image = image
    this.shell = shell
  }

  static getDefaultContainerBuildNode(String os) {
    def images = DefaultContainerBuildNodeImages.images
    if (!images.containsKey(os)) {
      throw new IllegalArgumentException("'${os}' is not a valid default build node OS")
    }
    return new ContainerBuildNode(images[os]['image'], images[os]['shell'])
  }
}
