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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.window.NewMappingConfigurationTypeWindow;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.service.TopologyService;
import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
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
public class FilterWindow extends Window
{
	private Logger logger = Logger.getLogger(FilterWindow.class);
	
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField name;
	private TextArea description;
	private TopologyService topologyService;
	private Set<Component> components;
	
	private Filter filter;
	

	/**
	 * Constructor
	 * 
	 * @param topologyService
	 */
	public FilterWindow(TopologyService topologyService, Set<Component> components)
	{
		super();
		
		this.topologyService = topologyService;
		if(this.topologyService == null)
		{
			throw new IllegalArgumentException("topology service cannot be null!");
		}
		
		this.components = components;
		
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

		Label newBusinessStreamLabel = new Label("New Filter");
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
		

		
		saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
            		FilterWindow.this.name.validate();
            		FilterWindow.this.description.validate();
                } 
            	catch (InvalidValueException e) 
                {
                	FilterWindow.this.name.setValidationVisible(true);
                	FilterWindow.this.description.setValidationVisible(true);
                	
                	return;
                }
            	
            	FilterWindow.this.name.setValidationVisible(false);
            	FilterWindow.this.description.setValidationVisible(false);
            	
            	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
        	        	.getAttribute(DashboardSessionValueConstants.USER);

            	try
            	{
            		filter = topologyService.createFilter(name.getValue(), description.getValue(), authentication.getName(), components);
            	}
            	catch(DataIntegrityViolationException e)
            	{
            		Notification.show("A filter with this name already exists. Please use a different name!", Type.ERROR_MESSAGE);
            	}
            	catch(Exception e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    logger.error("An error occurred trying to save mapping configuration type!", e); 
                    
                    Notification.show("A general exception has occurred!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
                    
                    UI.getCurrent().removeWindow(FilterWindow.this);
                    
                    return;
            	}
            	
            	Notification.show("New Filter Created!");
            	
            	UI.getCurrent().removeWindow(FilterWindow.this);
            }
        });
		
		cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
                UI.getCurrent().removeWindow(FilterWindow.this);
            }
        });
		
		this.setContent(gridLayout);
	}


	/**
	 * @return the filter
	 */
	public Filter getFilter()
	{
		return filter;
	}

}
