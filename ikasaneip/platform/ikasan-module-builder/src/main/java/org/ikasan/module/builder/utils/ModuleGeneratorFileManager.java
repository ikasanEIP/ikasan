package org.ikasan.module.builder.utils;

import java.io.File;

public class ModuleGeneratorFileManager {
    private File scaffoldingDir;
    private File componentsDir;
    private File distributionBase;
    private File scaffoldingJavaSrcMainBase;
    private File scaffoldingResourcesMainBase;
    private File scaffoldingJavaSrcTestBase;
    private File scaffoldingResourcesTestBase;
    private File componentsJavaSrcMainBase;
    private File componentsResourcesMainBase;
    private File componentsJavaSrcTestBase;
    private File componentsResourcesTestBase;
    private File binBase;

    /**
     * Constructs a ModuleGeneratorFileManager instance with the specified root directory.
     *
     * @param rootDir the root directory where the module generator files will be generated
     */
    public ModuleGeneratorFileManager(File rootDir) {
        this.scaffoldingDir = this.createChildDirectory(rootDir, ModuleGeneratorConstants.SCAFFOLDING_BASE);
        this.componentsDir = this.createChildDirectory(rootDir, ModuleGeneratorConstants.COMPONENTS_BASE);
        this.distributionBase = this.createChildDirectory(rootDir, ModuleGeneratorConstants.DISTRIBUTION_BASE);
        this.scaffoldingJavaSrcMainBase = this.createChildDirectory(scaffoldingDir, ModuleGeneratorConstants.JAVA_MAIN_SRC_DIR);
        this.scaffoldingResourcesMainBase = this.createChildDirectory(scaffoldingDir, ModuleGeneratorConstants.MAIN_RESOURCES_DIR);
        this.scaffoldingJavaSrcTestBase = this.createChildDirectory(scaffoldingDir, ModuleGeneratorConstants.JAVA_TEST_SRC_DIR);
        this.scaffoldingResourcesTestBase = this.createChildDirectory(scaffoldingDir, ModuleGeneratorConstants.TEST_RESOURCES_DIR);
        this.componentsJavaSrcMainBase = this.createChildDirectory(componentsDir, ModuleGeneratorConstants.JAVA_MAIN_SRC_DIR);
        this.componentsResourcesMainBase = this.createChildDirectory(componentsDir, ModuleGeneratorConstants.MAIN_RESOURCES_DIR);
        this.componentsJavaSrcTestBase = this.createChildDirectory(componentsDir, ModuleGeneratorConstants.JAVA_TEST_SRC_DIR);
        this.componentsResourcesTestBase = this.createChildDirectory(componentsDir, ModuleGeneratorConstants.TEST_RESOURCES_DIR);
        this.binBase = this.createChildDirectory(rootDir, ModuleGeneratorConstants.BIN_DIR);
    }

    /**
     * Creates a child directory inside the specified output directory.
     *
     * @param outputDir The parent directory in which the child directory will be created.
     * @param child The name of the child directory to be created.
     * @return The File object representing the newly created child directory.
     */
    private File createChildDirectory(File outputDir, String child) {
        File childDirectory = new File(outputDir.getAbsolutePath()+"/"+child);
        childDirectory.mkdirs();

        return childDirectory;
    }

    /**
     * Retrieves the directory where the scaffolding files are stored.
     *
     * @return File object representing the scaffolding directory
     */
    public File getScaffoldingDir() {
        return scaffoldingDir;
    }

    /**
     * Returns the directory where components are stored.
     *
     * @return the components directory
     */
    public File getComponentsDir() {
        return componentsDir;
    }

    /**
     * Gets the base directory for the distribution files.
     *
     * @return the distribution base directory
     */
    public File getDistributionBase() {
        return distributionBase;
    }

    /**
     * Returns the base directory for the Java source code of the scaffolding module.
     *
     * @return The base directory for the Java source code of the scaffolding module.
     */
    public File getScaffoldingJavaSrcMainBase() {
        return scaffoldingJavaSrcMainBase;
    }

    /**
     * Retrieves the base directory for scaffolding Java resources.
     *
     * @return The base directory for scaffolding Java resources.
     */
    public File getScaffoldingResourcesMainBase() {
        return scaffoldingResourcesMainBase;
    }

    /**
     * Returns the base directory for Java source test files in the scaffolding module.
     *
     * @return The base directory for Java source test files in the scaffolding module
     */
    public File getScaffoldingJavaSrcTestBase() {
        return scaffoldingJavaSrcTestBase;
    }

    /**
     * Retrieves the base directory for scaffolding Java test resources.
     *
     * @return The base directory for scaffolding Java test resources
     */
    public File getScaffoldingResourcesTestBase() {
        return scaffoldingResourcesTestBase;
    }

    /**
     * Returns the base directory for Java source files related to components.
     *
     * @return the base directory for Java source files related to components
     */
    public File getComponentsJavaSrcMainBase() {
        return componentsJavaSrcMainBase;
    }

    /**
     * Returns the base directory for components Java resources in the main source set.
     * This directory is used for storing resources related to components in the main Java source set.
     *
     * @return The base directory for components Java resources in the main source set
     */
    public File getComponentsResourcesMainBase() {
        return componentsResourcesMainBase;
    }

    /**
     * Retrieves the base directory for Java source files related to components testing.
     *
     * @return the base directory for Java source files related to components testing
     */
    public File getComponentsJavaSrcTestBase() {
        return componentsJavaSrcTestBase;
    }

    /**
     * Retrieves the base directory for Java resources related to components.
     *
     * @return the base directory for Java resources related to components
     */
    public File getComponentsResourcesTestBase() {
        return componentsResourcesTestBase;
    }

    /**
     * Returns the base directory for the binary files.
     *
     * @return The base directory for the binary files.
     */
    public File getBinBase() {
        return binBase;
    }
}
