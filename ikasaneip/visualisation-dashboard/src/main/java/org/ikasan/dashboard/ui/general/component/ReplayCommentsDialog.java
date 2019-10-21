package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.ikasan.dashboard.ui.search.model.replay.ReplayDialogDto;

public class ReplayCommentsDialog extends Dialog
{
    private ReplayDialogDto replayDialogDto;
    private boolean isSaved;

    public ReplayCommentsDialog(ReplayDialogDto replayAudit)
    {
        this.replayDialogDto = replayAudit;
        if(this.replayDialogDto == null)
        {
            throw new IllegalArgumentException("ReplayAudit cannot be null!");
        }

        init();
    }

    private void init()
    {
        H3 replayLabel = new H3(getTranslation("label.replay", UI.getCurrent().getLocale()));
        Image replayImage = new Image("/frontend/images/replay-service.png", "");

        replayImage.setHeight("50px");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.add(replayImage, replayLabel);

        FormLayout formLayout = new FormLayout();

        Binder<ReplayDialogDto> binder = new Binder<>(ReplayDialogDto.class);

        TextField targetUrlTf = new TextField(getTranslation("text-field.target-module-url", UI.getCurrent().getLocale(), null));
        binder.forField(targetUrlTf)
            .withValidator(description -> description != null && description.length() > 0, getTranslation("message.comment-target-module-url", UI.getCurrent().getLocale(), null))
            .bind(ReplayDialogDto::getTargetServer, ReplayDialogDto::setTargetServer);

        TextField targetUserTf = new TextField(getTranslation("text-field.target-module-username", UI.getCurrent().getLocale(), null));
        binder.forField(targetUserTf)
            .withValidator(description -> description != null && description.length() > 0, getTranslation("message.missing-target-username", UI.getCurrent().getLocale(), null))
            .bind(ReplayDialogDto::getAuthenticationUser, ReplayDialogDto::setAuthenticationUser);

        PasswordField targetPasswordTf = new PasswordField(getTranslation("text-field.target-module-password", UI.getCurrent().getLocale(), null));
        binder.forField(targetPasswordTf)
            .withValidator(description -> description != null && description.length() > 0, getTranslation("message.missing-target-password", UI.getCurrent().getLocale(), null))
            .bind(ReplayDialogDto::getPassword, ReplayDialogDto::setPassword);

        TextArea commentTf = new TextArea(getTranslation("text-field.comment", UI.getCurrent().getLocale(), null));
        binder.forField(commentTf)
            .withValidator(description -> description != null && description.length() > 0
                , getTranslation("message.comment-missing", UI.getCurrent().getLocale(), null))
            .bind(ReplayDialogDto::getReplayReason, ReplayDialogDto::setReplayReason);
        commentTf.setHeight("200px");

        binder.readBean(this.replayDialogDto);

        formLayout.add(targetUrlTf);
        formLayout.setColspan(targetUrlTf, 2);

        formLayout.add(targetUserTf);
        formLayout.add(targetPasswordTf);

        formLayout.add(commentTf);
        formLayout.setColspan(commentTf, 2);

        Div result = new Div();
        result.add(formLayout);
        result.setSizeFull();

        formLayout.setSizeFull();

        Button save = new Button(getTranslation("button.replay", UI.getCurrent().getLocale(), null));
        save.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                binder.writeBean(this.replayDialogDto);
                this.isSaved = true;
                this.close();
            }
            catch (ValidationException e)
            {
                // Ignore as the form will provide feedback to the user via the validation mechanism.
            }
        });

        Button cancel = new Button(getTranslation("button.cancel", UI.getCurrent().getLocale(), null));
        cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            this.close();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(save, cancel);

        this.setWidth("600px");
        this.setHeight("100%");

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, formLayout, buttonLayout);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonLayout);
        this.add(layout);
    }

    public boolean isSaved()
    {
        return isSaved;
    }
}
