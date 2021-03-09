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
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   */
  ImageRemover(script) {
    this.script = script
    this.dockerWrapper = new DockerWrapper(script)
  }

  /**
   * Remove old images, keeping default build node images.
   *
   * The images kept are the ones in {@link DefaultContainerBuildNodeImages}.
   */
  def cleanImages() {
    def images = this.dockerWrapper.getImages()
    this.script.echo "Existing Docker images: ${images}"
    def imagesToRemove = this.getImagesToRemove(images)
    if (imagesToRemove.size() > 0) {
      this.script.echo "Images to be removed: ${imagesToRemove}"
      this.dockerWrapper.removeImages(imagesToRemove)
    }
  }

  private def getImagesToRemove(images) {
    def imageNamesToKeep = this.getDefaultBuildNodeNames()
    def irf = new ImageRemovalFilter(imageNamesToKeep)
    def imagesToRemove = irf.getFilteredImageIDs(images)

    return imagesToRemove
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
