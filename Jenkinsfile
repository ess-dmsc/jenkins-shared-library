// Run tests for test branch, mirror repository on DMSC GitLab and publish
// documentation to GitHub for master.

if (env.BRANCH_NAME == 'test') {
  @Library('ecdc-pipeline-test')
  import ecdcpipeline.ContainerBuildNode
  import ecdcpipeline.PipelineBuilder
}

properties([disableConcurrentBuilds()])

// Pipeline for test branch job.
containerBuildNodes = [
  'centos': ContainerBuildNode.getDefaultContainerBuildNode('centos7'),
  'ubuntu': ContainerBuildNode.getDefaultContainerBuildNode('ubuntu1804')
]

pipelineBuilder = new PipelineBuilder(this, containerBuildNodes, "/home/jenkins/data:/var/data")
pipelineBuilder.activateEmailFailureNotifications()

builders = pipelineBuilder.createBuilders { container ->
  pipelineBuilder.stage("${container.key}: Checkout") {
    dir(pipelineBuilder.project) {
      scm_vars = checkout scm
    }
    container.copyTo(pipelineBuilder.project, pipelineBuilder.project)
  }  // stage

  pipelineBuilder.stage("${container.key}: Dependencies") {
    def conan_remote = "ess-dmsc-local"
    container.sh """
      mkdir build
      cd build
      conan remote add \
        --insert 0 \
        ${conan_remote} ${local_conan_server}
      conan install --build=outdated ../${pipelineBuilder.project}/jenkins/test/conanfile.txt
    """
  }  // stage
}

node('docker') {
  // Delete workspace when build is done.
  cleanWs()

  dir('code') {
    stage('Checkout') {
      scmVars = checkout scm
    }
  }  // dir

  if (env.BRANCH_NAME == 'test') {
    try {
      parallel builders
    } catch (e) {
      pipelineBuilder.handleFailureMessages()
      throw e
    }  // try/catch
  }  // if

  stage('Create documentation') {
    sh """
      cd code
      /opt/dm_group/groovy/current/bin/groovydoc -sourcepath src -d ../docs 'ecdcpipeline' '*.groovy'
    """
  }  // stage

  if (env.BRANCH_NAME == 'master') {
    dir('code') {
      stage('Push to GitLab') {
        withCredentials([usernamePassword(
          credentialsId: 'dm_jenkins_gitlab_token',
          usernameVariable: 'USERNAME',
          passwordVariable: 'PASSWORD'
        )]) {
          sh """
            git checkout ${env.BRANCH_NAME}
            set +x
            ./jenkins/push-mirror-repo \
              http://git.esss.dk/dm_group/jenkins-shared-library.git \
              HEAD:${env.BRANCH_NAME} \
              ${USERNAME} \
              ${PASSWORD}
          """
        }  // withCredentials
      }  // stage

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
            credentialsId: 'cow-bot-username',
            usernameVariable: 'USERNAME',
            passwordVariable: 'PASSWORD'
          )
        ]) {
          sh "../push-docs-repo ${USERNAME} ${PASSWORD}"
        }  // withCredentials
      }  // stage
    }  // dir
  }  // if
}
