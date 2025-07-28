<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>org.ikasan</groupId>
    <artifactId>${moduleMetaData.name}-parent</artifactId>
    <version>${moduleMetaData.version}</version>
    <packaging>pom</packaging>

    <modules>
        <module>components</module>
        <module>scaffolding</module>
        <module>distribution</module>
    </modules>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <version.ikasan>4.1.1-SNAPSHOT</version.ikasan>
        <version.org.springboot>3.4.5</version.org.springboot>
        <version.activemq>6.1.6</version.activemq>
        <version.com.h2database>2.2.224</version.com.h2database>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.ikasan</groupId>
                <artifactId>ikasan-eip-standalone-bom</artifactId>
                <version>${"$"}{version.ikasan}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>com.diffplug.spotless</groupId>
                    <artifactId>spotless-maven-plugin</artifactId>
                    <version>2.45.0</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

</project>
