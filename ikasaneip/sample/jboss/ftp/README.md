Example use of FTP Consumer and FTP Producer
========================

The below instructions were checked on JBoss EAP 6.2

Pre-requisit:
JBoss 6.2 configured for running Ikasan

- Build whole ikasan or filetransfer module
 ```
cd ikasan/ikasaneip
mvn clean install```

- Create org.ikasan.ftp.ra module $JBOSS_HOME/modules

```tar cvf ikasaneip/filetransfer/connector-ftp/rar/target/ikasan-connector-ftpRar-1.0.0-rc5-SNAPSHOT $JBOSS_HOME/modules/org/ikasan/ftp/ra/main```

- create module.xml in $JBOSS_HOME/modules/org/ikasan/ftp/ra/main

```
<?xml version='1.0' encoding='UTF-8'?>
<module xmlns="urn:jboss:module:1.1" name="org.ikasan.ftp.ra">
    <resources>
        <resource-root path="."/>
        <resource-root path="commons-net-3.3.jar"/>
        <resource-root path="hamcrest-all-1.3.jar"/>
        <resource-root path="hibernate-core-4.3.9.Final.jar"/>
        <resource-root path="hibernate-commons-annotations-4.0.5.Final.jar"/>
		<resource-root path="hibernate-jpa-2.1-api-1.0.0.Final.jar"/>
		<resource-root path="hibernate-validator-4.3.2.Final.jar"/>
		<resource-root path="validation-api-1.0.0.GA.jar"/>

	    <resource-root path="javassist-3.18.1-GA.jar"/>
        <resource-root path="jandex-1.1.0.Final.jar"/>
		<resource-root path="antlr-2.7.7.jar"/>
        <resource-root path="xstream-1.3.1.jar"/>
    	<resource-root path="ikasan-client-connection-1.0.0-rc5-SNAPSHOT.jar"/>
		<resource-root path="ikasan-client-filetransfer-1.0.0-rc5-SNAPSHOT.jar"/>
		<resource-root path="ikasan-connector-base-1.0.0-rc5-SNAPSHOT.jar"/>
        <resource-root path="ikasan-connector-basefiletransfer-1.0.0-rc5-SNAPSHOT.jar"/>
        <resource-root path="ikasan-connector-ftp-classes-1.0.0-rc5-SNAPSHOT.jar"/>
        <resource-root path="ikasan-filetransfer-common-1.0.0-rc5-SNAPSHOT.jar"/>

    </resources>

    <dependencies>
        <module name="org.jboss.common-core" slot="main"/>
        <module name="javax.activation.api" export="true" />
        <module name="javax.annotation.api" export="true" />
        <module name="javax.resource.api" slot="main"/>
        <module name="org.jboss.logging" slot="main"/>
		<module name="org.jboss.jts" slot="main"/>
        <module name="org.apache.log4j" slot="main"/>
        <module name="org.slf4j" slot="main"/>
        <module name="org.dom4j" slot="main"/>
        <module name="com.sybase" slot="main"/>
        <module name="org.apache.commons.collections" slot="main"/>
        <module name="org.apache.commons.lang" slot="main"/>

		<module name="javax.api" slot="main"/>
    </dependencies>
</module>
```

- Build ikasan-sample-ftp

 ```cd ikasan/ikasaneip/sample/jboss/ftp
mvn clean install assembly:assembly```

- Create a private topic test.file - by running create-private-topics.cli script

```$JBOSS_HOME/bin/jboss-cli.sh --connect  "--file=target/ikasan-sample-ftp-1.0.0-rc5-SNAPSHOT-distribution/ikasan-sample-ftp-1.0.0-rc5-SNAPSHOT/jboss-eap/create-private-topics.cli" ```

- Create a resource adapter - by running ftp-resource-adapter.cli script

```$JBOSS_HOME/bin/jboss-cli.sh --connect  "--file=target/ikasan-sample-ftp-1.0.0-rc5-SNAPSHOT-distribution/ikasan-sample-ftp-1.0.0-rc5-SNAPSHOT/jboss-eap/ftp-resource-adapter.cli" ```

- Deploy ikasan-sample-ftp module

```cp target/ikasan-sample-ftp-1.0.0-rc5-SNAPSHOT-distribution/ikasan-sample-ftp-1.0.0-rc5-SNAPSHOT/jboss-eap/module $JBOSS_HOME/modules```

- Deploy ikasan-sample-ftp ear

```$JBOSS_HOME/bin/jboss-cli.sh --connect  "deploy --force ear/target/ikasan-sample-ftp-ear-1.0.0-rc5-SNAPSHOT.ear"```
