package ecdcpipeline


class BuildNode {
  static images = [
    'centos7': [
      'name': 'essdmscdm/centos7-build-node:3.1.0',
      'sh': '/usr/bin/scl enable devtoolset-6 -- /bin/bash -e'
    ],
    'debian9': [
      'name': 'essdmscdm/debian9-build-node:2.1.0',
      'sh': 'bash -e'
    ],
    'ubuntu1804': [
      'name': 'essdmscdm/ubuntu18.04-build-node:1.1.0',
      'sh': 'bash -e'
    ]
  ]

  String type

  BuildNode(String type) {
    if (!images.containsKey(type)) {
      throw new Exception("Invalid build node type: ${type}")
    }
    this.type = type
  }
}
