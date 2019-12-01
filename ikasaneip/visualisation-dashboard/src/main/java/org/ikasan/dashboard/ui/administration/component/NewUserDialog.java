package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;

public class NewUserDialog extends Dialog
{
    private UserService userService;
    private SystemEventLogger systemEventLogger;

    public NewUserDialog(UserService userService, SystemEventLogger systemEventLogger)
    {
        this.userService = userService;
        if(this.userService == null)
        {
            throw new IllegalArgumentException("userService cannot be null!");
        }
        this.systemEventLogger = systemEventLogger;
        if(this.systemEventLogger == null)
        {
            throw new IllegalArgumentException("systemEventLogger cannot be null!");
        }
        init();
    }

    private void init()
    {
        H3 newRoleLabel = new H3(getTranslation("label.new-user", UI.getCurrent().getLocale(), null));

        FormLayout formLayout = new FormLayout();

        Binder<User> binder = new Binder<>(User.class);

        TextField nameTf = new TextField(getTranslation("text-field.username", UI.getCurrent().getLocale(), null));
        nameTf.setWidth("250px");
        binder.forField(nameTf)
            .withValidator(name -> name != null && name.length() > 0, getTranslation("error.username-missing", UI.getCurrent().getLocale(), null))
            .bind(User::getUsername, User::setUsername);
        formLayout.add(nameTf);
        formLayout.setColspan(nameTf, 2);

        TextField firstNameTf = new TextField(getTranslation("text-field.firstname", UI.getCurrent().getLocale(), null));
        binder.forField(firstNameTf)
            .withValidator(name -> name != null && name.length() > 0, getTranslation("error.firstname-missing", UI.getCurrent().getLocale(), null))
            .bind(User::getFirstName, User::setFirstName);
        formLayout.add(firstNameTf);
        formLayout.setColspan(firstNameTf, 2);

        TextField surnameTf = new TextField(getTranslation("text-field.surname", UI.getCurrent().getLocale(), null));
        binder.forField(surnameTf)
            .withValidator(name -> name != null && name.length() > 0, getTranslation("error.surname-missing", UI.getCurrent().getLocale(), null))
            .bind(User::getSurname, User::setSurname);
        formLayout.add(surnameTf);
        formLayout.setColspan(surnameTf, 2);

        TextField emailTf = new TextField(getTranslation("text-field.email", UI.getCurrent().getLocale(), null));
        binder.forField(emailTf)
            .withValidator(name -> name != null && name.length() > 0, getTranslation("error.email-missing", UI.getCurrent().getLocale(), null))
            .bind(User::getEmail, User::setEmail);
        formLayout.add(emailTf);
        formLayout.setColspan(emailTf, 2);

        PasswordField passwordTf = new PasswordField(getTranslation("text-field.password", UI.getCurrent().getLocale(), null));
        binder.forField(passwordTf)
            .withValidator(name -> name != null && name.length() > 0, getTranslation("error.password-missing", UI.getCurrent().getLocale(), null))
            .bind(User::getPassword, User::setPassword);
        formLayout.add(passwordTf);
        formLayout.setColspan(passwordTf, 2);

        User user = new User();
        binder.readBean(user);

        Div result = new Div();
        result.add(formLayout);
        result.setSizeFull();

        formLayout.setSizeFull();

        Button save = new Button(getTranslation("button.save", UI.getCurrent().getLocale(), null));
        save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                binder.writeBean(user);

                user.setRequiresPasswordChange(true);
                this.userService.createUser(user);

                this.systemEventLogger.logEvent(SystemEventConstants.NEW_USER_CREATED
                    , "New user " + user.getUsername() + " added.", null);

                this.close();
            }
            catch (ValidationException e)
            {
                // Ignore as the form will provide feedback to the user via the validation mechanism.
            }
            catch (Exception e)
            {
                e.printStackTrace();
                NotificationHelper.showErrorNotification(String.format(getTranslation("error.falied-to-create-user", UI.getCurrent().getLocale(), null)));
            }
        });

        Button cancel = new Button(getTranslation("button.cancel", UI.getCurrent().getLocale(), null));
        cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            this.close();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(save, cancel);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.add(newRoleLabel, formLayout, buttonLayout);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonLayout);
        this.add(layout);
        this.setWidth("400px");
    }
}
