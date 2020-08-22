package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.spec.module.client.ConfigurationService;

public class FlowOptionsDialog extends Dialog
{
    protected ConfigurationService configurationRestService;
    protected Module module;
    protected Flow flow;

    protected FlowOptionsDialog(Module module, Flow flow, ConfigurationService configurationRestService)
    {
        this.module = module;
        this.flow = flow;
        this.configurationRestService = configurationRestService;

        init();
    }

    private void init()
    {
        VerticalLayout verticalLayout = new VerticalLayout();

        Image mrSquidImage = new Image("/frontend/images/mr-squid-head.png", "");
        mrSquidImage.setHeight("35px");

        H3 flowOptions = new H3(String.format(getTranslation("label.flow-options", UI.getCurrent().getLocale())));

        HorizontalLayout header = new HorizontalLayout();
        header.add(mrSquidImage, flowOptions);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, mrSquidImage, flowOptions);

        verticalLayout.add(header);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, header);

        Button invokerConfigurationButton = new Button(getTranslation("button.flow-configuration", UI.getCurrent().getLocale()));
        invokerConfigurationButton.setWidthFull();
        invokerConfigurationButton.addClickListener((ComponentEventListener<ClickEvent<Button>>)
            buttonClickEvent -> openFlowConfigurationDialog());

        ComponentSecurityVisibility.applySecurity(invokerConfigurationButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.PLATORM_CONFIGURATON_ADMIN
            , SecurityConstants.PLATORM_CONFIGURATON_READ
            , SecurityConstants.PLATORM_CONFIGURATON_WRITE);

        verticalLayout.add(invokerConfigurationButton);

        this.add(verticalLayout);
    }

    private void openFlowConfigurationDialog()
    {
        FlowConfigurationDialog flowConfigurationDialog = new FlowConfigurationDialog(module, flow.getName(), configurationRestService);
        flowConfigurationDialog.open();
        this.close();
    }
}
