package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.cache.CacheStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.general.component.ProgressIndicatorDialog;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeListener;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ControlPanel extends HorizontalLayout implements GraphViewChangeListener
{
    private Logger logger = LoggerFactory.getLogger(ControlPanel.class);

    public static final String START = "START";
    public static final String STOP = "STOP";
    public static final String PAUSE = "PAUSE";
    public static final String START_PAUSE = "START-PAUSE";
    public static final String SELECT = "SELECT";

    private HorizontalLayout controlPanelLayout = new HorizontalLayout();
    private ModuleControlRestServiceImpl moduleControlRestService;

    private Button startButton;
    private Button stopButton;
    private Button pauseButton;
    private Button startPauseButton;
    private Button all;

    private Image playImage;
    private Image playImageDisabled;
    private Image stopImage;
    private Image stopImageDisabled;
    private Image pauseImage;
    private Image pauseImageDisabled;
    private Image startPauseImage;
    private Image startPauseImageDisabled;
    private Image selectAllImageOff;

    private Registration flowStateBroadcasterRegistration;
    private Registration cacheStateBroadcasterRegistration;

    private Module module;
    private Flow currentFlow;

    private Tooltip startButtonTooltip;
    private Tooltip stopButtonTooltip;
    private Tooltip pauseButtonTooltip;
    private Tooltip startPauseButtonTooltip;

    public ControlPanel(ModuleControlRestServiceImpl moduleControlRestService)
    {
        this.moduleControlRestService = moduleControlRestService;
        playImage = new Image("/frontend/images/start-control-small.png", "");
        playImage.setHeight("40px");
        playImageDisabled = new Image("/frontend/images/start-grey-control-small.png", "");
        playImageDisabled.setHeight("40px");
        stopImage = new Image("/frontend/images/stop-control-small.png", "");
        stopImage.setHeight("40px");
        stopImageDisabled = new Image("/frontend/images/stop-grey-control-small.png", "");
        stopImageDisabled.setHeight("40px");
        pauseImage = new Image("/frontend/images/pause-control-small.png", "");
        pauseImage.setHeight("40px");
        pauseImageDisabled = new Image("/frontend/images/pause-grey-control-small.png", "");
        pauseImageDisabled.setHeight("40px");
        startPauseImage = new Image("/frontend/images/startpause-control-small.png", "");
        startPauseImage.setHeight("40px");
        startPauseImageDisabled = new Image("/frontend/images/startpause-grey-control-small.png", "");
        startPauseImageDisabled.setHeight("40px");
        selectAllImageOff = new Image("/frontend/images/all-small-off-icon.png", "");
        selectAllImageOff.setHeight("40px");

        startButton = createButton(playImage, START);
        startButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(startButton, getTranslation("tooltip.start-flow", UI.getCurrent().getLocale()));

        stopButton = createButton(stopImage, STOP);
        stopButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(stopButton, getTranslation("tooltip.stop-flow", UI.getCurrent().getLocale()));

        pauseButton = createButton(pauseImage, PAUSE);
        pauseButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(pauseButton, getTranslation("tooltip.pause-flow", UI.getCurrent().getLocale()));

        startPauseButton = createButton(startPauseImage, START_PAUSE);
        startPauseButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(startPauseButton, getTranslation("tooltip.start-pause-flow", UI.getCurrent().getLocale()));


        all = createButton(selectAllImageOff, SELECT);

        controlPanelLayout.add(startButton, startButtonTooltip, stopButton, stopButtonTooltip, pauseButton, pauseButtonTooltip, startPauseButton, startPauseButtonTooltip);
        controlPanelLayout.setVerticalComponentAlignment(Alignment.BASELINE, startButton, stopButton, pauseButton, startPauseButton);
        controlPanelLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        controlPanelLayout.setWidth("250px");
        controlPanelLayout.setMargin(true);

        this.add(controlPanelLayout);
        this.setVerticalComponentAlignment(Alignment.BASELINE, controlPanelLayout);
        this.setJustifyContentMode(JustifyContentMode.END);
    }

    public void setFlowStatus(State state)
    {
        if(state.equals(State.RUNNING_STATE))
        {
            startButton = createButton(playImageDisabled, START);
            stopButton = createButton(stopImage, STOP);
            stopButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(stopButton, getTranslation("tooltip.stop-flow", UI.getCurrent().getLocale()));
            pauseButton = createButton(pauseImage, PAUSE);
            pauseButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(pauseButton, getTranslation("tooltip.pause-flow", UI.getCurrent().getLocale()));
            startPauseButton = createButton(startPauseImageDisabled, START_PAUSE);
        }
        else if(state.equals(State.STOPPED_STATE))
        {
            startButton = createButton(playImage, START);
            startButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(startButton, getTranslation("tooltip.start-flow", UI.getCurrent().getLocale()));
            stopButton = createButton(stopImageDisabled, STOP);
            pauseButton = createButton(pauseImageDisabled, PAUSE);
            startPauseButton = createButton(startPauseImage, START_PAUSE);
            startPauseButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(startPauseButton, getTranslation("tooltip.start-pause-flow", UI.getCurrent().getLocale()));
        }
        else if(state.equals(State.STOPPED_IN_ERROR_STATE))
        {
            startButton = createButton(playImage, START);
            startButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(startButton, getTranslation("tooltip.start-flow", UI.getCurrent().getLocale()));
            stopButton = createButton(stopImageDisabled, STOP);
            stopButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(stopButton, getTranslation("tooltip.stop-flow", UI.getCurrent().getLocale()));
            pauseButton = createButton(pauseImageDisabled, PAUSE);
            startPauseButton = createButton(startPauseImage, START_PAUSE);
            startPauseButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(startPauseButton, getTranslation("tooltip.start-pause-flow", UI.getCurrent().getLocale()));
        }
        else if(state.equals(State.PAUSED_STATE))
        {
            startButton = createButton(playImage, START);
            startButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(startButton, getTranslation("tooltip.start-flow", UI.getCurrent().getLocale()));

            stopButton = createButton(stopImage, STOP);
            stopButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(stopButton, getTranslation("tooltip.stop-flow", UI.getCurrent().getLocale()));
            pauseButton = createButton(pauseImageDisabled, PAUSE);
            startPauseButton = createButton(startPauseImageDisabled, START_PAUSE);
        }
        else if(state.equals(State.START_PAUSE_STATE))
        {
            startButton = createButton(playImage, START);
            startButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(startButton, getTranslation("tooltip.start-flow", UI.getCurrent().getLocale()));
            stopButton = createButton(stopImage, STOP);
            stopButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(stopButton, getTranslation("tooltip.stop-flow", UI.getCurrent().getLocale()));
            pauseButton = createButton(pauseImageDisabled, PAUSE);
            startPauseButton = createButton(startPauseImageDisabled, START_PAUSE);
        }
        else if(state.equals(State.RECOVERING_STATE))
        {
            startButton = createButton(playImageDisabled, START);
            stopButton = createButton(stopImage, STOP);
            stopButtonTooltip = TooltipHelper.getTooltipForComponentTopRight(stopButton, getTranslation("tooltip.stop-flow", UI.getCurrent().getLocale()));
            pauseButton = createButton(pauseImageDisabled, PAUSE);
            startPauseButton = createButton(startPauseImageDisabled, START_PAUSE);
        }

        controlPanelLayout.removeAll();
        controlPanelLayout.add(startButton, stopButton, pauseButton, startPauseButton);
        controlPanelLayout.setVerticalComponentAlignment(Alignment.BASELINE, startButton, stopButton, pauseButton, startPauseButton);
    }

    private Button createButton(Image image, String id)
    {
        Button button = new Button(image);
        button.setHeight("40px");
        button.setWidth("40px");

        button.setId(id);

        button.addClickListener(this.asButtonClickedListener());
        return button;
    }

    public ComponentEventListener<ClickEvent<Button>> asButtonClickedListener()
    {
        return (ComponentEventListener<ClickEvent<Button>>) selectedChangeEvent ->
        {
            // the id associated with the action is an action
            performFlowControlAction(selectedChangeEvent.getSource().getElement().getAttribute("id"));
        };
    }

    protected void performFlowControlAction(String action)
    {
        ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(false);

        if(action.equals(START))
        {
            progressIndicatorDialog.open(String.format(getTranslation("progress-indicator.starting-flow", UI.getCurrent().getLocale()), currentFlow.getName()));
            performAction(progressIndicatorDialog, action);
        }
        else if(action.equals(STOP))
        {
            progressIndicatorDialog.open(String.format(getTranslation("progress-indicator.stopping-flow", UI.getCurrent().getLocale()), currentFlow.getName()));
            performAction(progressIndicatorDialog, action);
        }
        else if(action.equals(PAUSE))
        {
            progressIndicatorDialog.open(String.format(getTranslation("progress-indicator.pausing-flow", UI.getCurrent().getLocale()), currentFlow.getName()));
            performAction(progressIndicatorDialog, action);
        }
        else if(action.equals(START_PAUSE))
        {
            progressIndicatorDialog.open(String.format(getTranslation("progress-indicator.start-pause-flow", UI.getCurrent().getLocale()), currentFlow.getName()));
            performAction(progressIndicatorDialog, action);
        }
    }

    protected void performAction(ProgressIndicatorDialog progressIndicatorDialog, String action)
    {
        final UI current = UI.getCurrent();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try
            {
                Thread.sleep(2000);
                State state = null;

                if(action.equals(ControlPanel.START))
                {
                    if(this.moduleControlRestService.changeFlowState(module.getUrl(),
                        module.getName(), currentFlow.getName(), "start"))
                    {
                        state = State.RUNNING_STATE;
                        FlowStateBroadcaster.broadcast(new FlowState(this.module.getName(), this.currentFlow.getName(), state));
                    }
                    else
                    {
                        NotificationHelper.showErrorNotification(String.format(getTranslation("message.error-starting-flow", UI.getCurrent().getLocale(), this.currentFlow.getName())));
                    }
                }
                else if(action.equals(ControlPanel.STOP))
                {
                    if(this.moduleControlRestService.changeFlowState(module.getUrl(),
                        module.getName(), currentFlow.getName(), "stop"))
                    {
                        state = State.STOPPED_STATE;
                        FlowStateBroadcaster.broadcast(new FlowState(this.module.getName(), this.currentFlow.getName(), state));
                    }
                    else
                    {
                        NotificationHelper.showErrorNotification(String.format(getTranslation("message.error-stopping-flow", UI.getCurrent().getLocale(), this.currentFlow.getName())));
                    }
                }
                else if(action.equals(ControlPanel.PAUSE))
                {
                    if(this.moduleControlRestService.changeFlowState(module.getUrl(),
                        module.getName(), currentFlow.getName(), "pause"))
                    {
                        state = State.PAUSED_STATE;
                        FlowStateBroadcaster.broadcast(new FlowState(this.module.getName(), this.currentFlow.getName(), state));
                    }
                    else
                    {
                        NotificationHelper.showErrorNotification(String.format(getTranslation("message.error-pausing-flow", UI.getCurrent().getLocale(), this.currentFlow.getName())));
                    }
                }
                else if(action.equals(ControlPanel.START_PAUSE))
                {
                    if(this.moduleControlRestService.changeFlowState(module.getUrl(),
                        module.getName(), currentFlow.getName(), "startPause"))
                    {
                        state = State.START_PAUSE_STATE;
                        FlowStateBroadcaster.broadcast(new FlowState(this.module.getName(), this.currentFlow.getName(), state));
                    }
                    else
                    {
                        NotificationHelper.showErrorNotification(String.format(getTranslation("message.error-start-pause-flow", UI.getCurrent().getLocale(), this.currentFlow.getName())));
                    }
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

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        UI ui = attachEvent.getUI();
        flowStateBroadcasterRegistration = FlowStateBroadcaster.register(flowState ->
        {
            logger.info("Received flow state: " + flowState);
            setFlowState(ui, flowState);
        });

        cacheStateBroadcasterRegistration = CacheStateBroadcaster.register(flowState ->
        {
            logger.info("Received flow state: " + flowState);
            setFlowState(ui, flowState);
        });

        this.startButtonTooltip.attachToComponent(startButton);
        this.stopButtonTooltip.attachToComponent(stopButton);
        this.pauseButtonTooltip.attachToComponent(pauseButton);
        this.startPauseButtonTooltip.attachToComponent(startPauseButton);
    }

    protected void setFlowState(UI ui, FlowState flowState)
    {
        ui.access(() ->
        {
            if(currentFlow != null && flowState.getFlowName().equals(currentFlow.getName())
                && module != null && flowState.getModuleName().equals(module.getName()))
            {
                this.setFlowStatus(flowState.getState());
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
        this.flowStateBroadcasterRegistration.remove();
        this.flowStateBroadcasterRegistration = null;
        this.cacheStateBroadcasterRegistration.remove();
        this.cacheStateBroadcasterRegistration = null;
    }

    @Override
    public void onChange(GraphViewChangeEvent event)
    {
        this.module = event.getModule();
        this.currentFlow = event.getFlow();

        FlowState flowState = FlowStateCache.instance().get(module, currentFlow);

        if(flowState != null)
        {
            this.setFlowStatus(flowState.getState());
        }
    }
}
