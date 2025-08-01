package org.ikasan.module.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlowTestInspector {

    /**
     * Finds a specific Flow Test file within the provided project directory based on the test name.
     *
     * @param projectDirectory The base directory of the project to search within.
     * @param testName         The name of the test to locate (excluding the ".java" extension).
     * @return The found File object representing the located Flow Test file, or null if not found.
     * @throws IOException if an I/O exception occurs during file operations.
     */
    public File findFlowTest(File projectDirectory, String testName) throws IOException {
        Path projectPath = projectDirectory.toPath();

        File testFile = null;
        try (Stream<Path> paths = Files.walk(projectPath)) {
            List<Path> pomFiles = paths
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().equals("pom.xml"))
                .collect(Collectors.toList());

            for (Path pomFile : pomFiles) {
                Path moduleDir = pomFile.getParent();
                Path testDir = moduleDir.resolve(Paths.get("src", "test", "java"));
                if (Files.exists(testDir)) {
                    try (Stream<Path> testFiles = Files.walk(testDir)) {
                        Optional<File> flowTestOptional = testFiles
                            .filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(testName+".java"))
                            .map(Path::toFile)
                            .findFirst();

                        if(flowTestOptional.isPresent()) {
                            testFile = flowTestOptional.get();
                        }
                    }
                }
            }
        }

        return testFile;
    }
}
