package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.ui.general.component.*;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.search.component.ChangePasswordDialog;
import org.ikasan.dashboard.ui.search.component.SearchForm;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.component.filter.SearchFilter;
import org.ikasan.dashboard.ui.search.listener.IgnoreHospitalEventSubmissionListener;
import org.ikasan.dashboard.ui.search.listener.ReplayEventSubmissionListener;
import org.ikasan.dashboard.ui.search.listener.ResubmitHospitalEventSubmissionListener;
import org.ikasan.dashboard.ui.search.listener.SearchListener;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamFilteringGrid;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamUploadDialog;
import org.ikasan.dashboard.ui.visualisation.component.ModuleFilteringGrid;
import org.ikasan.dashboard.ui.visualisation.component.filter.BusinessStreamSearchFilter;
import org.ikasan.dashboard.ui.visualisation.component.filter.ModuleSearchFilter;
import org.ikasan.rest.client.*;
import org.ikasan.security.model.User;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrGeneralService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Route(value = "visualisation", layout = IkasanAppLayout.class)
@UIScope
@PageTitle("Ikasan - Visualisation")
@Component
public class GraphView extends VerticalLayout implements BeforeEnterObserver, SearchListener
{
    Logger logger = LoggerFactory.getLogger(GraphView.class);

    public static final String ALL = "All";
    public static final String WIRETAP = "Wiretap";
    public static final String ERROR = "Error";
    public static final String EXCLUSION = "Exclusion";
    public static final String REPLAY = "Replay";

    @Resource
    private ErrorReportingService errorReportingService;

    @Resource
    private HospitalAuditService hospitalAuditService;

    @Resource
    private ResubmissionRestServiceImpl resubmissionRestService;

    @Resource
    private ReplayRestServiceImpl replayRestService;

    @Resource
    private BatchInsert replayAuditService;

    @Resource
    private WiretapService<FlowEvent,PagedSearchResult<WiretapEvent>, String> solrWiretapService;

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

    @Resource
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService;


    private ModuleFilteringGrid modulesGrid;
    private BusinessStreamFilteringGrid businessStreamGrid;
    private Button viewListButton;
    private RadioButtonGroup<String> group = new RadioButtonGroup<>();
    private GraphViewBusinessStreamVisualisation businessStreamVisualisation;
    private GraphViewModuleVisualisation moduleVisualisation;
    private H2 moduleLabel = new H2();

    private Registration broadcasterRegistration;

    private boolean initialised = false;

    private SlideTab toolSlider;
    private SlideTab searchSlider;
    private Button uploadBusinssStreamButton;
    private Tooltip uploadBusinssStreamButtonTooltip;

    private SearchForm searchForm;

    private HashMap<String, Checkbox> selectionBoxes = new HashMap<>();
    private HashMap<String, IkasanSolrDocument> selectionItems = new HashMap<>();
    private Boolean selected = Boolean.FALSE;

    private SolrSearchFilteringGrid searchResultsGrid;
    private WiretapDialog wiretapDialog = new WiretapDialog();
    private ErrorDialog errorDialog = new ErrorDialog();
    private ReplayDialog replayDialog;
    private HospitalDialog exclusionDialog;

    private HorizontalLayout buttonLayout = new HorizontalLayout();

    private Label resultsLabel = new Label();
    private Button selectAllButton;
    private Button replayButton;
    private Button resubmitButton;
    private Button ignoreButton;

    private Button searchButton;
    private Tooltip allButtonTooltip;
    private Image wiretapImage;
    private Tooltip wiretapButtonTooltip;
    private Image hospitalImage;
    private Tooltip hospitalButtonTooltip;
    private Image errorImage;
    private Tooltip errorButtonTooltip;
    private Image replayImage;
    private Tooltip replaySearchButtonTooltip;
    private Tooltip replayButtonTooltip;
    private Tooltip selectAllTooltip;
    private Tooltip resubmitButtonTooltip;
    private Tooltip ignoreButtonTooltip;

    private Registration replayEventRegistration;
    private ReplayEventSubmissionListener replayEventSubmissionListener;

    private Registration resubmitHospitalEventRegistration;
    private ResubmitHospitalEventSubmissionListener resubmitHospitalEventSubmissionListener;

    private Registration ignoreHospitalEventRegistration;
    private IgnoreHospitalEventSubmissionListener ignoreHospitalEventSubmissionListener;

    private String currentSearchType = "";

    /**
     * Constructor
     */
    public GraphView()
    {
        this.setMargin(true);
        this.setSizeFull();
    }

