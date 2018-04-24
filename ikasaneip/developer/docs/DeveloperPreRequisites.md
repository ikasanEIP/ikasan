
# Ikasan Developer Pre-Requisites

# Introduction

## Overview

The Ikasan Enterprise Integration Platform (IkasanEIP aka IkasanESB) provides a robust and scalable ESB platform based on open technologies and standards. 
IkasanEIP can be adopted as little or as much as required to meet your integration needs.

## About

This guide details the technology pre-requisites to start developing with IkasanESB.

This is part of the documentation suite for the Ikasan Enterprise Integration Platform.

## Audience

This guide is targeted at developers wishing to get started and undertake any development with the IkasanESB.

A familiarity with Java and Maven is required.

## How to Use This Guide

This guide provides a quick and concise series of steps for establishing the pre-requisites for IkasanESB.
 
On completion of this document the reader should have installed and configured all required software for development.
 
# Development Environment

You can choose to install and configure the tooling on your machine as is your preference – the following is recommended simply as a guide providing a split between the development tools; your development sandbox area; and your development runtime environment.
```
${root_install_dir}/${devtools_install_dir}
                   /${runtime_dir}
                   /${sandbox_dir}
```

where,

${devtools_install_dir} - contains your development tools installs i.e. Java, Maven

${runtime_dir}  - is your runtime environment for deployment

${sandbox_dir}  - is your area for creating and building Integration Modules

For convenience we will refer to the base directories above throughout this guide.

# Development Tools

This section assumes you have nothing currently in place. If you do have any of the tools installed/configured then please check the setup and versions to ensure they are compatible.

## Java Development Kit/Runtime Environment

IkasanEIP is built in Java (JDK) and runs on the Java Runtime Environment (JRE).

IkasanEIP will support any language which compiles to bytecode and runs within the Java Virtual Machine (JVM).

For more details on Java see [http://www.oracle.com./technetwork/java](http://www.oracle.com./technetwork/java)

### Version

IkasanEIP version 2.x.y+ requires Java 1.8.x.

### Installation

Download the JDK appropriate for your Operating System.

All Java JDK downloads are available from [http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Install by following the JDK installation instructions.

We will refer to the java install directory as ${devtools\_install\_dir}/java although depending on your OS and installer the location may differ.  For instance, on Windows this may be under "_Program Files\Java_".

The important point is that we have the JAVA_HOME and PATH environment variables set to reflect this java install.

#### Setting Unix Environment Variables
```
export JAVA_HOME=${root_install_dir}/${platform_install_dir}/<java install dir name>;
export PATH=$JAVA_HOME/bin;$PATH
```

#### Setting Windows Environment Variables
```
set JAVA_HOME=%root_install_dir%\%platform_install_dir%\<java install dir name>
set PATH=%JAVA_HOME%\bin:%PATH% 
```

### Sanity Checks

You can test your installation by starting a new command line session and typing,
```
  java –version 
```

This should reflect the Java JDK version you have just installed.

For instance – the line in bold depicts the important information.

```
java version "1.8.0_31"
Java(TM) SE Runtime Environment (build 1.8.0_31-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.31-b07, mixed mode)
```

## Maven

IkasanEIP uses Maven for its build and build time dependency management.

For more details on Maven see [http://maven.apache.org](http://maven.apache.org/)

### Version

IkasanEIP version 2.x.y+ requires at least Maven 3.3.x, but has been validated up to Maven 3.5.3.

### Installation

Download the latest 3.5.3 Maven binary from [http://maven.apache.org/download.cgi](http://maven.apache.org/download.cgi)

Unzip the image under ${devtools\_install\_dir}.

The M2_HOME and PATH environment variables should be set to reflect this maven install.

#### Setting Environment Variables From UNIX Command Line

```
export M2_HOME=${root_install_dir}/${platform_install_dir}/<maven install dir name>
export PATH=$M2_HOME/bin;$PATH 
```

#### Setting Environment Variables From Windows Command Line

```
set M2_HOME=${root_install_dir}/${platform_install_dir}\<maven install dir name>
set PATH=%M2_HOME%\bin;%PATH% 
```

### Sanity Checks

You can test your installation by starting a new command line session and typing,

```
mvn --version 
```

This should reflect the Maven version you have just installed.

For instance – the bold output depicts the important information,

```
Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=128m; support was removed in 8.0
Apache Maven 3.5.3 (3383c37e1f9e9b3bc3df5050c29c8aff9f295297; 2018-02-24T19:49:05Z)
Maven home: /opt/platform/maven-platform/apache-maven-3.5.3
Java version: 1.8.0_141, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_141.jdk/Contents/Home/jre
Default locale: en_GB, platform encoding: UTF-8
OS name: "mac os x", version: "10.13.3", arch: "x86_64", family: "mac"
```

### update mvn settings.xml 
Your default Maven settings.xml can be found under your Maven .m2 directory, located under your user home dir by default.

```
cd ~<username>/.m2
```

If you using Snapshot versions, edit your settings.xml to ensure you refer to the ikasaneip-snapshots and ikasaneip-releases _oss_ repositories in your .m2/settings.xml.
```xml  
<profile>
  <id>securecentral</id>
  <repositories>
      <repository>
          <id>ikasaneip-snapshots</id>
          <url>http://oss.sonatype.org/content/repositories/snapshots/</url>
          <releases>
              <enabled>false</enabled>
          </releases>
          <snapshots>
              <enabled>true</enabled>
          </snapshots>
      </repository>
      <repository>
          <id>ikasaneip-releases</id>
          <url>http://oss.sonatype.org/content/repositories/releases/</url>
          <releases>
              <enabled>true</enabled>
          </releases>
          <snapshots>
              <enabled>false</enabled>
          </snapshots>
      </repository>
      <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases>
              <enabled>true</enabled>
          </releases>
      </repository>
  </repositories>
  <pluginRepositories>
      <pluginRepository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases>
              <enabled>true</enabled>
          </releases>
      </pluginRepository>
  </pluginRepositories>
</profile>
        
 <activeProfiles>
    <activeProfile>securecentral</activeProfile>
 </activeProfiles>
```
 

## Java IDE
Any Java IDE will suffice (even a text editor if you are feeling brave). 
We recommend IntelliJ Community Edition Java IDE which can be downloaded from [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/).
 
# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | March 2018 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
