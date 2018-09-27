package ecdcpipeline

import ecdcpipeline.PipelineBuilder
import ecdcpipeline.Container

/**
 * Automated Conan pipeline builder.
 */
class ConanPackageBuilder {

  /**
   * Conan package channel.
   */
  String conanPackageChannel

  private def script
  private PipelineBuilder pipelineBuilder
  private String remoteUploadNode
  private boolean shouldSkipUpload

  /**
   * <p></p>
   *
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   * @param containerBuildNodes map with string keys and {@link
   *   ContainerBuildNode} values
   * @param conanPackageChannel Conan package channel
   */
  ConanPackageBuilder(script, containerBuildNodes, String conanPackageChannel='stable') {
    this.script = script
    this.pipelineBuilder = new PipelineBuilder(script, containerBuildNodes)
    this.conanPackageChannel = conanPackageChannel
    this.remoteUploadNode = ''
    this.shouldSkipUpload = false
  }

  // This method is not called set because Groovy apparently autogenerates a
  // setter with that name, which could not be used in the pipeline script
  // without Jenkins administrator approval.
  /**
   * Define the container build node key to use for remote upload.
   *
   * If a remote upload node is not set, the remote upload step is skipped.
   *
   * @param containerBuildNode key for the {@link ContainerBuildNode} object in
   *   the map
   */
  def defineRemoteUploadNode(String containerBuildNodeKey) {
    remoteUploadNode = containerBuildNodeKey
  }

  /**
   * Skip recipe and package upload stage.
   */
  def skipUpload() {
    shouldSkipUpload = true
  }

  /**
   * Create a map of builders to be passed to a Jenkins {@code parallel} step.
   *
   * The builders include automated local Conan server setup and upload to the
   * local and remote servers.
   *
   * @param pipeline parameterised closure defined with curly braces and the
   *   parameter name before an arrow, where the parameter uses the {@link
   *   Container} interface
   */
  def createPackageBuilders(Closure configurations) {
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
        configurations(container)
      }  // stage

      if (shouldSkipUpload) {
        script.echo 'Skipping upload stage: user request'
      } else if (isPullRequestBuild()) {
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

  /**
   * Add default configuration to a {@link #createPackageBuilders()} closure.
   *
   * @param container the closure parameter
   *
   * @return Jenkins {@code sh} step inside the container
   */
  def addConfiguration(Container container) {
    return addConfiguration(container, [:])
  }

  /**
   * Add custom configuration to a {@link #createPackageBuilders()} closure.
   *
   * @param container the closure parameter
   * @param settingsAndOptions map with optional {@code settings} and {@code
   *   options} keys, whose associated values are maps of property–value pairs
   *
   * @return Jenkins {@code sh} step inside the container
   */
  def addConfiguration(Container container, settingsAndOptions) {
    String settingsString = ''
    if (settingsAndOptions.containsKey('settings')) {
      settingsAndOptions['settings'].each { key, value ->
        settingsString = settingsString + "--settings ${key}=${value} "
      }
    }

    String optionsString = ''
    if (settingsAndOptions.containsKey('options')) {
      settingsAndOptions['options'].each { key, value ->
        settingsString = settingsString + "--options ${key}=${value} "
      }
    }

    return container.createConanPackage(conanPackageChannel, pipelineBuilder.project, settingsString, optionsString)
  }

}
