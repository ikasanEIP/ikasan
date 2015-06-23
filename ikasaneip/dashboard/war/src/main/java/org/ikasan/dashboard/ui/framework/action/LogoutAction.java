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
package org.ikasan.dashboard.ui.framework.action;

import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
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
    private Button setupButton;
    private Label userLabel;
    private Component logOutButton;
    private NavigationPanel navigationPanel;

    /**
     * Constructor
     * 
     * @param visibilityGroup
     * @param editableGroup
     * @param layout
     * @param loginButton
     */
    public LogoutAction(VisibilityGroup visibilityGroup,
            EditableGroup editableGroup, GridLayout layout, Button loginButton, Button setupButton, Component logOutButton,
            Label userLabel, NavigationPanel navigationPanel)
    {
        super();
        this.visibilityGroup = visibilityGroup;
        this.editableGroup = editableGroup;
        this.layout = layout;
        this.loginButton = loginButton;
        this.setupButton = setupButton;
        this.logOutButton = logOutButton;
        this.userLabel = userLabel;
        this.navigationPanel = navigationPanel;
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#exectuteAction()
     */
    @Override
    public void exectuteAction()
    {
    	VaadinService.getCurrentRequest().getWrappedSession()
        	.setAttribute(DashboardSessionValueConstants.USER, null);
        this.visibilityGroup.setVisible(false);
        this.editableGroup.setEditable(false);

        layout.removeComponent(this.logOutButton);
        layout.addComponent(this.loginButton, 2, 0);
        layout.addComponent(this.setupButton, 3, 0);
        layout.setComponentAlignment(this.setupButton, Alignment.MIDDLE_RIGHT);
        layout.setComponentAlignment(this.loginButton, Alignment.MIDDLE_RIGHT);
        this.layout.removeComponent(userLabel);
        
        VaadinSession vSession = VaadinSession.getCurrent();
        WrappedSession httpSession = vSession.getSession();
        
        this.navigationPanel.loadTopLevelNavigator();
        this.navigationPanel.reset();
        
       //Invalidate HttpSession
        httpSession.invalidate();
        vSession.close();
       //Redirect the user to the login/default Page
        Page.getCurrent().setLocation("/ikasan-dashboard");
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
