package org.ikasan.module.builder.service;

import org.ikasan.module.builder.model.properties.ModuleProperties;
import org.ikasan.spec.metadata.ModuleManifestMetaData;

public class ModuleManifestMetaDataModulePropertiesModelAdapter {

    public ModuleProperties adapt(ModuleManifestMetaData moduleManifestMetaData, String moduleBasePackage)
    {
        ModuleProperties moduleProperties = new ModuleProperties();
        moduleProperties.setModuleName(moduleManifestMetaData.getModuleMetaData().getName());

        moduleProperties.setComponentConfigurations
            (new ModuleManifestMetaDataConfigurationModelAdapter().adapt(moduleManifestMetaData, moduleBasePackage));

        return moduleProperties;
    }
}
