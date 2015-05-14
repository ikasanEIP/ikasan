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
import org.ikasan.dashboard.ui.framework.action.LogoutAction;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.RefreshGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.navigation.Navigation;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.framework.window.LoginDialog;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.authentication.IkasanAuthentication;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author Ikasan Development Team
 * 
 */
public class NavigationPanel extends Panel implements ViewContext, Navigation
{

	private static final long serialVersionUID = 5649279357596506519L;

	/** Logger instance */
	private static Logger logger = Logger.getLogger(NavigationPanel.class);

	private AuthenticationService authenticationService;
	private VisibilityGroup visibilityGroup;
	private Button loginButton;
	private Button logoutButton;
	private EditableGroup editableGroup;
	private GridLayout layout = new GridLayout(4, 1);
	private FunctionalGroup newMappingConfigurationFunctionalGroup;
	private FunctionalGroup existingMappingConfigurationFunctionalGroup;
	private Button collapseButton;
	private Button expandButton;
	private VerticalLayout imagePanelLayout;
	private Label loggedInUserLabel;
	private HashMap<String, IkasanUINavigator> views;
	private String currentView;
	private MenuBar actionMenu = new MenuBar();
	private MenuBar utilityMenu = new MenuBar();
	private List<RefreshGroup> refreshGroups;

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
			List<RefreshGroup> refreshGroups)
	{
		this.authenticationService = authenticationService;
		this.visibilityGroup = visibilityGroup;
		this.editableGroup = editableGroup;
		this.newMappingConfigurationFunctionalGroup = newMappingConfigurationFunctionalGroup;
		this.existingMappingConfigurationFunctionalGroup = existingMappingConfigurationFunctionalGroup;
		this.imagePanelLayout = imagePanelLayout;
		this.views = views;
		this.refreshGroups = refreshGroups;
		init();
	}

	/**
	 * Helper method to initialise the object.
	 */
	protected void init()
	{
		logger.info("Initialising navigation panel.");

		this.setWidth(100, Unit.PERCENTAGE);
		this.setHeight(30, Unit.PIXELS);
		this.setStyleName("navigation");
		this.layout.setColumnExpandRatio(0, 45f);
		this.layout.setColumnExpandRatio(1, 50f);
		this.layout.setColumnExpandRatio(2, 2.5f);
		this.layout.setColumnExpandRatio(3, 2.5f);
		
		this.actionMenu.setStyleName("ikasan");
		this.utilityMenu.setStyleName("ikasan");
		
		this.createActionMenuItems();
		this.createUtilityMenuItems();

		this.layout.addComponent(actionMenu, 0, 0);
		this.layout.setComponentAlignment(actionMenu, Alignment.MIDDLE_LEFT);

		final LoginDialog dialog = new LoginDialog(this.authenticationService, visibilityGroup,
				this);

		this.loginButton = new Button("Login");
		this.loginButton.setStyleName(BaseTheme.BUTTON_LINK);
		this.loginButton.addStyleName("white");
		this.loginButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				UI.getCurrent().addWindow(dialog);
			}
		});
		this.layout.setWidth(97, Unit.PERCENTAGE);
		this.layout.setHeight(100, Unit.PERCENTAGE);
		this.layout.addComponent(this.loginButton, 2, 0);
		this.layout.setComponentAlignment(this.loginButton,
				Alignment.MIDDLE_RIGHT);


		logoutButton = new Button(new ThemeResource("images/user.png"));
		this.logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
		this.logoutButton.addStyleName("white");
		this.logoutButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				manageLogout();
			}
		});

		this.collapseButton = new Button("^");
		this.collapseButton.setStyleName(BaseTheme.BUTTON_LINK);
		this.collapseButton.addStyleName("white");
		this.layout.addComponent(this.collapseButton, 3, 0);
		this.layout.setComponentAlignment(this.collapseButton,
				Alignment.MIDDLE_RIGHT);
		this.collapseButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				imagePanelLayout.setVisible(false);
				layout.removeComponent(collapseButton);
				layout.addComponent(expandButton, 3, 0);
				layout.setComponentAlignment(expandButton,
						Alignment.MIDDLE_RIGHT);
			}
		});

		this.expandButton = new Button("+");
		this.expandButton.setStyleName(BaseTheme.BUTTON_LINK);
		this.expandButton.addStyleName("white");
		this.expandButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				imagePanelLayout.setVisible(true);
				layout.removeComponent(expandButton);
				layout.addComponent(collapseButton, 3, 0);
				layout.setComponentAlignment(collapseButton,
						Alignment.MIDDLE_RIGHT);
			}
		});

		this.setContent(layout);
	}

	/**
	 * Helper method to create the action menu
	 */
	protected void createActionMenuItems()
	{
		this.actionMenu.removeItems();
//		MenuItem dashboards = actionMenu.addItem("Dashboards",
//				new ThemeResource("images/menu-icon.png"), null);
//		dashboards.setStyleName("ikasan");
//
//		MenuBar.Command dashboardCommand = createNavigatorMenuCommand(
//				"dashboard", "dashboardView");
//		dashboards.addItem("Default", dashboardCommand);
//		dashboards.addSeparator();
//		dashboards.addItem("My custom dasboard 1", null, null);
//		dashboards.addItem("My custom dasboard 2", null, null);

		MenuBar.Command mappingCommand = createNavigatorMenuCommand("mapping",
				"mappingView");
//		MenuBar.Command errorCommand = createNavigatorMenuCommand("error",
//				"errorView");
//		MenuBar.Command replayCommand = createNavigatorMenuCommand("replay",
//				"replayView");
//		MenuBar.Command hospitalCommand = createNavigatorMenuCommand(
//				"hospital", "hospitalView");
		MenuBar.Command topologyCommand = createNavigatorMenuCommand(
				"topology", "topologyView");

		// Another top-level item
		
		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(MappingConfigurationUISessionValueConstants.USER);
	    	
    	if(authentication != null)
    	{
			MenuItem service = this.actionMenu.addItem("Services",
					new ThemeResource("images/menu-icon.png"), null);
			service.setStyleName("ikasan");
			service.addItem("Topology", null, topologyCommand);
			service.addSeparator();
			service.addItem("Mapping", null, mappingCommand);
	//		service.addSeparator();
	//		service.addItem("Error", null, errorCommand);
	//		service.addItem("Replay", null, replayCommand);
	//		service.addItem("Hospital", null, hospitalCommand);
    	}
	}

	protected void createUtilityMenuItems()
	{
		utilityMenu.removeItems();
		
		MenuBar.Command helpCommand = new MenuBar.Command()
		{
			public void menuSelected(MenuItem selectedItem)
			{				
				JavaScript.getCurrent().execute
					("window.open('http://google.com', 'Help', 'height=300,width=200,resizable');");

			}
		};

		utilityMenu.addItem("", new ThemeResource(
				"images/help.png"), helpCommand);
		
		MenuBar.Command userCommand = createNavigatorMenuCommand("user",
				"userView");
		MenuBar.Command authenticationMethodCommand = createNavigatorMenuCommand("topLevel",
				"authenticationMethodView");
		MenuBar.Command principalManagementCommand = createNavigatorMenuCommand("principalManagement",
				"principalManagementView");
		MenuBar.Command roleManagementCommand = createNavigatorMenuCommand("roleManagement",
				"roleManagementView");
		MenuBar.Command policyManagementCommand = createNavigatorMenuCommand("policyManagement",
				"policyManagementView");
		
		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(MappingConfigurationUISessionValueConstants.USER);
	    	
    	if(authentication != null 
    			&& authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
    	{
    		MenuItem admin = utilityMenu.addItem("", new ThemeResource(
    				"images/gear.png"), null);
    		admin.setStyleName("ikasan");
    		admin.addItem("Manage Users", null, userCommand);
    		admin.addItem("Manage Principals", null, principalManagementCommand);
    		admin.addItem("Manage Roles", null, roleManagementCommand);
    		admin.addItem("Manage Policies", null, policyManagementCommand);
    		admin.addItem("Security Administration", null, authenticationMethodCommand);
    	}		

		MenuBar.Command profileCommand = createNavigatorMenuCommand("profile",
				"profileView");
		MenuBar.Command logOutCommand = new MenuBar.Command()
		{
			public void menuSelected(MenuItem selectedItem)
			{
				manageLogout();
			}
		};

		MenuItem userItem = this.utilityMenu.addItem("", new ThemeResource(
				"images/user.png"), null);
		userItem.setStyleName("ikasan");
		userItem.addItem("Profile", profileCommand);
		userItem.addSeparator();
		userItem.addItem("Log Out", logOutCommand);
	}

	/**
	 * Method to manage a logout
	 */
	protected void manageLogout()
	{
		LogoutAction action = new LogoutAction(this.visibilityGroup,
				this.editableGroup, this.layout,
				this.loginButton, this.utilityMenu, this.loggedInUserLabel, this);

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
		loggedInUserLabel = new Label("");
		loggedInUserLabel.setStyleName("ikasan-white");
		loggedInUserLabel.setVisible(false);
		this.layout.addComponent(loggedInUserLabel, 1, 0);
		this.layout.setComponentAlignment(loggedInUserLabel,
				Alignment.MIDDLE_RIGHT);
		this.layout.addComponent(this.utilityMenu, 2, 0);
		this.layout
				.setComponentAlignment(this.utilityMenu, Alignment.MIDDLE_RIGHT);

		this.newMappingConfigurationFunctionalGroup.initialiseButtonState();
		this.existingMappingConfigurationFunctionalGroup
				.initialiseButtonState();
		
		this.createUtilityMenuItems();
		this.createActionMenuItems();
	}

	public void loadTopLevelNavigator()
	{
		Navigator navigator = new Navigator(UI.getCurrent(), views.get(
				"topLevel").getContainer());

		for (IkasanUIView view : this.views.get("topLevel").getIkasanViews())
		{
			logger.info("Adding view:" + view.getPath());
			navigator.addView(view.getPath(), view.getView());
		}
	}

	protected MenuBar.Command createNavigatorMenuCommand(
			final String navigatorName, final String viewName)
	{
		return new MenuBar.Command()
		{
			public void menuSelected(MenuItem selectedItem)
			{
				if (currentView == null
						|| !currentView.equals(views.get(navigatorName)
								.getName()))
				{
					refresh();
					
					loadTopLevelNavigator();

					UI.getCurrent().getNavigator().navigateTo(viewName);

					currentView = views.get(navigatorName).getName();

					List<IkasanUIView> mappingViews = views.get(navigatorName)
							.getIkasanViews();

					Navigator navigator = new Navigator(UI.getCurrent(), views
							.get(navigatorName).getContainer());

					for (IkasanUIView view : mappingViews)
					{
						navigator.addView(view.getPath(), view.getView());
					}
				}
			}
		};
	}
	
	protected void refresh()
	{
		for(RefreshGroup refreshGroup: this.refreshGroups)
		{
			refreshGroup.refresh();
		}
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.layout.setVisible(visible);
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
		this.createActionMenuItems();
	}

}
