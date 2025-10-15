package org.ikasan.module.builder.service;

import org.ikasan.module.builder.model.configuration.ComponentConfiguration;
import org.ikasan.module.builder.model.configuration.ConfigurationParameter;
import org.ikasan.spec.metadata.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleManifestMetaDataConfigurationModelAdapter {


    /**
     * Adapts the ModuleManifestMetaData to a list of ComponentConfiguration objects based on the provided module base package.
     *
     * @param moduleManifestMetaData The metadata of the module to adapt.
     * @param moduleBasePackage The base package of the module to determine local components.
     * @return A list of ComponentConfiguration objects adapted from the module metadata.
     */
    public List<ComponentConfiguration> adapt(ModuleManifestMetaData moduleManifestMetaData, String moduleBasePackage) {
        Map<String, FlowElementMetaData> componentsMap = new HashMap<>();
        if(moduleManifestMetaData.getModuleMetaData() != null && moduleManifestMetaData.getModuleMetaData().getFlows() != null) {
            for (FlowMetaData flowMetaData : moduleManifestMetaData.getModuleMetaData().getFlows()) {
                flowMetaData.getFlowElements().forEach(flowElementMetaData -> {
                    if(flowElementMetaData.isConfigurable()) {
                        componentsMap.put(flowElementMetaData.getConfigurationId(), flowElementMetaData);
                    }
                });
            }
        }

        List<ComponentConfiguration> results = new ArrayList<>();
        for (ConfigurationMetaData configurationMetaData : moduleManifestMetaData.getConfigurationMetaData()) {
            ComponentConfiguration componentConfiguration = new ComponentConfiguration();
            componentConfiguration.setPackageName(configurationMetaData.getImplementingClass()
                .substring(0, configurationMetaData.getImplementingClass().lastIndexOf(".")));
            componentConfiguration.setClassName(configurationMetaData.getImplementingClass()
                .substring(configurationMetaData.getImplementingClass().lastIndexOf(".") + 1));
            componentConfiguration.setConfiguredResourceId(configurationMetaData.getConfigurationId());
            componentConfiguration.setImplementingClass(configurationMetaData.getImplementingClass());
            componentConfiguration.setComponentName(componentsMap.get(configurationMetaData.getConfigurationId()).getComponentName());
            componentConfiguration.setLocal(componentsMap.get(configurationMetaData.getConfigurationId())
                .getImplementingClass().startsWith(moduleBasePackage));
            List<ConfigurationParameterMetaData> configurationParameterMetaDataList
                = (List<ConfigurationParameterMetaData>) configurationMetaData.getParameters();

            List<ConfigurationParameter> configurationParameters = new ArrayList<>();
            for (ConfigurationParameterMetaData configurationParameterMetaData : configurationParameterMetaDataList) {
                configurationParameters.add(this.getConfigurationParameter(configurationParameterMetaData));
            }

            componentConfiguration.setConfigurationParameters(configurationParameters);

            results.add(componentConfiguration);
        }
        return results;
    }


    /**
     * Retrieves a ConfigurationParameter object based on the provided ConfigurationParameterMetaData.
     *
     * @param configurationParameterMetaData The metadata of the configuration parameter.
     * @return A ConfigurationParameter object with updated properties based on the metadata.
     */
    private ConfigurationParameter getConfigurationParameter(ConfigurationParameterMetaData configurationParameterMetaData) {
        ConfigurationParameter configurationParameter = new ConfigurationParameter();
        configurationParameter.setName(configurationParameterMetaData.getName());
        configurationParameter.setDescription(configurationParameterMetaData.getDescription());

        if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.Integer");
            configurationParameter.setType("Integer");
            if(configurationParameterMetaData.getValue() != null) {
                configurationParameter.setValue(configurationParameterMetaData.getValue().toString());
            }
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterLongImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.Long");
            configurationParameter.setType("Long");
            if(configurationParameterMetaData.getValue() != null) {
                configurationParameter.setValue(configurationParameterMetaData.getValue().toString());
            }
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterStringImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.String");
            configurationParameter.setType("String");
            if(configurationParameterMetaData.getValue() != null) {
                configurationParameter.setValue(configurationParameterMetaData.getValue().toString());
            }
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.Boolean");
            configurationParameter.setType("Boolean");
            if(configurationParameterMetaData.getValue() != null) {
                configurationParameter.setValue(configurationParameterMetaData.getValue().toString());
            }
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterListImpl")) {
            configurationParameter.setFullyQualifiedType("java.util.List");
            configurationParameter.setType("List<String>");
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterMapImpl")) {
            configurationParameter.setFullyQualifiedType("java.util.Map");
            configurationParameter.setType("Map<String, String>");
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.String");
            configurationParameter.setType("String");
            if(configurationParameterMetaData.getValue() != null) {
                configurationParameter.setValue(configurationParameterMetaData.getValue().toString());
            }
        }

        return configurationParameter;
    }
}
