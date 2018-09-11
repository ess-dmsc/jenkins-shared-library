# Jenkins Shared Library

Common functionality for ECDC Jenkins pipeline jobs.


## Making the library available in Jenkins

To make this library globally available in Jenkins, go to **Manage Jenkins**, **Configure System**, **Global Pipeline Libraries** and follow the instructions below:

1. Click the **Add** button.
2. Fill the *Name* field with the chosen name for the library. This name will be used to access the library in pipeline scripts (recommendation: `ecdc-pipeline`).
3. Set the *Default version* to a valid Git identifier (e.g. `master`, a commit hash or tag).
4. Under **Retrieval method**, select **Modern SCM**.
5. Under **Source Code Management**, select **Git**.
6. Set *Project Repository* to `https://git.esss.dk/dm_group/jenkins-shared-library`
7. Click **Save** to save the changes.
