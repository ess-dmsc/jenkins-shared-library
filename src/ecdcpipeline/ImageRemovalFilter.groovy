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
   * @param imageNamesToKeep list of image names to be kept
   */
  ImageRemovalFilter(imageNamesToKeep) {
    this.imageNamesToKeep = imageNamesToKeep
  }

  /**
   * Get a list of filtered image IDs to remove.
   *
   * @param images map of image IDs to names
   *
   * @return List of image IDs to remove
   */
  def getFilteredImageIDs(images) {
    def imageIDsToRemove = []
    images.each { id, name ->
      if (!(name in this.imageNamesToKeep)) {
        imageIDsToRemove << id
      }
    }
    return imageIDsToRemove
  }

}
