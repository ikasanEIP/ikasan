package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonArray;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.cache.CacheStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.visualisation.adapter.service.BusinessStreamVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrGeneralService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
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
import org.ikasan.vaadin.visjs.network.options.physics.Physics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BusinessStreamVisualisation extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(BusinessStreamVisualisation.class);
    private NetworkDiagram networkDiagram;

    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    private Registration flowStateBroadcasterRegistration;
    private Registration cacheStateBroadcasterRegistration;

    private ModuleControlRestServiceImpl moduleControlRestService;
    private ConfigurationRestServiceImpl configurationRestService;
    private TriggerRestServiceImpl triggerRestService;
    private ModuleMetaDataService moduleMetaDataService;
    private ConfigurationMetaDataService configurationMetadataService;

//    private WiretapService<FlowEvent,PagedSearchResult<WiretapEvent>> solrWiretapService;

    private BusinessStream businessStream;
    private ArrayList<Node> nodes = new ArrayList<>();

//    private Button viewListButton;

    private Map<String, Flow> flowMap;

    private UI current;

    public BusinessStreamVisualisation(ModuleControlRestServiceImpl moduleControlRestService
        , ConfigurationRestServiceImpl configurationRestService, TriggerRestServiceImpl triggerRestService
        , ModuleMetaDataService moduleMetaDataService
        , ConfigurationMetaDataService configurationMetadataService
        , SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService)
    {
        this.moduleControlRestService = moduleControlRestService;
        this.configurationRestService = configurationRestService;
        this.triggerRestService = triggerRestService;
//        this.solrWiretapService = solrWiretapService;
        this.moduleMetaDataService = moduleMetaDataService;
        this.configurationMetadataService = configurationMetadataService;
//        this.viewListButton = viewListButton;
        this.solrSearchService = solrSearchService;
        current = UI.getCurrent();

        this.setSizeFull();
    }

    /**
     *
     * @param json
     */
    public void createBusinessStreamGraphGraph(String json) throws IOException {
        BusinessStreamVisjsAdapter adapter = new BusinessStreamVisjsAdapter();

        this.businessStream = adapter.toBusinessStreamGraph(json);

        nodes = new ArrayList<>();
        nodes.addAll(businessStream.getFlows());
        nodes.addAll(businessStream.getDestinations());
        nodes.addAll(businessStream.getIntegratedSystems());

        if (this.networkDiagram != null) {
            this.remove(networkDiagram);
        }

        this.populateFlowMap(businessStream.getFlows());

        updateNetworkDiagram(nodes, businessStream.getEdges());
        this.add(networkDiagram);
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
                .withInteraction(Interaction.builder().withDragNodes(false).build())
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
            logger.info("Flow + " + this.flowMap.get(nodeId));

            if(this.flowMap.get(nodeId) != null){
                ModuleMetaData moduleMetaData = this.moduleMetaDataService
                    .findById(nodeId.substring(0, nodeId.indexOf(".")));

                logger.info("ModuleMetaData + " + moduleMetaData);

                FlowVisualisationDialog flowVisualisationDialog
                    = new FlowVisualisationDialog(this.moduleControlRestService, this.configurationRestService,
                    this.triggerRestService, this.configurationMetadataService, moduleMetaData
                    , nodeId.substring(nodeId.indexOf(".") + 1), this.solrSearchService);

                flowVisualisationDialog.open();
            }


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
//        Set<String> moduleNames = new HashSet<>();
//        Set<String> flowNames = new HashSet<>();
//
//        boolean userDotSeperator = false;
//        for(Node node: this.businessStream.getFlows())
//        {
//            if (node.getId().contains("."))
//            {
//                String[] moduleFlowPair = node.getId().split(Pattern.quote("."));
//                moduleNames.add(moduleFlowPair[0]);
//                flowNames.add(moduleFlowPair[1]);
//                userDotSeperator = true;
//            }
//            else
//            {
//                moduleNames.add(node.getId());
//            }
//        }

//        final PagedSearchResult<WiretapEvent> results =  this.solrWiretapService.findWiretapEvents(0, 500, "", false, moduleNames, flowNames,
//            null, null, null, startDate, endDate, searchTerm);
//
//        logger.info("Found:" + results.getResultSize());
//
//        HashMap<String, Node> nodeMap = new HashMap<>();

//        for(Node node: nodes)
//        {
//            if(System.currentTimeMillis() % 2 == 0) {
//                node.setWiretapFoundStatus(NodeFoundStatus.FOUND);
//            } else {
//                node.setWiretapFoundStatus(NodeFoundStatus.NOT_FOUND);
//            }
////            nodeMap.put(node.getId(), node);
////
////            if(node instanceof org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow)
////            {
////                this.networkDiagram.drawStatusBorder(node.getX() - 40, node.getY() - 30, 80
////                    , 60, State.RUNNING_COLOUR);
////            }
//        }

//        HashSet<String> correlationValues = new HashSet<>();
//        HashMap<String, WiretapEvent<String>> uniqueResults = new HashMap<>();
//        for(WiretapEvent<String> result: results.getPagedResults())
//        {
//            Node node = userDotSeperator ? nodeMap.get(result.getModuleName() + "." + result.getFlowName()) : nodeMap.get(result.getModuleName());
//
//            if(node != null)
//            {
//                node.setWiretapFoundStatus(NodeFoundStatus.FOUND);
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
//
//        logger.info("Number of unique correlations values = " + correlationValues.size());
//
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
////                    if(System.currentTimeMillis() % 2 == 0) {
////                        node.setWiretapFoundStatus(NodeFoundStatus.FOUND);
////                    } else {
////                        node.setWiretapFoundStatus(NodeFoundStatus.NOT_FOUND);
////                    }
////                    ((org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow)node)
////                        .setWireapEvent(result.getEvent());
////                    uniqueResults.put(result.getEvent(), result);
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

        for(Node node: nodes)
        {
            if(System.currentTimeMillis() % 2 == 0) {
                node.setWiretapFoundStatus(NodeFoundStatus.FOUND);
            } else {
                node.setWiretapFoundStatus(NodeFoundStatus.NOT_FOUND);
            }
        }

        logger.info("nodes" + nodes);

        current.access(() ->{
            this.networkDiagram.updateNodesStates(nodes);
            this.networkDiagram.drawNodeFoundStatus();
            this.networkDiagram.diagamRedraw();
        });
    }

    private void drawFlowStatus(FlowState state)
    {
        if(this.flowMap != null && flowMap.containsKey(state.getModuleName() + "." + state.getFlowName())) {
            Flow flow = flowMap.get(state.getModuleName() + "." + state.getFlowName());
            this.networkDiagram.drawStatusBorder(flow.getX() - 40
                , flow.getY() - 30, 80
                , 60, state.getState().getStateColour());
        }

        this.networkDiagram.diagamRedraw();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.redraw();
    }

    public void redraw()
    {
        if(this.businessStream != null) {
            nodes = new ArrayList<>();
            nodes.addAll(businessStream.getFlows());
            nodes.addAll(businessStream.getDestinations());
            nodes.addAll(businessStream.getIntegratedSystems());

            if (this.networkDiagram != null) {
                this.remove(networkDiagram);
            }

            this.populateFlowMap(businessStream.getFlows());

            updateNetworkDiagram(nodes, businessStream.getEdges());

            for(String key: this.flowMap.keySet()) {
                if(key.contains(".")) {
                    ModuleMetaData module = this.moduleMetaDataService
                        .findById(key.substring(0, key.indexOf(".")));

                    if(module != null) {
                        FlowState flowState = FlowStateCache.instance().get(module, key.substring(key.indexOf(".") + 1));

                        if (flowState != null) {
                            this.drawFlowStatus(flowState);
                        }
                    }
                }
            }

            this.add(networkDiagram);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.redraw();
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
            if(this.flowMap != null && this.flowMap.containsKey(flowState.getModuleName() + "." + flowState.getFlowName()))
            {
                this.drawFlowStatus(flowState);
            }
        });
    }

    private void populateFlowMap(List<Flow> flows){
        this.flowMap = flows
            .stream()
            .collect(Collectors.toMap(Flow::getId, Function.identity()));
    }
}
