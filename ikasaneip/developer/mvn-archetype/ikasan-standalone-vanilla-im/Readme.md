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

### Standard Maven archetype Coordinates

|Coordinate    | Description |
|--------------| ------------|
|archetypeGroupId| Is always **org.ikasan** for Ikasan based archetypes|
|archetypeArtifactId|Details the archetype type to invoke **ikasan-standalone-vanilla-im-maven-plugin**|
|archetypeVersion| Details the version of the Ikasan archetype type to invoke **2.0.0 and above**|

### Parameters Provided for the Integration Module POM Being Created

|Coordinate    | Description |
|--------------| ------------|
|groupId| groupId for this new Integration Module|
|artifactId| artifactId for this new Integration Module|
|version| version of this new Integration Module|

#### Example Usage

```
mvn archetype:generate     
    \-DarchetypeGroupId=org.ikasan     
    \-DarchetypeArtifactId=ikasan-standalone-vanilla-im-maven-plugin 
    \-DarchetypeVersion=2.0.4    
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

# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
