package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ControlPanel extends VerticalLayout
{
    public static final String START = "START";
    public static final String STOP = "STOP";
    public static final String PAUSE = "PAUSE";

    Button startButton = new Button(VaadinIcon.PLAY.create());
    Button stopButton = new Button(VaadinIcon.STOP.create());
    Button pauseButton = new Button(VaadinIcon.PAUSE.create());

    public ControlPanel()
    {
        startButton.setId(START);
        stopButton.setId(STOP);
        pauseButton.setId(PAUSE);

        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.add(startButton, stopButton, pauseButton);
        controlPanel.setVerticalComponentAlignment(Alignment.CENTER, startButton, stopButton, pauseButton);

        this.add(controlPanel);
        this.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, controlPanel);
        this.setWidth("200px");
    }

    public void setFlowStatus(String status)
    {
        if(status.equals("running"))
        {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            pauseButton.setEnabled(true);
        }
        else if(status.equals("stopped"))
        {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }
        else if(status.equals("stoppedInError"))
        {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }
        else if(status.equals("paused"))
        {
            startButton.setEnabled(true);
            stopButton.setEnabled(true);
            pauseButton.setEnabled(false);
        }
        else if(status.equals("recovering"))
        {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            pauseButton.setEnabled(true);
        }
    }

    public void registerListener(ComponentEventListener<ClickEvent<Button>> eventListener)
    {
        startButton.addClickListener(eventListener);
        stopButton.addClickListener(eventListener);
        pauseButton.addClickListener(eventListener);
    }
}
