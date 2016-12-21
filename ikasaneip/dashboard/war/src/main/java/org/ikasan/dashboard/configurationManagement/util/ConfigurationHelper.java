package org.ikasan.dashboard.configurationManagement.util;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stewmi on 21/12/2016.
 */
public abstract class ConfigurationHelper
{
    private Logger logger = Logger.getLogger(ConfigurationHelper.class);

    protected ConfigurationManagement<ConfiguredResource, Configuration> configurationService;

    /**
     * Constructor
     *
     * @param configurationService
     */
    public ConfigurationHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationService)
    {
        this.configurationService = configurationService;
    }

    protected List<Configuration> getFlowConfigurations(Flow flow)
    {
        List<Configuration> configurations = new ArrayList<Configuration>();

        ConfigurationCreationHelper helper = new ConfigurationCreationHelper(configurationService);

        logger.info("Getting configurations for flow: " + flow.getName() + " with " + flow.getComponents().size() + " components");

        for(org.ikasan.topology.model.Component component: flow.getComponents())
        {
            if(component.isConfigurable() && component.getConfigurationId() != null)
            {
                logger.info("Component is configurable: " + component.getName());

                Configuration configuration = configurationService
                        .getConfiguration(component.getConfigurationId());

                if(configuration == null)
                {
                    logger.info("Creating configuration for component: " + component.getName());
                    configuration = helper.createConfiguration(component);
                }

                configurations.add(configuration);
            }
        }

        return configurations;
    }
}
