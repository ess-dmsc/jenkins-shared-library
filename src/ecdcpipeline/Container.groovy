package ecdcpipeline

import ecdcpipeline.ContainerBuildNode

/**
 * Docker container wrapper for Jenkins pipeline commands.
 */
class Container implements Serializable {

  /**
   * Jenkins pipeline script.
   */
  def script

  /**
   * Container build node key in map.
   */
  String key

  /**
   * Docker container name.
   */
  String name

  /**
   * Object with Docker container image and shell definition.
   */
  ContainerBuildNode containerBuildNode

  private final String conanUser = 'ess-dmsc'
  private final String conanRemote = 'ess-dmsc-local'

  /**
   * <p></p>
   *
   * @param script reference to the current pipeline script ({@code this} in a
   *   Jenkinsfile)
   * @param key container build node key in map
   * @param name Docker container name
   * @param containerBuildNode object with Docker container image and shell
   *   definition.
   */
  Container(script, String key, String name, ContainerBuildNode containerBuildNode) {
    this.script = script
    this.key = key
    this.name = name
    this.containerBuildNode = containerBuildNode
  }

  /**
   * Run shell script inside the container
   *
   * @param shellScript shell script to be run
   *
   * @return Jenkins pipeline {@code sh} step with the script run inside the
   *   container using the shell defined in the {@link ContainerBuildNode}
   *   object
   */
  def sh(String shellScript) {
    return containerShell(shellScript, false)
  }

  private def containerShell(String shellScript, Boolean getStdout) {
    def result
    if (getStdout) {
      result = script.sh(
        script: "docker exec ${name} ${containerBuildNode.shell} -c \"" + shellScript + "\"",
        returnStdout: true
      ).trim()
    } else {
      result = script.sh("docker exec ${name} ${containerBuildNode.shell} -c \"" + shellScript + "\"")
    }

    return result
  }

  /**
   * Copy {@code src} in the build node to {@code dst} in the container.
   *
   * If {@code dst} is a relative path, it gets prefixed with @{code
   *  /home/jenkins/}.
   *
   * @param src source path in build node
   * @param dst destination path in container
   */
  def copyTo(String src, String dst) {
    def resolvedPath = resolveContainerPath(dst)
    return script.sh("docker cp ${src} ${name}:${resolvedPath}")
  }

  /**
   * Copy {@code src} in the container to {@code dst} in the build node.
   *
   * If {@code src} is a relative path, it gets prefixed with @{code
   *  /home/jenkins/}.
   *
   * @param src source path in container
   * @param dst destination path in build node
   */
  def copyFrom(String src, String dst) {
    def resolvedPath = resolveContainerPath(src)
    return script.sh("docker cp ${name}:${resolvedPath} ${dst}")
  }

  def setupLocalConanServer() {
    script.withCredentials([
      script.string(
        credentialsId: 'local-conan-server-password',
        variable: 'CONAN_PASSWORD'
      )
    ]) {
      sh """
        set +x
        conan remote add \
          --insert 0 \
          ${conanRemote} ${script.env.local_conan_server}
        conan user \
          --password '${script.CONAN_PASSWORD}' \
          --remote ${conanRemote} \
          ${conanUser} \
          > /dev/null
      """
    }  // withCredentials
  }

  def uploadLocalConanPackage(String packageDir, String conanPackageChannel) {
    def conanUploadFlag = getConanUploadFlag(conanPackageChannel)
    def packageNameAndVersion = getPackageNameAndVersion(packageDir)
    sh """
      conan upload \
        --all \
        ${conanUploadFlag} \
        --remote ${conanRemote} \
        ${packageNameAndVersion}@${conanUser}/${conanPackageChannel}
    """
  }

  def uploadRemoteConanRecipe(String packageDir, String conanPackageChannel) {
    script.withCredentials([
      script.usernamePassword(
        credentialsId: 'cow-bot-bintray-username-and-api-key',
        passwordVariable: 'COWBOT_PASSWORD',
        usernameVariable: 'COWBOT_USERNAME'
      )
    ]) {
      sh """
        set +x
        conan user \
          --password '${script.COWBOT_PASSWORD}' \
          --remote ess-dmsc \
          ${script.COWBOT_USERNAME} \
          > /dev/null
      """
    }  // withCredentials

    def conanUploadFlag = getConanUploadFlag(conanPackageChannel)
    def packageNameAndVersion = getPackageNameAndVersion(packageDir)
    sh """
      conan upload \
        ${conanUploadFlag} \
        --remote ess-dmsc \
        ${packageNameAndVersion}@${conanUser}/${conanPackageChannel}
    """
  }

  private def getConanUploadFlag(String conanPackageChannel) {
    def conanUploadFlag
    if (conanPackageChannel == 'stable') {
      conanUploadFlag = '--no-overwrite'
    } else {
      conanUploadFlag = ''
    }

    return conanUploadFlag
  }

  private def getPackageNameAndVersion(String packageDir) {
    def shellScript = """
      cd ${packageDir}
      conan inspect --attribute name --attribute version .
    """
    def conanInspection = containerShell(shellScript, true)
    def conanInspectionLines = conanInspection.tokenize("\n")
    def packageName = conanInspectionLines[0].tokenize(": ")[1]
    def packageVersion = conanInspectionLines[1].tokenize(": ")[1]

    return "${packageName}/${packageVersion}"
  }

  private def resolveContainerPath(String path) {
    String resolvedPath

    def trimmedPath = path.trim()
    if (trimmedPath.startsWith('/')) {
      resolvedPath = trimmedPath
    } else {
      resolvedPath = '/home/jenkins/' + trimmedPath
    }

    return resolvedPath
  }

  def createConanPackage(String conanPackageChannel, String packageDir, String settings, String options, String env) {
    def shellScript = """
      cd ${packageDir}
      ${env} conan create . ${conanUser}/${conanPackageChannel} \
        --build=outdated \
        ${settings} \
        ${options}
    """

    return containerShell(shellScript, false)
  }
}
