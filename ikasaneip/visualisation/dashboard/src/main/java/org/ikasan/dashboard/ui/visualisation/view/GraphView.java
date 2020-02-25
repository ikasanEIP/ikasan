package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.grid.HeaderRow;
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
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.component.ErrorListDialog;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.component.WiretapListDialog;
import org.ikasan.dashboard.ui.general.component.TableButton;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.component.*;
import org.ikasan.dashboard.ui.visualisation.component.filter.BusinessStreamSearchFilter;
import org.ikasan.dashboard.ui.visualisation.component.filter.ModuleSearchFilter;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeListener;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrGeneralService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.vaadin.visjs.network.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.erik.SlideMode;
import org.vaadin.erik.SlideTab;
import org.vaadin.erik.SlideTabBuilder;
import org.vaadin.erik.SlideTabPosition;
import org.vaadin.tabs.PagedTabs;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

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

//    private EventViewDialog eventViewDialog = new EventViewDialog();

    private BusinessStream graph = null;
    private List<Node> nodes = new ArrayList<>();
    private VaadinSession session;
    private UI current;
    private ModuleFilteringGrid modulesGrid;
    private BusinessStreamFilteringGrid businessStreamGrid;
    private Button viewListButton;
    private RadioButtonGroup<String> group = new RadioButtonGroup<>();
