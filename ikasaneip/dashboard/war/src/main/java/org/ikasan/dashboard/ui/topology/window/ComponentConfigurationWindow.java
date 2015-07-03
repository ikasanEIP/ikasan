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

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.configurationService.model.DefaultConfiguration;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.topology.panel.TopologyViewPanel;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ComponentConfigurationWindow extends Window
{
	private Logger logger = Logger.getLogger(TopologyViewPanel.class);
	
	@SuppressWarnings("rawtypes")
	private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
	private GridLayout layout;
	private HashMap<String, TextArea> textFields = new HashMap<String, TextArea>();
	private Configuration configuration;
	
	/**
	 * @param configurationManagement
	 */
	public ComponentConfigurationWindow(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
	{
		super();
		this.configurationManagement = configurationManagement;
		
		init();
	}

	/**
     * Helper method to initialise this object.
     * 
     * @param message
     */
    protected void init()
    {
    	this.setSizeFull();
    	this.setModal(true);    	
    }

    @SuppressWarnings("unchecked")
	public void populate(Component component)
    {
    	configuration = this.configurationManagement.getConfiguration(component.getConfigurationId());
    	
    	if(configuration == null)
    	{
    		Server server = component.getFlow().getModule().getServer();
    		
    		String url = "http://" + server.getUrl() + ":" + server.getPort()
    				+ component.getFlow().getModule().getContextRoot()
    				+ "/rest/configuration/createConfiguration/"
    	    		+ component.getFlow().getModule().getName() 
    	    		+ "/"
    	    		+ component.getFlow().getName()
    	    		+ "/"
    	    		+ component.getName();
    		
    		logger.info("Configuration Url: " + url);

    		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
    	        	.getAttribute(DashboardSessionValueConstants.USER);
        	
        	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
        	
        	ClientConfig clientConfig = new ClientConfig();
        	clientConfig.register(feature) ;
        	
        	Client client = ClientBuilder.newClient(clientConfig);
        	
        	ObjectMapper mapper = new ObjectMapper();
        	
    	    WebTarget webTarget = client.target(url);
    	    
    	    configuration = webTarget.request().get(DefaultConfiguration.class);
    	}
    		
    	  	
		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();
		
		this.layout = new GridLayout(2, parameters.size() + 2);
		this.layout.setColumnExpandRatio(0, .25f);
		this.layout.setColumnExpandRatio(1, .75f);

		this.layout.setWidth("100%");
		this.layout.setMargin(true);
		
		int i=0;
		
		for(ConfigurationParameter parameter: parameters)
		{
			logger.info(parameter.getName() + " " + parameter.getValue());
			Label label = new Label(parameter.getName());
			TextArea textField = new TextArea();
			textField.setWidth("80%");
			textField.setId(parameter.getName());

			textFields.put(parameter.getName(), textField);

			if(parameter.getValue() != null)
			{
				textField.setValue(parameter.getValue().toString());
			}
			
			this.layout.addComponent(label, 0, i);
			this.layout.addComponent(textField, 1, i);
			
			i++;
		}
		
		Button saveButton = new Button("Save");    	
    	saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	for(ConfigurationParameter parameter: parameters)
        		{
            		TextArea textField = ComponentConfigurationWindow
            				.this.textFields.get(parameter.getName());

            		if(parameter.getValue() != null)
            		{
            			logger.info("parameter.getValue().getClass(): " + parameter.getValue().getClass());
            		}
            		
            		if(parameter.getValue() instanceof Integer)
            		{
            			logger.info("Setting Integer value: " + textField.getValue());
            			parameter.setValue(new Integer(textField.getValue()));
            		}
            		else if(parameter.getValue() instanceof String)
            		{
            			logger.info("Setting String value: " + textField.getValue());
            			parameter.setValue(textField.getValue());
            		}
            		else if(parameter.getValue() instanceof Boolean)
            		{
            			logger.info("Setting Boolean value: " + textField.getValue());
            			parameter.setValue(new Boolean(textField.getValue()));
            		}
            		else if(parameter.getValue() instanceof Long)
            		{
            			logger.info("Setting Boolean value: " + textField.getValue());
            			parameter.setValue(new Long	(textField.getValue()));
            		}
  
        			logger.info(parameter.getName() + " " + parameter.getValue());
        		}
            	
            	ComponentConfigurationWindow.this.configurationManagement
            		.saveConfiguration(configuration);            	
            }
        });
		
    	this.layout.addComponent(saveButton, 1, i);
    	
    	Panel configurationPanel = new Panel("Configure");
    	configurationPanel.setStyleName("dashboard");
    	configurationPanel.setContent(this.layout);
    	
    	HorizontalLayout mainLayout = new HorizontalLayout();
    	mainLayout.setMargin(true);
    	mainLayout.setSizeFull();
    	mainLayout.addComponent(configurationPanel);
    	
		this.setContent(mainLayout);
    }
}