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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class WiretapPayloadViewPanel extends Panel
{
	private Logger logger = LoggerFactory.getLogger(WiretapPayloadViewPanel.class);

	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField roleName;
	private TextField roleDescription;
	private WiretapEvent<String> wiretapEvent;


	/**
	 * Constructor
	 *
	 * @param wiretapEvent
     */
	public WiretapPayloadViewPanel(WiretapEvent<String> wiretapEvent)
	{
		super();
		this.wiretapEvent = wiretapEvent;
		
		this.init();
	}


	public void init()
	{
		this.setSizeFull();
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		
		layout.addComponent( createWiretapDetailsPanel());
		
		
		this.setContent(layout);
	}

	protected Panel createWiretapDetailsPanel()
	{
		Panel wiretapDetailsPanel = new Panel();
		wiretapDetailsPanel.setSizeFull();
		wiretapDetailsPanel.setStyleName("dashboard");
		
		GridLayout layout = new GridLayout(2, 6);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setColumnExpandRatio(0, 0.2f);
		layout.setColumnExpandRatio(1, 0.8f);
		
		Label wiretapDetailsLabel = new Label("Wiretap Details");
		wiretapDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(wiretapDetailsLabel);
		
		
		Label moduleNameLabel = new Label("Module Name:");
		moduleNameLabel.setSizeUndefined();
		
		layout.addComponent(moduleNameLabel, 0, 1);
		layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleName = new TextField();
		moduleName.setValue(this.wiretapEvent.getModuleName());
		moduleName.setReadOnly(true);
		moduleName.setWidth("80%");
		layout.addComponent(moduleName, 1, 1);
		
		Label flowNameLabel = new Label("Flow Name:");
		flowNameLabel.setSizeUndefined();
		
		layout.addComponent(flowNameLabel, 0, 2);
		layout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.wiretapEvent.getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 2);
		
		Label componentNameLabel = new Label("Component Name:");
		componentNameLabel.setSizeUndefined();
		
		layout.addComponent(componentNameLabel, 0, 3);
		layout.setComponentAlignment(componentNameLabel, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.wiretapEvent.getComponentName());
		tf3.setReadOnly(true);
		tf3.setWidth("80%");
		layout.addComponent(tf3, 1, 3);
		
		Label dateTimeLabel = new Label("Date/Time:");
		dateTimeLabel.setSizeUndefined();
		
		layout.addComponent(dateTimeLabel, 0, 4);
		layout.setComponentAlignment(dateTimeLabel, Alignment.MIDDLE_RIGHT);
		
		Date date = new Date(this.wiretapEvent.getTimestamp());
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
		tf5.setValue(wiretapEvent.getEventId());
		tf5.setReadOnly(true);
		tf5.setWidth("80%");
		layout.addComponent(tf5, 1, 5);
		
		GridLayout wrapperLayout = new GridLayout(2, 4);
		wrapperLayout.setWidth("100%");
		
		final AceEditor editor = new AceEditor();
		editor.setCaption("Event");
		editor.setValue(this.wiretapEvent.getEvent());
		editor.setReadOnly(true);
		editor.setMode(AceMode.xml);
		editor.setWordWrap(true);
		editor.setTheme(AceTheme.eclipse);
		editor.setWidth("100%");
		editor.setSizeFull();
		
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

		VerticalSplitPanel rightVpanel = new VerticalSplitPanel(wrapperLayout
				, editor);
		rightVpanel.setSizeFull();
		rightVpanel.setSplitPosition(250, Unit.PIXELS);
		rightVpanel.setLocked(true);

		wiretapDetailsPanel.setContent(rightVpanel);
		return wiretapDetailsPanel;
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
    
    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     * 
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getPayloadStream() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(wiretapEvent.getEvent().getBytes());

        return out;
    }
}
