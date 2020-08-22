package org.ikasan.dashboard.ui.visualisation.component;

import org.ikasan.dashboard.ui.general.component.AbstractConfigurationDialog;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.spec.module.client.ConfigurationService;

public class FlowConfigurationDialog extends AbstractConfigurationDialog
{
    /**
     * Constructor
     *
     * @param module
     * @param flowName
     * @param configurationRestService
     */
    public FlowConfigurationDialog(Module module, String flowName
        , ConfigurationService configurationRestService)
    {
        super(module, flowName, null, configurationRestService);
    }

    @Override
    protected boolean loadConfigurationMetaData()
    {
        this.configurationMetaData = this.configurationRestService
            .getFlowConfiguration(module.getUrl(), module.getName(), flowName);

        return this.configurationMetaData != null;
    }
}
