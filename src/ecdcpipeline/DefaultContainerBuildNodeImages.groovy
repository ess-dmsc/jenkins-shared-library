package ecdcpipeline


class DefaultContainerBuildNodeImages {
  static images = [
    'centos7': [
      'image': 'screamingudder/centos7-build-node:4.9.0',
      'shell': '/usr/bin/scl enable devtoolset-6 -- /bin/bash -e -x'
    ],
    'centos7-gcc8': [
      'image': 'screamingudder/centos7-build-node:6.0.4',
      'shell': '/usr/bin/scl enable devtoolset-8 rh-python38 -- /bin/bash -e -x'
    ],
    'debian9': [
      'image': 'screamingudder/debian9-build-node:3.6.0',
      'shell': 'bash -e -x'
    ],
    'debian10': [
      'image': 'screamingudder/debian10-build-node:4.0.1',
      'shell': 'bash -e -x'
    ],
    'ubuntu1804': [
      'image': 'screamingudder/ubuntu18.04-build-node:2.6.0',
      'shell': 'bash -e -x'
    ],
    'ubuntu1804-gcc8': [
      'image': 'screamingudder/ubuntu18.04-build-node:4.0.1',
      'shell': 'bash -e -x'
    ],
    'ubuntu2004': [
      'image': 'screamingudder/ubuntu20.04-build-node:2.0.2',
      'shell': 'bash -e -x'
    ]
  ]
}
