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
package org.ikasan.dashboard.ui.administration.window;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.configurationService.model.ConfigurationParameterStringImpl;
import org.ikasan.dashboard.notification.NotificationConfiguredResource;
import org.ikasan.dashboard.notification.NotificationContentProducerConfiguration;
import org.ikasan.dashboard.notification.contentproducer.CategorisedErrorNotificationContentProducer;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.constants.NotificationConstants;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.model.Notification;
import org.ikasan.topology.service.TopologyService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class NotificationWindow extends Window
{
	private Logger logger = Logger.getLogger(NotificationWindow.class);
	
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField name;
	private ComboBox contextCombo  = new ComboBox();
	private ComboBox filterCombo  = new ComboBox();
	private TextArea recipients;
	private TextField subject;
	private TextArea body;
	
	
	private Label policyLinkHintLabel = new Label();
	
	private Notification notification;
	private NotificationContentProducerConfiguration notificationContentProducerConfiguration;
	private NotificationConfiguredResource notificationConfiguredResource;
	
	private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
	private TopologyService topologyService; 

	/**
	 * @param policy
	 */
	public NotificationWindow(TopologyService topologyService, ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
	{
		super();
		this.notification = new Notification();
		
		this.topologyService = topologyService;
		this.configurationManagement = configurationManagement;

		init();
	}
	
	/**
	 * @param policy
	 */
	public NotificationWindow(TopologyService topologyService, ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
			Notification notification)
	{
		super();
		this.notification = notification;
		
		this.topologyService = topologyService;
		this.configurationManagement = configurationManagement;

		init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		
		this.setWidth("60%");
		this.setHeight("70%");
		
		GridLayout gridLayout = new GridLayout(2, 8);
		gridLayout.setWidth("100%");
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		
		gridLayout.setColumnExpandRatio(0, .1f);
		gridLayout.setColumnExpandRatio(1, .9f);
		
		Label createNewPolicyLabel = new Label("Notification");
		createNewPolicyLabel.setStyleName(ValoTheme.LABEL_HUGE);
		
		gridLayout.addComponent(createNewPolicyLabel, 0, 0, 1, 0);

		Label nameLabel = new Label("Name:");
		nameLabel.setSizeUndefined();
		this.name = new TextField();
		this.name.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		this.name.setWidth("80%");
		
		if(notification.getName() != null)
		{
			this.name.setValue(notification.getName());
			this.name.setReadOnly(true);
		}
		
		this.name.setNullRepresentation("");
		
		gridLayout.addComponent(nameLabel, 0, 1);
		gridLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(name, 1, 1);
		
		Label contextLabel = new Label("Context");
		contextLabel.setSizeUndefined();
		
		this.contextCombo.addItem(NotificationConstants.CATEGORISED_ERROR_NOTIFICATION_CONTEXT);
		this.contextCombo.addItem(NotificationConstants.CATEGORISED_ERROR_NOTIFICATION_CONTEXT_PERIODIC);
		this.contextCombo.setNullSelectionAllowed(false);
		this.contextCombo.setRequired(true);
		this.contextCombo.setRequiredError("Please select a context");
		this.contextCombo.setValue(notification.getContext());
		
		gridLayout.addComponent(contextLabel, 0, 2);
		gridLayout.setComponentAlignment(contextLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(this.contextCombo, 1, 2);
		
		Label filterLabel = new Label("Filter");
		filterLabel.setSizeUndefined();
		
		gridLayout.addComponent(filterLabel, 0, 3);
		gridLayout.setComponentAlignment(filterLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(this.filterCombo, 1, 3);
		
		List<Filter> filters = this.topologyService.getAllFilters();
		
		for(Filter filter: filters)
		{
			this.filterCombo.addItem(filter);
			this.filterCombo.setItemCaption(filter, filter.getName());
			
			if(notification.getFilter() != null && notification.getFilter().getId().compareTo(filter.getId()) == 0)
			{
				this.filterCombo.setValue(filter);
				
				logger.debug("Setting filter to: " + filter.getName());
			}
		}		
		
		
		this.filterCombo.setNullSelectionAllowed(false);
		this.filterCombo.setRequired(true);
		this.filterCombo.setRequiredError("Please select a filter");
		
		Label recipientLabel = new Label("Recipients");
		recipientLabel.setSizeUndefined();
		
		this.recipients = new TextArea();
		this.recipients.addValidator(new StringLengthValidator(
	            "A comma seperated list of recipients must be entered.",
	            1, null, false));
		this.recipients.setWidth("80%");
		this.recipients.setRows(2);
		
		gridLayout.addComponent(recipientLabel, 0, 4);
		gridLayout.setComponentAlignment(recipientLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(this.recipients, 1, 4);
		
		Label subjectLabel = new Label("Subject");
		subjectLabel.setSizeUndefined();
		
		this.subject = new TextField();
		this.subject.addValidator(new StringLengthValidator(
	            "A subject must be entered.",
	            1, null, false));
		this.subject.setWidth("80%");
		
		gridLayout.addComponent(subjectLabel, 0, 5);
		gridLayout.setComponentAlignment(subjectLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(this.subject, 1, 5);
		
		Label bodyLabel = new Label("Body");
		bodyLabel.setSizeUndefined();
		
		this.body = new TextArea();
		this.body.addValidator(new StringLengthValidator(
	            "A body must be entered.",
	            1, null, false));
		this.body.setWidth("80%");
		this.body.setRows(4);
		
		gridLayout.addComponent(bodyLabel, 0, 6);
		gridLayout.setComponentAlignment(bodyLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(this.body, 1, 6);
		
		this.name.setValidationVisible(false);
    	this.subject.setValidationVisible(false);
    	this.recipients.setValidationVisible(false);
    	this.body.setValidationVisible(false);
    	this.contextCombo.setValidationVisible(false);
    	this.filterCombo.setValidationVisible(false);
		
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(saveButton);
		buttonLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);

		
		gridLayout.addComponent(buttonLayout, 0, 7, 1, 7);
		gridLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {   
            	try 
                {
            		NotificationWindow.this.body.validate();
            		NotificationWindow.this.subject.validate();
            		NotificationWindow.this.recipients.validate();
            		NotificationWindow.this.name.validate();
            		NotificationWindow.this.filterCombo.validate();
            		NotificationWindow.this.contextCombo.validate();
                } 
                catch (InvalidValueException e) 
                {
                	NotificationWindow.this.body.setValidationVisible(true);
            		NotificationWindow.this.subject.setValidationVisible(true);
            		NotificationWindow.this.recipients.setValidationVisible(true);
            		NotificationWindow.this.name.setValidationVisible(true);
            		NotificationWindow.this.filterCombo.setValidationVisible(true);
            		NotificationWindow.this.contextCombo.setValidationVisible(true);
                	
            		com.vaadin.ui.Notification.show("Validation errors have occurred!", Type.ERROR_MESSAGE);
                	
                    return;
                }
            	
            	if(NotificationWindow.this.notification == null)
            	{
            		Notification notification = new Notification();
            	}
            	
            	notification.setFilter((Filter)NotificationWindow.this.filterCombo.getValue());
            	notification.setName(NotificationWindow.this.name.getValue());
            	notification.setContext((String)NotificationWindow.this.contextCombo.getValue());
            	
            	if(NotificationWindow.this.notificationConfiguredResource == null)
            	{
            		NotificationWindow.this.notificationConfiguredResource 
            			= new CategorisedErrorNotificationContentProducer(NotificationWindow.this.notification);
            	}

            	Configuration configuration = NotificationWindow.this.configurationManagement
            			.getConfiguration(NotificationWindow.this.notificationConfiguredResource);
            	
            	if(configuration == null)
            	{
            		configuration = NotificationWindow.this.configurationManagement
            				.createConfiguration(NotificationWindow.this.notificationConfiguredResource);
            	}
            	
            	final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();      
                
                for(ConfigurationParameter parameter: parameters)
                {
                	if(parameter.getName().equals("subject"))
                	{
                		((ConfigurationParameterStringImpl)parameter).setValue(NotificationWindow.this.subject.getValue());
                	}
                	else if(parameter.getName().equals("body"))
                	{
                		((ConfigurationParameterStringImpl)parameter).setValue(NotificationWindow.this.body.getValue());
                	}
                	if(parameter.getName().equals("recipients"))
                	{
                		((ConfigurationParameterStringImpl)parameter).setValue(NotificationWindow.this.recipients.getValue());
                	}
                	if(parameter.getName().equals("notificationName"))
                	{
                		((ConfigurationParameterStringImpl)parameter).setValue(NotificationWindow.this.name.getValue());
                	}
                }
 
                NotificationWindow.this.configurationManagement.saveConfiguration(configuration);
                
                NotificationWindow.this.topologyService.save(notification);
            	
            	logger.debug("Notification: " + notification);
                
            	com.vaadin.ui.Notification.show("Notification Saved!");

            	UI.getCurrent().removeWindow(NotificationWindow.this);
            }
        });
		
		cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
                UI.getCurrent().removeWindow(NotificationWindow.this);
            }
        });
		
		setNotificationFormValues();
		
		this.setContent(gridLayout);
	}
	
	protected void setNotificationFormValues()
	{
		Configuration configuration = NotificationWindow.this.configurationManagement
    			.getConfiguration(new CategorisedErrorNotificationContentProducer(this.notification));
    	
    	if(configuration != null)
    	{
    		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();      
            
            for(ConfigurationParameter parameter: parameters)
            {
            	if(parameter.getName().equals("subject"))
            	{
            		this.subject.setValue(((ConfigurationParameterStringImpl)parameter).getValue());
            	}
            	else if(parameter.getName().equals("body"))
            	{
            		this.body.setValue(((ConfigurationParameterStringImpl)parameter).getValue());
            	}
            	if(parameter.getName().equals("recipients"))
            	{
            		this.recipients.setValue(((ConfigurationParameterStringImpl)parameter).getValue());
            	}
            }
    	}
    	
	}
	
}
