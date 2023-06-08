![Problem Domain](../../docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Enterprise Scheduler Distribution Deployment Demo

This archetype creates a very simple maven project that assists in the deployment of the Ikasan Enterprise Scheduler
dashboard along with an Ikasan Enterprise Scheduler Agent.


A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate     
    -DarchetypeGroupId=org.ikasan    
    -DarchetypeArtifactId=ikasan-scheduler-distribution-deployment-demo-maven-plugin 
    -DarchetypeVersion=<Ikasan Version>    
    -DgroupId=<Maven Group Id>     
    -DartifactId=<Module Name>     
    -Dversion=<Module Version>     
```


### Maven Archetype Coordinates

- **archetypeGroupId** - Is always **org.ikasan** for Ikasan based archetypes
- **archetypeArtifactId** - Details the archetype type to invoke **ikasan-scheduler-distribution-deployment-demo-maven-plugin**
- **archetypeVersion** - Details the version of the Ikasan archetype type to invoke

### Parameters Provided for the Integration Module POM Being Created

- **groupId** - groupId for this new Job Plan Builder Demo
- **artifactId** - artifactId for this new Job Plan Builder Demo
- **version** - version of this new Job Plan Builder Demo


#### Example Usage

```
mvn archetype:generate     
    \-DarchetypeGroupId=org.ikasan     
    \-DarchetypeArtifactId=ikasan-scheduler-distribution-deployment-demo-maven-plugin
    \-DarchetypeVersion=3.3.0-scheduler-SNAPSHOT  
    \-DgroupId=com.sample     
    \-DartifactId=my-first-ikasan-scheduler-deployment     
    \-Dversion=1.0.0-SNAPSHOT     
```

(Accept defaults or update as required)

This will create a standard integration module project structure. 

### Build
In ```pom.xml``` set the following to your preferred deployment location:
- ```<scheduler-dashboard-distribution-deploy-directory>```
- ```<scheduler-agent-distribution-deploy-directory>```
To build and deploy the distributions.

```
cd my-first-ikasan-scheduler-deployment
mvn clean install 
```

This will download and deploy the Ikasan Enterprise Scheduler Dashboard and an agent to the locations defined in the ```pom.xml```.

- ```<scheduler-dashboard-distribution-deploy-directory>deployemnt location</scheduler-dashboard-distribution-deploy-directory>```
- ```<scheduler-agent-distribution-deploy-directory>deployment location</scheduler-agent-distribution-deploy-directory>```

### Understanding the Content
The unzipped Ikasan Enterprise Scheduler Dashboard distribution contains the following:
```unix
config
ikasan.sh
lib
solr
```
- ```config``` directory contains the runtime application.properties
- ```ikasan.sh``` simple shell script wrapping the usage of the Ikasan Enterprise Scheduler Dashboard
- ```lib``` directory contains all binaries required to run the Ikasan Enterprise Scheduler Dashboard


The following additional directories are created on first execution of the demo.
- ```logs``` directory contains the runtime output logs (std.out, std.err redirected) for the demo.
- ```persistence``` the location of the h2 database file used by the Ikasan Enterprise Scheduler Dashboard.

The unzipped Ikasan Enterprise Scheduler Agent distribution contains the following:
```unix
config
lib
config-service-env.bat
config-service-env.sh
ikasan-config-service.bat
ikasan-config-service.sh
ikasan-scheduler-agent.service
ikasan-simple.bat
ikasan-simple.sh
simple-env.batsimple-env.sh
```
- ```config``` directory contains the runtime application.properties
- ```lib``` directory contains all binaries required to run the Ikasan Enterprise Scheduler Dashboard


The following additional directories are created on first execution of the demo.
- ```logs``` directory contains the runtime output logs (std.out, std.err redirected) for the demo.
- ```persistence``` the location of the h2 database file used by the Ikasan Enterprise Scheduler Dashboard.
- ```bigqueue``` directory contains the runtime output logs (std.out, std.err redirected) for the demo.
- ```process-logs``` the location of the h2 database file used by the Ikasan Enterprise Scheduler Dashboard.
- ```pid``` the location of the h2 database file used by the Ikasan Enterprise Scheduler Dashboard.


### Running the Demo

#### Starting the Ikasan Enterprise Scheduler Dashboard

UNIX
```
cd <scheduler-dashboard-distribution-deploy-directory>
./ikasan.sh start
```
Windows
```
TBD
```

#### Starting the Ikasan Enterprise Scheduler Agent
This will build a zip file called MyFirstJobPlan.zip and write it to the configured ```zip.output.dir=./``` found in application.properties. It
will then deploy it to the Ikasan Enterprise Scheduler Instance running on the URL found in application.properties associated with```ikasan.dashboard.extract.base.url```.

UNIX
```
cd <scheduler-dashboard-agent-deploy-directory>
./ikasan-simple.sh start
```
Windows
```
cd <scheduler-dashboard-agent-deploy-directory>
./ikasan-simple.bat start
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
