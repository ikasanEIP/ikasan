package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import elemental.json.JsonArray;
import org.apache.commons.io.IOUtils;
import org.ikasan.dashboard.ui.visualisation.adapter.service.BusinessStreamVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanModuleLayoutManager;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStreamGraph;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.FlowState;
import org.ikasan.dashboard.ui.component.ErrorListDialog;
import org.ikasan.dashboard.ui.component.EventViewDialog;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.component.WiretapListDialog;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.NodeFoundStatus;
import org.ikasan.vaadin.visjs.network.event.OnContextEvent;
import org.ikasan.vaadin.visjs.network.listener.ClickListener;
import org.ikasan.vaadin.visjs.network.listener.DoubleClickListener;
import org.ikasan.vaadin.visjs.network.listener.OnContextListener;
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
import org.springframework.stereotype.Component;
import org.vaadin.erik.SlideMode;
import org.vaadin.erik.SlideTab;
import org.vaadin.erik.SlideTabBuilder;
import org.vaadin.erik.SlideTabPosition;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Route(value = "visualisation", layout = IkasanAppLayout.class)
@UIScope
@Component
public class GraphView extends HorizontalLayout
{
    Logger logger = LoggerFactory.getLogger(GraphView.class);

    @Resource
    private WiretapService<FlowEvent,PagedSearchResult<WiretapEvent>> solrWiretapService;

    @Resource
    private ErrorReportingService solrErrorReportingService;

    @Resource
    private ExclusionManagementService solrExclusionService;

    private BusinessStreamGraph graph = null;
    private Upload upload;
    private List<Node> nodes = new ArrayList<>();
    private NetworkDiagram networkDiagram;
    private VaadinSession session;
    private UI current;
    private Grid<Flow> grid = new Grid<>();
    private Button viewListButton;
    private RadioButtonGroup<String> group = new RadioButtonGroup<>();
    private List<WiretapEvent> wiretapSearchResults;
    private List<ErrorOccurrence> errorOccurrences;

    /**
     * Constructor
     */
    public GraphView()
    {
        this.setMargin(true);
        this.setSizeFull();

        this.createNetworkDiagram();
        this.initialiseToolsGrid();
        this.createToolsSlider();
        this.createSearchSlider();

        session = UI.getCurrent().getSession();
        current = UI.getCurrent();
    }

