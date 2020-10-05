package ecdcpipeline

/**
 * Filter for selecting images to be removed.
 */
class ImageRemovalFilter implements Serializable {

  /**
   * List of image names to be kept.
   */
  private def imageNamesToKeep
  private def script

  /**
   * <p></p>
   *
   * @param imageNamesToKeep list of image names to be kept
   */
  ImageRemovalFilter(imageNamesToKeep, script) {
    this.imageNamesToKeep = imageNamesToKeep
    this.script = script
  }

  /**
   * Get a list of filtered image names to remove.
   *
   * @param imageNames list of image names
   *
   * @return List of image names to remove
   */
  def getFilteredImageNames(imageNames) {
    this.script.echo "Inside filter class"
    this.script.echo "All images:"
    this.script.echo "${imageNames}"
    this.script.echo "To Keep:"
    this.script.echo "${imageNamesToKeep}"

    return imageNames - imageNamesToKeep
  }

}
