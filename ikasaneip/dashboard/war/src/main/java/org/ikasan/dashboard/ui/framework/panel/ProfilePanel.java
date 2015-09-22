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
package org.ikasan.dashboard.ui.framework.panel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.administration.listener.AssociatedPrincipalItemClickListener;
import org.ikasan.dashboard.ui.framework.constants.SystemEventConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author CMI2 Development Team
 * 
 */
public class ProfilePanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(ProfilePanel.class);

	private UserService userService;
	private SecurityService securityService;
	private TextField usernameField = new TextField();
	private TextField firstName;
	private TextField surname;
	private Table dashboadActivityTable = new Table();
	private Table associatedPrincipalsTable = new Table();
	private User user;
	private AssociatedPrincipalItemClickListener associatedPrincipalItemClickListener;
	private TextField department = new TextField();
	private TextField email = new TextField();
	private Table roleTable = new Table();
	private Table permissionChangeTable = new Table();
	private SystemEventService systemEventService;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public ProfilePanel(UserService userService, SecurityService securityService,
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
	
		Label mappingConfigurationLabel = new Label("User Profile");
 		mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
 		gridLayout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);
 			 		
		Label usernameLabel = new Label("Username:");
		
		usernameField.setWidth("65%");
		
		firstName = new TextField();
		firstName.setWidth("65%");
		firstName.setNullRepresentation("");
		surname = new TextField();
		surname.setWidth("65%");
		surname.setNullRepresentation("");
		department.setWidth("65%");
		department.setNullRepresentation("");
		email.setWidth("65%");
		email.setNullRepresentation("");
		
		roleTable.addContainerProperty("Role", String.class, null);
		roleTable.addStyleName("ikasan");
		roleTable.addStyleName(ValoTheme.TABLE_SMALL);
		roleTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		roleTable.setHeight("520px");
		roleTable.setWidth("250px");
		
		
		GridLayout formLayout = new GridLayout(2, 5);
		formLayout.setSpacing(true);
		formLayout.setWidth("100%");
		
		formLayout.setColumnExpandRatio(0, .1f);
		formLayout.setColumnExpandRatio(1, .8f);

		usernameLabel.setSizeUndefined();
		formLayout.addComponent(usernameLabel, 0, 0);
		formLayout.setComponentAlignment(usernameLabel, Alignment.MIDDLE_RIGHT);
		formLayout.addComponent(usernameField, 1, 0);		

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
				" The Roles table below displays the Ikasan roles that the user has.");
		rolesAndGroupsHintLabel1.addStyleName(ValoTheme.LABEL_TINY);
		rolesAndGroupsHintLabel1.addStyleName(ValoTheme.LABEL_LIGHT);
		rolesAndGroupsHintLabel1.setWidth(300, Unit.PIXELS);
		gridLayout.addComponent(rolesAndGroupsHintLabel1, 0, 3, 1, 3);
		
		Label rolesAndGroupsHintLabel2 = new Label();
		rolesAndGroupsHintLabel2.setCaptionAsHtml(true);
		rolesAndGroupsHintLabel2.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" The Groups table below displays all the LDAP groups that the user is a member of.");
		
		rolesAndGroupsHintLabel2.addStyleName(ValoTheme.LABEL_TINY);
		rolesAndGroupsHintLabel2.addStyleName(ValoTheme.LABEL_LIGHT);
		rolesAndGroupsHintLabel2.setWidth(300, Unit.PIXELS);
		gridLayout.addComponent(rolesAndGroupsHintLabel2, 0, 4, 1, 4);
		
		dashboadActivityTable.addContainerProperty("Action", String.class, null);
		dashboadActivityTable.addContainerProperty("Date/Time", String.class, null);
		dashboadActivityTable.addStyleName("ikasan");
		dashboadActivityTable.addStyleName(ValoTheme.TABLE_SMALL);
		dashboadActivityTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		dashboadActivityTable.setHeight("350px");
		dashboadActivityTable.setWidth("300px");
		
		this.permissionChangeTable.addContainerProperty("Action", String.class, null);
		this.permissionChangeTable.addContainerProperty("Date/Time", String.class, null);
		this.permissionChangeTable.addStyleName("ikasan");
		this.permissionChangeTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.permissionChangeTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.permissionChangeTable.setHeight("350px");
		this.permissionChangeTable.setWidth("300px");

				
		gridLayout.addComponent(roleTable, 0, 5);
		
		this.associatedPrincipalsTable.addContainerProperty("Groups", String.class, null);
		this.associatedPrincipalsTable.addItemClickListener(this.associatedPrincipalItemClickListener);
		this.associatedPrincipalsTable.addStyleName("ikasan");
		this.associatedPrincipalsTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.associatedPrincipalsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		associatedPrincipalsTable.setHeight("520px");
		associatedPrincipalsTable.setWidth("400px");
		
		gridLayout.addComponent(this.associatedPrincipalsTable, 1, 5);
					
		
		Panel roleMemberPanel = new Panel();
 		
		roleMemberPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		roleMemberPanel.setHeight("100%");
		roleMemberPanel.setWidth("100%");
		
		GridLayout roleMemberLayout = new GridLayout();
		roleMemberLayout.setSpacing(true);
		roleMemberLayout.setWidth("100%");
		
		Label dashboardActivityLabel = new Label("Dashboard Activity");
		dashboardActivityLabel.setStyleName(ValoTheme.LABEL_HUGE);
		
		
 		roleMemberLayout.addComponent(dashboardActivityLabel);
		roleMemberLayout.addComponent(this.dashboadActivityTable);
		
		Label permissionChangeLabel = new Label("User Security Changes");
		permissionChangeLabel.setStyleName(ValoTheme.LABEL_HUGE);
		
		roleMemberLayout.addComponent(permissionChangeLabel);
		roleMemberLayout.addComponent(this.permissionChangeTable);
		
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
		
		this.dashboadActivityTable.removeAllItems();
		
		IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
            	.getAttribute(DashboardSessionValueConstants.USER);
		
		this.user = (User)ikasanAuthentication.getPrincipal();
		
		usernameField.setValue(user.getUsername());
		firstName.setValue(user.getFirstName());
		surname.setValue(user.getSurname());
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
	            	
	            	dashboadActivityTable.removeItem(principal.getName());
	            }
	        });
			
			roleTable.addItem(new Object[]
					{ role.getName()}, role);
			
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
		
		ArrayList<String> subjects = new ArrayList<String>();
		subjects.add(SystemEventConstants.DASHBOARD_LOGIN_CONSTANTS);
		subjects.add(SystemEventConstants.DASHBOARD_LOGOUT_CONSTANTS);
		subjects.add(SystemEventConstants.DASHBOARD_SESSION_EXPIRED_CONSTANTS);
		
		List<SystemEvent> events = this.systemEventService.listSystemEvents(subjects, user.getUsername(), null, null);
		
		for(SystemEvent systemEvent: events)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			
			String date = dateFormat.format(systemEvent.getTimestamp());
			
			dashboadActivityTable.addItem(new Object[]
					{ systemEvent.getAction(), date}, systemEvent);
		}

		subjects = new ArrayList<String>();
		subjects.add(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS);

		
		events = this.systemEventService.listSystemEvents(subjects, user.getUsername(), null, null);
		
		for(SystemEvent systemEvent: events)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			
			String date = dateFormat.format(systemEvent.getTimestamp());
			
			this.permissionChangeTable.addItem(new Object[]
					{ systemEvent.getAction(), date}, systemEvent);
		}
	}
}
