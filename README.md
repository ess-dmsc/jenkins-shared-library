# Jenkins Shared Library

Common functionality for ECDC Jenkins pipeline jobs.


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
6. Set *Project Repository* to `https://git.esss.dk/dm_group/jenkins-shared-library.git`
7. Click **Save** to save the changes.


## Using the library

See the commented sample Jenkinsfile provided in this repository at *examples/build/Jenkinsfile* and the *MANUAL.md* file.


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


### Does this library use proper Groovy coding conventions or style?

Possibly not. If you know how to make it more proper Groovy-like and want to do it, make the changes and send a pull request.
