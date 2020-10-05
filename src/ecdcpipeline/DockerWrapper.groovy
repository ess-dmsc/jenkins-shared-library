package ecdcpipeline

import ecdcpipeline.DockerOutputParser

/**
 * Wrapper for Docker commands.
 */
class DockerWrapper implements Serializable {

  private def script
  private def dockerOutputParser

  /**
   * <p></p>
   *
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
   * @returns List of image names
   */
  def getImages() {
    def formatStr = dockerOutputParser.IMAGES_FORMAT
    def result = script.sh(
      script: "docker images --format ${formatStr}",
      returnStdout: true
    )
    println("Testing result:")
    println(result.size())
    def images = dockerOutputParser.parseImages(result)

    println(script.sh(script: "pwd", returnStdout: true))

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
