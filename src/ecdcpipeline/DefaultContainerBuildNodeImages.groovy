package ecdcpipeline


class DefaultContainerBuildNodeImages {
  static images = [
    'centos7-gcc11': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/centos7-build-node:11.1.0',
      'shell': '/usr/bin/scl enable devtoolset-11 rh-python38 -- /bin/bash -e -x'
    ],
    'centos7-gcc8': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/centos7-build-node:6.4.0',
      'shell': '/usr/bin/scl enable devtoolset-8 rh-python38 -- /bin/bash -e -x'
    ],
    'debian11': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/debian11-build-node:3.1.0',
      'shell': 'bash -e -x'
    ],
    'ubuntu2204': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/ubuntu22.04-build-node:3.1.0',
      'shell': 'bash -e -x'
    ]
  ]
}
