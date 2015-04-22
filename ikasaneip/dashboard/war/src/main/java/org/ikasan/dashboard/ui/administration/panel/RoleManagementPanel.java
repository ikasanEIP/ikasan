/*
 * $Id: EstateViewPanel.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/EstateViewPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.administration.panel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.window.NewRoleWindow;
import org.ikasan.security.model.Policy;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
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
import com.vaadin.ui.themes.Reindeer;
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
	private Table policyTable;;
	private Button newButton = new Button("New");
	private Button saveButton = new Button("Save");
	private Button deleteButton = new Button("Delete");
	private Role role = new Role();
	private AutocompleteField<Role> roleNameField;
	private AutocompleteField<Policy> policyNameField;
	private TextArea descriptionField;
	private BeanItem<Role> roleItem;
	private DragAndDropWrapper policyNameFieldWrap;
	
	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public RoleManagementPanel(UserService userService, SecurityService securityService)
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
		layout.setMargin(true);
		layout.setSizeFull();

		Panel securityAdministrationPanel = new Panel("Role Management");
		securityAdministrationPanel.setStyleName("dashboard");
		securityAdministrationPanel.setHeight("100%");
		securityAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(3, 16);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");
		gridLayout.setMargin(true);
		gridLayout.setSizeFull();
		
		
		Layout controlLayout = this.initControlLayout();
		
    	gridLayout.addComponent(controlLayout, 0, 0, 1, 0);
    	
    	Label policyNameLabel = new Label("Role Name");
		initRoleNameField();
		
		gridLayout.addComponent(policyNameLabel, 0, 1);
		gridLayout.addComponent(this.roleNameField, 1, 1);

		Label descriptionLabel = new Label("Description");
		this.descriptionField = new TextArea();
		this.descriptionField.setWidth("80%");
		this.descriptionField.setHeight("60px");
		gridLayout.addComponent(descriptionLabel, 0, 2);
		gridLayout.addComponent(descriptionField, 1, 2);  	
					
		gridLayout.addComponent(this.policyDropPanel, 2, 0, 2, 15);

		securityAdministrationPanel.setContent(gridLayout);
		layout.addComponent(securityAdministrationPanel);
		this.setContent(layout);
	}

	/**
	 * 
	 */
	protected void createPolicyDropPanel()
	{
		this.policyDropPanel = new Panel();
		
		this.policyDropPanel.setStyleName("dashboard");
		this.policyDropPanel.setHeight("600px");
		this.policyDropPanel.setWidth("100%");
				
		this.policyTable = new Table();
		this.policyTable.addContainerProperty("Role Policies", String.class, null);
		this.policyTable.addContainerProperty("", Button.class, null);
		this.policyTable.setHeight("400px");
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
				ThemeResource deleteIcon = new ThemeResource(
						"images/remove-icon.png");
				deleteButton.setIcon(deleteIcon);
				deleteButton.setStyleName(Reindeer.BUTTON_LINK);				
				
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
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setWidth("100%");
		layout.setHeight("100%");
		layout.addComponent(this.policyNameFieldWrap);
		layout.setExpandRatio(this.policyNameFieldWrap, 0.05f);
		layout.addComponent(this.policyTable);
		layout.setExpandRatio(this.policyTable, 0.95f);
		
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
					ThemeResource deleteIcon = new ThemeResource(
							"images/remove-icon.png");
					deleteButton.setIcon(deleteIcon);
					deleteButton.setStyleName(Reindeer.BUTTON_LINK);
					
					deleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {
			            	Policy selectedPolicy = RoleManagementPanel.this.securityService
									.findPolicyByName(policy.getName());
			            	
			            	logger.info("Trying to remove policy: " + selectedPolicy);
							
							Role selectedRole = RoleManagementPanel.this.securityService
									.findRoleByName(role.getName());
							
							logger.info("From role: " + selectedRole);
							
			            	selectedRole.getPolicies().remove(selectedPolicy);		            	
			            	RoleManagementPanel.this.saveRole(selectedRole);
			            	
			            	RoleManagementPanel.this.policyTable.removeItem(policy.getName());			            	
			            }
			        });
					
					
					RoleManagementPanel.this.policyTable.addItem(new Object[]
							{ policy.getName(), deleteButton }, policy.getName());
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
		this.newButton.setStyleName(Reindeer.BUTTON_LINK);
    	this.newButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	final NewRoleWindow newRoleWindow = new NewRoleWindow();
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
    	
    	this.saveButton.setStyleName(Reindeer.BUTTON_LINK);
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
    	
    	this.deleteButton.setStyleName(Reindeer.BUTTON_LINK);
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
	}
}
