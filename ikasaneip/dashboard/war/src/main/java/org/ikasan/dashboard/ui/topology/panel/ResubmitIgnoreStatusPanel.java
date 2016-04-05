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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.hospital.service.HospitalService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ResubmitIgnoreStatusPanel extends Panel
{
	private Logger logger = Logger.getLogger(ResubmitIgnoreStatusPanel.class);
	
	public static final String RESUBMIT = "resubmit";
	public static final String IGNORE = "ignore";
	
	private List<ExclusionEvent> exclusionEvents;
	
	private TopologyService topologyService;
	
	private ErrorReportingService errorReportingService;
	
	private ErrorReportingManagementService errorReportingManagementService;
	
	private ExclusionManagementService<ExclusionEvent, String> exclusionManagementService;
	
	private IndexedContainer tableContainer;
	
	private FilterTable replayEventsTable;
	
	private Container container;
	private String action;
	
	private IkasanAuthentication authentication;
	
	private TextArea comments;
	
	private ProgressBar bar = new ProgressBar(0.0f);
	
	private int count;
	
	private HospitalService<byte[]> hospitalService;	
	
	private boolean cancelled = false;
	
	public ResubmitIgnoreStatusPanel(List<ExclusionEvent> replayEvents,
			TopologyService topologyService, ErrorReportingService errorReportingService,
			ErrorReportingManagementService errorReportingManagementService, 
			ExclusionManagementService<ExclusionEvent, String> exclusionManagementService,
			Container container, String action, HospitalService<byte[]> hospitalService) 
	{
		super();
		
		this.exclusionEvents = replayEvents;
		if(this.exclusionEvents == null)
		{
			throw new IllegalArgumentException("replayEvents cannot be null!");
		}
		this.topologyService = topologyService;
		if(this.topologyService == null)
		{
			throw new IllegalArgumentException("topologyService cannot be null!");
		}
		this.errorReportingService = errorReportingService;
		if(this.errorReportingService == null)
		{
			throw new IllegalArgumentException("errorReportingService cannot be null!");
		}
		this.errorReportingManagementService = errorReportingManagementService;
		if(this.errorReportingManagementService == null)
		{
			throw new IllegalArgumentException("errorReportingManagementService cannot be null!");
		}
		this.exclusionManagementService = exclusionManagementService;
		if(this.exclusionManagementService == null)
		{
			throw new IllegalArgumentException("exclusionManagementService cannot be null!");
		}
		this.container = container;
		if(this.container == null)
		{
			throw new IllegalArgumentException("container cannot be null!");
		}
		this.action = action;
		if(this.exclusionManagementService == null)
		{
			throw new IllegalArgumentException("action cannot be null!");
		}
		this.hospitalService = hospitalService;
		if(this.hospitalService == null)
		{
			throw new IllegalArgumentException("hospitalService cannot be null!");
		}
		
		init();
	}
	
	protected IndexedContainer buildContainer() 
	{			
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Error URI", String.class,  null);
		cont.addContainerProperty("Error Message", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		cont.addContainerProperty("", Label.class,  null);
		
        return cont;
    }

	public void init()
	{
		this.setSizeFull();
		
		this.authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		GridLayout formLayout = new GridLayout(2, 6);
		formLayout.setSizeFull();
		formLayout.setSpacing(true);
		formLayout.setColumnExpandRatio(0, 0.2f);
		formLayout.setColumnExpandRatio(1, 0.8f);
		
		Label actionLabel; 
		
		if(this.action.equals(RESUBMIT))
		{
			actionLabel = new Label("Resubmit Exclusions");
		}
		else
		{
			actionLabel = new Label("Ignore Exclusions");
		}
				
		actionLabel.setStyleName(ValoTheme.LABEL_HUGE);
		formLayout.addComponent(actionLabel);
		
		
		Label eventLabel = new Label("Number of exclusion events to action:");
		eventLabel.setSizeUndefined();
		
		formLayout.addComponent(eventLabel, 0, 1);
		formLayout.setComponentAlignment(eventLabel, Alignment.MIDDLE_RIGHT);
		
		TextField exclusionCount = new TextField();
		
		if(this.exclusionEvents != null)
		{
			exclusionCount.setValue(Integer.toString(this.exclusionEvents.size()));
		}
		else
		{
			exclusionCount.setValue("0");
		}
		
		exclusionCount.setReadOnly(true);
		exclusionCount.setWidth("80%");
		formLayout.addComponent(exclusionCount, 1, 1);
		
		
		Label targetServerLabel = new Label("Comment:");
		targetServerLabel.setSizeUndefined();
		
		formLayout.addComponent(targetServerLabel, 0, 2);
		formLayout.setComponentAlignment(targetServerLabel, Alignment.TOP_RIGHT);
		
		comments = new TextArea();
		comments.setWidth("80%");
		comments.setRows(4);
		comments.setRequired(true);
		comments.addValidator(new StringLengthValidator(
	            "You must supply a comment!", 1, 2048, false));
		comments.setValidationVisible(false);         
		comments.setRequiredError("A comment is required!");
		comments.setNullSettingAllowed(false);
		
		formLayout.addComponent(comments, 1, 2);
		
		final Button resubmitButton = new Button("Resubmit");
		resubmitButton.addStyleName(ValoTheme.BUTTON_SMALL);
		resubmitButton.setImmediate(true);
		resubmitButton.setDescription("Resubmit all exclusions.");
		
		final Button ignoreButton = new Button("Ignore");
		ignoreButton.addStyleName(ValoTheme.BUTTON_SMALL);
		ignoreButton.setImmediate(true);
		ignoreButton.setDescription("Ignore all exclusions.");
		
		final Button cancelButton = new Button("Cancel");
		cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
		cancelButton.setImmediate(true);
		cancelButton.setDescription("Cancel action!");
		cancelButton.setVisible(false);
		
		final ExecutorService executorService = Executors
    			.newSingleThreadExecutor();
		
		resubmitButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	
            	try 
            	{
            		comments.validate();
                } 
                catch (Exception e) 
                {
                	comments.setValidationVisible(true);                	
                	comments.markAsDirty();
                    return;
                }
                
            	bar.setVisible(true);
            	resubmitButton.setVisible(false);
            	ignoreButton.setVisible(false);
            	cancelButton.setVisible(true);
            	            	
            	try
            	{
	            	executorService.execute(new Runnable()
	    			{
	    				@Override
	    				public void run() 
	    				{
	    					resubmitExcludedEvents();
	    					bar.setVisible(false);
	    				}
	    			});
            	}
            	finally
            	{
            		executorService.shutdown();
            	}
            }
        });
		
		ignoreButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	try 
            	{
            		comments.validate();
                } 
                catch (Exception e) 
                {
                	comments.setValidationVisible(true);                	
                	comments.markAsDirty();
                    return;
                }
            	
            	bar.setVisible(true);
            	resubmitButton.setVisible(false);
            	ignoreButton.setVisible(false);
            	cancelButton.setVisible(true);
            	            	
            	try
            	{
	            	executorService.execute(new Runnable()
	    			{
	    				@Override
	    				public void run() 
	    				{
	    					ignoreExcludedEvents();
	    					bar.setVisible(false);
	    				}
	    			});
            	}
            	finally
            	{
            		executorService.shutdown();
            	}
            }
        });
		
		cancelButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	cancelled = true;
            	
            	executorService.shutdown();
            	
            	bar.setVisible(false);
            	cancelButton.setVisible(false);
				
				Notification.show("Action cancelled!");
            }
        });
		
		if(this.action.equals(RESUBMIT))
		{
			
			GridLayout buttonsLayout = new GridLayout(2, 1);
			buttonsLayout.addComponent(resubmitButton);
			buttonsLayout.addComponent(cancelButton);
			
			formLayout.addComponent(buttonsLayout, 0, 3, 1, 3);
			formLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_CENTER);
		}
		else
		{
			GridLayout buttonsLayout = new GridLayout(2, 1);
			buttonsLayout.addComponent(ignoreButton);
			buttonsLayout.addComponent(cancelButton);
			
			formLayout.addComponent(buttonsLayout, 0, 3, 1, 3);
			formLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_CENTER);
		}
		
		this.bar.setWidth("40%");	
		this.bar.setImmediate(true);
		this.bar.setIndeterminate(true);
		this.bar.setVisible(false);
		
		formLayout.addComponent(bar, 0, 4, 1, 4);
		formLayout.setComponentAlignment(bar, Alignment.MIDDLE_CENTER);
		
		this.replayEventsTable = new FilterTable();
		this.replayEventsTable.setFilterBarVisible(true);
		this.replayEventsTable.setWidth("100%");
		this.replayEventsTable.setHeight("600px");
		this.replayEventsTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.replayEventsTable.addStyleName("ikasan");
		
		this.replayEventsTable.setColumnExpandRatio("Module Name", .14f);
		this.replayEventsTable.setColumnExpandRatio("Flow Name", .18f);
		this.replayEventsTable.setColumnExpandRatio("Error URI", .18f);
		this.replayEventsTable.setColumnExpandRatio("Event Id / Payload Id", .33f);
		this.replayEventsTable.setColumnExpandRatio("Timestamp", .1f);
		this.replayEventsTable.setColumnExpandRatio("", .05f);
		
		this.replayEventsTable.addStyleName("wordwrap-table");
		this.replayEventsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.replayEventsTable.setPageLength(100);
		
		tableContainer = this.buildContainer();
		this.replayEventsTable.setContainerDataSource(tableContainer);
		
		this.populateTable();
		
		GridLayout layout = new GridLayout(1, 2);
		layout.setWidth("100%");
		layout.setMargin(true);
		
		layout.addComponent(formLayout);
		layout.addComponent(this.replayEventsTable);
		
		this.setContent(layout);
	}
	
	protected void populateTable()
	{
		for(final ExclusionEvent exclusionEvent: exclusionEvents)
    	{
    		Date date = new Date(exclusionEvent.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    final ErrorOccurrence errorOccurrence = (ErrorOccurrence)errorReportingService.find(exclusionEvent.getErrorUri());
    	    
    	    Item item = this.tableContainer.addItem(exclusionEvent);			            	    
    	    
    	    item.getItemProperty("Module Name").setValue(exclusionEvent.getModuleName());
			item.getItemProperty("Flow Name").setValue(exclusionEvent.getFlowName());
			item.getItemProperty("Error URI").setValue(exclusionEvent.getErrorUri());
			
			if(errorOccurrence != null && errorOccurrence.getErrorMessage() != null)
			{
				if(errorOccurrence.getErrorMessage().length() > 500)
				{
					item.getItemProperty("Error Message").setValue(errorOccurrence.getErrorMessage().substring(0, 500));
				}
				else
				{
					item.getItemProperty("Error Message").setValue(errorOccurrence.getErrorMessage());
				}
			}
			
			item.getItemProperty("Timestamp").setValue(timestamp);    	    	    	    
    	}
	}
	
	/**
	 * Helper method to ignore all selected excluded events.
	 */
	protected void ignoreExcludedEvents()
	{
		List<String> uris = new ArrayList<String>();
		
		count = 0;
		
		cancelled = false;

		for(final ExclusionEvent exclusionEvent: this.exclusionEvents)
		{
			if(cancelled)
			{
				Notification.show("Events ignore cancelled.");
				return;
			}
			
			// We want to make sure that the event is still available and has not been ignored or resubmitted already.
			if(this.exclusionManagementService.find(exclusionEvent.getErrorUri()) != null)
			{	
				count++;
				
				uris.add(exclusionEvent.getErrorUri());        	
	        	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
	        	
	        	ClientConfig clientConfig = new ClientConfig();
	        	clientConfig.register(feature) ;
	        	
	        	Client client = ClientBuilder.newClient(clientConfig);
	        	
	        	Module module = topologyService.getModuleByName(exclusionEvent.getModuleName());
	        	
	        	if(module == null)
	        	{
	        		Notification.show("Error", "Unable to find server information for module we are attempting to re-submit to: " + exclusionEvent.getModuleName() 
	        				, Type.ERROR_MESSAGE);
	        		
	        		return;
	        	}
	        	
	        	this.hospitalService.ignore(module.getName(), exclusionEvent.getFlowName(), exclusionEvent.getErrorUri()
	        			, exclusionEvent.getEvent(), this.authentication);
	    	    
	    	    UI.getCurrent().access(new Runnable() 
	    		{
	                @Override
	                public void run() 
	                {
	                	VaadinSession.getCurrent().getLockInstance().lock();
	            		try 
	            		{
	            			Item item = tableContainer.getItem(exclusionEvent);
	            			item.getItemProperty("").setValue(new Label(VaadinIcons.CHECK.getHtml(), ContentMode.HTML));
	            			

	            			float current = count / exclusionEvents.size();
	        				
	        				bar.setValue(current);
	        				
	            		} 
	            		finally 
	            		{
	            			VaadinSession.getCurrent().getLockInstance().unlock();
	            		}
	                	
	                	UI.getCurrent().push();	
	                }
	            });	
			}
		}
		
		this.errorReportingManagementService.close(uris, this.comments.getValue(), this.authentication.getName());
		
		for(final ExclusionEvent exclusionEvent: this.exclusionEvents)
		{
			UI.getCurrent().access(new Runnable() 
    		{
                @Override
                public void run() 
                {
                	VaadinSession.getCurrent().getLockInstance().lock();
            		try 
            		{
            			container.removeItem(exclusionEvent);
            		} 
            		finally 
            		{
            			VaadinSession.getCurrent().getLockInstance().unlock();
            		}
                	
                	UI.getCurrent().push();	
                }
            });	
		}
		
		Notification.show("Events ignored successfully.");
	}
	
	/**
	 * Helper method to resubmit all selected excluded events.
	 */
	protected void resubmitExcludedEvents()
	{
		List<String> uris = new ArrayList<String>();

		count = 0;
		
		cancelled = false;
		
		for(final ExclusionEvent exclusionEvent: this.exclusionEvents)
		{
			if(cancelled)
			{
				Notification.show("Events resubmit cancelled.");
				return;
			}
			
			// We want to make sure that the event is still available and has not been ignored or resubmitted already.
			if(this.exclusionManagementService.find(exclusionEvent.getErrorUri()) != null)
			{	 
				count++;
				uris.add(exclusionEvent.getErrorUri());
	        	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
	        	
	        	ClientConfig clientConfig = new ClientConfig();
	        	clientConfig.register(feature) ;
	        	
	        	Client client = ClientBuilder.newClient(clientConfig);
	        	
	        	Module module = topologyService.getModuleByName(exclusionEvent.getModuleName());
	        	
	        	if(module == null)
	        	{
	        		Notification.show("Error", "Unable to find server information for module we are attempting to re-submit to: " + exclusionEvent.getModuleName() 
	        				, Type.ERROR_MESSAGE);
	        		
	        		return;
	        	}
	        	
	        	Server server = module.getServer();
	        	
	        	if(server == null)
	        	{
	        		logger.error("An error was received trying to resubmit event. " +
	        				"Unable to get server details for module: " + module); 
	    	        
	    	    	Notification.show("Error", "An error was received trying to resubmit event. " +
	    	    			"Unable to get server details for module: " + module, Type.ERROR_MESSAGE);
	    	    	return;
	        	}
	    		
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

	    	    Response response = null;
	    	    try
	    	    {
	    	    	response = webTarget.request().put(Entity.entity(exclusionEvent.getEvent(), MediaType.APPLICATION_OCTET_STREAM));
	    	    }
	    	    catch(Exception e)
	    	    {
					logger.error("An exception was received trying to ignore event", e); 
	    	        
	    	    	Notification.show("Error", "An exception was received trying to ignore event: " 
	    	    			+ e.getMessage(), Type.ERROR_MESSAGE);
	    	    }
	    	    
	    	    if(response.getStatus()  != 200)
	    	    {
	    	    	response.bufferEntity();
	    	        
	    	        String responseMessage = response.readEntity(String.class);
	    	        
	    	        logger.error("An error was received trying to resubmit event: " + responseMessage); 
	    	        
	    	    	Notification.show("Error", "An error was received trying to resubmit event: " 
	    	    			+ responseMessage, Type.ERROR_MESSAGE);
	    	    	return;
	    	    }
	    	    	    	    
	    	    UI.getCurrent().access(new Runnable() 
	    		{
	                @Override
	                public void run() 
	                {
	                	VaadinSession.getCurrent().getLockInstance().lock();
	            		try 
	            		{
	            			Item item = tableContainer.getItem(exclusionEvent);
	            			item.getItemProperty("").setValue(new Label(VaadinIcons.CHECK.getHtml(), ContentMode.HTML));
	            			
	            			replayEventsTable.setCurrentPageFirstItemId(item);
	            			
	            			
	            			float current = count / exclusionEvents.size();
	    					
	        				bar.setValue(current);      				
	            		} 
	            		finally 
	            		{
	            			VaadinSession.getCurrent().getLockInstance().unlock();
	            		}
	                	
	                	UI.getCurrent().push();	
	                }
	            });	
			}
		}
		
		this.errorReportingManagementService.close(uris, this.comments.getValue(), this.authentication.getName());
		
		for(final ExclusionEvent exclusionEvent: this.exclusionEvents)
		{
			UI.getCurrent().access(new Runnable() 
    		{
                @Override
                public void run() 
                {
                	VaadinSession.getCurrent().getLockInstance().lock();
            		try 
            		{
            			container.removeItem(exclusionEvent);
            		} 
            		finally 
            		{
            			VaadinSession.getCurrent().getLockInstance().unlock();
            		}
                	
                	UI.getCurrent().push();	
                }
            });	
		}
		
		Notification.show("Events resumitted successfully.");
    	
	}
}