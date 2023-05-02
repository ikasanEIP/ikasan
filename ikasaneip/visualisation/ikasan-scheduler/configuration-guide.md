![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Configuration Guide

## Introduction
The Ikasan Enterprise Scheduler is built on the [Spring Boot Framework](https://spring.io/) and uses property injection, which allows you to easily configure your application by providing values to certain properties 
that the application needs. These properties can be things like server port number or database connection information. 
You can either define these values directly in a configuration file or you can pass them in through environment variables. 
Once the values are provided, the Ikasan Enterprise Scheduler will inject them into the appropriate places in your application, allowing it to 
run with the desired configuration. This makes it easy to customize your application and adapt it to different environments
without needing to change your code.

The Ikasan Enterprise Scheduler provide a number of mechanisms in order for properties to be made available to the application.
1. Variable passed via the jvm start up command.
2. Via properties file on the file system.
3. Using [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/).

### Properties Provided on JVM Startup
There are two ways to pass variables to a Spring Boot application via JVM startup:

Command Line Arguments:
You can pass variables to a Spring Boot application by setting them as arguments when launching the JVM. 
For example, if you have a variable named "appName" that you want to pass to the JVM, you can start the application 
with the following command:

`java -jar my-app.jar --appName=myAppName`

In your Spring Boot application, you can retrieve this value by using the @Value annotation in your application 
configuration or by accessing it directly through the Environment object.

System Properties:
You can also pass variables to a Spring Boot application by setting them as system properties when launching the JVM. 
For example, if you have a variable named "appName" that you want to pass to the JVM, you can start the application with the following command:

`java -DappName=myAppName -jar my-app.jar`

In your Spring Boot application, you can retrieve this value by using the @Value annotation in your application configuration 
or by accessing it directly through the System object.

Note: It is important to use unique key names for any variables that you pass to the JVM, to avoid any conflicts with existing 
system properties.

### Loading Properties From Property Files on the File System
When building applications with Spring Boot, it is often useful to 
store configuration properties in a properties file on the file system. This file contains key-value pairs that define various 
parameters for the application, such as database connection details or logging settings.

When the Spring Boot application starts up, it searches the file system for any files with a specific name like 
'application.properties'. It then looks for a file in a number of pre-configured locations. If it finds such a file, 
it reads the key-value pairs and makes the values available to the application.

Once the properties file is loaded, Spring Boot provides them to the application in a variety of ways, such as through 
the use of annotations or injection into objects. This makes it easy to configure and customize the behavior of your 
Spring Boot application without writing any additional code.

Spring Boot looks for property files in the following order:

1. The current directory
2. The classpath root
3. The /config subdirectory of the current directory
4. The /config subdirectory of the classpath root

Spring Boot searches for property files in the following formats:
1. application.properties
2. application.yml
3. .properties
4. .yml

Note:  represents the name of the Spring Boot module or application. For example, if you have a module named my-module, \
Spring Boot will look for my-module.properties or my-module.yml.

### Spring Cloud Config
Spring Cloud Config is a tool that allows developers to store application configuration settings in a centralized location and have 
those properties made available to your Spring Boot application on start up. This makes it easier to manage and update the settings for 
multiple applications, without having to manually update each individual instance. It also allows for version control of configuration 
settings, so developers can see how settings have changed over time. Overall, Spring Cloud Config helps streamline the process of managing 
application settings and ensures consistency across different instances of an application.

You can pass Spring Cloud Config details to a Spring Boot application by setting the following JVM system properties:

- spring.cloud.config.uri: Specifies the location of the Spring Cloud Config Server.
- spring.cloud.config.username: Specifies the username to authenticate with the Spring Cloud Config Server (optional).
- spring.cloud.config.password: Specifies the password to authenticate with the Spring Cloud Config Server (optional).

For example, you can pass these properties to the JVM by using the "-D" option like this:

`$ java -Dspring.cloud.config.uri=http://localhost:8888 -Dspring.cloud.config.username=user -Dspring.cloud.config.password=pass -jar my-app.jar`

Spring Cloud bootstrap properties can also be defined in a secure location of the file system containing sensitive data and referenced on the start up.

`$ java -Dspring.cloud.config.uri=http://localhost:8888 spring.cloud.bootstrap.location=~/bootstrap.properties -jar my-app.jar`

With the contents of the `bootstrap.properties` as follows:

```properties
spring.cloud.config.username=user
spring.cloud.config.password=pass
```

## Ikasan Scheduler Dashboard Configuration Parameters
Now that we have discussed how properties can be provided to the Ikasan Scheduler Dashboard, the following section will describe the 
various configuration parameters that can be passed to the application and how they affect the system.

### Core Job Orchestration Configuration Parameters 
| Configuration Name | Type                              | Default | Description | Sample |
|--------------------|-----------------------------------|---------|-------------|--------|
|context.lifecycle.active                    | boolean                           | true    |             |        |
|job.context.params.to.spel.calculators           | Map<String, String>               |         |             |        |
|scheduled.job.context.queue.directory| String                            |         |             |        |

### Notification Configuration Parameters
| Configuration Name | Type                | Default | Description | Sample |
|--------------------|---------------------|---------|-------------|--------|
|scheduler.notification.file.overdue.tolerance.minutes                    | Integer             | 0       |             |        |
|mail.link.url                   | String              |         |             |        |
|notifications.enabled                    | boolean             | true    |             |        |
|notifications.polling.interval.minutes                    | int                 | 1       |             |        |

### Scheduler Job Provision Configuration Parameters
| Configuration Name | Type    | Default | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           | Sample |
|--------------------|---------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------|
|scheduler.provision.jobs.on.upload                   | boolean | true    | Ikasan provides a feature that allows for job plan bundles to be uploaded. The upload of a job plan bundle performs 2 major functions. Firstly the Ikasan Scheduler writes all of the job plan artifacts to the underlying document datastore. Once this is complete, the Ikasan Scheduler then publishes to job artifacts to the Scheduler Agents associated with the jon plan. This flag allows for the Scheduler Agent job provisioning phase of the job plan upload to be skipped. In the case that the job provisioning phase is skipped, users will be expected to manually provision the jobs. |        |

### Scheduler Agent Client Rest Configuration Parameters
| Configuration Name | Type | Default | Description | Sample |
|--------------------|------|---------|-------------|--------|
|file.job.submission.wait.time.seconds                   | int  | 3       |             |        |

