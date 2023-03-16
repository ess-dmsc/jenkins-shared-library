package ecdcpipeline

/**
 * Artifact publisher for releases.
 */
class ArtifactPublisher implements Serializable {

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
  ArtifactPublisher(script, String pipelineName) {
    this.script = script
    this.pipelineName = pipelineName
  }

  /**
   * Publish artifact to ESS GitLab server.
   *
   * @param artifactPath path to the artifact to be published
   * @param packageName name for the published package
   * @param packageVersion version for the published package
   */
  def publish(String artifactPath, String packageName, String packageVersion) {
    String projectIdCredentialsId = "ess-gitlab-${pipelineName}-id"
    script.withEnv([
      "gitlab_server=${script.env.ess_gitlab_server}",
      "artifactPath=${artifactPath}",
      "packageName=${packageName}",
      "packageVersion=${packageVersion}"
    ]) {
      script.withCredentials([script.string(
        credentialsId: projectIdCredentialsId,
        variable: 'PROJECT_ID'
      )]) {
        script.withCredentials([script.string(
          credentialsId: 'ess-gitlab-ecdc-package-token',
          variable: 'PACKAGE_TOKEN'
        )]) {
          script.sh '''
            ls
            curl --header "PRIVATE-TOKEN: $PACKAGE" \
              --upload-file "$artifactPath" \
              $gitlab_server/api/v4/projects/$PROJECT_ID/packages/generic/$package_name/$package_version/file.txt
          '''
        }  // withCredentials
      }  // withCredentials
    }  // withEnv
  }

}
