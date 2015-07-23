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
package org.ikasan.dashboard.ui.topology.window;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.topology.panel.TopologyViewPanel;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorCategorisationLink;
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.topology.model.Component;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorCategorisationWindow extends Window
{
	private Logger logger = Logger.getLogger(TopologyViewPanel.class);
		
	private Component component;
	private ErrorCategorisationService errorCategorisationService;
	private ErrorCategorisation errorCategorisation;
	private ErrorCategorisationLink errorCategorisationLink;
	
	/**
	 * @param configurationManagement
	 */
	public ErrorCategorisationWindow(Component component,
			ErrorCategorisationService errorCategorisationService)
	{
		super("Error Categorisation");
		this.setIcon(VaadinIcons.EXCLAMATION_CIRCLE_O);
		
		this.component = component;
		this.errorCategorisationService = errorCategorisationService;
		
		init();
	}

	/**
     * Helper method to initialise this object.
     * 
     * @param message
     */
    protected void init()
    {   	
    	setModal(true);
		setHeight("90%");
		setWidth("90%"); 
		
		GridLayout layout = new GridLayout(2, 8);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setColumnExpandRatio(0, .25f);
		layout.setColumnExpandRatio(1, .75f);
		
		this.errorCategorisationLink = this.errorCategorisationService.find(this.component.getFlow().getModule().getName(),
				this.component.getFlow().getName(), this.component.getName());
		
		if(this.errorCategorisationLink == null)
		{	
			this.errorCategorisationLink = new ErrorCategorisationLink(this.component.getFlow().getModule().getName(),
					this.component.getFlow().getName(), this.component.getName(), "", "");
			
			this.errorCategorisation = new ErrorCategorisation("", "");
		}
		else
		{
			this.errorCategorisation = this.errorCategorisationLink.getErrorCategorisation();
		}
		
		final BeanItem<ErrorCategorisation> errorCategorisationItem = new BeanItem<ErrorCategorisation>(this.errorCategorisation);
		final BeanItem<ErrorCategorisationLink> errorCategorisationLinkItem = new BeanItem<ErrorCategorisationLink>(this.errorCategorisationLink);
    	
    	Label configuredResourceIdLabel = new Label("Error Categorisation");
		configuredResourceIdLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(configuredResourceIdLabel);

		Label moduleNameLabel = new Label();
		moduleNameLabel.setContentMode(ContentMode.HTML);
		moduleNameLabel.setValue(VaadinIcons.ARCHIVE.getHtml() + " Module Name:");
		moduleNameLabel.setSizeUndefined();		
		layout.addComponent(moduleNameLabel, 0, 1);
		layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleNameTextField = new TextField();
		moduleNameTextField.setRequired(true);
		moduleNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("moduleName"));
		moduleNameTextField.setReadOnly(true);
		moduleNameTextField.setWidth("80%");
		layout.addComponent(moduleNameTextField, 1, 1); 
		
		Label flowNameLabel = new Label();
		flowNameLabel.setContentMode(ContentMode.HTML);
		flowNameLabel.setValue(VaadinIcons.AUTOMATION.getHtml() + " Flow Name:");
		flowNameLabel.setSizeUndefined();		
		layout.addComponent(flowNameLabel, 0, 2);
		layout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField flowNameTextField = new TextField();
		flowNameTextField.setRequired(true);
		flowNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("flowName"));
		flowNameTextField.setReadOnly(true);
		flowNameTextField.setWidth("80%");
		layout.addComponent(flowNameTextField, 1, 2); 
		
		Label componentNameLabel = new Label();
		componentNameLabel.setContentMode(ContentMode.HTML);
		componentNameLabel.setValue(VaadinIcons.COG.getHtml() + " Component Name:");
		componentNameLabel.setSizeUndefined();		
		layout.addComponent(componentNameLabel, 0, 3);
		layout.setComponentAlignment(componentNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField componentNameTextField = new TextField();
		componentNameTextField.setRequired(true);
		componentNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("flowElementName"));
		componentNameTextField.setReadOnly(true);
		componentNameTextField.setWidth("80%");
		layout.addComponent(componentNameTextField, 1, 3); 
		
		Label actionLabel = new Label();
		actionLabel.setContentMode(ContentMode.HTML);
		actionLabel.setValue("Action:");
		actionLabel.setSizeUndefined();		
		layout.addComponent(actionLabel, 0, 4);
		layout.setComponentAlignment(actionLabel, Alignment.MIDDLE_RIGHT);
		
		
		final ComboBox actionCombo = new ComboBox();
		actionCombo.addItem(ErrorCategorisationLink.EXCLUDE_EVENT_ACTION);
		actionCombo.addItem(ErrorCategorisationLink.RETRY_ACTION);
		actionCombo.addItem(ErrorCategorisationLink.STOP_ACTION);
		actionCombo.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("action"));
		actionCombo.setRequired(true);
		actionCombo.setRequiredError("An action must be selected!");
		actionCombo.setValidationVisible(false);
		layout.addComponent(actionCombo, 1, 4);
		
		Label errorCategoryLabel = new Label("Error Category:");
		errorCategoryLabel.setSizeUndefined();		
		layout.addComponent(errorCategoryLabel, 0, 5);
		layout.setComponentAlignment(errorCategoryLabel, Alignment.MIDDLE_RIGHT);
		
		final ComboBox errorCategoryCombo = new ComboBox();
		errorCategoryCombo.addValidator(new StringLengthValidator(
	            "An error category must be selected!", 1, -1, false));
		errorCategoryCombo.setValidationVisible(false);
		errorCategoryCombo.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorCategory"));
		errorCategoryCombo.setRequired(true);
		errorCategoryCombo.setHeight("30px");
		errorCategoryCombo.setNullSelectionAllowed(false);
		layout.addComponent(errorCategoryCombo, 1, 5); 
		errorCategoryCombo.addItem(ErrorCategorisation.TRIVIAL);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.TRIVIAL, VaadinIcons.ARROW_DOWN);
		errorCategoryCombo.addItem(ErrorCategorisation.MAJOR);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.MAJOR, VaadinIcons.ARROW_UP);
		errorCategoryCombo.addItem(ErrorCategorisation.CRITICAL);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.CRITICAL, VaadinIcons.EXCLAMATION_CIRCLE_O);
		errorCategoryCombo.addItem(ErrorCategorisation.BLOCKER);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.BLOCKER, VaadinIcons.BAN);
		
		Label errorMessageLabel = new Label("Error Message:");
		errorMessageLabel.setSizeUndefined();		
		layout.addComponent(errorMessageLabel, 0, 6);
		layout.setComponentAlignment(errorMessageLabel, Alignment.TOP_RIGHT);
		
		final TextArea errorMessageTextArea = new TextArea();
		errorMessageTextArea.addValidator(new StringLengthValidator(
	            "You must define an error message between 1 and 1024 characters in length!", 1, 1024, false));
		errorMessageTextArea.setValidationVisible(false);
		errorMessageTextArea.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorDescription"));
		errorMessageTextArea.setRequired(true);
		errorMessageTextArea.setWidth("650px");
		errorMessageTextArea.setRows(8);
		errorMessageTextArea.setRequiredError("An error message is required!");
		layout.addComponent(errorMessageTextArea, 1, 6); 
		
		GridLayout buttonLayouts = new GridLayout(3, 1);
		buttonLayouts.setSpacing(true);
		
		Button saveButton = new Button("Save");
		saveButton.setStyleName(ValoTheme.BUTTON_SMALL);		
		saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
            	{
            		errorCategoryCombo.validate();
            		errorMessageTextArea.validate();
            		actionCombo.validate();
                } 
                catch (InvalidValueException e) 
                {
                	errorCategoryCombo.setValidationVisible(true);
                	errorMessageTextArea.setValidationVisible(true);
                	actionCombo.setValidationVisible(true);
                	
                	errorCategoryCombo.markAsDirty();
                	errorMessageTextArea.markAsDirty();
                	actionCombo.markAsDirty();
                    return;
                }
            	
            	errorCategorisationService.save(errorCategorisationItem.getBean());
            	
            	errorCategorisationLink.setErrorCategorisation(errorCategorisationItem.getBean());
            	
            	errorCategorisationService.save(errorCategorisationLink);
            	
            	Notification.show("Saved!");
            }
        });
		
		Button deleteButton = new Button("Delete");
		deleteButton.setStyleName(ValoTheme.BUTTON_SMALL);
		deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	errorCategorisationService.delete(errorCategorisationItem.getBean());
            	errorCategorisationService.delete(errorCategorisationLinkItem.getBean());
            	
            	errorCategorisation = new ErrorCategorisation(null, null);
            }
        });
		
		Button cancelButton = new Button("Cancel");
		cancelButton.setStyleName(ValoTheme.BUTTON_SMALL);
		cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	close();
            }
        });
		
		buttonLayouts.addComponent(saveButton);
		buttonLayouts.addComponent(deleteButton);
		buttonLayouts.addComponent(cancelButton);
		
		layout.addComponent(buttonLayouts, 0, 7, 1, 7);
		layout.setComponentAlignment(buttonLayouts, Alignment.MIDDLE_CENTER);
		
		Panel paramPanel = new Panel();
		paramPanel.setStyleName("dashboard");
		paramPanel.setWidth("100%");
		paramPanel.setContent(layout);
		
		GridLayout wrapper = new GridLayout();
		wrapper.setMargin(true);
		wrapper.setSizeFull();
		wrapper.addComponent(paramPanel);
		
		this.setContent(wrapper);
    }

    
}