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
import org.ikasan.dashboard.ui.administration.listener.AssociatedPrincipalItemClickListener;
import org.ikasan.dashboard.ui.administration.window.NewRoleWindow;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.vaadin.ui.themes.ValoTheme;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * @author CMI2 Development Team
 * 
 */
public class RoleManagementPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(RoleManagementPanel.class);

	private UserService userService;
	private SecurityService securityService;
	private Panel policyDropPanel;
	private Table policyTable;
	private Button newButton = new Button("New");
	private Button saveButton = new Button("Save");
	private Button deleteButton = new Button("Delete");
	private Role role = new Role();
	private AutocompleteField<Role> roleNameField;
	private AutocompleteField<Policy> policyNameField;
	private TextArea descriptionField;
	private BeanItem<Role> roleItem;
	private DragAndDropWrapper policyNameFieldWrap;
	private Table associatedPrincipalsTable;
	private AssociatedPrincipalItemClickListener associatedPrincipalItemClickListener;
	
	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public RoleManagementPanel(UserService userService, SecurityService securityService,
			AssociatedPrincipalItemClickListener associatedPrincipalItemClickListener)
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
		this.associatedPrincipalItemClickListener = associatedPrincipalItemClickListener;
		if (this.securityService == null)
		{
			throw new IllegalArgumentException(
					"associatedPrincipalItemClickListener cannot be null!");
		}

		init();
	}

	@SuppressWarnings({ "serial" })
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");

		this.initPolicyNameField();
		this.createPolicyDropPanel();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		Panel roleAdministrationPanel = new Panel();
		roleAdministrationPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		roleAdministrationPanel.setHeight("100%");
		roleAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(2, 6);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");
		gridLayout.setMargin(true);
		gridLayout.setSizeFull();
		
		Label roleManagementLabel = new Label("Role Management");
 		roleManagementLabel.setStyleName(ValoTheme.LABEL_HUGE);
 		gridLayout.addComponent(roleManagementLabel, 0, 0, 1, 0);
 		
 		Label roleSearchHintLabel = new Label();
		roleSearchHintLabel.setCaptionAsHtml(true);
		roleSearchHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Type into the Role Name field to find a role.");
		roleSearchHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		roleSearchHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		gridLayout.addComponent(roleSearchHintLabel, 0, 1, 1, 1);
		
		
		Layout controlLayout = this.initControlLayout();
		
    	gridLayout.addComponent(controlLayout, 0, 2, 1, 2);
    	
    	Label roleNameLabel = new Label("Role Name:");
    	roleNameLabel.setSizeUndefined();
		initRoleNameField();
		
		GridLayout formLayout = new GridLayout(2, 2);
		formLayout.setWidth("100%");
		formLayout.setHeight("115px");
		formLayout.setSpacing(true);
		
		formLayout.setColumnExpandRatio(0, 1);
		formLayout.setColumnExpandRatio(1, 5);

		this.roleNameField.setWidth("70%");
		formLayout.addComponent(roleNameLabel, 0, 0);
		formLayout.setComponentAlignment(roleNameLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(this.roleNameField, 1, 0);

		Label descriptionLabel = new Label("Description:");
		descriptionLabel.setSizeUndefined();
		this.descriptionField = new TextArea();
		this.descriptionField.setWidth("70%");
		this.descriptionField.setHeight("60px");
		formLayout.addComponent(descriptionLabel, 0, 1);
		formLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
		formLayout.addComponent(descriptionField, 1, 1);
		
		gridLayout.addComponent(formLayout, 0, 3, 1, 3);
		
		Label roleTableHintLabel = new Label();
		roleTableHintLabel.setCaptionAsHtml(true);
		roleTableHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" The Associated Users/Groups table below displays the users/groups that are assigned the current role.");
		roleTableHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		roleTableHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		gridLayout.addComponent(roleTableHintLabel, 0, 4, 1, 4);
		
		this.associatedPrincipalsTable = new Table();
		this.associatedPrincipalsTable.addItemClickListener(this.associatedPrincipalItemClickListener);
		this.associatedPrincipalsTable.addContainerProperty("Associated Users/Groups", String.class, null);
		this.associatedPrincipalsTable.addContainerProperty("", Button.class, null);
		this.associatedPrincipalsTable.setHeight("600px");
		this.associatedPrincipalsTable.setWidth("650px");
		
		gridLayout.addComponent(this.associatedPrincipalsTable, 0, 5, 1, 5);

		roleAdministrationPanel.setContent(gridLayout);
		layout.addComponent(roleAdministrationPanel);
		
		HorizontalLayout policyDropPanelLayout = new HorizontalLayout();
		policyDropPanelLayout.setMargin(true);
		policyDropPanelLayout.addComponent(this.policyDropPanel);
		policyDropPanelLayout.setSizeFull();
		
		HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
		hsplit.setFirstComponent(layout);
		hsplit.setSecondComponent(policyDropPanelLayout);


		// Set the position of the splitter as percentage
		hsplit.setSplitPosition(65, Unit.PERCENTAGE);
		hsplit.setLocked(true);

		this.setContent(hsplit);
	}

	/**
	 * 
	 */
	protected void createPolicyDropPanel()
	{
		this.policyDropPanel = new Panel();
		 		
		this.policyDropPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
		this.policyDropPanel.setHeight("100%");
		this.policyDropPanel.setWidth("100%");
				
		this.policyTable = new Table();
		this.policyTable.addContainerProperty("Role Policies", String.class, null);
		this.policyTable.addContainerProperty("", Button.class, null);
		this.policyTable.setHeight("685px");
		this.policyTable.setWidth("300px");
		
		this.policyTable.setDragMode(TableDragMode.ROW);
		this.policyTable.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				if(role == null)
				{
					// Do nothing if there is no role selected
					logger.info("Ignoring drop: " + dropEvent);
					return;
				}
				
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

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
				
				deleteButton.addClickListener(new Button.ClickListener() 
		        {
		            public void buttonClick(ClickEvent event) 
		            {	
		            	Policy policy = RoleManagementPanel.this.securityService
								.findPolicyByName(sourceContainer.getText());
		            	
		            	logger.info("Trying to remove policy: " + policy);
						
						Role selectedRole = RoleManagementPanel.this.securityService
								.findRoleByName(RoleManagementPanel.this.roleNameField.getText());
						
						logger.info("From role: " + selectedRole);
						
		            	selectedRole.getPolicies().remove(policy);		            	
		            	RoleManagementPanel.this.saveRole(selectedRole);
		            	
		            	RoleManagementPanel.this.policyTable.removeItem(policy.getName());
		            }
		        });
				
				Policy policy = RoleManagementPanel.this.securityService
						.findPolicyByName(sourceContainer.getText());
				
				Role selectedRole = RoleManagementPanel.this.securityService
						.findRoleByName(RoleManagementPanel.this.roleNameField.getText());

				selectedRole.getPolicies().add(policy);
				
				RoleManagementPanel.this.saveRole(selectedRole);
				
				RoleManagementPanel.this.policyTable.addItem(new Object[]
						{ sourceContainer.getText(), deleteButton}, sourceContainer.getText());

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
		Label associatedPoliciesLabel = new Label("Associated Policies");
		associatedPoliciesLabel.setStyleName(ValoTheme.LABEL_HUGE);
		
		Label policy1HintLabel = new Label();
		policy1HintLabel.setCaptionAsHtml(true);
		policy1HintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Type into the text box below to find a policy.");
		policy1HintLabel.addStyleName(ValoTheme.LABEL_TINY);
		policy1HintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
 		
 		layout.addComponent(associatedPoliciesLabel); 		
		layout.addComponent(policy1HintLabel);
		
		layout.addComponent(policyNameFieldWrap);
		
		Label policy2HintLabel = new Label();
		policy2HintLabel.setCaptionAsHtml(true);
		policy2HintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Drag below to associate policy with current role.");
		policy2HintLabel.addStyleName(ValoTheme.LABEL_TINY);
		policy2HintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		
		layout.addComponent(policy2HintLabel);
		
		layout.addComponent(this.policyTable);
		
		this.policyDropPanel.setContent(layout);
	}

	/**
	 * Helper method to initialise behaviour of the role name field.
	 * 
	 * @return
	 */
	protected void initRoleNameField()
	{
		// The policy field name is an autocomplete field.
		this.roleNameField = new AutocompleteField<Role>();
		this.roleNameField.setWidth("80%");
		
		// In order to have the auto complete work we must add a query listener.
		// The query listener gets activated when a user begins to type into 
		// the field and hits the database looking for suggestions.
		roleNameField.setQueryListener(new AutocompleteQueryListener<Role>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<Role> field,
					String query)
			{
				// Iterate over the returned results and add them as suggestions
				for (Role role : securityService.getRoleByNameLike(query))
				{
					field.addSuggestion(role, role.getName());
				}
			}
		});

		// Once a suggestion is selected the listener below gets fired and we populate
		// associated fields as required.
		roleNameField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<Role>()
		{
			@Override
			public void onSuggestionPicked(final Role pickedRole)
			{
				RoleManagementPanel.this.role = pickedRole;
				
				// Populate all the policy related fields.
				RoleManagementPanel.this.roleItem = new BeanItem<Role>(RoleManagementPanel.this.role);
				RoleManagementPanel.this.roleNameField.setPropertyDataSource(roleItem.getItemProperty("name"));
				RoleManagementPanel.this.descriptionField.setPropertyDataSource(roleItem.getItemProperty("description"));
				
				RoleManagementPanel.this.policyTable.removeAllItems();
								
				for(final Policy policy: role.getPolicies())
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
			            	Policy selectedPolicy = RoleManagementPanel.this.securityService
									.findPolicyByName(policy.getName());
			            								
							Role selectedRole = RoleManagementPanel.this.securityService
									.findRoleByName(role.getName());
							
			            	selectedRole.getPolicies().remove(selectedPolicy);		            	
			            	RoleManagementPanel.this.saveRole(selectedRole);
			            	
			            	RoleManagementPanel.this.policyTable.removeItem(policy.getName());			            	
			            }
			        });
					
					
					RoleManagementPanel.this.policyTable.addItem(new Object[]
							{ policy.getName(), deleteButton }, policy.getName());
				}
				
				RoleManagementPanel.this.associatedPrincipalsTable.removeAllItems();
				
				logger.info("Trying to get pinciplas for role: " + role);
				
				List<IkasanPrincipal> principals = RoleManagementPanel.this.securityService
						.getAllPrincipalsWithRole(role.getName());
				
				logger.info("Adding the following number of principals : " + principals.size());
				
				for(final IkasanPrincipal ikasanPrincipal: principals)
		        {
					Button deleteButton = new Button();
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);			
					deleteButton.setDescription("Unassign this role from the given User/Group");
					
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {	
			            	Role selectedRole = RoleManagementPanel.this.securityService
									.findRoleByName(RoleManagementPanel.this.roleNameField.getText());
							
							logger.info("Removing principal role: " + selectedRole);
							ikasanPrincipal.getRoles().remove(selectedRole);            	
			            	RoleManagementPanel.this.securityService.savePrincipal(ikasanPrincipal);
			            	
			            	RoleManagementPanel.this.associatedPrincipalsTable.removeItem(ikasanPrincipal);
			            }
			        });
					
					RoleManagementPanel.this.associatedPrincipalsTable.addItem(new Object[]
			        		{ ikasanPrincipal.getName(), deleteButton}, ikasanPrincipal);
		        }
	        }
		});
	}

	/**
	 * Helper method to initialise behaviour of the role name field.
	 * 
	 * @return
	 */
	protected void initPolicyNameField()
	{
		// The policy field name is an autocomplete field.
		this.policyNameField = new AutocompleteField<Policy>();
		this.policyNameField.setWidth("80%");
		
		policyNameFieldWrap = new DragAndDropWrapper(
				this.policyNameField);
		policyNameFieldWrap.setDragStartMode(DragStartMode.COMPONENT);
		policyNameFieldWrap.setWidth("80%");
		
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
				// Nothing to do
			}
		});
	}

	/**
	 * 
	 */
	protected Layout initControlLayout()
	{
		this.newButton.setIcon(VaadinIcons.PLUS);
		this.newButton.setDescription("Create a New Role");
    	this.newButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    	this.newButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    	this.newButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	final NewRoleWindow newRoleWindow = new NewRoleWindow(securityService);
                UI.getCurrent().addWindow(newRoleWindow);
                
                newRoleWindow.addCloseListener(new Window.CloseListener() {
                    // inline close-listener
                    public void windowClose(CloseEvent e) {
                    	RoleManagementPanel.this.role = newRoleWindow.getRole();
                		
                    	RoleManagementPanel.this.roleItem = new BeanItem<Role>(RoleManagementPanel.this.role);
                		RoleManagementPanel.this.roleNameField.setText(RoleManagementPanel.this.role.getName());
                		RoleManagementPanel.this.roleNameField.setPropertyDataSource(roleItem.getItemProperty("name"));
                		RoleManagementPanel.this.descriptionField.setPropertyDataSource(roleItem.getItemProperty("description"));
                    }
                });
            }
        });
    	
    	this.saveButton.setStyleName(ValoTheme.BUTTON_LINK);
    	this.saveButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		RoleManagementPanel.this.save();
            		
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
    	
    	this.deleteButton.setIcon(VaadinIcons.TRASH);
    	this.deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    	this.deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    	this.deleteButton.setDescription("Delete the Current Role");
    	this.deleteButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	try
            	{
            		RoleManagementPanel.this.securityService.deleteRole(role);
            		
            		RoleManagementPanel.this.roleNameField.setText("");
            		RoleManagementPanel.this.descriptionField.setValue("");
            		
            		RoleManagementPanel.this.policyTable.removeAllItems();
            		
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
    	controlLayout.setHeight("20px");
    	Label spacerLabel = new Label("");
    	controlLayout.addComponent(spacerLabel);
    	controlLayout.setExpandRatio(spacerLabel, 0.91f);
    	controlLayout.addComponent(newButton);
    	controlLayout.setExpandRatio(newButton, 0.045f);
    	
    	controlLayout.addComponent(deleteButton);
    	controlLayout.setExpandRatio(deleteButton, 0.045f);
    	
    	return controlLayout;
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
		this.securityService.saveRole(this.role);
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
		this.policyNameField.clearChoices();
		this.roleNameField.clearChoices();
		
		if(this.role != null)
		{
			RoleManagementPanel.this.associatedPrincipalsTable.removeAllItems();
			
			logger.info("Trying to get pincipals for role: " + role);
			
			List<IkasanPrincipal> principals = RoleManagementPanel.this.securityService
					.getAllPrincipalsWithRole(role.getName());
			
			logger.info("Adding the following number of principals : " + principals.size());
			
			for(final IkasanPrincipal ikasanPrincipal: principals)
	        {
				Button deleteButton = new Button();
				deleteButton.setIcon(VaadinIcons.TRASH);
				deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
				deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);			
				deleteButton.setDescription("Unassign this role from the given User/Group");
				
				deleteButton.addClickListener(new Button.ClickListener() 
		        {
		            public void buttonClick(ClickEvent event) 
		            {	
		            	Role selectedRole = RoleManagementPanel.this.securityService
								.findRoleByName(RoleManagementPanel.this.roleNameField.getText());
						
						logger.info("Removing principal role: " + selectedRole);
						ikasanPrincipal.getRoles().remove(selectedRole);            	
		            	RoleManagementPanel.this.securityService.savePrincipal(ikasanPrincipal);
		            	
		            	RoleManagementPanel.this.associatedPrincipalsTable.removeItem(ikasanPrincipal);
		            }
		        });
				
				RoleManagementPanel.this.associatedPrincipalsTable.addItem(new Object[]
		        		{ ikasanPrincipal.getName(), deleteButton}, ikasanPrincipal);
	        }
		}
	}
}
