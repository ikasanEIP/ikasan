![Problem Domain](../../docs/quickstart-images/Ikasan-title-transparent.png)
# Local File System to JMS / JMS to Local File System Integration Module Archetype

This archetype creates a working integration module containing two flows,

- **File System to JMS** – local file system files are consumed and published as JMS (ActiveMQ) events
- **JMS to File System** – JMS (ActiveMQ) events are consumed and published to a local file system

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate     
    -DarchetypeGroupId=org.ikasan    
    -DarchetypeArtifactId=ikasan-standalone-filesystem-im-maven-plugin 
    -DarchetypeVersion=<Ikasan Version>    
    -DgroupId=<Maven Group Id>     
    -DartifactId=<Module Name>     
    -Dversion=<Module Version>     
    -DsourceFlowName=<Source Flow Name>     
    -DtargetFlowName=<Target Flow Name>
```

### Maven Archetype Coordinates

- **archetypeGroupId** – is always **org.ikasan** for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke **ikasan-standalone-filesystem-im-maven-plugin**
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke

### Parameters Provided for the Integration Module POM Being Created

- **groupId** – groupId for this new Integration Module
- **artifactId** – artifactId for this new Integration Module
- **version** – version of this new Integration Module
- **sourceFlowName** – source flow name within this Integration Module
- **targetFlowName** – target flow name within this Integration Module


#### Example Usage

```
mvn archetype:generate     
    \-DarchetypeGroupId=org.ikasan     
    \-DarchetypeArtifactId=ikasan-standalone-filesystem-im-maven-plugin 
    \-DarchetypeVersion=3.2.3-SNAPSHOT    
    \-DgroupId=com.ikasan     
    \-DartifactId=myIntegrationModule     
    \-Dversion=1.0.0-SNAPSHOT     
    \-DsourceFlowName=fileSystemToJMSFlow     
    \-DtargetFlowName=jmsToFileSystemFlow
```

(Accept defaults or update as required)

This will create a standard integration module project structure. To build and create a deployable integration module image you need to go into the directory and run a maven clean package.

### Build
To build and create a deployable integration module image you need to go into the directory and run a maven clean package.

```
cd myIntegrationModule
mvn clean package 
```

This will build and create a zip binary (specifically ```distribution/target/myIntegrationModule-distribution-${project.version}-dist.zip```) containing all the required artefacts for the deployment of your integration module.

### Deploy
Copy the created zip image from to the required destination, unzip and navigate into the unzipped directory.
```xslt
 unzip myIntegrationModule-distribution-${project.version}-dist.zip
 cd myIntegrationModule-distribution-${project.version}
```

### Understanding the Content
The unzipped distribution contains the following,
```unix
config
ikasan-config-service.sh
config-service-env.sh
ikasan-config-service.bat
config-service-env.bat
ikasan-simple.sh
simple-env.sh
ikasan-simple.bat
simple-env.bat
lib
```
- ```config``` directory contains the runtime application.properties
- ```ikasan-config-service.sh``` is the Spring config service based shell script for managing the stopping and starting of the Integration Module and h2 processes
- ```config-service-env.sh``` user environment customisations to be picked up by the ikasan-config-service.sh script
- ```ikasan-config-service.bat``` Windows equivalent script
- ```config-service-env.bat``` Windows equivalent env script
- ```ikasan-simple.sh``` is the application.properties based shell script for managing the stopping and starting of the Integration Module and h2 processes
- ```simple-env.sh``` user environment customisations to be picked up by the ikasan-simple.sh script
- ```ikasan-simple.bat``` Windows equivalent script
- ```simple-env.bat``` Windows equivalent env script
- ```lib``` directory contains all binaries required to run the Integration Module

It is recommended you create a Windows shortcut or UNIX symlink to your selected script (either ikasan-simple or ikasan-config-service) called ```ikasan.sh``` or ```ikasan.bat``` as appropriate,

For example in UNIX,

```
ln -s ikasan-simple.sh ikasan.sh
```

The following additional directories are created on first startup of the Integration Module.
- ```logs``` directory contains the runtime output logs (std.out, std.err redirected) for the Integration Module and h2 database
- ```persistence``` directory for the persistence of the transaction logs and h2 database of the Integration Module
- ```pid``` directory for the persistence of the running processes for the h2 database and Integration Module

The contents of the  ```pid``` directory are transient and really just for convenience of ikasan identifying running processes. These can be deleted without any consequence to the runtime platform.

The contents of the  ```persistence``` directory are critical to the runtime operation and crash/recovery ability of the Integration Module. 
It is advised that this be located on a separate mount in production runtime, either through a symlink or by changing the location in the application.properties.

#### Relocating Persistence via Symlink
To relocate the persistence directory via a symlink firstly ensure the processes are not running then move the persistence directory as follows,
```java
mv persistence /someother/location
ln -s /someother/location persistence
```

#### Relocating Standard Directories via application.properties
Although recommended as default values, both the persistence and lib directory can be changed by updating the following application.properties,
 
 ```java
 # standard dirs
 persistence.dir=./persistence
 lib.dir=./lib
 ``` 

### Running the Integration Module
The Integration Module runs as two JVM processes,

- h2 database process - file based Java SQL database
- Integration Module process - the Integration Module itself
 
The Integration Module requires h2 to be running before the module process can be started.
The ```ikasan.sh``` (UNIX) or ```ikasan.bat``` (Windows) script manages the starting and stopping of these processes.

#### Starting All Processes
The following will start h2 and the Integration Module in the correct order. If any are already running then no additional instances are started. 

UNIX
```
./ikasan.sh start
```
Windows
```
./ikasan.bat start
```

#### Stopping All Processes
The following will stop the Integration Module and h2 in the correct order.

UNIX
```
./ikasan.sh stop
```
Windows
```
./ikasan.bat stop
```

#### Starting h2 Only
The following will only start h2. If an instance is already running then no additional instances are started.

UNIX
```
./ikasan.sh start-h2
```
Windows
```
./ikasan.bat start-h2
```

#### Starting Integration Module Only
The following will only start the Integration Module. If an instance is already running then no additional instances are started.

UNIX
```
./ikasan.sh start-module
```
Windows
```
./ikasan.bat start-module
```

#### Stopping Integration Module Only
The following will stop the Integration Module process only.

UNIX
```
./ikasan.sh stop-module
```
Windows
```
./ikasan.bat stop-module
```

#### Stopping H2 Only
The following will stop the h2 process only.

UNIX
```
./ikasan.sh stop-h2
```
Windows
```
./ikasan.bat stop-h2
```

#### Checking Processes
The following will check to see which processes are running.

UNIX
```
./ikasan.sh ps
```
Windows
```
./ikasan.bat ps
```

### Common Problems When Generating From Archetypes

Occasionally you might see similar issue when generating an artifact
``` 
 mvn archetype:generate     -DarchetypeGroupId=org.ikasan     -DarchetypeArtifactId=ikasan-standalone-filesystem-im-maven-plugin -DarchetypeVersion=3.2.3-SNAPSHOT
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
Downloading: http://repo.maven.apache.org/maven2/org/ikasan/ikasan-standalone-filesystem-im-maven-plugin/3.0.0/ikasan-standalone-filesystem-im-maven-plugin-2.2.0-SNAPSHOT.jar
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
