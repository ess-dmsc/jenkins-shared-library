package ecdcpipeline

/**
 * Deployment pipeline trigger.
 */
class DeploymentTrigger implements Serializable {

  /**
   * Jenkins pipeline script.
   */
  def script

  /**
   * Deployment pipeline name.
   */
  String pipelineName

  /**
   * <p></p>
   *
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   * @param pipelineName name of the deployment pipeline to trigger
   */
  DeploymentTrigger(script, String pipelineName) {
    this.script = script
    this.pipelineName = pipelineName
  }

  /**
   * Trigger deployment pipeline
   *
   * @param version version to deploy (passed as a trigger parameter)
   */
  def deploy(String version) {
    String credentialsId = "ess-gitlab-${pipelineName}-url-and-token"
    script.withEnv(["version=${version}"]) {
      script.withCredentials([script.usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'TRIGGER_URL',
        passwordVariable: 'TRIGGER_TOKEN'
      )]) {
        script.sh """
          set +x
          curl -X POST \
            --fail \
            -F token='TRIGGER_TOKEN' \
            -F ref=main \
            -F 'variables[VERSION]=$version' \
            $TRIGGER_URL > /dev/null 2>&1
        """
      }  // withCredentials
    }  // withEnv
  }

}
