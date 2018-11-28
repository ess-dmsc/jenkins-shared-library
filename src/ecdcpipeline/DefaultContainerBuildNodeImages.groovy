package ecdcpipeline


class DefaultContainerBuildNodeImages {
  static images = [
    'centos7': [
      'image': 'essdmscdm/centos7-build-node:3.5.1',
      'shell': '/usr/bin/scl enable devtoolset-6 -- /bin/bash -e -x'
    ],
    'debian9': [
      'image': 'essdmscdm/debian9-build-node:2.5.1',
      'shell': 'bash -e -x'
    ],
    'ubuntu1804': [
      'image': 'essdmscdm/ubuntu18.04-build-node:1.3.1',
      'shell': 'bash -e -x'
    ]
  ]
}
