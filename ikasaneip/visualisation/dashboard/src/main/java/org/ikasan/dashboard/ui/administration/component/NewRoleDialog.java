package org.ikasan.dashboard.ui.administration.component;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.util.SystemEventConstants;
import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;

public class NewRoleDialog extends Dialog
{
    private SecurityService securityService;
    private SystemEventLogger systemEventLogger;

    public NewRoleDialog(SecurityService securityService, SystemEventLogger systemEventLogger)
    {
        this.securityService = securityService;
        if(this.securityService == null)
        {
            throw new IllegalArgumentException("securityService cannot be null!");
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
        H3 newRoleLabel = new H3(getTranslation("label.new-role", UI.getCurrent().getLocale(), null));

        FormLayout formLayout = new FormLayout();

        Binder<Role> binder = new Binder<>(Role.class);

        TextField nameTf = new TextField(getTranslation("text-field.new-role-name", UI.getCurrent().getLocale(), null));
        binder.forField(nameTf)
            .withValidator(name -> name != null && name.length() > 0, getTranslation("message.new-role-name-missing", UI.getCurrent().getLocale(), null))
            .bind(Role::getName, Role::setName);
        formLayout.add(nameTf);
        formLayout.setColspan(nameTf, 2);

        TextArea descriptionTf = new TextArea(getTranslation("text-field.new-role-description", UI.getCurrent().getLocale(), null));
        binder.forField(descriptionTf)
            .withValidator(description -> description != null && description.length() > 0, getTranslation("message.new-role-description-missing", UI.getCurrent().getLocale(), null))
            .bind(Role::getDescription, Role::setDescription);
        descriptionTf.setHeight("150px");

        Role role = new Role();
        binder.readBean(role);

        formLayout.add(descriptionTf);
        formLayout.setColspan(descriptionTf, 2);

        Div result = new Div();
        result.add(formLayout);
        result.setSizeFull();

        formLayout.setSizeFull();

        Button save = new Button(getTranslation("button.save", UI.getCurrent().getLocale(), null));
        save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                binder.writeBean(role);

                this.securityService.saveRole(role);

                this.systemEventLogger.logEvent(SystemEventConstants.DASHBOARD_ROLE_ADDED
                    , "New role " + role.getName() + " added.", null);

                this.close();
            }
            catch (ValidationException e)
            {
                // Ignore as the form will provide feedback to the user via the validation mechanism.
            }
            catch (Exception e)
            {
                NotificationHelper.showErrorNotification(String.format(getTranslation("message.role-name-must-be-unique", UI.getCurrent().getLocale(), null)));
            }
        });

        Button cancel = new Button(getTranslation("button.cancel", UI.getCurrent().getLocale(), null));
        cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            this.close();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.add(save, cancel);

        VerticalLayout layout = new VerticalLayout();
        layout.add(newRoleLabel, formLayout, buttonLayout);
        this.add(layout);
    }
}
