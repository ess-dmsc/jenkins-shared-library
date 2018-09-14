package ecdcpipeline

import ecdcpipeline.BuildNode


class Container implements Serializable {
  def script
  String name
  BuildNode buildNode

  Container(script, String name, BuildNode buildNode) {
    this.script = script
    this.name = name
    this.buildNode = buildNode
  }

  def sh(String shellScript) {
    return script.sh("docker exec ${name} ${buildNode.shell} -c \"" + shellScript + "\"")
  }

  def copyTo(String src, String dst) {
    return script.sh("docker cp ${src} ${name}:${dst}")
  }

  def copyFrom(String src, String dst) {
    return script.sh("docker cp ${name}:${src} ${dst}")
  }
}
