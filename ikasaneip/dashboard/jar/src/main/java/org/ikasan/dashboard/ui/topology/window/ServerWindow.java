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
import java.util.Locale;

import org.ikasan.dashboard.ui.framework.validator.IntegerValidator;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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
public class ServerWindow extends Window
{
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField name;
	private TextArea description;
	private TextField url;
	private TextField port;
	private Server server;
	private TopologyService topologyService;
	

	/**
	 * Constructor
	 * 
	 * @param topologyService
	 */
	public ServerWindow(TopologyService topologyService)
	{
		super();
		
		this.topologyService = topologyService;
		if(this.topologyService == null)
		{
			throw new IllegalArgumentException("topology service cannot be null!");
		}
		
		this.server = new Server();
		
		this.init();
	}
	
	/**
	 * Constructor
	 * 
	 * @param topologyService
	 * @param server
	 */
	public ServerWindow(TopologyService topologyService, Server server)
	{
		super();
		
		this.topologyService = topologyService;
		if(this.topologyService == null)
		{
			throw new IllegalArgumentException("topology service cannot be null!");
		}
		
		this.server = server;
		if(this.server == null)
		{
			throw new IllegalArgumentException("server cannot be null!");
		}
		
		this.init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		
		GridLayout gridLayout = new GridLayout(2, 7);
		gridLayout.setWidth("480px");
		gridLayout.setColumnExpandRatio(0, .15f);
		gridLayout.setColumnExpandRatio(1, .85f);

		gridLayout.setSpacing(true);
		gridLayout.setMargin(true);

		Label newBusinessStreamLabel = new Label("Server");
		newBusinessStreamLabel.addStyleName(ValoTheme.LABEL_HUGE);
		
		gridLayout.addComponent(newBusinessStreamLabel, 0, 0, 1, 0);
		
		Label nameLabel = new Label("Name:");
		nameLabel.setSizeUndefined();
		this.name = new TextField();
		this.name.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		this.name.setWidth("90%");
		
		gridLayout.addComponent(nameLabel, 0, 1);
		gridLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(name, 1, 1);
		
		
		Label urlLabel = new Label("URL:");
		urlLabel.setSizeUndefined();
		this.url = new TextField();
		this.url.addValidator(new StringLengthValidator(
	            "A url must be entered.",
	            1, null, false));
		this.url.setWidth("90%");
		
		gridLayout.addComponent(urlLabel, 0, 3);
		gridLayout.setComponentAlignment(urlLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(url, 1, 3);
		
		Label portLabel = new Label("Port Number:");
		portLabel.setSizeUndefined();
		this.port = new TextField();
		this.port.addValidator(new IntegerValidator(
	            "A port number and must be a valid number."));
		this.port.setWidth("45%");
		
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
		this.port.setConverter(plainIntegerConverter);
		
		gridLayout.addComponent(portLabel, 0, 4);
		gridLayout.setComponentAlignment(portLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(port, 1, 4);
		
		Label descriptionLabel = new Label("Description:");
		descriptionLabel.setSizeUndefined();
		this.description = new TextArea();
		this.description.addValidator(new StringLengthValidator(
	            "A description must be entered.",
	            1, null, false));
		this.description.setWidth("90%");
		this.description.setRows(4);
		
		this.name.setValidationVisible(false);
    	this.description.setValidationVisible(false);
    	this.url.setValidationVisible(false);
    	this.port.setValidationVisible(false);
		
		gridLayout.addComponent(descriptionLabel, 0, 5);
		gridLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(description, 1, 5);
		
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		
		GridLayout buttonLayout = new GridLayout(2, 1);
		buttonLayout.setSpacing(true);

		buttonLayout.addComponent(saveButton);
		buttonLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		
		gridLayout.addComponent(buttonLayout, 0, 6, 1, 6);
		gridLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		BeanItem<Server> serverItem = new BeanItem<Server>(this.server);

		name.setPropertyDataSource(serverItem.getItemProperty("name"));
		description.setPropertyDataSource(serverItem.getItemProperty("description"));
		url.setPropertyDataSource(serverItem.getItemProperty("url"));
		this.port.setPropertyDataSource(serverItem.getItemProperty("port"));
		
		saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
            		ServerWindow.this.name.validate();
            		ServerWindow.this.description.validate();
            		ServerWindow.this.url.validate();
            		ServerWindow.this.port.validate();
                } 
            	catch (InvalidValueException e) 
                {
                	ServerWindow.this.name.setValidationVisible(true);
                	ServerWindow.this.description.setValidationVisible(true);
                	ServerWindow.this.url.setValidationVisible(true);
                	ServerWindow.this.port.setValidationVisible(true);
                	
                	return;
                }
            	
            	ServerWindow.this.name.setValidationVisible(false);
            	ServerWindow.this.description.setValidationVisible(false);
            	ServerWindow.this.url.setValidationVisible(false);
            	ServerWindow.this.port.setValidationVisible(false);

            	topologyService.save(server);
            	
            	Notification.show("New Server Created!");
            	
            	UI.getCurrent().removeWindow(ServerWindow.this);
            }
        });
		
		cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
                UI.getCurrent().removeWindow(ServerWindow.this);
            }
        });
		
		this.setContent(gridLayout);
	}
}
