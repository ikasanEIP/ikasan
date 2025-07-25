<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.ikasan</groupId>
        <artifactId>${moduleMetaData.name}-parent</artifactId>
        <version>${moduleMetaData.version}</version>
    </parent>

    <groupId>org.ikasan</groupId>
    <artifactId>${moduleMetaData.name}-components</artifactId>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-eip-standalone</artifactId>
            <version>${"$"}{version.ikasan}</version>
        </dependency>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-jms-spring-arjuna</artifactId>
            <version>${"$"}{version.ikasan}</version>
        </dependency>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-h2-standalone-persistence</artifactId>
            <version>${"$"}{version.ikasan}</version>
        </dependency>

        <dependency>
            <groupId>org.ikasan</groupId>
            <artifactId>ikasan-test</artifactId>
            <version>${"$"}{version.ikasan}</version>
            <scope>test</scope>
        </dependency>

<#--        <dependency>-->
<#--            <groupId>org.apache.activemq</groupId>-->
<#--            <artifactId>activemq-client</artifactId>-->
<#--        </dependency>-->

<#--        <dependency>-->
<#--            <groupId>org.apache.activemq</groupId>-->
<#--            <artifactId>activemq-broker</artifactId>-->
<#--        </dependency>-->

<#--        <dependency>-->
<#--            <groupId>org.awaitility</groupId>-->
<#--            <artifactId>awaitility</artifactId>-->
<#--            <scope>test</scope>-->
<#--        </dependency>-->

<#--        <dependency>-->
<#--            <groupId>org.glassfish.jaxb</groupId>-->
<#--            <artifactId>jaxb-runtime</artifactId>-->
<#--            <scope>test</scope>-->
<#--        </dependency>-->

<#--        <dependency>-->
<#--            <groupId>org.slf4j</groupId>-->
<#--            <artifactId>jul-to-slf4j</artifactId>-->
<#--            <scope>test</scope>-->
<#--        </dependency>-->

    </dependencies>

</project>
