package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import elemental.json.JsonArray;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.security.SecurityUtils;
import org.ikasan.dashboard.ui.component.ErrorListDialog;
import org.ikasan.dashboard.ui.component.EventViewDialog;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.component.WiretapListDialog;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.adapter.service.BusinessStreamVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.component.*;
import org.ikasan.dashboard.ui.visualisation.dao.BusinessStreamMetaDataDaoImpl;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeListener;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.listener.DoubleClickListener;
import org.ikasan.vaadin.visjs.network.options.Options;
import org.ikasan.vaadin.visjs.network.options.edges.ArrowHead;
import org.ikasan.vaadin.visjs.network.options.edges.Arrows;
import org.ikasan.vaadin.visjs.network.options.edges.EdgeColor;
import org.ikasan.vaadin.visjs.network.options.edges.Edges;
import org.ikasan.vaadin.visjs.network.options.physics.Physics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.vaadin.erik.SlideMode;
import org.vaadin.erik.SlideTab;
import org.vaadin.erik.SlideTabBuilder;
import org.vaadin.erik.SlideTabPosition;
import org.vaadin.tabs.PagedTabs;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Route(value = "visualisation", layout = IkasanAppLayout.class)
@UIScope
@PageTitle("Ikasan - Visualisation")
@Component
public class GraphView extends VerticalLayout implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(GraphView.class);

    @Resource
    private WiretapService<FlowEvent,PagedSearchResult<WiretapEvent>> solrWiretapService;

    @Resource
    private ErrorReportingService solrErrorReportingService;

    @Resource
    private ExclusionManagementService solrExclusionService;

    @Resource
    private ModuleControlRestServiceImpl moduleControlRestService;

    @Autowired
    private ModuleMetaDataService moduleMetadataService;

    @Autowired
    private ConfigurationRestServiceImpl configurationRestService;

    @Autowired
    private TriggerRestServiceImpl triggerRestService;

    @Resource
    private ConfigurationMetaDataService configurationMetadataService;

    @Resource
    private BusinessStreamMetaDataService<BusinessStreamMetaData> businessStreamMetaDataService;

    private EventViewDialog eventViewDialog = new EventViewDialog();

    private BusinessStream graph = null;
    private List<Node> nodes = new ArrayList<>();
    private NetworkDiagram networkDiagram;
    private VaadinSession session;
    private UI current;
    private Grid<ModuleMetaData> modulesGrid = new Grid<>();
    private Grid<BusinessStreamMetaData> businessStreamGrid = new Grid<>();
    private Button viewListButton;
    private RadioButtonGroup<String> group = new RadioButtonGroup<>();
    private List<WiretapEvent> wiretapSearchResults;
    private List<ErrorOccurrence> errorOccurrences;
    private BusinessStreamMetaDataDaoImpl businessStreamMetaDataDao = new BusinessStreamMetaDataDaoImpl();
    private ModuleVisualisation moduleVisualisation;
    private H2 moduleLabel = new H2();
    private HorizontalLayout hl = new HorizontalLayout();
    private FlowComboBox flowComboBox;
    private ControlPanel controlPanel;

    private Registration broadcasterRegistration;

    private StatusPanel statusPanel;

    private Module currentModule;
    private Flow currentFlow;

    private List<GraphViewChangeListener> graphViewChangeListeners;

    private boolean initialised = false;

    private SlideTab toolSlider;
    private Button uploadBusinssStreamButton;
    private Tooltip uploadBusinssStreamButtonTooltip;

    /**
     * Constructor
     */
    public GraphView()
    {
        this.setMargin(true);
        this.setSizeFull();

        this.graphViewChangeListeners = new ArrayList<>();

        session = UI.getCurrent().getSession();
        current = UI.getCurrent();
    }

    private void init()
    {
        this.createNetworkDiagram();
        this.createToolsSlider();
        this.createSearchSlider();

        this.createModuleGrid();
        this.createdBusinessStreamGrid();
    }

    protected void createModuleGrid()
    {
        // Create a modulesGrid bound to the list
        modulesGrid.removeAllColumns();
        modulesGrid.setVisible(true);
        modulesGrid.setHeight("800px");
        modulesGrid.setWidth("100%");

        modulesGrid.addColumn(ModuleMetaData::getName).setHeader("Name");

        modulesGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<ModuleMetaData>>)
            doubleClickEvent ->
            {
                this.moduleLabel.setText(doubleClickEvent.getItem().getName());
                this.hl.setVisible(true);
                this.flowComboBox.setVisible(true);
                this.controlPanel.setVisible(true);
                this.statusPanel.setVisible(true);
                createGraph(doubleClickEvent.getItem());

                if(this.toolSlider.isExpanded())
                {
                    this.toolSlider.collapse();
                }
            });
    }

    protected void createdBusinessStreamGrid()
    {
        // Create a modulesGrid bound to the list
        businessStreamGrid.removeAllColumns();
        businessStreamGrid.setVisible(true);
        businessStreamGrid.setHeight("800px");
        businessStreamGrid.setWidth("100%");

        businessStreamGrid.addColumn(BusinessStreamMetaData::getName).setHeader("Name");
        businessStreamGrid.addColumn(new ComponentRenderer<>(businessStreamMetaData->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                this.businessStreamMetaDataService.delete(businessStreamMetaData.getId());
                this.populateBusinessStreamGrid();
            });

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setFlexGrow(1);

        businessStreamGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<BusinessStreamMetaData>>) doubleClickEvent ->
        {
            try
            {
                this.createBusinessStreamGraphGraph(doubleClickEvent.getItem().getJson());

                this.moduleLabel.setText(doubleClickEvent.getItem().getName());
                this.hl.setVisible(true);
                this.flowComboBox.setVisible(false);
                this.controlPanel.setVisible(false);
                this.statusPanel.setVisible(false);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                NotificationHelper.showErrorNotification(getTranslation("error.could-not-open-business-stream", UI.getCurrent().getLocale()));
            }

            if(this.toolSlider.isExpanded())
            {
                this.toolSlider.collapse();
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.populateModulesGrid();
        this.populateBusinessStreamGrid();

        if(uploadBusinssStreamButtonTooltip != null && this.uploadBusinssStreamButton != null)
        {
            this.uploadBusinssStreamButtonTooltip.attachToComponent(this.uploadBusinssStreamButton);
        }
    }

    /**
     * Method to create the new network diagram and add it as a component to the view.
     */
    protected void createNetworkDiagram()
    {
        this.updateNetworkDiagram(new ArrayList<>(), new ArrayList<>());

        flowComboBox = new FlowComboBox();
        flowComboBox.setItemLabelGenerator(org.ikasan.dashboard.ui.visualisation.model.flow.Flow::getName);
        flowComboBox.setHeight("40px");
        flowComboBox.setWidth("600px");

        flowComboBox.setRenderer(new ComponentRenderer<>(item ->
        {
            HorizontalLayout container = new HorizontalLayout();

            Icon icon = new Icon(VaadinIcon.CIRCLE);

            FlowState flowState = FlowStateCache.instance().get(currentModule, item);

            if(flowState != null)
            {
                icon.setColor(flowState.getState().getStateColour());
            }

            icon.setSize("15px");
            icon.setVisible(true);
            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setWidth("20px");
            verticalLayout.add(icon);
            verticalLayout.setHorizontalComponentAlignment(Alignment.END, icon);

            Label namelabel = new Label(item.getName());
            namelabel.setWidth("500px");

            container.setVerticalComponentAlignment(Alignment.CENTER, namelabel);
            container.add(namelabel, verticalLayout);

            return container;
        }));

        this.flowComboBox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<org.ikasan.dashboard.ui.visualisation.model.flow.Flow>, org.ikasan.dashboard.ui.visualisation.model.flow.Flow>>) comboBoxFlowComponentValueChangeEvent ->
        {
            if(comboBoxFlowComponentValueChangeEvent.getValue() != null)
            {
                logger.info("Switching to flow {}", comboBoxFlowComponentValueChangeEvent.getValue().getName());
                this.moduleVisualisation.setCurrentFlow(comboBoxFlowComponentValueChangeEvent.getValue());
                this.moduleVisualisation.redraw();

                this.currentFlow = comboBoxFlowComponentValueChangeEvent.getValue();

                this.fireModuleFlowChangeEvent();
                logger.info("Finished switching to flow {}", comboBoxFlowComponentValueChangeEvent.getValue().getName());
            }
        });

        HorizontalLayout moduleNameLayout = new HorizontalLayout();
        moduleNameLayout.setMargin(false);
        moduleNameLayout.setSpacing(false);
        moduleNameLayout.add(moduleLabel);

        HorizontalLayout comboBoxLayout = new HorizontalLayout();
        comboBoxLayout.setMargin(false);
        comboBoxLayout.setSpacing(false);
        comboBoxLayout.add(flowComboBox);

        hl.setWidth("100%");
        hl.setMargin(false);

        statusPanel = new StatusPanel(this.moduleControlRestService);

        hl.setFlexGrow(1, moduleNameLayout);
        hl.setFlexGrow(1, statusPanel);
        hl.setFlexGrow(5, comboBoxLayout);
        hl.setFlexGrow(3, controlPanel);

        hl.add(moduleNameLayout, statusPanel, comboBoxLayout, controlPanel);
        hl.setVerticalComponentAlignment(Alignment.BASELINE, moduleNameLayout, statusPanel, comboBoxLayout, controlPanel);

        if(this.currentModule == null)
        {
            hl.setVisible(false);
        }

        moduleVisualisation = new ModuleVisualisation(this.moduleControlRestService,
            this.configurationRestService,
            this.triggerRestService);

        this.add(hl);
        this.add(moduleVisualisation);

        this.graphViewChangeListeners.add(statusPanel);
        this.graphViewChangeListeners.add(controlPanel);
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

    /**
     * Method to initialise the modulesGrid on the tools slider.
     */
    protected void populateModulesGrid()
    {
        List<ModuleMetaData> moduleMetaData = this.moduleMetadataService.findAll();

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        if(!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)) {
            Set<String> accessibleModules = SecurityUtils.getAccessibleModules(authentication);

            moduleMetaData = moduleMetaData
                .stream()
                .filter(metadata -> accessibleModules.contains(metadata.getName()))
                .collect(Collectors.toList());
        }

        this.modulesGrid.setItems(moduleMetaData);
    }

    /**
     * Method to initialise the modulesGrid on the tools slider.
     */
    protected void populateBusinessStreamGrid()
    {
        List<BusinessStreamMetaData> businessStreamMetaData = this.businessStreamMetaDataService.findAll();
        this.businessStreamGrid.setItems(businessStreamMetaData);
    }

    /**
     * Create module graph
     *
     * @param moduleMetaData
     */
    protected void createGraph(ModuleMetaData moduleMetaData)
    {
        List<String> configurationIds = moduleMetaData.getFlows().stream()
            .map(flowMetaData -> flowMetaData.getFlowElements()).flatMap(List::stream)
            .map(flowElementMetaData -> flowElementMetaData.getConfigurationId())
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());

        List<ConfigurationMetaData> configurationMetaData
            = this.configurationMetadataService.findByIdList(configurationIds);

        ModuleVisjsAdapter adapter = new ModuleVisjsAdapter();
        Module module = adapter.adapt(moduleMetaData, configurationMetaData);

        this.remove(moduleVisualisation);
        this.remove(networkDiagram);

        this.currentModule = module;
        this.currentFlow = module.getFlows().get(0);

        this.fireModuleFlowChangeEvent();

        this.moduleVisualisation = new ModuleVisualisation(this.moduleControlRestService,
            this.configurationRestService,
            this.triggerRestService);
        moduleVisualisation.addModule(module);
        moduleVisualisation.setCurrentFlow(module.getFlows().get(0));
        moduleVisualisation.redraw();
        this.flowComboBox.setCurrentModule(module);
        this.add(moduleVisualisation);
    }

    /**
     *
     * @param json
     */
    protected void createBusinessStreamGraphGraph(String json) throws IOException
    {
        BusinessStreamVisjsAdapter adapter = new BusinessStreamVisjsAdapter();

        this.graph = adapter.toBusinessStreamGraph(json);

        nodes = new ArrayList<>();
        nodes.addAll(graph.getFlows());
        nodes.addAll(graph.getDestinations());
        nodes.addAll(graph.getIntegratedSystems());

        this.remove(networkDiagram);
        this.remove(this.moduleVisualisation);
        updateNetworkDiagram(nodes, graph.getEdges());
        this.add(networkDiagram);
    }

    /**
     * Method to create the tool slider.
     */
    protected void createToolsSlider()
    {
        PagedTabs tabs = new PagedTabs();
        tabs.setSizeFull();

        VerticalLayout modulesLayout = new VerticalLayout();
        modulesLayout.setSizeFull();
        modulesLayout.add(this.modulesGrid);


        VerticalLayout businessStreamLayout = new VerticalLayout();
        businessStreamLayout.setSizeFull();

        uploadBusinssStreamButton = new Button(VaadinIcon.UPLOAD.create());
        uploadBusinssStreamButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            BusinessStreamUploadDialog uploadDialog = new  BusinessStreamUploadDialog(this.businessStreamMetaDataService);
            uploadDialog.open();

            uploadDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>)
                dialogOpenedChangeEvent -> populateBusinessStreamGrid());
        });

        uploadBusinssStreamButtonTooltip = TooltipHelper.getTooltipForComponentBottom(uploadBusinssStreamButton, getTranslation("tooltip.upload-business-stream", UI.getCurrent().getLocale()));

        businessStreamLayout.add(uploadBusinssStreamButtonTooltip, uploadBusinssStreamButton, this.businessStreamGrid);
        businessStreamLayout.setHorizontalComponentAlignment(Alignment.END, uploadBusinssStreamButton);

        businessStreamLayout.addAttachListener((ComponentEventListener<AttachEvent>) attachEvent ->
        {
            if(uploadBusinssStreamButtonTooltip != null && this.uploadBusinssStreamButton != null)
            {
                this.uploadBusinssStreamButtonTooltip.attachToComponent(this.uploadBusinssStreamButton);
            }
        });

        tabs.add((SerializableSupplier<com.vaadin.flow.component.Component>) () -> businessStreamLayout, "Business Streams");
        tabs.add((SerializableSupplier<com.vaadin.flow.component.Component>) () -> modulesLayout, "Modules");


        Image transparent = new Image("frontend/images/transparent.png", "");
        transparent.setHeight("60px");

        Div card = new Div();
        card.setSizeFull();
        card.setWidth("360px");
        card.setHeight("100%");
        card.getStyle().set("background", "white");
        card.add(transparent, tabs);


        toolSlider = new SlideTabBuilder(card)
            .expanded(false)
            .mode(SlideMode.RIGHT)
            .caption("Tools")
            .tabPosition(SlideTabPosition.MIDDLE)
            .fixedContentSize(360)
            .zIndex(1)
            .flowInContent(true)
            .build();

        super.add(toolSlider);
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
//        Set<String> moduleNames = new HashSet<>();
//        Set<String> flowNames = new HashSet<>();
//
//        boolean userDotSeperator = false;
//        for(Node node: this.graph.getFlows())
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
//
//        final PagedSearchResult<WiretapEvent> results =  this.solrWiretapService.findWiretapEvents(0, 500, "", false, moduleNames, flowNames,
//            null, null, null, startDate, endDate, searchTerm);
//
//        logger.info("Found:" + results.getResultSize());
//
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
//            Node node = userDotSeperator ? nodeMap.get(result.getModuleName() + "." + result.getFlowName()) : nodeMap.get(result.getModuleName());
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
//
//        current.access(() ->
//            networkDiagram.updateNodesStates(nodes));
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

    protected void fireModuleFlowChangeEvent()
    {
        GraphViewChangeEvent graphViewChangeEvent = new GraphViewChangeEvent(this.currentModule, this.currentFlow);

        for(GraphViewChangeListener graphViewChangeListener: this.graphViewChangeListeners)
        {
            graphViewChangeListener.onChange(graphViewChangeEvent);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        UI ui = attachEvent.getUI();
        this.controlPanel = new ControlPanel(this.moduleControlRestService);

        if(!initialised)
        {
            this.init();
            initialised = true;
        }

        broadcasterRegistration = FlowStateBroadcaster.register(flowState ->
        {
            ui.access(() ->
            {
                // do something interesting here.
               logger.info("Received flow state: " + flowState);
            });
        });

    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }
}

