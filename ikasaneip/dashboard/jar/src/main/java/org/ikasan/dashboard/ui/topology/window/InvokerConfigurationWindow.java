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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.configurationService.model.*;
import org.ikasan.configurationService.util.ComponentConfigurationExportHelper;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.dashboard.ui.framework.validation.BooleanValidator;
import org.ikasan.dashboard.ui.framework.validation.LongValidator;
import org.ikasan.dashboard.ui.framework.validation.StringValidator;
import org.ikasan.dashboard.ui.framework.validator.IntegerValidator;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.topology.action.DeleteConfigurationAction;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.*;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.teemu.VaadinIcons;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class InvokerConfigurationWindow extends AbstractConfigurationWindow
{
	private Logger logger = LoggerFactory.getLogger(InvokerConfigurationWindow.class);

	private ComponentConfigurationExportHelper exportHelper = null;
	private PlatformConfigurationService platformConfigurationService = null;


	public InvokerConfigurationWindow(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
                                      ComponentConfigurationExportHelper exportHelper, PlatformConfigurationService platformConfigurationService)
	{
		super(configurationManagement, "Invoker Configuration");
		this.setIcon(VaadinIcons.COG_O);

		this.configurationManagement = configurationManagement;
		if(this.configurationManagement == null)
		{
			throw new IllegalArgumentException("configurationManagement cannot be null!");
		}

		this.exportHelper = exportHelper;
		if(this.exportHelper == null)
		{
			throw new IllegalArgumentException("exportHelper cannot be null!");
		}

		this.platformConfigurationService = platformConfigurationService;
		if(this.platformConfigurationService == null)
		{
			throw new IllegalArgumentException("platformConfigurationService cannot be null!");
		}

		init();
	}

    @SuppressWarnings("unchecked")
	public void populate(Component component)
    {
		configuration = this.configurationManagement.getConfiguration(component.getInvokerConfigurationId());

        if (configuration == null)
        {
            Server server = component.getFlow().getModule().getServer();

            String url = server.getUrl() + ":" + server.getPort()
                + component.getFlow().getModule().getContextRoot()
                + "/rest/configuration/createInvokerConfiguration/"
                + component.getFlow().getModule().getName()
                + "/"
                + component.getFlow().getName()
                + "/"
                + component.getName();


            IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String) authentication.getCredentials());

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);

            Client client = ClientBuilder.newClient(clientConfig);

            ObjectMapper mapper = new ObjectMapper();

            WebTarget webTarget = client.target(url);

            Response response = webTarget.request().get();

            if (response.getStatus() != 200) {
                response.bufferEntity();

                String responseMessage = response.readEntity(String.class);
                Notification.show("An error was received trying to create invoker configuration: "
                    + responseMessage, Type.ERROR_MESSAGE);

                return;
            }

            configuration = this.configurationManagement.getConfiguration(component.getConfigurationId());
        }

		this.buildLayout();
	}

	protected void buildLayout()
	{
		passwordFields = new HashMap<String, PasswordField>();
		textFields = new HashMap<String, TextArea>();
		comboBoxes = new HashMap<String, ComboBox>();
		descriptionTextFields = new HashMap<String, TextArea>();
		mapTextFields = new HashMap<String, TextFieldKeyValuePair>();
		valueTextFields = new HashMap<String, TextArea>();

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

		Button exportMappingConfigurationButton = new Button();
		exportMappingConfigurationButton.setIcon(VaadinIcons.DOWNLOAD_ALT);
		exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		exportMappingConfigurationButton.setDescription("Export the current component configuration");
		exportMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

		FileDownloader fd = new FileDownloader(this.getComponentConfigurationExportStream());
		fd.extend(exportMappingConfigurationButton);

		Button importMappingConfigurationButton = new Button();

		final ComponentConfigurationImportWindow componentConfigurationImportWindow = new ComponentConfigurationImportWindow(configuration);

		componentConfigurationImportWindow.addCloseListener(new CloseListener()
		{
			@Override
			public void windowClose(CloseEvent closeEvent)
			{
				buildLayout();
			}
		});

		importMappingConfigurationButton.setIcon(VaadinIcons.UPLOAD_ALT);
		importMappingConfigurationButton.setDescription("Import a component configuration");
		importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		importMappingConfigurationButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		importMappingConfigurationButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().addWindow(componentConfigurationImportWindow);
			}
		});

		HorizontalLayout uploadDownloadLayout = new HorizontalLayout();
		uploadDownloadLayout.setSpacing(true);
		uploadDownloadLayout.setWidth("100px");

		uploadDownloadLayout.addComponent(exportMappingConfigurationButton);
		uploadDownloadLayout.addComponent(importMappingConfigurationButton);

		this.layout.addComponent(uploadDownloadLayout, 1 ,0);
		this.layout.setComponentAlignment(uploadDownloadLayout, Alignment.MIDDLE_RIGHT);

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

		
		this.layout.addComponent(paramLayout, 0, 2, 1, 2);
		
		int i=3;
		
		for(ConfigurationParameter parameter: parameters)
		{	
			if(parameter instanceof ConfigurationParameterIntegerImpl)
    		{
				this.layout.addComponent(this.createTextFieldPanel(parameter, new IntegerValidator("Must be a valid number")), 0, i, 1, i);
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
    			this.layout.addComponent(this.createTrueFalsePanel(parameter, new BooleanValidator()), 0, i, 1, i);
    		}
    		else if(parameter instanceof ConfigurationParameterLongImpl)
    		{
    			this.layout.addComponent(this.createTextFieldPanel(parameter, new LongValidator()), 0, i, 1, i);
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
            		TextArea textField = InvokerConfigurationWindow
            				.this.textFields.get(parameter.getName());
            		TextArea descriptionTextField = InvokerConfigurationWindow
            				.this.descriptionTextFields.get(parameter.getName());
            		
            		if(parameter != null && descriptionTextField != null)
            		{
            			parameter.setDescription(descriptionTextField.getValue());
            		}

            		if(parameter instanceof ConfigurationParameterIntegerImpl)
            		{
            			
            			if(textField.getValue() != null && textField.getValue().length() > 0)
            			{
            				logger.debug("Setting Integer value: " + textField.getValue());
            				parameter.setValue(new Integer(textField.getValue()));
            			}
            		}
            		else if(parameter instanceof ConfigurationParameterStringImpl)
            		{
            			if(textField.getValue() != null && textField.getValue().length() > 0)
            			{
            				logger.debug("Setting String value: " + textField.getValue());
            				parameter.setValue(textField.getValue());
            			}
            		}
            		else if(parameter instanceof ConfigurationParameterBooleanImpl)
            		{
            			ComboBox combo = InvokerConfigurationWindow
								.this.comboBoxes.get(parameter.getName());
            			if(combo.getValue() != null)
            			{
            				logger.debug("Setting Boolean value: " + combo.getValue());
            				parameter.setValue((Boolean)combo.getValue());
            			}
            		}
            		else if(parameter instanceof ConfigurationParameterLongImpl)
            		{
            			if(textField.getValue() != null && textField.getValue().length() > 0)
            			{
            				logger.debug("Setting Boolean value: " + textField.getValue());
            				parameter.setValue(new Long	(textField.getValue()));
            			}
            		}
            		else if(parameter instanceof ConfigurationParameterMaskedStringImpl)
            		{
            			PasswordField passwordField = passwordFields.get(parameter.getName());
            			
            			if(passwordField.getValue() != null && passwordField.getValue().length() > 0)
            			{
            				logger.debug("Setting Masked String value: " + passwordField.getValue());
            				parameter.setValue(passwordField.getValue());
            			}
            		}
            		else if(parameter instanceof ConfigurationParameterMapImpl)
            		{
            			ConfigurationParameterMapImpl mapParameter = (ConfigurationParameterMapImpl) parameter;
            			
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
            	
            	InvokerConfigurationWindow.this.configurationManagement
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
            			(configuration, configurationManagement, InvokerConfigurationWindow.this);
            	
            	IkasanMessageDialog dialog = new IkasanMessageDialog("Delete configuration", 
            			"Are you sure you would like to delete this configuration?", action);

            	UI.getCurrent().addWindow(dialog);         	
            }
        });
		
    	GridLayout buttonLayout = new GridLayout(2, 1);
    	buttonLayout.setSpacing(true);
    	buttonLayout.addComponent(saveButton, 0 , 0);
    	buttonLayout.addComponent(deleteButton, 1 , 0);

		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute(DashboardSessionValueConstants.USER);

		if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_WRITE)
				|| authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_ADMIN)
				|| authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
		{
			buttonLayout.setVisible(true);
		}
		else
		{
			buttonLayout.setVisible(false);
		}
    	
    	this.layout.addComponent(buttonLayout, 0, i, 1, i);
    	this.layout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
    	
    	Panel configurationPanel = new Panel();
    	configurationPanel.setContent(this.layout);

		this.setContent(configurationPanel);
    }

	/**
	 * Helper method to get the stream associated with the export of the file.
	 *
	 * @return the StreamResource associated with the export.
	 */
	private StreamResource getComponentConfigurationExportStream()
	{
		StreamResource.StreamSource source = new StreamResource.StreamSource()
		{

			public InputStream getStream() {
				ByteArrayOutputStream stream = null;
				try
				{
					stream = getComponentConfigurationExport();
				}
				catch (IOException e)
				{
					logger.error(e.getMessage(), e);
				}
				InputStream input = new ByteArrayInputStream(stream.toByteArray());
				return input;

			}
		};
		StreamResource resource = new StreamResource ( source,"componentConfigurationExport.xml");
		return resource;
	}

	/**
	 * Helper method to get the ByteArrayOutputStream associated with the export.
	 *
	 * @return
	 * @throws IOException
	 */
	private ByteArrayOutputStream getComponentConfigurationExport() throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		String schemaLocation = (String)this.platformConfigurationService.getConfigurationValue("componentConfigurationSchemaLocation");

		if(schemaLocation == null || schemaLocation.length() == 0)
		{
			throw new RuntimeException("Cannot resolve the platform configuration mappingExportSchemaLocation!");
		}

		logger.info("Resolved schemaLocation " + schemaLocation);

		exportHelper.setSchemaLocation(schemaLocation);

		String exportXml = exportHelper.getComponentConfigurationExportXml(this.configuration);

		out.write(XmlFormatter.format(exportXml).getBytes());

		return out;
	}
}