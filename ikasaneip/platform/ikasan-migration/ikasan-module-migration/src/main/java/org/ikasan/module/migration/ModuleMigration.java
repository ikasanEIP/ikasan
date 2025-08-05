package org.ikasan.module.migration;

import freemarker.template.TemplateException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.builder.ModuleGenerator;
import org.ikasan.module.migration.util.maven.MavenProjectBuilder;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.module.migration.util.maven.service.LocalBeanMigrationManager;
import org.ikasan.spec.metadata.ModuleManifestMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ModuleMigration {
    private static Logger logger = LoggerFactory.getLogger(ModuleMigration.class);

    private String migrationModuleName;
    private String migrationProjectBaseDirectory;
    private String migrationProjectBasePackageName;
    private String migrationWorkingDirectory;
    private String testClassName;
    private String migrationProjectMavenGroupId;

    private ModuleFileManager moduleFileManager;
    private ModuleBuildMigrationHelper moduleBuildMigrationHelper;
    private LocalBeanMigrationManager localBeanMigrationManager;

    /**
     * Constructs a new ModuleMigration object with the specified parameters.
     *
     * @param migrationProjectBaseDirectory The base directory of the migration project.
     * @param migrationProjectBasePackageName The base package name of the migration project.
     * @param migrationWorkingDirectory The working directory for the migration process.
     * @param testClassName The name of the test class to be used for migration.
     */
    public ModuleMigration(String migrationModuleName, String migrationProjectBaseDirectory, String migrationProjectBasePackageName
        , String migrationWorkingDirectory, String testClassName, String migrationProjectMavenGroupId) {
        this.migrationModuleName = migrationModuleName;
        this.migrationProjectBaseDirectory = migrationProjectBaseDirectory;
        this.migrationProjectBasePackageName = migrationProjectBasePackageName;
        this.migrationWorkingDirectory = migrationWorkingDirectory;
        this.testClassName = testClassName;
        this.migrationProjectMavenGroupId = migrationProjectMavenGroupId;

        // todo fix dir
        File rootDir = new File(this.migrationWorkingDirectory, this.migrationModuleName);
        rootDir.mkdirs();

        this.moduleFileManager = new ModuleFileManager(rootDir);
        this.localBeanMigrationManager = new LocalBeanMigrationManager(migrationProjectBasePackageName
            , this.moduleFileManager, this.migrationProjectBaseDirectory);
        this.moduleBuildMigrationHelper = new ModuleBuildMigrationHelper(localBeanMigrationManager,
            moduleFileManager);
    }

    /**
     * Method to migrate a module by performing a series of steps including modifying the Flow Test class,
     * running the Flow Test to extract module metadata, loading module manifest metadata,
     * generating a migrated module based on the metadata, and building the migrated module.
     *
     * @throws IOException if an I/O exception occurs during file operations.
     * @throws XmlPullParserException if an error occurs in parsing XML.
     * @throws TemplateException if an error occurs during template processing.
     */
    public void migrate() throws IOException, XmlPullParserException, TemplateException {
        File flowTest = this.getFlowTestFile();
        if(flowTest == null) {
            logger.info(String.format("Could not locate the test class[%s], in a directory under[%s]. Exiting!"
                , this.testClassName, this.migrationProjectBaseDirectory));
            return;
        }

        this.modifyFlowTestTestClassInOrderToExtractModuleMetaData(flowTest);
        this.runFlowTestTestTestClassInOrderToExtractModuleMetaData();

        ModuleManifestMetaData moduleManifestMetaData = this.loadModuleManifestMetaData();
        logger.info(ModuleManifestMetaDataHelper.serialiseModuleManifest(moduleManifestMetaData));

        this.generateMigratedModule(moduleManifestMetaData);
        this.localBeanMigrationManager.migrateSpringBeans(moduleManifestMetaData);
        this.buildMigratedModule();
    }

    /**
     * Retrieves the Flow Test file based on the provided migration project base directory and test class name.
     * This method utilizes a FlowTestInspector to search for the specific test file.
     *
     * @return The File object representing the located Flow Test file, or null if not found.
     * @throws IOException if an I/O exception occurs during file operations.
     */
    private File getFlowTestFile() throws IOException {
        FlowTestInspector inspector = new FlowTestInspector();
        return inspector.findFlowTest(new File(migrationProjectBaseDirectory), testClassName);
    }

    /**
     * Modifies the provided Flow Test class in order to extract module metadata.
     * This method adds metadata generation method, autowired application context, json configuration metadata extractor,
     * json module metadata provider, and a new dependency to the pom.xml file of the migration project.
     *
     * @param flowTest The File object representing the Flow Test class to be modified.
     * @throws IOException if an I/O exception occurs during file operations.
     * @throws XmlPullParserException if an error occurs in parsing XML.
     */
    private void modifyFlowTestTestClassInOrderToExtractModuleMetaData(File flowTest) throws IOException, XmlPullParserException {
        TestClassEditor testClassEditor = new TestClassEditor(this.migrationProjectBasePackageName, flowTest
            , new File(this.migrationWorkingDirectory + "/moduleMetaData.json"));
        testClassEditor.addMetaDataGenerationMethod("metadata_extractor");
        testClassEditor.addAutowiredApplicationContext(flowTest);
        testClassEditor.addJsonConfigurationMetaDataExtractor(flowTest);
        testClassEditor.addJsonModuleMetaDataProvider(flowTest);

//        PomEditor.removeDependency(new File(this.migrationProjectBaseDirectory + "/jar/pom.xml"),
//            "org.ikasan", "ikasan-manifest");
//        PomEditor.addDependency(new File(this.migrationProjectBaseDirectory + "/jar/pom.xml")
//            , "org.ikasan","ikasan-manifest", "3.3.9-alpha-SNAPSHOT");
//        PomEditor.removeDependency(new File(this.migrationProjectBaseDirectory + "/jar/pom.xml"),
//            "org.ikasan", "ikasan-spec-metadata");
//        PomEditor.addDependency(new File(this.migrationProjectBaseDirectory + "/jar/pom.xml")
//            , "org.ikasan","ikasan-spec-metadata", "3.3.9-alpha-SNAPSHOT");
    }

    /**
     * This method is responsible for running the flow test class in order to extract module metadata.
     */
    private void runFlowTestTestTestClassInOrderToExtractModuleMetaData() {
        MavenProjectBuilder builder = new MavenProjectBuilder(System.getenv("M2_HOME"));
        builder.build(new File(migrationProjectBaseDirectory+"/jar")
            , "clean test -Dtest="+testClassName+"#metadata_extractor");
    }

    /**
     * Loads the module manifest metadata by deserializing the content of the moduleMetaData.json file located in the
     * migrationWorkingDirectory.
     *
     * @return The deserialized ModuleManifestMetaData object representing the top-level manifest for an Ikasan module,
     *         or null if an error occurs during deserialization.
     * @throws IOException if an I/O exception occurs during file operations.
     */
    private ModuleManifestMetaData loadModuleManifestMetaData() throws IOException {
        byte[] moduleManifestMetaDataContents = Files.readAllBytes
            (new File(this.migrationWorkingDirectory + "/moduleMetaData.json").toPath());

        return ModuleManifestMetaDataHelper.deserialiseModuleManifest(new String(moduleManifestMetaDataContents));
    }

    /**
     * Generates a migrated module based on the provided ModuleManifestMetaData.
     *
     * @param moduleManifestMetaData The metadata of the module manifest to generate the migrated module.
     * @throws TemplateException if an error occurs during template processing.
     * @throws IOException if an I/O exception occurs during file operations.
     */
    private void generateMigratedModule(ModuleManifestMetaData moduleManifestMetaData) throws TemplateException, IOException {
        ModuleGenerator moduleGenerator = new ModuleGenerator(this.moduleFileManager, this.migrationProjectBasePackageName, this.migrationProjectMavenGroupId);
        moduleGenerator.generate(moduleManifestMetaData);
    }

    /**
     * This method is responsible for building a migrated module by invoking the runBuild method of the moduleBuildMigrationHelper.
     * It ensures that the module is built successfully by continuously attempting to clean and install the project until it passes.
     * If any missing dependencies are encountered during the build process, they are copied by the localBeanMigrationManager.
     *
     * @throws IOException if an I/O exception occurs during file operations
     */
    private void buildMigratedModule() throws IOException {
        this.moduleBuildMigrationHelper.runBuild();
    }
}
