package ecdcpipeline

/**
 * Automated Docker build image builder and uploader.
 */
class ImageBuilder {

  private def script
  private String imageName

  /**
   * <p></p>
   *
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   * @param imageName Docker image name with version
   */
  ImageBuilder(script, String imageName) {
    this.script = script
    this.imageName = imageName
  }

  /**
   * Build Docker image and push to container registry.
   *
   * It is assumed that the Jenkins node has already authenticated with the
   * container registry.
   */
  def buildAndPush() {
    return script.node('docker') {
      // Delete workspace when build is done.
      script.cleanWs()

      script.stage('Checkout') {
        script.checkout script.scm
      }
      
      script.stage('Build') {
        // Add prefix for non-master builds.
        if (script.env.CHANGE_ID) {
          // Pull request.
          this.imageName = "${this.imageName}-pr"
        } else if (script.env.BRANCH_NAME != 'master') {
          // Development branch.
          this.imageName = "${this.imageName}-dev"
        }

        script.echo "Building image ${this.imageName}"
        script.sh """
          docker build \
            --build-arg http_proxy=${script.env.http_proxy} \
            --build-arg https_proxy=${script.env.https_proxy} \
            -t ${this.imageName} .
        """
      }  // stage

      script.stage("Push") {
        if (script.env.BRANCH_NAME == 'master') {
          // Don't overwrite image if it exists.
          try {
            // Is there another way of checking if an image exists?
            script.sh "docker manifest inspect ${this.imageName}"
            script.error "Image ${this.imageName} already exists in registry, cannot push from master."
          } catch (e) {
            script.echo "Image ${this.imageName} not found in registry and will be pushed."
          }
        } else {
          script.echo "Not in master, image will be pushed."
        }

        script.sh "docker push ${this.ImageName}"
      }  // stage
    }  // node
  }  // def

}
