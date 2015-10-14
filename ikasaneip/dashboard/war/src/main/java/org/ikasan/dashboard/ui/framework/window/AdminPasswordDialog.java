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
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.security.service.AuthenticationService;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class AdminPasswordDialog extends Window
{
    private static final long serialVersionUID = 2394313614920487219L;
    
    /** Logger instance */
    private static Logger logger = Logger.getLogger(AdminPasswordDialog.class);
    
    private String password;

    /**
     * Constructor
     * 
     * @param userService
     * @param authProvider
     * @param visibilityGroup
     * @param commitHandler
     */
    public AdminPasswordDialog()
    {
        super();
        init();
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
    protected void init()
    {
        super.setModal(true);
        super.setResizable(false);
        super.center();
        this.setWidth(450, Unit.PIXELS);
    	this.setHeight(220, Unit.PIXELS);
    	
    	super.setClosable(false);
       
        GridLayout form = new GridLayout(2, 5);
        form.setColumnExpandRatio(0, .15f);
        form.setColumnExpandRatio(1, .85f);
        form.setWidth(100, Unit.PERCENTAGE);
        form.setMargin(true);
        form.setSpacing(true);
        
        Label newTypeLabel =new Label("Initial Administration Password");
        newTypeLabel.setStyleName(ValoTheme.LABEL_HUGE);
		form.addComponent(newTypeLabel, 0, 0, 1, 0);
		
		Label adminPasswordHint = new Label();
		adminPasswordHint.setCaptionAsHtml(true);
		adminPasswordHint.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" The initial admin password will be used to log into the application as the 'admin' user.");
		adminPasswordHint.addStyleName(ValoTheme.LABEL_TINY);
		adminPasswordHint.addStyleName(ValoTheme.LABEL_LIGHT);
		
		form.addComponent(adminPasswordHint, 0, 1, 1, 1);
        
		Label passwordLabel = new Label("Password:");
		passwordLabel.setSizeUndefined();
		form.addComponent(passwordLabel, 0, 2);
		form.setComponentAlignment(passwordLabel, Alignment.MIDDLE_RIGHT);
		
        final PasswordField passwordField = new PasswordField();
        passwordField.addValidator(new StringLengthValidator(
            "The username must not be empty",
            1, null, true));
        passwordField.setValidationVisible(false);
        passwordField.setStyleName("ikasan");
        form.addComponent(passwordField, 1, 2);

        Label passwordConfirmLabel = new Label("Confirm Password:");
        passwordConfirmLabel.setSizeUndefined();
		form.addComponent(passwordConfirmLabel, 0, 3);
		form.setComponentAlignment(passwordConfirmLabel, Alignment.MIDDLE_RIGHT);
		
        final PasswordField passwordConfirmField = new PasswordField();
        passwordConfirmField.setStyleName("ikasan");
        passwordConfirmField.addValidator(new StringLengthValidator(
            "The password must not be empty",
            1, null, true));
        passwordConfirmField.setValidationVisible(false);
        form.addComponent(passwordConfirmField, 1, 3);


        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        
        Button saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        saveButton.setClickShortcut(KeyCode.ENTER);

        saveButton.addClickListener(new ClickListener() 
        {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                try 
                {
                    passwordField.validate();
                    passwordConfirmField.validate();
                    
                    if(!passwordField.getValue()
                    		.equals(passwordConfirmField.getValue()))
                    {
                    	Notification.show("Password and confirmation must be the same!", Type.ERROR_MESSAGE);
                    	
                    	return;
                    }
                } 
                catch (InvalidValueException e) 
                {
                    passwordField.setValidationVisible(true);
                    passwordConfirmField.setValidationVisible(true);
                    return;
                }

                password = passwordField.getValue();
                
                passwordField.setValue("");
                passwordConfirmField.setValue("");
                close();

   
            }
        });
        buttons.addComponent(saveButton);
        
        form.addComponent(buttons, 0, 4, 1, 4);
        form.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
        
        this.setContent(form);
    }

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}
}
