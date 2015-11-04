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

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.Menu;
import org.ikasan.dashboard.ui.framework.action.LogoutAction;
import org.ikasan.dashboard.ui.framework.constants.SystemEventConstants;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.util.CommitHandler;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.framework.window.LoginDialog;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Ikasan Development Team
 * 
 */
public class NavigationPanel extends Panel implements ViewContext, CommitHandler
{

	private static final long serialVersionUID = 5649279357596506519L;

	/** Logger instance */
	private static Logger logger = Logger.getLogger(NavigationPanel.class);

	private AuthenticationService authenticationService;
	private VisibilityGroup visibilityGroup;
	private Button loginButton;
	private Button logoutButton;
	private Button setupButton;
	private EditableGroup editableGroup;
	private GridLayout layout = new GridLayout(5, 1);
	private FunctionalGroup newMappingConfigurationFunctionalGroup;
	private FunctionalGroup existingMappingConfigurationFunctionalGroup;
	private Button collapseButton;
	private Button expandButton;
	private VerticalLayout imagePanelLayout;
	private Label loggedInUserLabel;
	private HashMap<String, IkasanUINavigator> views;
	private String currentView;
	private List<RefreshGroup> refreshGroups;
	private Component toggleButton = new Button();
	private Menu menu;
	private SystemEventService systemEventService;
	private UserService userService; 

	/**
	 * 
	 * @param authenticationService
	 * @param visibilityGroup
	 * @param editableGroup
	 * @param newMappingConfigurationFunctionalGroup
	 * @param existingMappingConfigurationFunctionalGroup
	 * @param imagePanelLayout
	 * @param views
	 */
	public NavigationPanel(AuthenticationService authenticationService,
			VisibilityGroup visibilityGroup,
			EditableGroup editableGroup,
			FunctionalGroup newMappingConfigurationFunctionalGroup,
			FunctionalGroup existingMappingConfigurationFunctionalGroup,
			VerticalLayout imagePanelLayout,
			HashMap<String, IkasanUINavigator> views,
			List<RefreshGroup> refreshGroups,
			SystemEventService systemEventService, UserService userService)
	{
		this.authenticationService = authenticationService;
		this.visibilityGroup = visibilityGroup;
		this.editableGroup = editableGroup;
		this.newMappingConfigurationFunctionalGroup = newMappingConfigurationFunctionalGroup;
		this.existingMappingConfigurationFunctionalGroup = existingMappingConfigurationFunctionalGroup;
		this.imagePanelLayout = imagePanelLayout;
		this.views = views;
		this.refreshGroups = refreshGroups;
		this.systemEventService = systemEventService;
		this.userService = userService;
		init();
	}

