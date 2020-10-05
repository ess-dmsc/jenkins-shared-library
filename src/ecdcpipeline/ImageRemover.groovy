package ecdcpipeline

import ecdcpipeline.DefaultContainerBuildNodeImages
import ecdcpipeline.DockerOutputParser
import ecdcpipeline.ImageRemovalFilter

/**
 * Container image removal.
 */
class ImageRemover implements Serializable {

  private def dockerWrapper

  /**
   * <p></p>
   *
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   */
  ImageRemover(script) {
    this.dockerWrapper = new DockerWrapper(script)
  }

  /**
   * Remove old images, keeping default build node images.
   *
   * The images kept are the ones in {@link DefaultContainerBuildNodeImages}.
   */
  def cleanImages() {
    def images = this.dockerWrapper.getImages()
    def imageNamesToRemove = this.getImagesToRemove(images)
    if (imageNamesToRemove.size() > 0) {
      this.dockerWrapper.removeImages(imageNamesToRemove)
    }
  }

  private def getImagesToRemove(images) {
    def imageNamesToKeep = this.getDefaultBuildNodeNames()
    def irf = new ImageRemovalFilter(imageNamesToKeep)
    def imageNamesToRemove = irf.getFilteredImageNames(images)

    return imageNamesToRemove
  }

  private def getDefaultBuildNodeNames() {
    def imageValues = DefaultContainerBuildNodeImages.images.values()
    def imageNames = []
    imageValues.each { imageAndShell ->
      imageNames += imageAndShell['image']
    }

    return imageNames
  }

}
