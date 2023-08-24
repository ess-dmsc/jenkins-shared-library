// Mirror repository on DMSC GitLab and publish documentation to GitHub.

properties([disableConcurrentBuilds()])

node('docker') {
  // Delete workspace when build is done.
  cleanWs()

  dir('code') {
    stage('Checkout') {
      scmVars = checkout scm
    }

    stage('Create documentation') {
      sh """
        /ess/ecdc/groovy/current/bin/groovydoc -sourcepath src -d ../docs 'ecdcpipeline' '*.groovy'
      """
    }  // stage

    if (env.BRANCH_NAME == "master" || env.BRANCH_NAME == "test" || env.BRANCH_NAME == "conan2") {
      stage('Push to GitLab') {
        withCredentials([
          gitUsernamePassword(
            credentialsId: 'dmsc-gitlab-username-with-token',
            gitToolName: 'Default'
          )
        ]) {
          sh '''
            git checkout $BRANCH_NAME
            git push --force http://git.esss.dk/dm_group/jenkins-shared-library.git $BRANCH_NAME
          '''
        }  // withCredentials
      }  // stage
    }  // if
  }  // dir

  if (env.BRANCH_NAME == 'master') {
    dir('code') {
      stage('Publish documentation') {
        withCredentials([string(
          credentialsId: 'jenkins-notification-email',
          variable: 'NOTIFICATION_EMAIL'
        )]) {
          sh '''
            git config user.email $NOTIFICATION_EMAIL
            git config user.name cow-bot
            git config remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'
          '''
        }  // withCredentials

        sh """
          git fetch
          git checkout gh-pages
          rm -rf *
          mv ../docs/* .
          git status
          git add .
          git commit -m 'Jenkins build ${env.BUILD_NUMBER} for ${scmVars.GIT_COMMIT}'
        """

        withCredentials([
          gitUsernamePassword(
            credentialsId: 'cow-bot-username-with-token',
            gitToolName: 'Default'
          )
        ]) {
          sh 'git push'
        }  // withCredentials
      }  // stage
    }  // dir
  }  // if
}
