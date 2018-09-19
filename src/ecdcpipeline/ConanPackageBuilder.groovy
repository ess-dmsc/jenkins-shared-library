package ecdcpipeline

import ecdcpipeline.PipelineBuilder
import ecdcpipeline.Container


class ConanPackageBuilder {

  String conanPackageChannel

  private def script
  private PipelineBuilder pipelineBuilder
  private String remoteUploadNode

  ConanPackageBuilder(script, containerBuildNodes, String conanPackageChannel='stable') {
    this.script = script
    this.pipelineBuilder = new PipelineBuilder(script, containerBuildNodes)
    this.conanPackageChannel = conanPackageChannel
    this.remoteUploadNode = ''
  }

  def createPackageBuilders(Closure pipeline) {
    def builders = pipelineBuilder.createBuilders { container ->
      pipelineBuilder.stage("${container.key}: checkout") {
        script.dir(pipelineBuilder.project) {
          script.checkout script.scm
        }
        // Copy source code to container
        container.copyTo(pipelineBuilder.project, pipelineBuilder.project)
      }  // stage

      pipelineBuilder.stage("${container.key}: Conan setup") {
        container.setupLocalConanServer()
      }  // stage

      pipelineBuilder.stage("${container.key}: package") {
        pipeline(container)
      }  // stage

      if (isPullRequestBuild()) {
        script.echo 'Skipping upload stage: pull request build'
      } else if (isStableButNotMaster()) {
        script.echo 'Skipping upload stage: only the master branch can upload to the stable channel'
      } else {
        pipelineBuilder.stage("${container.key}: upload") {
          container.uploadLocalConanPackage(pipelineBuilder.project, conanPackageChannel)
          if (container.key == remoteUploadNode) {
            container.uploadRemoteConanRecipe(pipelineBuilder.project, conanPackageChannel)
          }
        }  // stage
      }
    }

    return builders
  }

  private def isPullRequestBuild() {
    if (script.env.CHANGE_ID) {
      return true
    } else {
      return false
    }
  }

  private def isStableButNotMaster() {
    return (conanPackageChannel == 'stable' && script.env.BRANCH_NAME != 'master')
  }

  def addConfiguration(Container container, settingsAndOptions) {
    String settingsString = ''
    settingsAndOptions['settings'].each { key, value ->
      settingsString = settingsString + "--settings ${key}=${value} "
    }

    String optionsString = ''
    settingsAndOptions['options'].each { key, value ->
      settingsString = settingsString + "--options ${key}=${value} "
    }

    return container.createConanPackage(conanPackageChannel, pipelineBuilder.project, settingsString, optionsString)
  }

}
