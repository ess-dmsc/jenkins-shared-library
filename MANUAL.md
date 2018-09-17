# The ECDC Jenkins Pipeline Library

This documents describes the public interface of the ECDC Jenkins Pipeline Library and is a companion to the example Jenkinsfile. The instructions assume you have added the shared library globally to Jenkins using the name `ecdc-pipeline`, according to the *Making the library available in Jenkins* section of the *README.md* file.


## Making the library available in the pipeline script

Add the following lines to a Jenkinsfile to make the *ContainerBuildNode* and *PipelineBuilder* library classes available:

```
@Library('ecdc-pipeline')
import ecdcpipeline.ContainerBuildNode
import ecdcpipeline.PipelineBuilder
```


## Selecting container build nodes

The pipeline builder constructor expects a map with *ContainerBuildNode* objects as values as one of its arguments. The map keys are user-selected strings identifying each container and the values can be created in two different ways:

### `ContainerBuildNode getDefaultContainerBuildNode(String os)`

Return a default container build node for the operating system `os`. The valid values for this parameter are the keys in `DefaultContainerBuildNodeImages` (defined in *src/DefaultContainerBuildNodeImages.groovy*). This is the recommended approach.

### `ContainerBuildNode(String image, String shell)`

The *ContainerBuildNode* constructor takes a Docker image name and the shell command to be used with it.


## The pipeline builder

The *PipelineBuilder* class provides the interface for creating a parallel pipeline to be run on the selected build node containers.

### `PipelineBuilder(script, containerContainerBuildNodes)`

The  *PipelineBuilder* constructor takes a reference to the current pipeline script (`this`) and a map of container build nodes as described above:

```
pipelineBuilder = new PipelineBuilder(this, containerContainerBuildNodes)
```

A *PipelineBuilder* object has string fields that can be used in the build script:

* `project`
* `branch`
* `buildNumber`
* `baseContainerName`

### `activateEmailFailureNotifications()`

Activate email failure notifications for exceptions inside the *PipelineBuilder* `stage` method.

### `activateSlackFailureNotifications()`

Activate Slack failure notifications for exceptions inside the *PipelineBuilder* `stage` method.

### `createBuilders(Closure pipeline)`

Return a map of builders to be passed to a Jenkins `parallel` step. The argument is a parameterised *Closure*, where the parameter is the container interface (see section below), defined with curly braces and the parameter name before an arrow:

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

### `copyFrom(String src, String dst)`

Copy `src` in the container to `dst` in the build node. If `src` is a relative path, it gets prefixed with `/home/jenkins/`.
