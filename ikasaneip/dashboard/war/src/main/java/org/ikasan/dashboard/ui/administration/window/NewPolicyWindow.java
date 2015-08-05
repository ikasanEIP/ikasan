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

import java.util.List;

import org.ikasan.dashboard.ui.framework.util.PolicyLinkTypeConstants;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.vaadin.teemu.VaadinIcons;

import com.thoughtworks.selenium.webdriven.commands.GetValue;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
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
public class NewPolicyWindow extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3347325521531925322L;
	
	private UserService userService;
	private SecurityService securityService;
	
	private TextField policyName;
	private TextArea policyDescription;
	private Policy policy;
	private ComboBox linkTypeCombo  = new ComboBox();
	private Button linkButton = new Button("Link");
	private TextArea linkedEntity;
	
	private PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow;
	private PolicyAssociationModuleSearchWindow policyAssociationModuleSearchWindow;
	private PolicyAssociationFlowSearchWindow policyAssociationFlowSearchWindow;
	private PolicyAssociationBusinessStreamSearchWindow policyAssociationBusinessStreamSearchWindow;
	
	private Long associatedEntityId;
	
	Label policyLinkHintLabel = new Label();
	

	/**
	 * @param policy
	 */
	public NewPolicyWindow(UserService userService, SecurityService securityService,
			PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow,
			PolicyAssociationFlowSearchWindow policyAssociationFlowSearchWindow,
			PolicyAssociationModuleSearchWindow policyAssociationModuleSearchWindow,
			PolicyAssociationBusinessStreamSearchWindow policyAssociationBusinessStreamSearchWindow)
	{
		super();
		this.policy = new Policy();
		
		this.userService = userService;
		if (this.userService == null)
		{
			throw new IllegalArgumentException("userService cannot be null!");
		}
		this.securityService = securityService;
		if (this.securityService == null)
		{
			throw new IllegalArgumentException(
					"securityService cannot be null!");
		}
		this.policyAssociationMappingSearchWindow = policyAssociationMappingSearchWindow;
		if (this.policyAssociationMappingSearchWindow == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationMappingSearchWindow cannot be null!");
		}
		this.policyAssociationFlowSearchWindow = policyAssociationFlowSearchWindow;
		if (this.policyAssociationFlowSearchWindow == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationFlowSearchWindow cannot be null!");
		}
		this.policyAssociationModuleSearchWindow = policyAssociationModuleSearchWindow;
		if (this.policyAssociationModuleSearchWindow == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationModuleSearchWindow cannot be null!");
		}
		this.policyAssociationBusinessStreamSearchWindow = policyAssociationBusinessStreamSearchWindow;
		if (this.policyAssociationBusinessStreamSearchWindow == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationBusinessStreamSearchWindow cannot be null!");
		}

		init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);
		
		this.setWidth("600px");
		this.setHeight("400px");
		
		GridLayout gridLayout = new GridLayout(2, 8);
		gridLayout.setWidth("100%");
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		
		gridLayout.setColumnExpandRatio(0, .1f);
		gridLayout.setColumnExpandRatio(1, .9f);
		
		Label createNewPolicyLabel = new Label("Create a New Policy");
		createNewPolicyLabel.setStyleName(ValoTheme.LABEL_HUGE);
		
		gridLayout.addComponent(createNewPolicyLabel, 0, 0, 1, 0);

		Label nameLabel = new Label("Name:");
		nameLabel.setSizeUndefined();
		this.policyName = new TextField();
		this.policyName.addValidator(new StringLengthValidator(
	            "A name must be entered.",
	            1, null, false));
		this.policyName.setWidth("80%");
		
		gridLayout.addComponent(nameLabel, 0, 1);
		gridLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);
		gridLayout.addComponent(policyName, 1, 1);
		
		Label descriptionLabel = new Label("Description");
		descriptionLabel.setSizeUndefined();
		this.policyDescription = new TextArea();
		this.policyDescription.addValidator(new StringLengthValidator(
	            "A description must be entered.",
	            1, null, false));
		this.policyDescription.setRows(4);
		this.policyDescription.setWidth("80%");
		
		this.policyName.setValidationVisible(false);
    	this.policyDescription.setValidationVisible(false);
		
		gridLayout.addComponent(descriptionLabel, 0, 2);
		gridLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(policyDescription, 1, 2);
		
		Button createButton = new Button("Create");
		Button cancelButton = new Button("Cancel");
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(createButton);
		buttonLayout.setComponentAlignment(createButton, Alignment.MIDDLE_CENTER);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		
		BeanItem<Policy> policyItem = new BeanItem<Policy>(this.policy);

		this.policyName.setPropertyDataSource(policyItem.getItemProperty("name"));
		this.policyDescription.setPropertyDataSource(policyItem.getItemProperty("description"));
		
		Label linkTypeLabel = new Label("Policy Link Type");
		linkTypeLabel.setSizeUndefined();
		gridLayout.addComponent(linkTypeLabel, 0, 3);
		gridLayout.setComponentAlignment(linkTypeLabel, Alignment.TOP_RIGHT);
		this.linkTypeCombo.setWidth("80%");
		gridLayout.addComponent(this.linkTypeCombo, 1, 3);
		
		List<PolicyLinkType> policyLinkTypes = this.securityService.getAllPolicyLinkTypes();
		
		this.linkTypeCombo.removeAllItems();
		
		for(PolicyLinkType policyLinkType: policyLinkTypes)
		{
			this.linkTypeCombo.addItem(policyLinkType);
			this.linkTypeCombo.setItemCaption(policyLinkType, policyLinkType.getName());
		}
		
		
		policyLinkHintLabel.setCaptionAsHtml(true);
		policyLinkHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" You are linking this policy to an entity. Click link below to search for the entity to link to.");
		policyLinkHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		policyLinkHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		policyLinkHintLabel.setVisible(false);
		gridLayout.addComponent(policyLinkHintLabel, 0, 4, 1, 4);
		
		linkButton.setStyleName(ValoTheme.BUTTON_LINK);
    	linkButton.setVisible(false);
    	linkButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	PolicyLinkType policyLinkType = (PolicyLinkType)NewPolicyWindow.this.linkTypeCombo.getValue();
            	
            	if(policyLinkType.getName().equals(PolicyLinkTypeConstants.MAPPING_CONFIGURATION_LINK_TYPE))
            	{
            		NewPolicyWindow.this.policyAssociationMappingSearchWindow.clear();
            		UI.getCurrent().addWindow(NewPolicyWindow.this.policyAssociationMappingSearchWindow);
            	}
            	else if(policyLinkType.getName().equals(PolicyLinkTypeConstants.MODULE_LINK_TYPE))
            	{
            		NewPolicyWindow.this.policyAssociationModuleSearchWindow.clear();
            		UI.getCurrent().addWindow(NewPolicyWindow.this.policyAssociationModuleSearchWindow);
            	}
            	else if(policyLinkType.getName().equals(PolicyLinkTypeConstants.FLOW_LINK_TYPE))
            	{
            		NewPolicyWindow.this.policyAssociationFlowSearchWindow.clear();
            		UI.getCurrent().addWindow(NewPolicyWindow.this.policyAssociationFlowSearchWindow);
            	}
            	else if(policyLinkType.getName().equals(PolicyLinkTypeConstants.BUSINESS_STREAM_LINK_TYPE))
            	{
            		NewPolicyWindow.this.policyAssociationBusinessStreamSearchWindow.clear();
            		UI.getCurrent().addWindow(NewPolicyWindow.this.policyAssociationBusinessStreamSearchWindow);
            	}
            }
        });
    	gridLayout.addComponent(this.linkButton, 1, 5);
    	
    	final Label linkedEntityLabel = new Label("Linked to");
    	linkedEntityLabel.setSizeUndefined();
    	
    	this.linkedEntity = new TextArea();
		this.linkedEntity.addValidator(new StringLengthValidator(
	            "If a Policy Link Type is selected, you must link to an approptiate entity.",
	            1, null, false));
		this.linkedEntity.setWidth("80%");
		this.linkedEntity.setValidationVisible(false);
		this.linkedEntity.setHeight("60px");
		
		gridLayout.addComponent(linkedEntityLabel, 0, 6);
		gridLayout.setComponentAlignment(linkedEntityLabel, Alignment.TOP_RIGHT);
		gridLayout.addComponent(linkedEntity, 1, 6);
		linkedEntityLabel.setVisible(false);
		linkedEntity.setVisible(false);
    	
    	this.policyAssociationMappingSearchWindow.addCloseListener(new Window.CloseListener() 
    	{
            // inline close-listener
            public void windowClose(CloseEvent e) 
            {
            	if(policyAssociationMappingSearchWindow.getMappingConfiguration() != null)
            	{
	            	NewPolicyWindow.this.linkedEntity.setValue
	            		(policyAssociationMappingSearchWindow.getMappingConfiguration().toStringLite());
	            	NewPolicyWindow.this.associatedEntityId 
	            		= NewPolicyWindow.this.policyAssociationMappingSearchWindow.getMappingConfiguration().getId();
            	}
            }
        });

    	this.policyAssociationFlowSearchWindow.addCloseListener(new Window.CloseListener() 
    	{
            // inline close-listener
            public void windowClose(CloseEvent e) 
            {
            	if(policyAssociationFlowSearchWindow.getFlow() != null)
            	{
	            	NewPolicyWindow.this.linkedEntity.setValue
	            		(policyAssociationFlowSearchWindow.getFlow().toString());
	            	NewPolicyWindow.this.associatedEntityId 
	            		= NewPolicyWindow.this.policyAssociationFlowSearchWindow.getFlow().getId();
            	}
            }
        });
    	
    	this.policyAssociationModuleSearchWindow.addCloseListener(new Window.CloseListener() 
    	{
            // inline close-listener
            public void windowClose(CloseEvent e) 
            {
            	if(policyAssociationModuleSearchWindow.getModule() != null)
            	{
	            	NewPolicyWindow.this.linkedEntity.setValue
	            		(policyAssociationModuleSearchWindow.getModule().toString());
	            	NewPolicyWindow.this.associatedEntityId 
	            		= NewPolicyWindow.this.policyAssociationModuleSearchWindow.getModule().getId();
            	}
            }
        });
    	
    	this.policyAssociationBusinessStreamSearchWindow.addCloseListener(new Window.CloseListener() 
    	{
            // inline close-listener
            public void windowClose(CloseEvent e) 
            {
            	if(policyAssociationBusinessStreamSearchWindow.getBusinessStream() != null)
        		{
	            	NewPolicyWindow.this.linkedEntity.setValue
	            		(policyAssociationBusinessStreamSearchWindow.getBusinessStream().toString());
	            	NewPolicyWindow.this.associatedEntityId 
	            		= NewPolicyWindow.this.policyAssociationBusinessStreamSearchWindow.getBusinessStream().getId();
        		}
            }
    		
        });
		
		this.linkTypeCombo.addValueChangeListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        final PolicyLinkType policyLinkType = (PolicyLinkType)event.getProperty().getValue();
		        
		        if(policyLinkType != null)
		        {		        	
		        	linkButton.setVisible(true);
		        	linkedEntityLabel.setVisible(true);
		        	linkedEntity.setVisible(true);
		        	policyLinkHintLabel.setVisible(true);
		        }
		        else
		        {
		        	linkButton.setVisible(false);
		        	linkedEntityLabel.setVisible(false);
		        	linkedEntity.setVisible(false);
		        	policyLinkHintLabel.setVisible(false);
		        }
		    }
		});
		
		gridLayout.addComponent(buttonLayout, 0, 7, 1, 7);
		gridLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		createButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try 
                {
            		NewPolicyWindow.this.policyName.validate();
            		NewPolicyWindow.this.policyDescription.validate();
            		
            		if(linkTypeCombo.getValue() != null)
            		{
            			NewPolicyWindow.this.linkedEntity.validate();
            		}
                } 
            	catch (InvalidValueException e) 
                {
                	NewPolicyWindow.this.policyName.setValidationVisible(true);
                	NewPolicyWindow.this.policyDescription.setValidationVisible(true);
                	NewPolicyWindow.this.linkedEntity.setValidationVisible(true);
                	
                	return;
                }
            	
            	NewPolicyWindow.this.policyName.setValidationVisible(false);
            	NewPolicyWindow.this.policyDescription.setValidationVisible(false);
            	NewPolicyWindow.this.linkedEntity.setValidationVisible(false);
            	
            	if(linkTypeCombo.getValue() != null)
        		{
        			PolicyLinkType policyLinkType = (PolicyLinkType)linkTypeCombo.getValue();
        			String linkedEntityName = linkedEntity.getValue();
        			PolicyLink policyLink = new PolicyLink(policyLinkType, 
        					associatedEntityId, linkedEntityName);
        			
        			securityService.savePolicyLink(policyLink);
        			
        			policy.setPolicyLink(policyLink);
        			
        			securityService.savePolicy(policy);
        		}
        		else
        		{
        			PolicyLink policyLink = policy.getPolicyLink();
        			policy.setPolicyLink(null);
        			
        			securityService.savePolicy(policy);
        			
        			if(policyLink != null)
        			{
        				securityService.deletePolicyLink(policyLink);
        			}
        		}
            	
            	Notification.show("New policy successfully created!");

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
