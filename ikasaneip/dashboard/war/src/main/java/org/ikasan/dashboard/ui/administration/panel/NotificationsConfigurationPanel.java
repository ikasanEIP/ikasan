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
package org.ikasan.dashboard.ui.administration.panel;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.notification.NotifierService;
import org.ikasan.dashboard.notification.contentproducer.CategorisedErrorNotificationContentProducer;
import org.ikasan.dashboard.ui.administration.window.NotificationWindow;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.monitor.component.MonitorIcons;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.model.Notification;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author CMI2 Development Team
 * 
 */
public class NotificationsConfigurationPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(NotificationsConfigurationPanel.class);
	
	private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
	
	private TopologyService topologyService;
	
	private FilterTable notificationTable;
	
	private IndexedContainer container = null;
	
	private Button newButton = new Button();
	
	private NotifierService notifierService;
	
	private PlatformConfigurationService platformConfigurationService;
	
	private Label notificationIntervalLabel;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public NotificationsConfigurationPanel(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
			TopologyService topologyService, NotifierService notifierService)
	{
		super();
		this.configurationManagement = configurationManagement;
		if (this.configurationManagement == null)
		{
			throw new IllegalArgumentException("configurationService cannot be null!");
		}
		this.topologyService = topologyService;
		if (this.topologyService == null)
		{
			throw new IllegalArgumentException("topologyService cannot be null!");
		}
		this.notifierService = notifierService;
		if (this.notifierService == null)
		{
			throw new IllegalArgumentException("notifierService cannot be null!");
		}
		
		init();
	}

	@SuppressWarnings("deprecation")
	protected void init()
	{
		container = buildContainer();
		this.notificationTable = new FilterTable();
		this.notificationTable.setFilterBarVisible(true);
		this.notificationTable.setSizeFull();
		this.notificationTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.notificationTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.notificationTable.addStyleName("ikasan");
		this.notificationTable.setContainerDataSource(container);		
		this.notificationTable.addStyleName("wordwrap-table");
		
		this.notificationTable.setColumnExpandRatio("Notification Name", .32f);
		this.notificationTable.setColumnExpandRatio("Notification Context", .32f);
		this.notificationTable.setColumnExpandRatio("Associated Filter", .32f);
		this.notificationTable.setColumnExpandRatio("", .04f);
		
		this.notificationTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if (itemClickEvent.isDoubleClick())
		    	{
		    		final NotificationWindow notificationWindow = new NotificationWindow(topologyService, configurationManagement,
		    				(Notification)itemClickEvent.getItemId());
	                UI.getCurrent().addWindow(notificationWindow);
	                
	                notificationWindow.addCloseListener(new CloseListener()
					{
						@Override
						public void windowClose(CloseEvent e)
						{
							refresh();
						}
					});
		    	}
		    }
		});
		
		final GridLayout layout = new GridLayout(2, 5);
        layout.setWidth("100%");
        layout.setSpacing(true);
        layout.setMargin(true);
        
        layout.setColumnExpandRatio(0, .10f);
        layout.setColumnExpandRatio(1, .9f);
        
        
        Label configLabel = new Label("Notifications");
		configLabel.addStyleName(ValoTheme.LABEL_HUGE);
		configLabel.setSizeUndefined();
		
		this.newButton.setIcon(VaadinIcons.PLUS);
		this.newButton.setDescription("Create a new filter");
    	this.newButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    	this.newButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);	
    	this.newButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	final NotificationWindow notificationWindow = new NotificationWindow(topologyService, configurationManagement);
                UI.getCurrent().addWindow(notificationWindow);
                
                notificationWindow.addCloseListener(new CloseListener()
				{
					@Override
					public void windowClose(CloseEvent e)
					{
						refresh();
					}
				});
            }
        });

		layout.addComponent(configLabel, 0, 0);
		layout.addComponent(this.newButton, 1, 0);
		layout.setComponentAlignment(this.newButton, Alignment.MIDDLE_LEFT);
		
		
		Label statusLabel = new Label("Status:");
		statusLabel.addStyleName(ValoTheme.LABEL_LARGE);
		statusLabel.setSizeUndefined();
		
		final MonitorIcons runningIcon = MonitorIcons.CHECK_CIRCLE_O;
    	runningIcon.setSizePixels(20);
    	runningIcon.setColor("green");
    	
    	final MonitorIcons stoppedIcon = MonitorIcons.EXCLAMATION_CIRCLE_O;
    	stoppedIcon.setSizePixels(20);
    	stoppedIcon.setColor("red");
    	
    	final Label statusIconLabel = new Label();
		statusIconLabel.setCaptionAsHtml(true);    	
		
		
		if(this.notifierService.getState().equals(NotifierService.STATE_RUNNING))
		{
			statusIconLabel.setCaption(runningIcon.getHtml());
		}
		else
		{
			statusIconLabel.setCaption(stoppedIcon.getHtml());
		}
		
		
		layout.addComponent(statusLabel, 0, 1);
		layout.addComponent(statusIconLabel, 1, 1);
		layout.setComponentAlignment(statusIconLabel, Alignment.MIDDLE_LEFT);
		
		Label notificationIntervalMinutesLabel = new Label("Notification Interval (minutes):");
		notificationIntervalMinutesLabel.addStyleName(ValoTheme.LABEL_LARGE);
		notificationIntervalMinutesLabel.setSizeUndefined();
		
		notificationIntervalLabel = new Label();
		statusLabel.addStyleName(ValoTheme.LABEL_LARGE);
		statusLabel.setSizeUndefined();
		
		layout.addComponent(notificationIntervalMinutesLabel, 0, 2);
		layout.addComponent(notificationIntervalLabel, 1, 2);
		layout.setComponentAlignment(statusIconLabel, Alignment.MIDDLE_LEFT);
		
		Label controlLabel = new Label("Control:");
		controlLabel.addStyleName(ValoTheme.LABEL_LARGE);
		controlLabel.setSizeUndefined();
		
		final Button startButton = new Button("Start");
		startButton.setDescription("Start the notifier");
		startButton.addStyleName(ValoTheme.BUTTON_SMALL);
		
		final Button stopButton = new Button("Stop");
		stopButton.setDescription("Start the notifier");
		stopButton.addStyleName(ValoTheme.BUTTON_SMALL);
		
		startButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	notifierService.start();
            	layout.removeComponent(startButton);
            	layout.addComponent(stopButton, 1, 3);
    			layout.setComponentAlignment(stopButton, Alignment.MIDDLE_LEFT);
    			
    			statusIconLabel.setCaption(runningIcon.getHtml());
    			
    			notificationIntervalLabel.setCaption(Integer.toString(notifierService.getNotificationInterval()));
            }
        });
		
		
		stopButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	notifierService.stop();
            	
            	layout.removeComponent(stopButton);
            	layout.addComponent(startButton, 1, 3);
    			layout.setComponentAlignment(startButton, Alignment.MIDDLE_LEFT);
    			
    			statusIconLabel.setCaption(stoppedIcon.getHtml());
            }
        });
		
		if(this.notifierService.getState().equals(NotifierService.STATE_RUNNING))
		{
			layout.addComponent(controlLabel, 0, 3);
			layout.addComponent(stopButton, 1, 3);
			layout.setComponentAlignment(stopButton, Alignment.MIDDLE_LEFT);
		}
		else
		{
			layout.addComponent(controlLabel, 0, 3);
			layout.addComponent(startButton, 1, 3);
			layout.setComponentAlignment(startButton, Alignment.MIDDLE_LEFT);
		}
		
		layout.addComponent(this.notificationTable, 0, 4, 1, 4);
		
		this.setContent(layout);
	}
	
	protected IndexedContainer buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Notification Name", String.class,  null);
		cont.addContainerProperty("Notification Context", String.class,  null);
		cont.addContainerProperty("Associated Filter", String.class,  null);
		cont.addContainerProperty("", Button.class,  null);

        return cont;
    }
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
	 * .ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		refresh();
	}
	
	private void refresh()
	{
		this.container.removeAllItems();
		
		List<Notification> notifications = this.topologyService.getAllNotifications();
		
		for(final Notification notification: notifications)
		{
			Item item = container.addItem(notification);			            	    
 	    
		 	item.getItemProperty("Notification Name").setValue(notification.getName());
			item.getItemProperty("Notification Context").setValue(notification.getContext());
			item.getItemProperty("Associated Filter").setValue(notification.getFilter().getName());
			
			Button deleteButton = new Button();
			deleteButton.setIcon(VaadinIcons.TRASH);
			deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);	
			deleteButton.setDescription("Remove Policy from this Role");
			
			// Add the delete functionality to each notification that is added
			deleteButton.addClickListener(new Button.ClickListener() 
	        {
	            public void buttonClick(ClickEvent event) 
	            {		
	            	Configuration configuration = configurationManagement
	            			.getConfiguration(new CategorisedErrorNotificationContentProducer(notification));
	            	
	            	configurationManagement.deleteConfiguration(configuration);
	            	
	            	topologyService.delete(notification);
	            	
	            	container.removeItem(notification);
	            }
	        });
			
			item.getItemProperty("").setValue(deleteButton);
		}
		
		this.notificationIntervalLabel.setCaption(Integer.toString(this.notifierService.getNotificationInterval()));
	}
	

}
