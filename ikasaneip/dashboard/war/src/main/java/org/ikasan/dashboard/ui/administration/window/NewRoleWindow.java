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
package org.ikasan.dashboard.ui.administration.window;

import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
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
public class NewRoleWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField roleName;
	private TextArea roleDescription;
	private Role role;
	private SecurityService securityService;
	

	/**
	 * @param policy
	 */
	public NewRoleWindow(SecurityService securityService)
	{
		super();
		this.role = new Role();
		
		this.securityService = securityService;
		
		this.init();
	}


	public void init()
	{
		this.setWidth("550px");
		this.setHeight("240px");
		this.setModal(true);
		this.setResizable(false);
		
		GridLayout gridLayout = new GridLayout(2, 4);
		gridLayout.setWidth("100%");
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		
		gridLayout.setColumnExpandRatio(0, .1f);
		gridLayout.setColumnExpandRatio(1, .9f);
		
		Label createNewRoleLabel = new Label("Create a New Role");
		createNewRoleLabel.setStyleName(ValoTheme.LABEL_HUGE);
		
		gridLayout.addComponent(createNewRoleLabel, 0, 0, 1, 0);

		Label nameLabel = new Label("Name:");
		nameLabel.setSizeUndefined();
		this.roleName = new TextField();
		this.roleName.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		this.roleName.setWidth("80%");
		
		gridLayout.addComponent(nameLabel, 0, 1);
		gridLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(roleName, 1, 1);
		
		Label descriptionLabel = new Label("Description:");
		descriptionLabel.setSizeUndefined();
		this.roleDescription = new TextArea();
		this.roleDescription.addValidator(new StringLengthValidator(
	            "A description must be entered.",
	            1, null, false));
		this.roleDescription.setRows(4);
		roleDescription.setWidth("80%");
		
		this.roleName.setValidationVisible(false);
    	this.roleDescription.setValidationVisible(false);
		
		gridLayout.addComponent(descriptionLabel, 0, 2);
		gridLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(roleDescription, 1, 2);
		
		Button createButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(createButton);
		buttonLayout.setComponentAlignment(createButton, Alignment.MIDDLE_CENTER);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		
		gridLayout.addComponent(buttonLayout, 0, 3, 1, 3);
		gridLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		BeanItem<Role> policyItem = new BeanItem<Role>(this.role);

		roleName.setPropertyDataSource(policyItem.getItemProperty("name"));
		roleDescription.setPropertyDataSource(policyItem.getItemProperty("description"));
		
		createButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
            		NewRoleWindow.this.roleName.validate();
            		NewRoleWindow.this.roleDescription.validate();
                } 
            	catch (InvalidValueException e) 
                {
                	NewRoleWindow.this.roleName.setValidationVisible(true);
                	NewRoleWindow.this.roleDescription.setValidationVisible(true);
                	
                	return;
                }
            	
            	NewRoleWindow.this.roleName.setValidationVisible(false);
            	NewRoleWindow.this.roleDescription.setValidationVisible(false);

            	UI.getCurrent().removeWindow(NewRoleWindow.this);
            	
            	securityService.saveRole(role);
            	
            	Notification.show("Role successfully created!");
            }
        });
		
		cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
                UI.getCurrent().removeWindow(NewRoleWindow.this);
            }
        });
		
		this.setContent(gridLayout);
	}
	
	/**
	 * 
	 * @return
	 */
	public Role getRole()
	{
		return this.role;
	}
}
