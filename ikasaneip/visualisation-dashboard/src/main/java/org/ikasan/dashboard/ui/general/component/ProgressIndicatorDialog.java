package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.progressbar.ProgressBar;

public class ProgressIndicatorDialog extends Dialog
{
    public void open(String label)
    {
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);

        H2 h2 = new H2(label);

        VerticalLayout layout = new VerticalLayout();
        layout.add(h2, progressBar);

        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, h2);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, progressBar);
        layout.setSizeFull();

        this.setSizeFull();
        this.add(layout);

        this.open();
    }
}
