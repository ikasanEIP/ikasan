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

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

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
	private TextField roleDescription;
	private Role role;
	

	/**
	 * @param policy
	 */
	public NewRoleWindow()
	{
		super();
		this.role = new Role();
		
		this.init();
	}


	public void init()
	{
		this.setWidth("300px");
		this.setHeight("200px");
		this.setModal(true);
		
		GridLayout gridLayout = new GridLayout(2, 3);
		gridLayout.setMargin(true);

		Label nameLabel = new Label("Name");
		this.roleName = new TextField();
		this.roleName.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		
		gridLayout.addComponent(nameLabel, 0, 0);
		gridLayout.addComponent(roleName, 1, 0);
		
		Label descriptionLabel = new Label("Description");
		this.roleDescription = new TextField();
		this.roleDescription.addValidator(new StringLengthValidator(
	            "A description must be entered.",
	            1, null, false));
		
		this.roleName.setValidationVisible(false);
    	this.roleDescription.setValidationVisible(false);
		
		gridLayout.addComponent(descriptionLabel, 0, 1);
		gridLayout.addComponent(roleDescription, 1, 1);
		
		Button createButton = new Button("Create");
		Button cancelButton = new Button("Cancel");
		
		gridLayout.addComponent(createButton, 0, 2);
		gridLayout.addComponent(cancelButton, 1, 2);
		
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
