package ecdcpipeline

import ecdcpipeline.DefaultContainerBuildNodeImages

/**
 * Docker container build node for parallel pipelines.
 */
class ContainerBuildNode implements Serializable  {

  /**
   * Docker image.
   */
  String image

  /**
   * Shell command for {@code docker exec}.
   */
  String shell

  /**
   * <p></p>
   *
   * This is the recommended approach for getting container build nodes for
   * regular builds.
   *
   * @param image Docker image
   * @param shell shell command to be used with this image
   */
  ContainerBuildNode(String image, String shell) {
    this.image = image
    this.shell = shell
  }

  /**
   * Get the default container build node for an operating system.
   *
   * The valid values for the parameter are the keys in {@link
   * DefaultContainerBuildNodeImages}. This is the recommended approach for
   * getting container build nodes for Conan package builds.
   *
   * @throws IllegalArgumentException if the argument is not a valid key
   *
   * @param os operating system name
   *
   * @return Container build node object
   */
  static getDefaultContainerBuildNode(String os) {
    def images = DefaultContainerBuildNodeImages.images
    if (!images.containsKey(os)) {
      throw new IllegalArgumentException("'${os}' is not a valid default build node OS")
    }
    return new ContainerBuildNode(images[os]['image'], images[os]['shell'])
  }
}
