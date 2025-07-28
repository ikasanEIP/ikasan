package org.ikasan.module.builder.model.configuration;

import java.util.List;

public class ComponentConfiguration {
    private String packageName;
    private String className;
    private List<ConfigurationParameter> configurationParameters;

    /**
     * Retrieves the package name associated with this ComponentConfiguration object.
     *
     * @return The package name.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the package name for the ComponentConfiguration object.
     *
     * @param packageName The new package name to set.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Retrieves the class name stored in this ComponentConfiguration object.
     *
     * @return The class name as a String.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name of the configuration object.
     *
     * @param className The name of the class to be set.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Retrieves the list of configuration parameters associated with this ComponentConfiguration.
     *
     * @return List of ConfigurationParameter objects representing the configuration parameters.
     */
    public List<ConfigurationParameter> getConfigurationParameters() {
        return configurationParameters;
    }

    /**
     * Sets the list of configuration parameters for this configuration.
     *
     * @param configurationParameters the list of ConfigurationParameter objects to set
     */
    public void setConfigurationParameters(List<ConfigurationParameter> configurationParameters) {
        this.configurationParameters = configurationParameters;
    }
}
