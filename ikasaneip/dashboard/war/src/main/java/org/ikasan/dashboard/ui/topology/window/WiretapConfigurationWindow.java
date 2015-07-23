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

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanCellStyleGenerator;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.trigger.model.Trigger;
import org.ikasan.wiretap.service.TriggerManagementService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class WiretapConfigurationWindow extends Window
{
	private static final long serialVersionUID = 5681865414123002596L;

	private Logger logger = Logger.getLogger(WiretapConfigurationWindow.class);
	
	private Component component;
	private TriggerManagementService triggerManagementService;
	private Table triggerTable;
	
	/**
	 * @param configurationManagement
	 */
	public WiretapConfigurationWindow(Component component,
			TriggerManagementService triggerManagementService)
	{
		super();
		
		this.component = component;
		this.triggerManagementService = triggerManagementService;
		this.triggerTable = triggerTable;
		
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
		
		GridLayout layout = new GridLayout(2, 10);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setColumnExpandRatio(0, .25f);
		layout.setColumnExpandRatio(1, .75f);
    	
    	Label wiretapLabel = new Label("Wiretap Configuration");
		wiretapLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(wiretapLabel);

		Label moduleNameLabel = new Label();
		moduleNameLabel.setContentMode(ContentMode.HTML);
		moduleNameLabel.setValue(VaadinIcons.ARCHIVE.getHtml() + " Module Name:");
		moduleNameLabel.setSizeUndefined();		
		layout.addComponent(moduleNameLabel, 0, 1);
		layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleNameTextField = new TextField();
		moduleNameTextField.setRequired(true);
		moduleNameTextField.setValue(this.component.getFlow().getModule().getName());
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
		flowNameTextField.setValue(this.component.getFlow().getName());
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
		componentNameTextField.setValue(this.component.getName());
		componentNameTextField.setReadOnly(true);
		componentNameTextField.setWidth("80%");
		layout.addComponent(componentNameTextField, 1, 3); 
		
		Label errorCategoryLabel = new Label("Relationship:");
		errorCategoryLabel.setSizeUndefined();		
		layout.addComponent(errorCategoryLabel, 0, 4);
		layout.setComponentAlignment(errorCategoryLabel, Alignment.MIDDLE_RIGHT);
		
		final ComboBox relationshipCombo = new ComboBox();
//		relationshipCombo.addValidator(new StringLengthValidator(
//	            "An relationship must be selected!", 1, -1, false));
		relationshipCombo.setImmediate(false);
		relationshipCombo.setValidationVisible(false);
		relationshipCombo.setRequired(true);
		relationshipCombo.setRequiredError("A relationship must be selected!");
		relationshipCombo.setHeight("30px");
		relationshipCombo.setNullSelectionAllowed(false);
		layout.addComponent(relationshipCombo, 1, 4); 
		relationshipCombo.addItem("before");
		relationshipCombo.setItemCaption("before", "Before");
		relationshipCombo.addItem("after");
		relationshipCombo.setItemCaption("after", "After");
		
		Label jobTypeLabel = new Label("Job Type:");
		jobTypeLabel.setSizeUndefined();		
		layout.addComponent(jobTypeLabel, 0, 5);
		layout.setComponentAlignment(jobTypeLabel, Alignment.MIDDLE_RIGHT);
		
		final ComboBox jobTopCombo = new ComboBox();
//		jobTopCombo.addValidator(new StringLengthValidator(
//	            "A job type must be selected!", 1, -1, false));
		jobTopCombo.setImmediate(false);
		jobTopCombo.setValidationVisible(false);
		jobTopCombo.setRequired(true);
		jobTopCombo.setRequiredError("A job type must be selected!");
		jobTopCombo.setHeight("30px");
		jobTopCombo.setNullSelectionAllowed(false);
		layout.addComponent(jobTopCombo, 1, 5); 
		jobTopCombo.addItem("loggingJob");
		jobTopCombo.setItemCaption("loggingJob", "Logging Job");
		jobTopCombo.addItem("wiretapJob");
		jobTopCombo.setItemCaption("wiretapJob", "Wiretap Job");
		
		final Label timeToLiveLabel = new Label("Time to Live:");
		timeToLiveLabel.setSizeUndefined();		
		timeToLiveLabel.setVisible(false);
		layout.addComponent(timeToLiveLabel, 0, 6);
		layout.setComponentAlignment(timeToLiveLabel, Alignment.MIDDLE_RIGHT);
		
		final TextField timeToLiveTextField = new TextField();
		timeToLiveTextField.setRequired(true);
		timeToLiveTextField.setValidationVisible(false);
		jobTopCombo.setRequiredError("A time to live value must be entered!");
		timeToLiveTextField.setVisible(false);
		timeToLiveTextField.setWidth("40%");
		layout.addComponent(timeToLiveTextField, 1, 6);
		
		jobTopCombo.addValueChangeListener(new ComboBox.ValueChangeListener()
		{

			/* (non-Javadoc)
			 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
			 */
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				String value = (String)event.getProperty().getValue();
				
				if(value.equals("wiretapJob"))
				{
					timeToLiveLabel.setVisible(true);
					timeToLiveTextField.setVisible(true);
				}
				else
				{
					timeToLiveLabel.setVisible(false);
					timeToLiveTextField.setVisible(false);
				}
			}
	
		});

		
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
            		relationshipCombo.validate();
            		jobTopCombo.validate();
            		
            		if(timeToLiveTextField.isVisible())
            		{
            			timeToLiveTextField.validate();
            		}
                } 
                catch (InvalidValueException e) 
                {
                	relationshipCombo.setValidationVisible(true);
                	relationshipCombo.markAsDirty();
                	jobTopCombo.setValidationVisible(true);
                	jobTopCombo.markAsDirty();
                	
                	if(timeToLiveTextField.isVisible())
            		{
                		timeToLiveTextField.setValidationVisible(true);
                		timeToLiveTextField.markAsDirty();
            		}
                	
                	Notification.show("There are errors on the wiretap creation form!"
                			, Type.ERROR_MESSAGE);
                    return;
                }
            	
            	createWiretap((String)relationshipCombo.getValue() 
            			,(String)jobTopCombo.getValue(), timeToLiveTextField.getValue());
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
		
		layout.addComponent(buttonLayouts, 0, 7, 1, 7);
		layout.setComponentAlignment(buttonLayouts, Alignment.MIDDLE_CENTER);
		
		Panel paramPanel = new Panel();
		paramPanel.setStyleName("dashboard");
		paramPanel.setWidth("100%");
		paramPanel.setContent(layout);
		
		triggerTable = new Table();
		
		Label existingWiretapLabel = new Label("Existing Wiretaps");
		existingWiretapLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(existingWiretapLabel, 0, 8, 1, 8);
		
		layout.addComponent(triggerTable, 0, 9, 1, 9);
		layout.setComponentAlignment(triggerTable, Alignment.TOP_CENTER);
		
		this.triggerTable.setWidth("80%");
		this.triggerTable.setHeight(150, Unit.PIXELS);
		this.triggerTable.setCellStyleGenerator(new IkasanCellStyleGenerator());
		this.triggerTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.triggerTable.addStyleName("ikasan");
		this.triggerTable.addContainerProperty("Job Type", String.class,  null);
		this.triggerTable.addContainerProperty("Relationship", String.class,  null);
		this.triggerTable.addContainerProperty("Trigger Parameters", String.class,  null);
		this.triggerTable.addContainerProperty("", Button.class,  null);
		
		
		refreshTriggerTable();		
		
		GridLayout wrapper = new GridLayout(1, 1);
		wrapper.setMargin(true);
		wrapper.setSizeFull();
		wrapper.addComponent(paramPanel);
		
		this.setContent(wrapper);
    }
    
    protected void refreshTriggerTable()
    {
    	this.triggerTable.removeAllItems();
    	
    	List<Trigger> triggers = this.triggerManagementService.findTriggers(component.getFlow().getModule().getName()
				, component.getFlow().getName(), component.getName());
		
		for(final Trigger trigger: triggers)
		{
			String parameters = new String();
			
			Set<String> keys = trigger.getParams().keySet();
			
			for(String key: keys)
			{
				String value = trigger.getParams().get(key);
				
				if(value != null && value.trim().length() > 0)
				{
					parameters += key + "=" + trigger.getParams().get(key) + " ";
				}
			}
			
			Button deleteTriggerButton = new Button();
			Resource deleteIcon = VaadinIcons.CLOSE_CIRCLE_O;
			deleteTriggerButton.setIcon(deleteIcon);
			deleteTriggerButton.setStyleName(ValoTheme.BUTTON_LINK);

			
			// Add the delete functionality to each role that is added
			deleteTriggerButton.addClickListener(new Button.ClickListener() 
	        {
	            public void buttonClick(ClickEvent event) 
	            {		
	            	deleteWiretap(trigger.getId());
	            }
	        });
			
			this.triggerTable.addItem(new Object[]{trigger.getJobName(), trigger.getRelationship().name()
    				, parameters, deleteTriggerButton}, trigger);
		}
    }
    

    protected void createWiretap(String relationship, String jobType, String timeToLive)
    {
    	IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
    	
    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    	
    	Server server = this.component.getFlow().getModule().getServer();
		
		String url = "http://" + server.getUrl() + ":" + server.getPort()
				+ this.component.getFlow().getModule().getContextRoot() 
				+ "/rest/wiretap/createTrigger/"
	    		+ this.component.getFlow().getModule().getName() 
	    		+ "/"
	    		+ this.component.getFlow().getName()
	    		+ "/"
	    		+ this.component.getName()
	    		+ "/"
	    		+ relationship
	    		+ "/"
	    		+ jobType;
		
		logger.info("Resubmission Url: " + url);
		
	    WebTarget webTarget = client.target(url);
	    Response response = webTarget.request().put(Entity.entity(timeToLive, MediaType.APPLICATION_OCTET_STREAM));
	    
	    if(response.getStatus()  != 200)
	    {
	    	response.bufferEntity();
	        
	        String responseMessage = response.readEntity(String.class);
	        
	        logger.error("An error occurred trying to create a wiretap: " + responseMessage);
	    	Notification.show("An error was received trying to create a wiretap: " 
	    			+ responseMessage, Type.ERROR_MESSAGE);
	    }
	    else
	    {
	    	Notification.show("Wiretap created successfully!");
	    	
	    	this.refreshTriggerTable();
	    }
    }
    
    protected void deleteWiretap(Long triggerId)
    {
    	IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
    	
    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    	
    	Server server = this.component.getFlow().getModule().getServer();
		
		String url = "http://" + server.getUrl() + ":" + server.getPort()
				+ this.component.getFlow().getModule().getContextRoot() 
				+ "/rest/wiretap/deleteTrigger";
		
		logger.info("Resubmission Url: " + url);
		
	    WebTarget webTarget = client.target(url);
	    Response response = webTarget.request().put(Entity.entity(triggerId, MediaType.APPLICATION_JSON));
	    
	    if(response.getStatus()  != 200)
	    {
	    	response.bufferEntity();
	        
	        String responseMessage = response.readEntity(String.class);
	        
	        logger.error("An error occurred trying to delete a wiretap: " + responseMessage);
	    	Notification.show("An error was received trying to delete a wiretap: " 
	    			+ responseMessage, Type.ERROR_MESSAGE);
	    }
	    else
	    {
	    	Notification.show("Wiretap deleted successfully!");
	    	
	    	this.refreshTriggerTable();
	    }
    }
    
}