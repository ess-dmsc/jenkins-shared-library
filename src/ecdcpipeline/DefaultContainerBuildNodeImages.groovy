package ecdcpipeline


class DefaultContainerBuildNodeImages {
  static images = [
    'centos7': [
      'image': 'screamingudder/centos7-build-node:4.6.0',
      'shell': '/usr/bin/scl enable devtoolset-6 -- /bin/bash -e -x'
    ],
    'debian9': [
      'image': 'screamingudder/debian9-build-node:3.4.0',
      'shell': 'bash -e -x'
    ],
    'ubuntu1804': [
      'image': 'screamingudder/ubuntu18.04-build-node:2.5.0',
      'shell': 'bash -e -x'
    ],
    'alpine': [
      'image': 'screamingudder/alpine-build-node:1.6.0',
      'shell': 'bash -e -x'
    ]
  ]
}