package org.ikasan.rest.configuration;

import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.rest.ConfigurationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Configuration application implementing the REST contract
 */
@Component
public class ConfigurationApplication implements ConfigurationResource
{

    @Autowired
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    @Autowired
    private ModuleService moduleService;

    @Override
    public Configuration getConfiguration(String moduleName, String flowName, String flowElementName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        FlowElement flowElement = flow.getFlowElement(flowElementName);
        if(flowElement == null)
        {
            throw new RuntimeException("FlowComponent not found for module ["
                    + moduleName + "] flow [" + flowName + "] flowElementName [" + flowElementName + "]");
        }
        Object flowComponent = flowElement.getFlowComponent();
        if (flowComponent instanceof ConfiguredResource)
        {
            return configurationManagement.getConfiguration((ConfiguredResource) flowComponent);
        }
        else
        {
            throw new UnsupportedOperationException("Component must be of type 'ConfiguredResource' to support component configuration");
        }
    }

    @Override
    public Configuration getConfigurationById(String configurationId)
    {
        //TODO
        return null;//configurationManagement.getConfiguration(configurationId);
    }

    @Override
    public void saveConfiguration(Configuration configuration)
    {
        configurationManagement.saveConfiguration(configuration);
    }

    @Override
    public void deleteConfiguration(String moduleName, String flowName, String flowElementName)
    {
        configurationManagement.deleteConfiguration(getConfiguration(moduleName, flowName, flowElementName));
    }

    @Override
    public void deleteConfigurationById(String configurationId)
    {
        // TODO
        //configurationManagement.deleteConfiguration(configurationManagement.getConfiguration(configurationId));
    }

    public ConfigurationManagement<ConfiguredResource, Configuration> getConfigurationManagement()
    {
        return configurationManagement;
    }

    public void setConfigurationManagement(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
    {
        this.configurationManagement = configurationManagement;
    }

    public ModuleService getModuleService()
    {
        return moduleService;
    }

    public void setModuleService(ModuleService moduleService)
    {
        this.moduleService = moduleService;
    }
}
