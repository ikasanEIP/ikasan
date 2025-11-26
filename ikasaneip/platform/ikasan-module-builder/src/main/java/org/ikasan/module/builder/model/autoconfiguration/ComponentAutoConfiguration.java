package org.ikasan.module.builder.model.autoconfiguration;

import org.ikasan.module.builder.model.component.BeanComponent;
import org.ikasan.module.builder.model.component.Component;
import org.ikasan.module.builder.model.configuration.ComponentConfiguration;
import org.ikasan.spec.metadata.ImportedResourceMetaData;

import java.util.List;

public class ComponentAutoConfiguration {
    private String packageName;
    private List<Component> components;
    private List<BeanComponent> beanComponents;
    private List<ComponentConfiguration> componentConfigurations;
    private List<ImportedResourceMetaData> importedClassConfigurationResources;
    private List<ImportedResourceMetaData> importedXmlResources;

    /**
     * Construct a new ComponentAutoConfiguration with the provided parameters.
     *
     * @param packageName The package name associated with the ComponentAutoConfiguration
     * @param components List of Component objects to be stored in the ComponentAutoConfiguration
     * @param beanComponents List of BeanComponent objects to be stored in the ComponentAutoConfiguration
     * @param componentConfigurations List of ComponentConfiguration objects associated with the ComponentAutoConfiguration
     * @param importedClassConfigurationResources List of ImportedResourceMetaData objects representing imported class
     *                                            configuration resources
     * @param importedXmlResources List of ImportedResourceMetaData objects representing imported XML resources
     */
    public ComponentAutoConfiguration(String packageName, List<Component> components, List<BeanComponent> beanComponents
        , List<ComponentConfiguration> componentConfigurations
        , List<ImportedResourceMetaData> importedClassConfigurationResources
        , List<ImportedResourceMetaData> importedXmlResources) {
        this.packageName = packageName;
        this.components = components;
        this.beanComponents = beanComponents;
        this.componentConfigurations = componentConfigurations;
        this.importedClassConfigurationResources = importedClassConfigurationResources;
        this.importedXmlResources = importedXmlResources;
    }

    /**
     * Retrieves the package name associated with this ComponentAutoConfiguration.
     *
     * @return The package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Retrieves the list of components stored in this ComponentAutoConfiguration.
     *
     * @return The list of components
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Retrieves the list of BeanComponents stored in this ComponentAutoConfiguration.
     *
     * @return The list of BeanComponents
     */
    public List<BeanComponent> getBeanComponents() {
        return beanComponents;
    }

    /**
     * Retrieves the list of ComponentConfigurations associated with this ComponentAutoConfiguration object.
     *
     * @return List of ComponentConfiguration objects representing the component configurations.
     */
    public List<ComponentConfiguration> getComponentConfigurations() {
        return componentConfigurations;
    }

    /**
     * Retrieves the list of imported class configuration resources stored in this ComponentAutoConfiguration object.
     *
     * @return List of ImportedResourceMetaData objects representing the imported class configuration resources.
     */
    public List<ImportedResourceMetaData> getImportedClassConfigurationResources() {
        return importedClassConfigurationResources;
    }

    /**
     * Retrieves the list of imported XML resources stored in this ComponentAutoConfiguration object.
     *
     * @return List of ImportedResourceMetaData objects representing the imported XML resources.
     */
    public List<ImportedResourceMetaData> getImportedXmlResources() {
        return importedXmlResources;
    }
}