    /**
     * Method to create the new network diagram and add it as a component to the view.
     */
    protected void createNetworkDiagram()
    {
        this.updateNetworkDiagram(new ArrayList<>(), new ArrayList<>());
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

            for(Flow flow: graph.getFlows())
            {
                if(flow.getId().equals(nodeId) && flow.getFoundStatus().equals(NodeFoundStatus.FOUND))
                {
                    EventViewDialog eventViewDialog = new EventViewDialog(flow.getWireapEvent());
                    eventViewDialog.open();
                }
            }
        });

    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param module to render.
     */
    protected void updateNetworkDiagram(Module module)
    {
        Physics physics = new Physics();
        physics.setEnabled(false);

        networkDiagram = new NetworkDiagram
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
                        .build())
                .withNodes(Nodes.builder().withFont(Font.builder().withSize(11).build()).build())
                .build());

        networkDiagram.setSizeFull();

        IkasanModuleLayoutManager layoutManager = new IkasanModuleLayoutManager(module, networkDiagram, null);
        layoutManager.layout();

        networkDiagram.addDoubleClickListener((DoubleClickListener) doubleClickEvent ->
        {
            logger.info(doubleClickEvent.getParams().toString());
        });

        networkDiagram.addClickListener((ClickListener) clickEvent -> {
            logger.info(clickEvent.getParams().toString());
        });

        networkDiagram.addOnContextListener((OnContextListener) onContextEvent -> {
            logger.info(onContextEvent.getParams().toString());
        });
    }

    /**
     * Method to initialise the grid on the tools slider.
     */
    protected void initialiseToolsGrid()
    {
        // Create a grid bound to the list
        grid.setWidth("100%");
        grid.setHeight("100%");
        grid.addColumn(Node::getLabel).setHeader("Name");
        grid.addColumn(new ComponentRenderer<>(node ->
        {
            ComboBox<String> comboBox = new ComboBox<>("");
            comboBox.setWidth("200px");
            comboBox.getClassNames().add("small");
            comboBox.setItems(FlowState.RUNNING, FlowState.PAUSED, FlowState.RECOVERING,
                FlowState.STOPPED, FlowState.STOPPED_IN_ERROR);


            comboBox.addValueChangeListener(event -> {
                node.setState(comboBox.getValue());

                current.access(() ->
                    networkDiagram.updateNodesStates(nodes));

            });

            return comboBox;

        })).setHeader("State");
    }

    /**
     * Method to create the graph contents from an InputStream. Typically
     * used by the upload feature of the application.
     *
     * @param mimeType The mime type of the uploaded content.
     * @param stream The input stream.
     */
    protected void createGraph(String mimeType, InputStream stream)
    {
        logger.info(mimeType);
        if (mimeType.equals("application/json"))
        {
            String text;
            try
            {
                text = IOUtils.toString(stream, "UTF-8");

                logger.info(text);

//                BusinessStreamVisjsAdapter adapter = new BusinessStreamVisjsAdapter();
//
//                this.graph = adapter.toBusinessStreamGraph(text);
//
//                nodes = new ArrayList<>();
//                nodes.addAll(graph.getFlows());
//                nodes.addAll(graph.getDestinations());
//                nodes.addAll(graph.getIntegratedSystems());
//
//                this.remove(networkDiagram);
//                updateNetworkDiagram(nodes, graph.getEdges());
//                this.add(networkDiagram);
//
//                this.grid.setItems(graph.getFlows());

                JsonModuleMetaDataProvider jsonModuleMetaDataProvider
                    = new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());

                ModuleMetaData moduleMetaData = jsonModuleMetaDataProvider
                    .deserialiseModule(text);

                ModuleVisjsAdapter adapter = new ModuleVisjsAdapter();
                Module module = adapter.adapt(moduleMetaData);

                this.remove(networkDiagram);
                updateNetworkDiagram(module);
                this.add(networkDiagram);
            }
            catch (IOException e)
            {
                 e.printStackTrace();
                 NotificationHelper.showErrorNotification("An error has occurred attempting to load graph JSON: " + e.getLocalizedMessage());
            }
        }
        else
        {
            NotificationHelper.showErrorNotification("File should be JSON!");
        }

    }

    /**
     * Method to create the tool slider.
     */
    protected void createToolsSlider()
    {
        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);


        upload.addSucceededListener(event ->
            createGraph(event.getMIMEType(), buffer.getInputStream()));


        Div div = new Div();
        div.add(upload);

        VerticalLayout sliderLayout = new VerticalLayout();
        sliderLayout.setSizeFull();
        sliderLayout.add(div);
        sliderLayout.add(grid);

        Div card = new Div();
        card.setSizeFull();
        card.setWidth("680px");
        card.setHeight("100%");
        card.getStyle().set("background", "white");
        card.add(sliderLayout);


        SlideTab gridSlider = new SlideTabBuilder(card)
            .expanded(false)
            .mode(SlideMode.RIGHT)
            .caption("Tools")
            .tabPosition(SlideTabPosition.MIDDLE)
            .fixedContentSize(700)
            .zIndex(1)
            .flowInContent(true)
            .build();

        super.add(gridSlider);
    }

    /**
     * Method to create the search slider.
     */
    protected void createSearchSlider()
    {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setSizeFull();

        LocalDate now = LocalDate.now();

        group.setItems("Wiretap", "Error", "Exclusion");
        group.setValue("Wiretap");

        searchLayout.add(group);

        DatePicker startDate = new DatePicker(now.minus(1, ChronoUnit.DAYS));
        searchLayout.add(startDate);

        DatePicker endDate = new DatePicker(now.plus(1, ChronoUnit.DAYS));
        searchLayout.add(endDate);

        TextField searchText = new TextField();
        searchText.setWidth("300px");
        searchText.setHeight("30px");
        searchLayout.add(searchText);

        Button searchButton = new Button("Search");
        searchButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> search(searchText.getValue(),
            Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())));

        searchLayout.add(searchButton);

        this.viewListButton = new Button("Result List");
        this.viewListButton.setVisible(false);
        this.viewListButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            if(this.group.getValue().equals("Wiretap"))
            {
                WiretapListDialog dialog = new WiretapListDialog(this.wiretapSearchResults);
                dialog.open();
            }
            else if(this.group.getValue().equals("Error"))
            {
                ErrorListDialog dialog = new ErrorListDialog(this.errorOccurrences, this.solrErrorReportingService);
                dialog.open();
            }
        });

        searchLayout.add(viewListButton);

        Div searchDiv = new Div();
        searchDiv.setSizeFull();
        searchDiv.setWidth("100%");
        searchDiv.setHeight("70px");
        searchDiv.getStyle().set("background", "white");
        searchDiv.getStyle().set("color", "black");
        searchDiv.add(searchLayout);

        SlideTab searchSlider = new SlideTabBuilder(searchDiv)
            .expanded(false)
            .mode(SlideMode.BOTTOM)
            .caption("Search")
            .tabPosition(SlideTabPosition.MIDDLE)
            .fixedContentSize(80)
            .zIndex(1)
            .flowInContent(true)
            .build();

        add(searchSlider);
    }

    /**
     * Method to perform the search.
     *
     * @param searchTerm The search term we are seeding the search with.
     * @param startDate The start date range.
     * @param endDate The end date range.
     */
    protected void search(String searchTerm, Date startDate, Date endDate)
    {
        if(graph == null || graph.getFlows() == null || graph.getFlows().isEmpty())
        {
            NotificationHelper.showUserNotification("The Ikasan Visualisation appears to be empty!");
            return;
        }

        if(searchTerm == null || searchTerm.isEmpty())
        {
            NotificationHelper.showUserNotification("A search term must be entered!");
            return;
        }

        if(group.getValue().equals("Wiretap"))
        {
            this.performWiretapSearch(searchTerm, startDate, endDate);
        }
        else if(group.getValue().equals("Error"))
        {
            this.performErrorSearch(searchTerm, startDate, endDate);
        }
    }

    protected void performWiretapSearch(String searchTerm, Date startDate, Date endDate)
    {
        Set<String> moduleNames = new HashSet<>();
        Set<String> flowNames = new HashSet<>();

        boolean userDotSeperator = false;
        for(Node node: this.graph.getFlows())
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

        final PagedSearchResult<WiretapEvent> results =  this.solrWiretapService.findWiretapEvents(0, 500, "", false, moduleNames, flowNames,
            null, null, null, startDate, endDate, searchTerm);

        logger.info("Found:" + results.getResultSize());

        HashMap<String, Node> nodeMap = new HashMap<>();

        for(Node node: nodes)
        {
            node.setFoundStatus(NodeFoundStatus.NOT_FOUND);
            nodeMap.put(node.getId(), node);
        }

        HashSet<String> correlationValues = new HashSet<>();
        HashMap<String, WiretapEvent<String>> uniqueResults = new HashMap<>();
        for(WiretapEvent<String> result: results.getPagedResults())
        {
            Node node = userDotSeperator ? nodeMap.get(result.getModuleName() + "." + result.getFlowName()) : nodeMap.get(result.getModuleName());

            if(node != null)
            {
                node.setFoundStatus(NodeFoundStatus.FOUND);
                ((Flow)node).setWireapEvent(result.getEvent());
                uniqueResults.put(result.getEvent(), result);

                if(((Flow)node).getCorrelator() != null)
                {
                    String correlationValue = (String)((Flow)node).getCorrelator().correlate(result.getEvent());

                    correlationValues.add(correlationValue);
                    logger.info("Correlation value = " + correlationValue);
                }
            }
        }

        logger.info("Number of unique correlations values = " + correlationValues.size());

        for(String value: correlationValues)
        {
            PagedSearchResult<WiretapEvent> secondResults =  this.solrWiretapService.findWiretapEvents(0, 500, "timestamp", false, moduleNames, flowNames,
                null, null, null, startDate, endDate, value);

            logger.info("Found correlating:" + secondResults.getResultSize());

            for(WiretapEvent<String> result: secondResults.getPagedResults())
            {
                Node node = nodeMap.get(result.getModuleName() + "." + result.getFlowName());

                if(node != null)
                {
                    node.setFoundStatus(NodeFoundStatus.FOUND);
                    ((Flow)node).setWireapEvent(result.getEvent());
                    uniqueResults.put(result.getEvent(), result);
                }
            }
        }

        logger.info("Number of unique events = " + uniqueResults.size());

        this.wiretapSearchResults = new ArrayList<>(uniqueResults.values());

        if(uniqueResults.size() > 0)
        {
            this.viewListButton.setVisible(true);
        }
        else
        {
            this.viewListButton.setVisible(false);
        }

        current.access(() ->
            networkDiagram.updateNodesStates(nodes));
    }

    protected void performErrorSearch(String searchTerm, Date startDate, Date endDate)
    {
        ArrayList<String> moduleNames = new ArrayList<>();
        ArrayList<String> flowNames = new ArrayList<>();

        for(Node node: this.graph.getFlows())
        {
            if (node.getId().contains("."))
            {
                String[] moduleFlowPair = node.getId().split(Pattern.quote("."));
                moduleNames.add(moduleFlowPair[0]);
                flowNames.add(moduleFlowPair[1]);
            }
            else
            {
                moduleNames.add(node.getId());
            }
        }

        errorOccurrences = this.solrErrorReportingService.find(moduleNames, flowNames, null, startDate, endDate, 500);

        logger.info("Found errors:" + errorOccurrences.size());

        if(errorOccurrences.size() > 0)
        {
            this.viewListButton.setVisible(true);
        }
        else
        {
            this.viewListButton.setVisible(false);
        }

//        HashMap<String, Node> nodeMap = new HashMap<>();
//
//        for(Node node: nodes)
//        {
//            node.setFoundStatus(NodeFoundStatus.NOT_FOUND);
//            nodeMap.put(node.getId(), node);
//        }
//
//        HashSet<String> correlationValues = new HashSet<>();
//        HashMap<String, WiretapEvent<String>> uniqueResults = new HashMap<>();
//        for(WiretapEvent<String> result: results.getPagedResults())
//        {
//            Node node = nodeMap.get(result.getModuleName() + "." + result.getFlowName());
//
//            if(node != null)
//            {
//                node.setFoundStatus(NodeFoundStatus.FOUND);
//                ((Flow)node).setWireapEvent(result.getEvent());
//                uniqueResults.put(result.getEvent(), result);
//
//                if(((Flow)node).getCorrelator() != null)
//                {
//                    String correlationValue = (String)((Flow)node).getCorrelator().correlate(result.getEvent());
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
//                    node.setFoundStatus(NodeFoundStatus.FOUND);
//                    ((Flow)node).setWireapEvent(result.getEvent());
//                    uniqueResults.put(result.getEvent(), result);
//                }
//            }
//        }
//
//        logger.info("Number of unique events = " + uniqueResults.size());
//
//        if(uniqueResults.size() > 0)
//        {
//            this.viewListButton.setVisible(true);
//
//            this.viewListButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
//            {
//                final WiretapListDialog component = new WiretapListDialog(results.getPagedResults());
//                component.open();
//
//                component.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
//                {
//                    if(dialogOpenedChangeEvent.isOpened() == false)
//                    {
//                        component.removeAll();
//                    }
//                });
//            });
//        }
//        else
//        {
//            this.viewListButton.setVisible(false);
//        }
//
//        current.access(() ->
//            networkDiagram.updateNodesStates(nodes));
    }
}