    private void init()
    {
        this.createModuleGrid();
        this.createdBusinessStreamGrid();
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
                this.createBusinessStreamGraph(doubleClickEvent.getItem().getName(), doubleClickEvent.getItem());
                this.moduleLabel.setText(doubleClickEvent.getItem().getName());
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

    /**
     * Create the results grid layout.
     */
    protected VerticalLayout createSearchResultGridLayout()
    {
        createSearchResultsGrid();

        this.resultsLabel.setVisible(false);

        Image selectAllImage = new Image("/frontend/images/all-small-off-icon.png", "");
        selectAllImage.setHeight("30px");
        selectAllButton = new Button(selectAllImage);
        Image replayImage = new Image("/frontend/images/replay-service.png", "");
        replayImage.setHeight("30px");
        replayButton = new Button(replayImage);
        Image resubmitImage = new Image("/frontend/images/resubmit-icon.png", "");
        resubmitImage.setHeight("30px");
        resubmitButton = new Button(resubmitImage);
        Image ignoreImage = new Image("/frontend/images/ignore-icon.png", "");
        ignoreImage.setHeight("30px");
        ignoreButton = new Button(ignoreImage);

        selectAllTooltip = TooltipHelper.getTooltipForComponentBottom(selectAllButton, getTranslation("tooltip.select-all", UI.getCurrent().getLocale()));
        resubmitButtonTooltip = TooltipHelper.getTooltipForComponentBottom(resubmitButton, getTranslation("tooltip.bulk-resubmit", UI.getCurrent().getLocale()));
        ignoreButtonTooltip = TooltipHelper.getTooltipForComponentBottom(ignoreButton, getTranslation("tooltip.bulk-ignore", UI.getCurrent().getLocale()));
        replayButtonTooltip = TooltipHelper.getTooltipForComponentBottom(replayButton, getTranslation("tooltip.bulk-replay", UI.getCurrent().getLocale()));

        selectAllButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> toggleSelected());

        buttonLayout.setWidth("70px");

        HorizontalLayout buttonLayoutWrapper = new HorizontalLayout();
        buttonLayoutWrapper.setWidthFull();
        buttonLayoutWrapper.add(this.resultsLabel, buttonLayout);
        buttonLayoutWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayoutWrapper.setVerticalComponentAlignment(FlexComponent.Alignment.END, buttonLayout);

        HorizontalLayout resultsLayout = new HorizontalLayout();
        resultsLayout.setWidthFull();
        resultsLayout.add(resultsLabel);

        HorizontalLayout controlLayout = new HorizontalLayout();
        controlLayout.setWidthFull();
        controlLayout.add(resultsLayout, buttonLayoutWrapper);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("60vh");
        layout.setSpacing(false);
        layout.setMargin(false);
        layout.add(controlLayout, searchResultsGrid);
        layout.getStyle().set("background", "white");
        layout.getStyle().set("color", "black");

        return layout;
    }

