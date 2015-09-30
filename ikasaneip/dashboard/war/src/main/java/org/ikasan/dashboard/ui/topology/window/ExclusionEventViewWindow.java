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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ExclusionEventViewWindow extends Window
{
	private static Logger logger = Logger.getLogger(ExclusionEventViewWindow.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField roleName;
	private TextField roleDescription;
	private ExclusionEvent exclusionEvent;
	private ErrorOccurrence errorOccurrence;
	private SerialiserFactory serialiserFactory;
	private ExclusionEventAction action;
	private HospitalManagementService<ExclusionEventAction> hospitalManagementService;
	private TopologyService topologyService;

	/**
	 * @param policy
	 */
	public ExclusionEventViewWindow(ExclusionEvent exclusionEvent, ErrorOccurrence errorOccurrence, SerialiserFactory serialiserFactory, ExclusionEventAction action,
			HospitalManagementService<ExclusionEventAction> hospitalManagementService, TopologyService topologyService)
	{
		super();
		this.exclusionEvent = exclusionEvent;
		this.errorOccurrence = errorOccurrence;
		this.serialiserFactory = serialiserFactory;
		this.action = action;
		this.hospitalManagementService = hospitalManagementService;
		this.topologyService = topologyService;
		
		this.init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		this.setHeight("90%");
		this.setWidth("90%");
		
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("100%");
		
		layout.addComponent(createExclusionEventDetailsPanel());
			
		this.setContent(layout);
	}

	protected Panel createExclusionEventDetailsPanel()
	{
		Panel exclusionEventDetailsPanel = new Panel();
		exclusionEventDetailsPanel.setSizeFull();
		exclusionEventDetailsPanel.setStyleName("dashboard");
		
		GridLayout layout = new GridLayout(4, 7);
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, .10f);
		layout.setColumnExpandRatio(1, .30f);
		layout.setColumnExpandRatio(2, .05f);
		layout.setColumnExpandRatio(3, .30f);
		
		layout.setWidth("100%");
		
		Label exclusionEvenDetailsLabel = new Label("Exclusion Event Details");
		exclusionEvenDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(exclusionEvenDetailsLabel, 0, 0, 3, 0);
		
		Label label = new Label("Module Name:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf1 = new TextField();
		tf1.setValue(this.exclusionEvent.getModuleName());
		tf1.setReadOnly(true);
		tf1.setWidth("80%");
		layout.addComponent(tf1, 1, 1);
		
		label = new Label("Flow Name:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.exclusionEvent.getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 2);
		
		label = new Label("Event Id:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.errorOccurrence.getEventLifeIdentifier());
		tf3.setReadOnly(true);
		tf3.setWidth("80%");
		layout.addComponent(tf3, 1, 3);
		
		label = new Label("Date/Time:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 4);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf4 = new TextField();
		tf4.setValue(new Date(this.exclusionEvent.getTimestamp()).toString());
		tf4.setReadOnly(true);
		tf4.setWidth("80%");
		layout.addComponent(tf4, 1, 4);
		
		label = new Label("Error URI:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 5);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf5 = new TextField();
		tf5.setValue(exclusionEvent.getErrorUri());
		tf5.setReadOnly(true);
		tf5.setWidth("80%");
		layout.addComponent(tf5, 1, 5);
		
		label = new Label("Action:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 2, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		final TextField tf6 = new TextField();
		if(this.action != null)
		{
			tf6.setValue(action.getAction());
		}
		tf6.setReadOnly(true);
		tf6.setWidth("80%");
		layout.addComponent(tf6, 3, 1);
		
		label = new Label("Actioned By:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 2, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		final TextField tf7 = new TextField();
		if(this.action != null)
		{
			tf7.setValue(action.getActionedBy());
		}
		tf7.setReadOnly(true);
		tf7.setWidth("80%");
		layout.addComponent(tf7, 3, 2);
		
		label = new Label("Actioned Time:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 2, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		final TextField tf8 = new TextField();
		if(this.action != null)
		{   	    
			tf8.setValue(new Date(action.getTimestamp()).toString());
		}
		tf8.setReadOnly(true);
		tf8.setWidth("80%");
		layout.addComponent(tf8, 3, 3);
		
		final Button resubmitButton = new Button("Re-submit");
		final Button ignoreButton = new Button("Ignore");
		
		resubmitButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
        	        	.getAttribute(DashboardSessionValueConstants.USER);
            	
            	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
            	
            	ClientConfig clientConfig = new ClientConfig();
            	clientConfig.register(feature) ;
            	
            	Client client = ClientBuilder.newClient(clientConfig);
            	
            	Module module = topologyService.getModuleByName(exclusionEvent.getModuleName());
            	
            	if(module == null)
            	{
            		Notification.show("Unable to find server information for module we are attempting to re-submit to: " + exclusionEvent.getModuleName() 
            				, Type.ERROR_MESSAGE);
            		
            		return;
            	}
            	
            	Server server = module.getServer();
        		
        		String url = server.getUrl() + ":" + server.getPort()
        				+ module.getContextRoot() 
        				+ "/rest/resubmission/resubmit/"
        	    		+ exclusionEvent.getModuleName() 
        	    		+ "/"
        	    		+ exclusionEvent.getFlowName()
        	    		+ "/"
        	    		+ exclusionEvent.getErrorUri();
        		
        		logger.info("Resubmission Url: " + url);
        		
        	    WebTarget webTarget = client.target(url);
        	    Response response = webTarget.request().put(Entity.entity(exclusionEvent.getEvent(), MediaType.APPLICATION_OCTET_STREAM));
        	    
        	    if(response.getStatus()  != 200)
        	    {
        	    	response.bufferEntity();
        	        
        	        String responseMessage = response.readEntity(String.class);
        	    	Notification.show("An error was received trying to resubmit event: " 
        	    			+ responseMessage, Type.ERROR_MESSAGE);
        	    }
        	    else
        	    {
        	    	Notification.show("Event resumitted successfully.");
        	    	resubmitButton.setVisible(false);
        	    	ignoreButton.setVisible(false);
        	    	
        	    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
        	    	tf6.setReadOnly(false);
        			tf7.setReadOnly(false);
        			tf8.setReadOnly(false);
        	    	tf6.setValue(action.getAction());
        			tf7.setValue(action.getActionedBy());
        			tf8.setValue(new Date(action.getTimestamp()).toString());
        			tf6.setReadOnly(true);
        			tf7.setReadOnly(true);
        			tf8.setReadOnly(true);
        	    }
            }
        });
		
		ignoreButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
        	        	.getAttribute(DashboardSessionValueConstants.USER);
            	
            	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
            	
            	ClientConfig clientConfig = new ClientConfig();
            	clientConfig.register(feature) ;
            	
            	Client client = ClientBuilder.newClient(clientConfig);
            	
            	Module module = topologyService.getModuleByName(exclusionEvent.getModuleName());
            	
            	if(module == null)
            	{
            		Notification.show("Unable to find server information for module we are submitting the ignore to: " + exclusionEvent.getModuleName() 
            				, Type.ERROR_MESSAGE);
            		
            		return;
            	}
            	
            	Server server = module.getServer();
        		
        		String url = server.getUrl() + ":" + server.getPort()
        				+ module.getContextRoot() 
        				+ "/rest/resubmission/ignore/"
        				+ exclusionEvent.getModuleName() 
        	    		+ "/"
        	    		+ exclusionEvent.getFlowName()
        	    		+ "/"
        	    		+ exclusionEvent.getErrorUri();
        		
        		logger.info("Ignore Url: " + url);
        		
        	    WebTarget webTarget = client.target(url);
        	    Response response = webTarget.request().put(Entity.entity(exclusionEvent.getEvent(), MediaType.APPLICATION_OCTET_STREAM));
        	    
        	    if(response.getStatus()  != 200)
        	    {
        	    	response.bufferEntity();
        	        
        	        String responseMessage = response.readEntity(String.class);
        	    	Notification.show("An error was received trying to resubmit event: " 
        	    			+ responseMessage, Type.ERROR_MESSAGE);
        	    }
        	    else
        	    {
        	    	Notification.show("Event ignored successfully.");
        	    	resubmitButton.setVisible(false);
        	    	ignoreButton.setVisible(false);
        	    	
        	    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
        	    	tf6.setReadOnly(false);
        			tf7.setReadOnly(false);
        			tf8.setReadOnly(false);
        	    	tf6.setValue(action.getAction());
        			tf7.setValue(action.getActionedBy());
        			tf8.setValue(new Date(action.getTimestamp()).toString());
        			tf6.setReadOnly(true);
        			tf7.setReadOnly(true);
        			tf8.setReadOnly(true);
        	    }
            }
        });
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setHeight("100%");
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth(200, Unit.PIXELS);
		buttonLayout.setMargin(true);
		buttonLayout.addComponent(resubmitButton);
		buttonLayout.addComponent(ignoreButton);
		
		if(this.action == null)
		{
			layout.addComponent(buttonLayout, 0, 6, 3, 6);
			layout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		}
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		if(authentication != null 
    			&& (!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
    					&& !authentication.hasGrantedAuthority(SecurityConstants.ACTION_EXCLUSIONS_AUTHORITY)))
    	{
			resubmitButton.setVisible(false);
			ignoreButton.setVisible(false);
    	}
		
		final AceEditor eventEditor = new AceEditor();
		eventEditor.setCaption("Event Payload");
		logger.info("Setting exclusion event to: " + new String(this.exclusionEvent.getEvent()));
		Object event = this.serialiserFactory.getDefaultSerialiser().deserialise(this.exclusionEvent.getEvent());
		eventEditor.setValue(event.toString());
		eventEditor.setReadOnly(true);
		eventEditor.setMode(AceMode.java);
		eventEditor.setTheme(AceTheme.eclipse);
		eventEditor.setWordWrap(true);
		eventEditor.setWidth("100%");
		eventEditor.setHeight(600, Unit.PIXELS);
		
		CheckBox wrapTextCheckBox = new CheckBox("Wrap text");
		wrapTextCheckBox.addValueChangeListener(new Property.ValueChangeListener() 
		{
            @Override
            public void valueChange(ValueChangeEvent event)
            {
                Object value = event.getProperty().getValue();
                boolean isCheck = (null == value) ? false : (Boolean) value;
               
                eventEditor.setWordWrap(isCheck);
            }
        });
		wrapTextCheckBox.setValue(true);
		
		HorizontalLayout eventEditorLayout = new HorizontalLayout();
		eventEditorLayout.setSizeFull();
		eventEditorLayout.addComponent(eventEditor);
		
		AceEditor errorEditor = new AceEditor();
		errorEditor.setCaption("Error Details");
		errorEditor.setValue(this.errorOccurrence.getErrorDetail());
		errorEditor.setReadOnly(true);
		errorEditor.setWordWrap(true);
		errorEditor.setMode(AceMode.xml);
		errorEditor.setTheme(AceTheme.eclipse);
		errorEditor.setWidth("100%");
		errorEditor.setHeight(600, Unit.PIXELS);
		
		HorizontalLayout errorEditorLayout = new HorizontalLayout();
		errorEditorLayout.setSizeFull();
		errorEditorLayout.addComponent(errorEditor);

		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		
		VerticalLayout h1 = new VerticalLayout();
		h1.setSizeFull();
		h1.setMargin(true);
		h1.addComponent(wrapTextCheckBox);
		h1.addComponent(eventEditorLayout);
		
		HorizontalLayout h2 = new HorizontalLayout();
		h2.setSizeFull();
		h2.setMargin(true);
		h2.addComponent(errorEditorLayout);
		
		HorizontalLayout formLayout = new HorizontalLayout();
		formLayout.setWidth("100%");
		formLayout.setHeight(240, Unit.PIXELS);
		formLayout.addComponent(layout);
		
		GridLayout wrapperLayout = new GridLayout(1, 4);
		wrapperLayout.setMargin(true);
		wrapperLayout.setWidth("100%");
		wrapperLayout.addComponent(formLayout);

		tabsheet.addTab(h1, "Event Payload");
		tabsheet.addTab(h2, "Error Details");
		
		wrapperLayout.addComponent(tabsheet);
		exclusionEventDetailsPanel.setContent(wrapperLayout);
		return exclusionEventDetailsPanel;
	}
}
