![Problem Domain](../../docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Command-Line Interface (CLI) Shell

The Ikasan CLI Shell is a cross-platform client interface for IkasanESB.
It can be used start, check, and stop Ikasan JVM processes for H2 and Integration Modules, either interactively or non-iteractively as part of an 
automated script in a sophisticated ALM / build system.

## Deploying Ikasan CLI Shell
The Ikasan CLI Shell can be deployed by simply copying the following jar to your runtime.
```
ikasan-shell-<version>.jar
```
The Ikasan CLI Shell is shipped with all generated Integration Module Maven archetypes along with simple execution scripts - ```ikasan.sh``` for UNIX; and ```ikasan.bat``` for Windows.

## Running the Ikasan CLI Shell
### Pre-Requisites
Ikasan CLI Shell requires Java 11+.

### Runtime Properties
The following properties are used by the Ikasan CLI Shell and should be set as properties within the Integration Module. 

```
# Ikasan Shell logging
logging.level.org.ikasan.cli.shell=ERROR

# Ikasan Shell process commands
h2.java.command=java -Dmodule.name=${module.name} -classpath ./lib/h2-1.4.200.jar org.h2.tools.Server -ifNotExists -tcp -tcpAllowOthers -tcpPort ${h2.db.port}
module.java.command=java -server -Xms256m -Xmx256m -XX:MaxMetaspaceSize=128m -Dcom.arjuna.ats.arjuna.objectstore.objectStoreDir=./persistence/${module.name}-ObjectStore -Dorg.apache.activemq.SERIALIZABLE_PACKAGES=* -Dmodule.name=${module.name} -jar ./lib/${module.name}-1.0.0-SNAPSHOT.jar
shell.history.file=logs/ikasan-shell.log
```

- ```logging.level.org.ikasan.cli.shell``` - Java logging level for the Ikasan CLI Shell
-  ```h2.java.command``` - Java command line required to start the Integration Module;s H2 JVM process
- ```module.java.command``` - Java command line required to start the Integration Module JVM process
- ```shell.history.file``` - location of where the shell command line history log is persisted. This contains a list of all commands issued within the shell.

### Non-Interactive Shell
The Ikasan CLI Shell can be run non-interactively as follows.

For UNIX, ```./ikasan.sh <command ...>```

For Windows, ```.\ikasan.bat <command ...>```

where non-interactive commands can be one or more of
- ```start``` - start the Integration Module's h2 process and then the Integration Module
- ```stop``` - stop the Integration Module followed by stopping the Integration Module's H2 process
- ```start-h2``` - start the Integration Module's H2 process
- ```stop-h2``` - stop the Integration Module's H2 process
- ```start-module``` - start the Integration Module
- ```stop-module``` - stop the Integration Module
- ```ps``` - check whether the Integration Module or associated H2 processes are running


### Interactive Shell
The Ikasan CLI Shell can be run interactively from the command line as follows,

For UNIX, ```./ikasan.sh```

For Windows, ```.\ikasan.bat```

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
        !: Execute any command line command. Syntax: ! ls -la
        env: Show environment details. Syntax: env
        ps: Check running process. Syntax: ps <process name> | -name <process name>, ps <process name> | -name <process name>
        start-h2: Start H2 persistence JVM
        start-module: Start Integration Module JVM
        stop-h2: Stop H2 persistence JVM
        stop-module: Stop Integration Module JVM


Ikasan Shell:> 
```

