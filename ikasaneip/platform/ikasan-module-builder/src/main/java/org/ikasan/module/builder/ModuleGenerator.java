package org.ikasan.module.builder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.ikasan.module.builder.model.FlowModel;
import org.ikasan.module.builder.model.ModuleModel;
import org.ikasan.module.builder.service.ModuleMetaDataAdapter;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.spec.metadata.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ModuleGenerator {

    private ModuleFileManager moduleFileManager;
    private Configuration freeMarkerConfiguration;

    public ModuleGenerator(ModuleFileManager moduleFileManager) {
        this.moduleFileManager = moduleFileManager;

        this.freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_32);
        this.freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/templates");
        this.freeMarkerConfiguration.setDefaultEncoding("UTF-8");
        this.freeMarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.freeMarkerConfiguration.setLogTemplateExceptions(false);
        this.freeMarkerConfiguration.setWrapUncheckedExceptions(true);
    }

    public void generate(ModuleManifestMetaData root, String migrationProjectBasePackage) throws IOException, TemplateException {
        ModuleMetaData moduleMetaData = root.getModuleMetaData();

        File rootDir = new File(moduleMetaData.getName());
        rootDir.mkdirs();

        // Generate ModuleConfig.java
        File moduleConfigPackage = new File(this.moduleFileManager.getScaffoldingJavaSrcMainBase()
            , migrationProjectBasePackage.replaceAll("\\.", "/"));
        moduleConfigPackage.mkdirs();

        ModuleMetaDataAdapter adapter = new ModuleMetaDataAdapter();
        ModuleModel model = adapter.adapt(root, migrationProjectBasePackage);

        this.executionFreeMarkerTemplate(moduleConfigPackage, "Application.java.ftl"
            , model, "Application.java");

        this.executionFreeMarkerTemplate(moduleConfigPackage, "ModuleConfig.java.ftl"
            , model, "ModuleConfig.java");

        File flowConfigPackage = new File(this.moduleFileManager.getScaffoldingJavaSrcMainBase()
            , migrationProjectBasePackage.replaceAll("\\.", "/")+"/flow");
        flowConfigPackage.mkdirs();

        for (FlowModel flowModel : model.getFlowModelMap().values()) {
            this.executionFreeMarkerTemplate(flowConfigPackage, "FlowConfig.java.ftl"
                , flowModel, capitalizeFirst(flowModel.getName().replaceAll(" ", "")) + "Config.java");
        }

        moduleMetaData.getFlows().forEach(flowMetaData -> {
            flowMetaData.getFlowElements().forEach(flowElementMetaData -> {
                if(flowElementMetaData.getImplementingClass().startsWith("com.ikasan.sample.spring.boot")) {
                    System.out.println("I am a local component");
                }
            });
        });

        File componentConfigPackage = new File(this.moduleFileManager.getScaffoldingJavaSrcMainBase()
            , migrationProjectBasePackage.replaceAll("\\.", "/")+"/component");
        componentConfigPackage.mkdirs();

        this.executionFreeMarkerTemplate(componentConfigPackage, "ComponentFactory.java.ftl"
            , moduleMetaData, "ComponentFactory.java");

        this.executionFreeMarkerTemplate(this.moduleFileManager.getDistributionBase(), "distribution.xml.ftl"
            , root, "distribution.xml");

        this.executionFreeMarkerTemplate(this.moduleFileManager.getDistributionBase(), "distribution.xml.ftl"
            , root, "distribution.xml");

        this.generateAllModulePoms(rootDir, root);
        System.out.println("Successfully generated Ikasan module: " + moduleMetaData.getName());
    }

    /**
     * Generates POM files for all modules of a migrated module based on the provided module root directory and metadata.
     *
     * @param migrationRootDirectory The root directory of the module migration.
     * @param moduleManifestMetaData Metadata about the module manifest.
     * @throws TemplateException If an error occurs during template processing.
     * @throws IOException If an I/O error occurs.
     */
    private void generateAllModulePoms(File migrationRootDirectory
        , ModuleManifestMetaData moduleManifestMetaData) throws TemplateException, IOException {
        // Create the migrated module's parent POM.
        this.managePomCreation(migrationRootDirectory
            , "parent-pom.xml.ftl", moduleManifestMetaData);
        // Create the migrated module's scaffolding module POM.
        this.managePomCreation(moduleFileManager.getScaffoldingDir()
            , "scaffolding-pom.xml.ftl", moduleManifestMetaData);
        // Create the migrated module's components module POM.
        this.managePomCreation(moduleFileManager.getComponentsDir()
            , "components-pom.xml.ftl", moduleManifestMetaData);
        // Create the migrated module's distribution module POM.
        this.managePomCreation(moduleFileManager.getDistributionBase()
            , "distribution-pom.xml.ftl", moduleManifestMetaData);
    }

    /**
     * Manages the creation of a POM file using the provided FreeMarker configuration,
     * output directory, POM template name, and data object.
     *
     * @param outputDir The output directory where the POM file will be created.
     * @param pomTemplateName The name of the POM template to be used for generation.
     * @param data The data object to be processed with the POM template.
     * @throws IOException If an I/O error occurs during the file writing.
     * @throws TemplateException If an error occurs during template processing.
     */
    private void managePomCreation(File outputDir , String pomTemplateName, Object data)
        throws IOException, TemplateException {
        this.executionFreeMarkerTemplate(outputDir, pomTemplateName, data, "pom.xml");
    }

    /**
     * Executes the FreeMarker template processing for generating a file based on the provided template,
     * data, output directory, and output file name.
     *
     * @param outputDir The output directory where the file will be created.
     * @param pomTemplateName The name of the FreeMarker template for processing.
     * @param data The data object to be used in processing the FreeMarker template.
     * @param outputFileName The name of the output file to be generated.
     * @throws IOException If an I/O error occurs during file writing.
     * @throws TemplateException If an error occurs during FreeMarker template processing.
     */
    private void executionFreeMarkerTemplate(File outputDir , String pomTemplateName
        , Object data, String outputFileName)
        throws IOException, TemplateException {
        Template pomTemplate = this.freeMarkerConfiguration.getTemplate(pomTemplateName);
        try (Writer fileWriter = new FileWriter(new File(outputDir, outputFileName))) {
            pomTemplate.process(data, fileWriter);
        }
    }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str; // Handle null or empty strings
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
