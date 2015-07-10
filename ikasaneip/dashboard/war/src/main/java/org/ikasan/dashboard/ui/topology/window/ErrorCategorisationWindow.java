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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.configurationService.model.ConfigurationParameterListImpl;
import org.ikasan.configurationService.model.ConfigurationParameterMapImpl;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.topology.action.DeleteConfigurationAction;
import org.ikasan.dashboard.ui.topology.panel.TopologyViewPanel;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Server;
import org.vaadin.teemu.VaadinIcons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorCategorisationWindow extends Window
{
	private Logger logger = Logger.getLogger(TopologyViewPanel.class);
		
	private Component component;
	
	
	/**
	 * @param configurationManagement
	 */
	public ErrorCategorisationWindow(Component component)
	{
		super("Error Categorisation");
		this.setIcon(VaadinIcons.EXCLAMATION_CIRCLE_O);
		
		this.component = component;
		
		init();
	}

	/**
     * Helper method to initialise this object.
     * 
     * @param message
     */
    protected void init()
    {
//    	private Long id;
//    	private String moduleName;
//    	private String flowName;
//    	private String flowElementName;
//    	private String errorCategory;
//    	private String errorDescription;
    	
    	setModal(true);
		setHeight("90%");
		setWidth("90%"); 
		
		GridLayout layout = new GridLayout(2, 7);
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setMargin(true);
    	
    	Label configuredResourceIdLabel = new Label("Error Categorisation");
		configuredResourceIdLabel.setStyleName("large");
		layout.addComponent(configuredResourceIdLabel);

		Label configuredResourceIdValueLabel = new Label("Module Name");
		configuredResourceIdValueLabel.setSizeUndefined();		
		layout.addComponent(configuredResourceIdValueLabel, 0, 1);
		layout.setComponentAlignment(configuredResourceIdValueLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleNameTextField = new TextField();
		moduleNameTextField.setIcon(VaadinIcons.ARCHIVE);
		moduleNameTextField.setRequired(true);
		moduleNameTextField.setValue(this.component.getFlow().getModule().getName());
		moduleNameTextField.setReadOnly(true);
		layout.addComponent(moduleNameTextField, 1, 1);  	
		
		Panel paramPanel = new Panel();
		paramPanel.setStyleName("dashboard");
		paramPanel.setWidth("100%");
		paramPanel.setContent(layout);
		
		this.setContent(paramPanel);
    }

    
}