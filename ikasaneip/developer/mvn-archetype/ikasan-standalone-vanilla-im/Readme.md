![Problem Domain](../../docs/quickstart-images/Ikasan-title-transparent.png)
# Vanilla Integration Module Archetype
This archetype creates an empty Ikasan shell as a starting point for your classes and configuration, free from specifics of any integration use cases. 
This is a good starting point when first learning Ikasan.

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate     
    -DarchetypeGroupId=org.ikasan    
    -DarchetypeArtifactId=ikasan-standalone-vanilla-im-maven-plugin 
    -DarchetypeVersion=<Ikasan Version>    
    -DgroupId=<Maven Group Id>     
    -DartifactId=<Module Name>     
    -Dversion=<Module Version>     
    -DsourceFlowName=<Source Flow Name>     

```

### Maven Archetype Coordinates

- **archetypeGroupId** – is always **org.ikasan** for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke **ikasan-standalone-vanilla-im-maven-plugin**
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke  **2.1.0 and above**

### Parameters Provided for the Integration Module POM Being Created

- **groupId** - groupId for this new Integration Module
- **artifactId** - artifactId for this new Integration Module
- **version** - version of this new Integration Module
- **sourceFlowName** - source flow name within this Integration Module

#### Example Usage

```
mvn archetype:generate     
    \-DarchetypeGroupId=org.ikasan     
    \-DarchetypeArtifactId=ikasan-standalone-vanilla-im-maven-plugin 
    \-DarchetypeVersion=3.1.0    
    \-DgroupId=com.sample     
    \-DartifactId=vanilla-im     
    \-Dversion=1.0.0-SNAPSHOT     
```

(Accept defaults or update as required)

It is recommended this archetype be used when getting familiar with Ikasan as part of the "Hands On Developer Walk Through" section.

### Build
To build and create a deployable integration module image you need to go into the directory and run a maven clean package.

```
cd vanilla-im
mvn clean package 
```

This will build and create a zip binary (specifically ```distribution/target/vanilla-im-distribution-${project.version}-dist.zip```) containing all the required artefacts for the deployment of your integration module.

### Deploy
Copy the created zip image from to the required destination, unzip and navigate into the unzipped directory.
```xslt
 unzip vanilla-im-distribution-${project.version}-dist.zip
 cd vanilla-im-distribution-${project.version}
```

### Understanding the Content
The unzipped distribution contains the following,
```unix
config
ikasan.sh
ikasan.bat
env.sh
env.bat
lib
```
- ```config``` directory contains the runtime application.properties
- ```ikasan.sh``` is the shell script for managing the stopping and starting of the Integration Module and h2 processes
- ```env.sh``` user environment customisations to be picked up by the ikasan.sh script
- ```ikasan.bat``` Windows equivalent script
- ```env.bat``` Windows equivalent env script
- ```lib``` directory contains all binaries required to run the Integration Module

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
#### Relocating Persistence via application.properties
To change the persistence directory location via the application.properties you need to update the following properties which refer to the relative location of './persistence'.
```java
com.arjuna.ats.arjuna.objectstore.objectStoreDir=./persistence/${artifactId}-ObjectStore

datasource.url=jdbc:h2:tcp://localhost:${h2.db.port}/./persistence/${module.name}-db/esb;IFEXISTS=FALSE
```
 

### Running the Integration Module
The Integration Module runs as two JVM processes,

- h2 database process - file based Java SQL database
- Integration Module process - the Integration Module itself
 
The Integration Module requires h2 to be running before the module process can be started.
The ```ikasan.sh``` (UNIX) or ```ikasan.bat``` (Windows) script manages the starting and stopping of these processes.

#### Starting All Processes
The following will start h2 and the Integration Module in the correct order. If any are already running then no additional instances are started. 
```
./ikasan.sh start
```

#### Stopping All Processes
The following will stop the Integration Module and h2 in the correct order.
```
./ikasan.sh stop
```

#### Starting h2 Only
The following will only start h2. If an instance is already running then no additional instances are started.
```
./ikasan.sh start-h2
```

#### Starting Integration Module Only
The following will only start the Integration Module. If an instance is already running then no additional instances are started.
```
./ikasan.sh start-module
```

#### Stopping Integration Module Only
The following will stop the Integration Module process only.
```
./ikasan.sh stop-module
```

#### Stopping H2 Only
The following will stop the h2 process only.
```
./ikasan.sh stop-h2
```

#### Checking Processes
The following will check to see which processes are running.
```
./ikasan.sh ps
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
