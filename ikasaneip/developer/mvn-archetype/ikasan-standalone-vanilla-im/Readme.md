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

|Coordinate    | Description |
|--------------| ------------|
|archetypeGroupId| Is always **org.ikasan** for Ikasan based archetypes|
|archetypeArtifactId|Details the archetype type to invoke **ikasan-standalone-vanilla-im-maven-plugin**|
|archetypeVersion| Details the version of the Ikasan archetype type to invoke **2.0.0 and above**|

### Parameters Provided for the Integration Module POM Being Created

|Parameter    | Description |
|--------------| ------------|
|groupId| groupId for this new Integration Module|
|artifactId| artifactId for this new Integration Module|
|version| version of this new Integration Module|

#### Example Usage

```
mvn archetype:generate     
    \-DarchetypeGroupId=org.ikasan     
    \-DarchetypeArtifactId=ikasan-standalone-vanilla-im-maven-plugin 
    \-DarchetypeVersion=2.1.0    
    \-DgroupId=com.sample     
    \-DartifactId=vanilla-im     
    \-Dversion=1.0.0-SNAPSHOT     
```

(Accept defaults or update as required)

This will create a standard empty Ikasan application as a starting point. 
Even without an Integration Module and associated flows we can still build and create a deployable image by going into the directory and run a maven clean package.

```
cd vanilla-im
mvn clean package 
```

This will build and create a zip binary containing all the required deployments.

It is recommended this archetype be used when getting familiar with Ikasan as part of the "Hands On Developer Walk Through" section.

### Common Problems When Generating From Archetypes

Occasionally you might see similar issue when generating an artifact
``` 
 mvn archetype:generate     -DarchetypeGroupId=org.ikasan     -DarchetypeArtifactId=ikasan-standalone-filesystem-im-maven-plugin -DarchetypeVersion=2.0.4
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
Downloading: http://repo.maven.apache.org/maven2/org/ikasan/ikasan-standalone-filesystem-im-maven-plugin/2.1.0/maven-metadata.xml
Downloading: http://repo.maven.apache.org/maven2/org/ikasan/ikasan-standalone-filesystem-im-maven-plugin/2.1.0/ikasan-standalone-filesystem-im-maven-plugin-2.1.0.jar
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
