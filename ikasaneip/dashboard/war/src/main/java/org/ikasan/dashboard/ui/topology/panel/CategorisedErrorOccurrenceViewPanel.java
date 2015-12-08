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
package org.ikasan.dashboard.ui.topology.panel;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
import org.ikasan.error.reporting.model.CategorisedErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;
import org.vaadin.teemu.VaadinIcons;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.rendering.converter.Converter;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.syntax.Syntax;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class CategorisedErrorOccurrenceViewPanel extends Panel
{
	private static Logger logger = Logger.getLogger(ExclusionEventViewPanel.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private CategorisedErrorOccurrence categorisedErrorOccurrence;
	
	private ErrorReportingManagementService errorReportingManagementService;
	
	private HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService;
	
	private TopologyService topologyService;
	
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	

	/**
	 * @param policy
	 */
	public CategorisedErrorOccurrenceViewPanel(CategorisedErrorOccurrence errorOccurrence,
			ErrorReportingManagementService errorReportingManagementService,
			HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService,
			TopologyService topologyService, ExclusionManagementService<ExclusionEvent, String> exclusionManagementService)
	{
		super();
		this.categorisedErrorOccurrence = errorOccurrence;
		this.errorReportingManagementService = errorReportingManagementService;
		this.hospitalManagementService = hospitalManagementService;
		this.topologyService = topologyService;
		this.exclusionManagementService = exclusionManagementService;
		
		this.init();
	}


	public void init()
	{
		this.setSizeFull();
		
		GridLayout layout = new GridLayout(1, 1);
		layout.setWidth("100%");

		layout.addComponent(createErrorOccurrenceDetailsPanel(), 0, 0);
		
		this.setContent(layout);
	}

	protected Panel createErrorOccurrenceDetailsPanel()
	{
		Panel errorOccurrenceDetailsPanel = new Panel();
		
		GridLayout layout = new GridLayout(4, 8);
		layout.setWidth("100%");
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, .10f);
		layout.setColumnExpandRatio(1, .30f);
		layout.setColumnExpandRatio(2, .05f);
		layout.setColumnExpandRatio(3, .30f);
		
		Label errorOccurrenceDetailsLabel = new Label(" Categorised Error Details", ContentMode.HTML);
		Label errorCategoryLabel = new Label();
		
		if(categorisedErrorOccurrence.getErrorCategorisation().getErrorCategory().equals(ErrorCategorisation.BLOCKER))
	    {
			errorOccurrenceDetailsLabel = new Label(VaadinIcons.BAN.getHtml() + " Categorised Error Occurence Details", ContentMode.HTML);
			errorCategoryLabel = new Label(VaadinIcons.BAN.getHtml() + " Blocker", ContentMode.HTML);
	    }
	    else if(categorisedErrorOccurrence.getErrorCategorisation().getErrorCategory().equals(ErrorCategorisation.CRITICAL))
	    {
	    	errorOccurrenceDetailsLabel = new Label(VaadinIcons.EXCLAMATION.getHtml() + " Categorised Error Occurence Details", ContentMode.HTML);
	    	errorCategoryLabel = new Label(VaadinIcons.EXCLAMATION.getHtml() + " Critical", ContentMode.HTML);
	    }
	    else if(categorisedErrorOccurrence.getErrorCategorisation().getErrorCategory().equals(ErrorCategorisation.MAJOR))
	    {
	    	errorOccurrenceDetailsLabel = new Label(VaadinIcons.ARROW_UP.getHtml() + " Categorised Error Occurence Details", ContentMode.HTML);
	    	errorCategoryLabel = new Label(VaadinIcons.ARROW_UP.getHtml() + " Major", ContentMode.HTML);
	    }
	    else if(categorisedErrorOccurrence.getErrorCategorisation().getErrorCategory().equals(ErrorCategorisation.TRIVIAL))
	    {
	    	errorOccurrenceDetailsLabel = new Label(VaadinIcons.ARROW_DOWN.getHtml() + " Categorised Error Occurence Details", ContentMode.HTML);
	    	errorCategoryLabel = new Label(VaadinIcons.ARROW_DOWN.getHtml() + " Trivial", ContentMode.HTML);
	    }
		
		errorOccurrenceDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(errorOccurrenceDetailsLabel, 0, 0, 3, 0);
		
		Label label = new Label("Module Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf1 = new TextField();
		tf1.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getModuleName());
		tf1.setReadOnly(true);
		tf1.setWidth("80%");
		layout.addComponent(tf1, 1, 1);
		
		label = new Label("Flow Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 2);
		
		label = new Label("Component Name:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getFlowElementName());
		tf3.setReadOnly(true);
		tf3.setWidth("80%");
		layout.addComponent(tf3, 1, 3);
		
		label = new Label("Date/Time:");
		label.setSizeUndefined();		
		layout.addComponent(label, 0, 4);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		Date date = new Date(this.categorisedErrorOccurrence.getErrorOccurrence().getTimestamp());
		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
	    String timestamp = format.format(date);
		
		TextField tf4 = new TextField();
		tf4.setValue(timestamp);
		tf4.setReadOnly(true);
		tf4.setWidth("80%");
		layout.addComponent(tf4, 1, 4);
		
		GridLayout wrapperLayout = new GridLayout(1, 4);
		wrapperLayout.setMargin(true);
		wrapperLayout.setWidth("100%");
		
		label = new Label("Error Category:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 1);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		layout.addComponent(errorCategoryLabel, 3, 1);
		layout.setComponentAlignment(errorCategoryLabel, Alignment.MIDDLE_LEFT);
		
		label = new Label("System Action:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField systemAction = new TextField();
		systemAction.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getAction());
		systemAction.setReadOnly(true);
		systemAction.setWidth("80%");
		layout.addComponent(systemAction, 3, 2);
		
		ExclusionEventAction action = this.hospitalManagementService.getExclusionEventActionByErrorUri(this.categorisedErrorOccurrence.getErrorOccurrence().getUri());
		
		label = new Label("User Action:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		
		final TextField userAction = new TextField();
		userAction.setValue("");
		userAction.setReadOnly(true);
		userAction.setWidth("80%");
		layout.addComponent(userAction, 3, 3);
		
		label = new Label("User Action By:");
		label.setSizeUndefined();		
		layout.addComponent(label, 2, 4);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		final TextField userActionBy = new TextField();
		userActionBy.setValue("");
		userActionBy.setReadOnly(true);
		userActionBy.setWidth("80%");
		layout.addComponent(userActionBy, 3, 4);
		
		if(action != null)
		{
			userAction.setValue(action.getAction());
			userActionBy.setValue(action.getActionedBy());
		}
		
		
		if(action == null && this.categorisedErrorOccurrence.getErrorOccurrence().getAction().equals("ExcludeEvent"))
		{
			final Button resubmitButton = new Button("Re-submit");
			final Button ignoreButton = new Button("Ignore");
			
			final ExclusionEvent exclusionEvent = exclusionManagementService.find(categorisedErrorOccurrence.getErrorOccurrence().getUri());
			
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
	            		Notification.show("Unable to find server information for module we are attempting to re-submit to: " 
	            				+ exclusionEvent.getModuleName()
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
	        		
	        		logger.debug("Resubmission Url: " + url);
	        		
	        	    WebTarget webTarget = client.target(url);
	        	    Response response = webTarget.request().put(Entity.entity(exclusionEvent.getEvent(), MediaType.APPLICATION_OCTET_STREAM));
	        	    
	        	    if(response.getStatus()  != 200)
	        	    {
	        	    	response.bufferEntity();
	        	        
	        	        String responseMessage = response.readEntity(String.class);
	        	        
	        	        logger.error("An error was received trying to resubmit event: " + responseMessage); 
	        	        
	        	    	Notification.show("An error was received trying to resubmit event: " 
	        	    			+ responseMessage, Type.ERROR_MESSAGE);
	        	    }
	        	    else
	        	    {
	        	    	Notification.show("Event resumitted successfully.");
	        	    	resubmitButton.setVisible(false);
	        	    	ignoreButton.setVisible(false);
	        	    	
	        	    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
	        	    	userAction.setReadOnly(false);
	        	    	userActionBy.setReadOnly(false);
	//        			tf8.setReadOnly(false);
	        	    	userAction.setValue(action.getAction());
	        	    	userActionBy.setValue(action.getActionedBy());
	//        			tf8.setValue(new Date(action.getTimestamp()).toString());
	        	    	userAction.setReadOnly(true);
	        	    	userActionBy.setReadOnly(true);
	//        			tf8.setReadOnly(true);
	        	    	
	        	    	closeAssociatedErrorOccurence();
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
	            		logger.error("Unable to find server information for module we are submitting the ignore to: " + exclusionEvent.getModuleName()); 
	            		
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
	        		
	        		logger.debug("Ignore Url: " + url);
	        		
	        	    WebTarget webTarget = client.target(url);
	        	    Response response = webTarget.request().put(Entity.entity(exclusionEvent.getEvent(), MediaType.APPLICATION_OCTET_STREAM));
	        	    
	        	    if(response.getStatus()  != 200)
	        	    {
	        	    	response.bufferEntity();
	        	        
	        	        String responseMessage = response.readEntity(String.class);
	        	        
	        	        logger.error("An error was received trying to resubmit event: " 
	        	    			+ responseMessage);
	        	        
	        	    	Notification.show("An error was received trying to resubmit event: " 
	        	    			+ responseMessage, Type.ERROR_MESSAGE);
	        	    }
	        	    else
	        	    {
	        	    	Notification.show("Event ignored successfully.");
	        	    	resubmitButton.setVisible(false);
	        	    	ignoreButton.setVisible(false);
	        	    	
	        	    	ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(exclusionEvent.getErrorUri());
	        	    	userAction.setReadOnly(false);
	        	    	userActionBy.setReadOnly(false);
	//        			tf8.setReadOnly(false);
	        	    	userAction.setValue(action.getAction());
	        	    	userActionBy.setValue(action.getActionedBy());
	//        			tf8.setValue(new Date(action.getTimestamp()).toString());
	        	    	userAction.setReadOnly(true);
	        	    	userActionBy.setReadOnly(true);
	//        			tf8.setReadOnly(true);
	        	    	
	        	    	closeAssociatedErrorOccurence();
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
		
			layout.addComponent(buttonLayout, 0, 5, 3, 5);
			layout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		}

		
		
		AceEditor errorMessageEditor = new AceEditor();
		errorMessageEditor.setValue(this.categorisedErrorOccurrence.getErrorCategorisation().getErrorDescription()
				 + this.categorisedErrorOccurrence.getErrorOccurrence().getErrorMessage());
		errorMessageEditor.setReadOnly(true);
		errorMessageEditor.setMode(AceMode.xml);
		errorMessageEditor.setTheme(AceTheme.textmate);
		errorMessageEditor.setHeight(500, Unit.PIXELS);
		errorMessageEditor.setWidth("100%");
		errorMessageEditor.setWordWrap(true);
		
		AceEditor errorDetailEditor = new AceEditor();
		errorDetailEditor.setValue(this.categorisedErrorOccurrence.getErrorOccurrence().getErrorDetail());
		errorDetailEditor.setReadOnly(true);
		errorDetailEditor.setMode(AceMode.xml);
		errorDetailEditor.setTheme(AceTheme.eclipse);
		errorDetailEditor.setHeight(500, Unit.PIXELS);
		errorDetailEditor.setWidth("100%");

		
		final AceEditor eventEditor = new AceEditor();
		
		if(this.categorisedErrorOccurrence.getErrorOccurrence().getEvent() != null)
		{
			eventEditor.setValue(new String((byte[])this.categorisedErrorOccurrence.getErrorOccurrence().getEvent()));
		}
		
		eventEditor.setReadOnly(true);
		eventEditor.setMode(AceMode.java);
		eventEditor.setTheme(AceTheme.eclipse);
		eventEditor.setHeight(500, Unit.PIXELS);
		eventEditor.setWidth("100%");
		
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

		HorizontalLayout formLayout = new HorizontalLayout();
		formLayout.setWidth("100%");
		formLayout.setHeight(200, Unit.PIXELS);
		formLayout.addComponent(layout);
		wrapperLayout.addComponent(formLayout, 0, 0);
				
		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		
		VerticalLayout h1 = new VerticalLayout();
		h1.setSizeFull();
		h1.setMargin(true);
		h1.addComponent(wrapTextCheckBox);
		h1.addComponent(eventEditor);
		
		HorizontalLayout h2 = new HorizontalLayout();
		h2.setSizeFull();
		h2.setMargin(true);
		h2.addComponent(errorDetailEditor);
		
		HorizontalLayout h3 = new HorizontalLayout();
		h3.setSizeFull();
		h3.setMargin(true);
		h3.addComponent(errorMessageEditor);
		
		tabsheet.addTab(h3, "Error Message");
		tabsheet.addTab(h2, "Error Details");
		tabsheet.addTab(h1, "Message Data");
		tabsheet.addTab(createCommentsTabsheet(), "Notes / Links");
		
		wrapperLayout.addComponent(tabsheet, 0, 1);

		errorOccurrenceDetailsPanel.setContent(wrapperLayout);
		return errorOccurrenceDetailsPanel;
	}
	
	protected void closeAssociatedErrorOccurence()
	{
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
    	
    	ArrayList<String> uris = new ArrayList<String>();
    	
    	uris.add(this.categorisedErrorOccurrence.getErrorOccurrence().getUri());    	
    	
    	errorReportingManagementService.close(uris, "This error was automatically closed as the associated excluded event has been actioned."
    			, authentication.getName());
	}
	
	protected Layout createCommentsTabsheet()
	{
		List<ErrorOccurrenceNote> notes = errorReportingManagementService.getErrorOccurrenceNotesByErrorUri(this.categorisedErrorOccurrence.getErrorOccurrence().getUri());
		
		final GridLayout layout = new GridLayout();
		layout.setSpacing(true);
		layout.setWidth("100%");
		
		final Button commentButton = new Button("Comment");
		commentButton.addStyleName(ValoTheme.BUTTON_SMALL);
		commentButton.setImmediate(true);
		commentButton.setDescription("Comment on the below errors.");
		
		HorizontalLayout commentButtonLayout = new HorizontalLayout();
		commentButtonLayout.setSpacing(true);
		commentButtonLayout.addComponent(commentButton);
		
		layout.addComponent(commentButtonLayout);
		
		commentButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	final TextArea tf1 = new TextArea();
        		tf1.addValidator(new NonZeroLengthStringValidator("You must enter a comment!"));
        		tf1.setRows(5);
        		tf1.setReadOnly(false);
        		tf1.setWidth("100%");
        		tf1.setValidationVisible(false);
        		layout.removeAllComponents();
        		layout.addComponent(tf1);
            	
        		final Button saveButton = new Button("Save");
        		saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        		saveButton.setImmediate(true);
        		saveButton.setDescription("Save the comment");
        		
        		saveButton.addClickListener(new Button.ClickListener() 
                {
                    public void buttonClick(ClickEvent event) 
                    {
                    	try
                    	{
                    		tf1.validate();
                    	}
                    	catch (InvalidValueException e)
                    	{
                    		tf1.setValidationVisible(true);
                    		return;
                    	}
                    	
                    	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
        			        	.getAttribute(DashboardSessionValueConstants.USER);
                    	
                    	ArrayList<String> uris = new ArrayList<String>();
        
                    	uris.add(categorisedErrorOccurrence.getErrorOccurrence().getUri());
        
                    	errorReportingManagementService.update(uris, tf1.getValue(), authentication.getName());
                    	
                    	layout.removeAllComponents();                    	
                    	layout.addComponent(commentButton);
                    	
                    	updateNotes(layout);
                    }
                });
        		
        		final Button cancelButton = new Button("Cancel");
        		cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        		cancelButton.setImmediate(true);
        		
        		cancelButton.addClickListener(new Button.ClickListener() 
                {
                    public void buttonClick(ClickEvent event) 
                    {
                    	layout.removeAllComponents();
                    	layout.addComponent(commentButton);
                    	
                    	updateNotes(layout);
                    }
                });
        		
        		HorizontalLayout buttonLayout = new HorizontalLayout();
        		buttonLayout.setSpacing(true);
        		
        		buttonLayout.addComponent(saveButton);
        		buttonLayout.addComponent(cancelButton);
        		
        		layout.addComponent(buttonLayout);
        		
        		updateNotes(layout);
            }
        });
		
		this.updateNotes(layout);		
		
		return layout;
	}
	
	protected Layout updateNotes(Layout layout)
	{
		List<ErrorOccurrenceNote> notes = errorReportingManagementService.getErrorOccurrenceNotesByErrorUri(this.categorisedErrorOccurrence.getErrorOccurrence().getUri());		
		
		for(ErrorOccurrenceNote note: notes)
		{
			Label whoLabel = new Label(new Date(note.getNote().getTimestamp()) + ": " + note.getNote().getUser() + " wrote: ");
			whoLabel.setWidth("100%");
			whoLabel.setValue(new Date(note.getNote().getTimestamp()) + ": " + note.getNote().getUser() + " wrote: ");
			
			layout.addComponent(whoLabel);
			
			// Initialize Rendering components and allow getting instances
			EmbeddableComponentManager componentManager = new EmbeddableComponentManager();
			componentManager.initialize(this.getClass().getClassLoader());
			
			Converter converter;
			try
			{
				converter = componentManager.getInstance(Converter.class);
				
				// Convert input in XWiki Syntax 2.1 into XHTML. The result is stored in the printer.
				WikiPrinter printer = new DefaultWikiPrinter();
				converter.convert(new StringReader(note.getNote().getNote()), Syntax.XWIKI_2_1, Syntax.XHTML_1_0, printer);
				
				Label l = new Label(printer.toString(), ContentMode.HTML);
				l.setWidth("100%");
				
				layout.addComponent(l);
			} 
			catch (Exception e)
			{
				Notification.show("An error has occurred trying to render wiki test content: " + e.getMessage(), Type.ERROR_MESSAGE);
			} 
			
			layout.addComponent(new Label("<hr />",ContentMode.HTML));
		}
		
		
		return layout;
	}
}
