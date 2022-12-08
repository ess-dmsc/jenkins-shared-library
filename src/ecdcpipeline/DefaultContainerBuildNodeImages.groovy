package ecdcpipeline


class DefaultContainerBuildNodeImages {
  static images = [
    'centos7-gcc11': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/centos7-build-node:9.0.0',
      'shell': '/usr/bin/scl enable devtoolset-11 rh-python38 -- /bin/bash -e -x'
    ],
    'centos7-gcc8': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/centos7-build-node:6.4.0',
      'shell': '/usr/bin/scl enable devtoolset-8 rh-python38 -- /bin/bash -e -x'
    ],
    'centos7-pyenv': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/centos7-build-node:10.0.0-dev',
      'shell': '/bin/bash -e -x'
    ],
    'debian10': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/debian10-build-node:4.3.0',
      'shell': 'bash -e -x'
    ],
    'debian11': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/debian11-build-node:2.3.0',
      'shell': 'bash -e -x'
    ],
    'ubuntu2004': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/ubuntu20.04-build-node:3.2.0',
      'shell': 'bash -e -x'
    ],
    'ubuntu2204': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/ubuntu22.04-build-node:2.3.0',
      'shell': 'bash -e -x'
    ]
  ]
}
