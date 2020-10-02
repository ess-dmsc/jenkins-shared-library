package ecdcpipeline

import ecdcpipeline.DockerOutputParser

/**
 * Wrapper for Docker commands.
 */
class DockerWrapper implements Serializable {

  private def dockerOutputParser

  /**
   * <p></p>
   */
  DockerWrapper() {
    this.dockerOutputParser = new DockerOutputParser()
  }

  /**
   * Get locally available images.
   *
   * @returns List of image names
   */
  def getImages() {
    def formatStr = this.dockerOutputParser.IMAGES_FORMAT
    def result = "docker images --format ${formatStr}".execute().text.trim()
    println(result)
    def images = this.dockerOutputParser.parseImages(result)

    return images
  }

  /**
   * Remove images.
   *
   * @param imageNamesToRemove list of image names to remove
   */
  def removeImages(imageNamesToRemove) {
    def imageNamesStr = imageNamesToRemove.join(" ")
    // this.script.sh("docker rmi ${imageNamesStr}")
    println("docker rmi ${imageNamesStr}")
  }
}
