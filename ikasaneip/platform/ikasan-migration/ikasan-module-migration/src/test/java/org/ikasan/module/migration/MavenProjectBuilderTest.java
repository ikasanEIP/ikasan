package org.ikasan.module.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "M2_HOME", matches = ".*")
class MavenProjectBuilderTest {

    @TempDir
    Path tempDir;

    @Test
    void build() throws IOException {
        // Create a dummy project structure
        Path projectDir = tempDir.resolve("test-project");
        Files.createDirectories(projectDir);

        // Create a simple pom.xml
        Path pomFile = projectDir.resolve("pom.xml");
        String pomContent = "<project>" +
            "<modelVersion>4.0.0</modelVersion>" +
            "<groupId>com.test</groupId>" +
            "<artifactId>test-project</artifactId>" +
            "<version>1.0.0</version>" +
            "</project>";
        Files.write(pomFile, pomContent.getBytes());

        // Create the builder and build the project
        MavenProjectBuilder builder = new MavenProjectBuilder(System.getenv("M2_HOME"));
        boolean result = builder.build(projectDir.toFile(), "clean install");

        // Assert the build was successful
        assertTrue(result);
    }

    @Test
    public void test_against_real_project() throws IOException {
        MavenProjectBuilder builder = new MavenProjectBuilder(System.getenv("M2_HOME"));
        boolean result = builder.build(new File("/Users/mick/workspace/archetype/jms-demo/jar")
                , "clean test -Dtest=JmsSampleFlowTest#metadata_extractor");
        assertTrue(result);
    }
}
