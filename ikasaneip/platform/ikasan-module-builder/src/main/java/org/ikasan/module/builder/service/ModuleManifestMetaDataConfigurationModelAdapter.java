package org.ikasan.module.builder.service;

import org.ikasan.module.builder.model.configuration.ComponentConfiguration;
import org.ikasan.module.builder.model.configuration.ConfigurationParameter;
import org.ikasan.spec.metadata.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleManifestMetaDataConfigurationModelAdapter {


    public List<ComponentConfiguration> adapt(ModuleManifestMetaData moduleManifestMetaData, String moduleBasePackage)
    {
        List<ComponentConfiguration> results = new ArrayList<>();
        for (ConfigurationMetaData configurationMetaData : moduleManifestMetaData.getConfigurationMetaData()) {
            if(configurationMetaData.getImplementingClass().startsWith(moduleBasePackage)) {
                ComponentConfiguration componentConfiguration = new ComponentConfiguration();
                componentConfiguration.setPackageName(configurationMetaData.getImplementingClass()
                    .substring(0, configurationMetaData.getImplementingClass().lastIndexOf(".")));
                componentConfiguration.setClassName(configurationMetaData.getImplementingClass()
                    .substring(configurationMetaData.getImplementingClass().lastIndexOf(".") + 1));

                List<ConfigurationParameterMetaData> configurationParameterMetaDataList
                    = (List<ConfigurationParameterMetaData>) configurationMetaData.getParameters();

                List<ConfigurationParameter> configurationParameters = new ArrayList<>();
                for (ConfigurationParameterMetaData configurationParameterMetaData : configurationParameterMetaDataList) {
                    configurationParameters.add(this.getConfigurationParameter(configurationParameterMetaData));
                }

                componentConfiguration.setConfigurationParameters(configurationParameters);

                results.add(componentConfiguration);
            }
        }
        return results;
    }


    private ConfigurationParameter getConfigurationParameter(ConfigurationParameterMetaData configurationParameterMetaData) {
        ConfigurationParameter configurationParameter = new ConfigurationParameter();
        configurationParameter.setName(configurationParameterMetaData.getName());
        configurationParameter.setDescription(configurationParameterMetaData.getDescription());

        if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.Integer");
            configurationParameter.setType("Integer");
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterLongImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.Long");
            configurationParameter.setType("Long");
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterStringImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.String");
            configurationParameter.setType("String");
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.Boolean");
            configurationParameter.setType("Boolean");
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterListImpl")) {
            configurationParameter.setFullyQualifiedType("java.util.ArrayList");
            configurationParameter.setType("ArrayList<String>");
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterMapImpl")) {
            configurationParameter.setFullyQualifiedType("java.util.HashMap");
            configurationParameter.setType("HashMap<String, String>");
        }
        else if(configurationParameterMetaData.getImplementingClass()
            .equals("org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl")) {
            configurationParameter.setFullyQualifiedType("java.lang.String");
            configurationParameter.setType("String");
        }

        return configurationParameter;
    }
}
