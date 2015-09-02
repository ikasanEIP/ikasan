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

import java.util.HashSet;

import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
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
public class NewBusinessStreamWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private TextField roleName;
	private TextArea roleDescription;
	private BusinessStream businessStream;
	

	/**
	 * @param policy
	 */
	public NewBusinessStreamWindow()
	{
		super();
		this.businessStream = new BusinessStream();
		
		this.init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		
		GridLayout gridLayout = new GridLayout(2, 4);
		gridLayout.setWidth("480px");
		gridLayout.setColumnExpandRatio(0, .15f);
		gridLayout.setColumnExpandRatio(1, .85f);

		gridLayout.setSpacing(true);
		gridLayout.setMargin(true);

		Label newBusinessStreamLabel = new Label("New Business Steam");
		Label nameLabel = new Label("Name:");
		newBusinessStreamLabel.addStyleName(ValoTheme.LABEL_HUGE);
		
		gridLayout.addComponent(newBusinessStreamLabel, 0, 0, 1, 0);
		
		nameLabel.setSizeUndefined();
		this.roleName = new TextField();
		this.roleName.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		this.roleName.setWidth("90%");
		
		gridLayout.addComponent(nameLabel, 0, 1);
		gridLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(roleName, 1, 1);
		
		Label descriptionLabel = new Label("Description:");
		descriptionLabel.setSizeUndefined();
		this.roleDescription = new TextArea();
		this.roleDescription.addValidator(new StringLengthValidator(
	            "A description must be entered.",
	            1, null, false));
		this.roleDescription.setWidth("90%");
		this.roleDescription.setRows(4);
		
		this.roleName.setValidationVisible(false);
    	this.roleDescription.setValidationVisible(false);
		
		gridLayout.addComponent(descriptionLabel, 0, 2);
		gridLayout.setComponentAlignment(descriptionLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(roleDescription, 1, 2);
		
		Button createButton = new Button("Create");
		Button cancelButton = new Button("Cancel");
		
		GridLayout buttonLayout = new GridLayout(2, 1);
		buttonLayout.setSpacing(true);

		buttonLayout.addComponent(createButton);
		buttonLayout.setComponentAlignment(createButton, Alignment.MIDDLE_CENTER);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		
		gridLayout.addComponent(buttonLayout, 0, 3, 1, 3);
		gridLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		BeanItem<BusinessStream> policyItem = new BeanItem<BusinessStream>(this.businessStream);

		roleName.setPropertyDataSource(policyItem.getItemProperty("name"));
		roleDescription.setPropertyDataSource(policyItem.getItemProperty("description"));
		this.businessStream.setFlows(new HashSet<BusinessStreamFlow>());
		
		createButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
            		NewBusinessStreamWindow.this.roleName.validate();
            		NewBusinessStreamWindow.this.roleDescription.validate();
                } 
            	catch (InvalidValueException e) 
                {
                	NewBusinessStreamWindow.this.roleName.setValidationVisible(true);
                	NewBusinessStreamWindow.this.roleDescription.setValidationVisible(true);
                	
                	return;
                }
            	
            	NewBusinessStreamWindow.this.roleName.setValidationVisible(false);
            	NewBusinessStreamWindow.this.roleDescription.setValidationVisible(false);

            	UI.getCurrent().removeWindow(NewBusinessStreamWindow.this);
            }
        });
		
		cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
                UI.getCurrent().removeWindow(NewBusinessStreamWindow.this);
            }
        });
		
		this.setContent(gridLayout);
	}
	
	/**
	 * 
	 * @return
	 */
	public BusinessStream getBusinessStream()
	{
		return this.businessStream;
	}
}
