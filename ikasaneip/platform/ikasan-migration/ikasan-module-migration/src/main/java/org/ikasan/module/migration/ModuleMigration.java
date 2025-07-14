package org.ikasan.module.migration;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.spec.metadata.ModuleManifestMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ModuleMigration {
    private static Logger logger = LoggerFactory.getLogger(ModuleMigration.class);

    private String migrationProjectBaseDirectory;
    private String migrationProjectBasePackageName;
    private String migrationWorkingDirectory;
    private String testClassName;

    public ModuleMigration(String migrationProjectBaseDirectory, String migrationProjectBasePackageName
        , String migrationWorkingDirectory, String testClassName) {
        this.migrationProjectBaseDirectory = migrationProjectBaseDirectory;
        this.migrationProjectBasePackageName = migrationProjectBasePackageName;
        this.migrationWorkingDirectory = migrationWorkingDirectory;
        this.testClassName = testClassName;
    }

    /**
     * This method is responsible for executing the migration process. It performs the following steps:
     * 1. Retrieves the Flow Test file.
     * 2. Modifies the Flow Test class in order to extract module metadata.
     * 3. Runs the Flow Test class to extract module metadata.
     * 4. Loads the module manifest metadata.
     * 5. Logs the serialized module manifest metadata.
     *
     * @throws IOException if an I/O exception occurs during file operations.
     * @throws XmlPullParserException if an error occurs in parsing XML.
     */
    public void migrate() throws IOException, XmlPullParserException {
        File flowTest = this.getFlowTestFile();
        if(flowTest == null) {
            logger.info(String.format("Could not locate the test class[%s], in an directories under[%s]. Exiting!"
                , this.testClassName, this.migrationProjectBaseDirectory));
            return;
        }

        this.modifyFlowTestTestClassInOrderToExtractModuleMetaData(flowTest);
        this.runFlowTestTestTestClassInOrderToExtractModuleMetaData();

        ModuleManifestMetaData moduleManifestMetaData = this.loadModuleManifestMetaData();
        logger.info(ModuleManifestMetaDataHelper.serialiseModuleManifest(moduleManifestMetaData));
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
        PomEditor.addDependency(new File(this.migrationProjectBaseDirectory + "/jar/pom.xml")
            , "org.ikasan","ikasan-manifest", "4.1.1-SNAPSHOT");
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
     * Loads the module manifest metadata by deserializing the content of the moduleMetaData.json file located in the migrationWorkingDirectory.
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
}
