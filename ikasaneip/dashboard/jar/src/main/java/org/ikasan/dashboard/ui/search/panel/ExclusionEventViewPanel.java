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
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.hospital.service.HospitalManagementService;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ExclusionEventViewPanel extends Panel
{
	private static Logger logger = LoggerFactory.getLogger(ExclusionEventViewPanel.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField roleName;
	private TextField roleDescription;
	private ExclusionEvent exclusionEvent;
	private ErrorOccurrence errorOccurrence;
	private ExclusionEventAction action;
	private HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService;
	private TopologyService topologyService;
	private ErrorReportingManagementService errorReportingManagementService;
	private HospitalService<byte[]> hospitalService;
	private TextArea comments;


	public ExclusionEventViewPanel(ExclusionEvent exclusionEvent, ErrorOccurrence errorOccurrence, ExclusionEventAction action,
                                   HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService, TopologyService topologyService,
                                   ErrorReportingManagementService errorReportingManagementService, HospitalService<byte[]> hospitalService)
	{
		super();
		this.exclusionEvent = exclusionEvent;
		this.errorOccurrence = errorOccurrence;
		this.action = action;
		this.hospitalManagementService = hospitalManagementService;
		this.topologyService = topologyService;
		this.errorReportingManagementService = errorReportingManagementService;
		this.hospitalService = hospitalService;
		
		this.init();
	}


	public void init()
	{
		this.setSizeFull();
		
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
		if(this.exclusionEvent != null)
		{
			tf1.setValue(this.exclusionEvent.getModuleName());
		}
		else
		{
			tf1.setValue(this.action.getModuleName());
		}
		tf1.setReadOnly(true);
		tf1.setWidth("100%");
		layout.addComponent(tf1, 1, 1);
		
		label = new Label("Flow Name:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 2);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf2 = new TextField();
		if(this.exclusionEvent != null)
		{
			tf2.setValue(this.exclusionEvent.getFlowName());
		}
		else
		{
			tf2.setValue(this.action.getFlowName());
		}
		tf2.setReadOnly(true);
		tf2.setWidth("100%");
		layout.addComponent(tf2, 1, 2);
		
		label = new Label("Event Id:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 3);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.exclusionEvent.getIdentifier());
		tf3.setReadOnly(true);
		tf3.setWidth("100%");
		layout.addComponent(tf3, 1, 3);
		
		label = new Label("Date/Time:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 4);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		Date date = null;
		if(this.exclusionEvent != null)
		{
			date = new Date(this.exclusionEvent.getTimestamp());
		}
		else
		{
			date = new Date(this.action.getTimestamp());
		}
		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
	    String timestamp = format.format(date);
	    
		TextField tf4 = new TextField();
		tf4.setValue(timestamp);
		tf4.setReadOnly(true);
		tf4.setWidth("100%");
		layout.addComponent(tf4, 1, 4);
		
		label = new Label("Error URI:"); 
		label.setSizeUndefined();
		layout.addComponent(label, 0, 5);
		layout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
		
		TextField tf5 = new TextField();
		if(exclusionEvent != null)
		{
			tf5.setValue(exclusionEvent.getErrorUri());
		}
		else
		{
			tf5.setValue(action.getErrorUri());
		}
		tf5.setReadOnly(true);
		tf5.setWidth("100%");
		layout.addComponent(tf5, 1, 5);
		
		final AceEditor eventEditor = new AceEditor();
		eventEditor.setCaption("Event Payload");

		
		if(this.exclusionEvent != null && this.exclusionEvent.getEvent() != null)
		{
			eventEditor.setValue(new String((byte[])this.exclusionEvent.getEvent()));
		}
		else if(this.action != null && this.action.getEvent() != null)
		{
			eventEditor.setValue(new String((byte[])this.action.getEvent()));
		}
		
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

		if(this.errorOccurrence != null)
		{
			errorEditor.setValue(this.errorOccurrence.getErrorDetail());
		}
		else
		{
			errorEditor.setValue("Error details not available.");
		}
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
		formLayout.setHeight(220, Unit.PIXELS);
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
