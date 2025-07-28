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

    <build>
        <plugins>
            <plugin>
                <groupId>com.diffplug.spotless</groupId>
                <artifactId>spotless-maven-plugin</artifactId>
                <version>2.45.0</version>
                <configuration>
                    <java>
                        <!-- These are the defaults, you can override if you want -->
                        <includes>
                            <include>src/main/java/**/*.java</include>
                            <#--                            <include>src/test/java/**/*.java</include>-->
                        </includes>

                        <!-- Cleanthat will refactor your code, but it may break your style: apply it before your formatter -->
                        <cleanthat />        <!-- has its own section below -->

                        <googleJavaFormat>
                            <version>1.28.0</version>                      <!-- optional, 1.8 is the minimum supported version for Java 11 -->
                            <style>AOSP</style>                       <!-- or AOSP (optional) -->
                            <reflowLongStrings>true</reflowLongStrings> <!-- optional -->
                            <formatJavadoc>false</formatJavadoc>        <!-- optional -->
                            <!-- optional: custom group artifact (you probably don't need this) -->
                            <groupArtifact>com.google.googlejavaformat:google-java-format</groupArtifact>
                        </googleJavaFormat>

                        <#--                        <eclipse />          <!-- has its own section below &ndash;&gt;-->
                        <#--                        <prettier />         <!-- has its own section below &ndash;&gt;-->
                        <#--                        <idea />             <!-- has its own section below &ndash;&gt;-->

                        <importOrder /> <!-- standard import order -->
                        <importOrder>  <!-- or a custom ordering -->
                            <wildcardsLast>false</wildcardsLast> <!-- Optional, default false. Sort wildcard import after specific imports -->
                            <#--                            <order>java|javax,org,com,com.diffplug,,\#com.diffplug,\#</order>  <!-- or use <file>{project.basedir}/eclipse.importorder</file> &ndash;&gt;-->
                            <!-- you can use an empty string for all the imports you didn't specify explicitly, '|' to join group without blank line, and '\#` prefix for static imports. -->
                            <semanticSort>false</semanticSort> <!-- Optional, default false. Sort by package, then class, then member (for static imports). Splitting is based on common conventions (packages are lower case, classes start with upper case). Use <treatAsPackage> and <treatAsClass> for exceptions. -->
                            <#--                            <treatAsPackage> <!-- Packages starting with upper case letters. &ndash;&gt;-->
                            <#--                                <package>com.example.MyPackage</package>-->
                            <#--                            </treatAsPackage>-->
                            <#--                            <treatAsClass> <!-- Classes starting with lower case letters. &ndash;&gt;-->
                            <#--                                <class>com.example.myClass</class>-->
                            <#--                            </treatAsClass>-->
                        </importOrder>

                        <#--                        <removeUnusedImports /> <!-- self-explanatory &ndash;&gt;-->
                        <#--                        <removeWildcardImports /> <!-- drop any import ending with '*' &ndash;&gt;-->

                        <#--                        <formatAnnotations />  <!-- fixes formatting of type annotations, see below &ndash;&gt;-->

                        <licenseHeader>
                            <content>/* (C)$YEAR */</content>  <!-- or <file>{project.basedir}/license-header</file> -->
                        </licenseHeader>
                    </java>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
