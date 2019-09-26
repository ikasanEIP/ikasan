package org.ikasan.dashboard.ui.search.component;


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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.search.model.replay.ReplayAuditImpl;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.ikasan.spec.replay.ReplayAudit;

public class ReplayCommentsDialog extends Dialog
{
    private ReplayAudit replayAudit;
    private boolean isSaved;

    public ReplayCommentsDialog(ReplayAudit replayAudit)
    {
        this.replayAudit = replayAudit;
        if(this.replayAudit == null)
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

        Binder<ReplayAudit> binder = new Binder<>(ReplayAudit.class);

        TextField targetUrlTf = new TextField(getTranslation("text-field.target-module-url", UI.getCurrent().getLocale(), null));
        binder.forField(targetUrlTf)
            .withValidator(description -> description != null && description.length() > 0, getTranslation("message.comment-target-module-url", UI.getCurrent().getLocale(), null))
            .bind(ReplayAudit::getTargetServer, ReplayAudit::setTargetServer);

        TextArea commentTf = new TextArea(getTranslation("text-field.comment", UI.getCurrent().getLocale(), null));
        binder.forField(commentTf)
            .withValidator(description -> description != null && description.length() > 0
                , getTranslation("message.comment-missing", UI.getCurrent().getLocale(), null))
            .bind(ReplayAudit::getReplayReason, ReplayAudit::setReplayReason);
        commentTf.setHeight("200px");

        ReplayAudit replayAudit = new ReplayAuditImpl();
        binder.readBean(replayAudit);

        formLayout.add(targetUrlTf);
        formLayout.setColspan(targetUrlTf, 2);

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
                binder.writeBean(replayAudit);
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
