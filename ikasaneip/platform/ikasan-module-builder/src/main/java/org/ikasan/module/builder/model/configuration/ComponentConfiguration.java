package org.ikasan.module.builder.model.configuration;

import java.util.List;

public class ComponentConfiguration {
    private String packageName;
    private String className;
    private String implementingClass;
    private String componentName;
    private String configuredResourceId;
    private List<ConfigurationParameter> configurationParameters;
    private boolean local = true;

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
     * Retrieves the implementing class associated with this ComponentConfiguration object.
     *
     * @return The implementing class as a String.
     */
    public String getImplementingClass() {
        return implementingClass;
    }

    /**
     *
     * Sets the implementing class for this ComponentConfiguration object.
     *
     * @param implementingClass The fully qualified name of the implementing class to set.
     */
    public void setImplementingClass(String implementingClass) {
        this.implementingClass = implementingClass;
    }

    /**
     * Retrieves the name of the component associated with this ComponentConfiguration object.
     *
     * @return The name of the component as a String.
     */
    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * Retrieves the configured resource ID associated with this ComponentConfiguration object.
     *
     * @return The configured resource ID as a String.
     */
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    /**
     * Sets the configured resource id for this ComponentConfiguration object.
     *
     * @param configuredResourceId The resource id to be set for the configuration object.
     */
    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
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

    /**
     * Determines if the ComponentConfiguration object is set to be local or not.
     *
     * @return true if the ComponentConfiguration object is local, false otherwise.
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * Sets the boolean flag indicating whether the ComponentConfiguration object is local or not.
     *
     * @param local The boolean value to set for the local flag.
     */
    public void setLocal(boolean local) {
        this.local = local;
    }
}
