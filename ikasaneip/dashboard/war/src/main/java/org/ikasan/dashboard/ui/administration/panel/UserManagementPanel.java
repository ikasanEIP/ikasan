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

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.listener.AssociatedPrincipalItemClickListener;
import org.ikasan.dashboard.ui.framework.constants.SystemEventConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.vaadin.teemu.VaadinIcons;

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
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
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
	private Table userDropTable = new Table();
	private Table associatedPrincipalsTable = new Table();
	private User user;
	private AssociatedPrincipalItemClickListener associatedPrincipalItemClickListener;
	private SystemEventService systemEventService;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public UserManagementPanel(UserService userService, SecurityService securityService,
			AssociatedPrincipalItemClickListener associatedPrincipalItemClickListener,
			SystemEventService systemEventService)
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
		this.systemEventService = systemEventService;
		if (this.systemEventService == null)
		{
			throw new IllegalArgumentException(
					"systemEventService cannot be null!");
		}

		init();
	}

	@SuppressWarnings("deprecation")
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		Panel securityAdministrationPanel = new Panel();
		securityAdministrationPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		securityAdministrationPanel.setHeight("100%");
		securityAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(2, 6);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		gridLayout.setSizeFull();
	
		Label mappingConfigurationLabel = new Label("User Management");
 		mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
 		gridLayout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);
 		
 		Label userSearchHintLabel = new Label();
		userSearchHintLabel.setCaptionAsHtml(true);
		userSearchHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Type into the Username, Firstname or Surname fields to find a user.");
		userSearchHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		userSearchHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		gridLayout.addComponent(userSearchHintLabel, 0, 1, 1, 1);
	 		
		Label usernameLabel = new Label("Username:");
		
		usernameField.setWidth("40%");

		final DragAndDropWrapper usernameFieldWrap = new DragAndDropWrapper(
				usernameField);
		usernameFieldWrap.setDragStartMode(DragStartMode.COMPONENT);

		
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
		roleTable.setHeight("520px");
		roleTable.setWidth("250px");

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
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					
					
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
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					
					
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
					deleteButton.setIcon(VaadinIcons.TRASH);
					deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
					deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					
					
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
		
		GridLayout formLayout = new GridLayout(2, 5);
		formLayout.setSpacing(true);
		formLayout.setWidth("100%");
		
		formLayout.setColumnExpandRatio(0, .1f);
		formLayout.setColumnExpandRatio(1, .8f);

		usernameLabel.setSizeUndefined();
		formLayout.addComponent(usernameLabel, 0, 0);
		formLayout.setComponentAlignment(usernameLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(usernameFieldWrap, 1, 0);		

		Label firstNameLabel = new Label("First name:");
		firstNameLabel.setSizeUndefined();
		formLayout.addComponent(firstNameLabel, 0, 1);
		formLayout.setComponentAlignment(firstNameLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(firstName, 1, 1);

		Label surnameLabel = new Label("Surname:");
		surnameLabel.setSizeUndefined();
		formLayout.addComponent(surnameLabel, 0, 2);
		formLayout.setComponentAlignment(surnameLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(surname, 1, 2);

		Label departmentLabel = new Label("Department:");
		departmentLabel.setSizeUndefined();
		formLayout.addComponent(departmentLabel, 0, 3);
		formLayout.setComponentAlignment(departmentLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(department, 1, 3);

		Label emailLabel = new Label("Email address:");
		emailLabel.setSizeUndefined();
		formLayout.addComponent(emailLabel, 0, 4);
		formLayout.setComponentAlignment(emailLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(email, 1, 4);
		
		gridLayout.addComponent(formLayout, 0, 2, 1, 2);
		
		Label rolesAndGroupsHintLabel1 = new Label();
		rolesAndGroupsHintLabel1.setCaptionAsHtml(true);
		rolesAndGroupsHintLabel1.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" The Roles table below displays the roles that the user has. Roles can be deleted from this table.");
		rolesAndGroupsHintLabel1.addStyleName(ValoTheme.LABEL_TINY);
		rolesAndGroupsHintLabel1.addStyleName(ValoTheme.LABEL_LIGHT);
		rolesAndGroupsHintLabel1.setWidth(300, Unit.PIXELS);
		gridLayout.addComponent(rolesAndGroupsHintLabel1, 0, 3, 1, 3);
		
		Label rolesAndGroupsHintLabel2 = new Label();
		rolesAndGroupsHintLabel2.setCaptionAsHtml(true);
		rolesAndGroupsHintLabel2.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" The Groups table below displays all the LDAP groups that the user is a member of. You cannot manage these " +
				"from this application.");
		rolesAndGroupsHintLabel2.addStyleName(ValoTheme.LABEL_TINY);
		rolesAndGroupsHintLabel2.addStyleName(ValoTheme.LABEL_LIGHT);
		rolesAndGroupsHintLabel2.setWidth(300, Unit.PIXELS);
		gridLayout.addComponent(rolesAndGroupsHintLabel2, 0, 4, 1, 4);
		
		final ClientSideCriterion acceptCriterion = new SourceIs(usernameField);

		userDropTable.addContainerProperty("Members", String.class, null);
		userDropTable.addContainerProperty("", Button.class, null);
		userDropTable.setHeight("685px");
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
				
				deleteButton.setIcon(VaadinIcons.TRASH);
				deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
				deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
				
				
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
		            	
		            	IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
		                    	.getAttribute(DashboardSessionValueConstants.USER);
		            	
		            	String action = "Role " + roleToRemove.getName() + " removed by " + ikasanAuthentication.getName();
		            	
		            	systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, usernameField.getText());
		            }
		        });
				
				userDropTable.addItem(new Object[]
						{ sourceContainer.getText(), deleteButton}, sourceContainer.getText());
				
				principal.getRoles().add((Role)rolesCombo.getValue());
				
				securityService.savePrincipal(principal);
				
				IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                    	.getAttribute(DashboardSessionValueConstants.USER);
            	
            	String action = "Role " + ((Role)rolesCombo.getValue()).getName() + " added by " + ikasanAuthentication.getName();
            	
            	systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, usernameField.getText());

				roleTable.removeAllItems();
				
				for (final Role role : principal.getRoles())
				{
					Button roleDeleteButton = new Button();
					roleDeleteButton.setIcon(VaadinIcons.TRASH);
					roleDeleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
					roleDeleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
					
					roleDeleteButton.addClickListener(new Button.ClickListener() 
			        {
			            public void buttonClick(ClickEvent event) 
			            {
			            	roleTable.removeItem(role);
			            	
			            	principal.getRoles().remove(role);
			            	
			            	securityService.savePrincipal(principal);
			            	
			            	userDropTable.removeItem(principal.getName());
			            	
			            	IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
			                    	.getAttribute(DashboardSessionValueConstants.USER);
			            	
			            	String action = "Role " + role.getName() + " removed by " + ikasanAuthentication.getName();
			            	
			            	systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, usernameField.getText());
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
		
		gridLayout.addComponent(roleTable, 0, 5);
		
		this.associatedPrincipalsTable.addContainerProperty("Groups", String.class, null);
		this.associatedPrincipalsTable.addItemClickListener(this.associatedPrincipalItemClickListener);
		associatedPrincipalsTable.setHeight("520px");
		associatedPrincipalsTable.setWidth("400px");
		
		gridLayout.addComponent(this.associatedPrincipalsTable, 1, 5);
					
		this.rolesCombo = new ComboBox("Roles");
		this.rolesCombo.setWidth("90%");
		this.rolesCombo.addValueChangeListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        final Role role = (Role)event.getProperty().getValue();
		        
		        if(role != null)
		        {		        
			        logger.debug("Value changed got Role: " + role);
			        
			        List<IkasanPrincipal> principals = securityService.getAllPrincipalsWithRole(role.getName());
					
					userDropTable.removeAllItems();
					
					for(final IkasanPrincipal principal: principals)
					{
						Button deleteButton = new Button();
						deleteButton.setIcon(VaadinIcons.TRASH);
						deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
						deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
						
						
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
				            	
				            	IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
				                    	.getAttribute(DashboardSessionValueConstants.USER);
				            	
				            	String action = "Role " + role.getName() + " removed by " + ikasanAuthentication.getName();
				            	
				            	systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, usernameField.getText());
				            }
				        });
						
						
						userDropTable.addItem(new Object[]
								{ principal.getName(), deleteButton }, principal.getName());
					}
		        }
		    }
		});
		
		Panel roleMemberPanel = new Panel();
 		
		roleMemberPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		roleMemberPanel.setHeight("100%");
		roleMemberPanel.setWidth("100%");
		
		GridLayout roleMemberLayout = new GridLayout();
		roleMemberLayout.setSpacing(true);
		roleMemberLayout.setWidth("100%");
		
		Label roleMemberAssociationsLabel = new Label("Role/Member Associations");
		roleMemberAssociationsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		
		Label userDragHintLabel = new Label();
		userDragHintLabel.setCaptionAsHtml(true);
		userDragHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Drop users into the table below to assign them the role.");
		
 		roleMemberLayout.addComponent(roleMemberAssociationsLabel);
 		roleMemberLayout.addComponent(userDragHintLabel);
		roleMemberLayout.addComponent(this.rolesCombo);
		roleMemberLayout.addComponent(this.userDropTable);
		
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
