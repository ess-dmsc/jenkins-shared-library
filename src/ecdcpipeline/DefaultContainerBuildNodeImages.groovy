package ecdcpipeline


class DefaultContainerBuildNodeImages {
  static images = [
    'centos7-gcc11': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/centos7-build-node:12.0.0',
      'shell': '/usr/bin/scl enable devtoolset-11 rh-python38 -- /bin/bash -e -x'
    ],
    'almalinux8-gcc12': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/almalinux8-build-node:1.0.0',
      'shell': '/usr/bin/scl enable gcc-toolset-12 -- /bin/bash -e -x'
    ],
    'debian11': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/debian11-build-node:5.0.0',
      'shell': 'bash -e -x'
    ],
    'ubuntu2204': [
      'image': 'dockerregistry.esss.dk/ecdc_group/build-node-images/ubuntu22.04-build-node:4.0.0',
      'shell': 'bash -e -x'
    ]
  ]
}
