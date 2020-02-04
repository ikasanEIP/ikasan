package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
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
import org.ikasan.dashboard.ui.visualisation.adapter.service.BusinessStreamVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanFlowLayoutManager;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanModuleLayoutManager;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.NodeFoundStatus;
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

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class BusinessStreamVisualisation extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(BusinessStreamVisualisation.class);
    private Map<String, Node> nodeMap;
    private NetworkDiagram networkDiagram;
    private Flow currentFlow;
    private Module module;
    private boolean moduleView = false;

    private Registration flowStateBroadcasterRegistration;
    private Registration cacheStateBroadcasterRegistration;

    private ModuleControlRestServiceImpl moduleControlRestService;
    private ConfigurationRestServiceImpl configurationRestService;
    private TriggerRestServiceImpl triggerRestService;

    private BusinessStream businessStream;
    private ArrayList<Node> nodes = new ArrayList<>();

    private UI current;

    public BusinessStreamVisualisation(ModuleControlRestServiceImpl moduleControlRestService
        , ConfigurationRestServiceImpl configurationRestService
        , TriggerRestServiceImpl triggerRestService)
    {
        this.moduleControlRestService = moduleControlRestService;
        this.configurationRestService = configurationRestService;
        this.triggerRestService = triggerRestService;
        this.setSizeFull();
        this.nodeMap = new HashMap<>();
        current = UI.getCurrent();
    }

    /**
     *
     * @param json
     */
    public void createBusinessStreamGraphGraph(String json) throws IOException
    {
        BusinessStreamVisjsAdapter adapter = new BusinessStreamVisjsAdapter();

        this.businessStream = adapter.toBusinessStreamGraph(json);

        nodes.addAll(businessStream.getFlows());
        nodes.addAll(businessStream.getDestinations());
        nodes.addAll(businessStream.getIntegratedSystems());

        if(this.networkDiagram != null)
        {
            this.remove(networkDiagram);
        }

        updateNetworkDiagram(nodes, businessStream.getEdges());
        this.add(networkDiagram);
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

            JsonArray nodes = doubleClickEvent.getParams().getArray("nodes");

            if(nodes.length() == 0)
            {
                JsonObject pointer = doubleClickEvent.getParams().getObject("pointer");
                logger.info("pointer: " + pointer);

                JsonObject canvas = pointer.getObject("canvas");

                Double x = canvas.getNumber("x");
                Double y = canvas.getNumber("y");

                logger.info(currentFlow.toString());

                if((x > currentFlow.getX() && x < (currentFlow.getX() + currentFlow.getW()))
                    && (y > currentFlow.getY() && y < (currentFlow.getY() + currentFlow.getH())))
                {
                    logger.info("Inside flow!");
                    FlowOptionsDialog flowOptionsDialog = new FlowOptionsDialog(module, currentFlow, configurationRestService);
                    flowOptionsDialog.open();
                }

                return;
            }

            String node = nodes.get(0).asString();

            logger.info("Node: " + node);

            if(this.module.getComponentMap().get(node) != null)
            {
                ComponentOptionsDialog componentNodeActionDialog = new ComponentOptionsDialog(this.module,
                    this.currentFlow.getName(), this.module.getComponentMap().get(node).getComponentName(),
                    this.module.getComponentMap().get(node).isConfigurable(), this.configurationRestService,
                    this.triggerRestService);

                componentNodeActionDialog.open();
            }
        });

        logger.info("Finished creating network diagram for module [{}] to visualisation.", module.getName());

        return networkDiagram;
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param nodes a list containing all network nodes.
     * @param edges a list containing all the network edges.
     */
    protected void updateNetworkDiagram(List<Node> nodes, List<Edge> edges)
    {
        Physics physics = new Physics();
        physics.setEnabled(false);

        networkDiagram = new NetworkDiagram
            (Options.builder()
                .withAutoResize(true)
                .withPhysics(physics)
                .withEdges(
                    Edges.builder()
                        .withArrows(new Arrows(new ArrowHead()))
                        .withColor(EdgeColor.builder()
                            .withColor("#000000")
                            .build())
                        .withDashes(false)
                        .build())
                .build());

        networkDiagram.setSizeFull();

        networkDiagram.setNodes(nodes);
        networkDiagram.setEdges(edges);

        networkDiagram.addDoubleClickListener((DoubleClickListener) doubleClickEvent ->
        {
            logger.info(doubleClickEvent.getParams().toString());

            JsonArray nodesArray = doubleClickEvent.getParams().getArray("nodes");
            String nodeId = nodesArray.get(0).asString();

            logger.info(nodeId);

//            for(Flow flow: graph.getFlows())
//            {
//                if(flow.getId().equals(nodeId) && flow.getFoundStatus().equals(NodeFoundStatus.FOUND))
//                {
//                    eventViewDialog.open(flow.getWireapEvent());
//                }
//            }
        });

    }

    public void performWiretapSearch(String searchTerm, Date startDate, Date endDate)
    {
        Set<String> moduleNames = new HashSet<>();
        Set<String> flowNames = new HashSet<>();

        boolean userDotSeperator = false;
        for(Node node: this.businessStream.getFlows())
        {
            if (node.getId().contains("."))
            {
                String[] moduleFlowPair = node.getId().split(Pattern.quote("."));
                moduleNames.add(moduleFlowPair[0]);
                flowNames.add(moduleFlowPair[1]);
                userDotSeperator = true;
            }
            else
            {
                moduleNames.add(node.getId());
            }
        }

//        final PagedSearchResult<WiretapEvent> results =  this.solrWiretapService.findWiretapEvents(0, 500, "", false, moduleNames, flowNames,
//            null, null, null, startDate, endDate, searchTerm);
//
//        logger.info("Found:" + results.getResultSize());

        HashMap<String, Node> nodeMap = new HashMap<>();

        for(Node node: nodes)
        {
            node.setFoundStatus(NodeFoundStatus.FOUND);
            nodeMap.put(node.getId(), node);

            if(node instanceof org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow)
            {
                this.networkDiagram.drawStatusBorder(node.getX() - 40, node.getY() - 30, 80
                    , 60, State.RUNNING_COLOUR);
            }
        }

        HashSet<String> correlationValues = new HashSet<>();
        HashMap<String, WiretapEvent<String>> uniqueResults = new HashMap<>();
//        for(WiretapEvent<String> result: results.getPagedResults())
//        {
//            Node node = userDotSeperator ? nodeMap.get(result.getModuleName() + "." + result.getFlowName()) : nodeMap.get(result.getModuleName());
//
//            if(node != null)
//            {
//                node.setFoundStatus(NodeFoundStatus.FOUND);
//                ((org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow)node).setWireapEvent(result.getEvent());
//                uniqueResults.put(result.getEvent(), result);
//
//                if(((org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow)node).getCorrelator() != null)
//                {
//                    String correlationValue = (String)((org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow)node)
//                        .getCorrelator().correlate(result.getEvent());
//
//                    correlationValues.add(correlationValue);
//                    logger.info("Correlation value = " + correlationValue);
//                }
//            }
//        }

        logger.info("Number of unique correlations values = " + correlationValues.size());

//        for(String value: correlationValues)
//        {
//            PagedSearchResult<WiretapEvent> secondResults =  this.solrWiretapService.findWiretapEvents(0, 500, "timestamp", false, moduleNames, flowNames,
//                null, null, null, startDate, endDate, value);
//
//            logger.info("Found correlating:" + secondResults.getResultSize());
//
//            for(WiretapEvent<String> result: secondResults.getPagedResults())
//            {
//                Node node = nodeMap.get(result.getModuleName() + "." + result.getFlowName());
//
//                if(node != null)
//                {
//                    node.setFoundStatus(NodeFoundStatus.FOUND);
//                    ((org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow)node)
//                        .setWireapEvent(result.getEvent());
//                    uniqueResults.put(result.getEvent(), result);
//                }
//            }
//        }
//
//        logger.info("Number of unique events = " + uniqueResults.size());
//
//        this.wiretapSearchResults = new ArrayList<>(uniqueResults.values());
//
//        if(uniqueResults.size() > 0)
//        {
//            this.viewListButton.setVisible(true);
//        }
//        else
//        {
//            this.viewListButton.setVisible(false);
//        }

        current.access(() ->
            networkDiagram.updateNodesStates(nodes));
        this.networkDiagram.diagamRedraw();
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

            this.networkDiagram.drawFlow(this.currentFlow.getX(), this.currentFlow.getY(), this.currentFlow.getW()
                , this.currentFlow.getH(), this.currentFlow.getName());

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
