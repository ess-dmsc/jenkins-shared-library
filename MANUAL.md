# The ECDC Jenkins Pipeline Library

This documents describes the public interface of the ECDC Jenkins Pipeline Library and is a companion to the example Jenkinsfiles available in the *examples* folder. The instructions assume you have added the shared library globally to Jenkins using the name `ecdc-pipeline`, according to the *Making the library available in Jenkins* section of the *README.md* file.


## Making the library available in the pipeline script

Add the following line to a Jenkinsfile to make the library available:

```
@Library('ecdc-pipeline')
```

## Importing classes from the library

To make library classes available in the pipeline, use `import`:

```
import ecdcpipeline.ContainerBuildNode
import ecdcpipeline.PipelineBuilder
```


## Selecting container build nodes

The pipeline builder constructor expects a map with *ContainerBuildNode* objects as values as one of its arguments. The map keys are user-selected strings identifying each container and the values can be created in two different ways:

```
containerBuildNodes = [
  'centos': ContainerBuildNode.getDefaultContainerBuildNode('centos7'),
  'ubuntu': new ContainerBuildNode('essdmscdm/ubuntu18.04-build-node:1.1.0', 'bash -e')
]
```

### `static ContainerBuildNode getDefaultContainerBuildNode(String os)`

Return a default container build node for the operating system `os`. The valid values for this parameter are the keys in `DefaultContainerBuildNodeImages` (defined in *src/DefaultContainerBuildNodeImages.groovy*). This is the recommended approach for Conan package builds.

### `ContainerBuildNode(String image, String shell)`

The *ContainerBuildNode* constructor takes a Docker image name and the shell command to be used with it. This is the recommended approach for regular builds.


## The pipeline builder

The *PipelineBuilder* class provides the interface for creating a parallel pipeline to be run on the selected build node containers.

### `PipelineBuilder(script, containerBuildNodes)`

The *PipelineBuilder* constructor takes a reference to the current pipeline script (`this`) and a map of container build nodes as described above:

```
pipelineBuilder = new PipelineBuilder(this, containerBuildNodes)
```

A *PipelineBuilder* object has string fields that can be used in the build script:

* `project`
* `branch`
* `buildNumber`
* `baseContainerName`

```
sh "mkdir ${pipelineBuilder.project}"
echo pipelineBuilder.branch
```

### `activateEmailFailureNotifications()`

Activate email failure notifications for exceptions inside the *PipelineBuilder* `stage` method.

```
pipelineBuilder.activateEmailFailureNotifications()
```

### `activateSlackFailureNotifications()`

Activate Slack failure notifications for exceptions inside the *PipelineBuilder* `stage` method.

```
pipelineBuilder.activateSlackFailureNotifications()
```

### `createBuilders(Closure pipeline)`

Return a map of builders to be passed to a Jenkins `parallel` step. The argument is a parameterised *Closure* defined with curly braces and the parameter name before an arrow, where the parameter is the container interface (see section below):

```
builders = pipelineBuilder.createBuilders { container ->
  // Stages and steps here.
}
```

### `stage(String name, Closure stageCommands)`

Return a Jenkins pipeline stage enclosed in a `try`/`catch` block, calling the set failure notification handlers (see `activateEmailFailureNotifications` and `activateEmailFailureNotifications` above). The name can be passed inside parentheses and the *Closure* as a block between curly braces:

```
pipelineBuilder.stage("The Stage Name") {
  // Steps here.
}
```


## The container interface

Inside a `createBuilders` argument block, standard Jenkins pipeline steps will be executed on the allocated build node *outside* the container. To run commands with the container, call the methods below on the *Closure* parameter (named `container` in the examples presented in this document).

### `sh(String shellScript)`

Run a shell script inside the container:

```
container.sh "# Simple script"
container.sh """
  # Multiline script
"""
```

### `copyTo(String src, String dst)`

Copy `src` in the build node to `dst` in the container. If `dst` is a relative path, it gets prefixed with `/home/jenkins/`.

```
container.copyTo('code', "pipelineBuilder.project")
```

### `copyFrom(String src, String dst)`

Copy `src` in the container to `dst` in the build node. If `src` is a relative path, it gets prefixed with `/home/jenkins/`.

```
container.copyFrom('build', '.')
```


## Automated Conan package generation and upload

The automated Conan packaging functionality uses the *ConanPackageBuilder* class instead to create builders:

```
import ecdcpipeline.ConanPackageBuilder
```

### `ConanPackageBuilder(script, containerBuildNodes, String conanPackageChannel='stable')`

The *ConanPackageBuilder* constructor takes a reference to the current pipeline script (`this`), a map of container build nodes as described above and the Conan package channel as a string (with a default value of `stable`):

```
packageBuilder = new ConanPackageBuilder(this, containerBuildNodes, conanPackageChannel)
```

### `defineRemoteUploadNode(String containerBuildNodeKey)`

Set the container build node key to use for uploading the package recipe to the remote repository.

```
packageBuilder.defineRemoteUploadNode('centos')
```

### `skipUpload()`

Skip recipe and package upload stage.

```
packageBuilder.skipUpload()
```

### `createPackageBuilders(Closure configurations)`

Return a map of builders to be passed to a Jenkins `parallel` step, with automated local Conan server setup and upload to the local and remote servers. The argument is a parameterised *Closure* defined with curly braces and the parameter name before an arrow, where the parameter is the container interface (see section above):

```
builders = packageBuilder.createPackageBuilders { container ->
  // Configurations here.
}
```

### `def addConfiguration(Container container)`
### `def addConfiguration(Container container, settingsAndOptions)`

Add configurations to a `createPackageBuilders` argument closure. The single-argument version uses default settings and options values, while the two-argument version takes a map with optional `settings` and `options` keys, whose associated values are maps of property–value pairs:

```
packageBuilder.addConfiguration(container, [
  'settings': [
    'librdkafka:build_type': 'Debug'
  ],
  'options': [
    'librdkafka:shared': 'False'
  ]
])
packageBuilder.addConfiguration(container, [
  'options': [
    'librdkafka:shared': 'True'
  ]
])
packageBuilder.addConfiguration(container)
```

## Running the pipelines

The function `parallel` is used to execute the pipelines (e.g. `parallel builders`) and this function will throw an exception on failure of one or more of the pipelines that it tries to execute. To prevent this from immediately stopping the build process and (thus) enable the sending of failure messages (via Slack or email), this function has to be executed in a try statement. This try statment can then implement code for dealing with build failures (i.e. calling `PipelineBuilder.handleFailureMessages()`). A simple way of implementing this is as follows:

```
try {
  parallel builders
  } catch (e) {
    pipeline_builder.handleFailureMessages()
    throw e
}
```
