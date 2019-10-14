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

public class HospitalCommentsDialog extends Dialog
{
    private ExclusionEventAction exclusionEventAction;
    private boolean isSaved;
    private String action;

    public HospitalCommentsDialog(ExclusionEventAction exclusionEventAction, String action)
    {
        this.exclusionEventAction = exclusionEventAction;
        if(this.exclusionEventAction == null)
        {
            throw new IllegalArgumentException("exclusionEventAction cannot be null!");
        }
        this.action = action;
        if(this.action == null)
        {
            throw new IllegalArgumentException("action cannot be null!");
        }
        if(!this.action.equalsIgnoreCase(ExclusionEventAction.RESUBMIT) && !this.action.equalsIgnoreCase(ExclusionEventAction.IGNORED))
        {
            throw new IllegalArgumentException(String.format("action must equal [%s] or [%s]!", ExclusionEventAction.RESUBMIT, ExclusionEventAction.IGNORED));
        }

        init();
    }

    private void init()
    {
        H3 hospitalLabel;
        Image hospitalImage;
        if(action.equalsIgnoreCase(ExclusionEventAction.RESUBMIT))
        {
            hospitalImage = new Image("/frontend/images/resubmit-icon.png", "");
            hospitalLabel = new H3(getTranslation("label.resubmit-hospital-events", UI.getCurrent().getLocale()));
        }
        else
        {
            hospitalImage = new Image("/frontend/images/ignore-icon.png", "");
            hospitalLabel = new H3(getTranslation("label.ignore-hospital-events", UI.getCurrent().getLocale()));
        }

        hospitalImage.setHeight("50px");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.add(hospitalImage, hospitalLabel);

        FormLayout formLayout = new FormLayout();

        Binder<ExclusionEventAction> binder = new Binder<>(ExclusionEventAction.class);

        TextArea commentTf = new TextArea(getTranslation("text-field.comment", UI.getCurrent().getLocale(), null));
        binder.forField(commentTf)
            .withValidator(description -> description != null && description.length() > 0, getTranslation("message.comment-missing", UI.getCurrent().getLocale(), null))
            .bind(ExclusionEventAction::getComment, ExclusionEventAction::setComment);
        commentTf.setHeight("200px");

        binder.readBean(exclusionEventAction);

        formLayout.add(commentTf);
        formLayout.setColspan(commentTf, 2);

        Div result = new Div();
        result.add(formLayout);
        result.setSizeFull();

        formLayout.setSizeFull();

        HorizontalLayout buttonLayout = new HorizontalLayout();

        if(action.equalsIgnoreCase(ExclusionEventAction.RESUBMIT))
        {
            Button resubmit = new Button(getTranslation("button.resubmit", UI.getCurrent().getLocale(), null));
            resubmit.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                try
                {
                    binder.writeBean(exclusionEventAction);
                    this.isSaved = true;
                    this.close();
                } catch (ValidationException e)
                {
                    // Ignore as the form will provide feedback to the user via the validation mechanism.
                }
            });

            buttonLayout.add(resubmit);
        }
        else
        {
            Button ignore = new Button(getTranslation("button.ignore", UI.getCurrent().getLocale(), null));
            ignore.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                try
                {
                    binder.writeBean(exclusionEventAction);
                    this.isSaved = true;
                    this.close();
                } catch (ValidationException e)
                {
                    // Ignore as the form will provide feedback to the user via the validation mechanism.
                }
            });

            buttonLayout.add(ignore);
        }

        Button cancel = new Button(getTranslation("button.cancel", UI.getCurrent().getLocale(), null));
        cancel.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            this.close();
        });

        buttonLayout.add(cancel);

        this.setWidth("600px");
        this.setHeight("100%");

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, formLayout, buttonLayout);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonLayout);
        this.add(layout);
    }

    public boolean isActioned()
    {
        return isSaved;
    }
}
