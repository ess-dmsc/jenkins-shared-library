package ecdcpipeline

import ecdcpipeline.DefaultContainerBuildNodeImages
import ecdcpipeline.DockerOutputParser
import ecdcpipeline.ImageRemovalFilter

/**
 * Container image removal.
 */
class ImageRemover implements Serializable {

  private def script
  private def dockerWrapper

  /**
   * <p></p>
   *
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   */
  ImageRemover(script) {
    this.script = script
    this.dockerWrapper = new DockerWrapper()
  }

  /**
   * Remove old images, keeping default build node images.
   *
   * The images kept are the ones in {@link DefaultContainerBuildNodeImages}.
   */
  def cleanImages() {
    def images = this.dockerWrapper.getImages()
    def imageNamesToRemove = this.getImagesToRemove(images)
    this.dockerWrapper.removeImages(imageNamesToRemove)
  }

  private def getImagesToRemove(images) {
    def imageNamesToKeep = this.getDefaultBuildNodeNames()
    def irf = new ImageRemovalFilter(imageNamesToKeep)
    def imageNamesToRemove = irf.getFilteredImageNames(imageNames)

    return imageNamesToRemove
  }

  private def getDefaultBuildNodeNames() {
    def imageValues = DefaultContainerBuildNodeImages.images.values()
    def imageNames = []
    imageValues.each { image, shell ->
      imageNames += image
    }

    return imageNames
  }

}
