package org.ikasan.module.builder.model.autoconfiguration;

import org.ikasan.module.builder.model.component.Component;
import org.ikasan.module.builder.model.configuration.ComponentConfiguration;
import org.ikasan.spec.metadata.ImportedResourceMetaData;

import java.util.List;

public class ComponentAutoConfiguration {
    private String packageName;
    private List<Component> components;
    private List<ComponentConfiguration> componentConfigurations;
    private List<ImportedResourceMetaData> importedClassConfigurationResources;
    private List<ImportedResourceMetaData> importedXmlResources;

    public ComponentAutoConfiguration(String packageName, List<Component> components, List<ComponentConfiguration> componentConfigurations
        , List<ImportedResourceMetaData> importedClassConfigurationResources
        , List<ImportedResourceMetaData> importedXmlResources) {
        this.packageName = packageName;
        this.components = components;
        this.componentConfigurations = componentConfigurations;
        this.importedClassConfigurationResources = importedClassConfigurationResources;
        this.importedXmlResources = importedXmlResources;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<ComponentConfiguration> getComponentConfigurations() {
        return componentConfigurations;
    }

    public List<ImportedResourceMetaData> getImportedClassConfigurationResources() {
        return importedClassConfigurationResources;
    }

    public List<ImportedResourceMetaData> getImportedXmlResources() {
        return importedXmlResources;
    }
}
