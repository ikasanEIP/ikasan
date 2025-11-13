package org.ikasan.module.builder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.ikasan.module.builder.model.autoconfiguration.ComponentAutoConfiguration;
import org.ikasan.module.builder.model.component.Component;
import org.ikasan.module.builder.model.configuration.ComponentConfiguration;
import org.ikasan.module.builder.model.manifest.EnrichedModuleManifestMetaData;
import org.ikasan.module.builder.model.module.FlowModel;
import org.ikasan.module.builder.model.module.ModuleMetaDataModel;
import org.ikasan.module.builder.model.module.ModuleModel;
import org.ikasan.module.builder.service.*;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.module.migration.util.maven.service.LocalBeanMigrationManager;
import org.ikasan.spec.metadata.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class ModuleGenerator {

    private ModuleFileManager moduleFileManager;
    private Configuration freeMarkerConfiguration;
    private ModuleManifestMetaDataModuleModelAdapter moduleManifestMetaDataModuleModelAdapter;
    private LocalBeanMigrationManager localBeanMigrationManager;
    private String migrationProjectBasePackage;
    private String migrationProjectMavenGroupId;

    public ModuleGenerator(ModuleFileManager moduleFileManager, String migrationProjectBasePackage,
                           String migrationProjectMavenGroupId) {
        this.moduleFileManager = moduleFileManager;

        this.localBeanMigrationManager = new LocalBeanMigrationManager(migrationProjectBasePackage
            , this.moduleFileManager, this.moduleFileManager.getProjectRootDirectory().getAbsolutePath());

        this.migrationProjectBasePackage = migrationProjectBasePackage;
        this.migrationProjectMavenGroupId = migrationProjectMavenGroupId;

        this.freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_32);
        this.freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/templates");
        this.freeMarkerConfiguration.setDefaultEncoding("UTF-8");
        this.freeMarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.freeMarkerConfiguration.setLogTemplateExceptions(false);
        this.freeMarkerConfiguration.setWrapUncheckedExceptions(true);

        this.moduleManifestMetaDataModuleModelAdapter = new ModuleManifestMetaDataModuleModelAdapter();
    }

    public void generate(ModuleManifestMetaData moduleManifestMetaData) throws IOException, TemplateException {
        if(moduleManifestMetaData.getModuleMetaData().getVersion() == null) {
            // Manually set version if not automatically set. Old versions of ikasan do not carry ths through.
            moduleManifestMetaData.getModuleMetaData().setVersion("1.0.0-SNAPSHOT");
        }
        ModuleMetaData moduleMetaData = moduleManifestMetaData.getModuleMetaData();

        this.moduleFileManager.getProjectRootDirectory().mkdirs();

        ModuleModel model = this.moduleManifestMetaDataModuleModelAdapter.adapt(moduleManifestMetaData, migrationProjectBasePackage);

        // Generate ModuleConfig.java
        File moduleBootBasePackage = this.generateBaseBootDirectoryArtefacts(model, moduleManifestMetaData, migrationProjectBasePackage);

        File flowConfigPackage =  this.generateFlowConfigurationArtefacts(model, moduleBootBasePackage);

        this.generateFlowTestArtefacts(model);
        this.generateScaffoldingTestResources(moduleManifestMetaData);

        File componentArtefactsPackage = this.generateComponentArtefacts(moduleManifestMetaData, moduleBootBasePackage);

        this.generateDistributionArtefacts(moduleManifestMetaData);

        this.generateAllModulePoms(this.moduleFileManager.getProjectRootDirectory(), moduleManifestMetaData);

        this.generateComponentAutoConfiguration(moduleManifestMetaData);

        System.out.println("Successfully generated Ikasan module: " + moduleMetaData.getName());
    }

    /**
     * Generates base boot directory artefacts for a module based on the provided module model, module manifest metadata
     * , and migration project base package.
     *
     * @param model The module model containing information about the module.
     * @param root The module manifest metadata representing the top-level manifest for the module.
     * @param migrationProjectBasePackage The base package for the migration project.
     * @return The File object representing the base boot package directory where artefacts are generated.
     * @throws TemplateException If an error occurs during template processing.
     * @throws IOException If an I/O error occurs.
     */
    private File generateBaseBootDirectoryArtefacts(ModuleModel model
        , ModuleManifestMetaData root, String migrationProjectBasePackage)
        throws TemplateException, IOException {
        // Generate ModuleConfig.java
        File moduleBootBasePackage = new File(this.moduleFileManager.getScaffoldingJavaSrcMainBase()
            , migrationProjectBasePackage.replaceAll("\\.", "/"));
        moduleBootBasePackage.mkdirs();

        this.executionFreeMarkerTemplate(moduleBootBasePackage, "scaffolding/main/boot/Application.java.ftl"
            , model, "Application.java");

        this.executionFreeMarkerTemplate(moduleBootBasePackage, "scaffolding/main/boot/ModuleConfig.java.ftl"
            , model, "ModuleConfig.java");

        return moduleBootBasePackage;
    }

    /**
     * Generates flow configuration artefacts for a given module model.
     *
     * @param model The ModuleModel containing information about the module and flow models.
     * @param moduleBootBasePackage The base package directory for the module boot.
     * @return The File object representing the flow configuration package directory where artefacts are generated.
     * @throws TemplateException If an error occurs during template processing.
     * @throws IOException If an I/O error occurs.
     */
    private File generateFlowConfigurationArtefacts(ModuleModel model, File moduleBootBasePackage)
        throws TemplateException, IOException {
        File flowConfigPackage = new File(moduleBootBasePackage, "/flow");
        flowConfigPackage.mkdirs();

        for (FlowModel flowModel : model.getFlowModelMap().values()) {
            this.executionFreeMarkerTemplate(flowConfigPackage, "scaffolding/main/flow/FlowConfig.java.ftl"
                , flowModel, capitalizeFirst(flowModel.getName().replaceAll(" ", "")) + "Config.java");
        }

        return flowConfigPackage;
    }

    private void generateFlowTestArtefacts(ModuleModel model) throws TemplateException, IOException {
        File moduleBootTestPackage = new File(this.moduleFileManager.getScaffoldingJavaSrcTestBase()
            , migrationProjectBasePackage.replaceAll("\\.", "/"));
        moduleBootTestPackage.mkdirs();

        for (FlowModel flowModel : model.getFlowModelMap().values()) {
            this.executionFreeMarkerTemplate(moduleBootTestPackage, "scaffolding/test/resources/FlowTest.java.ftl"
                , flowModel, capitalizeFirst(flowModel.getName().replaceAll(" ", "")) + "Test.java");
        }
    }

    private void generateScaffoldingTestResources(ModuleManifestMetaData moduleManifestMetaData) throws TemplateException, IOException {
        ModuleManifestMetaDataModulePropertiesModelAdapter adapter = new ModuleManifestMetaDataModulePropertiesModelAdapter();
        this.executionFreeMarkerTemplate(this.moduleFileManager.getScaffoldingResourcesTestBase(), "scaffolding/test/resources/application.properties.ftl"
            , adapter.adapt(moduleManifestMetaData, this.migrationProjectBasePackage), "application.properties");
    }

    /**
     * Generates component artefacts for a given module based on the provided module metadata
     * and module boot base package directory.
     *
     * @param moduleManifestMetaData The metadata of the module containing information about the component.
     * @param moduleBootBasePackage The base package directory for the module boot.
     * @return The File object representing the component configuration package directory where artefacts are generated.
     * @*/
    private File generateComponentArtefacts(ModuleManifestMetaData moduleManifestMetaData, File moduleBootBasePackage)
        throws TemplateException, IOException {
        File componentConfigPackage = new File(moduleBootBasePackage, "/component");
        componentConfigPackage.mkdirs();

        ModuleMetaDataModel moduleMetaDataModel = new ModuleMetaDataModel(this.migrationProjectBasePackage
            , moduleManifestMetaData.getModuleMetaData());

        this.executionFreeMarkerTemplate(componentConfigPackage, "scaffolding/main/component/ComponentFactory.java.ftl"
            , moduleMetaDataModel, "ComponentFactory.java");

        this.generateComponents(moduleManifestMetaData);
        this.generateConfigurations(moduleManifestMetaData);

        return componentConfigPackage;
    }

    private void generateComponents(ModuleManifestMetaData moduleManifestMetaData) throws IOException, TemplateException {
        ModuleManifestMetaDataComponentModelAdapter adapter = new ModuleManifestMetaDataComponentModelAdapter();
        List<Component> components = adapter.adapt(moduleManifestMetaData, this.migrationProjectBasePackage, true);
        for (Component component : components) {
            File componentPackageDirectory = new File(this.moduleFileManager.getComponentsJavaSrcMainBase()
                    , component.getClassPackage().replaceAll("\\.", "/"));
            componentPackageDirectory.mkdirs();

            if(component.getComponentType().equals("org.ikasan.spec.component.endpoint.Broker")) {
                this.executionFreeMarkerTemplate(componentPackageDirectory, "components/main/component/CustomBroker.java.ftl"
                    , component, component.getClassName()+".java");
            }
            if(component.getComponentType().equals("org.ikasan.spec.component.splitting.Splitter")) {
                this.executionFreeMarkerTemplate(componentPackageDirectory, "components/main/component/CustomSplitter.java.ftl"
                    , component, component.getClassName()+".java");
            }
            else if(component.getComponentType().equals("org.ikasan.spec.component.transformation.Converter")) {
                this.executionFreeMarkerTemplate(componentPackageDirectory, "components/main/component/CustomConverter.java.ftl"
                    , component, component.getClassName()+".java");
            }
            else if(component.getComponentType().equals("org.ikasan.spec.component.transformation.Translator")) {
                this.executionFreeMarkerTemplate(componentPackageDirectory, "components/main/component/CustomTranslator.java.ftl"
                    , component, component.getClassName()+".java");
            }
            else if(component.getComponentType().equals("org.ikasan.spec.component.filter.Filter")) {
                this.executionFreeMarkerTemplate(componentPackageDirectory, "components/main/component/CustomFilter.java.ftl"
                    , component, component.getClassName()+".java");
            }
            else if(component.getComponentType().equals("org.ikasan.spec.component.routing.SingleRecipientRouter")) {
                this.executionFreeMarkerTemplate(componentPackageDirectory, "components/main/component/CustomSingleRecipientRouter.java.ftl"
                    , component, component.getClassName()+".java");
            }
            else if(component.getComponentType().equals("org.ikasan.spec.component.routing.MultiRecipientRouter")) {
                this.executionFreeMarkerTemplate(componentPackageDirectory, "components/main/component/CustomMultiRecipientRouter.java.ftl"
                    , component, component.getClassName()+".java");
            }
            else if(component.getComponentType().equals("org.ikasan.spec.component.endpoint.Producer")) {
                this.executionFreeMarkerTemplate(componentPackageDirectory, "components/main/component/CustomProducer.java.ftl"
                    , component, component.getClassName()+".java");
            }
        }
    }

    private void generateConfigurations(ModuleManifestMetaData moduleManifestMetaData) throws IOException, TemplateException {
        ModuleManifestMetaDataConfigurationModelAdapter adapter = new ModuleManifestMetaDataConfigurationModelAdapter();
        List<ComponentConfiguration> componentConfigurations = adapter.adapt(moduleManifestMetaData, this.migrationProjectBasePackage);
        for (ComponentConfiguration componentConfiguration : componentConfigurations) {
            if(componentConfiguration.isLocal()) {
                File componentConfigurationPackageDirectory = new File(this.moduleFileManager.getComponentsJavaSrcMainBase()
                    , componentConfiguration.getPackageName().replaceAll("\\.", "/"));
                componentConfigurationPackageDirectory.mkdirs();

                this.executionFreeMarkerTemplate(componentConfigurationPackageDirectory, "components/main/component/configuration/ComponentConfiguration.java.ftl"
                    , componentConfiguration, componentConfiguration.getClassName() + ".java");
            }
        }
    }

    public void generateComponentAutoConfiguration(ModuleManifestMetaData moduleManifestMetaData) throws TemplateException, IOException {
        ModuleManifestMetaDataConfigurationModelAdapter configurationModelAdapter = new ModuleManifestMetaDataConfigurationModelAdapter();
        List<ComponentConfiguration> componentConfigurations = configurationModelAdapter.adapt(moduleManifestMetaData, this.migrationProjectBasePackage);

        ModuleManifestMetaDataComponentModelAdapter componentModelAdapter = new ModuleManifestMetaDataComponentModelAdapter();
        List<Component> components = componentModelAdapter.adapt(moduleManifestMetaData, this.migrationProjectBasePackage, false);

        ModuleManifestMetaDataImportedResourcesAdapter importedResourcesAdapter = new ModuleManifestMetaDataImportedResourcesAdapter();
        List<ImportedResourceMetaData> importedConfigurationResources = importedResourcesAdapter.adapt(moduleManifestMetaData, this.migrationProjectBasePackage,
            ImportedResourceMetaData.IMPORTED_CONFIGURATION_CLASS);
        List<ImportedResourceMetaData> importedXmlResources = importedResourcesAdapter.adapt(moduleManifestMetaData, this.migrationProjectBasePackage,
            ImportedResourceMetaData.IMPORTED_XML_RESOURCE);

        ComponentAutoConfiguration componentAutoConfiguration = new ComponentAutoConfiguration
            (this.migrationProjectBasePackage, components, componentConfigurations, importedConfigurationResources, importedXmlResources);
        File componentAutoConfigPackageDirectory = new File(this.moduleFileManager.getComponentsJavaSrcMainBase()
            , componentAutoConfiguration.getPackageName().replaceAll("\\.", "/"));

        this.executionFreeMarkerTemplate(componentAutoConfigPackageDirectory, "components/main/component/ComponentsAutoConfiguration.java.ftl"
            , componentAutoConfiguration, "ComponentsAutoConfiguration.java");

    }

    /**
     * Generates distribution artefacts based on the provided module manifest metadata.
     *
     * @param moduleManifestMetaData The metadata representing the module manifest for the distribution.
     * @throws TemplateException If an error occurs during template processing.
     * @throws IOException If an I/O error occurs.
     */
    private void generateDistributionArtefacts(ModuleManifestMetaData moduleManifestMetaData)
        throws TemplateException, IOException {
        EnrichedModuleManifestMetaData enrichedModuleManifestMetaData
            = new EnrichedModuleManifestMetaData(moduleManifestMetaData);
        this.executionFreeMarkerTemplate(this.moduleFileManager.getDistributionBase(), "distribution/distribution.xml.ftl"
            , enrichedModuleManifestMetaData, "distribution.xml");
    }

    /**
     * Generates POM files for all modules of a migrated module based on the provided module moduleManifestMetaData directory and metadata.
     *
     * @param migrationRootDirectory The moduleManifestMetaData directory of the module migration.
     * @param moduleManifestMetaData Metadata about the module manifest.
     * @throws TemplateException If an error occurs during template processing.
     * @throws IOException If an I/O error occurs.
     */
    private void generateAllModulePoms(File migrationRootDirectory
        , ModuleManifestMetaData moduleManifestMetaData) throws TemplateException, IOException {
        EnrichedModuleManifestMetaData enrichedModuleManifestMetaData
            = new EnrichedModuleManifestMetaData(moduleManifestMetaData);

        // Create the migrated module's parent POM.
        this.managePomCreation(migrationRootDirectory
            , "parent-pom.xml.ftl", enrichedModuleManifestMetaData);
        // Create the migrated module's scaffolding module POM.
        this.managePomCreation(moduleFileManager.getScaffoldingDir()
            , "scaffolding/scaffolding-pom.xml.ftl", enrichedModuleManifestMetaData);
        // Create the migrated module's components module POM.
        this.managePomCreation(moduleFileManager.getComponentsDir()
            , "components/components-pom.xml.ftl", enrichedModuleManifestMetaData);
        // Create the migrated module's distribution module POM.
        this.managePomCreation(moduleFileManager.getDistributionBase()
            , "distribution/distribution-pom.xml.ftl", enrichedModuleManifestMetaData);
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
     * @param templateName The name of the FreeMarker template for processing.
     * @param data The data object to be used in processing the FreeMarker template.
     * @param outputFileName The name of the output file to be generated.
     * @throws IOException If an I/O error occurs during file writing.
     * @throws TemplateException If an error occurs during FreeMarker template processing.
     */
    private void executionFreeMarkerTemplate(File outputDir, String templateName
        , Object data, String outputFileName)
        throws IOException, TemplateException {
        Template pomTemplate = this.freeMarkerConfiguration.getTemplate(templateName);
        outputDir.mkdirs();
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
