
# Ikasan Getting Started Guide

# Introduction

## Overview

The Ikasan Enterprise Integration Platform (IkasanEIP) provides a robust and scalable ESB platform based on open technologies and standards. IkasanEIP can be adopted as little or as much as required to meet your integration needs.

## About

This document will guide you through the process of creating an Ikasan development environment and runtime platform from scratch.

This will include the build tools and configuration; container install and configuration; and the creation and deployment of a simple Ikasan Integration Module.

This is part of the documentation suite for the Ikasan Enterprise Integration Platform.

## Audience

This guide is targeted at developers wishing to get started and undertake their first development projects with the Ikasan Enterprise Integration Platform.

Although not mandatory to getting started familiarity with Java, XML, Spring, and general Application Server concepts around data sources and deployment packaging constructs i.e. jar, ear, war will be advantageous.

## How to Use This Guide

This guide provides a quick and concise series of steps for getting started with the IkasanEIP platform. These steps should be followed sequentially. Points to note and other hints/tips are provided as additional information or best practices, however, full details on these aspects are beyond the scope of this document.

On completion of this document the reader should have installed and configured all required software for development and have an operational runtime environment containing a functional example Integration Module.

**NOTE: This does not denote a production quality runtime environment.  **

# Pre-Requisites

[Developer Pre-Requisiites](ikasaneip/developer/docs/DeveloperPreRequisites.md)


# Runtime Environment

