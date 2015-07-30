 /*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.mappingconfiguration.window.MappingConfigurationImportWindow;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
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
        this.addStyleName(ValoTheme.PANEL_BORDERLESS);

        // Set up the grid layout that is the container for the 
        // components.
        final GridLayout contentLayout = new GridLayout(2, 6);
        contentLayout.setColumnExpandRatio(0, 0.7f);
        contentLayout.setColumnExpandRatio(1, 0.3f);
        
        contentLayout.setWidth(300, Unit.PIXELS);
        contentLayout.setHeight("100%");
        contentLayout.setSpacing(true);

        // Add the new client label and button.
        this.newClientLabel.setSizeUndefined();
        contentLayout.addComponent(this.newClientLabel, 0, 0);
        contentLayout.setComponentAlignment(this.newClientLabel, Alignment.MIDDLE_RIGHT);
        
        this.newClientButton.setIcon(VaadinIcons.PLUS);
        this.newClientButton.setDescription("Create a new mapping configuration client");
        this.newClientButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.newClientButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.newClientButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event)
            {
                saveRequiredMonitor.manageSaveRequired("newClientPanel");
            }
        });
        contentLayout.addComponent(this.newClientButton, 1, 0);

        // Add the new context label and button        
        this.newContextLabel.setSizeUndefined();        
        contentLayout.addComponent( this.newContextLabel, 0, 1);
        contentLayout.setComponentAlignment(this.newContextLabel, Alignment.MIDDLE_RIGHT);

        this.newContextButton.setIcon(VaadinIcons.PLUS);
        this.newContextButton.setDescription("Create a new mapping configuration context");
        this.newContextButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.newContextButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.newContextButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(ClickEvent event) 
            {
                saveRequiredMonitor.manageSaveRequired("newContextPanel");
            }
        });
        contentLayout.addComponent(this.newContextButton, 1, 1);

        // Add the new type label and button.
        this.newTypeLabel.setSizeUndefined();
        contentLayout.addComponent(this.newTypeLabel, 0, 2);
        contentLayout.setComponentAlignment(this.newTypeLabel, Alignment.MIDDLE_RIGHT);
        
        this.newTypeButton.setIcon(VaadinIcons.PLUS);
        this.newTypeButton.setDescription("Create a new mapping configuration type");
        this.newTypeButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.newTypeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.newTypeButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                saveRequiredMonitor.manageSaveRequired("newConfigurationTypePanel");
            }
        });
        contentLayout.addComponent(this.newTypeButton, 1, 2);

        
        // Add the new type label and button.
        this.newMappingConfigurationLabel.setSizeUndefined();
        contentLayout.addComponent(this.newMappingConfigurationLabel, 0, 3);
        contentLayout.setComponentAlignment(this.newMappingConfigurationLabel, Alignment.MIDDLE_RIGHT);


        this.newMappingConfigurationButton.setIcon(VaadinIcons.PLUS);
        this.newMappingConfigurationButton.setDescription("Create a new mapping configuration");
        this.newMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.newMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
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


        this.importMappingConfigurationLabel.setSizeUndefined();
        contentLayout.addComponent(importMappingConfigurationLabel, 0, 4);
        contentLayout.setComponentAlignment(this.importMappingConfigurationLabel, Alignment.MIDDLE_RIGHT);

        this.importMappingConfigurationButton.setIcon(VaadinIcons.UPLOAD_ALT);
        this.importMappingConfigurationButton.setDescription("Import a mapping configuration");
        this.importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.importMappingConfigurationButton.addClickListener(new Button.ClickListener() {
          public void buttonClick(ClickEvent event) {
              UI.getCurrent().addWindow(mappingConfigurationImportWindow);
          }
        });
        contentLayout.addComponent(this.importMappingConfigurationButton, 1, 4);

        this.setContent(contentLayout);
    }
}
