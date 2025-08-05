package org.ikasan.module.migration.util.maven.service;

import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.spec.metadata.ModuleManifestMetaData;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class LocalBeanMigrationManager {
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
        moduleManifestMetaData.getBeanDefinitionMetaData().forEach(beanDefinitionMetaData -> {
            if(beanDefinitionMetaData.getBeanClass().startsWith(projectBaseNamespace)) {
                // migrate bean
                System.out.println(beanDefinitionMetaData.getBeanClass());
                String beanNamespace = beanDefinitionMetaData.getBeanClass()
                    .substring(0, beanDefinitionMetaData.getBeanClass().lastIndexOf("."));

                String beanFilePath = beanNamespace.replaceAll("\\.", "/");
                String beanClassName = beanDefinitionMetaData.getBeanClass()
                    .substring(beanDefinitionMetaData.getBeanClass().lastIndexOf(".")+1
                        , beanDefinitionMetaData.getBeanClass().length());
                Path beanDir = Paths.get(moduleFileManager.getComponentsJavaSrcMainBase().getAbsolutePath(), beanFilePath);
                Path srcProjectJavaDir = Paths.get(this.migrationProjectBaseDirectory.getAbsolutePath(), "jar/src/main/java");
                Path sourceJavaFile = Paths.get(srcProjectJavaDir.toString(), beanFilePath, beanClassName+".java");
                Path targetJavaFile = Paths.get(beanDir.toString(), beanClassName+".java");
                try {
                    // Create the nested directories
                    Files.createDirectories(beanDir);
                    Files.copy(sourceJavaFile, targetJavaFile, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Nested directories created successfully at: " + beanDir.toAbsolutePath());
                } catch (IOException e) {
                    // Handle potential I/O errors during directory creation
                    System.err.println("Error creating nested directories: " + e.getMessage());
                    e.printStackTrace();
                }
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
                if (file.getFileName().toString().equals(fileNameToFind)) {
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
