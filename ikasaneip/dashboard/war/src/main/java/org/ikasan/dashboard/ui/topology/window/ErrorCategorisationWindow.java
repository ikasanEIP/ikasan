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

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorCategorisationLink;
import org.ikasan.error.reporting.service.ErrorCategorisationService;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
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
	private Logger logger = Logger.getLogger(ErrorCategorisationWindow.class);
	
	private Server server;
	private Module module;
	private Flow flow;
	private Component component;
	private ErrorCategorisationService errorCategorisationService;
	private ErrorCategorisation errorCategorisation;
	private ErrorCategorisationLink errorCategorisationLink;
	
	private Table existingCategorisedErrorsTable;
	
	private TextArea errorMessageTextArea = new TextArea();
	private ComboBox actionCombo = new ComboBox();
	private ComboBox errorCategoryCombo = new ComboBox();
	private TextField componentNameTextField = new TextField();
	private TextField flowNameTextField = new TextField();
	private TextField moduleNameTextField = new TextField();
	private TextField exceptionClassTextField = new TextField();
	
	private BeanItem<ErrorCategorisation> errorCategorisationItem;
	private BeanItem<ErrorCategorisationLink> errorCategorisationLinkItem;
	
	private GridLayout layout = new GridLayout(2, 14);
	
	/**
	 * @param configurationManagement
	 */
	public ErrorCategorisationWindow(Server server, Module module, Flow flow, Component component,
			ErrorCategorisationService errorCategorisationService)
	{
//		super("Error Categorisation");
//		this.setIcon(VaadinIcons.EXCLAMATION_CIRCLE_O);
		
		this.server = server;
		this.module = module;
		this.flow = flow;
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
    	this.setModal(true);
    	this.setHeight("90%");
		this.setWidth("90%"); 
		this.setResizable(false);
		
		this.existingCategorisedErrorsTable = new Table();
		this.existingCategorisedErrorsTable.setWidth("100%");
		this.existingCategorisedErrorsTable.setHeight(200, Unit.PIXELS);
		this.existingCategorisedErrorsTable.addContainerProperty("Module Name", String.class,  null);
		this.existingCategorisedErrorsTable.setColumnExpandRatio("Module Name", .1f);
		this.existingCategorisedErrorsTable.addContainerProperty("Flow Name", String.class,  null);
		this.existingCategorisedErrorsTable.setColumnExpandRatio("Flow Name", .1f);
		this.existingCategorisedErrorsTable.addContainerProperty("Component Name", String.class,  null);
		this.existingCategorisedErrorsTable.setColumnExpandRatio("Component Name", .1f);
		this.existingCategorisedErrorsTable.addContainerProperty("Action", String.class,  null);
		this.existingCategorisedErrorsTable.setColumnExpandRatio("Action", .1f);
		this.existingCategorisedErrorsTable.addContainerProperty("Error Category", Label.class,  null);
		this.existingCategorisedErrorsTable.setColumnExpandRatio("Error Category", .1f);
		this.existingCategorisedErrorsTable.addContainerProperty("Error Message", String.class,  null);
		this.existingCategorisedErrorsTable.setColumnExpandRatio("Error Message", .5f);
		
		this.existingCategorisedErrorsTable.addStyleName("wordwrap-table");
		this.existingCategorisedErrorsTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
		
		this.existingCategorisedErrorsTable.setCellStyleGenerator(new Table.CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				
				ErrorCategorisationLink errorCategorisationLink = (ErrorCategorisationLink)itemId;
				
				if (propertyId == null) {
				// Styling for row			
					
					if(errorCategorisationLink.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.TRIVIAL))
					{
						return "ikasan-green-small";
					}
					else if(errorCategorisationLink.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.MAJOR))
					{
						return "ikasan-green-small";
					}
					else if(errorCategorisationLink.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.CRITICAL))
					{
						return "ikasan-orange-small";
					}
					else if(errorCategorisationLink.getErrorCategorisation()
							.getErrorCategory().equals(ErrorCategorisation.BLOCKER))
					{
						return "ikasan-red-small";
					}
				}
				
				if(errorCategorisationLink.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.TRIVIAL))
				{
					return "ikasan-green-small";
				}
				else if(errorCategorisationLink.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.MAJOR))
				{
					return "ikasan-green-small";
				}
				else if(errorCategorisationLink.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.CRITICAL))
				{
					return "ikasan-orange-small";
				}
				else if(errorCategorisationLink.getErrorCategorisation()
						.getErrorCategory().equals(ErrorCategorisation.BLOCKER))
				{
					return "ikasan-red-small";
				}
				
				return "ikasan-small";
			}
		});
		
		this.existingCategorisedErrorsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	logger.debug("table item selected: " + (ErrorCategorisationLink)itemClickEvent.getItemId());
		    	
		    	errorCategorisationLink = (ErrorCategorisationLink)itemClickEvent.getItemId();
		    	errorCategorisation = errorCategorisationLink.getErrorCategorisation();
		    	
		    	errorCategorisationItem = new BeanItem<ErrorCategorisation>(errorCategorisation);
				errorCategorisationLinkItem = new BeanItem<ErrorCategorisationLink>(errorCategorisationLink);
				
				moduleNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("moduleName"));
				flowNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("flowName"));
				componentNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("flowElementName"));
				errorCategoryCombo.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorCategory"));
				errorMessageTextArea.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorDescription"));
				actionCombo.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("action"));
				exceptionClassTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("exceptionClass"));
		    	
		    	errorMessageTextArea.markAsDirty();
		    	actionCombo.markAsDirty();
		    	errorCategoryCombo.markAsDirty();
		    	componentNameTextField.markAsDirty();
		    	flowNameTextField.markAsDirty();
		    	moduleNameTextField.markAsDirty();
		    }
		});
		
		refreshExistingCategorisedErrorsTable();
		
		layout.setWidth("100%");
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setColumnExpandRatio(0, .25f);
		layout.setColumnExpandRatio(1, .75f);
		
		if(this.errorCategorisationLink == null)
		{							
			clear();
		}
    	
    	Label configuredResourceIdLabel = new Label("Error Categorisation");
		configuredResourceIdLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(configuredResourceIdLabel, 0, 0, 1, 0);
		
		if(this.module == null && this.flow == null && this.component == null)
		{
			Label errorCategorisationHintLabel = new Label();
			errorCategorisationHintLabel.setCaptionAsHtml(true);
			errorCategorisationHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
					" You are creating an error categorisation for server wide errors. This categorisation will be applied" +
					" against errors that occur server wide, that do not have a more focused error categorisation.");
			errorCategorisationHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
			errorCategorisationHintLabel.addStyleName(ValoTheme.LABEL_SMALL);
			
			layout.addComponent(errorCategorisationHintLabel, 0, 1, 1, 1);
		}
		else if(this.flow == null && this.component == null)
		{
			Label errorCategorisationHintLabel = new Label();
			errorCategorisationHintLabel.setCaptionAsHtml(true);
			errorCategorisationHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
					" You are creating an error categorisation for module wide errors. This categorisation will be applied" +
					" against errors that occur within this module, that do not have a more focused error categorisation.");
			errorCategorisationHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
			errorCategorisationHintLabel.addStyleName(ValoTheme.LABEL_SMALL);
			
			layout.addComponent(errorCategorisationHintLabel, 0, 1, 1, 1);
		}
		else if(this.component == null)
		{
			Label errorCategorisationHintLabel = new Label();
			errorCategorisationHintLabel.setCaptionAsHtml(true);
			errorCategorisationHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
					" You are creating an error categorisation for flow wide errors. This categorisation will be applied" +
					" against errors that occur within this flow, that do not have a more focused error categorisation.");
			errorCategorisationHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
			errorCategorisationHintLabel.addStyleName(ValoTheme.LABEL_SMALL);
			
			layout.addComponent(errorCategorisationHintLabel, 0, 1, 1, 1);
		}
		else
		{
			Label errorCategorisationHintLabel = new Label();
			errorCategorisationHintLabel.setCaptionAsHtml(true);
			errorCategorisationHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
					" You are creating an error categorisation against a component. This is the most focused error categorisation" +
					" that can be applied. This categorisation will be applied against errors that occur on this component.");
			errorCategorisationHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
			errorCategorisationHintLabel.addStyleName(ValoTheme.LABEL_SMALL);
			
			layout.addComponent(errorCategorisationHintLabel, 0, 1, 1, 1);
		}

		if(this.module != null)
		{
			Label moduleNameLabel = new Label();
			moduleNameLabel.setContentMode(ContentMode.HTML);
			moduleNameLabel.setValue(VaadinIcons.ARCHIVE.getHtml() + " Module Name:");
			moduleNameLabel.setSizeUndefined();		
			layout.addComponent(moduleNameLabel, 0, 2);
			layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);
			
			moduleNameTextField.setRequired(true);
			moduleNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("moduleName"));
			moduleNameTextField.setReadOnly(true);
			moduleNameTextField.setWidth("80%");
			layout.addComponent(moduleNameTextField, 1, 2); 
		}
		
		if(this.flow != null)
		{
			Label flowNameLabel = new Label();
			flowNameLabel.setContentMode(ContentMode.HTML);
			flowNameLabel.setValue(VaadinIcons.AUTOMATION.getHtml() + " Flow Name:");
			flowNameLabel.setSizeUndefined();		
			layout.addComponent(flowNameLabel, 0, 3);
			layout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);
			
			flowNameTextField.setRequired(true);
			flowNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("flowName"));
			flowNameTextField.setReadOnly(true);
			flowNameTextField.setWidth("80%");
			layout.addComponent(flowNameTextField, 1, 3); 
		}
		
		if(this.component != null)
		{
			Label componentNameLabel = new Label();
			componentNameLabel.setContentMode(ContentMode.HTML);
			componentNameLabel.setValue(VaadinIcons.COG.getHtml() + " Component Name:");
			componentNameLabel.setSizeUndefined();		
			layout.addComponent(componentNameLabel, 0, 4);
			layout.setComponentAlignment(componentNameLabel, Alignment.MIDDLE_RIGHT);
			
			componentNameTextField.setRequired(true);
			componentNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("flowElementName"));
			componentNameTextField.setReadOnly(true);
			componentNameTextField.setWidth("80%");
			layout.addComponent(componentNameTextField, 1, 4); 
		}
		
		Label exceptionClassLabel = new Label();
		exceptionClassLabel.setContentMode(ContentMode.HTML);
		exceptionClassLabel.setValue("Exception Class:");
		exceptionClassLabel.setSizeUndefined();		
		layout.addComponent(exceptionClassLabel, 0, 5);
		layout.setComponentAlignment(exceptionClassLabel, Alignment.MIDDLE_RIGHT);
		
		this.exceptionClassTextField.setWidth("80%");
		exceptionClassTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("exceptionClass"));
		layout.addComponent(exceptionClassTextField, 1, 5); 
		
		Label actionLabel = new Label();
		actionLabel.setContentMode(ContentMode.HTML);
		actionLabel.setValue("Action:");
		actionLabel.setSizeUndefined();		
		layout.addComponent(actionLabel, 0, 6);
		layout.setComponentAlignment(actionLabel, Alignment.MIDDLE_RIGHT);

		
		Label errorCategoryLabel = new Label("Error Category:");
		errorCategoryLabel.setSizeUndefined();		
		layout.addComponent(errorCategoryLabel, 0, 7);
		layout.setComponentAlignment(errorCategoryLabel, Alignment.MIDDLE_RIGHT);
		
		this.setupComboBoxesAndItems();

		
		Label errorMessageLabel = new Label("Error Message:");
		errorMessageLabel.setSizeUndefined();		
		layout.addComponent(errorMessageLabel, 0, 8);
		layout.setComponentAlignment(errorMessageLabel, Alignment.TOP_RIGHT);
		
		
		errorMessageTextArea.addValidator(new StringLengthValidator(
	            "You must define an error message between 1 and 2048 characters in length!", 1, 2048, false));
		errorMessageTextArea.setValidationVisible(false);
		errorMessageTextArea.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorDescription"));
		errorMessageTextArea.setRequired(true);
		errorMessageTextArea.setWidth("650px");
		errorMessageTextArea.setRows(8);
		errorMessageTextArea.setRequiredError("An error message is required!");
		layout.addComponent(errorMessageTextArea, 1, 8); 
		
		GridLayout buttonLayouts = new GridLayout(4, 1);
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
//            		actionCombo.validate();
                } 
                catch (InvalidValueException e) 
                {
                	errorCategoryCombo.setValidationVisible(true);
                	errorMessageTextArea.setValidationVisible(true);
//                	actionCombo.setValidationVisible(true);
                	
                	errorCategoryCombo.markAsDirty();
                	errorMessageTextArea.markAsDirty();
                	actionCombo.markAsDirty();
                    return;
                }
            	
            	try
            	{
            		errorCategorisationService.save(errorCategorisationItem.getBean());
            		
            		errorCategorisationLink.setErrorCategorisation(errorCategorisationItem.getBean());
                	
                	errorCategorisationService.save(errorCategorisationLink);
            	}            	
            	catch(Exception e)
            	{
            		if(e.getCause() instanceof ConstraintViolationException)
            		{
            			Notification.show("An error occurred trying to save an error categorisation: Action type must be unique for a given node!"
                				, Type.ERROR_MESSAGE);
            		}
            		else
            		{
            			Notification.show("An error occurred trying to save an error categorisation: " + e.getMessage(), Type.ERROR_MESSAGE);
            		}
            	}
            	
            	refreshExistingCategorisedErrorsTable();
            	
            	Notification.show("Saved!");
            }
        });
		
		Button clearButton = new Button("Clear");
		clearButton.setStyleName(ValoTheme.BUTTON_SMALL);
		clearButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {           	
            	clear();
            }
        });
		
		Button deleteButton = new Button("Delete");
		deleteButton.setStyleName(ValoTheme.BUTTON_SMALL);
		deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	ErrorCategorisation ec = errorCategorisationLink.getErrorCategorisation();
            	
            	errorCategorisationService.delete(errorCategorisationLink);
            	errorCategorisationService.delete(ec);
            	existingCategorisedErrorsTable.removeItem(errorCategorisationLink);
            	
            	clear();
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
		buttonLayouts.addComponent(clearButton);
		buttonLayouts.addComponent(deleteButton);
		buttonLayouts.addComponent(cancelButton);
		
		layout.addComponent(buttonLayouts, 0, 9, 1, 9);
		layout.setComponentAlignment(buttonLayouts, Alignment.MIDDLE_CENTER);
		
		
		Label existingCategorisationLabel = new Label("Existing Error Categorisations");
		existingCategorisationLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(existingCategorisationLabel, 0, 10, 1, 10);
		
		Label uniquenessHintLabel = new Label();
		uniquenessHintLabel.setCaptionAsHtml(true);
		uniquenessHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" You can only create one error categorisation per Action type for a give node. If you attempt to create more you will receive an error when" +
				" trying to save.");
		uniquenessHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		uniquenessHintLabel.addStyleName(ValoTheme.LABEL_SMALL);
		layout.addComponent(uniquenessHintLabel, 0, 11, 1, 11);
		
		Label editHintLabel = new Label();
		editHintLabel.setCaptionAsHtml(true);
		editHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" You can can click on a row in the table below to edit an error categorisation.");
		editHintLabel.addStyleName(ValoTheme.LABEL_BOLD);
		editHintLabel.addStyleName(ValoTheme.LABEL_SMALL);
		layout.addComponent(editHintLabel, 0, 12, 1, 12);
	
		layout.addComponent(this.existingCategorisedErrorsTable, 0, 13, 1, 13);
		layout.setComponentAlignment(this.existingCategorisedErrorsTable, Alignment.MIDDLE_CENTER);
		
		

		this.setContent(layout);
    }

    protected void refreshExistingCategorisedErrorsTable()
    {
    	this.existingCategorisedErrorsTable.removeAllItems();
    	
    	List<ErrorCategorisationLink> categorisedErrors = null;
    	
    	if(this.component != null)
    	{
			categorisedErrors = this.errorCategorisationService.find(this.component.getFlow().getModule().getName(),
					this.component.getFlow().getName(), this.component.getName());
		}
		else if(this.flow != null)
		{		
			categorisedErrors = this.errorCategorisationService.find(this.flow.getModule().getName(),
					this.flow.getName(), "");
		}
		else if(this.module != null)
		{			
			categorisedErrors = this.errorCategorisationService.find(this.module.getName(),
					"", "");
		}
		else if(this.server != null)
		{
			categorisedErrors = this.errorCategorisationService.find("",
					"", "");
		}
    	
    	for(final ErrorCategorisationLink errorCategorisationLink: categorisedErrors)
    	{
    		Label errorCategory = new Label(errorCategorisationLink.getErrorCategorisation().getErrorCategory());
    		
    		String moduleName = errorCategorisationLink.getModuleName();
    		
    		if(moduleName == null || moduleName.trim().length() == 0)
    		{
    			moduleName = "All";
    		}
    		
    		String flowName = errorCategorisationLink.getFlowName();
    		
    		if(flowName == null || flowName.trim().length() == 0)
    		{
    			flowName = "All";
    		}
    		
    		String componentName = errorCategorisationLink.getFlowElementName();
    		
    		if(componentName == null || componentName.trim().length() == 0)
    		{
    			componentName = "All";
    		}
    		
    		String action = errorCategorisationLink.getAction();
    		
    		if(action == null || action.trim().length() == 0)
    		{
    			action = "All";
    		}
    		    		
    		this.existingCategorisedErrorsTable.addItem(new Object[]{moduleName, flowName
     				, componentName, action, errorCategory, errorCategorisationLink.getErrorCategorisation()
     				.getErrorDescription()}, errorCategorisationLink);
    	}
    }
    
    protected void clear()
    {
    	if(this.component != null)
		{
			this.errorCategorisationLink = new ErrorCategorisationLink(this.component.getFlow().getModule().getName(),
					this.component.getFlow().getName(), this.component.getName(), "", "");
		}
		else if(this.flow != null)
		{
			this.errorCategorisationLink = new ErrorCategorisationLink(this.flow.getModule().getName(),
					this.flow.getName(), "", "", "");
		}
		else if(this.module != null)
		{
			this.errorCategorisationLink = new ErrorCategorisationLink(this.module.getName(),
					"", "", "", "");
		}
		else if(this.server != null)
		{
			this.errorCategorisationLink = new ErrorCategorisationLink("",
					"", "", "", "");
		}
		
		this.errorCategorisation = new ErrorCategorisation("", "");
		
    	errorCategorisationItem = new BeanItem<ErrorCategorisation>(errorCategorisation);
		errorCategorisationLinkItem = new BeanItem<ErrorCategorisationLink>(errorCategorisationLink);
		
		errorCategoryCombo.setItemIcon(errorCategoryCombo.getValue(), null);
		errorCategoryCombo.removeAllItems();
		actionCombo.removeAllItems();
		
		setupComboBoxesAndItems();
		
		moduleNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("moduleName"));
		flowNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("flowName"));
		componentNameTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("flowElementName"));
		errorMessageTextArea.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorDescription"));
		errorCategoryCombo.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorCategory"));
		actionCombo.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("action"));
		exceptionClassTextField.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("exceptionClass"));
		
		errorMessageTextArea.markAsDirty();
    	actionCombo.markAsDirty();
    	errorCategoryCombo.markAsDirty();
    	componentNameTextField.markAsDirty();
    	flowNameTextField.markAsDirty();
    	moduleNameTextField.markAsDirty();
    }
    
    protected void setupComboBoxesAndItems()
    {
    	layout.removeComponent(actionCombo);
    	layout.removeComponent(errorCategoryCombo);
    	actionCombo = new ComboBox();
    	actionCombo.setPropertyDataSource(errorCategorisationLinkItem.getItemProperty("action"));
		actionCombo.setRequired(false);
		actionCombo.setRequiredError("An action must be selected!");
		actionCombo.setValidationVisible(false);
		actionCombo.setNullSelectionAllowed(true);
		actionCombo.setHeight("30px");
		
		errorCategoryCombo = new ComboBox();
		errorCategoryCombo.addValidator(new StringLengthValidator(
	            "An error category must be selected!", 1, -1, false));
		errorCategoryCombo.setValidationVisible(false);
		errorCategoryCombo.setPropertyDataSource(errorCategorisationItem.getItemProperty("errorCategory"));
		errorCategoryCombo.setRequired(true);
		errorCategoryCombo.setHeight("30px");
		errorCategoryCombo.setNullSelectionAllowed(false); 
		
    	actionCombo.addItem(ErrorCategorisationLink.EXCLUDE_EVENT_ACTION);
		actionCombo.addItem(ErrorCategorisationLink.RETRY_ACTION);
		actionCombo.addItem(ErrorCategorisationLink.STOP_ACTION);
		
		errorCategoryCombo.addItem(ErrorCategorisation.TRIVIAL);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.TRIVIAL, VaadinIcons.ARROW_DOWN);
		errorCategoryCombo.addItem(ErrorCategorisation.MAJOR);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.MAJOR, VaadinIcons.ARROW_UP);
		errorCategoryCombo.addItem(ErrorCategorisation.CRITICAL);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.CRITICAL, VaadinIcons.EXCLAMATION_CIRCLE_O);
		errorCategoryCombo.addItem(ErrorCategorisation.BLOCKER);
		errorCategoryCombo.setItemIcon(ErrorCategorisation.BLOCKER, VaadinIcons.BAN);
		
		layout.addComponent(actionCombo, 1, 6);		
		layout.addComponent(errorCategoryCombo, 1, 7); 
    }
    
}