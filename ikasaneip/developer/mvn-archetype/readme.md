Ikasan Project Archetypes
=========================
Ikasan provides the following maven archetype support for quick project structure creation of parent build poms and integration modules.
The Integration Module archetype(s) will actually give you a fully tested and functional IM with a sample flow.

ikasan-build-parent-maven-plugin
--------------------------------
Creates a standard build parent project containing all dependencies and versions of dependencies.

To create the project structure,

mvn archetype:generate -DarchetypeGroupId=org.ikasan -DarchetypeArtifactId=ikasan-build-parent-maven-plugin \
-DarchetypeVersion=<Ikasan Version>
-DgroupId=<Maven Group Id> \
-DartifactId=<Build Parent Name> \
-Dversion=<Build Parent Version>

Where,
- Ikasan Version       - the version of the Ikasan platform being used. i.e. 1.0.0-rc4-SNAPSHOT
- Maven Group Id       - maven group coordinates you want this build pom to have
- Build Parent Name    - name of the build pom project. If not specified this will default to esb-build
- Build Parent Version - version of the build pom you wish to assign
 
Example Usage,
mvn archetype:generate -DarchetypeGroupId=org.ikasan -DarchetypeArtifactId=ikasan-build-parent-maven-plugin -DarchetypeVersion=1.0.0-rc4-SNAPSHOT

ikasan-integration-module-jboss6-maven-plugin
---------------------------------------------
Creates a standard Integration Module project with a sample flow based on a simple Event Generating consumer.

mvn archetype:generate -DarchetypeGroupId=org.ikasan -DarchetypeArtifactId=ikasan-integration-module-jboss6-maven-plugin \
-DarchetypeVersion=<Ikasan Version> \
-DgroupId=<Maven Group Id> \
-DartifactId=<Module Name> \
-Dversion=<Module Version>

Where,
- Ikasan Version       - the version of the Ikasan platform being used. i.e. 1.0.0-rc4-SNAPSHOT
- Maven Group Id       - maven group coordinates you want this build pom to have
- Build Parent Name    - name of the Integration Module project.
- Build Parent Version - version of the Integration Module project
 
Example Usage,

For example,
mvn archetype:generate -DarchetypeGroupId=org.ikasan -DarchetypeArtifactId=ikasan-integration-module-jboss6-maven-plugin -DarchetypeVersion=1.0.0-rc4-SNAPSHOT -DgroupId=org.ikasan -DartifactId=MyModuleName -Dversion=1.0.0-SNAPSHOT 

(Accept defaults or update as required)

Then build it normally using mvn clean install


ikasan-rt-conf-jboss6-maven-plugin
---------------------------------------------
Creates a standard Ikasan configuration JBoss module for the runtime platform.

mvn archetype:generate -DarchetypeGroupId=org.ikasan -DarchetypeArtifactId=ikasan-rt-conf-jboss6-maven-plugin \
-DarchetypeVersion=<Ikasan Version> \
-DgroupId=<Maven Group Id> \
-DartifactId=<Module Name> \
-Dversion=<Module Version>

Where,
- Ikasan Version       - the version of the Ikasan platform being used. i.e. 1.0.0-rc4-SNAPSHOT
- Maven Group Id       - maven group coordinates you want this build pom to have
- Build Parent Name    - name of the Integration Module project.
- Build Parent Version - version of the Integration Module project

Example Usage,

For example,
mvn archetype:generate -DarchetypeGroupId=org.ikasan -DarchetypeArtifactId=ikasan-rt-conf-jboss6-maven-plugin -DarchetypeVersion=1.0.0-rc4-SNAPSHOT

(Accept defaults or update as required)

Then build it normally using mvn clean package assembly:assembly


ikasan-standalone-filesystem-im-maven-plugin
---------------------------------------------
Creates a standard Ikasan standalone module using local file system components.

```
mvn archetype:generate \
    -DarchetypeGroupId=org.ikasan \   
    -DarchetypeArtifactId=ikasan-standalone-filesystem-im-maven-plugin \
    -DarchetypeVersion=<Ikasan Version> \  
    -DgroupId=<Maven Group Id> \ 
    -DartifactId=<Module Name> \  
    -Dversion=<Module Version>  \   
    -DsourceFlowName=<Source Flow Name> \     
    -DtargetFlowName=<Target Flow Name> \
```
    
Where,
- Ikasan Version       - the version of the Ikasan platform being used. i.e. 2.0.0-rc2
- Maven Group Id       - maven group coordinates you want this build pom to have
- Module Name          - artifact is you want this build to have
- Module Version       - artifact version you want this build to have
- Source Flow Name     - name given to source flow
- Target Flow Name     - name given to target flow


Example Usage,

For example,
```
mvn archetype:generate \   
     -DarchetypeGroupId=org.ikasan \ 
     -DarchetypeArtifactId=ikasan-standalone-filesystem-im-maven-plugin \
     -DarchetypeVersion=2.0.0-SNAPSHOT \
     -DgroupId=com.sample \
     -DartifactId=fileSystem-im \     
     -Dversion=1.0.0-SNAPSHOT \
     -DsourceFlowName=fileSystemToJMSFlow \
     -DtargetFlowName=jmsToFileSystemFlow 
```
(Accept defaults or update as required)

