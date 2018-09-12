package ecdcpipeline

class ScriptStub {
  class Env {
    String JOB_NAME = "ess-dmsc/test-project/master"
  }

  def env = new Env()

  def sh(command) {}
}
