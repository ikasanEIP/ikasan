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
import org.ikasan.dashboard.ui.administration.window.PolicyAssociationBusinessStreamSearchWindow;
import org.ikasan.dashboard.ui.administration.window.PolicyAssociationFlowSearchWindow;
import org.ikasan.dashboard.ui.administration.window.PolicyAssociationMappingSearchWindow;
import org.ikasan.dashboard.ui.administration.window.PolicyAssociationModuleSearchWindow;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.vaadin.teemu.VaadinIcons;

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
import com.vaadin.ui.Alignment;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;
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
	private TextField linkType  = new TextField();
	private Panel associatedRolesPanel;
	private Panel policyDropPanel;
	private Table roleTable;
	private Table policyDropTable;
	private Button newButton = new Button("New");
	private Button deleteButton = new Button("Delete");
	private Button cancelButton = new Button("Cancel");
	private PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow;
	private PolicyAssociationModuleSearchWindow policyAssociationModuleSearchWindow;
	private PolicyAssociationFlowSearchWindow policyAssociationFlowSearchWindow;
	private PolicyAssociationBusinessStreamSearchWindow policyAssociationBusinessStreamSearchWindow;
	private Policy policy = new Policy();
	private AutocompleteField<Policy> policyNameField;
	private TextArea descriptionField;
	private BeanItem<Policy> policyItem;
	private TextArea linkedEntity;
	private Label linkedEntityLabel = new Label("Linked to:");
	private Label linkTypeLabel = new Label("Policy Link Type:");

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public PolicyManagementPanel(UserService userService, SecurityService securityService,
			PolicyAssociationMappingSearchWindow policyAssociationMappingSearchWindow,
			PolicyAssociationFlowSearchWindow policyAssociationFlowSearchWindow,
			PolicyAssociationModuleSearchWindow policyAssociationModuleSearchWindow,
			PolicyAssociationBusinessStreamSearchWindow policyAssociationBusinessStreamSearchWindow)
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

	@SuppressWarnings({ "serial" })
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");
		
		this.createAssociatedRolesPanel();
		this.createPolicyDropPanel();

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setWidth("100%");

		Panel policyAdministrationPanel = new Panel();
		policyAdministrationPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		policyAdministrationPanel.setHeight("100%");
		policyAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(2, 6);
		gridLayout.setSizeFull();
		
		Label roleManagementLabel = new Label("Policy Management");
 		roleManagementLabel.setStyleName(ValoTheme.LABEL_HUGE);
 		gridLayout.addComponent(roleManagementLabel, 0, 0, 1, 0);
 		
 		Label roleSearchHintLabel = new Label();
		roleSearchHintLabel.setCaptionAsHtml(true);
		roleSearchHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Type into the Policy Name field to find a policy.");
		roleSearchHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		roleSearchHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		gridLayout.addComponent(roleSearchHintLabel, 0, 1, 1, 1);
		
		
		Layout controlLayout = this.initControlLayout();
		
    	gridLayout.addComponent(controlLayout, 0, 2, 1, 2);
    	
    	GridLayout formLayout = new GridLayout(2, 4);
    	formLayout.setWidth("100%");
		formLayout.setSpacing(true);
		formLayout.setColumnExpandRatio(0, 1);
		formLayout.setColumnExpandRatio(1, 5);

    	Label policyNameLabel = new Label("Policy Name:");
    	policyNameLabel.setSizeUndefined();
		final DragAndDropWrapper policyNameFieldWrap = initPolicyNameField();
		
		formLayout.addComponent(policyNameLabel, 0, 0);
		formLayout.setComponentAlignment(policyNameLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(policyNameFieldWrap, 1, 0);

		Label descriptionLabel = new Label("Description:");
		descriptionLabel.setSizeUndefined();
		this.descriptionField = new TextArea();
		this.descriptionField.setWidth("70%");
		this.descriptionField.setHeight("60px");
		formLayout.addComponent(descriptionLabel, 0, 1);
		formLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
		formLayout.addComponent(this.descriptionField, 1, 1);
		

		this.linkTypeLabel.setSizeUndefined();
		formLayout.addComponent(this.linkTypeLabel, 0, 2);
		formLayout.setComponentAlignment(this.linkTypeLabel, Alignment.MIDDLE_RIGHT);
		this.linkType.setWidth("70%");
		formLayout.addComponent(this.linkType, 1, 2);
		this.linkTypeLabel.setVisible(false);
		this.linkType.setVisible(false);
		
    	
		this.linkedEntityLabel.setSizeUndefined();
		this.linkedEntity = new TextArea();
		this.linkedEntity.setWidth("70%");
		this.linkedEntity.setHeight("60px");
		
		formLayout.addComponent(this.linkedEntityLabel, 0, 3);
		formLayout.setComponentAlignment(this.linkedEntityLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(linkedEntity, 1, 3);
		this.linkedEntityLabel.setVisible(false);
		this.linkedEntity.setVisible(false);
    			
		gridLayout.addComponent(formLayout,0, 3, 1, 3);		

		Label roleTableHintLabel = new Label();
		roleTableHintLabel.setCaptionAsHtml(true);
		roleTableHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" The Roles table below displays the roles that are assigned the current policy.");
		roleTableHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		roleTableHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		gridLayout.addComponent(roleTableHintLabel, 0, 4, 1, 4);
		
		gridLayout.addComponent(this.roleTable, 0, 5, 1, 5);

		policyAdministrationPanel.setContent(gridLayout);
		layout.addComponent(policyAdministrationPanel);
		
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

		this.associatedRolesPanel.setHeight("500px");
		this.associatedRolesPanel.setWidth("100%");
		
		this.roleTable = new Table();
		this.roleTable.addContainerProperty("Role", String.class, null);
		this.roleTable.addContainerProperty("", Button.class, null);
		this.roleTable.setHeight("550px");
		this.roleTable.setWidth("100%");
		
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
		this.policyDropPanel = new Panel();
		
		Label rolePoliciesLabel = new Label("Role/Policy Associations");
		rolePoliciesLabel.setStyleName(ValoTheme.LABEL_HUGE);
		
		this.policyDropPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		this.policyDropPanel.setHeight("100%");
		this.policyDropPanel.setWidth("100%");
		
		this.rolesCombo = new ComboBox("Roles");
		this.rolesCombo.setWidth("90%");
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
						deleteButton.setIcon(VaadinIcons.TRASH);
						deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
						deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);	
						deleteButton.setDescription("Remove Policy from this Role");
						
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
		this.policyDropTable.setHeight("700px");
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
				deleteButton.setIcon(VaadinIcons.TRASH);
				deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
				deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);	
				deleteButton.setDescription("Remove Policy from this Role");
				
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
					roleDeleteButton.setIcon(VaadinIcons.TRASH);
					roleDeleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);	
					deleteButton.setDescription("Remove Policy from this Role");
					
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
		
		GridLayout layout = new GridLayout();
		layout.setSpacing(true);
		layout.setWidth("100%");
		layout.setHeight("100%");
		
		layout.addComponent(rolePoliciesLabel);
		layout.addComponent(this.rolesCombo);
		
		Label policyDropHintLabel = new Label();
		policyDropHintLabel.setCaptionAsHtml(true);
		policyDropHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Drop a policy into the table below to associate with a role.");
		policyDropHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		policyDropHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		layout.addComponent(policyDropHintLabel);
		
		layout.addComponent(this.policyDropTable);
		
		
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
		this.policyNameField.setWidth("70%");

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
					PolicyManagementPanel.this.linkedEntity.setVisible(true);
					PolicyManagementPanel.this.linkType.setVisible(true);
					PolicyManagementPanel.this.linkTypeLabel.setVisible(true);
					PolicyManagementPanel.this.linkedEntityLabel.setVisible(true);
					PolicyManagementPanel.this.linkType.setValue(PolicyManagementPanel.this.policy.getPolicyLink().getPolicyLinkType().getName());
					PolicyManagementPanel.this.linkedEntity.setValue(PolicyManagementPanel.this.policy.getPolicyLink().getName());
				}
				else
				{
					PolicyManagementPanel.this.linkType.setValue(null);
					PolicyManagementPanel.this.linkedEntity.setValue(new String());
					
            		PolicyManagementPanel.this.linkedEntity.setVisible(false);
            		PolicyManagementPanel.this.linkedEntityLabel.setVisible(false);
            		PolicyManagementPanel.this.linkType.setVisible(false);
					PolicyManagementPanel.this.linkTypeLabel.setVisible(false);
				}
				
				roleTable.removeAllItems();

				// Add all the associated roles to the role table.
				for (final Role role : policy.getRoles())
				{
					Button deleteButton = new Button();
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);	
					deleteButton.setDescription("Remove Policy from this Role");
					
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
				
        		PolicyManagementPanel.this.cancelButton.setVisible(false);
        		PolicyManagementPanel.this.newButton.setVisible(true);
        		PolicyManagementPanel.this.deleteButton.setVisible(true);
			}
		});
		
		return policyNameFieldWrap;
	}

	/**
	 * 
	 */
	protected Layout initControlLayout()
	{
		this.cancelButton.setVisible(false);
		this.deleteButton.setVisible(false);
		
		this.newButton.setIcon(VaadinIcons.PLUS);
		this.newButton.setDescription("Create a New Policy");
    	this.newButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    	this.newButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);	
    	this.newButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	final NewPolicyWindow newPolicyWindow = new NewPolicyWindow(userService, securityService,
            			policyAssociationMappingSearchWindow, policyAssociationFlowSearchWindow,
            			policyAssociationModuleSearchWindow, policyAssociationBusinessStreamSearchWindow);
                UI.getCurrent().addWindow(newPolicyWindow);
                
                newPolicyWindow.addCloseListener(new Window.CloseListener() {
                    // inline close-listener
                    public void windowClose(CloseEvent e) {
                    	PolicyManagementPanel.this.policy = newPolicyWindow.getPolicy();
                		
                    	PolicyManagementPanel.this.policyItem = new BeanItem<Policy>(PolicyManagementPanel.this.policy);
                		PolicyManagementPanel.this.policyNameField.setText(PolicyManagementPanel.this.policy.getName());
                		PolicyManagementPanel.this.policyNameField.setPropertyDataSource(policyItem.getItemProperty("name"));
                		PolicyManagementPanel.this.descriptionField.setPropertyDataSource(policyItem.getItemProperty("description"));
                		
                		if(PolicyManagementPanel.this.policy.getPolicyLink() != null)
                		{
	                		PolicyManagementPanel.this.linkType.setValue(PolicyManagementPanel.this.policy.getPolicyLink().getPolicyLinkType().getName());
	                		PolicyManagementPanel.this.linkedEntity.setValue(PolicyManagementPanel.this.policy.getPolicyLink().getName());
	                		
	                		PolicyManagementPanel.this.linkedEntity.setVisible(true);
                			PolicyManagementPanel.this.linkType.setVisible(true);
                		}
                		else
                		{
                			PolicyManagementPanel.this.linkedEntity.setVisible(false);
                			PolicyManagementPanel.this.linkType.setVisible(false);
                		}

                		PolicyManagementPanel.this.roleTable.removeAllItems();
                		
                		PolicyManagementPanel.this.newButton.setVisible(true);
                		PolicyManagementPanel.this.deleteButton.setVisible(false);
                    }
                });
            }
        });
    	
    	this.deleteButton.setIcon(VaadinIcons.TRASH);
    	this.deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    	this.deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    	this.deleteButton.setDescription("Delete the Current Policy");
    	this.deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		PolicyManagementPanel.this.securityService.deletePolicy(policy);
            		
            		PolicyManagementPanel.this.policyNameField.setText("");
            		PolicyManagementPanel.this.descriptionField.setValue("");
            		PolicyManagementPanel.this.linkType.setValue(null);
            		PolicyManagementPanel.this.linkedEntity.setValue("");
            		
            		PolicyManagementPanel.this.linkedEntity.setVisible(false);
            		
            		Notification.show("Deleted");
            		
            		PolicyManagementPanel.this.cancelButton.setVisible(false);
            		PolicyManagementPanel.this.newButton.setVisible(true);
            		PolicyManagementPanel.this.deleteButton.setVisible(false);
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
    	
    	this.cancelButton.setStyleName(ValoTheme.BUTTON_LINK);
    	this.cancelButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
        		PolicyManagementPanel.this.policyNameField.setText("");
        		PolicyManagementPanel.this.descriptionField.setValue("");
        		PolicyManagementPanel.this.linkType.setValue(null);
        		PolicyManagementPanel.this.linkedEntity.setValue("");
        		PolicyManagementPanel.this.linkedEntity.setVisible(false);
        		
        		PolicyManagementPanel.this.cancelButton.setVisible(false);
        		PolicyManagementPanel.this.newButton.setVisible(true);
        		PolicyManagementPanel.this.deleteButton.setVisible(false);
            }
        });
    	
    	HorizontalLayout controlLayout =new HorizontalLayout();
    	controlLayout.setWidth("100%");
    	Label spacerLabel = new Label("");
    	controlLayout.addComponent(spacerLabel);
    	controlLayout.setExpandRatio(spacerLabel, 0.865f);
    	controlLayout.addComponent(newButton);
    	controlLayout.setExpandRatio(newButton, 0.045f);
    	controlLayout.addComponent(deleteButton);
    	controlLayout.setExpandRatio(deleteButton, 0.045f);
    	controlLayout.addComponent(cancelButton);
    	controlLayout.setExpandRatio(cancelButton, 0.045f);
    	
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
		
		this.policyNameField.clearChoices();
	}

}
