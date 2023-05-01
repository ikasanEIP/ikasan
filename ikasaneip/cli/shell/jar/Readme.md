![Problem Domain](../../docs/quickstart-images/Ikasan-title-transparent.png)
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
- ```start-h2``` - start the Integration Module's H2 process
- ```stop-h2``` - stop the Integration Module's H2 process
- ```start-module``` - start the Integration Module
- ```stop-module``` - stop the Integration Module
- ```env``` - show runtime environment variables
- ```ps``` - check whether the Integration Module or associated H2 processes are running


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
        clear: Clear the shell screen.
        exit, quit: Exit the shell.
        help: Display help about available commands.
        history: Display or save the history of previously run commands
        script: Read and execute commands from a file.
        stacktrace: Display the full stacktrace of the last error.

Ikasan Commands
        env: Show runtime environment variables. Syntax: env [regexp variable name - to match specific variable names] [-names - to display variable name(s) only] [-no-expand - do not expand variable wildcards] [-list - returns results as a list]
        ps: Check running process. Syntax: ps [process name] | [-name <process name>] [-user <user name>]
        start-h2: Start H2 persistence JVM
        start-module: Start Integration Module JVM
        stop-h2: Stop H2 persistence JVM
        stop-module: Stop Integration Module JVM


Ikasan Shell:> 
```

#### Command Options

| Command | Description | Options | Examples |
| :---    | :---    | :---   | :---   | 
| env     | Displays the runtime environment variables currently picked up by the CLI shell. Specifying a regexp of a variable name will only show those variables matching.| -names boolean to display variable names only. <br/>-no-expand boolean to specify no wildcard expansion of variables. <br/> -list return the results as a list. | Example 1. <br/> ```env h2.java.process``` - will only show the h2.java.process variable. <br/><br/> Example 2. <br/> ```env h2 -names``` - will only show the variable names matching h2. <br/><br/>Example 3. <br/> ```env h2 -list``` - will show the variables matching h2 as a list. <br/><br/>Example 4. <br/> ```env h2 -no-expand``` - will show the variable without expanding wildcards. |
| ps      | Displays the status of the H2 JVM and Integration Module JVM as running, true or false. Uses default Integration Module name and username running the CLI. | -name <Alternate Module Name> |
|         |  | -user <Alternate Username> |
| start-h2 | Starts the H2 JVM process for this Integration Module. Uses default Integration Module name and user. | -name <Alternate Module Name> |
|         |  | -command <Alternate JVM Command> which overrides h2.java.command |
| stop-h2 | Stops the H2 JVM process for this Integration Module. | -name <Alternate Module Name> |
| start-module | Starts the Integration Module JVM process. Uses default Integration Module name and user. | -name <Alternate Module Name> |
|         |  | -command <Alternate JVM Command> which overrides module.java.command |
| stop-module | Stops the Integration Module JVM process. | -name <Alternate Module Name> |




