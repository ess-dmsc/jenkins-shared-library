package ecdcpipeline

/**
 * Docker command output parser.
 */
class DockerOutputParser implements Serializable {

  static final IMAGES_FORMAT = "'{{.Repository}}:{{.Tag}}'"

  def parseImages(output) {
    def images = output.tokenize("\n")
    return images
  }

}
