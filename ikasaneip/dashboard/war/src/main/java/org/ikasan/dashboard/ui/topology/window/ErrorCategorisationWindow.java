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
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.topology.model.Component;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
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
	
	/**
	 * @param configurationManagement
	 */
	public ErrorCategorisationWindow(Component component)
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
		
		GridLayout layout = new GridLayout(2, 7);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setColumnExpandRatio(0, .25f);
		layout.setColumnExpandRatio(1, .75f);
		
		ErrorCategorisation errorCaterorision = new ErrorCategorisation(this.component.getFlow().getModule().getName(),
				this.component.getFlow().getName(), this.component.getName(), "", "");
		
		BeanItem<ErrorCategorisation> errorCategorisationItem = new BeanItem<ErrorCategorisation>(errorCaterorision);
    	
    	Label configuredResourceIdLabel = new Label("Error Categorisation");
		configuredResourceIdLabel.setStyleName("large");
		layout.addComponent(configuredResourceIdLabel);

		Label moduleNameLabel = new Label("Module Name:");
		moduleNameLabel.setSizeUndefined();		
		layout.addComponent(moduleNameLabel, 0, 1);
		layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleNameTextField = new TextField();
//		moduleNameTextField.setIcon(VaadinIcons.ARCHIVE);
		moduleNameTextField.setRequired(true);
		moduleNameTextField.setPropertyDataSource(errorCategorisationItem.getItemProperty("moduleName"));
		moduleNameTextField.setReadOnly(true);
		moduleNameTextField.setWidth("80%");
		layout.addComponent(moduleNameTextField, 1, 1); 
		
		Label flowNameLabel = new Label("Flow Name:");
		flowNameLabel.setSizeUndefined();		
		layout.addComponent(flowNameLabel, 0, 2);
		layout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField flowNameTextField = new TextField();
//		moduleNameTextField.setIcon(VaadinIcons.ARCHIVE);
		flowNameTextField.setRequired(true);
		flowNameTextField.setPropertyDataSource(errorCategorisationItem.getItemProperty("flowName"));
		flowNameTextField.setReadOnly(true);
		flowNameTextField.setWidth("80%");
		layout.addComponent(flowNameTextField, 1, 2); 
		
		Label componentNameLabel = new Label("Component Name:");
		componentNameLabel.setSizeUndefined();		
		layout.addComponent(componentNameLabel, 0, 3);
		layout.setComponentAlignment(componentNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField componentNameTextField = new TextField();
//		moduleNameTextField.setIcon(VaadinIcons.ARCHIVE);
		componentNameTextField.setRequired(true);
		componentNameTextField.setPropertyDataSource(errorCategorisationItem.getItemProperty("flowElementName"));
		componentNameTextField.setReadOnly(true);
		componentNameTextField.setWidth("80%");
		layout.addComponent(componentNameTextField, 1, 3); 
		
		Label errorCategoryLabel = new Label("Error Category:");
		errorCategoryLabel.setSizeUndefined();		
		layout.addComponent(errorCategoryLabel, 0, 4);
		layout.setComponentAlignment(errorCategoryLabel, Alignment.MIDDLE_RIGHT);
		
		final ComboBox errorCategoryCombo = new ComboBox();
		errorCategoryCombo.addValidator(new StringLengthValidator(
	            "An error category must be selected!", 1, -1, false));
		errorCategoryCombo.setValidationVisible(false);
		errorCategoryCombo.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorCategory"));
		errorCategoryCombo.setRequired(true);
		errorCategoryCombo.setHeight("30px");
		layout.addComponent(errorCategoryCombo, 1, 4); 
		errorCategoryCombo.addItem(ErrorCategorisation.TRIVIAL);
		errorCategoryCombo.addItem(ErrorCategorisation.MAJOR);
		errorCategoryCombo.addItem(ErrorCategorisation.CRITICAL);
		errorCategoryCombo.addItem(ErrorCategorisation.BLOCKER);
		
		Label errorMessageLabel = new Label("Error Message:");
		errorMessageLabel.setSizeUndefined();		
		layout.addComponent(errorMessageLabel, 0, 5);
		layout.setComponentAlignment(errorMessageLabel, Alignment.TOP_RIGHT);
		
		final TextArea errorMessageTextArea = new TextArea();
		errorMessageTextArea.addValidator(new StringLengthValidator(
	            "You must define an error message!", 1, -1, false));
		errorMessageTextArea.setValidationVisible(false);
		errorMessageTextArea.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorDescription"));
		errorMessageTextArea.setRequired(true);
		errorMessageTextArea.setWidth("650px");
		errorMessageTextArea.setRows(8);
		layout.addComponent(errorMessageTextArea, 1, 5); 
		
		GridLayout buttonLayouts = new GridLayout(2, 1);
		buttonLayouts.setSpacing(true);
		
		Button saveButton = new Button("Save");
		saveButton.setStyleName(ValoTheme.BUTTON_SMALL);		
		saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
//            		errorCategoryCombo.validate();
            		errorMessageTextArea.validate();
                } 
                catch (InvalidValueException e) 
                {
//                	errorCategoryCombo.setValidationVisible(true);
                	errorMessageTextArea.setValidationVisible(true);
                    return;
                }
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
		buttonLayouts.addComponent(cancelButton);
		
		layout.addComponent(buttonLayouts, 0, 6, 1, 6);
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