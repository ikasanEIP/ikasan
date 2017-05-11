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
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;
import org.ikasan.dashboard.ui.mappingconfiguration.window.*;
import org.ikasan.mapping.service.MappingConfigurationService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class NewActions
{
    private static final long serialVersionUID = 9150730301535584905L;

    protected ExistingMappingConfigurationPanel existingMappingConfigurationPanel;
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
    protected Label actionsLabel;
    protected Label newMappingConfigurationLabel;
    protected Label importMappingConfigurationLabel;
    protected FunctionalGroup functionalGroup;
    protected MappingConfigurationImportWindow mappingConfigurationImportWindow;
    protected IkasanUINavigator uiNavigator;
    protected NewClientWindow newClientWindow;
    protected NewMappingConfigurationContextWindow newMappingConfigurationContextWindow;
    protected NewMappingConfigurationTypeWindow newMappingConfigurationTypeWindow;
    protected MappingConfigurationService mappingConfigurationService;

    /**
     * Constructor
     * 
     * @param existingMappingConfigurationPanel
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
    public NewActions(ExistingMappingConfigurationPanel existingMappingConfigurationPanel, RefreshGroup typeRefreshGroup,
                      RefreshGroup clientRefreshGroup, RefreshGroup contextRefreshGroup, SaveRequiredMonitor saveRequiredMonitor,
                      Button newClientButton, Button newContextButton, Button newTypeButton, Button newMappingConfigurationButton,
                      Button importMappingConfigurationButton, Label newClientLabel, Label newContextLabel, Label newTypeLabel, Label actionsLabel,
                      Label newMappingConfigurationLabel, Label importMappingConfigurationLabel,
                      MappingConfigurationImportWindow mappingConfigurationImportWindow, IkasanUINavigator uiNavigator, NewClientWindow newClientWindow,
                      NewMappingConfigurationContextWindow newMappingConfigurationContextWindow, NewMappingConfigurationTypeWindow newMappingConfigurationTypeWindow)
    {
        this.existingMappingConfigurationPanel = existingMappingConfigurationPanel;
        this.typeRefreshGroup = typeRefreshGroup;
        this.clientRefreshGroup = clientRefreshGroup;
        this.contextRefreshGroup = contextRefreshGroup;
        this.saveRequiredMonitor = saveRequiredMonitor;
        this.newClientButton = newClientButton;
        this.newContextButton = newContextButton;
        this.newTypeButton = newTypeButton;
        this.actionsLabel = actionsLabel;
        this.newMappingConfigurationButton = newMappingConfigurationButton;
        this.importMappingConfigurationButton = importMappingConfigurationButton;
        this.newClientLabel = newClientLabel;
        this.newTypeLabel = newTypeLabel;
        this.newContextLabel = newContextLabel;
        this.newMappingConfigurationLabel = newMappingConfigurationLabel;
        this.importMappingConfigurationLabel = importMappingConfigurationLabel;
        this.mappingConfigurationImportWindow = mappingConfigurationImportWindow;
        this.uiNavigator = uiNavigator;
        this.newClientWindow = newClientWindow;
        this.newMappingConfigurationContextWindow = newMappingConfigurationContextWindow;
        this.newMappingConfigurationTypeWindow = newMappingConfigurationTypeWindow;
        
        init();
    }

    /**
     * Helper method to initialise the object.
     */
    @SuppressWarnings("serial")
    protected void init()
    {        
        this.newClientButton.setIcon(VaadinIcons.PLUS);
        this.newClientButton.setDescription("Create a new mapping configuration client");
        this.newClientButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.newClientButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.newClientButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event)
            {
            	UI.getCurrent().addWindow(newClientWindow);
            }
        });

        this.newContextButton.setIcon(VaadinIcons.PLUS);
        this.newContextButton.setDescription("Create a new mapping configuration context");
        this.newContextButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.newContextButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.newContextButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(ClickEvent event) 
            {
            	UI.getCurrent().addWindow(newMappingConfigurationContextWindow);
            }
        });
        
        this.newTypeButton.setIcon(VaadinIcons.PLUS);
        this.newTypeButton.setDescription("Create a new mapping configuration type");
        this.newTypeButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.newTypeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.newTypeButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	UI.getCurrent().addWindow(newMappingConfigurationTypeWindow);
            }
        });

        this.newMappingConfigurationButton.setIcon(VaadinIcons.COPY_O);
        this.newMappingConfigurationButton.setDescription("Create a new mapping configuration");
        this.newMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.newMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.newMappingConfigurationButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(ClickEvent event) 
            {
                typeRefreshGroup.refresh();
                clientRefreshGroup.refresh();
                contextRefreshGroup.refresh();

                UI.getCurrent().addWindow(new NewMappingConfigurationWindow(mappingConfigurationService
                        , null, existingMappingConfigurationPanel, uiNavigator));
            }
        });

        this.importMappingConfigurationButton.setIcon(VaadinIcons.UPLOAD_ALT);
        this.importMappingConfigurationButton.setDescription("Import a mapping configuration");
        this.importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        this.importMappingConfigurationButton.addClickListener(new Button.ClickListener() 
        {
			public void buttonClick(ClickEvent event)
			{
			    UI.getCurrent().addWindow(mappingConfigurationImportWindow);
			}
        });
    }

	/**
	 * @return the newClientButton
	 */
	public Button getNewClientButton()
	{
		return newClientButton;
	}

	/**
	 * @return the newContextButton
	 */
	public Button getNewContextButton()
	{
		return newContextButton;
	}

	/**
	 * @return the newTypeButton
	 */
	public Button getNewTypeButton()
	{
		return newTypeButton;
	}

	/**
	 * @return the newMappingConfigurationButton
	 */
	public Button getNewMappingConfigurationButton()
	{
		return newMappingConfigurationButton;
	}

	/**
	 * @return the importMappingConfigurationButton
	 */
	public Button getImportMappingConfigurationButton()
	{
		return importMappingConfigurationButton;
	}

	/**
	 * @return the newClientLabel
	 */
	public Label getNewClientLabel()
	{
		return newClientLabel;
	}

	/**
	 * @return the newContextLabel
	 */
	public Label getNewContextLabel()
	{
		return newContextLabel;
	}

	/**
	 * @return the newTypeLabel
	 */
	public Label getNewTypeLabel()
	{
		return newTypeLabel;
	}

	/**
	 * @return the newMappingConfigurationLabel
	 */
	public Label getNewMappingConfigurationLabel()
	{
		return newMappingConfigurationLabel;
	}

	/**
	 * @return the importMappingConfigurationLabel
	 */
	public Label getImportMappingConfigurationLabel()
	{
		return importMappingConfigurationLabel;
	}

	/**
	 * @return the actionsLabel
	 */
	public Label getActionsLabel()
	{
		return actionsLabel;
	}

    public void setMappingConfigurationService(MappingConfigurationService mappingConfigurationService)
    {
        this.mappingConfigurationService = mappingConfigurationService;
    }
}
