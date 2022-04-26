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
    return node('docker') {
      // Delete workspace when build is done.
      cleanWs()

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

      stage("Push") {
        if (env.BRANCH_NAME == 'master') {
          // Don't overwrite image if it exists.
          try {
            // How to check if an image exists?
            sh "docker manifest inspect ${image_name}"
            error "Image ${image_name} already exists in registry, cannot push from master."
          } catch (e) {
            echo "Image ${image_name} not found in registry and will be pushed."
          }
        } else {
          echo "Not in master, image will be pushed."
        }

        sh "docker push ${image_name}"
      }  // stage
    }  // node
  }  // def

}
