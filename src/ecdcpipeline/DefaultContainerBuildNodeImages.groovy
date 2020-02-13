package ecdcpipeline


class DefaultContainerBuildNodeImages {
  static images = [
    'centos7': [
      'image': 'screamingudder/centos7-build-node:4.9.0',
      'shell': '/usr/bin/scl enable devtoolset-6 -- /bin/bash -e -x'
    ],
    'centos7-gcc8': [
      'image': 'screamingudder/centos7-build-node:5.0.4',
      'shell': '/usr/bin/scl enable devtoolset-8 -- /bin/bash -e -x'
    ],
    'debian9': [
      'image': 'screamingudder/debian9-build-node:3.5.0',
      'shell': 'bash -e -x'
    ],
    'debian10': [
      'image': 'screamingudder/debian10-build-node:1.0.1',
      'shell': 'bash -e -x'
    ],
    'ubuntu1804': [
      'image': 'screamingudder/ubuntu18.04-build-node:2.6.0',
      'shell': 'bash -e -x'
    ],
    'ubuntu1804-gcc8': [
      'image': 'screamingudder/ubuntu18.04-build-node:3.0.1',
      'shell': 'bash -e -x'
    ],
    'alpine': [
      'image': 'screamingudder/alpine-build-node:1.8.0',
      'shell': 'bash -e -x'
    ]
  ]
}
