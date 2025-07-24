<assembly>
    <id>dist</id>
    <formats>
        <format>zip</format>
    </formats>

    <fileSets>
        <fileSet>
            <directory>../bin</directory>
            <filtered>true</filtered>
            <fileMode>755</fileMode>
            <outputDirectory>${moduleMetaData.name}-${moduleMetaData.version}</outputDirectory>
        </fileSet>
        <fileSet>
            <directory>../jar/src/main/resources</directory>
            <filtered>true</filtered>
            <includes>
                <include>application.properties</include>
                <include>logback-spring.xml</include>
            </includes>
            <outputDirectory>${moduleMetaData.name}-${moduleMetaData.version}/config</outputDirectory>
        </fileSet>
    </fileSets>

    <includeBaseDirectory>false</includeBaseDirectory>

    <dependencySets>
        <dependencySet>
            <!-- Enable access to all projects in the current multimodule build! -->
            <useProjectArtifact>false</useProjectArtifact>
            <!-- Now, select which projects to include in this module-set. -->
            <includes>
                <include>org.ikasan:jms-demo</include>
                <include>com.h2database:h2:jar:2.2.224</include>
            </includes>
            <outputDirectory>jms-demo-1.0.0-SNAPSHOT/lib</outputDirectory>
        </dependencySet>
        <dependencySet>
            <!-- Enable access to all projects in the current multimodule build! -->
            <useProjectArtifact>false</useProjectArtifact>
            <!-- contains lib\hell,jar and ikasan.sh -->
            <includes>
                <include>org.ikasan:ikasan-shell-distribution:zip:${moduleMetaData.ikasanVersion}</include>
            </includes>
            <unpack>true</unpack>
            <outputDirectory>${moduleMetaData.name}-${moduleMetaData.version}/</outputDirectory>
        </dependencySet>
    </dependencySets>
</assembly>