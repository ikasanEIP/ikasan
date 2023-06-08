![Problem Domain](../../docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Enterprise Scheduler Job Plan Builder Demo

This archetype creates a project that provides 2 sample features:

- **Job Plan Builder** – A sample of how to use the job plan builder classes to create a simple job plan.
- **Job Plan Deployment** – A client that deploys the job plan to a running instance of the Ikasan Enterprise Scheduler.

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate     
    -DarchetypeGroupId=org.ikasan    
    -DarchetypeArtifactId=ikasan-job-plan-builder-demo-maven-plugin 
    -DarchetypeVersion=<Ikasan Version>    
    -DgroupId=<Maven Group Id>     
    -DartifactId=<Module Name>     
    -Dversion=<Module Version>     
```


### Maven Archetype Coordinates

- **archetypeGroupId** - Is always **org.ikasan** for Ikasan based archetypes
- **archetypeArtifactId** - Details the archetype type to invoke **ikasan-job-plan-builder-demo-maven-plugin**
- **archetypeVersion** - Details the version of the Ikasan archetype type to invoke

### Parameters Provided for the Integration Module POM Being Created

- **groupId** - groupId for this new Job Plan Builder Demo
- **artifactId** - artifactId for this new Job Plan Builder Demo
- **version** - version of this new Job Plan Builder Demo


#### Example Usage

```
mvn archetype:generate     
    \-DarchetypeGroupId=org.ikasan     
    \-DarchetypeArtifactId=ikasan-job-plan-builder-demo-maven-plugin 
    \-DarchetypeVersion=3.3.0-scheduler-SNAPSHOT  
    \-DgroupId=com.sample     
    \-DartifactId=my-job-plan     
    \-Dversion=1.0.0-SNAPSHOT     
```

(Accept defaults or update as required)

This will create a standard integration module project structure. 

### Build
To build and create a deployable integration module image you need to go into the directory and run a maven clean package.

```
cd my-job-plan
mvn clean package 
```

This will build and create a zip binary (specifically ```distribution/target/my-job-plan-distribution-${project.version}-dist.zip```) containing all the required artefacts used to run the demo.

### Deploy
Copy the created zip image from to the required destination, unzip and navigate into the unzipped directory.
```xslt
 unzip my-job-plan-distribution-${project.version}-dist.zip
 cd my-job-plan-distribution-${project.version}
```

### Understanding the Content
The unzipped distribution contains the following,
```unix
config
run.sh
lib
```
- ```config``` directory contains the runtime application.properties
- ```run.sh``` simple shell script wrapping the usage of the demo
- ```lib``` directory contains all binaries required to run the Integration Module


The following additional directories are created on first execution of the demo.
- ```logs``` directory contains the runtime output logs (std.out, std.err redirected) for the demo.
 

### Running the Demo§   
The job plan builder demo performs 3 different functions:
- Build the sample job plan bundle.
- Build and deploy the sample job plan bundle to a running Ikasan Enterprise Scheduler instance.
- Deploy a job plan bundle to a running Ikasan Enterprise Scheduler instance.

#### Build the Sample Job Plan Bundle
This will build a zip file called MyFirstJobPlan.zip and write it to the configured ```zip.output.dir=./``` found in application.properties.

UNIX
```
./run.sh build-bundle
```
Windows
```
TBD
```

#### Build the Sample Job Plan Bundle and Deploy it to a Running Ikasan Enterprise Scheduler Instance
This will build a zip file called MyFirstJobPlan.zip and write it to the configured ```zip.output.dir=./``` found in application.properties. It
will then deploy it to the Ikasan Enterprise Scheduler Instance running on the URL found in application.properties associated with```ikasan.dashboard.extract.base.url```.

UNIX
```
./run.sh build-bundle-and-deploy
```
Windows
```
TBD
```

#### Deploy a job plan bundel to a Running Ikasan Enterprise Scheduler Instance
This feature allows any job plan bundle to be deployed to the Ikasan Enterprise Scheduler Instance running on the URL 
found in application.properties associated with```ikasan.dashboard.extract.base.url```.

UNIX
```
./run.sh deploy-bundle <path to bundle zip file> 
```
Windows
```
TBD
```

### Common Problems When Generating From Archetypes

Occasionally you might see similar issue when generating an artifact
``` 
 mvn archetype:generate     -DarchetypeGroupId=org.ikasan     -DarchetypeArtifactId=ikasan-standalone-filesystem-im-maven-plugin -DarchetypeVersion=3.0.0
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building Maven Stub Project (No POM) 1
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] >>> maven-archetype-plugin:2.4:generate (default-cli) > generate-sources @ standalone-pom >>>
[INFO]
[INFO] <<< maven-archetype-plugin:2.4:generate (default-cli) < generate-sources @ standalone-pom <<<
[INFO]
[INFO] --- maven-archetype-plugin:2.4:generate (default-cli) @ standalone-pom ---
[INFO] Generating project in Interactive mode
[WARNING] Archetype not found in any catalog. Falling back to central repository (http://repo.maven.apache.org/maven2).
[WARNING] Use -DarchetypeRepository=<your repository> if archetype's repository is elsewhere.
Downloading: http://repo.maven.apache.org/maven2/org/ikasan/ikasan-standalone-filesystem-im-maven-plugin/3.0.0/maven-metadata.xml
Downloading: http://repo.maven.apache.org/maven2/org/ikasan/ikasan-standalone-filesystem-im-maven-plugin/3.0.0/ikasan-standalone-filesystem-im-maven-plugin-2.2.0.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.089 s
[INFO] Finished at: 2017-10-30T11:09:39+00:00
[INFO] Final Memory: 14M/309M
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-archetype-plugin:2.4:generate (default-cli) on project standalone-pom: The desired archetype does not exist (org.ikasan:ikasan-standalone-filesystem-im-maven-plugin:2.1.0) -> [Help 1]
```

This can be resolved by making sure you referring oss mvn repo in you mvn setting.xml file. [Check instructions](#update-mvn-settings.xml) 
