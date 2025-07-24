package org.ikasan.module.builder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.builder.model.ModuleModel;
import org.ikasan.module.migration.util.maven.service.LocalBeanMigrationManager;
import org.ikasan.module.builder.service.ModuleMetaDataAdapter;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.module.migration.util.maven.MavenProjectBuilder;
import org.ikasan.module.migration.util.maven.model.CompilationFailureMissingClass;
import org.ikasan.spec.metadata.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ModuleGenerator {

    private ModuleFileManager moduleFileManager;
    private LocalBeanMigrationManager localBeanMigrationManager;

    public void generate(ModuleManifestMetaData root, String migrationProjectBasePackage,
                         ModuleFileManager moduleFileManager) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);

        ModuleMetaData moduleMetaData = root.getModuleMetaData();

        File rootDir = new File(moduleMetaData.getName());
        rootDir.mkdirs();

        this.moduleFileManager = moduleFileManager;

        // Generate pom.xml
        this.managePomCreation(cfg, rootDir, "parent-pom.xml.ftl", root);
        this.managePomCreation(cfg, moduleFileManager.getScaffoldingDir()
            , "scaffolding-pom.xml.ftl", root);
        this.managePomCreation(cfg, moduleFileManager.getComponentsDir()
            , "components-pom.xml.ftl", root);
        this.managePomCreation(cfg, moduleFileManager.getDistributionBase()
            , "distribution-pom.xml.ftl", root);

        this.localBeanMigrationManager = new LocalBeanMigrationManager("com.ikasan.sample.spring.boot",
            new File("/Users/mick/workspace/archetype/jms-demo"), moduleFileManager);

        // Generate ModuleConfig.java
        File moduleConfigPackage = new File(this.moduleFileManager.getScaffoldingJavaSrcMainBase()
            , migrationProjectBasePackage.replaceAll("\\.", "/"));
        moduleConfigPackage.mkdirs();

        ModuleMetaDataAdapter adapter = new ModuleMetaDataAdapter();
        ModuleModel model = adapter.adapt(root, migrationProjectBasePackage);

        Template applicationTemplate = cfg.getTemplate("Application.java.ftl");
        try (Writer fileWriter = new FileWriter(new File(moduleConfigPackage, "Application.java"))) {
            applicationTemplate.process(model, fileWriter);
        }

        Template moduleConfigTemplate = cfg.getTemplate("ModuleConfig.java.ftl");
        try (Writer fileWriter = new FileWriter(new File(moduleConfigPackage, "ModuleConfig.java"))) {
            moduleConfigTemplate.process(model, fileWriter);
        }

        Template flowConfigTemplate = cfg.getTemplate("FlowConfig.java.ftl");

        File flowConfigPackage = new File(this.moduleFileManager.getScaffoldingJavaSrcMainBase()
            , migrationProjectBasePackage.replaceAll("\\.", "/")+"/flow");
        flowConfigPackage.mkdirs();

        model.getFlowModelMap().values().forEach(flowModel -> {
            try (Writer fileWriter = new FileWriter(new File(flowConfigPackage
                , capitalizeFirst(flowModel.getName().replaceAll(" ", ""))+"Config.java"))) {
                flowConfigTemplate.process(flowModel, fileWriter);
            } catch (IOException | TemplateException e) {
                throw new RuntimeException(e);
            }
        });

        File componentConfigPackage = new File(this.moduleFileManager.getScaffoldingJavaSrcMainBase()
            , migrationProjectBasePackage.replaceAll("\\.", "/")+"/component");
        componentConfigPackage.mkdirs();

        Template componentConfigurationConfigTemplate = cfg.getTemplate("ComponentFactory.java.ftl");
        try (Writer fileWriter = new FileWriter(new File(componentConfigPackage, "ComponentFactory.java"))) {
            componentConfigurationConfigTemplate.process(moduleMetaData, fileWriter);
        }

        Template distributionConfigTemplate = cfg.getTemplate("distribution.xml.ftl");
        try (Writer fileWriter = new FileWriter(new File(this.moduleFileManager.getDistributionBase()
            , "distribution.xml"))) {
            distributionConfigTemplate.process(root, fileWriter);
        }

        this.localBeanMigrationManager.migrateSpringBeans(root);

        System.out.println("Successfully generated Ikasan module: " + moduleMetaData.getName());
    }

    private void managePomCreation(Configuration cfg, File outputDir , String pomTemplateName, Object data)
        throws IOException, TemplateException {
        Template pomTemplate = cfg.getTemplate(pomTemplateName);
        try (Writer fileWriter = new FileWriter(new File(outputDir, "pom.xml"))) {
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
