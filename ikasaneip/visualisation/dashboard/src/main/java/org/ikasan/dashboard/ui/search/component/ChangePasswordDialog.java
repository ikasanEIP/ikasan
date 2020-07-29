package org.ikasan.dashboard.ui.search.component;

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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;

import java.util.function.IntPredicate;

public class ChangePasswordDialog extends Dialog
{
    private UserService userService;
    private User user;

    public ChangePasswordDialog(User user, UserService userService)
    {
        this.userService = userService;
        if(this.userService == null)
        {
            throw new IllegalArgumentException("userService cannot be null!");
        }
        this.user = user;
        if(this.user == null)
        {
            throw new IllegalArgumentException("user cannot be null!");
        }

        init();
    }

    private void init()
    {
        H3 newRoleLabel = new H3(getTranslation("label.change-password", UI.getCurrent().getLocale()));

        FormLayout formLayout = new FormLayout();

        Binder<User> binder = new Binder<>(User.class);

        PasswordField passwordTf = new PasswordField(getTranslation("text-field.password", UI.getCurrent().getLocale()));
        binder.forField(passwordTf)
            .withValidator(name -> name != null && name.length() > 0, getTranslation("error.password-missing", UI.getCurrent().getLocale()))
            .bind(User::getPassword, User::setPassword);
        formLayout.add(passwordTf);
        formLayout.setColspan(passwordTf, 2);

        PasswordField confirmPasswordTf = new PasswordField(getTranslation("text-field.confirm-password", UI.getCurrent().getLocale(), null));
        binder.forField(confirmPasswordTf)
            .withValidator(name -> name != null && name.length() > 0, getTranslation("error.password-missing", UI.getCurrent().getLocale()))
            .bind(User::getPassword, User::setPassword);
        formLayout.add(confirmPasswordTf);
        formLayout.setColspan(confirmPasswordTf, 2);

        User userPasswordChange = new User();
        binder.readBean(userPasswordChange);

        Div result = new Div();
        result.add(formLayout);
        result.setSizeFull();

        formLayout.setSizeFull();

        Button save = new Button(getTranslation("button.save", UI.getCurrent().getLocale(), null));
        save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                binder.writeBean(userPasswordChange);

                if(!passwordTf.getValue().equals(confirmPasswordTf.getValue()))
                {
                    NotificationHelper.showErrorNotification(getTranslation("error.passwords-differ", UI.getCurrent().getLocale()));
                    return;
                }

                if(passwordTf.getValue().length() < 8 || !containsNumber(passwordTf.getValue())
                    || !containsLowerCase(passwordTf.getValue()) || !containsUpperCase(passwordTf.getValue()))
                {
                    NotificationHelper.showErrorNotification(getTranslation("error.passwords-not-valid", UI.getCurrent().getLocale()));
                    return;
                }

                user.setRequiresPasswordChange(false);
                this.userService.updateUser(user);

                this.userService.changeUsersPassword(user.getUsername(), userPasswordChange.getPassword(), userPasswordChange.getPassword());



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

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(save);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.add(newRoleLabel, formLayout, buttonLayout);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonLayout);
        this.add(layout);
        this.setWidth("400px");
    }

    private boolean containsLowerCase(String value) {
        return contains(value, i -> Character.isLetter(i) && Character.isLowerCase(i));
    }

    private boolean containsUpperCase(String value) {
        return contains(value, i -> Character.isLetter(i) && Character.isUpperCase(i));
    }

    private boolean containsNumber(String value) {
        return contains(value, Character::isDigit);
    }

    private boolean contains(String value, IntPredicate predicate) {
        return value.chars().anyMatch(predicate);
    }
}
