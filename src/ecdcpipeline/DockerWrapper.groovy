package ecdcpipeline

import ecdcpipeline.DockerOutputParser

/**
 * Wrapper for Docker commands.
 */
class DockerWrapper implements Serializable {

  private def script
  private def dockerOutputParser

  /**
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   */
  DockerWrapper(script) {
    this.script = script
    this.dockerOutputParser = new DockerOutputParser()
  }

  /**
   * Get locally available images.
   *
   * @returns Map of image IDs to names
   */
  def getImages() {
    def formatStr = this.dockerOutputParser.IMAGES_FORMAT
    def result = this.script.sh(
      script: "docker images --format ${formatStr}",
      returnStdout: true
    ).trim()
    def images = this.dockerOutputParser.parseImages(result)

    return images
  }

  /**
   * Remove images.
   *
   * @param imageIDsToRemove list of image IDs to remove
   */
  def removeImages(imageIDsToRemove) {
    def imageIDsStr = imageIDsToRemove.join(" ")
    this.script.sh("docker rmi --force ${imageIDsStr}")
  }
}
