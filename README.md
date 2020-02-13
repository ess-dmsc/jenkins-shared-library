# Jenkins Shared Library

Common functionality for ECDC Jenkins pipeline jobs, including parallel builds in containers and automated Conan package generation and upload.


## Requirements

This library assumes the following plugins are installed on the Jenkins server:

- [Docker Pipeline](https://wiki.jenkins.io/display/JENKINS/Docker+Pipeline+Plugin)
- [Slack Notification](http://wiki.jenkins-ci.org/display/JENKINS/Slack+Plugin)

The following environment variables are assumed to be defined on build nodes:

- http_proxy
- https_proxy
- local_conan_server

The `docker` label is used to select the build nodes where the pipeline will be run.


## Making the library available in Jenkins

To make this library globally available in Jenkins, go to **Manage Jenkins**, **Configure System**, **Global Pipeline Libraries** and follow the instructions below:

1. Click the **Add** button.
2. Fill the *Name* field with the chosen name for the library. This name will be used to access the library in pipeline scripts (recommendation: `ecdc-pipeline`).
3. Set the *Default version* to a valid Git version identifier (e.g. `master`, a commit hash or tag).
4. Under **Retrieval method**, select **Modern SCM**.
5. Under **Source Code Management**, select **Git**.
6. Set *Project Repository* to `https://git.esss.dk/dm_group/jenkins-shared-library.git` (this repository is mirrored on the DMSC GitLab).
7. Click **Save** to save the changes.


## Using the library

See the commented sample Jenkinsfiles provided in the *examples* folder and the documentation at https://ess-dmsc.github.io/jenkins-shared-library/.

### Making the library available in the pipeline script

Assuming you have added the shared library globally to Jenkins using the name `ecdc-pipeline`, add the following line to a Jenkinsfile to make the library available:

```
@Library('ecdc-pipeline')
```

### Importing classes from the library

To make library classes available in the pipeline, use `import`:

```
import ecdcpipeline.ContainerBuildNode
import ecdcpipeline.PipelineBuilder
```


## Upgrading the default build node container images

To upgrade the default build node container images, edit *src/ecdcpipeline/DefaultBuildNodeImages.groovy*.


## Developing the library

To develop the library and run tests locally, you need [Groovy](http://www.groovy-lang.org). You can find out what version of Groovy Jenkins is using by clicking on **Master** under *Build Executor Status* and then going to the **Script Console**; enter `println GroovySystem.version` and click **Run** to get the result.


### Running unit tests

You can run unit tests from the root directory of this repository with

```
groovy --classpath src:test test/ecdcpipeline/<file_name>.groovy
```

where `<file_name>` must be substituted with a test file name (_*Test.groovy_).

### Generating documentation pages

To create the documentation pages locally, run from the root of this repository

```
groovydoc -sourcepath src -d docs 'ecdcpipeline' '*.groovy'
```

### The test branch

To test changes to the library on Jenkins before merging to _master_, merge them to the _test_ branch. The Jenkinsfie for this repository runs tests based on the _test_ version of the library.


### Does this library use proper Groovy coding conventions or style?

Possibly not. If you know how to make it more proper Groovy-like and want to do it, make the changes and send a pull request.
