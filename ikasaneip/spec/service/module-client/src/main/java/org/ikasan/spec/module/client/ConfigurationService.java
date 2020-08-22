package org.ikasan.spec.module.client;

import org.ikasan.spec.metadata.ConfigurationMetaData;

import java.util.List;

public interface ConfigurationService
{
    /**
     * Get the configuration metadata for all components in a module.
     * @param contextUrl
     * @return
     */
    public List<ConfigurationMetaData> getComponents(String contextUrl);


    /**
     * Get configuration metadata for a specific flow in a module.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    public List<ConfigurationMetaData> getFlowComponents(String contextUrl, String moduleName, String flowName);


    /**
     * Get all invoker configuration metadata for a module.
     * @param contextUrl
     * @return
     */
    public List<ConfigurationMetaData> getInvokers(String contextUrl);

    /**
     * Get a component invoker configuration metadata for a specific component in a module.
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @param componentName
     * @return
     */
    public ConfigurationMetaData getComponentInvoker(String contextUrl, String moduleName, String flowName, String componentName);

    /**
     * Get a flow invoker configuration metadata for a specific flow in a module.
     *
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    public List<ConfigurationMetaData> getFlowInvokers(String contextUrl, String moduleName, String flowName);

    /**
     * Get a flow configuration metadata for a specific flow in a module.
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    public ConfigurationMetaData getFlowConfiguration(String contextUrl, String moduleName, String flowName);

    /**
     * Get a component configuration metadata for a specific component in a module and flow.
     * @param contextUrl
     * @param moduleName
     * @param flowName
     * @param componentName
     * @return
     */
    public ConfigurationMetaData getConfiguredResourceConfiguration(String contextUrl, String moduleName, String flowName, String componentName);

    /**
     * Save a configuration back to the module.
     *
     * @param contextUrl
     * @param configuration
     * @return
     */
    public boolean storeConfiguration(String contextUrl, ConfigurationMetaData configuration);

    /**
     * Delete a configuration from a module.
     *
     * @param contextUrl
     * @param configurationId
     * @return
     */
    public boolean delete(String contextUrl, String configurationId);

}
