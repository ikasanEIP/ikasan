package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;

public class ComponentNodeActionDialog extends Dialog
{
    protected ConfigurationRestServiceImpl configurationRestService;
    protected Module module;
    protected String flowName;
    protected String componentName;
    protected boolean configuredResource;

    protected ComponentNodeActionDialog(Module module, String flowName, String componentName, boolean configuredResource,
        ConfigurationRestServiceImpl configurationRestService)
    {
        this.module = module;
        this.flowName = flowName;
        this.componentName = componentName;
        this.configurationRestService = configurationRestService;
        this.configuredResource = configuredResource;

        init();
    }

    private void init()
    {
        VerticalLayout verticalLayout = new VerticalLayout();

        if(this.configuredResource)
        {
            Button componentConfiguration = new Button("Component Configuration");
            componentConfiguration.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                ComponentConfigurationDialog componentConfigurationDialog = new ComponentConfigurationDialog(module, flowName,
                    componentName, configurationRestService);

                this.close();
                componentConfigurationDialog.open();
            });

            verticalLayout.add(componentConfiguration);
        }

        Button invokerConfiguration = new Button("Invoker Configuration");
        invokerConfiguration.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            InvokerConfigurationDialog componentConfigurationDialog = new InvokerConfigurationDialog(module, flowName,
                componentName, configurationRestService);

            this.close();
            componentConfigurationDialog.open();
        });

        verticalLayout.add(invokerConfiguration);

        this.add(verticalLayout);
    }
}
