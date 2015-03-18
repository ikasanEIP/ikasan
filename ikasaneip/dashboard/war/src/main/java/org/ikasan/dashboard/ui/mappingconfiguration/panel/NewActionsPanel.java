/*
 * $Id: NewActionsPanel.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/NewActionsPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.mappingconfiguration.window.MappingConfigurationImportWindow;

import com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author CMI2 Development Team
 *
 */
public class NewActionsPanel extends Panel
{
    private static final long serialVersionUID = 9150730301535584905L;

    protected NewMappingConfigurationPanel newMappingConfigurationPanel;
    protected RefreshGroup typeRefreshGroup;
    protected RefreshGroup clientRefreshGroup;
    protected RefreshGroup contextRefreshGroup;
    protected SaveRequiredMonitor saveRequiredMonitor;
    protected Button newClientButton;
    protected Button newContextButton;
    protected Button newTypeButton;
    protected Button newMappingConfigurationButton;
    protected Button importMappingConfigurationButton;
    protected Label newClientLabel;
    protected Label newContextLabel;
    protected Label newTypeLabel;
    protected Label newMappingConfigurationLabel;
    protected Label importMappingConfigurationLabel;
    protected FunctionalGroup functionalGroup;
    protected MappingConfigurationImportWindow mappingConfigurationImportWindow;

    /**
     * Constructor
     * 
     * @param newMappingConfigurationPanel
     * @param typeRefreshGroup
     * @param clientRefreshGroup
     * @param contextRefreshGroup
     * @param saveRequiredMonitor
     * @param newClientButton
     * @param newContextButton
     * @param newTypeButton
     * @param newMappingConfigurationButton
     * @param newClientLabel
     * @param newContextLabel
     * @param newTypeLabel
     * @param newMappingConfigurationLabel
     */
    public NewActionsPanel(NewMappingConfigurationPanel newMappingConfigurationPanel, RefreshGroup typeRefreshGroup,
            RefreshGroup clientRefreshGroup, RefreshGroup contextRefreshGroup, SaveRequiredMonitor saveRequiredMonitor,
            Button newClientButton, Button newContextButton, Button newTypeButton, Button newMappingConfigurationButton,
            Button importMappingConfigurationButton, Label newClientLabel, Label newContextLabel, Label newTypeLabel, 
            Label newMappingConfigurationLabel, Label importMappingConfigurationLabel,
            MappingConfigurationImportWindow mappingConfigurationImportWindow)
    {
        this.newMappingConfigurationPanel = newMappingConfigurationPanel;
        this.typeRefreshGroup = typeRefreshGroup;
        this.clientRefreshGroup = clientRefreshGroup;
        this.contextRefreshGroup = contextRefreshGroup;
        this.saveRequiredMonitor = saveRequiredMonitor;
        this.newClientButton = newClientButton;
        this.newContextButton = newContextButton;
        this.newTypeButton = newTypeButton;
        this.newMappingConfigurationButton = newMappingConfigurationButton;
        this.importMappingConfigurationButton = importMappingConfigurationButton;
        this.newClientLabel = newClientLabel;
        this.newTypeLabel = newTypeLabel;
        this.newContextLabel = newContextLabel;
        this.newMappingConfigurationLabel = newMappingConfigurationLabel;
        this.importMappingConfigurationLabel = importMappingConfigurationLabel;
        this.mappingConfigurationImportWindow = mappingConfigurationImportWindow;

        this.init();
    }

