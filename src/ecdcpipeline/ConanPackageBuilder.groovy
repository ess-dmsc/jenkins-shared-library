package ecdcpipeline

import ecdcpipeline.PipelineBuilder
import ecdcpipeline.Container


class ConanPackageBuilder {

  String conanPackageChannel

  private def script
  private PipelineBuilder pipelineBuilder

  ConanPackageBuilder(script, containerBuildNodes, String conanPackageChannel='master') {
    this.script = script
    this.pipelineBuilder = new PipelineBuilder(script, containerBuildNodes)
    this.conanPackageChannel = conanPackageChannel
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

      pipelineBuilder.stage("${container.key}: upload") {
        container.uploadLocalConanPackage(pipelineBuilder.project, conanPackageChannel)
        if (container.key == 'centos') {
          container.uploadRemoteConanRecipe(pipelineBuilder.project, conanPackageChannel)
        }
      }  // stage
    }  // super.createBuilders

    return builders
  }  // createBuilders

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
