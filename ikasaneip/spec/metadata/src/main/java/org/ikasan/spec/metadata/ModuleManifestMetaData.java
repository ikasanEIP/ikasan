package org.ikasan.spec.metadata;

import java.util.List;

/**
 * Represents the top-level manifest for an Ikasan module.
 * This corresponds to the 'moduleManifest' element in the JSON representation.
 */
public interface ModuleManifestMetaData {

    /**
     * Get the core metadata for the module.
     * @return The module metadata.
     */
    ModuleMetaData getModuleMetaData();

    /**
     * Set the core metadata for the module.
     * @param moduleMetaData The module metadata.
     */
    void setModuleMetaData(ModuleMetaData moduleMetaData);

    /**
     * Get the list of configuration metadata for the components in the module.
     * @return A list of configuration metadata.
     */
    List<ConfigurationMetaData> getConfigurationMetaData();

    /**
     * Set the list of configuration metadata for the components in the module.
     * @param configurationMetaData A list of configuration metadata.
     */
    void setConfigurationMetaData(List<ConfigurationMetaData> configurationMetaData);

    /**
     * Get the dependency management information for the module.
     * @return The dependency management metadata.
     */
    DependencyManagementMetaData getDependencyManagement();

    /**
     * Set the dependency management information for the module.
     *
     * @param dependencyManagement The dependency management metadata.
     */
    void setDependencyManagement(DependencyManagementMetaData dependencyManagement);

    /**
     * Retrieves a list of ParameterizedType objects representing the parameterized types of this symbol.
     *
     * @return A list of ParameterizedType objects representing the parameterized types.
     */
    List<ParameterizedType> getParameterizedTypes();

    /**
     * Sets the list of ParameterizedType objects representing the parameterized types.
     *
     * @param parameterizedTypes A list of ParameterizedType objects representing the parameterized types to set.
     */
    void setParameterizedTypes(List<ParameterizedType> parameterizedTypes);

    /**
     * Retrieves a list of ConstructorMetaData objects representing the constructors of a symbol.
     *
     * @return A list of ConstructorMetaData objects representing the constructors.
     */
    List<ConstructorMetaData> getConstructorMetaData();

    /**
     * Sets the constructor metadata for a symbol. This method replaces any existing constructor metadata with the new metadata provided.
     *
     * @param constructorMetaData A list of ConstructorMetaData objects representing the constructors of a symbol
     */
    void setConstructorMetaData(List<ConstructorMetaData> constructorMetaData);

    /**
     * Retrieves the metadata for the bean definitions.
     *
     * @return A list of BeanDefinitionMetaData objects representing the bean definitions.
     */
    List<BeanDefinitionMetaData> getBeanDefinitionMetaData();

    /**
     * Set the bean definition metadata for a module manifest.
     *
     * @param beanDefinitionMetaData List of BeanDefinitionMetaData objects representing the bean definitions.
     */
    void setBeanDefinitionMetaData(List<BeanDefinitionMetaData> beanDefinitionMetaData);

    /**
     * Retrieves a list of ImportedResourceMetaData objects representing the imported resources metadata.
     *
     * @return A list of ImportedResourceMetaData objects.
     */
    List<ImportedResourceMetaData> getImportedResourceMetaData();

    /**
     * Sets the imported resource metadata for a module manifest.
     *
     * @param importedResourceMetaData The list of ImportedResourceMetaData objects to be set as imported resource metadata.
     */
    void setImportedResourceMetaData(List<ImportedResourceMetaData> importedResourceMetaData);

    /**
     * Retrieves the module's POM metadata.
     *
     * @return ModulePomMetaData containing information such as group ID, artifact ID, and version.
     */
    ModulePomMetaData getModulePomMetaData();

    /**
     * Set the POM metadata for the module.
     *
     * @param modulePomMetaData The POM metadata containing group ID, artifact ID, and version.
     */
    void setModulePomMetaData(ModulePomMetaData modulePomMetaData);
}
