package org.ikasan.dashboard.ui.visualisation.component;

import org.ikasan.dashboard.ui.general.component.AbstractConfigurationDialog;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.spec.module.client.ConfigurationService;

public class FlowConfigurationDialog extends AbstractConfigurationDialog
{
    private ModuleVisualisation moduleVisualisation;
    private Flow flow;

    /**
     * Constructor
     *
     * @param module
     * @param flow
     * @param configurationRestService
     */
    public FlowConfigurationDialog(Module module, Flow flow
        , ConfigurationService configurationRestService,  ModuleVisualisation moduleVisualisation)
    {
        super(module, flow.getName(), null, configurationRestService);
        this.flow = flow;
        this.moduleVisualisation = moduleVisualisation;
        super.setHeight("500px");
    }

    @Override
    protected boolean loadConfigurationMetaData()
    {
        this.configurationMetaData = this.configurationRestService
            .getFlowConfiguration(module.getUrl(), module.getName(), flowName);

        return this.configurationMetaData != null;
    }

    @Override
    protected void save() {
        super.save();

        configurationMetaData.getParameters().stream()
            .filter(configurationParameterMetaData -> configurationParameterMetaData.getName().equals("isRecording"))
            .findFirst()
            .ifPresent(configurationParameterMetaData -> flow.setRecording((Boolean) configurationParameterMetaData.getValue()));

        this.moduleVisualisation.redraw();
    }
}
