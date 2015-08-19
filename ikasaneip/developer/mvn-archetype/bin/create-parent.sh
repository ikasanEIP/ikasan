export MODULENAME=esb-build
export VERSION=1.0.0-SNAPSHOT
export DASHED_VERSION=1-0-0-SNAPSHOT

mvn archetype:generate \
-DarchetypeGroupId=org.ikasan \
-DarchetypeArtifactId=ikasan-build-parent-maven-plugin \
-DarchetypeVersion=1.0.3-SNAPSHOT \
-DgroupId=com.mizuho.esb.mhsa \
-Dversion=${VERSION} \
-DartifactId=${MODULENAME} \