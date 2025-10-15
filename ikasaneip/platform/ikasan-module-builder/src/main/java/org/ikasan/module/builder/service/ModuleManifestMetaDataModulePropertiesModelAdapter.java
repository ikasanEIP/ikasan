package org.ikasan.module.builder.service;

import org.ikasan.module.builder.model.properties.ModuleProperties;
import org.ikasan.spec.metadata.ModuleManifestMetaData;

public class ModuleManifestMetaDataModulePropertiesModelAdapter {

    /**
     * Adapts the provided ModuleManifestMetaData and module base package to create a ModuleProperties object.
     *
     * @param moduleManifestMetaData The ModuleManifestMetaData containing metadata for the module.
     * @param moduleBasePackage The base package of the module.
     * @return ModuleProperties object populated with adapted data.
     */
    public ModuleProperties adapt(ModuleManifestMetaData moduleManifestMetaData, String moduleBasePackage)
    {
        ModuleProperties moduleProperties = new ModuleProperties();
        moduleProperties.setModuleName(moduleManifestMetaData.getModuleMetaData().getName());

        moduleProperties.setComponentConfigurations
            (new ModuleManifestMetaDataConfigurationModelAdapter().adapt(moduleManifestMetaData, moduleBasePackage));

        return moduleProperties;
    }
}
