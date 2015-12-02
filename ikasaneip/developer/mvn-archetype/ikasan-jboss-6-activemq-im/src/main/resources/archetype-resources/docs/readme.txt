Summary
=======
This Integration Module (IM) provides a simple example of a flow which subscribes to JMS and publishes to JMS.

Build Requirements
==================
Java JDK 1.7.x
Maven 3.3.x

Runtime Requirements
====================
JBoss EAP 6.2.x
HornetQ (shipped within JBoss EAP)

Archetype Creation
==================
This archetype can be used to create a JMS to JMS IM via the following Maven archeytpe command.

mvn archetype:generate \
-DarchetypeGroupId=org.ikasan \
-DarchetypeArtifactId=ikasan-im-jboss6-jms-maven-plugin \
-DarchetypeVersion=<Ikasan Version> \
-DgroupId=<Your project groupId> \
-DbuildParentGroupId=<Your project parent groupId> \
-Dversion=<your project version> \
-DartifactId=<IM Name> \
-DflowName=<IM Flow Name> \

For example,

mvn archetype:generate \
-DarchetypeGroupId=org.ikasan \
-DarchetypeArtifactId=ikasan-im-jboss6-jms-maven-plugin \
-DarchetypeVersion=1.0.3-SNAPSHOT \
-DgroupId=com.company.esb.project \
-DbuildParentGroupId=com.company.esb.project \
-Dversion=1.0.0-SNAPSHOT \
-DartifactId=MyIntegrationModule \
-DflowName=jmsFlow \
-DjbossModuleDir=com/company/esb/project/MyIntegrationModule/1-0-0-SNAPSHOT/conf/main

Build
=====
Once the archetype is created,

cd MyIntegrationModule
mvn clean package assembly:assembly

Deploy
======

Deploy the Jboss Module for this IM
-----------------------------------
cd MyIntegrationModule/target
unzip MyIntegrationModule-1-0-0-SNAPSHOT-dist.zip
cp -R AModuleJms-1.0.0-SNAPSHOT/jboss-eap/modules/com $JBOSS_HOME/modules


Deploy the IM via the JBoss Console
-----------------------------------
Open the Jboss Admin Console
Goto Deployments
Select the ear/target/MyIntegrationModule-ear-1.0.0-SNAPSHOT.ear
Load and assign to a server instance