    /**
     * Create the grid that the search results appear.
     */
    private void createSearchResultsGrid()
    {
        SearchFilter searchFilter = new SearchFilter();

        this.searchResultsGrid = new SolrSearchFilteringGrid(this.solrGeneralService, searchFilter, this.resultsLabel);

        // Add the icon column to the grid
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

            if(ikasanSolrDocument.getType().equalsIgnoreCase(WIRETAP))
            {
                Image wiretapImage = new Image("frontend/images/wiretap-service.png", "");
                wiretapImage.setHeight("30px");
                horizontalLayout.add(wiretapImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(ERROR))
            {
                Image errorImage = new Image("frontend/images/error-service.png", "");
                errorImage.setHeight("30px");
                horizontalLayout.add(errorImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(EXCLUSION))
            {
                Image hospitalImage = new Image("frontend/images/hospital-service.png", "");
                hospitalImage.setHeight("30px");
                horizontalLayout.add(hospitalImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(REPLAY))
            {
                Image replayImage = new Image("frontend/images/replay-service.png", "");
                replayImage.setHeight("30px");
                horizontalLayout.add(replayImage);
            }

            return horizontalLayout;
        })).setWidth("40px");

        // Add the module name column to the grid
        this.searchResultsGrid.addColumn(IkasanSolrDocument::getModuleName)
            .setKey("moduleName")
            .setHeader(getTranslation("table-header.module-name", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(4);

        // Add the flow name column to the grid
        this.searchResultsGrid.addColumn(IkasanSolrDocument::getFlowName).setKey("flowName")
            .setHeader(getTranslation("table-header.flow-name", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(6);

        // Add the component name column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.componentName]]</div>")
            .withProperty("componentName",
                ikasanSolrDocument -> Optional.ofNullable(ikasanSolrDocument.getComponentName()).orElse(getTranslation("label.not-applicable", UI.getCurrent().getLocale()))))
            .setKey("componentName")
            .setHeader(getTranslation("table-header.component-name", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(6);

        // Add the event identifier column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.eventIdentifier]]</div>")
            .withProperty("eventIdentifier",
                ikasanSolrDocument -> ikasanSolrDocument.getEventId()))
            .setKey("event")
            .setHeader(getTranslation("table-header.event-id", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(8);

        // Add the event details column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.event]]</div>")
            .withProperty("event",
                ikasanSolrDocument -> {
                    if(ikasanSolrDocument.getType().equals("error"))
                    {
                        if (ikasanSolrDocument.getErrorMessage() == null)
                        {
                            return "";
                        } else
                        {
                            int endIndex = ikasanSolrDocument.getErrorMessage().length() > 200 ? 200 : ikasanSolrDocument.getErrorMessage().length();
                            return ikasanSolrDocument.getErrorMessage().substring(0, endIndex);
                        }
                    }
                    else
                    {
                        if (ikasanSolrDocument.getEvent() == null)
                        {
                            return "";
                        } else
                        {
                            int endIndex = ikasanSolrDocument.getEvent().length() > 200 ? 200 : ikasanSolrDocument.getEvent().length();
                            return ikasanSolrDocument.getEvent().substring(0, endIndex);
                        }
                    }
                }))
            .setKey("payload")
            .setHeader(getTranslation("table-header.event-details", UI.getCurrent().getLocale()))
            // cannot sort on text_general data type
            .setSortable(false)
            .setFlexGrow(12);

        // Add the timestamp column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.date]]</div>")
            .withProperty("date",
                ikasanSolrDocument -> DateFormatter.getFormattedDate(ikasanSolrDocument.getTimeStamp())))
            .setHeader(getTranslation("table-header.timestamp", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setKey("timestamp")
            .setFlexGrow(2);

        // Add the select column to the grid
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();

            Checkbox checkbox = new Checkbox();
            checkbox.setId(ikasanSolrDocument.getId());
            horizontalLayout.add(checkbox);

            checkbox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>>) checkboxBooleanComponentValueChangeEvent ->
            {
                if(checkboxBooleanComponentValueChangeEvent.getValue())
                {
                    this.selectionItems.put(ikasanSolrDocument.getId(), ikasanSolrDocument);
                }
                else
                {
                    this.selectionItems.remove(ikasanSolrDocument.getId());
                }
            });

            if(!this.selectionBoxes.containsKey(ikasanSolrDocument.getId()))
            {
                checkbox.setValue(selected);
                this.selectionBoxes.put(ikasanSolrDocument.getId(), checkbox);
            }
            else
            {
                checkbox.setValue(selectionBoxes.get(ikasanSolrDocument.getId()).getValue());
                this.selectionItems.put(ikasanSolrDocument.getId(), ikasanSolrDocument);
                this.selectionBoxes.put(ikasanSolrDocument.getId(), checkbox);
            }

            return horizontalLayout;
        })).setWidth("20px");

        // Add the double click replayEventSubmissionListener to the grid so that the relevant dialog can be opened.
        this.searchResultsGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<IkasanSolrDocument>>)
            ikasanSolrDocumentItemDoubleClickEvent ->
            {
                if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(WIRETAP))
                {
                    wiretapDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(ERROR))
                {
                    errorDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(REPLAY))
                {
                    replayDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(EXCLUSION))
                {
                    exclusionDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
            });

        exclusionDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
        {
            if (!dialogOpenedChangeEvent.isOpened())
            {
                searchResultsGrid.getDataProvider().refreshAll();
            }
        });

        // Add filtering to the relevant columns.
        HeaderRow hr = searchResultsGrid.appendHeaderRow();
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setModuleNameFilter, "moduleName");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setFlowNameFilter, "flowName");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setComponentNameFilter, "componentName");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setEventIdFilter, "event");

        this.searchResultsGrid.setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        if(!initialised)
        {
            this.exclusionDialog = new HospitalDialog(this.errorReportingService, this.hospitalAuditService
                , this.resubmissionRestService, this.moduleMetadataService);

            this.replayDialog = new ReplayDialog(this.replayRestService, this.replayAuditService);

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
        if (this.businessStreamVisualisation != null)
        {
            this.remove(businessStreamVisualisation);
        }

        if(this.moduleVisualisation != null)
        {
            this.remove(moduleVisualisation);
        }

        this.moduleVisualisation = new GraphViewModuleVisualisation(this.moduleControlRestService,
            this.configurationRestService,
            this.triggerRestService, this.configurationMetadataService);
        this.moduleVisualisation.createModuleVisualisation(moduleMetaData);
        this.add(moduleVisualisation);
    }

    protected void createBusinessStreamGraph(String name, BusinessStreamMetaData businessStreamMetaData) throws IOException {

        if (this.businessStreamVisualisation != null) {
            this.remove(businessStreamVisualisation);
        }

        if(this.moduleVisualisation != null){
            this.remove(moduleVisualisation);
        }

        businessStreamVisualisation = new GraphViewBusinessStreamVisualisation(this.solrSearchService,
            this.moduleControlRestService, this.moduleMetadataService, this.configurationRestService
            , this.triggerRestService, this.configurationMetadataService);

        businessStreamVisualisation.createBusinessStreamGraph(name, businessStreamMetaData);

        this.add(businessStreamVisualisation);
        this.searchForm.addSearchListener(businessStreamVisualisation);
        this.searchForm.addSearchListener(this);
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

        uploadBusinssStreamButtonTooltip = TooltipHelper.getTooltipForComponentBottom(uploadBusinssStreamButton
            , getTranslation("tooltip.upload-business-stream", UI.getCurrent().getLocale()));

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
        transparent.setWidth("350px");

        Div card = new Div();
        card.setSizeFull();
        card.setWidth("370px");
        card.setHeight("100%");
        card.getStyle().set("background", "white");
        card.getStyle().set("position" , "absolute");
        card.getStyle().set("right" , "0px");
        card.add(transparent, tabs);



        toolSlider = new SlideTabBuilder(card)
            .expanded(false)
            .mode(SlideMode.RIGHT)
            .caption("Tools")
            .tabPosition(SlideTabPosition.MIDDLE)
            .fixedContentSize(397)
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
        searchForm = new SearchForm();
        searchForm.addSearchListener(this);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.setMargin(false);
        layout.setSpacing(false);

        Div orangeDiv = new Div();
        orangeDiv.setWidth("100%");
        orangeDiv.setHeight("5px");

        Div searchDiv = new Div();
        searchDiv.setWidth("100%");
        searchDiv.setHeight("250px");
        searchDiv.getStyle().set("background", "white");
        searchDiv.getStyle().set("color", "black");
        searchDiv.add(searchForm);

        layout.add(orangeDiv, searchDiv, this.createSearchResultGridLayout());

        searchSlider = new SlideTabBuilder(layout)
            .expanded(false)
            .mode(SlideMode.BOTTOM)
            .caption("Search")
            .tabPosition(SlideTabPosition.MIDDLE)
            .zIndex(1)
            .flowInContent(true)
            .build();

        super.add(searchSlider);
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

    @Override
    public void search(String searchTerm, List<String> entityTypes, boolean negateQuery, long startDate, long endDate) {
        if(this.searchSlider.isExpanded())
        {
            this.searchSlider.collapse();
        }
    }

    /**
     * Helper method to toggle the selected check box.
     */
    private void toggleSelected()
    {
        if(this.selected)
        {
            selectionBoxes.keySet().forEach(key -> selectionBoxes.get(key).setValue(false));

            Image selectedImage = new Image("/frontend/images/all-small-off-icon.png", "");
            selectedImage.setHeight("30px");

            this.selectAllButton = new Button(selectedImage);
            this.selectionItems.clear();

            this.selected = Boolean.FALSE;
            this.replayEventSubmissionListener.setSelected(Boolean.FALSE);
            this.resubmitHospitalEventSubmissionListener.setSelected(Boolean.FALSE);
            this.ignoreHospitalEventSubmissionListener.setSelected(Boolean.FALSE);
        }
        else
        {
            this.selectionBoxes.keySet().forEach(key -> selectionBoxes.get(key).setValue(true));
            Image deSelectedImage = new Image("/frontend/images/all-small-on-icon.png", "");
            deSelectedImage.setHeight("30px");

            this.selectAllButton = new Button(deSelectedImage);

            this.selected = Boolean.TRUE;
            this.replayEventSubmissionListener.setSelected(Boolean.TRUE);
            this.resubmitHospitalEventSubmissionListener.setSelected(Boolean.TRUE);
            this.ignoreHospitalEventSubmissionListener.setSelected(Boolean.TRUE);
        }

        selectAllButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> toggleSelected());
        functionalGroupSetup();
    }

    /**
     * Set up the functional group.
     */
    private void functionalGroupSetup()
    {
        buttonLayout.removeAll();

        if(this.currentSearchType.equals(REPLAY))
        {
            buttonLayout.add(replayButton, replayButtonTooltip, selectAllButton, selectAllTooltip);

            ComponentSecurityVisibility.applySecurity(replayButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);
            ComponentSecurityVisibility.applySecurity(selectAllButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);

            buttonLayout.setWidth("80px");
        }
        else if(this.currentSearchType.equals(EXCLUSION))
        {
            buttonLayout.add(this.resubmitButton, resubmitButtonTooltip, this.ignoreButton, ignoreButtonTooltip, this.selectAllButton, selectAllTooltip);

            ComponentSecurityVisibility.applySecurity(resubmitButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);
            ComponentSecurityVisibility.applySecurity(ignoreButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);
            ComponentSecurityVisibility.applySecurity(selectAllButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);

            buttonLayout.setWidth("120px");
        }
    }
}

