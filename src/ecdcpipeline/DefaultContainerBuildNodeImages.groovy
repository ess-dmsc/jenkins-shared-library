package ecdcpipeline


class DefaultContainerBuildNodeImages {
  static images = [
    'centos7-gcc11-qt6': [
      'image': 'registry.esss.lu.se/ecdc/ess-dmsc/docker-centos7-build-node-qt6:12.3.0',
      'shell': '/usr/bin/scl enable devtoolset-11 rh-python38 -- /bin/bash -e -x'
    ],
    'centos7-gcc11': [
      'image': 'registry.esss.lu.se/ecdc/ess-dmsc/docker-centos7-build-node:12.3.0',
      'shell': '/usr/bin/scl enable devtoolset-11 rh-python38 -- /bin/bash -e -x'
    ],
    'almalinux8-gcc12': [
      'image': 'registry.esss.lu.se/ecdc/ess-dmsc/docker-almalinux8-build-node:1.1.0',
      'shell': '/usr/bin/scl enable gcc-toolset-12 -- /bin/bash -e -x'
    ],
    'debian11': [
      'image': 'registry.esss.lu.se/ecdc/ess-dmsc/docker-debian11-build-node:5.2.0',
      'shell': 'bash -e -x'
    ],
    'ubuntu2204': [
      'image': 'registry.esss.lu.se/ecdc/ess-dmsc/docker-ubuntu2204-build-node:5.0.0',
      'shell': 'bash -e -x'
    ]
  ]
}
