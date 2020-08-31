package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.dashboard.ui.general.component.ProgressIndicatorDialog;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.spec.module.client.ModuleControlService;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


public class MultiFlowControlPanel extends ControlPanel {

    public MultiFlowControlPanel(ModuleControlService moduleControlRestService) {
        super(moduleControlRestService);
        super.asActionListener = false;
    }

    public void addStartButtonClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.startButton.addClickListener(listener);
    }

    public void addStopButtonClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.stopButton.addClickListener(listener);
    }

    public void addPauseButtonClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.pauseButton.addClickListener(listener);
    }

    public void addStartPauseButtonClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.startPauseButton.addClickListener(listener);
    }

    protected void performFlowControlAction(String action, Collection<Flow> flows)
    {
        ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(true);

        if(action.equals(START))
        {
            progressIndicatorDialog.open(String.format(getTranslation("progress-indicator.starting-flow", UI.getCurrent().getLocale()), flows.size() == 1 ? flows.stream().findFirst().get().getName() : "All Selected Flows"));
            performAction(progressIndicatorDialog, action, flows);
        }
        else if(action.equals(STOP))
        {
            progressIndicatorDialog.open(String.format(getTranslation("progress-indicator.stopping-flow", UI.getCurrent().getLocale()), flows.size() == 1 ? flows.stream().findFirst().get().getName() : "All Selected Flows"));
            performAction(progressIndicatorDialog, action, flows);
        }
        else if(action.equals(PAUSE))
        {
            progressIndicatorDialog.open(String.format(getTranslation("progress-indicator.pausing-flow", UI.getCurrent().getLocale()), flows.size() == 1 ? flows.stream().findFirst().get().getName() : "All Selected Flows"));
            performAction(progressIndicatorDialog, action, flows);
        }
        else if(action.equals(START_PAUSE))
        {
            progressIndicatorDialog.open(String.format(getTranslation("progress-indicator.start-pause-flow", UI.getCurrent().getLocale()), flows.size() == 1 ? flows.stream().findFirst().get().getName() : "All Selected Flows"));
            performAction(progressIndicatorDialog, action, flows);
        }
    }

    protected void performAction(ProgressIndicatorDialog progressIndicatorDialog, String action, Collection<Flow> flows)
    {
        final UI current = UI.getCurrent();
        final I18NProvider i18NProvider = VaadinService.getCurrent().getInstantiator().getI18NProvider();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try
            {
                AtomicReference<State> state = new AtomicReference<>();

                if(action.equals(ControlPanel.START))
                {
                    flows.forEach(flow -> {
                        if(this.moduleControlRestService.changeFlowState(module.getUrl(),
                            module.getName(), flow.getName(), "start"))
                        {
                            state.set(State.RUNNING_STATE);
                            FlowStateBroadcaster.broadcast(new FlowState(this.module.getName(), flow.getName(), state.get()));
                        }
                        else
                        {
                            current.access(() ->
                            {
                                NotificationHelper.showErrorNotification(String.format(i18NProvider.getTranslation("message.error-starting-flow", current.getLocale()), this.currentFlow.getName()));
                            });
                        }
                    });
                }
                else if(action.equals(ControlPanel.STOP))
                {
                    flows.forEach(flow -> {
                    if(this.moduleControlRestService.changeFlowState(module.getUrl(),
                        module.getName(), flow.getName(), "stop"))
                    {
                        state.set(State.STOPPED_STATE);
                        FlowStateBroadcaster.broadcast(new FlowState(this.module.getName(), flow.getName(), state.get()));
                    }
                    else
                    {
                        current.access(() ->
                        {
                            NotificationHelper.showErrorNotification(String.format(i18NProvider.getTranslation("message.error-stopping-flow", current.getLocale()), this.currentFlow.getName()));
                        });
                    }});
                }
                else if(action.equals(ControlPanel.PAUSE))
                {
                    flows.forEach(flow -> {
                        if(this.moduleControlRestService.changeFlowState(module.getUrl(),
                            module.getName(), flow.getName(), "pause"))
                        {
                            state.set(State.PAUSED_STATE);
                            FlowStateBroadcaster.broadcast(new FlowState(this.module.getName(), flow.getName(), state.get()));
                        }
                        else
                        {
                            current.access(() ->
                            {
                                NotificationHelper.showErrorNotification(String.format(i18NProvider.getTranslation("message.error-pausing-flow", current.getLocale()), this.currentFlow.getName()));
                            });
                        }
                    });
                }
                else if(action.equals(ControlPanel.START_PAUSE))
                {
                    flows.forEach(flow -> {
                        if(this.moduleControlRestService.changeFlowState(module.getUrl(),
                            module.getName(), flow.getName(), "startPause"))
                        {
                            state.set(State.START_PAUSE_STATE);
                            FlowStateBroadcaster.broadcast(new FlowState(this.module.getName(), flow.getName(), state.get()));
                        }
                        else
                        {
                            current.access(() ->
                            {
                                NotificationHelper.showErrorNotification(String.format(i18NProvider.getTranslation("message.error-start-pause-flow", current.getLocale()), this.currentFlow.getName()));
                            });
                        }
                    });
                }
                else
                {
                    throw new IllegalArgumentException(String.format("Received illegal action [%s] ", action));
                }

                current.access(() ->
                {
                    progressIndicatorDialog.close();
                });
            }
            catch(Exception e)
            {
                e.printStackTrace();
                current.access(() ->
                {
                    progressIndicatorDialog.close();
                });

                return;
            }
        });
    }
}
