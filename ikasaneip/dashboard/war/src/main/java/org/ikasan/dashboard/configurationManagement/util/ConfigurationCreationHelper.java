package org.ikasan.dashboard.configurationManagement.util;

import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Component;

/**
 * Created by stewmi on 20/12/2016.
 */
public class ConfigurationCreationHelper
{
    ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    public ConfigurationCreationHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
    {
        this.configurationManagement = configurationManagement;
    }

    public Configuration createConfiguration(Component component)
    {
        return null;
    }
}