    /**
     * Helper method to initialise the object.
     */
    @SuppressWarnings("serial")
    protected void init()
    {
        this.setStyleName("grey");

        // Set up the grid layout that is the container for the 
        // components.
        final GridLayout contentLayout = new GridLayout(2, 6);
        contentLayout.setColumnExpandRatio(0, 0.7f);
        contentLayout.setColumnExpandRatio(1, 0.3f);
        contentLayout.setRowExpandRatio(0, 1);
        contentLayout.setRowExpandRatio(1, 1);
        contentLayout.setRowExpandRatio(2, 1);
        contentLayout.setRowExpandRatio(3, 1);
        contentLayout.setRowExpandRatio(4, 1);
        contentLayout.setRowExpandRatio(5, 1);
        
        contentLayout.setWidth(300, Unit.PIXELS);
        contentLayout.setHeight("100%");
        contentLayout.setMargin(true);

        // Add the new client label and button.
        HorizontalLayout clientLabelLayout = new HorizontalLayout();
        clientLabelLayout.setHeight(20, Unit.PIXELS);
        clientLabelLayout.setWidth(180, Unit.PIXELS);
        clientLabelLayout.addComponent(this.newClientLabel);
        contentLayout.addComponent(clientLabelLayout, 0, 0);
        
        this.newClientButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.newClientButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                saveRequiredMonitor.manageSaveRequired("newClientPanel");
            }
        });
        contentLayout.addComponent(this.newClientButton, 1, 0);

        // Add the new context label and button
        HorizontalLayout contextLabelLayout = new HorizontalLayout();
        contextLabelLayout.setHeight(20, Unit.PIXELS);
        contextLabelLayout.setWidth(180, Unit.PIXELS);
        contextLabelLayout.addComponent(this.newContextLabel);
        contentLayout.addComponent(contextLabelLayout, 0, 1);

        this.newContextButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.newContextButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                saveRequiredMonitor.manageSaveRequired("newContextPanel");
            }
        });
        contentLayout.addComponent(this.newContextButton, 1, 1);

        // Add the new type label and button.
        HorizontalLayout typeLabelLayout = new HorizontalLayout();
        typeLabelLayout.setHeight(20, Unit.PIXELS);
        typeLabelLayout.setWidth(180, Unit.PIXELS);
        typeLabelLayout.addComponent(this.newTypeLabel);
        contentLayout.addComponent(typeLabelLayout, 0, 2);

        this.newTypeButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.newTypeButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                saveRequiredMonitor.manageSaveRequired("newConfigurationTypePanel");
            }
        });
        contentLayout.addComponent(this.newTypeButton, 1, 2);

        
        // Add the new type label and button.
        HorizontalLayout configurationLabelLayout = new HorizontalLayout();
        configurationLabelLayout.setHeight(20, Unit.PIXELS);
        configurationLabelLayout.setWidth(180, Unit.PIXELS);
        configurationLabelLayout.addComponent(this.newMappingConfigurationLabel);
        contentLayout.addComponent(configurationLabelLayout, 0, 3);

        this.newMappingConfigurationButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.newMappingConfigurationButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                newMappingConfigurationPanel.init();
                saveRequiredMonitor.manageSaveRequired("newMappingConfigurationPanel");
                newMappingConfigurationPanel.setEditable(true);
                typeRefreshGroup.refresh();
                clientRefreshGroup.refresh();
                contextRefreshGroup.refresh();
            }
        });
        contentLayout.addComponent(this.newMappingConfigurationButton, 1, 3);

//      Add the import label and button.
        HorizontalLayout importMappingConfigurationLayout = new HorizontalLayout();
        importMappingConfigurationLayout.setHeight(20, Unit.PIXELS);
        importMappingConfigurationLayout.setWidth(180, Unit.PIXELS);
        importMappingConfigurationLayout.addComponent(this.importMappingConfigurationLabel);
        contentLayout.addComponent(importMappingConfigurationLayout, 0, 4);

        this.importMappingConfigurationButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.importMappingConfigurationButton.addClickListener(new Button.ClickListener() {
          public void buttonClick(ClickEvent event) {
              UI.getCurrent().addWindow(mappingConfigurationImportWindow);
          }
        });
        contentLayout.addComponent(this.importMappingConfigurationButton, 1, 4);

        // Add the new type label and button.
        HorizontalLayout estateViewLayout = new HorizontalLayout();
        estateViewLayout.setHeight(20, Unit.PIXELS);
        estateViewLayout.setWidth(180, Unit.PIXELS);
        estateViewLayout.addComponent(new Label("View Estate"));
        contentLayout.addComponent(estateViewLayout, 0, 5);

        Button estateViewButton = new Button("View");
        estateViewButton.setStyleName(BaseTheme.BUTTON_LINK);
        estateViewButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                saveRequiredMonitor.manageSaveRequired("estateViewPanel");
            }
        });
        contentLayout.addComponent(estateViewButton, 1, 5);

        this.setContent(contentLayout);
    }
}
