package org.ikasan.dashboard.ui.visualisation.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.ui.general.component.*;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.search.component.SearchForm;
import org.ikasan.dashboard.ui.search.listener.SearchListener;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamFilteringGrid;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamUploadDialog;
import org.ikasan.dashboard.ui.visualisation.component.ModuleFilteringGrid;
import org.ikasan.dashboard.ui.visualisation.component.filter.BusinessStreamSearchFilter;
import org.ikasan.dashboard.ui.visualisation.component.filter.ModuleSearchFilter;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.module.client.ConfigurationService;
import org.ikasan.spec.module.client.MetaDataService;
import org.ikasan.spec.module.client.ModuleControlService;
import org.ikasan.spec.module.client.TriggerService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrGeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.erik.SlideMode;
import org.vaadin.erik.SlideTab;
import org.vaadin.erik.SlideTabBuilder;
import org.vaadin.erik.SlideTabPosition;
import org.vaadin.olli.FileDownloadWrapper;
import org.vaadin.tabs.PagedTabs;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "visualisation", layout = IkasanAppLayout.class)
@UIScope
@PageTitle("Ikasan - Visualisation")
@Component
public class GraphView extends VerticalLayout implements BeforeEnterObserver, SearchListener
{
    Logger logger = LoggerFactory.getLogger(GraphView.class);

    @Resource
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    @Resource
    private ErrorReportingService solrErrorReportingService;

    @Resource
    private ModuleControlService moduleControlRestService;

    @Autowired
    private ModuleMetaDataService moduleMetadataService;

    @Autowired
    private ConfigurationService configurationRestService;

    @Autowired
    private TriggerService triggerRestService;

    @Resource
    private ConfigurationMetaDataService configurationMetadataService;

    @Resource
    private BusinessStreamMetaDataService<BusinessStreamMetaData> businessStreamMetaDataService;

    @Resource
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService;

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
    private MetaDataService metaDataApplicationRestService;

    @Resource
    private BatchInsert<ModuleMetaData> moduleMetadataBatchInsert;

    private SearchResults searchResults;


    private ModuleFilteringGrid modulesGrid;
    private BusinessStreamFilteringGrid businessStreamGrid;
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

    /**
     * Constructor
     */
    public GraphView()
    {
        this.setMargin(false);

        this.setWidth("100%");
        this.setHeight("88vh");
    }

    private void init()
    {
        this.searchResults = new SearchResults(this.solrGeneralService, this.solrErrorReportingService,
            this.hospitalAuditService, this.resubmissionRestService, this.replayRestService,
            this.moduleMetadataService, this.replayAuditService);
        this.searchResults.setHeight("50vh");
        this.searchResults.setWidth("100%");

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
        modulesGrid.setHeight("80vh");
        modulesGrid.setWidth("100%");

        modulesGrid.addColumn(ModuleMetaData::getName)
            .setHeader(getTranslation("table-header.module-name", UI.getCurrent().getLocale())).setKey("name")
            .setFlexGrow(16);
        modulesGrid.addColumn(TemplateRenderer.<ModuleMetaData>of("<div style='white-space:normal'>[[item.description]]</div>")
            .withProperty("description", ModuleMetaData::getDescription))
            .setHeader(getTranslation("table-header.module-description", UI.getCurrent().getLocale()))
            .setKey("description")
            .setFlexGrow(32);
        modulesGrid.addColumn(ModuleMetaData::getVersion)
            .setHeader(getTranslation("table-header.module-version", UI.getCurrent().getLocale())).setKey("version")
            .setFlexGrow(8);
        modulesGrid.addColumn(new ComponentRenderer<>(moduleMetaData->
        {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] metaData = null;

            try {
                metaData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(moduleMetaData);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            Button downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
            byte[] finalMetaData = metaData;
            StreamResource streamResource = new StreamResource(moduleMetaData.getName().concat(".json")
                , () -> new ByteArrayInputStream(finalMetaData));

            FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(streamResource);
            buttonWrapper.wrapComponent(downloadButton);

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(buttonWrapper);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonWrapper);
            return layout;
        })).setWidth("30px");

        modulesGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<ModuleMetaData>>)
            doubleClickEvent ->
            {
                createModuleVisualisation(doubleClickEvent.getItem());

                if(this.toolSlider.isExpanded())
                {
                    this.toolSlider.collapse();
                }

                this.businessStreamVisualisation = null;
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
        businessStreamGrid.setHeight("80vh");
        businessStreamGrid.setWidth("100%");

        businessStreamGrid.addColumn(TemplateRenderer.<BusinessStreamMetaData>of("<div style='white-space:normal'>[[item.name]]</div>")
            .withProperty("name", BusinessStreamMetaData::getName))
            .setHeader(getTranslation("table-header.business-stream-name", UI.getCurrent().getLocale()))
            .setKey("name")
            .setFlexGrow(16);
        businessStreamGrid.addColumn(TemplateRenderer.<BusinessStreamMetaData>of("<div style='white-space:normal'>[[item.description]]</div>")
            .withProperty("description", BusinessStreamMetaData::getDescription)).setHeader(getTranslation("table-header.business-stream-description", UI.getCurrent().getLocale()))
            .setKey("description")
            .setFlexGrow(32);
        businessStreamGrid.addColumn(new ComponentRenderer<>(businessStreamMetaData->
        {
            Button editButton = new TableButton(VaadinIcon.EDIT.create());
            editButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                BusinessStreamUploadDialog uploadDialog = new  BusinessStreamUploadDialog(businessStreamMetaData, this.businessStreamMetaDataService);
                uploadDialog.open();

                uploadDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>)
                    dialogOpenedChangeEvent -> populateBusinessStreamGrid());
            });

            ComponentSecurityVisibility.applySecurity(editButton, SecurityConstants.PLATORM_CONFIGURATON_ADMIN,
                SecurityConstants.PLATORM_CONFIGURATON_WRITE, SecurityConstants.ALL_AUTHORITY);

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(editButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, editButton);
            return layout;
        })).setWidth("30px");
        businessStreamGrid.addColumn(new ComponentRenderer<>(businessStreamMetaData->
        {
            Button downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());
            StreamResource streamResource = new StreamResource(businessStreamMetaData.getName().concat(".json")
                , () -> new ByteArrayInputStream(businessStreamMetaData.getJson().getBytes()));

            FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(streamResource);
            buttonWrapper.wrapComponent(downloadButton);

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(buttonWrapper);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttonWrapper);
            return layout;
        })).setWidth("30px");
        businessStreamGrid.addColumn(new ComponentRenderer<>(businessStreamMetaData->
        {
            Button deleteButton = new TableButton(VaadinIcon.TRASH.create());
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                this.businessStreamMetaDataService.delete(businessStreamMetaData.getId());
                this.populateBusinessStreamGrid();
            });

            ComponentSecurityVisibility.applySecurity(deleteButton, SecurityConstants.PLATORM_CONFIGURATON_ADMIN,
                SecurityConstants.PLATORM_CONFIGURATON_WRITE, SecurityConstants.ALL_AUTHORITY);

            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.add(deleteButton);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, deleteButton);
            return layout;
        })).setWidth("30px");

        businessStreamGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<BusinessStreamMetaData>>) doubleClickEvent ->
        {
            try
            {
                this.createBusinessStreamGraph(doubleClickEvent.getItem().getName(), doubleClickEvent.getItem());
                this.moduleLabel.setText(doubleClickEvent.getItem().getName());
                this.moduleVisualisation = null;
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
            this.configurationRestService, this.triggerRestService, this.configurationMetadataService,
            this.metaDataApplicationRestService, this.moduleMetadataBatchInsert);

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
            , this.triggerRestService, this.configurationMetadataService, this.errorReportingService, this.hospitalAuditService,
            this.resubmissionRestService, this.replayRestService, this.replayAuditService, this.metaDataApplicationRestService,
            this.moduleMetadataBatchInsert);

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
        tabs.getElement().getThemeList().remove("padding");
        tabs.setSizeFull();

        VerticalLayout modulesLayout = new VerticalLayout();
        modulesLayout.getThemeList().remove("padding");
        modulesLayout.setSizeFull();
        modulesLayout.add(this.modulesGrid);


        VerticalLayout businessStreamLayout = new VerticalLayout();
        businessStreamLayout.getThemeList().remove("padding");
        businessStreamLayout.setSizeFull();

        uploadBusinssStreamButton = new Button(VaadinIcon.PLUS.create());
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
        transparent.setWidth("550px");

        Div card = new Div();
        card.setSizeFull();
        card.setWidth("670px");
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
            .fixedContentSize(697)
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

        VerticalLayout wrapperDiv = new VerticalLayout();
        wrapperDiv.getStyle().set("background", "white");
        wrapperDiv.getStyle().set("color", "black");
        wrapperDiv.setWidth("100%");
        wrapperDiv.setHeight("100%");
        wrapperDiv.setMargin(false);
        wrapperDiv.setSpacing(false);

        wrapperDiv.add(searchForm, this.searchResults);

        searchSlider = new SlideTabBuilder(wrapperDiv)
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

        if(this.businessStreamVisualisation == null && this.moduleVisualisation == null) {
            NotificationHelper.showUserNotification(getTranslation("notification.select-business-stream"
                , UI.getCurrent().getLocale()));
            return;
        }

        if(this.businessStreamVisualisation != null) {
            BusinessStream businessStream = this.businessStreamVisualisation.getBusinessStream();

            List<String> moduleNames = businessStream.getFlows()
                .stream()
                .map(flow -> flow.getModuleName())
                .collect(Collectors.toList());

            List<String> flowNames = businessStream.getFlows()
                .stream()
                .map(flow -> flow.getFlowName())
                .collect(Collectors.toList());

            this.searchResults.search(startDate, endDate, searchTerm, entityTypes, negateQuery, moduleNames, flowNames);
        }
        else {
            this.searchResults.search(startDate, endDate, searchTerm, entityTypes, negateQuery
                , List.of(this.moduleVisualisation.getModule().getName()), List.of(this.moduleVisualisation.getCurrentFlow().getName()));
        }
    }
}

