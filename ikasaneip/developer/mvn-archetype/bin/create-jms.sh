export MODULENAME=AModule
export FLOWNAME=myFlow
export VERSION=1.0.0-SNAPSHOT
export DASHED_VERSION=1-0-0-SNAPSHOT

mvn archetype:generate \
-DarchetypeGroupId=org.ikasan \
-DarchetypeArtifactId=ikasan-im-jboss6-jms-maven-plugin \
-DarchetypeVersion=1.0.3-SNAPSHOT \
-DgroupId=com.mizuho.esb.mhsa \
-DbuildParentGroupId=com.mizuho.esb.mhsa \
-Dversion=${VERSION} \
-DartifactId=${MODULENAME} \
-DflowName=${FLOWNAME} \
-DjbossModuleDir=com/mizuho/esb/mhsa/${MODULENAME}/${DASHED_VERSION}/conf/main
