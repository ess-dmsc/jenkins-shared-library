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
   * Publish artifact to ESS GitLab server; package name and versions are
   * checked with {@link isValidPackageArg}.
   *
   * @param artifactPath path to the artifact to be published
   * @param packageName name for the published package
   * @param packageVersion version for the published package
   *
   * @throws IllegalArgumentException if package name or version is invalid.
   */
  def publish(String artifactPath, String packageName, String packageVersion) {
    if (!isValidPackageArg(packageName)) {
      throw new IllegalArgumentException("'${packageName}' is not a valid name for the package")
    }

    if (!isValidPackageArg(packageVersion)) {
      throw new IllegalArgumentException("'${packageVersion}' is not a valid version for the package")
    }

    String projectIdCredentialsId = "ess-gitlab-${pipelineName}-id"
    script.withEnv([
      "gitlab_server=${script.env.ess_gitlab_server}",
      "artifactPath=${artifactPath}",
      "packageName=${packageName}",
      "packageVersion=${packageVersion}"
    ]) {
      script.withCredentials([
        script.string(
          credentialsId: projectIdCredentialsId,
          variable: 'PROJECT_ID'
        ),
        script.string(
          credentialsId: 'ess-gitlab-ecdc-package-token',
          variable: 'PACKAGE_TOKEN'
        )
      ]) {
        script.sh '''
          curl \
            --fail \
            --header "PRIVATE-TOKEN: $PACKAGE_TOKEN" \
            --upload-file "$artifactPath" \
            $gitlab_server/api/v4/projects/$PROJECT_ID/packages/generic/$packageName/$packageVersion/file.txt
        '''
      }  // withCredentials
    }  // withEnv
  }

  private boolean isValidPackageArg(String arg) {
    // Arguments can only contain alphanumeric characters, '.', '-' and '_'.
    return arg ==~ /^[a-zA-Z0-9.\-_]+$/
  }

}
