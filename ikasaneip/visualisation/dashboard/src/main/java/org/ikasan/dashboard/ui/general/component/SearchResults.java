package org.ikasan.dashboard.ui.general.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.ui.search.SearchConstants;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.component.filter.SearchFilter;
import org.ikasan.dashboard.ui.search.listener.IgnoreHospitalEventSubmissionListener;
import org.ikasan.dashboard.ui.search.listener.ReplayEventSubmissionListener;
import org.ikasan.dashboard.ui.search.listener.ResubmitHospitalEventSubmissionListener;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.module.client.ReplayService;
import org.ikasan.spec.module.client.ResubmissionService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrGeneralService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class SearchResults extends Div {
    private SolrSearchFilteringGrid searchResultsGrid;
    private Label resultsLabel = new Label();
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService;

    private HorizontalLayout buttonLayout = new HorizontalLayout();

    private Registration replayEventRegistration;
    private ReplayEventSubmissionListener replayEventSubmissionListener;

    private Registration resubmitHospitalEventRegistration;
    private ResubmitHospitalEventSubmissionListener resubmitHospitalEventSubmissionListener;

    private Registration ignoreHospitalEventRegistration;
    private IgnoreHospitalEventSubmissionListener ignoreHospitalEventSubmissionListener;

    private Button selectAllButton;
    private Button replayButton;
    private Button resubmitButton;
    private Button ignoreButton;
    private Tooltip selectAllTooltip;
    private Tooltip replayButtonTooltip;
    private Tooltip resubmitButtonTooltip;
    private Tooltip ignoreButtonTooltip;

    private HospitalAuditService hospitalAuditService;

    private ResubmissionService resubmissionRestService;

    private ReplayService replayRestService;

    private ModuleMetaDataService moduleMetadataService;

    private BatchInsert replayAuditService;

    private HashMap<String, Checkbox> selectionBoxes = new HashMap<>();
    private HashMap<String, IkasanSolrDocument> selectionItems = new HashMap<>();
    private Boolean selected = Boolean.FALSE;

    private String translatedEventActionMessage;

    private List<String> searchTypes;

    private SearchFilter searchFilter = new SearchFilter();

    public SearchResults(SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService,
                         HospitalAuditService hospitalAuditService, ResubmissionService resubmissionRestService,
                         ReplayService replayRestService, ModuleMetaDataService moduleMetadataService, BatchInsert replayAuditService){
        this.solrGeneralService = solrGeneralService;
        if(this.solrGeneralService == null) {
            throw new IllegalArgumentException("solrGeneralService cannot be null!!");
        }
        this.hospitalAuditService = hospitalAuditService;
        if(this.hospitalAuditService == null) {
            throw new IllegalArgumentException("hospitalAuditService cannot be null!!");
        }
        this.resubmissionRestService = resubmissionRestService;
        if(this.resubmissionRestService == null) {
            throw new IllegalArgumentException("resubmissionRestService cannot be null!!");
        }
        this.replayRestService = replayRestService;
        if(this.replayRestService == null) {
            throw new IllegalArgumentException("replayRestService cannot be null!!");
        }
        this.moduleMetadataService = moduleMetadataService;
        if(this.moduleMetadataService == null) {
            throw new IllegalArgumentException("moduleMetadataService cannot be null!!");
        }
        this.replayAuditService = replayAuditService;
        if(this.replayAuditService == null) {
            throw new IllegalArgumentException("replayAuditService cannot be null!!");
        }

        this.createSearchResultsGrid();

        createSearchResultGridLayout();

        this.setSizeFull();

        translatedEventActionMessage = getTranslation("message.resubmission-event-action"
            , UI.getCurrent().getLocale());
    }

    /**
     * Create the results grid layout.
     */
    protected void createSearchResultGridLayout()
    {
        createSearchResultsGrid();

        this.resultsLabel.setVisible(false);

        Image selectAllImage = new Image("/frontend/images/all-small-off-icon.png", "");
        selectAllImage.setHeight("30px");
        selectAllButton = new Button(selectAllImage);
        selectAllButton.setHeight("35px");
        selectAllButton.setWidth("35px");
        Image replayImage = new Image("/frontend/images/replay-service.png", "");
        replayImage.setHeight("30px");
        replayButton = new Button(replayImage);
        replayButton.setHeight("35px");
        replayButton.setWidth("35px");
        Image resubmitImage = new Image("/frontend/images/resubmit-icon.png", "");
        resubmitImage.setHeight("30px");
        resubmitButton = new Button(resubmitImage);
        resubmitButton.setHeight("35px");
        resubmitButton.setWidth("35px");
        Image ignoreImage = new Image("/frontend/images/ignore-icon.png", "");
        ignoreImage.setHeight("30px");
        ignoreButton = new Button(ignoreImage);
        ignoreButton.setHeight("35px");
        ignoreButton.setWidth("35px");

        selectAllTooltip = TooltipHelper.getTooltipForComponentTopLeft(selectAllButton, getTranslation("tooltip.select-all", UI.getCurrent().getLocale()));
        resubmitButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(resubmitButton, getTranslation("tooltip.bulk-resubmit", UI.getCurrent().getLocale()));
        ignoreButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(ignoreButton, getTranslation("tooltip.bulk-ignore", UI.getCurrent().getLocale()));
        replayButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(replayButton, getTranslation("tooltip.bulk-replay", UI.getCurrent().getLocale()));

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
        controlLayout.setSpacing(false);
        controlLayout.setMargin(false);
        controlLayout.getStyle().set("background", "white");
        controlLayout.getStyle().set("color", "black");
        controlLayout.add(resultsLayout, buttonLayoutWrapper);

        this.add(controlLayout, searchResultsGrid);
    }

    public void tooltipBottom() {
        selectAllTooltip = TooltipHelper.getTooltipForComponentBottom(selectAllButton, getTranslation("tooltip.select-all", UI.getCurrent().getLocale()));
        resubmitButtonTooltip = TooltipHelper.getTooltipForComponentBottom(resubmitButton, getTranslation("tooltip.bulk-resubmit", UI.getCurrent().getLocale()));
        ignoreButtonTooltip = TooltipHelper.getTooltipForComponentBottom(ignoreButton, getTranslation("tooltip.bulk-ignore", UI.getCurrent().getLocale()));
        replayButtonTooltip = TooltipHelper.getTooltipForComponentBottom(replayButton, getTranslation("tooltip.bulk-replay", UI.getCurrent().getLocale()));
    }

    /**
     * Create the grid that the search results appear.
     */
    private void createSearchResultsGrid()
    {
        this.searchResultsGrid = new SolrSearchFilteringGrid(this.solrGeneralService, searchFilter, this.resultsLabel);

        // Add the icon column to the grid
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            if(ikasanSolrDocument.getType().equalsIgnoreCase(SearchConstants.WIRETAP))
            {
                Image wiretapImage = new Image("frontend/images/wiretap-service.png", "");
                wiretapImage.setHeight("30px");
                horizontalLayout.add(wiretapImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(SearchConstants.ERROR))
            {
                Image errorImage = new Image("frontend/images/error-service.png", "");
                errorImage.setHeight("30px");
                horizontalLayout.add(errorImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(SearchConstants.EXCLUSION))
            {
                Image hospitalImage = new Image("frontend/images/hospital-service.png", "");
                hospitalImage.setHeight("30px");
                horizontalLayout.add(hospitalImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(SearchConstants.REPLAY)) {
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
            .setFlexGrow(4)
            .setResizable(true);

        // Add the flow name column to the grid
        this.searchResultsGrid.addColumn(IkasanSolrDocument::getFlowName).setKey("flowName")
            .setHeader(getTranslation("table-header.flow-name", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(6)
            .setResizable(true);

        // Add the component name column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.componentName]]</div>")
            .withProperty("componentName",
                ikasanSolrDocument -> Optional.ofNullable(ikasanSolrDocument.getComponentName()).orElse(getTranslation("label.not-applicable", UI.getCurrent().getLocale()))))
            .setKey("componentName")
            .setHeader(getTranslation("table-header.component-name", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(6)
            .setResizable(true);

        // Add the event identifier column to the grid
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();

            if(ikasanSolrDocument.getType().equals("wiretap")) {
                String route = RouteConfiguration.forSessionScope()
                    .getUrl(EventLifeIdDeepLinkView.class, ikasanSolrDocument.getEventId());
                Anchor link = new Anchor(route, ikasanSolrDocument.getEventId());
                link.setTarget("_blank");
                add(link);
                horizontalLayout.add(link);
                link.getStyle().set("color", "blue");
            }
            else if(ikasanSolrDocument.getType().equals("error") || ikasanSolrDocument.getType().equals("exclusion")) {
                Div div = new Div();

                String identifier = new String();

                if(ikasanSolrDocument.getEventId() != null && !ikasanSolrDocument.getEventId().isBlank()) {
                    identifier = ikasanSolrDocument.getEventId() + " / ";
                }

                div.add(identifier + ikasanSolrDocument.getErrorUri());
                horizontalLayout.add(div);
            }
            else {
                Div div = new Div();
                div.add(ikasanSolrDocument.getEventId());
                horizontalLayout.add(div);
            }

            return horizontalLayout;
        }))
            .setKey("event")
            .setHeader(getTranslation("table-header.event-id", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(8)
            .setResizable(true);

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
            .setSortable(false)
            .setFlexGrow(12)
            .setResizable(true);

        // Add the timestamp column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.date]]</div>")
            .withProperty("date",
                ikasanSolrDocument -> DateFormatter.getFormattedDate(ikasanSolrDocument.getTimeStamp()))).setHeader(getTranslation("table-header.timestamp", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setKey("timestamp")
            .setFlexGrow(2)
            .setResizable(true);

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
                if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(SearchConstants.WIRETAP))
                {
                    WiretapDialog wiretapDialog = new WiretapDialog();
                    wiretapDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(SearchConstants.ERROR))
                {
                    ErrorDialog errorDialog = new ErrorDialog();
                    errorDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(SearchConstants.REPLAY))
                {
                    ReplayDialog replayDialog = new ReplayDialog(this.replayRestService, this.replayAuditService);
                    replayDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(SearchConstants.EXCLUSION))
                {
                    HospitalDialog hospitalDialog = new HospitalDialog(this.solrGeneralService, this.hospitalAuditService
                        , this.resubmissionRestService, this.moduleMetadataService, this.searchResultsGrid);
                    hospitalDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
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
            selectAllButton.setHeight("35px");
            selectAllButton.setWidth("35px");
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
            selectAllButton.setHeight("35px");
            selectAllButton.setWidth("35px");

            this.selected = Boolean.TRUE;
            this.replayEventSubmissionListener.setSelected(Boolean.TRUE);
            this.resubmitHospitalEventSubmissionListener.setSelected(Boolean.TRUE);
            this.ignoreHospitalEventSubmissionListener.setSelected(Boolean.TRUE);
        }

        selectAllButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> toggleSelected());
        this.functionalGroupSetup(this.searchTypes);
    }

    /**
     * Add the event listener for replay events.
     */
    private void addReplayButtonEventListener()
    {
        if(this.replayEventRegistration != null)
        {
            this.replayEventRegistration.remove();
        }

        this.replayEventSubmissionListener = new ReplayEventSubmissionListener(this.replayRestService, this.replayAuditService, this.moduleMetadataService, this.searchResultsGrid, this.selectionBoxes, this.selectionItems);
        this.replayEventRegistration = this.replayButton.addClickListener(this.replayEventSubmissionListener);
    }

    /**
     * Add the event listener for the resubmission of hospital events.
     */
    private void addHospitalResubmitButtonEventListener()
    {
        if(this.resubmitHospitalEventRegistration != null)
        {
            this.resubmitHospitalEventRegistration.remove();
        }

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        this.resubmitHospitalEventSubmissionListener = new  ResubmitHospitalEventSubmissionListener(this.hospitalAuditService, this.resubmissionRestService
            , this.moduleMetadataService, this.solrGeneralService, translatedEventActionMessage, this.searchResultsGrid, this.selectionBoxes, this.selectionItems, authentication);
        this.resubmitHospitalEventRegistration = this.resubmitButton.addClickListener(this.resubmitHospitalEventSubmissionListener);
    }

    /**
     * Add the event listener to deal with ignoring hospital events.
     */
    private void addIgnoreButtonEventListener()
    {
        if(this.ignoreHospitalEventRegistration != null)
        {
            this.ignoreHospitalEventRegistration.remove();
        }

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        this.ignoreHospitalEventSubmissionListener = new IgnoreHospitalEventSubmissionListener(this.hospitalAuditService, this.resubmissionRestService
            , this.moduleMetadataService, this.solrGeneralService, translatedEventActionMessage, this.searchResultsGrid, this.selectionBoxes, this.selectionItems, authentication);
        this.ignoreHospitalEventRegistration = this.ignoreButton.addClickListener(ignoreHospitalEventSubmissionListener);
    }

    /**
     * Set up the functional group.
     */
    private void functionalGroupSetup(List<String> types)
    {
        buttonLayout.removeAll();

        if(types.size() != 1) {
            return;
        }

        String type = types.get(0);

        if(type.equals("replay"))
        {
            buttonLayout.add(replayButton, replayButtonTooltip, selectAllButton, selectAllTooltip);

            ComponentSecurityVisibility.applySecurity(replayButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);
            ComponentSecurityVisibility.applySecurity(selectAllButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);

            buttonLayout.setWidth("80px");
        }
        else if(type.equals("exclusion"))
        {
            buttonLayout.add(this.resubmitButton, resubmitButtonTooltip, this.ignoreButton, ignoreButtonTooltip, this.selectAllButton, selectAllTooltip);

            ComponentSecurityVisibility.applySecurity(resubmitButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);
            ComponentSecurityVisibility.applySecurity(ignoreButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);
            ComponentSecurityVisibility.applySecurity(selectAllButton, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);

            buttonLayout.setWidth("130px");
        }
    }

    public void search(long startTime, long endTime, String searchTerm, List<String> types, boolean negateQuery, List<String> moduleNames, List<String> flowNames) {
        this.searchTypes = types;
        this.selected = false;
        searchFilter.setModuleNamesFilterList(moduleNames);
        searchFilter.setFlowNamesFilterList(flowNames);

        if(searchTerm != null && !searchTerm.isEmpty()){
            searchTerm = "\""+searchTerm+"\"";
        }

        this.searchResultsGrid.init(startTime, endTime, searchTerm, types, negateQuery, null);
        this.resultsLabel.setVisible(true);

        this.functionalGroupSetup(types);

        this.addReplayButtonEventListener();
        this.addHospitalResubmitButtonEventListener();
        this.addIgnoreButtonEventListener();
    }
}
