package ecdcpipeline

/**
 * Docker command output parser.
 */
class DockerOutputParser implements Serializable {

  static final IMAGES_FORMAT = "'{{.Repository}}:{{.Tag}}'"

  def parseImages(imagesStr) {
    def images = imagesStr.tokenize("\n")
    return images
  }

}
