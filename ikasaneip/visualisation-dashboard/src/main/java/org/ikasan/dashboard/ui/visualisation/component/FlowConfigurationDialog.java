package org.ikasan.dashboard.ui.visualisation.component;

import org.ikasan.dashboard.ui.general.component.AbstractConfigurationDialog;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;

public class FlowConfigurationDialog extends AbstractConfigurationDialog
{
    /**
     * Constructor
     *
     * @param module
     * @param flowName
     * @param componentName
     * @param configurationRestService
     */
    public FlowConfigurationDialog(Module module, String flowName, String componentName
        , ConfigurationRestServiceImpl configurationRestService)
    {
        super(module, flowName, componentName, configurationRestService);
    }

    @Override
    protected void loadConfigurationMetaData()
    {
        this.configurationMetaData = this.configurationRestService
            .getComponentInvoker(module.getUrl(), module.getName(), flowName, componentName);
    }
}