//    private List<WiretapEvent> wiretapSearchResults;
//    private List<ErrorOccurrence> errorOccurrences;
    private BusinessStreamVisualisation businessStreamVisualisation;
    private ModuleVisualisation moduleVisualisation;
    private H2 moduleLabel = new H2();
    private HorizontalLayout moduleViewHeaderLayout = new HorizontalLayout();
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
        this.createModuleGrid();
        this.createdBusinessStreamGrid();

        this.createModuleViewHeader();
        this.createToolsSlider();
        this.createSearchSlider();
    }

    protected void createModuleGrid()
    {
        // Create a modulesGrid bound to the list
        ModuleSearchFilter moduleSearchFilter = new ModuleSearchFilter();
        modulesGrid = new ModuleFilteringGrid(this.moduleMetadataService, moduleSearchFilter);
        modulesGrid.removeAllColumns();
        modulesGrid.setVisible(true);
        modulesGrid.setHeight("800px");
        modulesGrid.setWidth("100%");

        modulesGrid.addColumn(ModuleMetaData::getName).setHeader("Name").setKey("name");

        modulesGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<ModuleMetaData>>)
            doubleClickEvent ->
            {
                this.moduleLabel.setText(doubleClickEvent.getItem().getName());
                this.moduleViewHeaderLayout.setVisible(true);
                this.flowComboBox.setVisible(true);
                this.controlPanel.setVisible(true);
                this.statusPanel.setVisible(true);
                createModuleVisualisation(doubleClickEvent.getItem());

                if(this.toolSlider.isExpanded())
                {
                    this.toolSlider.collapse();
                }
            });

        HeaderRow hr = this.modulesGrid.appendHeaderRow();
        this.modulesGrid.addGridFiltering(hr, moduleSearchFilter::setModuleNameFilter, "name");
    }

    protected void createdBusinessStreamGrid()
    {
        // Create a modulesGrid bound to the list
        BusinessStreamSearchFilter businessStreamSearchFilter = new BusinessStreamSearchFilter();
        this.businessStreamGrid = new BusinessStreamFilteringGrid(businessStreamMetaDataService,
            businessStreamSearchFilter);
        businessStreamGrid.removeAllColumns();
        businessStreamGrid.setVisible(true);
        businessStreamGrid.setHeight("800px");
        businessStreamGrid.setWidth("100%");

        businessStreamGrid.addColumn(BusinessStreamMetaData::getName).setHeader("Name").setKey("name").setFlexGrow(8);
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
                this.createBusinessStreamGraph(doubleClickEvent.getItem().getJson());

                this.moduleLabel.setText(doubleClickEvent.getItem().getName());
                this.moduleViewHeaderLayout.setVisible(true);
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

        HeaderRow hr = this.businessStreamGrid.appendHeaderRow();
        this.businessStreamGrid.addGridFiltering(hr, businessStreamSearchFilter::setBusinessStreamNameFilter, "name");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.controlPanel = new ControlPanel(this.moduleControlRestService);

        if(!initialised)
        {
            this.init();
            initialised = true;
        }

        this.populateModulesGrid();
        this.populateBusinessStreamGrid();

        if(uploadBusinssStreamButtonTooltip != null && this.uploadBusinssStreamButton != null)
        {
            this.uploadBusinssStreamButtonTooltip.attachToComponent(this.uploadBusinssStreamButton);
        }
    }

    protected void createModuleViewHeader()
    {
        this.createFlowCombo();

        HorizontalLayout moduleNameLayout = new HorizontalLayout();
        moduleNameLayout.setMargin(false);
        moduleNameLayout.setSpacing(false);
        moduleNameLayout.add(moduleLabel);

        HorizontalLayout comboBoxLayout = new HorizontalLayout();
        comboBoxLayout.setMargin(false);
        comboBoxLayout.setSpacing(false);
        comboBoxLayout.add(flowComboBox);

        moduleViewHeaderLayout.setWidth("100%");
        moduleViewHeaderLayout.setMargin(false);

        statusPanel = new StatusPanel(this.moduleControlRestService);

        moduleViewHeaderLayout.setFlexGrow(1, moduleNameLayout);
        moduleViewHeaderLayout.setFlexGrow(1, statusPanel);
        moduleViewHeaderLayout.setFlexGrow(5, comboBoxLayout);
        moduleViewHeaderLayout.setFlexGrow(3, controlPanel);

        moduleViewHeaderLayout.add(moduleNameLayout, statusPanel, comboBoxLayout, controlPanel);
        moduleViewHeaderLayout.setVerticalComponentAlignment(Alignment.BASELINE, moduleNameLayout, statusPanel, comboBoxLayout, controlPanel);

        if(this.currentModule == null)
        {
            moduleViewHeaderLayout.setVisible(false);
        }

        moduleVisualisation = new ModuleVisualisation(this.moduleControlRestService,
            this.configurationRestService,
            this.triggerRestService);

        this.add(moduleViewHeaderLayout);
//        this.add(moduleVisualisation);

        this.graphViewChangeListeners.add(statusPanel);
        this.graphViewChangeListeners.add(controlPanel);
    }

    private void createFlowCombo() {
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
    }

    /**
     * Method to initialise the modulesGrid on the tools slider.
     */
    protected void populateModulesGrid()
    {
        this.modulesGrid.init();
    }

    /**
     * Method to initialise the modulesGrid on the tools slider.
     */
    protected void populateBusinessStreamGrid()
    {
        this.businessStreamGrid.init();
    }

    /**
     * Create module graph
     *
     * @param moduleMetaData
     */
    protected void createModuleVisualisation(ModuleMetaData moduleMetaData)
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

        if(this.moduleVisualisation != null){
            this.remove(moduleVisualisation);
        }

        if (this.businessStreamVisualisation != null)
        {
            this.remove(businessStreamVisualisation);
        }

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
    protected void createBusinessStreamGraph(String json) throws IOException {

        if (this.businessStreamVisualisation != null) {
            this.remove(businessStreamVisualisation);
        }

        if(this.moduleVisualisation != null){
            this.remove(moduleVisualisation);
        }

        businessStreamVisualisation = new BusinessStreamVisualisation(this.moduleControlRestService,
            this.configurationRestService, this.triggerRestService, this.solrWiretapService, this.moduleMetadataService
            , this.configurationMetadataService, this.viewListButton, this.solrSearchService);

        businessStreamVisualisation.createBusinessStreamGraphGraph(json);

//        businessStreamVisualisation.redraw();
        this.add(businessStreamVisualisation);
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
//        searchButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> search(searchText.getValue(),
//            Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
//            Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())));

        searchLayout.add(searchButton);

        this.viewListButton = new Button("Result List");
        this.viewListButton.setVisible(false);
        this.viewListButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
//            if(this.group.getValue().equals("Wiretap"))
//            {
//                WiretapListDialog dialog = new WiretapListDialog(this.wiretapSearchResults);
//                dialog.open();
//            }
//            else if(this.group.getValue().equals("Error"))
//            {
//                ErrorListDialog dialog = new ErrorListDialog(this.errorOccurrences, this.solrErrorReportingService);
//                dialog.open();
//            }
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

//    /**
//     * Method to perform the search.
//     *
//     * @param searchTerm The search term we are seeding the search with.
//     * @param startDate The start date range.
//     * @param endDate The end date range.
//     */
//    protected void search(String searchTerm, Date startDate, Date endDate)
//    {
//        if(this.businessStreamVisualisation == null)
//        {
//            NotificationHelper.showUserNotification("The Ikasan Visualisation appears to be empty!");
//            return;
//        }
//
//        if(searchTerm == null || searchTerm.isEmpty())
//        {
//            NotificationHelper.showUserNotification("A search term must be entered!");
//            return;
//        }
//
//        if(group.getValue().equals("Wiretap"))
//        {
//            this.performWiretapSearch(searchTerm, startDate, endDate);
//        }
//        else if(group.getValue().equals("Error"))
//        {
//            this.performErrorSearch(searchTerm, startDate, endDate);
//        }
//    }
//
//    protected void performWiretapSearch(String searchTerm, Date startDate, Date endDate)
//    {
//        if(this.businessStreamVisualisation != null)
//        {
//            this.businessStreamVisualisation.performWiretapSearch(searchTerm, startDate, endDate);
//        }
//    }
//
//    protected void performErrorSearch(String searchTerm, Date startDate, Date endDate)
//    {
//        ArrayList<String> moduleNames = new ArrayList<>();
//        ArrayList<String> flowNames = new ArrayList<>();
//
//        for(Node node: this.graph.getFlows())
//        {
//            if (node.getId().contains("."))
//            {
//                String[] moduleFlowPair = node.getId().split(Pattern.quote("."));
//                moduleNames.add(moduleFlowPair[0]);
//                flowNames.add(moduleFlowPair[1]);
//            }
//            else
//            {
//                moduleNames.add(node.getId());
//            }
//        }
//
//        errorOccurrences = this.solrErrorReportingService.find(moduleNames, flowNames, null, startDate, endDate, 500);
//
//        logger.info("Found errors:" + errorOccurrences.size());
//
//        if(errorOccurrences.size() > 0)
//        {
//            this.viewListButton.setVisible(true);
//        }
//        else
//        {
//            this.viewListButton.setVisible(false);
//        }
//
////        HashMap<String, Node> nodeMap = new HashMap<>();
////
////        for(Node node: nodes)
////        {
////            node.setFoundStatus(NodeFoundStatus.NOT_FOUND);
////            nodeMap.put(node.getId(), node);
////        }
////
////        HashSet<String> correlationValues = new HashSet<>();
////        HashMap<String, WiretapEvent<String>> uniqueResults = new HashMap<>();
////        for(WiretapEvent<String> result: results.getPagedResults())
////        {
////            Node node = nodeMap.get(result.getModuleName() + "." + result.getFlowName());
////
////            if(node != null)
////            {
////                node.setFoundStatus(NodeFoundStatus.FOUND);
////                ((Flow)node).setWireapEvent(result.getEvent());
////                uniqueResults.put(result.getEvent(), result);
////
////                if(((Flow)node).getCorrelator() != null)
////                {
////                    String correlationValue = (String)((Flow)node).getCorrelator().correlate(result.getEvent());
////
////                    correlationValues.add(correlationValue);
////                    logger.info("Correlation value = " + correlationValue);
////                }
////            }
////        }
////
////        logger.info("Number of unique correlations values = " + correlationValues.size());
////
////        for(String value: correlationValues)
////        {
////            PagedSearchResult<WiretapEvent> secondResults =  this.solrWiretapService.findWiretapEvents(0, 500, "timestamp", false, moduleNames, flowNames,
////                null, null, null, startDate, endDate, value);
////
////            logger.info("Found correlating:" + secondResults.getResultSize());
////
////            for(WiretapEvent<String> result: secondResults.getPagedResults())
////            {
////                Node node = nodeMap.get(result.getModuleName() + "." + result.getFlowName());
////
////                if(node != null)
////                {
////                    node.setFoundStatus(NodeFoundStatus.FOUND);
////                    ((Flow)node).setWireapEvent(result.getEvent());
////                    uniqueResults.put(result.getEvent(), result);
////                }
////            }
////        }
////
////        logger.info("Number of unique events = " + uniqueResults.size());
////
////        if(uniqueResults.size() > 0)
////        {
////            this.viewListButton.setVisible(true);
////
////            this.viewListButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
////            {
////                final WiretapListDialog component = new WiretapListDialog(results.getPagedResults());
////                component.open();
////
////                component.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
////                {
////                    if(dialogOpenedChangeEvent.isOpened() == false)
////                    {
////                        component.removeAll();
////                    }
////                });
////            });
////        }
////        else
////        {
////            this.viewListButton.setVisible(false);
////        }
////
////        current.access(() ->
////            networkDiagram.updateNodesStates(nodes));
//    }

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

