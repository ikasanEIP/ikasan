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

import org.ikasan.dashboard.ui.administration.panel.PolicyManagementPanel;
import org.ikasan.security.model.Policy;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class NewPolicyWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField policyName;
	private TextField policyDescription;
	private Policy policy;
	

	/**
	 * @param policy
	 */
	public NewPolicyWindow()
	{
		super();
		this.policy = new Policy();
		
		this.init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		
		GridLayout gridLayout = new GridLayout(2, 3);
		gridLayout.setWidth("280px");
		gridLayout.setHeight("140px");
		gridLayout.setMargin(true);

		Label nameLabel = new Label("Name");
		this.policyName = new TextField();
		this.policyName.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		
		gridLayout.addComponent(nameLabel, 0, 0);
		gridLayout.addComponent(policyName, 1, 0);
		
		Label descriptionLabel = new Label("Description");
		this.policyDescription = new TextField();
		this.policyDescription.addValidator(new StringLengthValidator(
	            "A description must be entered.",
	            1, null, false));
		
		this.policyName.setValidationVisible(false);
    	this.policyDescription.setValidationVisible(false);
		
		gridLayout.addComponent(descriptionLabel, 0, 1);
		gridLayout.addComponent(policyDescription, 1, 1);
		
		Button createButton = new Button("Create");
		Button cancelButton = new Button("Cancel");
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidth("200px");
		buttonLayout.addComponent(createButton);
		buttonLayout.setComponentAlignment(createButton, Alignment.MIDDLE_CENTER);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		
		gridLayout.addComponent(buttonLayout, 0, 2, 1, 2);
		gridLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		BeanItem<Policy> policyItem = new BeanItem<Policy>(this.policy);

		policyName.setPropertyDataSource(policyItem.getItemProperty("name"));
		policyDescription.setPropertyDataSource(policyItem.getItemProperty("description"));
		
		createButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
            		NewPolicyWindow.this.policyName.validate();
            		NewPolicyWindow.this.policyDescription.validate();
                } 
            	catch (InvalidValueException e) 
                {
                	NewPolicyWindow.this.policyName.setValidationVisible(true);
                	NewPolicyWindow.this.policyDescription.setValidationVisible(true);
                	
                	return;
                }
            	
            	NewPolicyWindow.this.policyName.setValidationVisible(false);
            	NewPolicyWindow.this.policyDescription.setValidationVisible(false);

            	UI.getCurrent().removeWindow(NewPolicyWindow.this);
            }
        });
		
		cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
                UI.getCurrent().removeWindow(NewPolicyWindow.this);
            }
        });
		
		this.setContent(gridLayout);
	}
	
	/**
	 * 
	 * @return
	 */
	public Policy getPolicy()
	{
		return this.policy;
	}
}
