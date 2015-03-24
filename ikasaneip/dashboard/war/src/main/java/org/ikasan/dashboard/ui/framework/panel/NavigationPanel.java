/*
 * $Id: NavigationPanel.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/NavigationPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.panel;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.action.LogoutAction;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.framework.window.LoginDialog;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author CMI2 Development Team
 *
 */
public class NavigationPanel extends Panel
{
    private static final long serialVersionUID = 5649279357596506519L;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(NavigationPanel.class);

    private UserService userService;
    private SecurityService securityService;
    private VisibilityGroup visibilityGroup;
    private UserDetailsHelper userDetailsHelper;
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
    private MenuBar userMenu = new MenuBar();
    private HashMap<String, IkasanUINavigator> views;
    private String currentView;

    /**
     * Constructor
     * 
     * @param userService
     * @param authProvider
     * @param visibilityGroup
     * @param userDetailsHelper
     * @param editableGroup
     * @param newMappingConfigurationFunctionalGroup
     * @param existingMappingConfigurationFunctionalGroup
     */
    public NavigationPanel(UserService userService, SecurityService securityService
            , VisibilityGroup visibilityGroup, UserDetailsHelper userDetailsHelper, EditableGroup editableGroup,
            FunctionalGroup newMappingConfigurationFunctionalGroup, FunctionalGroup existingMappingConfigurationFunctionalGroup,
            VerticalLayout imagePanelLayout, HashMap<String, IkasanUINavigator> views)
    {
        this.userService = userService;
        this.securityService = securityService;
        this.visibilityGroup = visibilityGroup;
        this.userDetailsHelper = userDetailsHelper;
        this.editableGroup = editableGroup;
        this.newMappingConfigurationFunctionalGroup = newMappingConfigurationFunctionalGroup;
        this.existingMappingConfigurationFunctionalGroup = existingMappingConfigurationFunctionalGroup;
        this.imagePanelLayout = imagePanelLayout;
        this.views = views;
        init();
    }

