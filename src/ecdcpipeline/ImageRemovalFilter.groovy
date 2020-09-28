package ecdcpipeline

/**
 * Filter for selecting images to be removed.
 */
class ImageRemovalFilter implements Serializable {

  /**
   * List of image names to be kept.
   */
  private def imageNamesToKeep

  /**
   * <p></p>
   *
   * @param imageNamesToKeep list of image names to be kept
   */
  ImageRemovalFilter(imageNamesToKeep) {
    this.imageNamesToKeep = imageNamesToKeep
  }

  /**
   * Get a list of filtered image names to remove.
   *
   * @param imageNames list of image names
   *
   * @return List of image names to remove
   */
  def getFilteredImageNames(imageNames) {
    return imageNames - imageNamesToKeep
  }

}
