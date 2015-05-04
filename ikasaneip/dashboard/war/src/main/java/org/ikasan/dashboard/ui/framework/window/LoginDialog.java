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
package org.ikasan.dashboard.ui.framework.window;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.data.LoginFieldGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.security.service.AuthenticationService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class LoginDialog extends Window
{
    private static final long serialVersionUID = 2394313614920487219L;
    
    /** Logger instance */
    private static Logger logger = Logger.getLogger(LoginDialog.class);

    /**
     * Constructor
     * 
     * @param userService
     * @param authProvider
     * @param visibilityGroup
     * @param commitHandler
     */
    public LoginDialog(AuthenticationService authenticationService,
            VisibilityGroup visibilityGroup,
            NavigationPanel commitHandler)
    {
        super("Login");
        init(authenticationService, visibilityGroup, commitHandler);
    }

    /**
     * Helper method to initialise this object.
     * 
     * @param userService
     * @param authProvider
     * @param visibilityGroup
     * @param userDetailsHelper
     * @param commitHandler
     */
    protected void init(AuthenticationService authenticationService,
            VisibilityGroup visibilityGroup, final NavigationPanel commitHandler)
    {
        super.setModal(true);
        super.setResizable(false);
        super.center();
        super.setStyleName("ikasan");

        PropertysetItem item = new PropertysetItem();
        item.addItemProperty(LoginFieldGroup.USERNAME, new ObjectProperty<String>(""));
        item.addItemProperty(LoginFieldGroup.PASSWORD, new ObjectProperty<String>(""));
        
        FormLayout form = new FormLayout();
        form.setWidth("280px");
        form.setHeight("140px");
        form.setMargin(true);
        
        final TextField userNameField = new TextField("Username");
        userNameField.addValidator(new StringLengthValidator(
            "The username must not be empty",
            1, null, true));
        userNameField.setValidationVisible(false);
        userNameField.setStyleName("ikasan");
        form.addComponent(userNameField);

        final PasswordField passwordField = new PasswordField("Password");
        passwordField.setStyleName("ikasan");
        passwordField.addValidator(new StringLengthValidator(
            "The password must not be empty",
            1, null, true));
        passwordField.setValidationVisible(false);
        form.addComponent(passwordField);

        final LoginFieldGroup binder = new LoginFieldGroup(item, visibilityGroup
            , authenticationService);
        binder.bind(userNameField, LoginFieldGroup.USERNAME);
        binder.bind(passwordField, LoginFieldGroup.PASSWORD);

        HorizontalLayout buttons = new HorizontalLayout();
        
        Button loginButton = new Button("Login");
        loginButton.setStyleName(Reindeer.BUTTON_SMALL);
        loginButton.addClickListener(new ClickListener() 
        {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                try 
                {
                    userNameField.validate();
                    passwordField.validate();
                } catch (InvalidValueException e) {
                    userNameField.setValidationVisible(true);
                    passwordField.setValidationVisible(true);
                    return;
                }

                try 
                {
                    binder.commit();
                    userNameField.setValue("");
                    passwordField.setValue("");
                    close();
                    commitHandler.postCommit();
                } catch (CommitException e) 
                {
                    Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        buttons.addComponent(loginButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName(Reindeer.BUTTON_SMALL);
        cancelButton.addClickListener(new ClickListener() 
        {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                close();
                binder.discard();
            }
        });
        buttons.addComponent(cancelButton);

        form.addComponent(buttons);
        this.setContent(form);
    }
}
