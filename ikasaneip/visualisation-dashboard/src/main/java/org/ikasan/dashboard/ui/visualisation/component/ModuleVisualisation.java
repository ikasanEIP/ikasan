package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.cache.CacheStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.general.component.ConfigurationDialog;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanFlowLayoutManager;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanModuleLayoutManager;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.dto.FlowDto;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.listener.DoubleClickListener;
import org.ikasan.vaadin.visjs.network.options.Interaction;
import org.ikasan.vaadin.visjs.network.options.Options;
import org.ikasan.vaadin.visjs.network.options.edges.ArrowHead;
import org.ikasan.vaadin.visjs.network.options.edges.Arrows;
import org.ikasan.vaadin.visjs.network.options.edges.EdgeColor;
import org.ikasan.vaadin.visjs.network.options.edges.Edges;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.options.physics.Physics;
import org.ikasan.vaadin.visjs.network.util.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModuleVisualisation extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(ModuleVisualisation.class);
    private Map<String, Flow> flowMap;
    private NetworkDiagram networkDiagram;
    private Flow currentFlow;
    private Module module;
    private boolean moduleView = false;

    private Registration flowStateBroadcasterRegistration;
    private Registration cacheStateBroadcasterRegistration;

    private ModuleControlRestServiceImpl moduleControlRestService;
    private ConfigurationRestServiceImpl configurationRestService;

    public ModuleVisualisation(ModuleControlRestServiceImpl moduleControlRestService
        , ConfigurationRestServiceImpl configurationRestService)
    {
        this.moduleControlRestService = moduleControlRestService;
        this.configurationRestService = configurationRestService;
        this.setSizeFull();
        this.flowMap = new HashMap<>();
    }

    public void addModule(Module module)
    {
        for(Flow flow: module.getFlows())
        {
            add(flow);
        }

        this.module = module;
    }

    protected void add(Flow flow)
    {
        logger.info("Adding flow [{}] to visualisation.", flow.getName());
        this.flowMap.put(flow.getName(), flow);
        logger.info("Finished adding flow [{}] to visualisation.", flow.getName());
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param flow to render.
     */
    protected NetworkDiagram createNetworkDiagram(Flow flow)
    {
        logger.info("Creating network diagram for flow [{}] to visualisation.", flow.getName());

        NetworkDiagram networkDiagram = this.initialiseNetworkDiagram();

        IkasanFlowLayoutManager layoutManager = new IkasanFlowLayoutManager(flow, networkDiagram, null);
        layoutManager.layout();

        logger.info("Finished creating network diagram for flow [{}] to visualisation.", flow.getName());
        return networkDiagram;
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param module to render.
     */
    protected NetworkDiagram createNetworkDiagram(Module module)
    {
        logger.info("Creating network diagram for module [{}] to visualisation.", module.getName());
        NetworkDiagram networkDiagram = this.initialiseNetworkDiagram();

        IkasanModuleLayoutManager layoutManager = new IkasanModuleLayoutManager(module, networkDiagram, null);
        layoutManager.layout();

        return networkDiagram;
    }

    protected NetworkDiagram initialiseNetworkDiagram()
    {
        logger.info("Creating network diagram for module [{}] to visualisation.", module.getName());
        Physics physics = new Physics();
        physics.setEnabled(false);

        NetworkDiagram networkDiagram = new NetworkDiagram
            (Options.builder()
                .withAutoResize(true)
                .withPhysics(physics)
                .withInteraction(Interaction.builder().withDragNodes(false) .build())
                .withEdges(
                    Edges.builder()
                        .withArrows(new Arrows(new ArrowHead()))
                        .withColor(EdgeColor.builder()
                            .withColor("#000000")
                            .build())
                        .withDashes(false)
                        .withFont(Font.builder().withSize(9).build())
                        .build())
                .withNodes(Nodes.builder().withFont(Font.builder().withSize(11).build()).build())
                .build());

        networkDiagram.setSizeFull();

        networkDiagram.addDoubleClickListener((DoubleClickListener) doubleClickEvent ->
        {
            logger.info(doubleClickEvent.getParams().toString());
            String node = doubleClickEvent.getParams().getArray("nodes").get(0).asString();

            logger.info("Node: " + node);

            ConfigurationDialog configurationDialog = new ConfigurationDialog(this.module,
                this.currentFlow.getName(), this.module.getComponentMap().get(node).getComponentName()
                , this.configurationRestService);
            configurationDialog.open();
        });

//        networkDiagram.addClickListener((ClickListener) clickEvent -> {
//            logger.info(clickEvent.getParams().toString());
//        });
//
//        networkDiagram.addOnContextListener((OnContextListener) onContextEvent -> {
//            logger.info(onContextEvent.getParams().toString());
//        });

        logger.info("Finished creating network diagram for module [{}] to visualisation.", module.getName());

        return networkDiagram;
    }

    public ComponentEventListener<ClickEvent<Button>> asButtonClickedListener()
    {
        return (ComponentEventListener<ClickEvent<Button>>) selectedChangeEvent ->
        {
            if(selectedChangeEvent.getSource().getElement().getAttribute("id").equals(ControlPanel.START))
            {
                this.currentFlow.setStatus(State.RUNNING_STATE);
                this.drawFlowStatus(State.RUNNING_STATE);
            }
            else if(selectedChangeEvent.getSource().getElement().getAttribute("id").equals(ControlPanel.STOP))
            {
                this.currentFlow.setStatus(State.STOPPED_STATE);
                this.drawFlowStatus(State.STOPPED_STATE);
            }
            else if(selectedChangeEvent.getSource().getElement().getAttribute("id").equals(ControlPanel.PAUSE))
            {
                this.currentFlow.setStatus(State.PAUSED_STATE);
                this.drawFlowStatus(State.PAUSED_STATE);
            }
            else if(selectedChangeEvent.getSource().getElement().getAttribute("id").equals(ControlPanel.START_PAUSE))
            {
                this.currentFlow.setStatus(State.START_PAUSE_STATE);
                this.drawFlowStatus(State.START_PAUSE_STATE);
            }
        };
    }

    private void drawFlowStatus(State state)
    {
        this.networkDiagram.drawStatusBorder(this.currentFlow.getX() -20, this.currentFlow.getY() -20, this.currentFlow.getW() + 40
            , this.currentFlow.getH() + 40, state.getStateColour());
        this.networkDiagram.diagamRedraw();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.redraw();
    }

    public void redraw()
    {
        if (!this.moduleView && this.currentFlow != null)
        {
            this.networkDiagram = this.createNetworkDiagram(this.currentFlow);

            this.networkDiagram.drawFlow(this.currentFlow.getX(), this.currentFlow.getY(), this.currentFlow.getW(), this.currentFlow.getH(), this.currentFlow.getName());

            FlowState flowState = FlowStateCache.instance().get(this.module, this.currentFlow);

            if(flowState != null)
            {
                this.drawFlowStatus(flowState.getState());
            }

            this.removeAll();

            this.add(networkDiagram);
        }
        else if(this.moduleView && this.module != null)
        {
            this.networkDiagram = this.createNetworkDiagram(this.module);

            this.removeAll();

            this.add(networkDiagram);
        }
    }

    public void setCurrentFlow(Flow currentFlow)
    {
        this.currentFlow = currentFlow;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        UI ui = attachEvent.getUI();
        flowStateBroadcasterRegistration = FlowStateBroadcaster.register(flowState ->
        {
            logger.info("Received flow state: " + flowState);
            this.drawFlowStatus(ui, flowState);
        });

        cacheStateBroadcasterRegistration = CacheStateBroadcaster.register(flowState ->
        {
            logger.info("Received flow state: " + flowState);
            this.drawFlowStatus(ui, flowState);
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

    protected void drawFlowStatus(UI ui, FlowState flowState)
    {
        ui.access(() ->
        {
            if(currentFlow != null && flowState.getFlowName().equals(currentFlow.getName())
                && module != null && flowState.getModuleName().equals(module.getName()))
            {
                this.drawFlowStatus(flowState.getState());
            }
        });
    }
}
