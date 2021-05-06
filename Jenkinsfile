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
        /opt/dm_group/groovy/current/bin/groovydoc -sourcepath src -d ../docs 'ecdcpipeline' '*.groovy'
      """
    }  // stage

    if (env.BRANCH_NAME == "master" || env.BRANCH_NAME == "test") {
      stage('Push to GitLab') {
        withCredentials([usernamePassword(
          credentialsId: 'dmsc-gitlab-username-with-token',
          usernameVariable: 'USERNAME',
          passwordVariable: 'PASSWORD'
        )]) {
          sh '''
            git checkout $BRANCH_NAME
            set +x
            ./jenkins/push-mirror-repo \
              http://git.esss.dk/dm_group/jenkins-shared-library.git \
              HEAD:$BRANCH_NAME \
              $USERNAME \
              $PASSWORD
          '''
        }  // withCredentials
      }  // stage
    }  // if
  }  // dir

  if (env.BRANCH_NAME == 'master') {
    dir('code') {
      stage('Publish documentation') {
        sh """
          cp jenkins/push-docs-repo ..
          git config user.email 'dm-jenkins-integration@esss.se'
          git config user.name 'cow-bot'
          git config remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'
          git fetch
          git checkout gh-pages
          rm -rf *
          mv ../docs/* .
          git status
          git add .
          git commit -m 'Jenkins build ${env.BUILD_NUMBER} for ${scmVars.GIT_COMMIT}'
        """

        withCredentials([
          usernamePassword(
            credentialsId: 'cow-bot-username-with-token',
            usernameVariable: 'USERNAME',
            passwordVariable: 'PASSWORD'
          )
        ]) {
          sh '../push-docs-repo $USERNAME $PASSWORD'
        }  // withCredentials
      }  // stage
    }  // dir
  }  // if
}
