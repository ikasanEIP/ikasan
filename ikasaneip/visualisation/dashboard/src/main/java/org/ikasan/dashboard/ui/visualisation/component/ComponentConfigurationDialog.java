package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.UI;
import org.ikasan.dashboard.ui.general.component.AbstractConfigurationDialog;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.spec.module.client.ConfigurationService;

public class ComponentConfigurationDialog extends AbstractConfigurationDialog
{
    /**
     * Constructor
     *
     * @param module
     * @param flowName
     * @param componentName
     * @param configurationRestService
     */
    public ComponentConfigurationDialog(Module module, String flowName, String componentName
        , ConfigurationService configurationRestService)
    {
        super(module, flowName, componentName, configurationRestService);
        super.setWidth("60vw");
        super.setHeight("80vh");
    }

    @Override
    protected boolean loadConfigurationMetaData()
    {
        this.configurationMetaData = this.configurationRestService
            .getConfiguredResourceConfiguration(module.getUrl(), module.getName(), flowName, componentName);

        super.title.setText(getTranslation("button.component-configuration", UI.getCurrent().getLocale())
            + " - " + this.configurationMetaData.getConfigurationId());

        return this.configurationMetaData != null;
    }
}
