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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.window.NewPolicyWindow;
import org.ikasan.dashboard.ui.administration.window.PolicyAssociationMappingSearchWindow;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * @author CMI2 Development Team
 * 
 */
public class PolicyManagementPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(PolicyManagementPanel.class);

	private UserService userService;
	private SecurityService securityService;
	private ComboBox rolesCombo;
	private ComboBox linkTypeCombo  = new ComboBox();
	private Panel associatedRolesPanel;
	private Panel policyDropPanel;
	private Table roleTable;
	private Table policyDropTable;
	private Button linkButton = new Button("Link");
	private Button newButton = new Button("New");
	private Button saveButton = new Button("Save");
	private Button deleteButton = new Button("Delete");
	private PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow;
	private Policy policy = new Policy();
	private AutocompleteField<Policy> policyNameField;
	private TextArea descriptionField;
	private BeanItem<Policy> policyItem;
	private Long associatedEntityId;
	private TextArea linkedEntity;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public PolicyManagementPanel(UserService userService, SecurityService securityService,
			PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow)
	{
		super();
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
		if (this.securityService == null)
		{
			throw new IllegalArgumentException(
					"policyAssociationMappingSearchWindow cannot be null!");
		}

		init();
	}

	@SuppressWarnings({ "serial" })
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");
		
		this.createAssociatedRolesPanel();
		this.createPolicyDropPanel();

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();

		Panel securityAdministrationPanel = new Panel("Policy Management");
		securityAdministrationPanel.setStyleName("dashboard");
		securityAdministrationPanel.setHeight("100%");
		securityAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(2, 5);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");
		gridLayout.setMargin(true);
		gridLayout.setSizeFull();
		gridLayout.setRowExpandRatio(0, 0.25f);
		gridLayout.setRowExpandRatio(1, 1.5f);
		gridLayout.setRowExpandRatio(2, 1.5f);
		gridLayout.setRowExpandRatio(3, 0.20f);
		gridLayout.setRowExpandRatio(4, 5.00f);
		
		
		Layout controlLayout = this.initControlLayout();
		
    	gridLayout.addComponent(controlLayout, 0, 0, 1, 0);
    	
    	GridLayout formLayout = new GridLayout(2, 3);
    	formLayout.setWidth("100%");
		formLayout.setHeight("125px");
		formLayout.setRowExpandRatio(0, 1f);
		formLayout.setRowExpandRatio(1, 2f);
		formLayout.setRowExpandRatio(2, 1f);		
		formLayout.setColumnExpandRatio(0, 1);
		formLayout.setColumnExpandRatio(1, 5);

    	Label policyNameLabel = new Label("Policy Name");
		final DragAndDropWrapper policyNameFieldWrap = initPolicyNameField();
		
		formLayout.addComponent(policyNameLabel, 0, 0);
		formLayout.addComponent(policyNameFieldWrap, 1, 0);

		Label descriptionLabel = new Label("Description");
		this.descriptionField = new TextArea();
		this.descriptionField.setWidth("40%");
		this.descriptionField.setHeight("60px");
		formLayout.addComponent(descriptionLabel, 0, 1);
		formLayout.addComponent(descriptionField, 1, 1);
		
		Label linkTypeLabel = new Label("Policy Link Type");
		formLayout.addComponent(linkTypeLabel, 0, 2);
		this.linkTypeCombo.setWidth("40%");
		formLayout.addComponent(this.linkTypeCombo, 1, 2);
		
		GridLayout linkLayout = new GridLayout(2, 2);
		linkLayout.setWidth("100%");
		linkLayout.setHeight("80px");
		linkLayout.setRowExpandRatio(0, 1f);
		linkLayout.setRowExpandRatio(1, 2f);		
		linkLayout.setColumnExpandRatio(0, 1);
		linkLayout.setColumnExpandRatio(1, 5);
		
		linkButton.setStyleName(Reindeer.BUTTON_LINK);
    	linkButton.setVisible(false);
    	linkButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	policyAssociationMappingSearchWindow.clear();
                UI.getCurrent().addWindow(policyAssociationMappingSearchWindow);
            }
        });
    	linkLayout.addComponent(this.linkButton, 1, 0);
    	
    	final Label linkedEntityLabel = new Label("Linked to");
		linkedEntity = new TextArea();
		linkedEntity.setWidth("40%");
		linkedEntity.setHeight("60px");
		
		linkLayout.addComponent(linkedEntityLabel, 0, 1);
		linkLayout.addComponent(linkedEntity, 1, 1);
		linkedEntityLabel.setVisible(false);
		linkedEntity.setVisible(false);
    	
    	this.policyAssociationMappingSearchWindow.addCloseListener(new Window.CloseListener() {
            // inline close-listener
            public void windowClose(CloseEvent e) {
            	PolicyManagementPanel.this.linkedEntity.setValue
            		(policyAssociationMappingSearchWindow.getMappingConfiguration().toStringLite());
            	PolicyManagementPanel.this.associatedEntityId 
            		= PolicyManagementPanel.this.policyAssociationMappingSearchWindow.getMappingConfiguration().getId();
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
		        	
		        }
		        else
		        {
		        	linkButton.setVisible(false);
		        	linkedEntityLabel.setVisible(false);
		        	linkedEntity.setVisible(false);
		        }
		    }
		});
		
		gridLayout.addComponent(formLayout,0, 1, 1, 1);
		gridLayout.addComponent(linkLayout,0, 2, 1, 2);
		
		gridLayout.addComponent(new Label("<hr />",ContentMode.HTML),0, 3, 1, 3);

		gridLayout.addComponent(this.roleTable, 0, 4, 1, 4);

		securityAdministrationPanel.setContent(gridLayout);
		layout.addComponent(securityAdministrationPanel);

		
		HorizontalLayout roleMemberPanelLayout = new HorizontalLayout();
		roleMemberPanelLayout.setMargin(true);
		roleMemberPanelLayout.addComponent(this.policyDropPanel);
		roleMemberPanelLayout.setSizeFull();
		
		HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
		hsplit.setFirstComponent(layout);
		hsplit.setSecondComponent(roleMemberPanelLayout);


		// Set the position of the splitter as percentage
		hsplit.setSplitPosition(65, Unit.PERCENTAGE);
		hsplit.setLocked(true);
		
		this.setContent(hsplit);
	}

	/**
	 * Helper method to create the associated roles panel.
	 */
	protected void createAssociatedRolesPanel()
	{
		this.associatedRolesPanel = new Panel("Roles Associated with this Policy");

		this.associatedRolesPanel.setHeight("600px");
		this.associatedRolesPanel.setWidth("500px");
		
		this.roleTable = new Table();
		this.roleTable.addContainerProperty("Role", String.class, null);
		this.roleTable.addContainerProperty("", Button.class, null);
		this.roleTable.setHeight("400px");
		this.roleTable.setWidth("300px");
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setWidth("100%");
		layout.setHeight("100%");
		layout.addComponent(this.roleTable);
		
		this.associatedRolesPanel.setContent(layout);
	}

	/**
	 * 
	 */
	protected void createPolicyDropPanel()
	{
		this.policyDropPanel = new Panel("Role/Policy Associations");
		
		this.policyDropPanel.setStyleName("dashboard");
		this.policyDropPanel.setHeight("100%");
		this.policyDropPanel.setWidth("100%");
		
		this.rolesCombo = new ComboBox();
		this.rolesCombo.addValueChangeListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        final Role role = (Role)event.getProperty().getValue();

		        if(role != null)
		        {
			        List<Policy> policies = securityService.getAllPoliciesWithRole(role.getName());
					
			        PolicyManagementPanel.this.policyDropTable.removeAllItems();
					
					
					for(final Policy policy: policies)
					{
						Button deleteButton = new Button();
						ThemeResource deleteIcon = new ThemeResource(
								"images/remove-icon.png");
						deleteButton.setIcon(deleteIcon);
						deleteButton.setStyleName(Reindeer.BUTTON_LINK);
						
						deleteButton.addClickListener(new Button.ClickListener() 
				        {
				            public void buttonClick(ClickEvent event) 
				            {
				            	role.getPolicies().remove(policy);			            	
				            	PolicyManagementPanel.this.saveRole(role);
				            	
				            	PolicyManagementPanel.this.policyDropTable.removeItem(policy.getName());			            	
				            	PolicyManagementPanel.this.roleTable.removeItem(role);
				            }
				        });
						
						
						PolicyManagementPanel.this.policyDropTable.addItem(new Object[]
								{ policy.getName(), deleteButton }, policy.getName());
					}
		        }
		    }
		});
		
		this.policyDropTable = new Table();
		this.policyDropTable.addContainerProperty("Role Policies", String.class, null);
		this.policyDropTable.addContainerProperty("", Button.class, null);
		this.policyDropTable.setHeight("400px");
		this.policyDropTable.setWidth("300px");
		
		this.policyDropTable.setDragMode(TableDragMode.ROW);
		this.policyDropTable.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);
				
				if(rolesCombo.getValue() == null)
				{
					// Do nothing if there is no role selected
					logger.info("Ignoring drop: " + dropEvent);
					return;
				}

				final WrapperTransferable t = (WrapperTransferable) dropEvent
						.getTransferable();

				final AutocompleteField sourceContainer = (AutocompleteField) t
						.getDraggedComponent();
				logger.info("sourceContainer.getText(): "
						+ sourceContainer.getText());

				Button deleteButton = new Button();
				ThemeResource deleteIcon = new ThemeResource(
						"images/remove-icon.png");
				deleteButton.setIcon(deleteIcon);
				deleteButton.setStyleName(Reindeer.BUTTON_LINK);
				
				final Policy policy = PolicyManagementPanel.this.securityService
						.findPolicyByName(sourceContainer.getText());
				
				final Role selectedRole = PolicyManagementPanel.this.securityService
						.findRoleByName(((Role)rolesCombo.getValue()).getName());
				
				deleteButton.addClickListener(new Button.ClickListener() 
		        {
		            public void buttonClick(ClickEvent event) 
		            {	
		            	selectedRole.getPolicies().remove(policy);		            	
		            	PolicyManagementPanel.this.saveRole(selectedRole);
		            	
		            	policyDropTable.removeItem(policy.getName());
		            	roleTable.removeItem(selectedRole);
		            }
		        });
				
				PolicyManagementPanel.this.policyDropTable.addItem(new Object[]
						{ sourceContainer.getText(), deleteButton}, sourceContainer.getText());

				selectedRole.getPolicies().add(policy);
				
				PolicyManagementPanel.this.saveRole(selectedRole);
				policy.getRoles().add(selectedRole);

				PolicyManagementPanel.this.roleTable.removeAllItems();

				for (final Role role : policy.getRoles())
				{
					Button roleDeleteButton = new Button();
					roleDeleteButton.setIcon(deleteIcon);
					roleDeleteButton.setStyleName(Reindeer.BUTTON_LINK);
					
					roleDeleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {			            	
			            	selectedRole.getPolicies().remove(policy);
			            	PolicyManagementPanel.this.saveRole(selectedRole);
			            	
			            	PolicyManagementPanel.this.roleTable.removeItem(role);
			            	PolicyManagementPanel.this.policyDropTable.removeItem(policy.getName());
			            }
			        }); 
					
					PolicyManagementPanel.this.roleTable.addItem(new Object[]
							{ role.getName(), roleDeleteButton }, role);
				}
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setWidth("100%");
		layout.setHeight("100%");
		layout.addComponent(this.rolesCombo);
		layout.setExpandRatio(this.rolesCombo, 0.05f);
		layout.addComponent(this.policyDropTable);
		layout.setExpandRatio(this.policyDropTable, 0.95f);
		
		this.policyDropPanel.setContent(layout);
	}

	/**
	 * Helper method to initialise behaviour of the policy name field.
	 * 
	 * @return
	 */
	protected DragAndDropWrapper initPolicyNameField()
	{
		// The policy field name is an autocomplete field.
		this.policyNameField = new AutocompleteField<Policy>();
		this.policyNameField.setWidth("40%");

		// We also want it to be drag and drop friendly.
		final DragAndDropWrapper policyNameFieldWrap = new DragAndDropWrapper(
				policyNameField);
		policyNameFieldWrap.setDragStartMode(DragStartMode.COMPONENT);
		
		// In order to have the auto complete work we must add a query listener.
		// The query listener gets activated when a user begins to type into 
		// the field and hits the database looking for suggestions.
		policyNameField.setQueryListener(new AutocompleteQueryListener<Policy>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<Policy> field,
					String query)
			{
				// Iterate over the returned results and add them as suggestions
				for (Policy policy : securityService.getPolicyByNameLike(query))
				{
					field.addSuggestion(policy, policy.getName());
				}
			}
		});

		// Once a suggestion is selected the listener below gets fired and we populate
		// associated fields as required.
		policyNameField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<Policy>()
		{
			@Override
			public void onSuggestionPicked(final Policy pickedPolicy)
			{
				PolicyManagementPanel.this.policy = pickedPolicy;
				
				// Populate all the policy related fields.
				PolicyManagementPanel.this.policyItem = new BeanItem<Policy>(PolicyManagementPanel.this.policy);
				PolicyManagementPanel.this.policyNameField.setPropertyDataSource(policyItem.getItemProperty("name"));
				PolicyManagementPanel.this.descriptionField.setPropertyDataSource(policyItem.getItemProperty("description"));
		        
				if(PolicyManagementPanel.this.policy.getPolicyLink() != null)
				{
					PolicyManagementPanel.this.linkTypeCombo.setValue(PolicyManagementPanel.this.policy.getPolicyLink().getPolicyLinkType());
					PolicyManagementPanel.this.linkedEntity.setValue(PolicyManagementPanel.this.policy.getPolicyLink().getName());
				}
				else
				{
					PolicyManagementPanel.this.linkTypeCombo.setValue(null);
					PolicyManagementPanel.this.linkedEntity.setValue(new String());
					
					PolicyManagementPanel.this.linkButton.setVisible(false);
            		PolicyManagementPanel.this.linkedEntity.setVisible(false);
				}
				
				roleTable.removeAllItems();

				// Add all the associated roles to the role table.
				for (final Role role : policy.getRoles())
				{
					Button deleteButton = new Button();
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);
					
					// Add the delete functionality to each role that is added
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {		
			            	// Update the roles associated with policy
			            	// and update in the DB.
			            	policy.getRoles().remove(role);			            	
			            	PolicyManagementPanel.this.savePolicy(policy);
			            	
			            	// Once we are happy that the DB call was fine
			            	// update the UI components to reflect the change.
			            	roleTable.removeItem(role);
			            	policyDropTable.removeItem(policy.getName());
			            }
			        });
					
					roleTable.addItem(new Object[]
							{ role.getName(), deleteButton }, role);
				}
			}
		});
		
		return policyNameFieldWrap;
	}

	/**
	 * 
	 */
	protected Layout initControlLayout()
	{
		this.newButton.setStyleName(Reindeer.BUTTON_LINK);
    	this.newButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	final NewPolicyWindow newPolicyWindow = new NewPolicyWindow();
                UI.getCurrent().addWindow(newPolicyWindow);
                
                newPolicyWindow.addCloseListener(new Window.CloseListener() {
                    // inline close-listener
                    public void windowClose(CloseEvent e) {
                    	PolicyManagementPanel.this.policy = newPolicyWindow.getPolicy();
                		
                    	PolicyManagementPanel.this.policyItem = new BeanItem<Policy>(PolicyManagementPanel.this.policy);
                		PolicyManagementPanel.this.policyNameField.setText(PolicyManagementPanel.this.policy.getName());
                		PolicyManagementPanel.this.policyNameField.setPropertyDataSource(policyItem.getItemProperty("name"));
                		PolicyManagementPanel.this.descriptionField.setPropertyDataSource(policyItem.getItemProperty("description"));
                		PolicyManagementPanel.this.linkTypeCombo.setValue(null);
                		PolicyManagementPanel.this.linkedEntity.setValue(new String());
                		
                		PolicyManagementPanel.this.linkButton.setVisible(false);
                		PolicyManagementPanel.this.linkedEntity.setVisible(false);

                		PolicyManagementPanel.this.roleTable.removeAllItems();
                    }
                });
            }
        });
    	
    	this.saveButton.setStyleName(Reindeer.BUTTON_LINK);
    	this.saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		PolicyManagementPanel.this.save();
            		
            		Notification.show("Saved");
            	}
            	catch(RuntimeException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    
            		Notification.show("Cauget exception trying to save a Policy!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
            	}
            }
        });
    	
    	this.deleteButton.setStyleName(Reindeer.BUTTON_LINK);
    	this.deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		PolicyManagementPanel.this.securityService.deletePolicy(policy);
            		
            		PolicyManagementPanel.this.policyNameField.setText("");
            		PolicyManagementPanel.this.descriptionField.setValue("");
            		PolicyManagementPanel.this.linkTypeCombo.setValue(null);
            		PolicyManagementPanel.this.linkedEntity.setValue("");
            		
            		PolicyManagementPanel.this.linkedEntity.setVisible(false);
            		PolicyManagementPanel.this.linkButton.setVisible(false);
            		
            		Notification.show("Deleted");
            	}
            	catch(RuntimeException e)
            	{
            		StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    
            		Notification.show("Cauget exception trying to delete a Policy!", sw.toString()
                        , Notification.Type.ERROR_MESSAGE);
            	}
            }
        });
    	
    	HorizontalLayout controlLayout =new HorizontalLayout();
    	controlLayout.setWidth("100%");
    	Label spacerLabel = new Label("");
    	controlLayout.addComponent(spacerLabel);
    	controlLayout.setExpandRatio(spacerLabel, 0.865f);
    	controlLayout.addComponent(newButton);
    	controlLayout.setExpandRatio(newButton, 0.045f);
    	controlLayout.addComponent(saveButton);
    	controlLayout.setExpandRatio(saveButton, 0.045f);
    	controlLayout.addComponent(deleteButton);
    	controlLayout.setExpandRatio(deleteButton, 0.045f);
    	
    	return controlLayout;
	}
	
	/**
	 * 
	 * @param policy
	 */
	protected void savePolicy(Policy policy)
	{
		try
		{
			this.securityService.savePolicy(policy);
		}
		catch(RuntimeException e)
		{
			StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            
    		Notification.show("Caught exception trying to save a Policy!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 
	 * @param role
	 */
	protected void saveRole(Role role)
	{
		try
		{
			this.securityService.saveRole(role);
		}
		catch(RuntimeException e)
		{
			StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            
    		Notification.show("Caught exception trying to save a Role!", sw.toString()
                , Notification.Type.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 
	 */
	protected void save()
	{
		if(this.linkTypeCombo.getValue() != null)
		{
			PolicyLinkType policyLinkType = (PolicyLinkType)this.linkTypeCombo.getValue();
			String linkedEntityName = this.linkedEntity.getValue();
			PolicyLink policyLink = new PolicyLink(policyLinkType, 
					this.associatedEntityId, linkedEntityName);
			
			this.securityService.savePolicyLink(policyLink);
			
			this.policy.setPolicyLink(policyLink);
			
			this.securityService.savePolicy(this.policy);
		}
		else
		{
			PolicyLink policyLink = this.policy.getPolicyLink();
			this.policy.setPolicyLink(null);
			
			this.securityService.savePolicy(this.policy);
			
			if(policyLink != null)
			{
				this.securityService.deletePolicyLink(policyLink);
			}
		}
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
		List<Role> roles = this.securityService.getAllRoles();
		
		this.rolesCombo.removeAllItems();
		this.policyDropTable.removeAllItems();
		
		for(Role role: roles)
		{
			this.rolesCombo.addItem(role);
			this.rolesCombo.setItemCaption(role, role.getName());
		}
		
		List<PolicyLinkType> policyLinkTypes = this.securityService.getAllPolicyLinkTypes();
		
		this.linkTypeCombo.removeAllItems();
		
		for(PolicyLinkType policyLinkType: policyLinkTypes)
		{
			this.linkTypeCombo.addItem(policyLinkType);
			this.linkTypeCombo.setItemCaption(policyLinkType, policyLinkType.getName());
		}
		
		this.policyNameField.clearChoices();
	}

}
