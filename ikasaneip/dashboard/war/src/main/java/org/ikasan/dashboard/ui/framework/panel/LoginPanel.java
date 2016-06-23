package org.ikasan.dashboard.ui.framework.panel;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.data.LoginFieldGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.util.CommitHandler;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class LoginPanel extends Panel implements View
{
	/** Logger instance */
    private static Logger logger = Logger.getLogger(LoginPanel.class);

    /**
     * Constructor
     * 
     * @param userService
     * @param authProvider
     * @param visibilityGroup
     * @param commitHandler
     */
    public LoginPanel(AuthenticationService authenticationService,
            VisibilityGroup visibilityGroup, CommitHandler commitHandler,
            UserService userService, Image bannerImage, Label bannerLabel)
    {
        super();
        init(authenticationService, visibilityGroup, commitHandler, userService, bannerImage, bannerLabel);
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
            VisibilityGroup visibilityGroup, final CommitHandler commitHandler, UserService userService,
            Image bannerImage, Label bannerLabel)
    {
    	this.setWidth(100, Unit.PERCENTAGE);
    	this.setHeight(100, Unit.PERCENTAGE);
    	
    	VerticalLayout layout = new VerticalLayout();
    	layout.setSizeFull();
    	layout.setSpacing(true);
    	this.setContent(layout);
    	
    	Panel panel = new Panel();
    	panel.setWidth(400, Unit.PIXELS);
    	panel.setHeight(210, Unit.PIXELS);
    	
    	bannerLabel.setStyleName("ikasan-maroon");
        bannerLabel.setHeight("100%");
    	
    	layout.addComponent(panel);
    	layout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

        PropertysetItem item = new PropertysetItem();
        item.addItemProperty(LoginFieldGroup.USERNAME, new ObjectProperty<String>(""));
        item.addItemProperty(LoginFieldGroup.PASSWORD, new ObjectProperty<String>(""));
        
       
        GridLayout form = new GridLayout(3, 5);
        form.setColumnExpandRatio(0, .50f);
        form.setColumnExpandRatio(1, .15f);
        form.setColumnExpandRatio(2, .35f);
        form.setWidth(100, Unit.PERCENTAGE);
        form.setMargin(true);
        form.setSpacing(true);
        
        form.addComponent(bannerLabel, 0, 0, 2, 0);
        bannerLabel.setSizeUndefined();
        form.setComponentAlignment(bannerLabel, Alignment.TOP_CENTER);
        
        bannerImage.setHeight("50%");
        bannerImage.setWidth("100%");
        form.addComponent(bannerImage, 0, 1, 0, 4);
        form.setComponentAlignment(bannerImage, Alignment.TOP_CENTER);

		Label usernameLabel = new Label("Username:");
		usernameLabel.setSizeUndefined();
		form.addComponent(usernameLabel, 1, 2);
		form.setComponentAlignment(usernameLabel, Alignment.MIDDLE_RIGHT);
		
        final TextField userNameField = new TextField();
        userNameField.addValidator(new StringLengthValidator(
            "The username must not be empty",
            1, null, true));
        userNameField.setValidationVisible(false);
        userNameField.setStyleName("ikasan");
        form.addComponent(userNameField, 2, 2);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setSizeUndefined();
		form.addComponent(passwordLabel, 1, 3);
		form.setComponentAlignment(passwordLabel, Alignment.MIDDLE_RIGHT);
		
        final PasswordField passwordField = new PasswordField();
        passwordField.setStyleName("ikasan");
        passwordField.addValidator(new StringLengthValidator(
            "The password must not be empty",
            1, null, true));
        passwordField.setValidationVisible(false);
        form.addComponent(passwordField, 2, 3);

        final LoginFieldGroup binder = new LoginFieldGroup(item, visibilityGroup
            , authenticationService, userService);
        binder.bind(userNameField, LoginFieldGroup.USERNAME);
        binder.bind(passwordField, LoginFieldGroup.PASSWORD);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        
        Button loginButton = new Button("Login");
        loginButton.addStyleName(ValoTheme.BUTTON_SMALL);
        loginButton.setClickShortcut(KeyCode.ENTER);

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
                    commitHandler.postCommit();
                } catch (CommitException e) 
                {
                	passwordField.setValue("");
                    Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        buttons.addComponent(loginButton);

        form.addComponent(buttons, 1, 4, 2, 4);
        form.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
        
        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setSizeFull();
        wrapper.addComponent(form);
        wrapper.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        
        panel.setContent(wrapper);
    }
    
    @Override
	public void enter(ViewChangeEvent event)
	{
		// TODO Auto-generated method stub
		
	}
}
