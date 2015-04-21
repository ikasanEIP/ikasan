/*
 * $Id: LogoutAction.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/action/LogoutAction.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.action;

import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * @author CMI2 Development Team
 *
 */
public class LogoutAction implements Action
{
    private VisibilityGroup visibilityGroup;
    private EditableGroup editableGroup;
    private GridLayout layout;
    private Button loginButton;
    private Label userLabel;
    private Component logOutButton;

    /**
     * Constructor
     * 
     * @param visibilityGroup
     * @param editableGroup
     * @param layout
     * @param loginButton
     */
    public LogoutAction(VisibilityGroup visibilityGroup,
            EditableGroup editableGroup, GridLayout layout, Button loginButton, Component logOutButton,
            Label userLabel)
    {
        super();
        this.visibilityGroup = visibilityGroup;
        this.editableGroup = editableGroup;
        this.layout = layout;
        this.loginButton = loginButton;
        this.logOutButton = logOutButton;
        this.userLabel = userLabel;
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#exectuteAction()
     */
    @Override
    public void exectuteAction()
    {
    	VaadinService.getCurrentRequest().getWrappedSession()
        	.setAttribute(MappingConfigurationUISessionValueConstants.USER, null);
        this.visibilityGroup.setVisible(false);
        this.editableGroup.setEditable(false);

        layout.removeComponent(this.logOutButton);
        layout.addComponent(this.loginButton, 2, 0);
        layout.setComponentAlignment(this.loginButton, Alignment.MIDDLE_RIGHT);
        this.layout.removeComponent(userLabel);
        UI.getCurrent().getNavigator().navigateTo("emptyPanel");
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#ignoreAction()
     */
    @Override
    public void ignoreAction()
    {
        // Nothing to do here.
    }
}
