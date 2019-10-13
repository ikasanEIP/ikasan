package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.cache.CacheStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeListener;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusPanel extends HorizontalLayout implements GraphViewChangeListener
{
    private Logger logger = LoggerFactory.getLogger(StatusPanel.class);

    private Button runningButton;
    private Button stoppedButton;
    private Button stoppedInErrorButton;
    private Button recoveringButton;
    private Button pauseButton;

    private Module currentModule;
    private Flow currentFlow;

    private Registration broadcasterRegistration;

    public StatusPanel()
    {
        init();
    }

    protected void init()
    {
        Label runningLabel = new Label(getTranslation("status-label.running", UI.getCurrent().getLocale()));
        runningLabel.getStyle().set("font-size", "8pt");
        Label stoppedLabel = new Label(getTranslation("status-label.stopped", UI.getCurrent().getLocale()));
        stoppedLabel.getStyle().set("font-size", "8pt");
        Label stoppedInErrorLabel = new Label(getTranslation("status-label.stopped-in-error", UI.getCurrent().getLocale()));
        stoppedInErrorLabel.getStyle().set("font-size", "8pt");
        Label recoveringLabel = new Label(getTranslation("status-label.recovering", UI.getCurrent().getLocale()));
        recoveringLabel.getStyle().set("font-size", "8pt");
        Label pausedLabel = new Label(getTranslation("status-label.paused", UI.getCurrent().getLocale()));
        pausedLabel.getStyle().set("font-size", "8pt");

        runningButton = this.createStatusButton();
        runningButton.setText("0");

        VerticalLayout runningButtonLayout = this.createStatusButtonLayout(runningButton, runningLabel);

        stoppedButton = this.createStatusButton();
        stoppedButton.setText("0");

        VerticalLayout stoppedButtonLayout = this.createStatusButtonLayout(stoppedButton, stoppedLabel);

        stoppedInErrorButton = this.createStatusButton();
        stoppedInErrorButton.setText("0");

        VerticalLayout stoppedInErrorButtonLayout = this.createStatusButtonLayout(stoppedInErrorButton, stoppedInErrorLabel);

        recoveringButton = this.createStatusButton();
        recoveringButton.setText("0");

        VerticalLayout recoveringButtonLayout = this.createStatusButtonLayout(recoveringButton, recoveringLabel);

        pauseButton = this.createStatusButton();
        pauseButton.setText("0");

        VerticalLayout pauseButtonLayout = this.createStatusButtonLayout(pauseButton, pausedLabel);

        this.setSpacing(false);
        this.setMargin(false);
        this.expand(runningButtonLayout, stoppedButtonLayout, stoppedInErrorButtonLayout, recoveringButtonLayout, pauseButtonLayout);
        this.add(runningButtonLayout, stoppedButtonLayout, stoppedInErrorButtonLayout, recoveringButtonLayout, pauseButtonLayout);
        this.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, runningButtonLayout, stoppedButtonLayout, stoppedInErrorButtonLayout, recoveringButtonLayout, pauseButtonLayout);
    }

    private Button createStatusButton()
    {
        Button statusButton = new Button();
        statusButton.getStyle().set("color", "rgb(0,0,0)");
        statusButton.getStyle().set("font-weight", "bold");
        statusButton.getStyle().set("font-size", "14pt");
        statusButton.getStyle().set("border", "solid 2px");
        statusButton.getStyle().set("border-color", "rgb(241,90,35)");
        statusButton.setHeight("35px");
        statusButton.setWidth("35px");
        statusButton.setEnabled(false);

        return statusButton;
    }

    /**
     * Create the button layout
     *
     * @param button
     * @param label
     * @return
     */
    private VerticalLayout createStatusButtonLayout(Button button, Label label)
    {
        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setMargin(false);
        buttonLayout.setSpacing(false);

        label.setHeight("10px");
        buttonLayout.add(button, label);
        buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, button);
        buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, label);

        buttonLayout.setFlexGrow(4.0, button);
        buttonLayout.setFlexGrow(1.0, label);

        return buttonLayout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = CacheStateBroadcaster.register(flowState ->
        {
            ui.access(() ->
            {
                // do something interesting here.
                logger.info("Received flow state: " + flowState);

                calculateStatus();
            });
        });
    }

    protected void calculateStatus()
    {
        int running = 0;
        int stopped = 0;
        int inError = 0;
        int recovering = 0;
        int paused = 0;

        for(Flow flow: currentModule.getFlows())
        {
            FlowState flowState = FlowStateCache.instance().get(currentModule, flow);

            if(flowState == null)
            {
                continue;
            }
            else if(flowState.getState().equals(State.RUNNING_STATE))
            {
                running++;
            }
            else if(flowState.getState().equals(State.STOPPED_STATE))
            {
                stopped++;
            }
            else if(flowState.getState().equals(State.RECOVERING_STATE))
            {
                recovering++;
            }
            else if(flowState.getState().equals(State.PAUSED_STATE))
            {
                paused++;
            }
            else if(flowState.getState().equals(State.STOPPED_IN_ERROR_STATE))
            {
                inError++;
            }
        }

        this.recoveringButton.setText(Integer.toString(recovering));
        this.stoppedButton.setText(Integer.toString(stopped));
        this.runningButton.setText(Integer.toString(running));
        this.pauseButton.setText(Integer.toString(paused));
        this.stoppedInErrorButton.setText(Integer.toString(inError));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    @Override
    public void onChange(GraphViewChangeEvent event)
    {
        this.currentModule = event.getModule();
        this.currentFlow = event.getFlow();

        calculateStatus();
    }
}
