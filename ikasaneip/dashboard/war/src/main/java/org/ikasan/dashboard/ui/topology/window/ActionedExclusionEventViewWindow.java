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

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ActionedExclusionEventViewWindow extends Window
{
	private static Logger logger = Logger.getLogger(ActionedExclusionEventViewWindow.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField roleName;
	private TextField roleDescription;
	private ErrorOccurrence errorOccurrence;
	private SerialiserFactory serialiserFactory;
	private ExclusionEventAction action;
	private HospitalManagementService<ExclusionEventAction> hospitalManagementService;
	private TopologyService topologyService;

	/**
	 * @param policy
	 */
	public ActionedExclusionEventViewWindow(ErrorOccurrence errorOccurrence, SerialiserFactory serialiserFactory, ExclusionEventAction action,
			HospitalManagementService<ExclusionEventAction> hospitalManagementService, TopologyService topologyService)
	{
		super();
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
		this.setSizeFull();
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		
		layout.addComponent(createExclusionEventDetailsPanel());
			
		this.setContent(layout);
	}

	protected Panel createExclusionEventDetailsPanel()
	{
		Panel exclusionEventDetailsPanel = new Panel("Exclusion Event");
		exclusionEventDetailsPanel.setSizeFull();
		exclusionEventDetailsPanel.setStyleName("dashboard");
		
		GridLayout layout = new GridLayout(4, 6);
		layout.setWidth("100%");
		layout.setHeight(140, Unit.PIXELS);
		layout.addComponent(new Label("Module Name"), 0, 0);
		
		TextField tf1 = new TextField();
		tf1.setValue(this.action.getModuleName());
		tf1.setReadOnly(true);
		tf1.setWidth("80%");
		layout.addComponent(tf1, 1, 0);
		
		layout.addComponent(new Label("Flow Name"), 0, 1);
		
		TextField tf2 = new TextField();
		tf2.setValue(this.action.getFlowName());
		tf2.setReadOnly(true);
		tf2.setWidth("80%");
		layout.addComponent(tf2, 1, 1);
		
		layout.addComponent(new Label("Event Id"), 0, 2);
		
		TextField tf3 = new TextField();
		tf3.setValue(this.errorOccurrence.getEventLifeIdentifier());
		tf3.setReadOnly(true);
		tf3.setWidth("80%");
		layout.addComponent(tf3, 1, 2);
		
		layout.addComponent(new Label("Date/Time"), 0, 3);
		
		TextField tf4 = new TextField();
		tf4.setValue(new Date(this.action.getTimestamp()).toString());
		tf4.setReadOnly(true);
		tf4.setWidth("80%");
		layout.addComponent(tf4, 1, 3);
		
		layout.addComponent(new Label("Error URI"), 0, 4);
		
		TextField tf5 = new TextField();
		tf5.setValue(this.action.getErrorUri());
		tf5.setReadOnly(true);
		tf5.setWidth("80%");
		layout.addComponent(tf5, 1, 4);
		
		layout.addComponent(new Label("Action"), 2, 0);
		
		final TextField tf6 = new TextField();
		if(this.action != null)
		{
			tf6.setValue(action.getAction());
		}
		tf6.setReadOnly(true);
		tf6.setWidth("80%");
		layout.addComponent(tf6, 3, 0);
		
		layout.addComponent(new Label("Actioned By"), 2, 1);
		
		final TextField tf7 = new TextField();
		if(this.action != null)
		{
			tf7.setValue(action.getActionedBy());
		}
		tf7.setReadOnly(true);
		tf7.setWidth("80%");
		layout.addComponent(tf7, 3, 1);
		
		layout.addComponent(new Label("Actioned Time"), 2, 2);
		
		final TextField tf8 = new TextField();
		if(this.action != null)
		{   	    
			tf8.setValue(new Date(action.getTimestamp()).toString());
		}
		tf8.setReadOnly(true);
		tf8.setWidth("80%");
		layout.addComponent(tf8, 3, 2);
		
		
		AceEditor eventEditor = new AceEditor();
		eventEditor.setCaption("Event Payload");

		Object event = this.serialiserFactory.getDefaultSerialiser().deserialise(this.action.getEvent());
		eventEditor.setValue(event.toString());
		eventEditor.setReadOnly(true);
		eventEditor.setMode(AceMode.java);
		eventEditor.setTheme(AceTheme.eclipse);
		eventEditor.setWidth("100%");
		eventEditor.setHeight(600, Unit.PIXELS);
		
		HorizontalLayout eventEditorLayout = new HorizontalLayout();
		eventEditorLayout.setSizeFull();
		eventEditorLayout.setMargin(true);
		eventEditorLayout.addComponent(eventEditor);
		
		AceEditor errorEditor = new AceEditor();
		errorEditor.setCaption("Error Details");
		errorEditor.setValue(this.errorOccurrence.getErrorDetail());
		errorEditor.setReadOnly(true);
		errorEditor.setMode(AceMode.xml);
		errorEditor.setTheme(AceTheme.eclipse);
		errorEditor.setWidth("100%");
		errorEditor.setHeight(600, Unit.PIXELS);
		
		HorizontalLayout errorEditorLayout = new HorizontalLayout();
		errorEditorLayout.setSizeFull();
		errorEditorLayout.setMargin(true);
		errorEditorLayout.addComponent(errorEditor);

		
		VerticalSplitPanel splitPanel = new VerticalSplitPanel();
		splitPanel.setWidth("100%");
		splitPanel.setHeight(600, Unit.PIXELS);
		splitPanel.setFirstComponent(eventEditorLayout);
		splitPanel.setSecondComponent(errorEditorLayout);
		
		VerticalLayout wrapperLayout = new VerticalLayout();
		wrapperLayout.setSizeFull();
		wrapperLayout.setMargin(true);
		wrapperLayout.addComponent(layout);
		wrapperLayout.addComponent(splitPanel);

		exclusionEventDetailsPanel.setContent(wrapperLayout);
		return exclusionEventDetailsPanel;
	}
}
