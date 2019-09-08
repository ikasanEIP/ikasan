package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ControlPanel extends VerticalLayout
{
    Button startButton = new Button(VaadinIcon.PLAY.create());
    Button stopButton = new Button(VaadinIcon.STOP.create());
    Button pauseButton = new Button(VaadinIcon.PAUSE.create());

    public ControlPanel()
    {
        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.add(startButton, stopButton, pauseButton);
        H5 label = new H5("Control Panel");

        super.add(label, controlPanel);
        super.setHorizontalComponentAlignment(Alignment.CENTER, label);
        super.setHorizontalComponentAlignment(Alignment.CENTER, controlPanel);
        this.getStyle().set("border", "2px solid red");
        this.getStyle().set("border-radius", "25px");
        this.setMargin(true);
        this.setSpacing(true);
        this.setWidth("200px");
    }
}
