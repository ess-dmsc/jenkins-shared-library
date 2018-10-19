package ecdcpipeline

/**
 * Node status information.
 */
class NodeStatus implements Serializable {
  /**
   * Check whether any macOS build nodes are online.
   */
  static isMacOSOnline() {
    return isNodeWithLabelOnline('macos')
  }

  /**
   * Check whether any Windows 10 build nodes are online.
   */
  static isWindowsOnline() {
    return isNodeWithLabelOnline('windows10')
  }

  private static isNodeWithLabelOnline(String label) {
    def labels = Jenkins.get().getLabel(label)
    def nodes = labels.getNodes()

    for (node in nodes) {
      if (node.toComputer().isOnline()) {
        return true
      }
    }

    // No node with this label is online.
    return false
  }
}
