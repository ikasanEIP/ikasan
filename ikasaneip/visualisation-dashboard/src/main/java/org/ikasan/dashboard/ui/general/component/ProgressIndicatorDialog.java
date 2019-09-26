package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.progressbar.ProgressBar;


public class ProgressIndicatorDialog extends Dialog
{
    private boolean showCancelButton;
    private boolean isCancelled = false;

    public ProgressIndicatorDialog(boolean showCancelButton)
    {
        this.showCancelButton = showCancelButton;
    }

    public void open(String label)
    {
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);

        H2 h2 = new H2(label);

        Button cancelButton = new Button(getTranslation("button.cancel", UI.getCurrent().getLocale()));
        cancelButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            this.isCancelled = true;
            this.close();
        });

        VerticalLayout layout = new VerticalLayout();
        layout.add(h2, progressBar, cancelButton);

        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, cancelButton);

        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, h2);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, progressBar);
        layout.setSizeFull();

        this.setSizeFull();
        this.add(layout);


        cancelButton.setVisible(this.showCancelButton);

        this.open();
    }

    public boolean isCancelled()
    {
        return isCancelled;
    }
}
