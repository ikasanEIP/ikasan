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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl;
import org.ikasan.configurationService.model.ConfigurationParameterLongImpl;
import org.ikasan.configurationService.model.ConfigurationParameterMapImpl;
import org.ikasan.configurationService.model.PlatformConfiguration;
import org.ikasan.configurationService.model.PlatformConfigurationConfiguredResource;
import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author CMI2 Development Team
 * 
 */
public class PlatformConfigurationPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(PlatformConfigurationPanel.class);
	
	private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
	private PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource;
	private Configuration platformConfiguration;
	
	private ConfigurationParameter userParam;
	private ConfigurationParameter passwordParam;
	
	private HashMap<String, TextFieldKeyValuePair> mapTextFields = new HashMap<String, TextFieldKeyValuePair>();
	
	private TextField usernameField;
	private PasswordField passwordField;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public PlatformConfigurationPanel(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
	{
		super();
		this.configurationManagement = configurationManagement;
		if (this.configurationManagement == null)
		{
			throw new IllegalArgumentException("configurationService cannot be null!");
		}
		
		
		init();
	}

	@SuppressWarnings("deprecation")
	protected void init()
	{
		
	}
	
	protected Panel createPasswordFieldPanel(ConfigurationParameter parameter, Validator validator)
    {
    	Panel paramPanel = new Panel();
    	paramPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
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
		
		Label valueLabel = new Label("Value:");
		valueLabel.setSizeUndefined();
		passwordField = new PasswordField();
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

		BeanItem<ConfigurationParameter> parameterItem = new BeanItem<ConfigurationParameter>(parameter);

		if(parameter.getValue() != null)
		{
			passwordField.setPropertyDataSource(parameterItem.getItemProperty("value"));
		}
		
		paramLayout.addComponent(valueLabel, 0, 1);
		paramLayout.addComponent(passwordField, 1, 1);
		paramLayout.setComponentAlignment(valueLabel, Alignment.TOP_RIGHT);
		
		paramPanel.setContent(paramLayout);
		
		return paramPanel;
    }
	
	protected Panel createTextFieldPanel(ConfigurationParameter parameter, Validator validator)
    {		
    	Panel paramPanel = new Panel();
		paramPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		paramPanel.setWidth("100%");

		GridLayout paramLayout = new GridLayout(3, 3);
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
		paramLayout.addComponent(label, 0, 1, 1, 1);
		paramLayout.setComponentAlignment(label, Alignment.TOP_LEFT);
		
		Label valueLabel = new Label("Value:");
		valueLabel.setSizeUndefined();
		usernameField = new TextField();
		usernameField.addValidator(validator);
		usernameField.setNullSettingAllowed(true);
		usernameField.setNullRepresentation("");
		usernameField.setValidationVisible(false);
		usernameField.setWidth("80%");
		usernameField.setId(parameter.getName());
		
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
			usernameField.setConverter(plainIntegerConverter);
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
			usernameField.setConverter(plainLongConverter);
		}

		BeanItem<ConfigurationParameter> parameterItem = new BeanItem<ConfigurationParameter>(parameter);

		if(parameter.getValue() != null)
		{
			usernameField.setPropertyDataSource(parameterItem.getItemProperty("value"));
		}
		
		paramLayout.addComponent(valueLabel, 0, 2);
		paramLayout.addComponent(usernameField, 1, 2);
		paramLayout.setComponentAlignment(valueLabel, Alignment.TOP_RIGHT);
		
		paramPanel.setContent(paramLayout);
		
		return paramPanel;
    }

	
	protected Panel createMapPanel(final ConfigurationParameterMapImpl parameter)
    {
    	Panel paramPanel = new Panel();
    	paramPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		paramPanel.setWidth("100%");

		GridLayout paramLayout = new GridLayout(2, 3);
		paramLayout.setSpacing(true);
		paramLayout.setSizeFull();
		paramLayout.setMargin(true);
		paramLayout.setColumnExpandRatio(0, .25f);
		paramLayout.setColumnExpandRatio(1, .75f);
				
		final Map<String, String> valueMap = parameter.getValue();
		
		final GridLayout mapLayout = new GridLayout(5, (valueMap.size() != 0 ? valueMap.size(): 1) + 1);
		mapLayout.setColumnExpandRatio(0, .05f);
		mapLayout.setColumnExpandRatio(1, .425f);
		mapLayout.setColumnExpandRatio(2, .05f);
		mapLayout.setColumnExpandRatio(3, .425f);
		mapLayout.setColumnExpandRatio(4, .05f);
		
		mapLayout.setMargin(true);
		mapLayout.setSpacing(true);
		mapLayout.setWidth("100%");
		
		int i=0;
		
		for(final String key: valueMap.keySet())
		{
			final Label keyLabel = new Label("Name:");
			final Label valueLabel = new Label("Value:");
			
			final TextField keyField = new TextField();
			keyField.setValue(key);
			keyField.setWidth("100%");
			keyField.setNullSettingAllowed(false);
			keyField.addValidator(new NonZeroLengthStringValidator("Then configuration value name cannot be empty!"));
			keyField.setValidationVisible(false);
			
			final TextField valueField = new TextField();
			valueField.setWidth("100%");
			valueField.setValue(valueMap.get(key));
			valueField.setNullSettingAllowed(false);
			valueField.addValidator(new NonZeroLengthStringValidator("Then configuration value cannot be empty!"));
			valueField.setValidationVisible(false);
			
			mapLayout.addComponent(keyLabel, 0, i);
			mapLayout.setComponentAlignment(keyLabel, Alignment.MIDDLE_RIGHT);
			mapLayout.addComponent(keyField, 1, i);
			mapLayout.addComponent(valueLabel, 2, i);
			mapLayout.setComponentAlignment(valueLabel, Alignment.MIDDLE_RIGHT);
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
            	final Label keyLabel = new Label("Name:");
    			final Label valueLabel = new Label("Value:");
    			
    			final TextField keyField = new TextField();
    			keyField.setWidth("100%");
    			keyField.setNullSettingAllowed(false);
    			keyField.addValidator(new NonZeroLengthStringValidator("Then configuration value name cannot be empty!"));
    			keyField.setValidationVisible(false);
    			
    			final TextField valueField = new TextField();
    			valueField.setWidth("100%");
    			valueField.setNullSettingAllowed(false);
    			valueField.addValidator(new NonZeroLengthStringValidator("Then configuration value cannot be empty!"));
    			valueField.setValidationVisible(false);
    			
    			mapLayout.insertRow(mapLayout.getRows());
    			
    			mapLayout.removeComponent(addButton);
    			mapLayout.addComponent(keyLabel, 0, mapLayout.getRows() -2);
    			mapLayout.setComponentAlignment(keyLabel, Alignment.MIDDLE_RIGHT);
    			mapLayout.addComponent(keyField, 1, mapLayout.getRows() -2);
    			mapLayout.addComponent(valueLabel, 2, mapLayout.getRows() -2);
    			mapLayout.setComponentAlignment(valueLabel, Alignment.MIDDLE_RIGHT);
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
		mapPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
		mapPanel.setContent(mapLayout);
				
		
		Button saveButton = new Button("Save");   
		saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
    	saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
            		for(TextFieldKeyValuePair textField: mapTextFields.values())
                	{
                		textField.key.validate();
                		textField.value.validate();
                	}
            		
            		usernameField.validate();
            		passwordField.validate();
                } 
                catch (InvalidValueException e) 
                {
                	for(TextFieldKeyValuePair textField: mapTextFields.values())
                	{
                		textField.key.setValidationVisible(true);
                		textField.value.setValidationVisible(true);
                		usernameField.setValidationVisible(true);
                		passwordField.setValidationVisible(true);
                	}
                	
                	Notification.show("Validation errors have occurred!", Type.ERROR_MESSAGE);
                	
                    return;
                }
  
    			
    			HashMap<String, String> map = new HashMap<String, String>();
    			
    			logger.debug("Saving map: " + mapTextFields.size());
    			
    			for(String key: mapTextFields.keySet())
    			{
    				if(key.startsWith(parameter.getName()))
    				{
    					TextFieldKeyValuePair pair = mapTextFields.get(key);
    					
    					logger.debug("Saving for key: " + key);
    					
    					if(pair.key.getValue() != "")
    					{
    						map.put(pair.key.getValue(), pair.value.getValue());
    					}
    				}
    			}
    			
    			parameter.setValue(map);    			
    			userParam.setValue(usernameField.getValue());
    			passwordParam.setValue(passwordField.getValue());
            	
            	PlatformConfigurationPanel.this.configurationManagement
            		.saveConfiguration(platformConfiguration);      
            	
            	Notification notification = new Notification(
            		    "Saved",
            		    "The configuration has been saved successfully!",
            		    Type.HUMANIZED_MESSAGE);
            	notification.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
            	notification.show(Page.getCurrent());
            }
    	});
    	
    	Button deleteButton = new Button("Re-create");   
    	deleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
    	deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	configurationManagement.deleteConfiguration(platformConfiguration);
            	
            	refresh();
            }
    	});
    	
    	HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setHeight("100%");
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth(200, Unit.PIXELS);
		buttonLayout.setMargin(true);
		buttonLayout.addComponent(saveButton);
		buttonLayout.addComponent(deleteButton);
    	
    	paramLayout.addComponent(mapPanel, 0, 1, 1, 1);
		paramLayout.setComponentAlignment(mapPanel, Alignment.TOP_CENTER);
		paramLayout.addComponent(buttonLayout, 0, 2, 1, 2);
		paramLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);
		paramPanel.setContent(paramLayout);
		
		return paramPanel;
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
		this.platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();
		
		this.platformConfiguration = this.configurationManagement.getConfiguration(this.platformConfigurationConfiguredResource);
		
		// create the configuration if it does not already exist!
		if(platformConfiguration == null)
		{
			this.platformConfigurationConfiguredResource.setConfiguration(new PlatformConfiguration());
			platformConfiguration = this.configurationManagement.createConfiguration(platformConfigurationConfiguredResource);
		}
		
		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)platformConfiguration.getParameters();      
        
        GridLayout layout = new GridLayout();
        layout.setWidth("100%");
        layout.setSpacing(true);
        layout.setMargin(true);
        
        Panel userPanel = null;
        Panel passwordPanel = null;
        Panel mapPanel = null;
        
        for(ConfigurationParameter parameter: parameters)
        {
        	if(parameter.getName().equals("webServiceUserAccount"))
        	{
        		userParam = parameter;
        		userPanel = this.createTextFieldPanel(parameter, 
        				new NonZeroLengthStringValidator("The web service user account must be entered!"));
        	}
        	else if(parameter.getName().equals("webServiceUserPassword"))
        	{
        		passwordParam = parameter;
        		passwordPanel = this.createPasswordFieldPanel(parameter, 
        				new NonZeroLengthStringValidator("The web service user password must be entered!"));
        	}
        	if(parameter.getName().equals("configurationMap"))
        	{
        		mapPanel = this.createMapPanel((ConfigurationParameterMapImpl)parameter);
        	}
        }
        
        Label configLabel = new Label("Platform Configuration");
		configLabel.addStyleName(ValoTheme.LABEL_HUGE);
		configLabel.setSizeUndefined();

		layout.addComponent(configLabel);
		
		if(userPanel != null)
		{
			layout.addComponent(userPanel);
		}
		
		if(passwordPanel != null)
		{
			layout.addComponent(passwordPanel);
		}
		
		if(mapPanel != null)
		{
			layout.addComponent(mapPanel);
		}
        	
		this.setContent(layout);
	}
	
	private class TextFieldKeyValuePair
    {
    	public TextField key;
    	public TextField value;
    }
}
