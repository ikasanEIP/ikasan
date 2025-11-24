package org.ikasan.module.migration.util.maven.service;

import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.spec.metadata.BeanDefinitionMetaData;
import org.ikasan.spec.metadata.ImportedResourceMetaData;
import org.ikasan.spec.metadata.ModuleManifestMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocalBeanMigrationManager {
    private static Logger logger = LoggerFactory.getLogger(LocalBeanMigrationManager.class);

    private String projectBaseNamespace;
    private File migrationProjectBaseDirectory;
    private ModuleFileManager moduleFileManager;

    public LocalBeanMigrationManager(String projectBaseNamespace, ModuleFileManager moduleFileManager
        , String migrationProjectBaseDirectory) {
        this.projectBaseNamespace = projectBaseNamespace;
        this.moduleFileManager = moduleFileManager;
        this.migrationProjectBaseDirectory = new File(migrationProjectBaseDirectory);
    }

    /**
     * Migrates Spring beans from the provided ModuleManifestMetaData by copying Java files to the target directory.
     *
     * @param moduleManifestMetaData The ModuleManifestMetaData object containing bean definitions to migrate.
     */
    public void migrateSpringBeans(ModuleManifestMetaData moduleManifestMetaData) {
        List<String> classesToMigrate = moduleManifestMetaData.getBeanDefinitionMetaData().stream()
                .filter(beanDefinitionMetaData -> beanDefinitionMetaData.getBeanClass().startsWith(projectBaseNamespace))
                .map(beanDefinitionMetaData -> beanDefinitionMetaData.getBeanClass())
                .collect(Collectors.toList());

        classesToMigrate.addAll(moduleManifestMetaData.getBeanDefinitionMetaData().stream()
            .filter(beanDefinitionMetaData -> beanDefinitionMetaData.getBeanClass().startsWith(projectBaseNamespace) &&
                beanDefinitionMetaData.getBeanResource().startsWith(projectBaseNamespace) &&
                beanDefinitionMetaData.getType().equals("CONFIGURATION_CLASS_BEAN_DEFINITION"))
            .map(beanDefinitionMetaData -> beanDefinitionMetaData.getBeanResource())
            .distinct()
            .collect(Collectors.toList()));

        classesToMigrate.addAll(moduleManifestMetaData.getModuleMetaData().getFlows().stream()
                .flatMap(flowMetaData -> flowMetaData.getFlowElements().stream())
                    .filter(flowElementMetaData -> flowElementMetaData.getImplementingClass().startsWith(projectBaseNamespace))
                        .map(flowElementMetaData -> flowElementMetaData.getImplementingClass())
                            .collect(Collectors.toList()));

        classesToMigrate.addAll(moduleManifestMetaData.getImportedResourceMetaData().stream()
            .filter(importedResourceMetaData -> importedResourceMetaData.getSource().startsWith(projectBaseNamespace))
                .filter(importedResourceMetaData -> importedResourceMetaData.getResourceType()
                    .equals(ImportedResourceMetaData.IMPORTED_CONFIGURATION_CLASS))
                    .map(importedResourceMetaData -> importedResourceMetaData.getResource())
                        .collect(Collectors.toList()));


        classesToMigrate.forEach(classToMigrate -> {
            // migrate bean
            String beanNamespace = classToMigrate
                .substring(0, classToMigrate.lastIndexOf("."));

            String beanFilePath = beanNamespace.replaceAll("\\.", "/");
            String beanClassName = classToMigrate
                .substring(classToMigrate.lastIndexOf(".")+1
                    , classToMigrate.length());
            Path beanDir = Paths.get(moduleFileManager.getComponentsJavaSrcMainBase().getAbsolutePath(), beanFilePath);
            Path srcProjectJavaDir = Paths.get(this.migrationProjectBaseDirectory.getAbsolutePath(), "jar/src/main/java");
            Path sourceJavaFile = Paths.get(srcProjectJavaDir.toString(), beanFilePath, beanClassName+".java");
            Path targetJavaFile = Paths.get(beanDir.toString(), beanClassName+".java");
            try {
                // Create the nested directories
                Files.createDirectories(beanDir);
                Files.copy(sourceJavaFile, targetJavaFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Nested directories created successfully at: " + beanDir.toAbsolutePath());
            } catch (IOException e) {
                // Handle potential I/O errors during directory creation
                logger.error("Error creating nested directories: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Copies the missing dependency Java files to the target directory based on the provided missing dependency name.
     *
     * @param missingDependency the name of the missing dependency for which Java files need to be copied
     * @throws IOException if an I/O error occurs during the file copy process
     */
    public void copyMissingDependency(String missingDependency) throws IOException {
        List<Path> files = this.findFile(this.migrationProjectBaseDirectory.toPath(), missingDependency+".java");

        for (Path file : files) {
            String filePath = file.toAbsolutePath().toString().replace(this.migrationProjectBaseDirectory.getAbsolutePath()
                +"/jar/src/main/java/", "");
            Path targetFile = Paths.get(moduleFileManager.getComponentsJavaSrcMainBase().getAbsolutePath()
                , filePath);
            Files.createDirectories(targetFile.getParent());

            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void copyMissingPackage(String missingPackage) throws IOException {
        List<Path> files = this.findFile(new File(this.migrationProjectBaseDirectory
            .getAbsolutePath()+"/jar/src/main/java/"+missingPackage.replace(".", "/")).toPath(), "*");

        for (Path file : files) {
            String filePath = file.toAbsolutePath().toString().replace(this.migrationProjectBaseDirectory.getAbsolutePath()
                +"/jar/src/main/java/", "");
            Path targetFile = Paths.get(moduleFileManager.getComponentsJavaSrcMainBase().getAbsolutePath()
                , filePath);
            Files.createDirectories(targetFile.getParent());

            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Copies main resources from the specified directory to the target directory while maintaining the directory structure.
     * This method searches for files in the source directory and copies them to the target location.
     *
     * @throws IOException if an I/O error occurs during the file copy process
     */
    public void copyMainResources() throws IOException {
        List<Path> files = this.findFile(new File(this.migrationProjectBaseDirectory
            .getAbsolutePath()+"/jar/src/main/resources/").toPath(), "*");

        for (Path file : files) {
            String filePath = file.toAbsolutePath().toString().replace(this.migrationProjectBaseDirectory.getAbsolutePath()
                +"/jar/src/main/resources/", "");
            Path targetFile = Paths.get(moduleFileManager.getScaffoldingResourcesMainBase().getAbsolutePath()
                , filePath);
            Files.createDirectories(targetFile.getParent());

            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void copyFlowTests() throws IOException {
        List<Path> files = this.findFile(new File(this.migrationProjectBaseDirectory
            .getAbsolutePath()+"/jar/src/main/resources/").toPath(), "*");

        for (Path file : files) {
            String filePath = file.toAbsolutePath().toString().replace(this.migrationProjectBaseDirectory.getAbsolutePath()
                +"/jar/src/main/resources/", "");
            Path targetFile = Paths.get(moduleFileManager.getScaffoldingResourcesMainBase().getAbsolutePath()
                , filePath);
            Files.createDirectories(targetFile.getParent());

            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Finds files with a specific file name in the given starting path.
     *
     * @param startPath The starting path for the search
     * @param fileNameToFind The name of the file to find in the directory
     * @return A list of paths to files that match the specified file name in the directory or its subdirectories
     * @throws IOException If an I/O error occurs during the file search process
     */
    private List<Path> findFile(Path startPath, String fileNameToFind) throws IOException {
        List<Path> foundFiles = new ArrayList<>();
        Files.walkFileTree(startPath, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (fileNameToFind.equals("*") || file.getFileName().toString().equals(fileNameToFind)) {
                    foundFiles.add(file);
                }
                return FileVisitResult.CONTINUE; // Continue traversing
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                // Handle errors during file visit (e.g., permission denied)
                System.err.println("Failed to visit file: " + file + " - " + exc.getMessage());
                return FileVisitResult.CONTINUE; // Continue even if a file fails
            }
        });
        return foundFiles;
    }
}