	/**
	 * Helper method to initialise the object.
	 */
	protected void init()
	{
		logger.debug("Initialising navigation panel.");

		this.setWidth(100, Unit.PERCENTAGE);
		this.setHeight(30, Unit.PIXELS);
		this.setStyleName("navigation");
		
		this.layout.setWidth(97, Unit.PERCENTAGE);
		this.layout.setHeight(100, Unit.PERCENTAGE);
		
		this.layout.setColumnExpandRatio(0, .05f);
		this.layout.setColumnExpandRatio(1, .905f);
		this.layout.setColumnExpandRatio(2, .015f);
		this.layout.setColumnExpandRatio(3, .015f);
		this.layout.setColumnExpandRatio(4, .015f);
		this.layout.addStyleName("valo-menuitems");


		final LoginDialog dialog = new LoginDialog(this.authenticationService, visibilityGroup,
				this, userService);

		this.loginButton = new Button("Login");
		this.loginButton.setPrimaryStyleName("valo-menu-item");
		this.loginButton.setHtmlContentAllowed(true);
		this.loginButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				UI.getCurrent().addWindow(dialog);
			}
		});
		
		this.layout.addComponent(this.loginButton, 2, 0);
		this.layout.setComponentAlignment(this.loginButton,
				Alignment.MIDDLE_RIGHT);


		logoutButton = new Button("Sign out");
		this.logoutButton.setPrimaryStyleName("valo-menu-item");
		this.logoutButton.setHtmlContentAllowed(true);
		this.logoutButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				manageLogout();
			}
		});
		
		this.setupButton = new Button("Setup");
		this.setupButton.setPrimaryStyleName("valo-menu-item");
		this.setupButton.setHtmlContentAllowed(true);
		this.setupButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				UI.getCurrent().getNavigator().navigateTo("persistanceSetupView");
			}
		});
		this.layout.addComponent(this.setupButton, 3, 0);
		this.layout.setComponentAlignment(this.setupButton,
				Alignment.MIDDLE_RIGHT);

		this.collapseButton = new Button("^");
		this.collapseButton.setPrimaryStyleName("valo-menu-item");
		this.collapseButton.setHtmlContentAllowed(true);
		this.collapseButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				imagePanelLayout.setVisible(false);
				layout.removeComponent(collapseButton);
				layout.addComponent(expandButton, 4, 0);
				layout.setComponentAlignment(expandButton,
						Alignment.MIDDLE_RIGHT);
			}
		});

		this.expandButton = new Button("+");
		this.expandButton.setPrimaryStyleName("valo-menu-item");
		this.expandButton.setHtmlContentAllowed(true);
		this.layout.addComponent(this.expandButton, 4, 0);
		this.layout.setComponentAlignment(this.expandButton,
				Alignment.MIDDLE_RIGHT);
		imagePanelLayout.setVisible(false);
		this.expandButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				imagePanelLayout.setVisible(true);
				layout.removeComponent(expandButton);
				layout.addComponent(collapseButton, 4, 0);
				layout.setComponentAlignment(collapseButton,
						Alignment.MIDDLE_RIGHT);
			}
		});

		this.setContent(layout);
	}

	/**
	 * Method to manage a logout
	 */
	protected void manageLogout()
	{
		LogoutAction action = new LogoutAction(this.visibilityGroup,
				this.editableGroup, this.layout, this.loginButton, 
				this.setupButton, this.logoutButton, this.loggedInUserLabel, this, this.systemEventService);

		IkasanMessageDialog dialog = new IkasanMessageDialog("Logout",
				"You are about to log out. Any unsaved data will be lost. "
						+ "Are you sure you wish to proceed?.", action);

		UI.getCurrent().addWindow(dialog);
	}

	/**
	 * Method to set component state post commit.
	 * 
	 * @throws CommitException
	 */
	public void postCommit() throws CommitException
	{
		this.layout.removeComponent(this.loginButton);
		this.layout.removeComponent(this.setupButton);
		IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
        	.getAttribute(DashboardSessionValueConstants.USER);
		
		
		loggedInUserLabel = new Label("");
		loggedInUserLabel.setStyleName("ikasan-white");
		loggedInUserLabel.setVisible(true);
		this.layout.addComponent(loggedInUserLabel, 1, 0);
		this.layout.setComponentAlignment(loggedInUserLabel, Alignment.MIDDLE_LEFT);
		this.layout.setComponentAlignment(loggedInUserLabel,
				Alignment.MIDDLE_RIGHT);
		this.layout.addComponent(this.logoutButton, 2, 0);
		this.layout.setComponentAlignment(this.logoutButton, Alignment.MIDDLE_RIGHT);

		this.newMappingConfigurationFunctionalGroup.initialiseButtonState();
		this.existingMappingConfigurationFunctionalGroup
				.initialiseButtonState();
		
		if(this.menu != null)
		{
			this.menu.setLoggedIn();
		}
		
		systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_LOGIN_CONSTANTS, 
        		"User logging in: " + ikasanAuthentication.getName(), ikasanAuthentication.getName());

		UI.getCurrent().getNavigator().navigateTo("landingView");
	}

	/**
	 * @param menu the menu to set
	 */
	public void setMenu(Menu menu)
	{
		this.menu = menu;
	}
	
	protected void refresh()
	{
		for(RefreshGroup refreshGroup: this.refreshGroups)
		{
			refreshGroup.refresh();
		}
	}
	
	public void setToggleButton(Component toggleButton)
	{
		this.layout.removeComponent(this.toggleButton);
		this.toggleButton = toggleButton;
		this.layout.addComponent(this.toggleButton, 0, 0);
		this.layout.setComponentAlignment(this.toggleButton,
				Alignment.MIDDLE_LEFT);
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.layout.setVisible(visible);
		
		if(this.visibilityGroup != null)
		{
			this.visibilityGroup.setVisible();
		}
	}

	public void setCurrentView(String currentView)
	{
		this.currentView = currentView;
	}

	public void resetCurrentView()
	{
		this.currentView = null;
	}
	
	public void reset()
	{
		currentView = null;
	}

	/**
	 * @param menuComponents the menuComponents to set
	 */
	public void setMenuComponents(HashMap<Component, String> menuComponents)
	{
		this.visibilityGroup.getComponents().putAll(menuComponents);
	}


	public void enter()
	{
		this.visibilityGroup.setVisible();
	}

}
