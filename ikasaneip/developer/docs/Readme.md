[../](../../../Readme.md)
![Problem Domain](quickstart-images/Ikasan-title-transparent.png)
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

[Developer Pre-Requisiites](./DeveloperPreRequisites.md)

## Standalone JVM

Running Ikasan Integration Module (IM) on single JVM does not require any any prior software. IM will run as a web application and would ship with embedded Tomcat web server. Moreover to simplify the setup process all samples are shipped with H2 in memory database. There are two options on how to proceed with standalone JVM approach:
 * check out one of the ready samples which can be downloaded from public MVN repo and started
 * Generate new IM from artefact provided

### Using existing standalone samples

| Samples overview |
|-------------|
|  [spring-boot-builder-pattern](../../sample/spring-boot/builder-pattern/README.md) |
|  [spring-boot-file](../../sample/spring-boot/file/README.md) |
|  [spring-boot-jms](../../sample/spring-boot/jms/README.md) |
|  [spring-boot-ftp](../../sample/spring-boot/ftp/README.md) |
|  [spring-boot-ftp-jms](../../sample/spring-boot/ftp-jms/README.md) |
|  [spring-boot-sftp](../../sample/spring-boot/sftp/README.md) |
|  [spring-boot-sftp-jms](../../sample/spring-boot/sftp-jms/README.md) |

### Using the Ikasan maven archetypes
Details of all the Ikasan maven archetypes that can be used to generate Ikasan integration module projects can be found [here](../mvn-archetype/Readme.md).
