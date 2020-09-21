package org.ikasan.dashboard.ui.search.view;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.general.component.*;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.search.component.ChangePasswordDialog;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.component.filter.SearchFilter;
import org.ikasan.dashboard.ui.search.listener.IgnoreHospitalEventSubmissionListener;
import org.ikasan.dashboard.ui.search.listener.ResubmitHospitalEventSubmissionListener;
import org.ikasan.dashboard.ui.search.listener.ReplayEventSubmissionListener;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.dashboard.ui.util.DateTimeUtil;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrGeneralService;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Route(value = "", layout = IkasanAppLayout.class)
@UIScope
@Component
@PageTitle("Ikasan - Search")
public class SearchView extends VerticalLayout implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(SearchView.class);

    public static final String ALL = "All";
    public static final String WIRETAP = "Wiretap";
    public static final String ERROR = "Error";
    public static final String EXCLUSION = "Exclusion";
    public static final String REPLAY = "Replay";

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
    private ModuleMetaDataService moduleMetadataService;

    @Resource
    private BatchInsert replayAuditService;

    @Resource
    private UserService userService;

    @Value("${render.search.images}")
    private boolean renderSearchImages;

    private SolrSearchFilteringGrid searchResultsGrid;

    private TextArea searchText = new TextArea();
    private DatePicker startDate;
    private DatePicker endDate;
    private TimePicker startTimePicker = new TimePicker();
    private TimePicker endTimePicker = new TimePicker();
    private Checkbox negateQueryCheckbox;

    private Label resultsLabel = new Label();
    private Button selectAllButton;
    private Button replayButton;
    private Button resubmitButton;
    private Button ignoreButton;

    private boolean initialised = false;

    private HashMap<String, Checkbox> selectionBoxes = new HashMap<>();
    private HashMap<String, IkasanSolrDocument> selectionItems = new HashMap<>();
    private Boolean selected = Boolean.FALSE;

    private HorizontalLayout buttonLayout = new HorizontalLayout();

    private Registration replayEventRegistration;
    private ReplayEventSubmissionListener replayEventSubmissionListener;

    private Registration resubmitHospitalEventRegistration;
    private ResubmitHospitalEventSubmissionListener resubmitHospitalEventSubmissionListener;

    private Registration ignoreHospitalEventRegistration;
    private IgnoreHospitalEventSubmissionListener ignoreHospitalEventSubmissionListener;

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

    private Checkbox wiretapCheckbox;
    private Checkbox errorCheckbox;
    private Checkbox hospitalCheckbox;
    private Checkbox replayCheckbox;


    private String currentSearchType = "";
    private String translatedEventActionMessage;

    /**
     * Constructor
     */
    public SearchView()
    {
        this.setMargin(false);
        this.setSizeFull();

        translatedEventActionMessage = getTranslation("message.resubmission-event-action"
            , UI.getCurrent().getLocale());
    }

    /**
     * Create the search form that appears at the top of the screen.
     */
    protected void createSearchForm()
    {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("1000px");
        searchLayout.setSpacing(true);

        LocalDate nowDate = LocalDate.now();

        HorizontalLayout startDateTimeLayout = new HorizontalLayout();
        this.startDate = new DatePicker(nowDate);
        this.startDate.setLocale(UI.getCurrent().getLocale());
        this.startDate.setWidth("150px");

        this.startTimePicker.setStep(Duration.ofMinutes(15l));
        this.startTimePicker.setLocale(UI.getCurrent().getLocale());
        this.startTimePicker.setValue(LocalTime.of(0, 0));
        this.startTimePicker.setWidth("150px");

        startDateTimeLayout.add(startDate, startTimePicker);

        HorizontalLayout endDateTimeLayout = new HorizontalLayout();
        this.endDate = new DatePicker(nowDate.plus(1, ChronoUnit.DAYS));
        this.endDate.setLocale(UI.getCurrent().getLocale());
        this.endDate.setWidth("150px");


        this.endTimePicker.setStep(Duration.ofMinutes(15l));
        this.endTimePicker.setLocale(UI.getCurrent().getLocale());
        this.endTimePicker.setValue(LocalTime.of(0, 0));
        this.endTimePicker.setWidth("150px");

        endDateTimeLayout.add(endDate, endTimePicker);

        VerticalLayout dateTimePickersLayout = new VerticalLayout();
        dateTimePickersLayout.add(startDateTimeLayout, endDateTimeLayout);
        dateTimePickersLayout.setWidth("350px");

        this.searchText.setWidth("600px");
        this.searchText.setHeight("80px");
        this.searchText.setPlaceholder("search term");
        this.searchText.setRequired(true);

        HorizontalLayout searchTextLayout = new HorizontalLayout();
        searchTextLayout.setMargin(true);
        searchTextLayout.add(searchText);

        Image searchButtonImage = new Image("frontend/images/search-icon.png", "");
        searchButtonImage.setHeight("50px");
        this.searchButton = new Button(searchButtonImage);
        this.searchButton.setHeight("50px");
        this.searchButton.setWidth("50px");

        addButtonSearchListener(this.searchButton);

        this.allButtonTooltip = TooltipHelper.getTooltipForComponentBottom(searchButton, getTranslation("tooltip.search-all-event-types"
            , UI.getCurrent().getLocale()));

        searchTextLayout.add(this.searchButton, this.allButtonTooltip);
        searchTextLayout.setVerticalComponentAlignment(Alignment.CENTER, searchButton);


        searchLayout.add(dateTimePickersLayout, searchTextLayout);

        this.wiretapImage = new Image("frontend/images/wiretap-service.png", "");
        this.wiretapImage.setHeight("40px");
        this.wiretapButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.wiretapImage, getTranslation("tooltip.search-wiretap-events", UI.getCurrent().getLocale()));


        this.wiretapCheckbox = new Checkbox();
        this.wiretapCheckbox.setValue(true);
        VerticalLayout wiretapFilterLayout = createSearchFilterLayout(this.wiretapImage, this.wiretapCheckbox);
        wiretapFilterLayout.add(this.wiretapButtonTooltip);

        this.errorImage = new Image("frontend/images/error-service.png", "");
        this.errorImage.setHeight("40px");
        this.errorButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.errorImage, getTranslation("tooltip.search-error-events", UI.getCurrent().getLocale()));

        this.errorCheckbox = new Checkbox();
        this.errorCheckbox.setValue(true);

        VerticalLayout errorFilterLayout = createSearchFilterLayout(this.errorImage, this.errorCheckbox);
        errorFilterLayout.add(this.errorButtonTooltip);

        this.hospitalImage = new Image("frontend/images/hospital-service.png", "");
        this.hospitalImage.setHeight("40px");
        this.hospitalButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.hospitalImage, getTranslation("tooltip.search-hospital-events", UI.getCurrent().getLocale()));

        this.hospitalCheckbox = new Checkbox();
        this.hospitalCheckbox.setValue(true);
        VerticalLayout hospitalFilterLayout = createSearchFilterLayout(this.hospitalImage, this.hospitalCheckbox);
        hospitalFilterLayout.add(this.hospitalButtonTooltip);

        this.replayImage = new Image("frontend/images/replay-service.png", "");
        this.replayImage.setHeight("40px");
        this.replaySearchButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(this.replayImage, getTranslation("tooltip.search-replay-events", UI.getCurrent().getLocale()));

        this.replayCheckbox = new Checkbox();
        this.replayCheckbox.setValue(true);
        VerticalLayout replayFilterLayout = createSearchFilterLayout(replayImage, replayCheckbox);
        replayFilterLayout.add(replaySearchButtonTooltip);

        this.negateQueryCheckbox = new Checkbox(getTranslation("checkbox-label.negate-search", UI.getCurrent().getLocale()));
        this.negateQueryCheckbox.setWidth("100px");
        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setWidth("100px");
        checkboxLayout.add(this.negateQueryCheckbox);
        checkboxLayout.setVerticalComponentAlignment(Alignment.CENTER, this.negateQueryCheckbox);


        HorizontalLayout searchIconLayout = new HorizontalLayout();
        searchIconLayout.setSpacing(true);
        searchIconLayout.add(wiretapFilterLayout, replayFilterLayout, hospitalFilterLayout, errorFilterLayout, checkboxLayout);
        searchIconLayout.setVerticalComponentAlignment(Alignment.CENTER, checkboxLayout);

        ComponentSecurityVisibility.applySecurity(replayFilterLayout, SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(errorFilterLayout, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_WRITE, SecurityConstants.ERROR_ADMIN,SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(hospitalFilterLayout, SecurityConstants.EXCLUSION_READ, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.EXCLUSION_ADMIN,SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(replayFilterLayout, SecurityConstants.REPLAY_READ, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);

        this.add(searchLayout, searchIconLayout);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchLayout);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchIconLayout);
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

        this.add(controlLayout, searchResultsGrid);
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
                    WiretapDialog wiretapDialog = new WiretapDialog();
                    wiretapDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(ERROR))
                {
                    ErrorDialog errorDialog = new ErrorDialog();
                    errorDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(REPLAY))
                {
                    ReplayDialog replayDialog = new ReplayDialog(this.replayRestService, this.replayAuditService);
                    replayDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(EXCLUSION))
                {
                    HospitalDialog exclusionDialog = new HospitalDialog(this.errorReportingService, this.hospitalAuditService
                        , this.resubmissionRestService, this.moduleMetadataService);

                    exclusionDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());

                    exclusionDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
                    {
                        if (!dialogOpenedChangeEvent.isOpened())
                        {
                            searchResultsGrid.getDataProvider().refreshAll();
                        }
                    });
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
     * Method to perform the search.
     *
     * @param searchTerm the search term
     * @param startDate the start date/time of the search
     * @param endDate the end date/time of the search
     */
    protected void search(String searchTerm, long startDate, long endDate)
    {
        if(this.replayCheckbox.getValue() && !this.errorCheckbox.getValue() && !this.hospitalCheckbox.getValue() && !this.wiretapCheckbox.getValue())
        {
            this.currentSearchType = REPLAY;
        }
        else if(this.hospitalCheckbox.getValue() && !this.replayCheckbox.getValue() && !this.errorCheckbox.getValue() && !this.wiretapCheckbox.getValue())
        {
            this.currentSearchType = EXCLUSION;
        }
        else
        {
            this.currentSearchType = "";
        }

        ArrayList<String> types = new ArrayList<>();

        if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY)
            && this.replayCheckbox.getValue())
        {
            types.add(SolrReplayDao.REPLAY);
        }

        if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_WRITE, SecurityConstants.SEARCH_ADMIN, SecurityConstants.SEARCH_READ, SecurityConstants.ALL_AUTHORITY))
        {
            if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.WIRETAP_READ, SecurityConstants.WIRETAP_WRITE, SecurityConstants.WIRETAP_ADMIN, SecurityConstants.ALL_AUTHORITY)
                && this.wiretapCheckbox.getValue())
            {
                types.add(SolrWiretapDao.WIRETAP);
            }

            if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.EXCLUSION_READ, SecurityConstants.EXCLUSION_ADMIN, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.ALL_AUTHORITY)
                && this.hospitalCheckbox.getValue())
            {
                types.add(SolrExclusionEventDao.EXCLUSION);
            }

            if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ERROR_WRITE, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_ADMIN, SecurityConstants.ALL_AUTHORITY)
                && this.errorCheckbox.getValue())
            {
                types.add(SolrErrorReportingServiceDao.ERROR);
            }
        }


        this.selectionBoxes = new HashMap<>();
        this.selectionItems = new HashMap<>();

        if(searchTerm != null && !searchTerm.isEmpty()){
            searchTerm = "\""+searchTerm+"\"";
        }

        this.searchResultsGrid.init(startDate, endDate, searchTerm, types, this.negateQueryCheckbox.getValue());

        if(selected)
        {
            toggleSelected();
        }
        functionalGroupSetup();
        this.resultsLabel.setVisible(true);
        this.addReplayButtonEventListener();
        this.addHospitalResubmitButtonEventListener();
        this.addIgnoreButtonEventListener();
    }

    /**
     * Add the search listener to a button.
     *
     * @param button
     */
    private void addButtonSearchListener(Button button)
    {
        button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            Binder<SearchTerm> searchTextBinder = new Binder<>(SearchTerm.class);
            SearchTerm searchTerm = new SearchTerm();

            searchTextBinder.forField(this.searchText)
                .bind(SearchTerm::getTerm, SearchTerm::setTerm);

            try
            {
                searchTextBinder.writeBean(searchTerm);
            }
            catch (ValidationException e)
            {
                return;
            }

            search(searchTerm.getTerm(),
                Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() + DateTimeUtil.getMilliFromTime(this.startTimePicker.getValue()),
                Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() + DateTimeUtil.getMilliFromTime(this.endTimePicker.getValue()));
        });
    }

    /**
     * Create the filter layout
     *
     * @param filterImage
     * @param checkbox
     * @return
     */
    private VerticalLayout createSearchFilterLayout(Image filterImage, Checkbox checkbox)
    {
        VerticalLayout buttonLayout = new VerticalLayout();
        filterImage.setHeight("40px");
        filterImage.setWidth("40px");

        checkbox.setHeight("10px");
        buttonLayout.add(filterImage, checkbox);
        buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, checkbox);
        buttonLayout.setFlexGrow(1.0, checkbox);

        buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, filterImage);
        buttonLayout.setWidth("50px");

        buttonLayout.setFlexGrow(2.0, filterImage);

        return buttonLayout;
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

            buttonLayout.setWidth("140px");
        }
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

        this.resubmitHospitalEventSubmissionListener = new ResubmitHospitalEventSubmissionListener(this.hospitalAuditService, this.resubmissionRestService
            , this.moduleMetadataService, this.errorReportingService, translatedEventActionMessage, this.searchResultsGrid, this.selectionBoxes
            , this.selectionItems, authentication);
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
            , this.moduleMetadataService, this.errorReportingService, translatedEventActionMessage, this.searchResultsGrid, this.selectionBoxes
            , this.selectionItems, authentication);
        this.ignoreHospitalEventRegistration = this.ignoreButton.addClickListener(ignoreHospitalEventSubmissionListener);
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


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        if(!initialised)
        {
            this.createSearchForm();
            this.createSearchResultGridLayout();

            this.initialised = true;

            IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

            if(authentication != null)
            {
                User user = this.userService.loadUserByUsername(authentication.getName());

                if (user.isRequiresPasswordChange())
                {
                    ChangePasswordDialog dialog = new ChangePasswordDialog(user, this.userService);
                    dialog.setCloseOnOutsideClick(false);
                    dialog.setCloseOnEsc(false);
                    dialog.setSizeFull();

                    dialog.open();
                }
            }
        }
    }

    private class SearchTerm
    {
        private String term;

        public String getTerm()
        {
            return term;
        }

        public void setTerm(String term)
        {
            this.term = term;
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        this.allButtonTooltip.attachToComponent(this.searchButton);
        this.wiretapButtonTooltip.attachToComponent(this.wiretapImage);
        this.errorButtonTooltip.attachToComponent(this.errorImage);
        this.hospitalButtonTooltip.attachToComponent(this.hospitalImage);
        this.replaySearchButtonTooltip.attachToComponent(this.replayImage);
        this.replayButtonTooltip.attachToComponent(this.replayButton);
        this.selectAllTooltip.attachToComponent(this.selectAllButton);
        this.resubmitButtonTooltip.attachToComponent(this.resubmitButton);
        this.ignoreButtonTooltip.attachToComponent(this.ignoreButton);
    }
}

