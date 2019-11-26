![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Dashboard


### Pre-Requisites
- JRE 8

### Ikasan Dashboard Download
The Ikasan Dashboard distribution can be downloaded from OSS Nexus.
```
curl -fSL https://oss.sonatype.org/content/repositories/releases/org/ikasan/ikasan-dashboard/${project.version}/ikasan-dashboard-${project.version}.jar -o ikasan-dashboard-${project.version}.jar
```
### Ikasan Dashboard Unpacking
Unzip the downloaded image and navigate into the unzipped directory.
```xslt
 unzip ikasan-dashboard-distrbution-${project.version}-dist.zip
 cd ikasan-dashboard-distrbution-${project.version}
```

### Unzipped Content
The unzipped distribution contains the following,
```unix
LICENSE.text
README.txt
config
ikasan.sh
lib
logs
solr
```
- LICENSE.txt and README.txt are self explanatory
- ```lib``` directory contains all binaries required to run the Ikasan Dashboard
- ```solr``` directory contains all binaries required to run the solr search platform underpinning the dashboard
- ```logs``` directory contains the runtime output logs (std.out, std.err redirected) for the Ikasan Dashboard and H2 database
- ```config``` directory contains the runtime application.properties
- ```ikasan.sh``` is the shell script for managing the stopping and starting of the Ikasan Dashboard and required processes


### Running the Ikasan Dashboard
The Ikasan Dashboard runs as three JVM processes,

- Solr process - scalable search platform built on Lucene (https://lucene.apache.org/)
- H2 database process - file based Java SQL database
- Ikasan Dashboard process - Ikasan Dashboard for management and runtime visualisation

The Ikasan Dashboard requires Solr and H2 to be running before the dashboard can be started.
The ```ikasan.sh``` script manages the starting and stopping of these processes.

#### Starting All Processes
The following will start Solr, H2, and the Ikasan Dashboard in the correct order. If any are already running then no additional instances are started. 
```
./ikasan.sh start
```

#### Stopping All Processes
The following will stop the Ikasan Dashboard, H2, and Solr in the correct order.
```
./ikasan.sh stop
```

#### Starting Solr Only
The following will only start Solr. If an instance is already running then no additional instances are started.
```
./ikasan.sh start-solr
```

#### Starting H2 Only
The following will only start H2. If an instance is already running then no additional instances are started.
```
./ikasan.sh start-h2
```

#### Checking Processes
The following will check to see which processes are running.
```
./ikasan.sh ps
```

 