// Mirror repository on DMSC GitLab.

node('docker') {
  stage('Checkout') {
    checkout scm

  }

  stage('Push to GitLab') {
    withCredentials([usernamePassword(
      credentialsId: 'dm_jenkins_gitlab_token',
      usernameVariable: 'USERNAME',
      passwordVariable: 'PASSWORD'
    )]) {
      sh """
        git checkout master
        set +x
        ./jenkins/push-mirror-repo \
          http://git.esss.dk/dm_group/jenkins-shared-library.git \
          ${USERNAME} \
          ${PASSWORD}
      """
    }  // withCredentials
  }  // stage
}