Then build it normally using mvn clean package 


ikasan-standalone-jms-im-maven-plugin
---------------------------------------------
Creates a standard Ikasan standalone module using JMS endpoints.

```
mvn archetype:generate \
    -DarchetypeGroupId=org.ikasan \   
    -DarchetypeArtifactId=ikasan-standalone-jms-im-maven-plugin \
    -DarchetypeVersion=<Ikasan Version> \  
    -DgroupId=<Maven Group Id> \ 
    -DartifactId=<Module Name> \  
    -Dversion=<Module Version>  \   
    -DsourceFlowName=<Source Flow Name>
```
    
Where,
- Ikasan Version       - the version of the Ikasan platform being used. i.e. 2.0.0-rc2
- Maven Group Id       - maven group coordinates you want this build pom to have
- Module Name          - artifact is you want this build to have
- Module Version       - artifact version you want this build to have
- Source Flow Name     - name given to source flow

Example Usage,

For example,
```
mvn archetype:generate \   
     -DarchetypeGroupId=org.ikasan \ 
     -DarchetypeArtifactId=ikasan-standalone-jms-im-maven-plugin \
     -DarchetypeVersion=2.0.0-SNAPSHOT \
     -DgroupId=com.sample \
     -DartifactId=jms-im \     
     -Dversion=1.0.0-SNAPSHOT \
     -DsourceFlowName="Sample JMS Flow"
```
(Accept defaults or update as required)

Then build it normally using mvn clean package assembly:assembly



ikasan-standalone-sftp-jms-im-maven-plugin
---------------------------------------------
Creates a standard Ikasan standalone module using SFTP and JMS components.

```
mvn archetype:generate \
    -DarchetypeGroupId=org.ikasan \   
    -DarchetypeArtifactId=ikasan-standalone-sftp-jms-im-maven-plugin \
    -DarchetypeVersion=<Ikasan Version> \  
    -DgroupId=<Maven Group Id> \ 
    -DartifactId=<Module Name> \  
    -Dversion=<Module Version>  \   
    -DsourceFlowName=<Source Flow Name> \
    -DtargetFlowName=<Target Flow Name>
```
    
Where,
- Ikasan Version       - the version of the Ikasan platform being used. i.e. 2.0.0-rc2
- Maven Group Id       - maven group coordinates you want this build pom to have
- Module Name          - artifact is you want this build to have
- Module Version       - artifact version you want this build to have
- Source Flow Name     - name given to source flow
- Target Flow Name     - name given to target flow

Example Usage,

For example,
```
mvn archetype:generate    
     -DarchetypeGroupId=org.ikasan 
     -DarchetypeArtifactId=ikasan-standalone-sftp-jms-im-maven-plugin 
     -DarchetypeVersion=2.0.0-SNAPSHOT 
     -DgroupId=com.sample 
     -DartifactId=sftp-jms-im      
     -Dversion=1.0.0-SNAPSHOT 
     -DsourceFlowName="Sample SFTP to JMS Flow" 
     -DtargetFlowName="Sample JMS To SFTP Flow" 
```
(Accept defaults or update as required)

Then build it normally using mvn clean package assembly:assembly

ikasan-standalone-ftp-jms-im-maven-plugin
---------------------------------------------
Creates a standard Ikasan standalone module using FTP and JMS components.

```
mvn archetype:generate \
    -DarchetypeGroupId=org.ikasan \   
    -DarchetypeArtifactId=ikasan-standalone-ftp-jms-im-maven-plugin \
    -DarchetypeVersion=<Ikasan Version> \  
    -DgroupId=<Maven Group Id> \ 
    -DartifactId=<Module Name> \  
    -Dversion=<Module Version>  \   
    -DsourceFlowName=<Source Flow Name> \
    -DtargetFlowName=<Target Flow Name>
```
    
Where,
- Ikasan Version       - the version of the Ikasan platform being used. i.e. 2.0.0-rc2
- Maven Group Id       - maven group coordinates you want this build pom to have
- Module Name          - artifact is you want this build to have
- Module Version       - artifact version you want this build to have
- Source Flow Name     - name given to source flow
- Target Flow Name     - name given to target flow

Example Usage,

For example,
```
mvn archetype:generate    
     -DarchetypeGroupId=org.ikasan 
     -DarchetypeArtifactId=ikasan-standalone-ftp-jms-im-maven-plugin 
     -DarchetypeVersion=2.0.0-SNAPSHOT 
     -DgroupId=com.sample 
     -DartifactId=ftp-jms-im      
     -Dversion=1.0.0-SNAPSHOT 
     -DsourceFlowName="Sample FTP to JMS Flow" 
     -DtargetFlowName="Sample JMS To FTP Flow" 
```
(Accept defaults or update as required)

Then build it normally using mvn clean package assembly:assembly