package ecdcpipeline


class DefaultBuildNodeImages {
  static images = [
    'centos7': [
      'image': 'essdmscdm/centos7-build-node:3.1.0',
      'sh': '/usr/bin/scl enable devtoolset-6 -- /bin/bash -e'
    ],
    'debian9': [
      'image': 'essdmscdm/debian9-build-node:2.1.0',
      'sh': 'bash -e'
    ],
    'ubuntu1804': [
      'image': 'essdmscdm/ubuntu18.04-build-node:1.1.0',
      'sh': 'bash -e'
    ]
  ]
}
