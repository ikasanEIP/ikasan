package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.cache.CacheStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.FlowControlManagementDialog;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanFlowLayoutManager;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanModuleLayoutManager;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.dashboard.ui.visualisation.model.flow.*;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.module.client.ConfigurationService;
import org.ikasan.spec.module.client.MetaDataService;
import org.ikasan.spec.module.client.ModuleControlService;
import org.ikasan.spec.module.client.TriggerService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.trigger.TriggerJobType;
import org.ikasan.spec.trigger.TriggerRelationship;
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    private ModuleControlService moduleControlRestService;
    private ConfigurationService configurationRestService;
    private TriggerService triggerRestService;
    private MetaDataService metaDataApplicationRestService;

    private BatchInsert<ModuleMetaData> moduleMetaDataService;

    private UI current;

    public  ModuleVisualisation(ModuleControlService moduleControlRestService
        , ConfigurationService configurationRestService
        , TriggerService triggerRestService, MetaDataService metaDataApplicationRestService
        , BatchInsert<ModuleMetaData> moduleMetaDataService)
    {
        this.moduleControlRestService = moduleControlRestService;
        this.configurationRestService = configurationRestService;
        this.triggerRestService = triggerRestService;
        this.metaDataApplicationRestService = metaDataApplicationRestService;
        this.moduleMetaDataService = moduleMetaDataService;

        this.setSizeFull();
        this.setMargin(false);
        this.setSpacing(false);
        this.flowMap = new HashMap<>();

        current = UI.getCurrent();
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
        logger.debug("Adding flow [{}] to visualisation.", flow.getName());
        this.flowMap.put(flow.getName(), flow);
        logger.debug("Finished adding flow [{}] to visualisation.", flow.getName());
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param flow to render.
     */
    protected NetworkDiagram createNetworkDiagram(Flow flow)
    {
        logger.debug("Creating network diagram for flow [{}] to visualisation.", flow.getName());

        NetworkDiagram networkDiagram = this.initialiseNetworkDiagram();

        IkasanFlowLayoutManager layoutManager = new IkasanFlowLayoutManager(flow, networkDiagram, null);
        layoutManager.layout();

        logger.debug("Finished creating network diagram for flow [{}] to visualisation.", flow.getName());
        return networkDiagram;
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param module to render.
     */
    protected NetworkDiagram createNetworkDiagram(Module module)
    {
        logger.debug("Creating network diagram for module [{}] to visualisation.", module.getName());
        NetworkDiagram networkDiagram = this.initialiseNetworkDiagram();

        IkasanModuleLayoutManager layoutManager = new IkasanModuleLayoutManager(module, networkDiagram, null);
        layoutManager.layout();

        return networkDiagram;
    }

    protected NetworkDiagram initialiseNetworkDiagram()
    {
        logger.debug("Creating network diagram for module [{}] to visualisation.", module.getName());
        Physics physics = new Physics();
        physics.setEnabled(false);

        NetworkDiagram networkDiagram = new NetworkDiagram
            (Options.builder()
                .withAutoResize(false)
                .withPhysics(physics)
                .withInteraction(Interaction.builder().withDragNodes(false).build())
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
            logger.debug(doubleClickEvent.getParams().toString());

            JsonObject coordinates = doubleClickEvent.getParams().getObject("pointer").getObject("canvas");

            Double x = coordinates.getNumber("x");
            Double y = coordinates.getNumber("y");

            JsonArray nodes = doubleClickEvent.getParams().getArray("nodes");

            if(nodes.length() == 0)
            {
                AbstractWiretapNode node = this.wiretapClickedOn(this.currentFlow.getConsumer(), x, y);

                if(node != null && ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.WIRETAP_ADMIN,
                    SecurityConstants.WIRETAP_WRITE, SecurityConstants.ALL_AUTHORITY)) {
                    if(node.wiretapBeforeClickedOn(x, y)) {
                        WiretapManagementDialog wiretapManagementDialog = new WiretapManagementDialog(this.triggerRestService,
                            this.getModule(), this.currentFlow,
                            node.getDecoratorMetaDataList().stream()
                                .filter(decoratorMetaData -> decoratorMetaData.getType().equals(TriggerJobType.WIRETAP.getDescription()) && decoratorMetaData.getName()
                                    .startsWith(TriggerRelationship.BEFORE.getDescription().toUpperCase()))
                                .collect(Collectors.toList()),
                            node, networkDiagram, WiretapManagementDialog.WIRETAP, WiretapManagementDialog.BEFORE);
                        wiretapManagementDialog.open();
                        return;
                    }
                    else if(node.wiretapAfterClickedOn(x, y)) {
                        WiretapManagementDialog wiretapManagementDialog = new WiretapManagementDialog(this.triggerRestService,
                            this.getModule(), this.currentFlow,
                            node.getDecoratorMetaDataList().stream()
                                .filter(decoratorMetaData -> decoratorMetaData.getType().equals(TriggerJobType.WIRETAP.getDescription()) && decoratorMetaData.getName()
                                    .startsWith(TriggerRelationship.AFTER.getDescription().toUpperCase()))
                                .collect(Collectors.toList()),
                            node, networkDiagram, WiretapManagementDialog.WIRETAP, WiretapManagementDialog.AFTER);
                        wiretapManagementDialog.open();
                        return;
                    }
                    else if(node.logWiretapBeforeClickedOn(x, y)) {
                       WiretapManagementDialog wiretapManagementDialog = new WiretapManagementDialog(this.triggerRestService,
                            this.getModule(), this.currentFlow,
                            node.getDecoratorMetaDataList().stream()
                                .filter(decoratorMetaData -> decoratorMetaData.getType().equals(TriggerJobType.LOG_WIRETAP.getDescription()) && decoratorMetaData.getName()
                                    .startsWith(TriggerRelationship.BEFORE.getDescription().toUpperCase()))
                                .collect(Collectors.toList()),
                            node, networkDiagram, WiretapManagementDialog.LOG, WiretapManagementDialog.BEFORE);
                        wiretapManagementDialog.open();
                    }
                    else if(node.logWiretapAfterClickedOn(x, y)) {
                        WiretapManagementDialog wiretapManagementDialog = new WiretapManagementDialog(this.triggerRestService,
                            this.getModule(), this.currentFlow,
                            node.getDecoratorMetaDataList().stream()
                                .filter(decoratorMetaData -> decoratorMetaData.getType().equals(TriggerJobType.LOG_WIRETAP.getDescription()) && decoratorMetaData.getName()
                                    .startsWith(TriggerRelationship.AFTER.getDescription().toUpperCase()))
                                .collect(Collectors.toList()),
                            node, networkDiagram, WiretapManagementDialog.LOG, WiretapManagementDialog.AFTER);
                        wiretapManagementDialog.open();
                    }
                }
                else if(this.currentFlow.controlIconClickedOn(x, y))
                {
                    FlowControlManagementDialog flowControlManagementDialog = new FlowControlManagementDialog(this.module, this.currentFlow,
                        this.moduleControlRestService, this);

                    flowControlManagementDialog.open();
                }
                else if(this.currentFlow.flowClickedOn(x, y))
                {
                    FlowOptionsDialog flowOptionsDialog = new FlowOptionsDialog(module, currentFlow, configurationRestService, this);
                    flowOptionsDialog.open();
                }
            }
            else {
                String node = nodes.get(0).asString();

                if (this.module.getComponentMap().get(node) != null) {
                    AbstractWiretapNode abstractWiretapNode = this.nodeClickedOn(this.currentFlow.getConsumer(), x, y);

                    ComponentOptionsDialog componentNodeActionDialog = new ComponentOptionsDialog(this.module,
                        this.currentFlow.getName(), this.module.getComponentMap().get(node).getComponentName(),
                        this.module.getComponentMap().get(node).isConfigurable(), this.configurationRestService,
                        this.triggerRestService, networkDiagram, abstractWiretapNode, this.metaDataApplicationRestService,
                        this.moduleMetaDataService);

                    componentNodeActionDialog.open();
                }
            }
        });

        logger.debug("Finished creating network diagram for module [{}] to visualisation.", module.getName());


        return networkDiagram;
    }

    protected AbstractWiretapNode wiretapClickedOn(AbstractWiretapNode transition, double x, double y)
    {
        if(transition.wiretapAfterClickedOn(x, y)) {
            return transition;
        }

        if(transition.wiretapBeforeClickedOn(x, y)) {
            return transition;
        }

        if(transition.logWiretapAfterClickedOn(x, y)) {
            return transition;
        }

        if(transition.logWiretapBeforeClickedOn(x, y)) {
            return transition;
        }

        if (transition instanceof SingleTransition && ((SingleTransition) transition).getTransition() != null)
        {
            if(((SingleTransition) transition).getTransition() instanceof AbstractWiretapNode) {
                return wiretapClickedOn((AbstractWiretapNode) ((SingleTransition) transition).getTransition(), x, y);
            }
        }
        else if (transition instanceof MultiTransition)
        {
            for (String key: ((MultiTransition) transition).getTransitions().keySet())
            {
                AbstractWiretapNode node = wiretapClickedOn((AbstractWiretapNode)((MultiTransition) transition).getTransitions().get(key), x, y);

                if(node!=null)return node;
            }
        }

        return null;
    }

    protected AbstractWiretapNode nodeClickedOn(AbstractWiretapNode transition, double x, double y)
    {
        if(transition.clickedOn(x, y)) {
            return transition;
        }

        if (transition instanceof SingleTransition && ((SingleTransition) transition).getTransition() != null)
        {
            if(((SingleTransition) transition).getTransition() instanceof AbstractWiretapNode) {
                return nodeClickedOn((AbstractWiretapNode) ((SingleTransition) transition).getTransition(), x, y);
            }
        }
        else if (transition instanceof MultiTransition)
        {
            for (String key: ((MultiTransition) transition).getTransitions().keySet())
            {
                AbstractWiretapNode node = nodeClickedOn((AbstractWiretapNode)((MultiTransition) transition).getTransitions().get(key), x, y);

                if(node!=null)return node;
            }
        }

        return null;
    }

    private void drawFlowStatus(State state)
    {
        this.networkDiagram.drawStatusBorder(this.currentFlow.getX() -20, this.currentFlow.getY() -20, this.currentFlow.getW() + 40
            , this.currentFlow.getH() + 40, state.getStateColour());
        this.networkDiagram.diagamRedraw();
    }

    private void drawFoundStatus() {
        current.access(() ->
            this.networkDiagram.drawNodeFoundStatus());

        this.networkDiagram.diagamRedraw();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.redraw();
    }

    public void redrawFlowControl() {
        this.networkDiagram.drawFlowControl(currentFlow.getControlX(), currentFlow.getControlY(), currentFlow.getControlImageW(), currentFlow.getControlImageH()
            , this.currentFlow.getStartupType() != null ? this.currentFlow.getStartupType().name().toLowerCase() : null);
        this.networkDiagram.diagamRedraw();
    }

    public void redraw()
    {
        if (!this.moduleView && this.currentFlow != null)
        {
            this.networkDiagram = this.createNetworkDiagram(this.currentFlow);

            this.networkDiagram.drawFlow(this.currentFlow.getX(), this.currentFlow.getY(), this.currentFlow.getW()
                , this.currentFlow.getH(), this.currentFlow.getName());

            this.networkDiagram.drawFlowControl(currentFlow.getControlX(), currentFlow.getControlY(), currentFlow.getControlImageW(), currentFlow.getControlImageH()
                , this.currentFlow.getStartupType() != null ? this.currentFlow.getStartupType().name().toLowerCase() : null);

            this.networkDiagram.drawIsRecording(10.0 + this.currentFlow.getX(), 10.0 + this.currentFlow.getY(), 40
                , 40, this.currentFlow.isRecording());

            FlowState flowState = FlowStateCache.instance().get(this.module, this.currentFlow);

            if(flowState != null)
            {
                this.drawFlowStatus(flowState.getState());
            }

            this.removeAll();
            this.add(networkDiagram);
            this.drawFoundStatus();
        }
        else if(this.moduleView && this.module != null)
        {
            this.networkDiagram = this.createNetworkDiagram(this.module);

            this.removeAll();

            this.add(networkDiagram);
            this.drawFoundStatus();
        }
    }

    public void setCurrentFlow(Flow currentFlow)
    {
        this.currentFlow = currentFlow;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.redraw();
        UI ui = attachEvent.getUI();
        flowStateBroadcasterRegistration = FlowStateBroadcaster.register(flowState ->
        {
            logger.debug("Received flow state: " + flowState);
            this.drawFlowStatus(ui, flowState);
        });

        cacheStateBroadcasterRegistration = CacheStateBroadcaster.register(flowState ->
        {
            logger.debug("Received flow state: " + flowState);
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

    public Module getModule() {
        return this.module;
    }

    public Flow getCurrentFlow() {
        return this.currentFlow;
    }
}
