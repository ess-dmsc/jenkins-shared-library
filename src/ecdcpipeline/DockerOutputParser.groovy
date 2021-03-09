package ecdcpipeline

/**
 * Docker command output parser.
 */
class DockerOutputParser implements Serializable {

  static final IMAGES_FORMAT = "'{{.ID}};{{.Repository}}:{{.Tag}}'"

  def parseImages(imagesStr) {
    def imageList = imagesStr.tokenize("\n")
    def images = [:]
    imageList.each { image ->
      def components = image.tokenize(";")
      images[components[0]] = components[1]
    }
    return images
  }

}
