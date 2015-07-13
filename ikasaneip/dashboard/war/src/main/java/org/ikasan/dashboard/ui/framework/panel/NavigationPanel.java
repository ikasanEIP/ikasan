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
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.framework.window.LoginDialog;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.vaadin.teemu.VaadinIcons;

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
public class NavigationPanel extends Panel implements ViewContext
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
		this.layout.setColumnExpandRatio(1, 47.5f);
		this.layout.setColumnExpandRatio(2, 2.5f);
		this.layout.setColumnExpandRatio(3, 2.5f);
		this.layout.setColumnExpandRatio(4, 2.5f);
		this.layout.addStyleName("valo-menuitems");
		
		this.actionMenu.setStyleName("ikasan");
		
		this.createActionMenuItems();
		this.createUtilityMenuItems();

//		this.layout.addComponent(actionMenu, 0, 0);
//		this.layout.setComponentAlignment(actionMenu, Alignment.MIDDLE_LEFT);

		final LoginDialog dialog = new LoginDialog(this.authenticationService, visibilityGroup,
				this);

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
		this.layout.setWidth(97, Unit.PERCENTAGE);
		this.layout.setHeight(100, Unit.PERCENTAGE);
		this.layout.addComponent(this.loginButton, 2, 0);
		this.layout.setComponentAlignment(this.loginButton,
				Alignment.MIDDLE_RIGHT);


		logoutButton = new Button(new ThemeResource("images/user.png"));
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
		this.layout.addComponent(this.collapseButton, 4, 0);
		this.layout.setComponentAlignment(this.collapseButton,
				Alignment.MIDDLE_RIGHT);
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

//		MenuBar.Command mappingCommand = createNavigatorMenuCommand("mapping",
//				"mappingView");
//		MenuBar.Command errorCommand = createNavigatorMenuCommand("error",
//				"errorView");
//		MenuBar.Command replayCommand = createNavigatorMenuCommand("replay",
//				"replayView");
//		MenuBar.Command hospitalCommand = createNavigatorMenuCommand(
//				"hospital", "hospitalView");
//		MenuBar.Command topologyCommand = createNavigatorMenuCommand(
//				"topology", "topologyView");

		// Another top-level item
		
//		IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
//	        	.getAttribute(DashboardSessionValueConstants.USER);
//	    	
//    	if(authentication != null)
//    	{
//			MenuItem service = this.actionMenu.addItem("Services",
//					new ThemeResource("images/menu-icon.png"), null);
//			service.setStyleName("ikasan");
//			service.addItem("Topology", null, topologyCommand);
//			service.addSeparator();
//			service.addItem("Mapping", null, mappingCommand);
	//		service.addSeparator();
	//		service.addItem("Error", null, errorCommand);
	//		service.addItem("Replay", null, replayCommand);
	//		service.addItem("Hospital", null, hospitalCommand);
//    	}
	}

	protected void createUtilityMenuItems()
	{
		utilityMenu.removeItems();
		utilityMenu.addStyleName("user-menu");
		MenuBar.Command logOutCommand = new MenuBar.Command()
		{
			public void menuSelected(MenuItem selectedItem)
			{
				manageLogout();
			}
		};

		MenuItem userItem = this.utilityMenu.addItem("", VaadinIcons.USER, null);
		userItem.addItem("Log Out", logOutCommand);
	}

	/**
	 * Method to manage a logout
	 */
	protected void manageLogout()
	{
		LogoutAction action = new LogoutAction(this.visibilityGroup,
				this.editableGroup, this.layout, this.loginButton, 
				this.setupButton, this.utilityMenu, this.loggedInUserLabel, this);

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

		UI.getCurrent().getNavigator().navigateTo("landingView");
	}

	protected MenuBar.Command createNavigatorMenuCommand(
			final String navigatorName, final String viewName)
	{
		return new MenuBar.Command()
		{
			public void menuSelected(MenuItem selectedItem)
			{
//				if (currentView == null
//						|| !currentView.equals(views.get(navigatorName)
//								.getName()))
//				{
//					refresh();
//					
//					loadTopLevelNavigator();
//
//					UI.getCurrent().getNavigator().navigateTo(viewName);
//
//					currentView = views.get(navigatorName).getName();
//
//					List<IkasanUIView> mappingViews = views.get(navigatorName)
//							.getIkasanViews();
//
//					Navigator navigator = new Navigator(UI.getCurrent(), views
//							.get(navigatorName).getContainer());
//
//					for (IkasanUIView view : mappingViews)
//					{
//						navigator.addView(view.getPath(), view.getView());
//					}
//				}
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
