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
package org.ikasan.dashboard.ui.search.panel;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.replay.model.BulkReplayResponse;
import org.ikasan.replay.model.HibernateReplayAuditEvent;
import org.ikasan.replay.model.ReplayResponse;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.replay.ReplayService;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;
import org.vaadin.teemu.VaadinIcons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ReplayEventViewPanel extends Panel
{
	private Logger logger = LoggerFactory.getLogger(ReplayEventViewPanel.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private ReplayEvent replayEvent;
	private ReplayService<ReplayEvent, HibernateReplayAuditEvent, ReplayResponse, BulkReplayResponse> replayService;
	private PlatformConfigurationService platformConfigurationService;
	private TopologyService topologyService;

	/**
	 * Constructor
	 *  
	 * @param replayEvent
	 * @param replayService
	 * @param platformConfigurationService
	 */
	public ReplayEventViewPanel(ReplayEvent replayEvent, ReplayService<ReplayEvent, HibernateReplayAuditEvent, ReplayResponse, BulkReplayResponse> replayService,
								PlatformConfigurationService platformConfigurationService,
								TopologyService topologyService)
	{
		super();
		this.replayEvent = replayEvent;
		this.replayService = replayService;
		this.platformConfigurationService = platformConfigurationService;
		this.topologyService = topologyService;
		
		this.init();
	}


	public void init()
	{
		this.setSizeFull();
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		
		layout.addComponent( createReplayEventDetailsPanel());
		
		
		this.setContent(layout);
	}

	protected Panel createReplayEventDetailsPanel()
	{
		Panel errorOccurrenceDetailsPanel = new Panel();
		errorOccurrenceDetailsPanel.setSizeFull();
		errorOccurrenceDetailsPanel.setStyleName("dashboard");
		
		GridLayout layout = new GridLayout(4, 7);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, 0.1f);
		layout.setColumnExpandRatio(1, 0.4f);
		layout.setColumnExpandRatio(2, 0.1f);
		layout.setColumnExpandRatio(3, 0.4f);
		
		Label wiretapDetailsLabel = new Label("Replay Event Details");
		wiretapDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(wiretapDetailsLabel);
		
		
		Label moduleNameLabel = new Label("Module Name:");
		moduleNameLabel.setSizeUndefined();
		
		layout.addComponent(moduleNameLabel, 0, 1);
		layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleName = new TextField();
		moduleName.setValue(this.replayEvent.getModuleName());
		moduleName.setReadOnly(true);
		moduleName.setWidth("80%");
		layout.addComponent(moduleName, 1, 1);
		
		Label flowNameLabel = new Label("Flow Name:");
		flowNameLabel.setSizeUndefined();
		
		layout.addComponent(flowNameLabel, 0, 2);
		layout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.replayEvent.getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 2);
		
		
		Label dateTimeLabel = new Label("Date/Time:");
		dateTimeLabel.setSizeUndefined();
		
		layout.addComponent(dateTimeLabel, 0, 4);
		layout.setComponentAlignment(dateTimeLabel, Alignment.MIDDLE_RIGHT);
		
		Date date = new Date(this.replayEvent.getTimestamp());
		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
	    String timestamp = format.format(date);
	    
		TextField tf4 = new TextField();
		tf4.setValue(timestamp);
		tf4.setReadOnly(true);
		tf4.setWidth("80%");
		layout.addComponent(tf4, 1, 4);
		
		
		Label eventIdLabel = new Label("Event Id:");
		eventIdLabel.setSizeUndefined();
		
		layout.addComponent(eventIdLabel, 0, 5);
		layout.setComponentAlignment(eventIdLabel, Alignment.MIDDLE_RIGHT);
		
		TextField tf5 = new TextField();
		tf5.setValue(replayEvent.getEventId());
		tf5.setReadOnly(true);
		tf5.setWidth("80%");
		layout.addComponent(tf5, 1, 5);

		Label commentLabel = new Label("Comment:");
		commentLabel.setSizeUndefined();

		layout.addComponent(commentLabel, 2, 1);
		layout.setComponentAlignment(commentLabel, Alignment.MIDDLE_RIGHT);

		TextArea commentTextArea = new TextArea();
		commentTextArea.setWidth("80%");
		commentTextArea.setRows(6);
		commentTextArea.setRequired(true);
		commentTextArea.setRequiredError("A comment is required in order to submit.");
		commentTextArea.setValidationVisible(false);

		layout.addComponent(commentTextArea, 3, 1, 3, 5);
		layout.setComponentAlignment(commentLabel, Alignment.MIDDLE_RIGHT);

		
		final Button replayButton = new Button("Replay");

		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute(DashboardSessionValueConstants.USER);

		final ArrayList<ReplayEvent> replayEvents = new ArrayList<>();
		replayEvents.add(replayEvent);

		replayButton.addClickListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				try
				{
					commentTextArea.setValidationVisible(false);
					commentTextArea.validate();
				}
				catch (Exception e)
				{
					commentTextArea.setValidationVisible(true);
					return;
				}

				replayButton.setVisible(false);

				ReplayResponse replayResponse = null;
				try
				{
					Module module = topologyService.getModuleByName(replayEvent.getModuleName());
					String targetServer = module.getServer().getUrl() + ":" + module.getServer().getPort();

					replayResponse = replayService.replay(targetServer, replayEvent, authentication.getName(),
							(String)authentication.getCredentials(), authentication.getName(),commentTextArea.getValue(),
                            getModuleContextMappings().get(replayEvent.getModuleName()));
				}
				catch (RuntimeException e)
				{
					Notification.show("An error occurred replaying event: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);

					return;
				}

				if(!replayResponse.isSuccess())
				{
					Notification.show("Failed to replay event: " + replayResponse.getResponseBody(), Notification.Type.ERROR_MESSAGE);
				}
				else
				{
					Notification.show("Event replay complete.");
				}
			}
		});

		if(authentication != null && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
				authentication.hasGrantedAuthority(SecurityConstants.REPLAY_ADMIN)
				|| authentication.hasGrantedAuthority(SecurityConstants.REPLAY_WRITE)
				|| authentication.hasGrantedAuthority(SecurityConstants.SEARCH_REPLAY_ADMIN)
				|| authentication.hasGrantedAuthority(SecurityConstants.SEARCH_REPLAY_WRITE)))
		{
			replayButton.setVisible(true);
		}
		else
		{
			replayButton.setVisible(false);
		}
        
        layout.addComponent(replayButton, 0, 6, 3, 6);
		layout.setComponentAlignment(replayButton, Alignment.MIDDLE_CENTER);
		
		GridLayout wrapperLayout = new GridLayout(2, 4);
		wrapperLayout.setWidth("100%");
		
		final AceEditor editor = new AceEditor();
		editor.setCaption("Event");
		if(this.replayEvent.getEventAsString() != null)
		{
			editor.setValue(this.replayEvent.getEventAsString());
		}
		else if(this.replayEvent.getEvent() != null)
		{
			editor.setValue(new String(this.replayEvent.getEvent()));
		}
		editor.setReadOnly(true);
		editor.setMode(AceMode.xml);
		editor.setWordWrap(true);
		editor.setTheme(AceTheme.eclipse);
		editor.setWidth("100%");
		editor.setHeight(550, Unit.PIXELS);
		
		CheckBox wrapTextCheckBox = new CheckBox("Wrap text");
		wrapTextCheckBox.addValueChangeListener(new Property.ValueChangeListener() 
		{
            @Override
            public void valueChange(ValueChangeEvent event)
            {
                Object value = event.getProperty().getValue();
                boolean isCheck = (null == value) ? false : (Boolean) value;
               
                editor.setWordWrap(isCheck);
            }
        });
		
		Button downloadButton = new Button();
		FileDownloader fd = new FileDownloader(this.getPayloadDownloadStream());
        fd.extend(downloadButton);

        downloadButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
        downloadButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        downloadButton.setDescription("Download the payload");
        downloadButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		
		wrapTextCheckBox.setValue(true);

		wrapperLayout.addComponent(layout, 0, 0, 1, 0);
		wrapperLayout.addComponent(wrapTextCheckBox, 0, 1);
		wrapperLayout.addComponent(downloadButton, 1, 1);
		wrapperLayout.setComponentAlignment(downloadButton, Alignment.MIDDLE_RIGHT);
		wrapperLayout.addComponent(editor, 0, 2, 1, 2);
		wrapperLayout.setComponentAlignment(editor, Alignment.TOP_LEFT);

		errorOccurrenceDetailsPanel.setContent(wrapperLayout);
		return errorOccurrenceDetailsPanel;
	}
	
	/**
     * Helper method to get the stream associated with the export of the file.
     * 
     * @return the StreamResource associated with the export.
     */
    private StreamResource getPayloadDownloadStream() 
    {
		StreamResource.StreamSource source = new StreamResource.StreamSource() 
		{
		    public InputStream getStream() {
		        ByteArrayOutputStream stream = null;
		        try
		        {
		            stream = getPayloadStream();
		        }
		        catch (IOException e)
		        {
		        	logger.error(e.getMessage(), e);
		        }
		        InputStream input = new ByteArrayInputStream(stream.toByteArray());
		        return input;
		
		    }
		};
            
	    StreamResource resource = new StreamResource ( source,"payload.txt");
	    return resource;
    }

    private Map<String, String> getModuleContextMappings()
    {
        HashMap<String, String> moduleContextMappings = new HashMap<>();

        List<Module> modules = this.topologyService.getAllModules();

        for(Module module: modules)
        {
            moduleContextMappings.put(module.getName(), module.getContextRoot());
        }

        return moduleContextMappings;
    }
    
    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     * 
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getPayloadStream() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(replayEvent.getEvent());

        return out;
    }
}