The specifics of each Application Server installation and configuration for IkasanEIP are detailed [below](#jboss-eap-application-server) .

## Standalone JVM

Running Ikasan Integration Module (IM) on single JVM does not require any any prior software. IM will run as a web application and would ship with embedded Tomcat web server. Moreover to simplify the setup process all samples are shipped with H2 in memory database. There are two options on how to proceed with standalone JVM approach:
 * check out one of the ready samples which can be downloaded from public MVN repo and started
 * Generate new IM from artefact provided

### Use existing standalone samples

| Samples overview |
|-------------|
|  [spring-boot-builder-pattern](../../sample/spring-boot/builder-pattern/README.md) |
|  [spring-boot-file](../../sample/spring-boot/file/README.md) |
|  [spring-boot-jms](../../sample/spring-boot/jms/README.md) |
|  [spring-boot-ftp](../../sample/spring-boot/ftp/README.md) |
|  [spring-boot-ftp-jms](../../sample/spring-boot/ftp-jms/README.md) |
|  [spring-boot-sftp](../../sample/spring-boot/sftp/README.md) |
|  [spring-boot-sftp-jms](../../sample/spring-boot/sftp-jms/README.md) |

### Creating an Integration Module from Sample Artefact



## JBoss EAP Application Server

The JBoss Enterprise Application Platform (EAP) is one of the more popular Application Servers.

For further information about JBoss see [http://www.jboss.org](http://www.jboss.org/products/eap/download/)

### Versions

IkasanEIP version 1.0.0+ has currently been ratified against the following versions.

- JBoss EAP 6.2.x.GA
- JBoss EAP 6.3.0.GA
- JBoss EAP 6.4.x GA

It is recommended you use the latest version from the above list.

### Installation

Download the image from [http://www.jboss.org/products/eap/download/](http://www.jboss.org/products/eap/download/) as is appropriate for your OS and installer type.

Install by following the JBoss installation instructions. Downloading of the zip image is the simplest and therefore, recommended approach.

Ensure the JBOSS_HOME environment variable is set.

#### Setting Environment Variables From Unix Command Line

```
export JBOSS_HOME==${root\_install\_dir}/${runtime\_dir}/<jboss install dir name>
export PATH=$JBOSS_HOME/bin;$PATH
```

#### Setting Environment Variables from Windows Command Line

```
set JBOSS_HOME==%root\_install\_dir%\%runtime\_dir%\<jboss install dir name>
set PATH=%JBOSS_HOME%\bin;%PATH% 
```

Some installers will create an initial administration user for jboss, if that&#39;s the case then you can skip the next steps. If not then you will need to manually create an initial administration user as follows.

#### Creating User from Unix Command Line

```
cd $JBOSS_HOME/bin./add-user.sh 
```

#### Creating User from Windows Command Line

```
cd %JBOSS_HOME%\bin.\add-user
```

You will be prompted for the following – select (a) and RETURN

```
What type of user do you wish to add?  a) Management User (mgmt-users.properties)  b) Application User (application-users.properties)(a):
 
Enter the name of the user and <RETURN>
Enter the details of the new user to add.Using realm <ManagementRealm> as discovered from the existing property files.Username :
Enter the password for this user and <RETURN>, re-enter to confirm and <RETURN>

Password requirements are listed below. To modify these restrictions edit the add-user.properties configuration file. 
- The password must not be one of the following restricted values {root, admin, administrator} 
- The password must contain at least 8 characters, 1 alphabetic character(s), 1 digit(s), 1 non-alphanumeric symbol(s) 
- The password must be different from the usernamePassword : **\*\*\*\*\*\*\***
Re-enter Password : **\*\*\*\*\*\*\***

Accept values with <RETURN>

What groups do you want this user to belong to? (Please enter a comma separated list, or leave blank for none)[ ]:

Accept with 'yes' and <RETURN>

About to add user <USERNAME> for realm 'ManagementRealm' Is this correct yes/no? **yes**

Accept with 'yes' and <RETURN>

Added user <USERNAME> to file ‘${root\_install\_dir}/${runtime\_dir}/jboss-eap-6.3/standalone/configuration/mgmt-users.properties’ 
Added user <USERNAME> to file ‘${root\_install\_dir}/${runtime\_dir}/jboss-eap-6.3/domain/configuration/mgmt-users.properties’
Added user <USERNAME>with groups  to file ‘${root\_install\_dir}/${runtime\_dir}/jboss-eap-6.3/standalone/configuration/mgmt-groups.properties’
Added user <USERNAME> with groups  to file ‘${root\_install\_dir}/${runtime\_dir}/jboss-eap-6.3/domain/configuration/mgmt-groups.properties’
Is this new user going to be used for one AS process to connect to another AS process? e.g. for a slave host controller connecting to the master or for a Remoting connection for server to server EJB calls.yes/no? **yes**
```

### Sanity Checks

You can test your installation by starting the server as follows,

```
cd $JBOSS_HOME/bin
./domain.sh
```

Ensure no errors are reported in the console logs.

NOTE: We tend to use JBoss&#39;s domain configuration as opposed to the standalone configuration for ease of management and availability of managing multiple server instances from a single console. However, IkasanEIP is not tied to this and will work equally within both.

## Ikasan Runtime Platform Configuration

### Versions

The latest IkasanEIP official release versions (and SNAPSHOT versions) are available from Maven Central and the required software will be downloaded automatically as you execute each step below. All steps assume access to the Internet.

### Installation

All Ikasan runtime configurations are available as Maven archetypes to ease the configuration steps required for particular flavours of Application Server providers.

#### JBoss Application Server Platform Configuration

The Ikasan runtime configuration is deployed to JBoss as a JBoss module. For more information about JBoss modules see JBoss&#39;s Application Server documentation. A Maven archetype to create this default configuration is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate
    -DarchetypeGroupId=org.ikasan
    -DarchetypeArtifactId=ikasan-rt-conf-jboss6-maven-plugin
    -DarchetypeVersion=<Ikasan Version>
```

Where,

-  **Ikasan Version** - the version of the Ikasan platform being used. i.e. 1.0.0


Example Usage,

```
mvn archetype:generate \
-DarchetypeGroupId=org.ikasan \
-DarchetypeArtifactId=ikasan-rt-conf-jboss6-maven-plugin \
-DarchetypeVersion=1.0.0
```

(Accept defaults)

On execution this archetype will create all the required directories and files under a parent directory called ikasan-rt-conf. Navigate into this directory and run maven package assembly to create a binary distribution image for the Ikasan runtime configuration.

```
mvn clean package assembly:assembly
```

This command will create a binary distribution zip within the ikasan-rt-conf/target directory.

Navigate into this directory and unzip the image.

```
cd target
unzip <ikasan-rf-conf-distribution>.zip
```
We need to deploy the following to the JBoss Application Server runtime environment.

##### JBoss Modules

Deploy the Ikasan runtime configuration JBoss module

```
cd ikasan-rt-conf-jboss6/jboss-eap/modules
cp –R org $JBOSS_HOME/modules
```

##### JBoss Cli Scipts

Configure IkasanEIP&#39;s datasources (for Ikasan&#39;s database persistence) within JBoss.

These steps require the JBoss server to be running, so if not already started, start JBoss as follows,

###### From UNIX Command Line

```
cd $JBOSS\_HOME/bin
./domain.sh
```

###### From Windows Command Line

```
cd %JBOSS_HOME%\bin
.\domain.bat
```

The script you choose to execute to create the data sources will depend on the flavour of the database provider you are using.

The supported databases with IkasanEIP are

- Sybase 12.x
- Sybase 15.x
- MS SQLServer 2008, R2
- MS SQLServer 2012
- MySQL 5.x
- H2

If you do not have a specific database setup then simply use the in-memory H2 database which is shipped with JBoss.

Copy your DB flavoured script to the JBoss bin directory as follows,

```
cd ikasan-rt-conf-jboss6/jboss-eap/bin
cp \<DB flavour>\* $JBOSS_HOME/bin
```

Select the domain datasource for your chosen database and register the data sources with JBoss as follows,

###### From UNIX Command Line

```
cd $JBOSS\_HOME/bin
./jboss-cli.sh --file=./domain-<DB flavour>-ikasan-ds.cli
```

###### From Windows Command Line

```
cd %JBOSS\_HOME%/bin
./jboss-cli --file=./domain-<DB flavour>-ikasan-ds.cli
```


##### Stop Gap Sanity Check

Check the JBoss Administration Console to ensure the two new data sources have been successfully created.

Open a browser to [http://localhost:9990/console](http://localhost:9990/console)

Login to the Console using the administration account created when you setup the JBoss Application Server user.

Select the Configuration tab from the Console.

As shown in the image below, select the &quot;_Datasources_&quot; to see the local h2-ikasan-ds datasource.



Select &quot;_XA Datasources_&quot; to see the XA h2-ikasan-xads datasource.

 !

#### Creating IkasanEIP Databases

The creation of the database for the IkasanEIP persistence tables is done automatically on the first deployment of the Ikasan Dashboard application.

#### IkasanEIP Dashboard Deployment

Download the latest official version of the Ikasan Dashboard and deploy this to the Application Server via the Administration Console.

As no database tables will currently exist you can use the administration features to create these for you.

Enter the following URL in a browser,

[http://localhost:8080/ikasan-dashboard/admin/setup/providers.htm](http://localhost:8080/ikasan-dashboard/admin/setup/providers.htm)

Select your desired database type and continue.

**NOTE: The default shipped database within JBoss and Ikasan is an H2 in-memory database, H2. As H2 is run as an in-memory database all database configurations are only good for the duration of the JBoss server running. Stopping and starting the server will drop and re-create the database.**

You should now be able to login to this console using the default credentials.

## Ikasan Integration Module Development Sandbox

All IkasanEIP Integration Modules have a very similar structure and configuration content. Due to this it is possible to use Maven archetypes to create Integration Modules from standard template to get you developing quickly.

A common approach to managing Integration Modules and their software dependencies such as log4j, junit, ikasan libraries, etc is to create a parent pom upon which all integration modules can inherit their required library versions. A Maven archetype exists for the creation of this parent build pom.

### Creating a Build Parent Artefact

Create a standard build parent project containing all dependencies and versions of dependencies for any Integration Module.

A Maven archetype to create this default configuration is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate
    -DarchetypeGroupId=org.ikasan
    -DarchetypeArtifactId=ikasan-build-parent-maven-plugin
    -DarchetypeVersion=<Ikasan Version>

    -DgroupId=<Maven Group Id>
    -DartifactId=<Module Name>
    -Dversion=<Module Version>
```

where the standard Maven archetype coordinates are,

- **archetypeGroupId** – is always org.ikasan for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke

where the following parameters provide the configuration for the Integration Module pom being created,

- **groupId** – groupId for this new Integration Module
- **artifactId** – artifactId for this new Integration Module
- **version** – version of this new Integration Module

Example Usage,

```
mvn archetype:generate \    -DarchetypeGroupId=org.ikasan \
    -DarchetypeArtifactId=ikasan-build-parent-maven-plugin \
    -DarchetypeVersion=1.0.0 \
    -DgroupId=org.ikasan \
    -DartifactId=esb-build \
    -Dversion=1.0.0-SNAPSHOT
```

(Accept defaults or update as required)

This will create a standard parent build project structure. To build and install this parent pom to your maven repository you need to go into the directory and run a maven clean, install.

```
cd esb-build
mvn clean install 
```


This will create the parent build pom and install it to your local maven repository for reference by subsequent integration module build dependencies.

### Creating an Integration Module Artefact

The basic constituents of an Ikasan Integration Module (IM) are the same. Due to this we have created some out of the box IM archetypes based on common technical integration problems.

#### Local File System to JMS / JMS to Local File System Archetype

This archetype creates a working integration module containing two flows,

- **File System to JMS** – local file system files are consumed and published as JMS (HornetQ) events
- **JMS to File System** – JMS (HornetQ) events are consumed and published to a local file system

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate
    -DarchetypeGroupId=org.ikasan
    -DarchetypeArtifactId=ikasan-im-jboss6-filesystem-maven-plugin
    -DarchetypeVersion=<Ikasan Version>

    -DbuildParentGroupId=<Maven Parent Group Id>
    -DbuildParentArtifactId=<Maven Parent Artifact Id>
    -DbuildParentVersion=<Maven Parent Version>

    -DgroupId=<Maven Group Id>
    -DartifactId=<Module Name>
    -Dversion=<Maven Group Id>
    -DflowName=<Default flow names>
    -DjbossModuleDir=<Default JBoss Module directory>
```


where the standard Maven archetype coordinates are,

- **archetypeGroupId** – is always org.ikasan for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke

where the following parameters provide the configuration for the parent pom for your new Integration Module,

- **buildParentGroupId** – groupId of the Maven parent pom for this Integration Module
- **buildParentArtifactId** – artifactId of the Maven parent pom for this Integration Module
- **buildParentVersion** – version of the Maven parent pom for this Integration Module

where the following parameters provide the configuration for the Integration Module pom being created,

- **groupId** – groupId for this new Integration Module
- **artifactId** – artifactId for this new Integration Module
- **version** – version of this new Integration Module
- **flowName** – flow name assigned to the examples flows within this Integration Module
- **jbossModuleDir** – runtime JBoss module directory for this Integration Module


Example Usage,

```
mvn archetype:generate \    -DarchetypeGroupId=org.ikasan \
    -DarchetypeArtifactId=ikasan-im-jboss6-filesystem-maven-plugin \
    -DarchetypeVersion=1.0.5-SNAPSHOT \
    -DbuildParentGroupId=org.ikasan \
    -DbuildParentArtifactId=esb-build \
    -DbuildParentVersion=1.0.0-SNAPSHOT \
    -DgroupId=org.ikasan \
    -DartifactId=myIntegrationModule \
    -Dversion=1.0.0-SNAPSHOT \
    -DsourceFlowName=sourceFlow \
    -DtargetFlowName=targetFlow \
    -DjbossModuleDir=org/ikasan/myIntegrationModule/1-0-0-SNAPSHOT/conf/main
```

(Accept defaults or update as required)

This will create a standard integration module project structure. To build and create a deployable integration module image you need to go into the directory and run a maven clean, package assembly.

```
cd myIntegrationModule
mvn clean package assembly:assembly 
```


This will build and create a zip binary containing all the required deployments for your integration module.

##### Deploying the Integration Module

Once the archetype has been generated and built there are two aspects that require deployment to runtime.

###### JBoss Module Deploy

```

cd target
unzip myIntegrationModule-<version>-dist.zip
cp –R myIntegrationModule-<version>/modules/com $JBOSS\_HOME/modules  
```


###### Ikasan Integration Module (EAR) Deploy

Use the JBoss Administration Console to deploy the EAR to the Application Server from,

```
ear/target/myIntegrationModule-<version>-ear.ear 
```

#### JMS to JMS (HornetQ) Archetype

This archetype creates a working integration module containing one flow,

- **JMS to JMS** – events are consumed from one JMS (HornetQ) destination and published to another JMS (HornetQ) destination

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate
    -DarchetypeGroupId=org.ikasan
    -DarchetypeArtifactId=ikasan-im-jboss6-jms-maven-plugin
    -DarchetypeVersion=<Ikasan Version>

    -DbuildParentGroupId=<Maven Parent Group Id>
    -DbuildParentArtifactId=<Maven Parent Artifact Id>
    -DbuildParentVersion=<Maven Parent Version>

    -DgroupId=<Maven Group Id>
    -DartifactId=<Module Name>
    -Dversion=<Maven Group Id>
    -DflowName=<Default flow names>
    -DjbossModuleDir=<Default JBoss Module directory>
```

where the standard Maven archetype coordinates are,

- **archetypeGroupId** – is always org.ikasan for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke

where the following parameters provide the configuration for the parent pom for your new Integration Module,

- **buildParentGroupId** – groupId of the Maven parent pom for this Integration Module
- **buildParentArtifactId** – artifactId of the Maven parent pom for this Integration Module
- **buildParentVersion** – version of the Maven parent pom for this Integration Module

where the following parameters provide the configuration for the Integration Module pom being created,

- **groupId** – groupId for this new Integration Module
- **artifactId** – artifactId for this new Integration Module
- **version** – version of this new Integration Module
- **flowName** – flow name assigned to the examples flows within this Integration Module
- **jbossModuleDir** – runtime JBoss module directory for this Integration Module


Example Usage,

```
mvn archetype:generate \    -DarchetypeGroupId=org.ikasan \
    -DarchetypeArtifactId=ikasan-im-jboss6-jms-maven-plugin \
    -DarchetypeVersion=1.0.5-SNAPSHOT \
    -DbuildParentGroupId=org.ikasan \
    -DbuildParentArtifactId=esb-build \
    -DbuildParentVersion=1.0.0-SNAPSHOT \
    -DgroupId=org.ikasan \
    -DartifactId=myIntegrationModule \
    -Dversion=1.0.0-SNAPSHOT \
    -DflowName=sourceFlow \
    -DjbossModuleDir=org/ikasan/myIntegrationModule/1-0-0-SNAPSHOT/conf/main
```

(Accept defaults or update as required)

This will create a standard integration module project structure. To build and create a deployable integration module image you need to go into the directory and run a maven clean, package assembly.

```
cd myIntegrationModule
mvn clean package assembly:assembly 
```

This will build and create a zip binary containing all the required deployments for your integration module.

##### Deploying the Integration Module

Once the archetype has been generated and built there are two aspects that require deployment to runtime.

###### JBoss Module Deploy

```

cd target
unzip myIntegrationModule-<version>-dist.zip
cp –R myIntegrationModule-<version>/modules/com $JBOSS\_HOME/modules 
```

###### Ikasan Integration Module (EAR) Deploy

Use the JBoss Administration Console to deploy the EAR to the Application Server from,

```
ear/target/myIntegrationModule-<version>-ear.ear 
```

#### JMS to JMS (ActiveMQ) Archetype

This archetype creates a working integration module containing one flow,

- **JMS to JMS** – events are consumed from one JMS (ActiveMQ) destination and published to another JMS (ActiveMQ) destination

This Integration Module archetype has a requirement on an ActiveMQ resource adapter. This resource adapter is created and deployed as a JBoss module and similar to the other integration modules can be created from an archetype.

##### 4.4.0.1.1ActiveMQ Resource Adapter JBoss Module

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```

mvn archetype:generate
    -DarchetypeGroupId=org.ikasan
    -DarchetypeArtifactId=jboss6-module-activemq-maven-plugin
    -DarchetypeVersion=<Ikasan Version>

    -DbuildParentGroupId=<Maven Parent Group Id>
    -DbuildParentArtifactId=<Maven Parent Artifact Id>
    -DbuildParentVersion=<Maven Parent Version>

    -DgroupId=<Maven Group Id>
    -DartifactId=<Module Name>
    -Dversion=<Maven Group Id>
    -DjbossModuleDir=<Default JBoss Module directory>
```

where the standard Maven archetype coordinates are,

- **archetypeGroupId** – is always org.ikasan for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke

where the following parameters provide the configuration for the parent pom for your new Integration Module,

- **buildParentGroupId** – groupId of the Maven parent pom for this JBoss Module
- **buildParentArtifactId** – artifactId of the Maven parent pom for this JBoss Module
- **buildParentVersion** – version of the Maven parent pom for this JBoss Module

where the following parameters provide the configuration for the JBoss Module pom being created,

- **groupId** – groupId for this new JBoss Module
- **artifactId** – artifactId for this new JBoss Module
- **version** – version of this new JBoss Module


Example Usage,

```
mvn archetype:generate \    -DarchetypeGroupId=org.ikasan \
    -DarchetypeArtifactId=jboss6-module-activemq-maven-plugin \
    -DarchetypeVersion=1.0.5-SNAPSHOT \
    -DbuildParentGroupId=org.ikasan \
    -DbuildParentArtifactId=esb-build \
    -DbuildParentVersion=1.0.0-SNAPSHOT \
    -DgroupId=org.ikasan \
    -DartifactId=activemq-jboss-module \
    -Dversion=1.0.0-SNAPSHOT
```

(Accept defaults or update as required)

This will create a standard JBoss module project structure specifically for ActiveMQ. To build and create a deployable JBoss module image you need to go into the directory and run a maven clean, package assembly.

Before undertaking the build consider if you need to override the ActiveMQ version rather than inheriting from your parent build. If you wish to change the version edit the pom.xml and add/update the following properties. For instance, if you wished to use ActiveMQ 5.12.1.

```
<properties>  
 <activemq.url>tcp://localhost:61616</activemq.url>   
<version.activemq>5.12.1</version.activemq>
</properties> 
```

Then to build the JBoss module run the following.

```
cd activemq-jboss-module
mvn clean package assembly:assembly 
```

This will build and create a zip binary containing all the required deployments for your JBoss module.

###### ActiveMQ Resource Adapter JBoss Module Deploy

```
cd target
unzip jboss-activemq-module-<version>-dist.zip
cp –R jboss-activemq-module-<version>/modules/com $JBOSS\_HOME/modules 
```

##### ActiveMQ Integration Module

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate
    -DarchetypeGroupId=org.ikasan
    -DarchetypeArtifactId=ikasan-im-jboss6-activemq-maven-plugin
    -DarchetypeVersion=<Ikasan Version>
    -DbuildParentGroupId=<Maven Parent Group Id>
    -DbuildParentArtifactId=<Maven Parent Artifact Id>
    -DbuildParentVersion=<Maven Parent Version>

    -DgroupId=<Maven Group Id>
    -DartifactId=<Module Name>
    -Dversion=<Maven Group Id>
    -DflowName=<Default flow names>
    -DjbossModuleDir=<Default JBoss Module directory>
```

where the standard Maven archetype coordinates are,

- **archetypeGroupId** – is always org.ikasan for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke

where the following parameters provide the configuration for the parent pom for your new Integration Module,

- **buildParentGroupId** – groupId of the Maven parent pom for this Integration Module
- **buildParentArtifactId** – artifactId of the Maven parent pom for this Integration Module
- **buildParentVersion** – version of the Maven parent pom for this Integration Module

where the following parameters provide the configuration for the Integration Module pom being created,

- **groupId** – groupId for this new Integration Module
- **artifactId** – artifactId for this new Integration Module
- **version** – version of this new Integration Module
- **flowName** – flow name assigned to the examples flows within this Integration Module
- **jbossModuleDir** – runtime JBoss module directory for this Integration Module

Example Usage,

```
mvn archetype:generate \    -DarchetypeGroupId=org.ikasan \
    -DarchetypeArtifactId=ikasan-im-jboss6-activemq-maven-plugin \
    -DarchetypeVersion=1.0.5-SNAPSHOT \
    -DbuildParentGroupId=org.ikasan \
    -DbuildParentArtifactId=esb-build \
    -DbuildParentVersion=1.0.0-SNAPSHOT \
    -DgroupId=org.ikasan \
    -DartifactId=myIntegrationModule \
    -Dversion=1.0.0-SNAPSHOT \
    -DflowName=sourceFlow \
    -DjbossModuleDir=org/ikasan/myIntegrationModule/1-0-0-SNAPSHOT/conf/main
```
(Accept defaults or update as required)

This will create a standard integration module project structure. To build and create a deployable integration module image you need to go into the directory and run a maven clean, package assembly.

```
cd myIntegrationModule
mvn clean package assembly:assembly |
```

This will build and create a zip binary containing all the required deployments for your integration module.

###### Integration Module JBoss Module Deploy

```
cd target
unzip myIntegrationModule-<version>-dist.zip
cp –R myIntegrationModule-<version>/modules/com $JBOSS\_HOME/modules 
```

###### Ikasan Integration Module (EAR) Deploy

Use the JBoss Administration Console to deploy the EAR to the Application Server from,

```
ear/target/myIntegrationModule-<version>-ear.ear 
```


#### DB to JMS / JMS to DB Archetype

This archetype creates a working integration module containing two flows,

- **DB to JMS** – events are consumed from a database and published to a JMS (HornetQ) destination

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate
    -DarchetypeGroupId=org.ikasan
    -DarchetypeArtifactId=ikasan-im-jboss6-db-maven-plugin
    -DarchetypeVersion=<Ikasan Version>

    -DbuildParentGroupId=<Maven Parent Group Id>
    -DbuildParentArtifactId=<Maven Parent Artifact Id>
    -DbuildParentVersion=<Maven Parent Version>

    -DgroupId=<Maven Group Id>
    -DartifactId=<Module Name>
    -Dversion=<Maven Group Id>
    -DflowName=<Default flow names>
    -DjbossModuleDir=<Default JBoss Module directory>
```

where the standard Maven archetype coordinates are,


- **archetypeGroupId** – is always org.ikasan for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke

where the following parameters provide the configuration for the parent pom for your new Integration Module,

- **buildParentGroupId** – groupId of the Maven parent pom for this Integration Module
- **buildParentArtifactId** – artifactId of the Maven parent pom for this Integration Module
- **buildParentVersion** – version of the Maven parent pom for this Integration Module

where the following parameters provide the configuration for the Integration Module pom being created,

- **groupId** – groupId for this new Integration Module
- **artifactId** – artifactId for this new Integration Module
- **version** – version of this new Integration Module
- **flowName** – flow name assigned to the examples flows within this Integration Module
- **jbossModuleDir** – runtime JBoss module directory for this Integration Module

Example Usage,

```
mvn archetype:generate \    -DarchetypeGroupId=org.ikasan \
    -DarchetypeArtifactId=ikasan-im-jboss6-db-maven-plugin \
    -DarchetypeVersion=1.0.5-SNAPSHOT \
    -DbuildParentGroupId=org.ikasan \
    -DbuildParentArtifactId=esb-build \
    -DbuildParentVersion=1.0.0-SNAPSHOT \
    -DgroupId=org.ikasan \
    -DartifactId=myIntegrationModule \
    -Dversion=1.0.0-SNAPSHOT \
    -DsourceFlowName=sourceFlow \
    -DtargetFlowName=targetFlow \
    -DjbossModuleDir=org/ikasan/myIntegrationModule/1-0-0-SNAPSHOT/conf/main
```

(Accept defaults or update as required)

This will create a standard integration module project structure. To build and create a deployable integration module image you need to go into the directory and run a maven clean, package assembly.

```
cd myIntegrationModule
mvn clean package assembly:assembly 
```

This will build and create a zip binary containing all the required deployments for your integration module.

##### Deploying the Integration Module

Once the archetype has been generated and built there are two aspects that require deployment to runtime.

###### JBoss Module Deploy

```
cd target
unzip myIntegrationModule-<version>-dist.zip
cp –R myIntegrationModule-<version>/modules/com $JBOSS\_HOME/modules 
```

###### Ikasan Integration Module (EAR) Deploy

Use the JBoss Administration Console to deploy the EAR to the Application Server from,

```
ear/target/myIntegrationModule-<version>-ear.ear 
```

#### FTP to JMS / JMS to SFTP Archetype

This archetype creates a working integration module containing two flows,

- **SFTP to JMS** – events are consumed from an SFTP server and published to a JMS (HornetQ) destination

A Maven archetype to create this is available from Maven Central and can be invoked as follows,

```
mvn archetype:generate
    -DarchetypeGroupId=org.ikasan
    -DarchetypeArtifactId=ikasan-im-jboss6-sftp-maven-plugin
    -DarchetypeVersion=<Ikasan Version>

    -DbuildParentGroupId=<Maven Parent Group Id>
    -DbuildParentArtifactId=<Maven Parent Artifact Id>
    -DbuildParentVersion=<Maven Parent Version>

    -DgroupId=<Maven Group Id>
    -DartifactId=<Module Name>
    -Dversion=<Maven Group Id>
    -DflowName=<Default flow names>
    -DjbossModuleDir=<Default JBoss Module directory>
```

where the standard Maven archetype coordinates are,

- **archetypeGroupId** – is always org.ikasan for Ikasan based archetypes
- **archetypeArtifactId** – details the archetype type to invoke
- **archetypeVersion** – details the version of the Ikasan archetype type to invoke

where the following parameters provide the configuration for the parent pom for your new Integration Module,

- **buildParentGroupId** – groupId of the Maven parent pom for this Integration Module
- **buildParentArtifactId** – artifactId of the Maven parent pom for this Integration Module
- **buildParentVersion** – version of the Maven parent pom for this Integration Module

where the following parameters provide the configuration for the Integration Module pom being created,

- **groupId** – groupId for this new Integration Module
- **artifactId** – artifactId for this new Integration Module
- **version** – version of this new Integration Module
- **flowName** – flow name assigned to the examples flows within this Integration Module
- **jbossModuleDir** – runtime JBoss module directory for this Integration Module


Example Usage,

```
mvn archetype:generate \    -DarchetypeGroupId=org.ikasan \
    -DarchetypeArtifactId=ikasan-im-jboss6-sftp-maven-plugin \
    -DarchetypeVersion=1.0.5-SNAPSHOT \
    -DbuildParentGroupId=org.ikasan \
    -DbuildParentArtifactId=esb-build \
    -DbuildParentVersion=1.0.0-SNAPSHOT \
    -DgroupId=org.ikasan \
    -DartifactId=myIntegrationModule \
    -Dversion=1.0.0-SNAPSHOT \
    -DsourceFlowName=sourceFlow \
    -DtargetFlowName=targetFlow \
    -DjbossModuleDir=org/ikasan/myIntegrationModule/1-0-0-SNAPSHOT/conf/main
```

(Accept defaults or update as required)

This will create a standard integration module project structure. To build and create a deployable integration module image you need to go into the directory and run a maven clean, package assembly.

```
cd myIntegrationModule mvn clean package assembly:assembly 
```

This will build and create a zip binary containing all the required deployments for your integration module.

##### Deploying the Integration Module

Once the archetype has been generated and built there are two aspects that require deployment to runtime.

###### JBoss Module Deploy

```
cd target
unzip myIntegrationModule-<version>-dist.zip
cp –R myIntegrationModule-<version>/modules/com $JBOSS\_HOME/modules 
```

###### Ikasan Integration Module (EAR) Deploy

Use the JBoss Administration Console to deploy the EAR to the Application Server from,

```
ear/target/myIntegrationModule-<version>-ear.ear 
```

# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | March 2018 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
