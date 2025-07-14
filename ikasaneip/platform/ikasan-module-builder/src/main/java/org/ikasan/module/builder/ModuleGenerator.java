package org.ikasan.module.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.builder.model.ModuleModel;
import org.ikasan.module.builder.service.ModuleMetaDataAdapter;
import org.ikasan.spec.metadata.*;
import org.ikasan.topology.metadata.model.*;
import org.ikasan.manifest.model.*;
import org.ikasan.configurationService.metadata.ConfigurationMetaDataImpl;
import org.ikasan.configurationService.metadata.ConfigurationParameterMetaDataImpl;
import org.ikasan.spec.metadata.TypeParameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ModuleGenerator {
    public void generate(String jsonFile) throws IOException, TemplateException {
        ModuleManifestMetaData root = ModuleManifestMetaDataHelper.deserialiseModuleManifest(jsonFile);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);

        ModuleMetaData moduleMetaData = root.getModuleMetaData();

        File outputDir = new File(moduleMetaData.getName());
        outputDir.mkdirs();

        // Generate pom.xml
        Template pomTemplate = cfg.getTemplate("pom.xml.ftl");
        try (Writer fileWriter = new FileWriter(new File(outputDir, "pom.xml"))) {
            pomTemplate.process(root, fileWriter);
        }

        // Generate ModuleConfig.java
        File packageDir = new File(outputDir, "src/main/java/org/ikasan/module/generated");
        packageDir.mkdirs();

        Template moduleConfigTemplate = cfg.getTemplate("ModuleConfig.java.ftl");
        try (Writer fileWriter = new FileWriter(new File(packageDir, "ModuleConfig.java"))) {
            moduleConfigTemplate.process(root, fileWriter);
        }

        ModuleMetaDataAdapter adapter = new ModuleMetaDataAdapter();
        ModuleModel model = adapter.adapt(root);
        Template flowConfigTemplate = cfg.getTemplate("FlowConfig.java.ftl");

        model.getFlowModelMap().values().forEach(flowModel -> {
            try (Writer fileWriter = new FileWriter(new File(packageDir
                , capitalizeFirst(flowModel.getName().replaceAll(" ", ""))+"Config.java"))) {
                flowConfigTemplate.process(flowModel, fileWriter);
            } catch (IOException | TemplateException e) {
                throw new RuntimeException(e);
            }
        });

//            // Generate ComponentFactory.java
//            Template componentFactoryTemplate = cfg.getTemplate("ComponentFactory.java.ftl");
//            try (Writer fileWriter = new FileWriter(new File(packageDir, "ComponentFactory.java"))) {
//                componentFactoryTemplate.process(root, fileWriter);
//            }

        // Generate custom component classes and their configurations
//            List<Map<String, Object>> flows = (List<Map<String, Object>>) root.get("flows");
//            if (flows != null) {
//                for (Map<String, Object> flow : flows) {
//                    List<Map<String, String>> components = (List<Map<String, String>>) flow.get("components");
//                    if (components != null) {
//                        for (Map<String, String> component : components) {
//                            String className = component.get("className");
//                            if (className != null && className.startsWith(customComponentPackage)) {
//                                String packageName = customComponentPackage;
//                                String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
//                                String componentType = component.get("type");
//
//                                String interfaceType = "";
//                                String invokeMethodSignature = "";
//                                switch (componentType) {
//                                    case "consumer":
//                                        interfaceType = "org.ikasan.spec.component.consumer.Consumer";
//                                        invokeMethodSignature = "Object invoke()";
//                                        break;
//                                    case "producer":
//                                        interfaceType = "org.ikasan.spec.component.producer.Producer";
//                                        invokeMethodSignature = "void invoke(Object payload)";
//                                        break;
//                                    case "filter":
//                                        interfaceType = "org.ikasan.spec.component.filter.Filter";
//                                        invokeMethodSignature = "boolean invoke(Object payload)";
//                                        break;
//                                    case "converter":
//                                        interfaceType = "org.ikasan.spec.component.transformation.Converter";
//                                        invokeMethodSignature = "Object invoke(Object payload)";
//                                        break;
//                                    case "multiRecipientRouter":
//                                        interfaceType = "org.ikasan.spec.component.routing.MultiRecipientRouter";
//                                        invokeMethodSignature = "java.util.List invoke(Object payload)";
//                                        break;
//                                    case "singleRecipientRouter":
//                                        interfaceType = "org.ikasan.spec.component.routing.SingleRecipientRouterComponent";
//                                        invokeMethodSignature = "Object invoke(Object payload)";
//                                        break;
//                                    case "sequencer":
//                                        interfaceType = "org.ikasan.spec.component.sequencing.Sequencer";
//                                        invokeMethodSignature = "Object invoke(Object payload)";
//                                        break;
//                                    case "splitter":
//                                        interfaceType = "org.ikasan.spec.component.splitting.Splitter";
//                                        invokeMethodSignature = "java.util.List invoke(Object payload)";
//                                        break;
//                                    case "translator":
//                                        interfaceType = "org.ikasan.spec.component.transformation.Translator";
//                                        invokeMethodSignature = "Object invoke(Object payload)";
//                                        break;
//                                    default:
//                                        System.err.println("Unknown component type: " + componentType);
//                                        continue;
//                                }
//
//                                Map<String, Object> componentData = new HashMap<>();
//                                componentData.put("packageName", packageName);
//                                componentData.put("simpleClassName", simpleClassName);
//                                componentData.put("interfaceType", interfaceType);
//                                componentData.put("invokeMethodSignature", invokeMethodSignature);
//
//                                boolean isConfigured = component.containsKey("isConfigured") && Boolean.parseBoolean(component.get("isConfigured"));
//                                componentData.put("isConfigured", isConfigured);
//
//                                if (isConfigured) {
//                                    String configurationClassName = simpleClassName + "Configuration";
//                                    String configurationPackageName = packageName;
//                                    componentData.put("configurationClassName", configurationClassName);
//                                    componentData.put("configurationPackageName", configurationPackageName);
//
//                                    // Generate ComponentConfiguration.java
//                                    Template componentConfigurationTemplate = cfg.getTemplate("ComponentConfiguration.java.ftl");
//                                    File configPackageDir = new File(outputDir, "src/main/java/" + configurationPackageName.replace(".", "/"));
//                                    configPackageDir.mkdirs();
//                                    try (Writer fileWriter = new FileWriter(new File(configPackageDir, configurationClassName + ".java"))) {
//                                        Map<String, Object> configData = new HashMap<>();
//                                        configData.put("packageName", configurationPackageName);
//                                        configData.put("className", configurationClassName);
//                                        configData.put("configuration", component.get("configuration"));
//                                        componentConfigurationTemplate.process(configData, fileWriter);
//                                    }
//                                }
//
//                                // Generate CustomComponent.java
//                                Template customComponentTemplate = cfg.getTemplate("CustomComponent.java.ftl");
//                                File customComponentPackageDir = new File(outputDir, "src/main/java/" + packageName.replace(".", "/"));
//                                customComponentPackageDir.mkdirs();
//                                try (Writer fileWriter = new FileWriter(new File(customComponentPackageDir, simpleClassName + ".java"))) {
//                                    customComponentTemplate.process(componentData, fileWriter);
//                                }
//                            }
//                        }
//                    }
//                }
//            }

        System.out.println("Successfully generated Ikasan module: " + moduleMetaData.getName());
    }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str; // Handle null or empty strings
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
