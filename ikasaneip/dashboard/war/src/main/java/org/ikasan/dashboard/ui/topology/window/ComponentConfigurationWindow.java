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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.configurationService.model.ConfigurationParameterBooleanImpl;
import org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl;
import org.ikasan.configurationService.model.ConfigurationParameterListImpl;
import org.ikasan.configurationService.model.ConfigurationParameterLongImpl;
import org.ikasan.configurationService.model.ConfigurationParameterMapImpl;
import org.ikasan.configurationService.model.ConfigurationParameterMaskedStringImpl;
import org.ikasan.configurationService.model.ConfigurationParameterStringImpl;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.validation.BooleanValidator;
import org.ikasan.dashboard.ui.framework.validation.LongValidator;
import org.ikasan.dashboard.ui.framework.validation.StringValidator;
import org.ikasan.dashboard.ui.framework.validator.IntegerValidator;
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
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

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
	private HashMap<String, PasswordField> passwordFields = new HashMap<String, PasswordField>();
	private HashMap<String, TextArea> textFields = new HashMap<String, TextArea>();
	private HashMap<String, TextArea> descriptionTextFields = new HashMap<String, TextArea>();
	private HashMap<String, TextFieldKeyValuePair> mapTextFields = new HashMap<String, TextFieldKeyValuePair>();
	private HashMap<String, TextField> valueTextFields = new HashMap<String, TextField>();
	private Configuration configuration;
	
	/**
	 * @param configurationManagement
	 */
	public ComponentConfigurationWindow(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
	{
		super("Component Configuration");
		this.setIcon(VaadinIcons.COG_O);
				
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
    	setModal(true);
		setHeight("90%");
		setWidth("90%");   	
    }

    @SuppressWarnings("unchecked")
	public void populate(Component component)
    {
    	configuration = this.configurationManagement.getConfiguration(component.getConfigurationId());
    	
    	if(configuration == null)
    	{
    		Server server = component.getFlow().getModule().getServer();
    		
    		String url = server.getUrl() + ":" + server.getPort()
    				+ component.getFlow().getModule().getContextRoot()
    				+ "/rest/configuration/createConfiguration/"
    	    		+ component.getFlow().getModule().getName() 
    	    		+ "/"
    	    		+ component.getFlow().getName()
    	    		+ "/"
    	    		+ component.getName();
    		

    		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
    	        	.getAttribute(DashboardSessionValueConstants.USER);
        	
    		logger.info("Configuration Url: " + url + "  " + authentication.getName() + " " + (String)authentication.getCredentials());
    		
        	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());
        	
        	ClientConfig clientConfig = new ClientConfig();
        	clientConfig.register(feature) ;
        	
        	Client client = ClientBuilder.newClient(clientConfig);
        	
        	ObjectMapper mapper = new ObjectMapper();
        	
    	    WebTarget webTarget = client.target(url);
    	    
    	    Response response = webTarget.request().get();
    	    
    	    if(response.getStatus()  != 200)
    	    {
    	    	response.bufferEntity();
    	        
    	        String responseMessage = response.readEntity(String.class);
    	    	Notification.show("An error was received trying to create configured resource '" + component.getConfigurationId() + "': " 
    	    			+ responseMessage, Type.ERROR_MESSAGE);
    	    	
    	    	return;
    	    }
    	    
    	    configuration = this.configurationManagement.getConfiguration(component.getConfigurationId());
    	}
    		
    	  	
		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();
		
		this.layout = new GridLayout(2, parameters.size() + 6);
		this.layout.setSpacing(true);
		this.layout.setColumnExpandRatio(0, .25f);
		this.layout.setColumnExpandRatio(1, .75f);

		this.layout.setWidth("95%");
		this.layout.setMargin(true);
		
		Label configurationParametersLabel = new Label("Configuration Parameters");
		configurationParametersLabel.setStyleName(ValoTheme.LABEL_HUGE);
		this.layout.addComponent(configurationParametersLabel, 0, 0);
		
		GridLayout paramLayout = new GridLayout(2, 2);
		paramLayout.setSpacing(true);
		paramLayout.setSizeFull();
		paramLayout.setMargin(true);
		paramLayout.setColumnExpandRatio(0, .25f);
		paramLayout.setColumnExpandRatio(1, .75f);

		Label configuredResourceIdLabel = new Label("Configured Resource Id");
		configuredResourceIdLabel.addStyleName(ValoTheme.LABEL_LARGE);
		configuredResourceIdLabel.addStyleName(ValoTheme.LABEL_BOLD);
		Label configuredResourceIdValueLabel = new Label(configuration.getConfigurationId());
		configuredResourceIdValueLabel.addStyleName(ValoTheme.LABEL_LARGE);
		configuredResourceIdValueLabel.addStyleName(ValoTheme.LABEL_BOLD);
		
		paramLayout.addComponent(configuredResourceIdLabel, 0, 0);
		paramLayout.setComponentAlignment(configuredResourceIdLabel, Alignment.TOP_RIGHT);
		paramLayout.addComponent(configuredResourceIdValueLabel, 1, 0);
		
		Label configurationDescriptionLabel = new Label("Description:");
		configurationDescriptionLabel.setSizeUndefined();
		paramLayout.addComponent(configurationDescriptionLabel, 0, 1);
		paramLayout.setComponentAlignment(configurationDescriptionLabel, Alignment.TOP_RIGHT);
		
		TextArea conmfigurationDescriptionTextField = new TextArea();
		conmfigurationDescriptionTextField.setRows(4);
		conmfigurationDescriptionTextField.setWidth("80%");
		paramLayout.addComponent(conmfigurationDescriptionTextField, 1, 1);

		
		this.layout.addComponent(paramLayout, 0, 1, 1, 1);
		
		int i=2;
		
		for(ConfigurationParameter parameter: parameters)
		{	
			if(parameter instanceof ConfigurationParameterIntegerImpl)
    		{
				this.layout.addComponent(this.createTextAreaPanel(parameter, new IntegerValidator("Must be a valid number")), 0, i, 1, i);
    		}
			else if(parameter instanceof ConfigurationParameterMaskedStringImpl)
    		{
    			this.layout.addComponent(this.createPasswordFieldPanel(parameter, new StringValidator()), 0, i, 1, i);
    		}
    		else if(parameter instanceof ConfigurationParameterStringImpl)
    		{
    			this.layout.addComponent(this.createTextAreaPanel(parameter, new StringValidator()), 0, i, 1, i);
    		}
    		else if(parameter instanceof ConfigurationParameterBooleanImpl)
    		{
    			this.layout.addComponent(this.createTextAreaPanel(parameter, new BooleanValidator()), 0, i, 1, i);
    		}
    		else if(parameter instanceof ConfigurationParameterLongImpl)
    		{
    			this.layout.addComponent(this.createTextAreaPanel(parameter, new LongValidator()), 0, i, 1, i);
    		}
    		else if(parameter instanceof ConfigurationParameterMapImpl)
    		{
    			this.layout.addComponent(this.createMapPanel
    					((ConfigurationParameterMapImpl)parameter), 0, i, 1, i);
    		}
    		else if(parameter instanceof ConfigurationParameterListImpl)
    		{
    			this.layout.addComponent(this.createListPanel
    					((ConfigurationParameterListImpl)parameter), 0, i, 1, i);
    		}
			
			i++;
		}
		
		Button saveButton = new Button("Save");   
		saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
    	saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
            		for(TextArea textField: textFields.values())
                	{
                		textField.validate();
                	}
                } 
                catch (InvalidValueException e) 
                {
                	e.printStackTrace();
                	for(TextArea textField: textFields.values())
                	{
                		textField.setValidationVisible(true);
                	}
                	
                	Notification.show("There are errors on the form above", Type.ERROR_MESSAGE);
                	
                    return;
                }
  
            	for(ConfigurationParameter parameter: parameters)
        		{
            		TextArea textField = ComponentConfigurationWindow
            				.this.textFields.get(parameter.getName());
            		TextArea descriptionTextField = ComponentConfigurationWindow
            				.this.descriptionTextFields.get(parameter.getName());
            		
            		if(parameter != null && descriptionTextField != null)
            		{
            			parameter.setDescription(descriptionTextField.getValue());
            		}

            		if(parameter instanceof ConfigurationParameterIntegerImpl)
            		{
            			logger.info("Setting Integer value: " + textField.getValue());
            			
            			if(textField.getValue() != null && textField.getValue().length() > 0)
            				parameter.setValue(new Integer(textField.getValue()));
            		}
            		else if(parameter instanceof ConfigurationParameterStringImpl)
            		{
            			logger.info("Setting String value: " + textField.getValue());
            			if(textField.getValue() != null && textField.getValue().length() > 0)
            				parameter.setValue(textField.getValue());
            		}
            		else if(parameter instanceof ConfigurationParameterBooleanImpl)
            		{
            			logger.info("Setting Boolean value: " + textField.getValue());
            			if(textField.getValue() != null && textField.getValue().length() > 0)
            				parameter.setValue(new Boolean(textField.getValue()));
            		}
            		else if(parameter instanceof ConfigurationParameterLongImpl)
            		{
            			logger.info("Setting Boolean value: " + textField.getValue());
            			if(textField.getValue() != null && textField.getValue().length() > 0)
            				parameter.setValue(new Long	(textField.getValue()));
            		}
            		else if(parameter instanceof ConfigurationParameterMaskedStringImpl)
            		{
            			PasswordField passwordField = passwordFields.get(parameter.getName());
            			logger.info("Setting Masked String value: " + passwordField.getValue());
            			if(passwordField.getValue() != null && passwordField.getValue().length() > 0)
            				parameter.setValue(passwordField.getValue());
            		}
            		else if(parameter instanceof ConfigurationParameterMapImpl)
            		{
            			ConfigurationParameterMapImpl mapParameter = (ConfigurationParameterMapImpl) parameter;
            			
            			HashMap<String, String> map = new HashMap<String, String>();
            			
            			logger.info("Saving map: " + mapTextFields.size());
            			
            			for(String key: mapTextFields.keySet())
            			{
            				if(key.startsWith(parameter.getName()))
            				{
            					TextFieldKeyValuePair pair = mapTextFields.get(key);
            					
            					logger.info("Saving for key: " + key);
            					
            					if(pair.key.getValue() != "")
            					{
            						map.put(pair.key.getValue(), pair.value.getValue());
            					}
            				}
            			}
            			
            			parameter.setValue(map);
            		}
            		else if(parameter instanceof ConfigurationParameterListImpl)
            		{
            			ConfigurationParameterListImpl mapParameter = (ConfigurationParameterListImpl) parameter;
            			
            			ArrayList<String> map = new ArrayList<String>();
            			
            			for(String key: valueTextFields.keySet())
            			{
            				if(key.startsWith(parameter.getName()))
            				{
            					map.add(valueTextFields.get(key).getValue());
            				}
            			}
            			
            			parameter.setValue(map);
            		}
  
        			
        		}
            	
            	ComponentConfigurationWindow.this.configurationManagement
            		.saveConfiguration(configuration);      
            	
            	Notification notification = new Notification(
            		    "Saved",
            		    "The configuration has been saved successfully!",
            		    Type.HUMANIZED_MESSAGE);
            	notification.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
            	notification.show(Page.getCurrent());
            }
        });
    	
    	Button deleteButton = new Button("Delete");    
    	deleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
    	deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	DeleteConfigurationAction action = new DeleteConfigurationAction
            			(configuration, configurationManagement, ComponentConfigurationWindow.this);
            	
            	IkasanMessageDialog dialog = new IkasanMessageDialog("Delete configuration", 
            			"Are you sure you would like to delete this configuration?", action);

            	UI.getCurrent().addWindow(dialog);         	
            }
        });
		
    	GridLayout buttonLayout = new GridLayout(2, 1);
    	buttonLayout.setSpacing(true);
    	buttonLayout.addComponent(saveButton, 0 , 0);
    	buttonLayout.addComponent(deleteButton, 1 , 0);
    	
    	this.layout.addComponent(buttonLayout, 0, i, 1, i);
    	this.layout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
    	
    	Panel configurationPanel = new Panel();
    	configurationPanel.setContent(this.layout);

    	
		this.setContent(configurationPanel);
    }
    
    protected Panel createPasswordFieldPanel(ConfigurationParameter parameter, Validator validator)
    {
    	Panel paramPanel = new Panel();
		paramPanel.setStyleName("dashboard");
		paramPanel.setWidth("100%");

		GridLayout paramLayout = new GridLayout(2, 3);
		paramLayout.setSpacing(true);
		paramLayout.setSizeFull();
		paramLayout.setMargin(true);
		paramLayout.setColumnExpandRatio(0, .25f);
		paramLayout.setColumnExpandRatio(1, .75f);
		
		Label label = new Label(parameter.getName());
		label.setIcon(VaadinIcons.COG);
		label.addStyleName(ValoTheme.LABEL_LARGE);
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		paramLayout.addComponent(label, 0, 0, 1, 0);
		paramLayout.setComponentAlignment(label, Alignment.TOP_LEFT);
		
		logger.info(parameter.getName() + " " + parameter.getValue());
		Label valueLabel = new Label("Value:");
		valueLabel.setSizeUndefined();
		PasswordField passwordField = new PasswordField();
		passwordField.addValidator(validator);
		passwordField.setNullSettingAllowed(true);
		passwordField.setNullRepresentation("");
		passwordField.setValidationVisible(false);
		passwordField.setWidth("80%");
		passwordField.setId(parameter.getName());
		
		if(parameter instanceof ConfigurationParameterIntegerImpl)
		{
			StringToIntegerConverter plainIntegerConverter = new StringToIntegerConverter() 
			{
			    protected java.text.NumberFormat getFormat(Locale locale) 
			    {
			        NumberFormat format = super.getFormat(locale);
			        format.setGroupingUsed(false);
			        return format;
			    };
			};
			
			// either set for the field or in your field factory for multiple fields
			passwordField.setConverter(plainIntegerConverter);
		}
		else if (parameter instanceof ConfigurationParameterLongImpl)
		{
			StringToLongConverter plainLongConverter = new StringToLongConverter() 
			{
			    protected java.text.NumberFormat getFormat(Locale locale) 
			    {
			        NumberFormat format = super.getFormat(locale);
			        format.setGroupingUsed(false);
			        return format;
			    };
			};
			
			// either set for the field or in your field factory for multiple fields
			passwordField.setConverter(plainLongConverter);
		}

		passwordFields.put(parameter.getName(), passwordField);

		BeanItem<ConfigurationParameter> parameterItem = new BeanItem<ConfigurationParameter>(parameter);

		if(parameter.getValue() != null)
		{
			passwordField.setPropertyDataSource(parameterItem.getItemProperty("value"));
		}
		
		paramLayout.addComponent(valueLabel, 0, 1);
		paramLayout.addComponent(passwordField, 1, 1);
		paramLayout.setComponentAlignment(valueLabel, Alignment.TOP_RIGHT);
		
		Label paramDescriptionLabel = new Label("Description:");
		paramDescriptionLabel.setSizeUndefined();
		TextArea descriptionTextField = new TextArea();
		descriptionTextField.setRows(4);
		descriptionTextField.setWidth("80%");
		descriptionTextField.setId(parameter.getName());
		
		paramLayout.addComponent(paramDescriptionLabel, 0, 2);
		paramLayout.addComponent(descriptionTextField, 1, 2);
		paramLayout.setComponentAlignment(paramDescriptionLabel, Alignment.TOP_RIGHT);

		descriptionTextFields.put(parameter.getName(), descriptionTextField);

		if(parameter.getDescription() != null)
		{
			descriptionTextField.setValue((String)parameter.getValue());
		}
		
		paramPanel.setContent(paramLayout);
		
		return paramPanel;
    }
    
    protected Panel createTextAreaPanel(ConfigurationParameter parameter, Validator validator)
    {
    	Panel paramPanel = new Panel();
		paramPanel.setStyleName("dashboard");
		paramPanel.setWidth("100%");

		GridLayout paramLayout = new GridLayout(2, 3);
		paramLayout.setSpacing(true);
		paramLayout.setSizeFull();
		paramLayout.setMargin(true);
		paramLayout.setColumnExpandRatio(0, .25f);
		paramLayout.setColumnExpandRatio(1, .75f);
		
		Label label = new Label(parameter.getName());
		label.setIcon(VaadinIcons.COG);
		label.addStyleName(ValoTheme.LABEL_LARGE);
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		paramLayout.addComponent(label, 0, 0, 1, 0);
		paramLayout.setComponentAlignment(label, Alignment.TOP_LEFT);
		
		logger.info(parameter.getName() + " " + parameter.getValue());
		Label valueLabel = new Label("Value:");
		valueLabel.setSizeUndefined();
		TextArea textField = new TextArea();
		textField.addValidator(validator);
		textField.setNullSettingAllowed(true);
		textField.setNullRepresentation("");
		textField.setValidationVisible(false);
		textField.setRows(4);
		textField.setWidth("80%");
		textField.setId(parameter.getName());
		
		if(parameter instanceof ConfigurationParameterIntegerImpl)
		{
			StringToIntegerConverter plainIntegerConverter = new StringToIntegerConverter() 
			{
			    protected java.text.NumberFormat getFormat(Locale locale) 
			    {
			        NumberFormat format = super.getFormat(locale);
			        format.setGroupingUsed(false);
			        return format;
			    };
			};
			
			// either set for the field or in your field factory for multiple fields
			textField.setConverter(plainIntegerConverter);
		}
		else if (parameter instanceof ConfigurationParameterLongImpl)
		{
			StringToLongConverter plainLongConverter = new StringToLongConverter() 
			{
			    protected java.text.NumberFormat getFormat(Locale locale) 
			    {
			        NumberFormat format = super.getFormat(locale);
			        format.setGroupingUsed(false);
			        return format;
			    };
			};
			
			// either set for the field or in your field factory for multiple fields
			textField.setConverter(plainLongConverter);
		}

		textFields.put(parameter.getName(), textField);

		BeanItem<ConfigurationParameter> parameterItem = new BeanItem<ConfigurationParameter>(parameter);

		if(parameter.getValue() != null)
		{
			textField.setPropertyDataSource(parameterItem.getItemProperty("value"));
		}
		
		paramLayout.addComponent(valueLabel, 0, 1);
		paramLayout.addComponent(textField, 1, 1);
		paramLayout.setComponentAlignment(valueLabel, Alignment.TOP_RIGHT);
		
		Label paramDescriptionLabel = new Label("Description:");
		paramDescriptionLabel.setSizeUndefined();
		TextArea descriptionTextField = new TextArea();
		descriptionTextField.setRows(4);
		descriptionTextField.setWidth("80%");
		descriptionTextField.setId(parameter.getName());
		
		paramLayout.addComponent(paramDescriptionLabel, 0, 2);
		paramLayout.addComponent(descriptionTextField, 1, 2);
		paramLayout.setComponentAlignment(paramDescriptionLabel, Alignment.TOP_RIGHT);

		descriptionTextFields.put(parameter.getName(), descriptionTextField);

		if(parameter.getDescription() != null)
		{
			descriptionTextField.setValue(parameter.getDescription());
		}
		
		paramPanel.setContent(paramLayout);
		
		return paramPanel;
    }
    
    protected Panel createMapPanel(final ConfigurationParameterMapImpl parameter)
    {
    	Panel paramPanel = new Panel();
		paramPanel.setStyleName("dashboard");
		paramPanel.setWidth("100%");

		GridLayout paramLayout = new GridLayout(2, 3);
		paramLayout.setSpacing(true);
		paramLayout.setSizeFull();
		paramLayout.setMargin(true);
		paramLayout.setColumnExpandRatio(0, .25f);
		paramLayout.setColumnExpandRatio(1, .75f);
		
		Label label = new Label(parameter.getName());
		label.setIcon(VaadinIcons.COG);
		label.addStyleName(ValoTheme.LABEL_LARGE);
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		paramLayout.addComponent(label, 0 , 0, 1, 0);
		paramLayout.setComponentAlignment(label, Alignment.TOP_LEFT);
				
		final Map<String, String> valueMap = parameter.getValue();
		
		final GridLayout mapLayout = new GridLayout(5, (valueMap.size() != 0 ? valueMap.size(): 1) + 1	);
		mapLayout.setMargin(true);
		mapLayout.setSpacing(true);
		
		int i=0;
		
		for(final String key: valueMap.keySet())
		{
			final Label keyLabel = new Label("Key");
			final Label valueLabel = new Label("Value");
			
			final TextField keyField = new TextField();
			keyField.setValue(key);
			
			final TextField valueField = new TextField();
			valueField.setValue(valueMap.get(key));
			
			mapLayout.addComponent(keyLabel, 0, i);
			mapLayout.addComponent(keyField, 1, i);
			mapLayout.addComponent(valueLabel, 2, i);
			mapLayout.addComponent(valueField, 3, i);
			final String mapKey = parameter.getName() + i;
			TextFieldKeyValuePair pair = new TextFieldKeyValuePair();
			pair.key = keyField;
			pair.value = valueField;
			
			this.mapTextFields.put(mapKey, pair);
			
			final Button removeButton = new Button("remove");
			removeButton.setStyleName(ValoTheme.BUTTON_LINK);
			removeButton.addClickListener(new Button.ClickListener() 
	    	{
	            public void buttonClick(ClickEvent event) 
	            {
	            	valueMap.remove(key); 
	            	mapLayout.removeComponent(keyLabel);
	            	mapLayout.removeComponent(valueLabel);
	            	mapLayout.removeComponent(keyField);
	            	mapLayout.removeComponent(valueField);
	            	mapLayout.removeComponent(removeButton);
	            	
	            	mapTextFields.remove(mapKey);
	            }
	        });
			
			mapLayout.addComponent(removeButton, 4, i);
			
			i++;
		}
		
		final Button addButton = new Button("add");
		addButton.setStyleName(ValoTheme.BUTTON_LINK);
		addButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	final Label keyLabel = new Label("Key");
    			final Label valueLabel = new Label("Value");
    			
    			final TextField keyField = new TextField();
    			
    			final TextField valueField = new TextField();
    			
    			mapLayout.insertRow(mapLayout.getRows());
    			
    			mapLayout.removeComponent(addButton);
    			mapLayout.addComponent(keyLabel, 0, mapLayout.getRows() -2);
    			mapLayout.addComponent(keyField, 1, mapLayout.getRows() -2);
    			mapLayout.addComponent(valueLabel, 2, mapLayout.getRows() -2);
    			mapLayout.addComponent(valueField, 3, mapLayout.getRows() -2);
    			
    			final String mapKey = parameter.getName() + mapTextFields.size();
    			TextFieldKeyValuePair pair = new TextFieldKeyValuePair();
    			pair.key = keyField;
    			pair.value = valueField;
    			
    			mapTextFields.put(mapKey, pair);
    			
    			final Button removeButton = new Button("remove");
    			removeButton.setStyleName(ValoTheme.BUTTON_LINK);
    			removeButton.addClickListener(new Button.ClickListener() 
    	    	{
    	            public void buttonClick(ClickEvent event) 
    	            {
    	            	mapLayout.removeComponent(keyLabel);
    	            	mapLayout.removeComponent(valueLabel);
    	            	mapLayout.removeComponent(keyField);
    	            	mapLayout.removeComponent(valueField);
    	            	
    	            	mapLayout.removeComponent(removeButton);
    	            	
    	            	mapTextFields.remove(mapKey);
    	            }
    	        });
    			
    			mapLayout.addComponent(removeButton, 4, mapLayout.getRows() -2);
    			
    			mapLayout.addComponent(addButton, 0, mapLayout.getRows() -1);
            }
        });
		
		mapLayout.addComponent(addButton, 0, mapLayout.getRows() -1);
		
		Panel mapPanel = new Panel();
		mapPanel.setStyleName("dashboard");
		mapPanel.setContent(mapLayout);
		
		paramLayout.addComponent(mapPanel, 0, 1, 1, 1);
		paramLayout.setComponentAlignment(mapPanel, Alignment.TOP_CENTER);
		paramPanel.setContent(paramLayout);
		
		Label paramDescriptionLabel = new Label("Description:");
		paramDescriptionLabel.setSizeUndefined();
		TextArea descriptionTextField = new TextArea();
		descriptionTextField.setRows(4);
		descriptionTextField.setWidth("80%");
		descriptionTextField.setId(parameter.getName());
		
		paramLayout.addComponent(paramDescriptionLabel, 0, 2);
		paramLayout.addComponent(descriptionTextField, 1, 2);
		paramLayout.setComponentAlignment(paramDescriptionLabel, Alignment.TOP_RIGHT);

		descriptionTextFields.put(parameter.getName(), descriptionTextField);

		if(parameter.getDescription() != null)
		{
			descriptionTextField.setValue(parameter.getDescription());
		}

		
		return paramPanel;
    }

    protected Panel createListPanel(final ConfigurationParameterListImpl parameter)
    {
    	Panel paramPanel = new Panel();
		paramPanel.setStyleName("dashboard");
		paramPanel.setWidth("100%");

		GridLayout paramLayout = new GridLayout(2, 3);
		paramLayout.setSpacing(true);
		paramLayout.setSizeFull();
		paramLayout.setMargin(true);
		paramLayout.setColumnExpandRatio(0, .25f);
		paramLayout.setColumnExpandRatio(1, .75f);
		
		Label label = new Label(parameter.getName());
		label.setIcon(VaadinIcons.COG);
		label.addStyleName(ValoTheme.LABEL_LARGE);
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		paramLayout.addComponent(label, 0 , 0, 1, 0);
		paramLayout.setComponentAlignment(label, Alignment.TOP_LEFT);
				
		final List<String> valueList = parameter.getValue();
		
		final GridLayout listLayout = new GridLayout(3, (valueList.size() != 0 ? valueList.size(): 1) + 1);
		listLayout.setWidth("450px");
		listLayout.setMargin(true);
		listLayout.setSpacing(true);
		
		listLayout.setColumnExpandRatio(0, 0.25f);
		listLayout.setColumnExpandRatio(1, 0.5f);
		listLayout.setColumnExpandRatio(2, 0.25f);
		
		int i=0;
		
		for(final String value: valueList)
		{
			final Label valueLabel = new Label("Value");
			
			final TextField valueField = new TextField();
			valueField.setValue(value);
			valueField.setWidth("90%");
		
			listLayout.addComponent(valueLabel, 0, i);
			listLayout.addComponent(valueField, 1, i);
			
			final String mapKey = parameter.getName() + i;
			
			this.valueTextFields.put(mapKey, valueField);
			
			final Button removeButton = new Button("remove");
			removeButton.setStyleName(ValoTheme.BUTTON_LINK);
			removeButton.addClickListener(new Button.ClickListener() 
	    	{
	            public void buttonClick(ClickEvent event) 
	            {
	            	valueList.remove(value); 
	            	listLayout.removeComponent(valueLabel);
	            	listLayout.removeComponent(valueField);
	            	listLayout.removeComponent(removeButton);
	            	
	            	valueTextFields.remove(mapKey);
	            }
	        });
			
			listLayout.addComponent(removeButton, 2, i);
			
			i++;
		}
		
		final Button addButton = new Button("add");
		addButton.setStyleName(ValoTheme.BUTTON_LINK);
		addButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
    			final Label valueLabel = new Label("Value");
    				
    			final TextField valueField = new TextField();
    			valueField.setWidth("90%");
    			
    			listLayout.insertRow(listLayout.getRows());
    			
    			listLayout.removeComponent(addButton);
    			listLayout.addComponent(valueLabel, 0, listLayout.getRows() -2);
    			listLayout.addComponent(valueField, 1, listLayout.getRows() -2);
    			
    			final String mapKey = parameter.getName() + valueTextFields.size();
    			
    			valueTextFields.put(mapKey, valueField);
    			
    			final Button removeButton = new Button("remove");
    			removeButton.setStyleName(ValoTheme.BUTTON_LINK);
    			removeButton.addClickListener(new Button.ClickListener() 
    	    	{
    	            public void buttonClick(ClickEvent event) 
    	            {
    	            	listLayout.removeComponent(valueLabel);
    	            	listLayout.removeComponent(valueField);
    	            	
    	            	listLayout.removeComponent(removeButton);
    	            	
    	            	valueTextFields.remove(mapKey);
    	            }
    	        });
    			
    			listLayout.addComponent(removeButton, 2, listLayout.getRows() -2);
    			
    			listLayout.addComponent(addButton, 0, listLayout.getRows() -1);
            }
        });
		
		listLayout.addComponent(addButton, 0, listLayout.getRows() -1);
		
		Panel mapPanel = new Panel();
		mapPanel.setStyleName("dashboard");
		mapPanel.setContent(listLayout);
		
		paramLayout.addComponent(mapPanel, 0, 1, 1, 1);
		paramLayout.setComponentAlignment(mapPanel, Alignment.TOP_CENTER);
		paramPanel.setContent(paramLayout);
		
		Label paramDescriptionLabel = new Label("Description:");
		paramDescriptionLabel.setSizeUndefined();
		TextArea descriptionTextField = new TextArea();
		descriptionTextField.setRows(4);
		descriptionTextField.setWidth("80%");
		descriptionTextField.setId(parameter.getName());
		
		paramLayout.addComponent(paramDescriptionLabel, 0, 2);
		paramLayout.addComponent(descriptionTextField, 1, 2);
		paramLayout.setComponentAlignment(paramDescriptionLabel, Alignment.TOP_RIGHT);

		descriptionTextFields.put(parameter.getName(), descriptionTextField);

		if(parameter.getDescription() != null)
		{
			descriptionTextField.setValue(parameter.getDescription());
		}

		
		return paramPanel;
    }
    
    private class TextFieldKeyValuePair
    {
    	public TextField key;
    	public TextField value;
    }
}