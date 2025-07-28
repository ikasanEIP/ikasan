package org.ikasan.module.builder.model.component;

import org.ikasan.configurationService.metadata.ConfigurationMetaDataImpl;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;

import java.util.List;

public class ComponentConfigurationMetaData extends ConfigurationMetaDataImpl {
    private String configurationClassName;
    private String configurationPackageName;

    /**
     * Constructs a ComponentConfigurationMetaData object based on the provided ConfigurationMetaData.
     * Initializes the configuration id, description, implementing class, and parameters of the ComponentConfigurationMetaData.
     *
     * @param configurationMetaData The configuration metadata to create the ComponentConfigurationMetaData from.
     */
    public ComponentConfigurationMetaData(ConfigurationMetaData<List<ConfigurationParameterMetaData>> configurationMetaData) {
        super(configurationMetaData.getConfigurationId(), configurationMetaData.getDescription(),
                configurationMetaData.getImplementingClass(), configurationMetaData.getParameters());
    }

    /**
     * Get the name of the configuration class.
     *
     * @return the fully qualified class name representing the configuration class
     */
    public String getConfigurationClassName() {
        return configurationClassName;
    }

    /**
     * Set the class name for configuration metadata.
     * This method updates the configuration class name property of the object.
     *
     * @param configurationClassName The class name for configuration metadata.
     */
    public void setConfigurationClassName(String configurationClassName) {
        this.configurationClassName = configurationClassName;
    }

    /**
     * Retrieves the package name of the configuration.
     *
     * @return The package name of the configuration.
     */
    public String getConfigurationPackageName() {
        return configurationPackageName;
    }

    /**
     * Set the package name for the configuration metadata.
     *
     * @param configurationPackageName The package name to set.
     */
    public void setConfigurationPackageName(String configurationPackageName) {
        this.configurationPackageName = configurationPackageName;
    }
}