    /**
     * Helper method to initialise the object.
     */
    protected void init()
    {
        this.setWidth(100, Unit.PERCENTAGE);
        this.setHeight(30, Unit.PIXELS);
        this.setStyleName("navigation");
        this.layout.setColumnExpandRatio(0, 45f);
        this.layout.setColumnExpandRatio(1, 50f);
        this.layout.setColumnExpandRatio(2, 2.5f);
        this.layout.setColumnExpandRatio(3, 2.5f);

        MenuBar.Command mappingCommand = createNavigatorMenuCommand("mapping", "mappingView");
        MenuBar.Command errorCommand = createNavigatorMenuCommand("error", "errorView");
        MenuBar.Command replayCommand = createNavigatorMenuCommand("replay", "replayView");
        MenuBar.Command hospitalCommand = createNavigatorMenuCommand("hospital", "hospitalView");
        MenuBar.Command topologyCommand = createNavigatorMenuCommand("topology", "topologyView");
        
        MenuBar barmenu = new MenuBar();

        this.layout.addComponent(barmenu, 0, 0);
        this.layout.setComponentAlignment(barmenu, Alignment.MIDDLE_LEFT);
        // A top-level menu item that opens a submenu
        MenuItem dashboards = barmenu.addItem("Dashboards", new ThemeResource("images/menu-icon.png"), null);

        MenuBar.Command dashboardCommand = createNavigatorMenuCommand("dashboard", "dashboardView");
        dashboards.addItem("Default", dashboardCommand);
        dashboards.addSeparator();
        dashboards.addItem("My custom dasboard 1", null, null);
        dashboards.addItem("My custom dasboard 2", null, null);
        
        

        // Another top-level item
        MenuItem service = barmenu.addItem("Services", new ThemeResource("images/menu-icon.png"), null);
        service.addItem("Topology", null, topologyCommand);
        service.addSeparator();
        service.addItem("Mapping", null, mappingCommand);
        service.addSeparator();
        service.addItem("Error",  null, errorCommand);
        service.addItem("Replay", null, replayCommand);
        service.addItem("Hospital", null, hospitalCommand);
                
        MenuBar.Command userCommand = createNavigatorMenuCommand("user", "userView");

        // Yet another top-level item
        MenuItem admin = barmenu.addItem("Administration", new ThemeResource("images/menu-icon.png"), null);
        admin.addItem("Users", null, userCommand);

        

        final LoginDialog dialog = new LoginDialog(userService, securityService
            , visibilityGroup, userDetailsHelper, this);

        this.loginButton = new Button("Login");
        this.loginButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.loginButton.addStyleName("white");
        this.loginButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                UI.getCurrent().addWindow(dialog);
            }
        });
        this.layout.setWidth(97, Unit.PERCENTAGE);
        this.layout.setHeight(100, Unit.PERCENTAGE);
        this.layout.addComponent(this.loginButton, 2, 0);
        this.layout.setComponentAlignment(this.loginButton, Alignment.MIDDLE_RIGHT);

        MenuBar.Command logOutCommand = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
            	 manageLogout();
            }  
        };

        MenuBar.Command profileCommand = createNavigatorMenuCommand("profile", "profileView");

        MenuItem userItem = this.userMenu.addItem("", new ThemeResource("images/user.png"), null);
        userItem.addItem("Profile", profileCommand);
        userItem.addSeparator();
        userItem.addItem("Log Out", logOutCommand);

        logoutButton = new Button(new ThemeResource("images/user.png"));
        this.logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.logoutButton.addStyleName("white");
        this.logoutButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                manageLogout();
            }
        });

        this.collapseButton = new Button("^");
        this.collapseButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.collapseButton.addStyleName("white");
        this.layout.addComponent(this.collapseButton, 3, 0);
        this.layout.setComponentAlignment(this.collapseButton, Alignment.MIDDLE_RIGHT);
        this.collapseButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                imagePanelLayout.setVisible(false);
                layout.removeComponent(collapseButton);
                layout.addComponent(expandButton, 3, 0);
                layout.setComponentAlignment(expandButton, Alignment.MIDDLE_RIGHT);
            }
        });

        this.expandButton = new Button("+");
        this.expandButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.expandButton.addStyleName("white");
        this.expandButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                imagePanelLayout.setVisible(true);
                layout.removeComponent(expandButton);
                layout.addComponent(collapseButton, 3, 0);
                layout.setComponentAlignment(collapseButton, Alignment.MIDDLE_RIGHT);
            }
        });

        this.setContent(layout);
    }

    /**
     * Method to manage a logout
     */
    protected void manageLogout()
    {
        LogoutAction action = new LogoutAction(this.visibilityGroup, this.userDetailsHelper,
            this.editableGroup, this.layout, this.loginButton, this.userMenu, this.loggedInUserLabel);

        IkasanMessageDialog dialog = new IkasanMessageDialog("Logout", 
            "You are about to log out. Any unsaved data will be lost. " +
            "Are you sure you wish to proceed?.", action);
        
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
        loggedInUserLabel = new Label("Logged in as " 
                + this.userDetailsHelper.getUserDetails().getUsername());
        loggedInUserLabel.setStyleName("ikasan-white");
        this.layout.addComponent(loggedInUserLabel, 1, 0);
        this.layout.setComponentAlignment(loggedInUserLabel, Alignment.MIDDLE_RIGHT);
        this.layout.addComponent(this.userMenu, 2, 0);
        this.layout.setComponentAlignment(this.userMenu, Alignment.MIDDLE_RIGHT);

        this.newMappingConfigurationFunctionalGroup.initialiseButtonState();
        this.existingMappingConfigurationFunctionalGroup.initialiseButtonState();
    }

    private void loadTopLevelNavigator()
    {
    	Navigator navigator = new Navigator(UI.getCurrent(), views.get("topLevel").getContainer());
        
        for(IkasanUIView view: this.views.get("topLevel").getIkasanViews())
        {
            navigator.addView(view.getPath(), view.getView());
        }
    }

    private MenuBar.Command createNavigatorMenuCommand(final String navigatorName, final String viewName)
    {
    	return new MenuBar.Command() 
        {
            public void menuSelected(MenuItem selectedItem) 
            {
            	if(currentView == null || !currentView.equals(views.get(navigatorName).getName()))
            	{
            		loadTopLevelNavigator();
            		
            		UI.getCurrent().getNavigator().navigateTo(viewName);
            		
            		currentView = views.get(navigatorName).getName();
            		
            		List<IkasanUIView> mappingViews = views.get(navigatorName).getIkasanViews();
                	
                	Navigator navigator = new Navigator(UI.getCurrent(), views.get(navigatorName).getContainer());
                    
                    for(IkasanUIView view: mappingViews)
                    {
                        navigator.addView(view.getPath(), view.getView());
                    }
            	}
            }  
        };
    }
}
