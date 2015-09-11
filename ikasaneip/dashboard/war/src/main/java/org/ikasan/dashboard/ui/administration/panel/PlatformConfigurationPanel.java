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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.configurationService.model.ConfigurationParameterMapImpl;
import org.ikasan.dashboard.ui.framework.model.PlatformConfiguration;
import org.ikasan.dashboard.ui.framework.model.PlatformConfigurationConfiguredResource;
import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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
	
	private HashMap<String, TextFieldKeyValuePair> mapTextFields = new HashMap<String, TextFieldKeyValuePair>();

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
		
		Label label = new Label("Platform Configuration");
		label.addStyleName(ValoTheme.LABEL_HUGE);
		label.setSizeUndefined();
		paramLayout.addComponent(label, 0 , 0, 1, 0);
		paramLayout.setComponentAlignment(label, Alignment.TOP_LEFT);
				
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
                } 
                catch (InvalidValueException e) 
                {
                	for(TextFieldKeyValuePair textField: mapTextFields.values())
                	{
                		textField.key.setValidationVisible(true);
                		textField.value.setValidationVisible(true);
                	}
                	
                	Notification.show("Validation errors have occurred!", Type.ERROR_MESSAGE);
                	
                    return;
                }
  
    			
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
    	
    	paramLayout.addComponent(mapPanel, 0, 1, 1, 1);
		paramLayout.setComponentAlignment(mapPanel, Alignment.TOP_CENTER);
		paramLayout.addComponent(saveButton, 0, 2, 1, 2);
		paramLayout.setComponentAlignment(saveButton, Alignment.TOP_CENTER);
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
		this.platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();
		
		platformConfiguration = this.configurationManagement.getConfiguration(this.platformConfigurationConfiguredResource);
		
		// create the configuration if it does not already exist!
		if(platformConfiguration == null)
		{
			this.platformConfigurationConfiguredResource.setConfiguration(new PlatformConfiguration());
			platformConfiguration = this.configurationManagement.createConfiguration(platformConfigurationConfiguredResource);
		}
		
		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)platformConfiguration.getParameters();
		
		//There will only be one map parameter!		
		this.setContent(this.createMapPanel
				((ConfigurationParameterMapImpl)parameters.get(0)));
	}
	
	private class TextFieldKeyValuePair
    {
    	public TextField key;
    	public TextField value;
    }
}
