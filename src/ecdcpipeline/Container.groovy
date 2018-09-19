package ecdcpipeline

import ecdcpipeline.ContainerBuildNode


class Container implements Serializable {
  def script
  String key
  String name
  ContainerBuildNode containerBuildNode

  private final String conanUser = 'ess-dmsc'
  private final String conanRemote = 'ess-dmsc-local'

  Container(script, String key, String name, ContainerBuildNode containerBuildNode) {
    this.script = script
    this.key = key
    this.name = name
    this.containerBuildNode = containerBuildNode
  }

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

  def copyTo(String src, String dst) {
    def resolvedPath = resolveContainerPath(dst)
    return script.sh("docker cp ${src} ${name}:${resolvedPath}")
  }

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
      conan info .
    """
    def conanInfo = containerShell(shellScript, true)
    def fullPackageName = conanInfo.tokenize("\n")[0]
    def packageNameAndVersion = fullPackageName.tokenize('@')[0]
    return packageNameAndVersion
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

  def createConanPackage(String conanPackageChannel, String packageDir, String settings, String options) {
    def shellScript = """
      cd ${packageDir}
      conan create . ${conanUser}/${conanPackageChannel} \
        --build=outdated \
        ${settings} \
        ${options}
    """

    return containerShell(shellScript, false)
  }
}
