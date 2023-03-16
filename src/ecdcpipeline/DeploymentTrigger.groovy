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
    String projectIdCredentialsId = "ess-gitlab-${pipelineName}-id"
    String tokenCredentialsId = "ess-gitlab-${pipelineName}-token"
    script.withEnv([
      "gitlab_server=${script.env.ess_gitlab_server}",
      "version=${version}"
    ]) {
      script.withCredentials([script.string(
        credentialsId: projectIdCredentialsId,
        variable: 'PROJECT_ID'
      )]) {
        script.withCredentials([script.string(
          credentialsId: tokenCredentialsId,
          variable: 'DEPLOYMENT_TOKEN'
        )]) {
          script.sh '''
            set +x
            curl -X POST \
              --fail \
              -F token=$DEPLOYMENT_TOKEN \
              -F ref=main \
              -F variables[VERSION]=$version \
              $gitlab_server/api/v4/projects/$PROJECT_ID/trigger/pipeline > /dev/null 2>&1
          '''
        }  // withCredentials
      }  // withCredentials
    }  // withEnv
  }

}
