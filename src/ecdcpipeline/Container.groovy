package ecdcpipeline

import ecdcpipeline.ContainerBuildNode


class Container implements Serializable {
  def script
  String key
  String name
  ContainerBuildNode containerBuildNode

  Container(script, String key, String name, ContainerBuildNode containerBuildNode) {
    this.script = script
    this.key = key
    this.name = name
    this.containerBuildNode = containerBuildNode
  }

  def sh(String shellScript) {
    return script.sh("docker exec ${name} ${containerBuildNode.shell} -c \"" + shellScript + "\"")
  }

  def copyTo(String src, String dst) {
    def resolvedPath = resolveContainerPath(dst)
    return script.sh("docker cp ${src} ${name}:${resolvedPath}")
  }

  def copyFrom(String src, String dst) {
    def resolvedPath = resolveContainerPath(src)
    return script.sh("docker cp ${name}:${resolvedPath} ${dst}")
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
}
