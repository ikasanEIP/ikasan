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

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.action.LogoutAction;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.framework.window.LoginDialog;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
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
    private GridLayout layout = new GridLayout(3, 1);
    private FunctionalGroup newMappingConfigurationFunctionalGroup;
    private FunctionalGroup existingMappingConfigurationFunctionalGroup;
    private Button collapseButton;
    private Button expandButton;
    private VerticalLayout imagePanelLayout;
    private Label loggedInUserLabel;

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
            VerticalLayout imagePanelLayout)
    {
        this.userService = userService;
        this.securityService = securityService;
        this.visibilityGroup = visibilityGroup;
        this.userDetailsHelper = userDetailsHelper;
        this.editableGroup = editableGroup;
        this.newMappingConfigurationFunctionalGroup = newMappingConfigurationFunctionalGroup;
        this.existingMappingConfigurationFunctionalGroup = existingMappingConfigurationFunctionalGroup;
        this.imagePanelLayout = imagePanelLayout;
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
        this.layout.setColumnExpandRatio(0, 95f);
        this.layout.setColumnExpandRatio(1, 2.5f);
        this.layout.setColumnExpandRatio(2, 2.5f);

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
        this.layout.addComponent(this.loginButton, 1, 0);
        this.layout.setComponentAlignment(this.loginButton, Alignment.MIDDLE_RIGHT);

        logoutButton = new Button("Logout");
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
        this.layout.addComponent(this.collapseButton, 2, 0);
        this.layout.setComponentAlignment(this.collapseButton, Alignment.MIDDLE_RIGHT);
        this.collapseButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                imagePanelLayout.setVisible(false);
                layout.removeComponent(collapseButton);
                layout.addComponent(expandButton, 2, 0);
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
                layout.addComponent(collapseButton, 2, 0);
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
            this.editableGroup, this.layout, this.loginButton, this.logoutButton, this.loggedInUserLabel);

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
        this.layout.addComponent(loggedInUserLabel, 0, 0);
        this.layout.setComponentAlignment(loggedInUserLabel, Alignment.MIDDLE_RIGHT);
        this.layout.addComponent(logoutButton, 1, 0);
        this.layout.setComponentAlignment(this.logoutButton, Alignment.MIDDLE_RIGHT);

        this.newMappingConfigurationFunctionalGroup.initialiseButtonState();
        this.existingMappingConfigurationFunctionalGroup.initialiseButtonState();
    }
}
