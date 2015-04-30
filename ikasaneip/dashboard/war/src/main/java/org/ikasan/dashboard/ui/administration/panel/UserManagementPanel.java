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

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.listener.AssociatedPrincipalItemClickListener;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
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
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * @author CMI2 Development Team
 * 
 */
public class UserManagementPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(UserManagementPanel.class);

	private UserService userService;
	private SecurityService securityService;
	private ComboBox rolesCombo;
	private AutocompleteField<User> usernameField = new AutocompleteField<User>();
	private AutocompleteField<User> firstName;
	private AutocompleteField<User> surname;
	private PasswordField passwordField = new PasswordField();
	private Table userDropTable = new Table();
	private Table associatedPrincipalsTable = new Table();
	private User user;
	private AssociatedPrincipalItemClickListener associatedPrincipalItemClickListener;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public UserManagementPanel(UserService userService, SecurityService securityService,
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
		if (this.associatedPrincipalItemClickListener == null)
		{
			throw new IllegalArgumentException(
					"associatedPrincipalItemClickListener cannot be null!");
		}

		init();
	}

	@SuppressWarnings("deprecation")
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();

		Panel securityAdministrationPanel = new Panel("User Management");
		securityAdministrationPanel.setStyleName("dashboard");
		securityAdministrationPanel.setHeight("100%");
		securityAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(2, 3);
		gridLayout.setMargin(true);
		gridLayout.setSizeFull();
	
		Label usernameLabel = new Label("Username");
		
		usernameField.setWidth("40%");

		final DragAndDropWrapper usernameFieldWrap = new DragAndDropWrapper(
				usernameField);
		usernameFieldWrap.setDragStartMode(DragStartMode.COMPONENT);

		passwordField = new PasswordField();
		passwordField.setWidth("40%");
		
		firstName = new AutocompleteField<User>();
		firstName.setWidth("40%");
		surname = new AutocompleteField<User>();
		surname.setWidth("40%");
		final TextField department = new TextField();
		department.setWidth("40%");
		final TextField email = new TextField();
		email.setWidth("40%");
		final Table roleTable = new Table();
		roleTable.addContainerProperty("Role", String.class, null);
		roleTable.addContainerProperty("", Button.class, null);
		roleTable.setHeight("400px");
		roleTable.setWidth("200px");

		usernameField.setQueryListener(new AutocompleteQueryListener<User>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<User> field,
					String query)
			{
				for (User user : userService.getUserByUsernameLike(query))
				{
					field.addSuggestion(user, user.getUsername());
				}
			}
		});

		usernameField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<User>()
		{
			@Override
			public void onSuggestionPicked(User user)
			{
				UserManagementPanel.this.user = user;
				passwordField.setValue(user.getPassword());
				firstName.setText(user.getFirstName());
				surname.setText(user.getSurname());
				department.setValue(user.getDepartment());
				email.setValue(user.getEmail());

				final IkasanPrincipal principal = securityService
						.findPrincipalByName(user.getUsername());

				roleTable.removeAllItems();
				
				logger.info("principal = " + principal);
				logger.info("user.getUsername() = " + user.getUsername());
				logger.info("principal.getRoles() = " + principal.getRoles());

				for (final Role role : principal.getRoles())
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
			            	roleTable.removeItem(role);
			            	
			            	principal.getRoles().remove(role);
			            	
			            	securityService.savePrincipal(principal);
			            	
			            	userDropTable.removeItem(principal.getName());
			            }
			        });
					
					roleTable.addItem(new Object[]
					{ role.getName(), deleteButton }, role);
				}
				
				associatedPrincipalsTable.removeAllItems();
				
				for(IkasanPrincipal ikasanPrincipal: user.getPrincipals())
		        {
		        	if(!ikasanPrincipal.getType().equals("user"))
		        	{
			        	associatedPrincipalsTable.addItem(new Object[]
			        		{ ikasanPrincipal.getName() }, ikasanPrincipal);
		        	}
		        }
			}
		});
		
		firstName.setQueryListener(new AutocompleteQueryListener<User>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<User> field,
					String query)
			{
				for (User user : userService.getUserByFirstnameLike(query))
				{
					field.addSuggestion(user, user.getFirstName() + " " + user.getSurname());
				}
			}
		});

		firstName.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<User>()
		{
			@Override
			public void onSuggestionPicked(User user)
			{
				UserManagementPanel.this.user = user;
				usernameField.setText(user.getUsername());
				passwordField.setValue(user.getPassword());
				firstName.setText(user.getFirstName());
				surname.setText(user.getSurname());
				department.setValue(user.getDepartment());
				email.setValue(user.getEmail());

				final IkasanPrincipal principal = securityService
						.findPrincipalByName(user.getUsername());

				roleTable.removeAllItems();

				for (final Role role : principal.getRoles())
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
			            	roleTable.removeItem(role);
			            	
			            	principal.getRoles().remove(role);
			            	
			            	securityService.savePrincipal(principal);
			            	
			            	userDropTable.removeItem(principal.getName());
			            }
			        });
					
					roleTable.addItem(new Object[]
					{ role.getName(), deleteButton }, role);
				}
				
				associatedPrincipalsTable.removeAllItems();
				
				for(IkasanPrincipal ikasanPrincipal: user.getPrincipals())
		        {
		        	if(!ikasanPrincipal.getType().equals("user"))
		        	{
			        	associatedPrincipalsTable.addItem(new Object[]
			        		{ ikasanPrincipal.getName() }, ikasanPrincipal);
		        	}
		        }
			}
		});
		
		surname.setQueryListener(new AutocompleteQueryListener<User>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<User> field,
					String query)
			{
				for (User user : userService.getUserBySurnameLike(query))
				{
					field.addSuggestion(user, user.getFirstName() + " " + user.getSurname());
				}
			}
		});

		surname.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<User>()
		{
			@Override
			public void onSuggestionPicked(User user)
			{
				UserManagementPanel.this.user = user;
				usernameField.setText(user.getUsername());
				passwordField.setValue(user.getPassword());
				firstName.setText(user.getFirstName());
				surname.setText(user.getSurname());
				department.setValue(user.getDepartment());
				email.setValue(user.getEmail());

				final IkasanPrincipal principal = securityService
						.findPrincipalByName(user.getUsername());

				roleTable.removeAllItems();

				for (final Role role : principal.getRoles())
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
			            	roleTable.removeItem(role);
			            	
			            	principal.getRoles().remove(role);
			            	
			            	securityService.savePrincipal(principal);
			            	
			            	userDropTable.removeItem(principal.getName());
			            }
			        });
					
					roleTable.addItem(new Object[]
							{ role.getName(), deleteButton }, role);
					
					associatedPrincipalsTable.removeAllItems();
					
					for(IkasanPrincipal ikasanPrincipal: user.getPrincipals())
			        {
			        	if(!ikasanPrincipal.getType().equals("user"))
			        	{
				        	associatedPrincipalsTable.addItem(new Object[]
				        		{ ikasanPrincipal.getName() }, ikasanPrincipal);
			        	}
			        }
				}
			}
		});
		
		GridLayout formLayout = new GridLayout(2, 6);
		formLayout.setWidth("100%");
		formLayout.setHeight("190px");
		
		formLayout.setColumnExpandRatio(0, 1);
		formLayout.setColumnExpandRatio(1, 5);

		formLayout.addComponent(usernameLabel, 0, 0);
		formLayout.addComponent(usernameFieldWrap, 1, 0);
		
		Label passwordLabel = new Label("Password");
		formLayout.addComponent(passwordLabel, 0, 1);
		formLayout.addComponent(passwordField, 1, 1);
		

		Label firstNameLabel = new Label("First name");
		formLayout.addComponent(firstNameLabel, 0, 2);
		formLayout.addComponent(firstName, 1, 2);

		Label surnameLabel = new Label("Surname");
		formLayout.addComponent(surnameLabel, 0, 3);
		formLayout.addComponent(surname, 1, 3);

		Label departmentLabel = new Label("Department");
		formLayout.addComponent(departmentLabel, 0, 4);
		formLayout.addComponent(department, 1, 4);

		Label emailLabel = new Label("Email address");
		formLayout.addComponent(emailLabel, 0, 5);
		formLayout.addComponent(email, 1, 5);
		
		gridLayout.addComponent(formLayout, 0, 0, 1, 0);
		
		gridLayout.addComponent(new Label("<hr />",ContentMode.HTML),0, 1, 1, 1);

		final ClientSideCriterion acceptCriterion = new SourceIs(usernameField);

		userDropTable.addContainerProperty("Members", String.class, null);
		userDropTable.addContainerProperty("", Button.class, null);
		userDropTable.setHeight("400px");
		userDropTable.setWidth("300px");

		userDropTable.setDragMode(TableDragMode.ROW);
		userDropTable.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				if(rolesCombo.getValue() == null)
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
				
				final IkasanPrincipal principal = securityService.findPrincipalByName(sourceContainer.getText());
				final Role roleToRemove = (Role)rolesCombo.getValue();
				
				deleteButton.addClickListener(new Button.ClickListener() 
		        {
		            public void buttonClick(ClickEvent event) 
		            {
		            	userDropTable.removeItem(principal.getName());
		            	
		            	principal.getRoles().remove(roleToRemove);
		            	
		            	securityService.savePrincipal(principal);
		            	
		            	if(UserManagementPanel.this.usernameField.getText().equals(principal.getName()))
		            	{
		            		roleTable.removeItem(roleToRemove);
		            	}
		            }
		        });
				
				userDropTable.addItem(new Object[]
						{ sourceContainer.getText(), deleteButton}, sourceContainer.getText());
				
				principal.getRoles().add((Role)rolesCombo.getValue());
				
				securityService.savePrincipal(principal);

				roleTable.removeAllItems();
				
				for (final Role role : principal.getRoles())
				{
					Button roleDeleteButton = new Button();
					roleDeleteButton.setIcon(deleteIcon);
					roleDeleteButton.setStyleName(Reindeer.BUTTON_LINK);
					
					roleDeleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {
			            	roleTable.removeItem(role);
			            	
			            	principal.getRoles().remove(role);
			            	
			            	securityService.savePrincipal(principal);
			            	
			            	userDropTable.removeItem(principal.getName());
			            }
			        }); 
					
					roleTable.addItem(new Object[]
					 { role.getName(), roleDeleteButton }, role);
				}
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		gridLayout.addComponent(roleTable, 0, 2);
		
		this.associatedPrincipalsTable.addContainerProperty("Associated Principals", String.class, null);
		this.associatedPrincipalsTable.addItemClickListener(this.associatedPrincipalItemClickListener);
		associatedPrincipalsTable.setHeight("400px");
		associatedPrincipalsTable.setWidth("450px");
		
		gridLayout.addComponent(this.associatedPrincipalsTable, 1, 2);
					
		this.rolesCombo = new ComboBox();
		this.rolesCombo.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        final Role role = (Role)event.getProperty().getValue();
		        
		        if(role != null)
		        {		        
			        logger.info("Value changed got Role: " + role);
			        
			        List<IkasanPrincipal> principals = securityService.getAllPrincipalsWithRole(role.getName());
					
					userDropTable.removeAllItems();
					
					for(final IkasanPrincipal principal: principals)
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
				            	userDropTable.removeItem(principal.getName());
				            	
				            	principal.getRoles().remove(role);
				            	
				            	securityService.savePrincipal(principal);
				            	
				            	if(UserManagementPanel.this.usernameField.getText().equals(principal.getName()))
				            	{
				            		roleTable.removeItem(role);
				            	}
				            }
				        });
						
						
						userDropTable.addItem(new Object[]
								{ principal.getName(), deleteButton }, principal.getName());
					}
		        }
		    }
		});
		
		Panel roleMemberPanel = new Panel("Role/Member Associations");
		
		roleMemberPanel.setStyleName("dashboard");
		roleMemberPanel.setHeight("100%");
		roleMemberPanel.setWidth("100%");
		
		VerticalLayout roleMemberLayout = new VerticalLayout();
		roleMemberLayout.setMargin(true);
		roleMemberLayout.setWidth("100%");
		roleMemberLayout.setHeight("100%");
		roleMemberLayout.addComponent(this.rolesCombo);
		roleMemberLayout.setExpandRatio(this.rolesCombo, 0.05f);
		roleMemberLayout.addComponent(this.userDropTable);
		roleMemberLayout.setExpandRatio(this.userDropTable, 0.95f);
		
		roleMemberPanel.setContent(roleMemberLayout);

		securityAdministrationPanel.setContent(gridLayout);
		layout.addComponent(securityAdministrationPanel);
		
		VerticalLayout roleMemberPanelLayout = new VerticalLayout();
		roleMemberPanelLayout.setWidth("100%");
		roleMemberPanelLayout.setHeight("100%");
		roleMemberPanelLayout.setMargin(true);
		roleMemberPanelLayout.addComponent(roleMemberPanel);
		roleMemberPanelLayout.setSizeFull();
		
		HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
		hsplit.setFirstComponent(layout);
		hsplit.setSecondComponent(roleMemberPanelLayout);


		// Set the position of the splitter as percentage
		hsplit.setSplitPosition(65, Unit.PERCENTAGE);
		hsplit.setLocked(true);
		
		this.setContent(hsplit);
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
		
		this.usernameField.clearChoices();
		this.firstName.clearChoices();
		this.surname.clearChoices();
		this.rolesCombo.removeAllItems();
		this.userDropTable.removeAllItems();
		
		for(Role role: roles)
		{
			this.rolesCombo.addItem(role);
			this.rolesCombo.setItemCaption(role, role.getName());
		}
	}
}
