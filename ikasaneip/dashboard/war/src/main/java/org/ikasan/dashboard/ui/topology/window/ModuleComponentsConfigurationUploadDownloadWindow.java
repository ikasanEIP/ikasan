package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.ui.Window;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Module;

/**
 * Created by stewmi on 20/12/2016.
 */
public class ModuleComponentsConfigurationUploadDownloadWindow extends Window
{
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationService;

    public ModuleComponentsConfigurationUploadDownloadWindow(ConfigurationManagement<ConfiguredResource, Configuration> configurationService)
    {
        this.configurationService = configurationService;
    }

    public void populate(Module module)
    {

    }
}
