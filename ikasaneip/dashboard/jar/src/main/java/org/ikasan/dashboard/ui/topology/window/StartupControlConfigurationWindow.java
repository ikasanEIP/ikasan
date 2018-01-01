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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupControlService;
import org.ikasan.spec.module.StartupType;
import org.ikasan.topology.model.Flow;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
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
public class StartupControlConfigurationWindow extends Window
{
	private static final long serialVersionUID = 8900720508474545668L;

	private Logger logger = LoggerFactory.getLogger(StartupControlConfigurationWindow.class);
	
	private StartupControlService startupControlService;
	
	private ComboBox startupType;
	private TextArea comment;
	private TextField moduleNameTextField;
	private TextField flowNameTextField;
	private Flow flow;
	private StartupControl startupControl;
	private BeanItem<StartupControl> startupControlItem;
	
	/**
	 * @param configurationManagement
	 */
	public StartupControlConfigurationWindow(StartupControlService startupControlService,
			Flow flow)
	{
		super("Startup Control");
		
		this.startupControlService = startupControlService;
		if(this.startupControlService == null)
		{
			throw new IllegalArgumentException("startupControlService cannot be null!");
		}
		this.flow = flow;
		if(this.flow == null)
		{
			throw new IllegalArgumentException("flow cannot be null!");
		}
		
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
    	setResizable(false);
		setHeight("320px");
		setWidth("550px");   	
		
		GridLayout gridLayout = new GridLayout(2, 6);
		gridLayout.setWidth("500px");
		gridLayout.setColumnExpandRatio(0, .15f);
		gridLayout.setColumnExpandRatio(1, .85f);

		gridLayout.setSpacing(true);
		gridLayout.setMargin(true);

		Label startupControlLabel = new Label("Startup Control");
		startupControlLabel.addStyleName(ValoTheme.LABEL_HUGE);
		
		gridLayout.addComponent(startupControlLabel, 0, 0, 1, 0);
		
		Label moduleNameLabel = new Label();
		moduleNameLabel.setContentMode(ContentMode.HTML);
		moduleNameLabel.setValue(VaadinIcons.ARCHIVE.getHtml() + " Module Name:");
		moduleNameLabel.setSizeUndefined();		
		gridLayout.addComponent(moduleNameLabel, 0, 1);
		gridLayout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);
		
		startupControl = this.startupControlService.getStartupControl(flow.getModule().getName()
				, flow.getName());
		
		startupControlItem = new BeanItem<StartupControl>(startupControl);
		
		moduleNameTextField = new TextField();
		moduleNameTextField.setRequired(true);
		moduleNameTextField.setPropertyDataSource(startupControlItem.getItemProperty("moduleName"));
		moduleNameTextField.setReadOnly(true);
		moduleNameTextField.setWidth("90%");
		gridLayout.addComponent(moduleNameTextField, 1, 1); 
		
		Label flowNameLabel = new Label();
		flowNameLabel.setContentMode(ContentMode.HTML);
		flowNameLabel.setValue(VaadinIcons.AUTOMATION.getHtml() + " Flow Name:");
		flowNameLabel.setSizeUndefined();		
		gridLayout.addComponent(flowNameLabel, 0, 2);
		gridLayout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);
		
		flowNameTextField = new TextField();
		flowNameTextField.setRequired(true);
		flowNameTextField.setPropertyDataSource(startupControlItem.getItemProperty("flowName"));
		flowNameTextField.setReadOnly(true);
		flowNameTextField.setWidth("90%");
		gridLayout.addComponent(flowNameTextField, 1, 2); 
		
		Label startupTypeLabel = new Label("Startup Type:");
		startupTypeLabel.setSizeUndefined();
		this.startupType = new ComboBox();
		this.startupType.addItem(StartupType.MANUAL);
		this.startupType.setItemCaption(StartupType.MANUAL, "Manual");
		this.startupType.addItem(StartupType.AUTOMATIC);
		this.startupType.setItemCaption(StartupType.AUTOMATIC, "Automatic");
		this.startupType.addItem(StartupType.DISABLED);	
		this.startupType.setItemCaption(StartupType.DISABLED, "Disabled");
		this.startupType.setPropertyDataSource(startupControlItem.getItemProperty("startupType"));
		this.startupType.setNullSelectionAllowed(false);
		
		this.startupType.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		this.startupType.setWidth("90%");
		this.startupType.setValidationVisible(false);
		
		gridLayout.addComponent(startupTypeLabel, 0, 3);
		gridLayout.setComponentAlignment(startupTypeLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(this.startupType, 1, 3);
		
		Label commentLabel = new Label("Comment:");
		commentLabel.setSizeUndefined();
		this.comment = new TextArea();
		this.comment.setRows(3);
		this.comment.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		this.comment.setWidth("90%");
		this.comment.setValidationVisible(false);
		this.comment.setNullRepresentation("");
		this.comment.setPropertyDataSource(startupControlItem.getItemProperty("comment"));
		
		gridLayout.addComponent(commentLabel, 0, 4);
		gridLayout.setComponentAlignment(commentLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(this.comment, 1, 4);
		
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		
		GridLayout buttonLayout = new GridLayout(2, 1);
		buttonLayout.setSpacing(true);

		buttonLayout.addComponent(saveButton);
		buttonLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		
		gridLayout.addComponent(buttonLayout, 0, 5, 1, 5);
		gridLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	StartupControl sc = startupControlItem.getBean();
            	
            	if(((StartupType)startupType.getValue()) == StartupType.DISABLED 
            			&& (comment.getValue() == null || comment.getValue().length() == 0))
            	{
            		Notification.show("A comment must be entered for a 'Disabled' start up type!", Type.ERROR_MESSAGE);
            		
            		return;
            	}
            	else
            	{
	            	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	        	.getAttribute(DashboardSessionValueConstants.USER);	            	
	            	
	            	StartupControlConfigurationWindow.this.startupControlService.setStartupType(sc.getModuleName(), sc.getFlowName()
	            			, (StartupType)startupType.getValue(), comment.getValue(), authentication.getName());
	            	
	            	Notification.show("Saved!");
            	}
            }
        });
		
		cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
                UI.getCurrent().removeWindow(StartupControlConfigurationWindow.this);
            }
        });
		
		this.setContent(gridLayout);
    }
}