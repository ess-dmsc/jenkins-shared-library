package ecdcpipeline

class ScriptStub {
  class Env {
    String JOB_NAME = "ess-dmsc/test-project/master"
    String BUILD_NUMBER = 42
    String http_proxy = "http://proxy"
    String https_proxy = "https://proxy"
    String local_conan_server = "http://local_conan_server"
  }

  class Docker {
    def image(id) {
      return new DockerImage(id)
    }
  }

  class DockerImage {
    String id
    DockerImage(String id) {
      this.id = id
    }

    def run(String args) {
      return new DockerContainer(id, args)
    }
  }

  class DockerContainer {
    String image
    String runArgs
    DockerContainer(String image, String runArgs) {
      this.image = image
      this.runArgs = runArgs
    }
  }

  def env = new Env()
  def docker = new Docker()

  def sh(command) {}
  def node(label) {}
}
