![Problem Domain](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Command-Line Interface (CLI) Shell

The Ikasan CLI Shell is a cross-platform client interface for IkasanESB.
It can be used to start, check, and stop Ikasan JVM processes for H2 and Integration Modules, either interactively or non-iteractively as part of an 
automated script in a sophisticated ALM / build system.

## Deploying Ikasan CLI Shell
The Ikasan CLI Shell can be deployed by simply copying the following jar to your runtime.
```
ikasan-shell-<version>.jar
```
The Ikasan CLI Shell is shipped with all generated Integration Module Maven archetypes along with support for both UNIX and Windows.

## Ikasan CLI Shells
The following UNIX and Windows scripts are provided as simple property based scripts and Spring Config Service based scripts.

### Ikasan Simple Scripts
```ikasan-simple.sh``` - UNIX script driven by standard application.properties of the Integration Module

```ikasan-simple.bat``` - Windows script driven by standard application.properties of the Integration Module

### Ikasan Spring Config Service Scripts
```ikasan-config-service.sh``` - UNIX script driven by Spring Config Service properties typically backed by the Git repository of the Integration Module

```ikasan-config-service.bat``` - Windows script driven by Spring Config Service properties typically backed by the Git repository of the Integration Module

## Running the Ikasan CLI Shell
### Pre-Requisites
Ikasan CLI Shell requires Java 11+.

### Runtime Properties
The following properties are used by the Ikasan CLI Shell and should be set as properties within the Integration Module. 

```
# Ikasan Shell process commands
h2.java.command=java -Dmodule.name=${module.name} -classpath ${lib.dir}/h2-*.jar org.h2.tools.Server -ifNotExists -tcp -tcpAllowOthers -tcpPort ${h2.db.port}
module.java.command=java -server -Xms256m -Xmx256m -XX:MaxMetaspaceSize=128m -Dspring.jta.logDir=${persistence.dir}/${module.name}-ObjectStore -Dorg.apache.activemq.SERIALIZABLE_PACKAGES=* -Dmodule.name=${module.name} -jar ${lib.dir}/${module.name}-*.jar
```

- ```h2.java.command``` - Java command line required to start the Integration Module's H2 JVM process
- ```module.java.command``` - Java command line required to start the Integration Module JVM process

Note: Both the classpath and the jar can be specified as wildcards to save changing the specific configured version on subsequent upgrades.

### Non-Interactive Shell
The Ikasan CLI Shell can be run non-interactively as follows.

For UNIX, 

```./ikasan-simple.sh <command ...>```

```./ikasan-config-service.sh <command ...>```

For Windows, 

```.\ikasan-simple.bat <command ...>```

```.\ikasan-config-service.bat <command ...>```

where non-interactive commands can be one or more of
- ```start``` - start the Integration Module's h2 process and then the Integration Module
- ```stop``` - stop the Integration Module followed by stopping the Integration Module's H2 process
- ```start-dashboard``` - start the Ikasan Dashboard's h2 process, Solr instance and then the Dashboard
- ```stop-dashboard``` - stop the Ikasan Dashboard followed by stopping the Solr instance and then Dashboard's H2 process
- ```start-h2``` - start the Integration Module's H2 process
- ```stop-h2``` - stop the Integration Module's H2 process
- ```migrate-h2``` - migrate the Integration Module's H2 database from one version to another
- ```start-module``` - start the Integration Module
- ```stop-module``` - stop the Integration Module
- ```start-solr``` - start the Solr instance associated with the dashboard
- ```stop-solr``` - stop the Solr instance associated with the dashboard
- ```env``` - show runtime environment variables
- ```ps``` - check whether the Integration Module, Solr or associated H2 processes are running


### Interactive Shell
The Ikasan CLI Shell can be run interactively from the command line as follows,

For UNIX, 
 
 ```./ikasan-simple.sh```
 or
 ```./ikasan-config-service.sh```
 
 For Windows, 
 
 ```.\ikasan-simple.bat```
 or
 ```.\ikasan-config-service.bat```

The CLI Shell will start and display the following,
```
 _____  __   __    ___      _____   ___    __    __
|_   _| | | / /   /   \    /  __/  /   \   | \  | |
  | |   | |/ /   / / \ \   \ \    / / \ \  |  \ | |
  | |   |   /   | |___| |   \ \  | |___| | |   \| |
 _| |_  | |\ \  |  ___  |  __\ \ |  ___  | | |\ ' |
|_____| |_| \_\ |_|   |_| /____/ |_|   |_| |_| \__|
===================================================
IkasanEIP  (v3.1.0)
Ikasan Shell:> 
```

Once started a full list of interactive commands can be seen by typing ```help```

```
Ikasan Shell:> help
AVAILABLE COMMANDS

Built-In Commands
       help: Display help about available commands
       stacktrace: Display the full stacktrace of the last error.
       clear: Clear the shell screen.
       quit, exit: Exit the shell.
       history: Display or save the history of previously run commands
       script: Read and execute commands from a file.

Ikasan Commands
       stop-module: Stop Integration Module JVM
       start-module: Start Integration Module JVM
       migrate-h2: Migrate H2 persistence
       ps: Check running process. Syntax: ps [process name] | [-name <process name>] [-user <user name>]
       stop-solr: Stop the Solr instance associated with the Ikasan Dashboard
       start-solr: Start the Solr instance associated with the Ikasan Dashboard
       stop-h2: Stop H2 persistence JVM
       env: Show runtime environment variables. Syntax: env [regexp variable name - to match specific variable names] [-names - to display variable name(s) only] [-no-expand - do not expand variable wildcards] [-list - returns results as a list]
       version: Get the Ikasan version of the module
       start-h2: Start H2 persistence JVM

Ikasan Shell:> 
```

#### Command Options

| Command      | Description                                                                                                                                                      | Options                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               | Examples                                                                                                                                                                                                                                                                                                                                                                                           |
|:-------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------| 
| env          | Displays the runtime environment variables currently picked up by the CLI shell. Specifying a regexp of a variable name will only show those variables matching. | -names boolean to display variable names only. <br/>-no-expand boolean to specify no wildcard expansion of variables. <br/> -list return the results as a list.                                                                                                                                                                                                                                                                                                                                                                                                                       | Example 1. <br/> ```env h2.java.process``` - will only show the h2.java.process variable. <br/><br/> Example 2. <br/> ```env h2 -names``` - will only show the variable names matching h2. <br/><br/>Example 3. <br/> ```env h2 -list``` - will show the variables matching h2 as a list. <br/><br/>Example 4. <br/> ```env h2 -no-expand``` - will show the variable without expanding wildcards. |
| ps           | Displays the status of the H2 JVM and Integration Module JVM as running, true or false. Uses default Integration Module name and username running the CLI.       | -name <Alternate Module Name>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
|              |                                                                                                                                                                  | -user <Alternate Username>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| start-h2     | Starts the H2 JVM process for this Integration Module. Uses default Integration Module name and user.                                                            | -name <Alternate Module Name>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
|              |                                                                                                                                                                  | -command <Alternate JVM Command> which overrides h2.java.command                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| stop-h2      | Stops the H2 JVM process for this Integration Module.                                                                                                            | -name <Alternate Module Name>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| migrate-h2   | Migrate H2 persistence                                                                                                                                           | --source-h2-version The version of the H2 database we are migrating from. [Optional, default = 1.4.200] </br> --target-h2-version The version of the H2 database we are migrating to. [Optional, default = 2.2.224] <br/> --h2-user The username of the H2 database to use for the migration. [Optional, default = sa] <br/> --h2-password The password of the H2 database to use for the migration. [Optional, default = sa] <br/> --h2-database-location The path to the database. The general Ikasan convention [<persistence-dir>/<module-name>-db/esb] will be used by default. [Optional] |
| start-module | Starts the Integration Module JVM process. Uses default Integration Module name and user.                                                                        | -name <Alternate Module Name>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| stop-module  | Stops the Integration Module JVM process.                                                                                                                        | -command <Alternate JVM Command> which overrides module.java.command                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| start-solr   | Start the Solr instance associated with the Ikasan Dashboard                                                                                                     |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| stop-solr    | Stop the Solr instance associated with the Ikasan Dashboard                                                                                                      | -name <Alternate Module Name>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| version      | Get the Ikasan version of the module.                                                                                                                            |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |

The configuration property `command.stop.process.wait.timeout.seconds` can be added to the application properties to configure a wait time for processes to stop. This has a default value of 300 seconds.

#### Sample Usage
Command

```ikasan-simple.sh env h2.java.module -list```

Output

```h2.java.command=java -Dmodule.name=vanilla-im -classpath ./lib/h2-1.4.200.jar: org.h2.tools.Server -ifNotExists -tcp -tcpAllowOthers -tcpPort 8082```


Assigning a variable sourced from Ikasan Shell
Command

```export myVar=`./ikasan-simple.sh env h2.java -values -list` ```
```echo $myVar ```

Output

```java -Dmodule.name=vanilla-im -classpath ./lib/h2-1.4.200.jar: org.h2.tools.Server -ifNotExists -tcp -tcpAllowOthers -tcpPort 8082```




