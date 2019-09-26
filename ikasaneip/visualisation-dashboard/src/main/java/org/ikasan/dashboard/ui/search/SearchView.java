package org.ikasan.dashboard.ui.search;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.general.component.*;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.component.filter.SearchFilter;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.solr.SolrSearchService;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Route(value = "search", layout = IkasanAppLayout.class)
@UIScope
@Component
public class SearchView extends VerticalLayout implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(SearchView.class);

    public static final String ALL = "All";
    public static final String WIRETAP = "Wiretap";
    public static final String ERROR = "Error";
    public static final String EXCLUSION = "Exclusion";
    public static final String REPLAY = "Replay";

    @Resource
    private SolrSearchService<IkasanSolrDocumentSearchResults> solrSearchService;

    @Resource
    private ErrorReportingService errorReportingService;

    private SolrSearchFilteringGrid searchResultsGrid;
    private WiretapDialog wiretapDialog = new WiretapDialog();
    private ErrorDialog errorDialog = new ErrorDialog();
    private ReplayDialog replayDialog = new ReplayDialog();
    private ExclusionDialog exclusionDialog;

    private RadioButtonGroup<EntityRadioEntry> searchTypeRadioGroup = new RadioButtonGroup<>();

    private TextField searchText = new TextField(getTranslation("label.search-term", UI.getCurrent().getLocale(), null));
    private DatePicker startDate;
    private DatePicker endDate;
    private TimePicker startTimePicker = new TimePicker();
    private TimePicker endTimePicker = new TimePicker();

    private Label resultsLabel = new Label();
    private Button selectAllButton = new TableButton(VaadinIcon.CHECK_SQUARE_O.create());
    private Button replayButton = new TableButton(VaadinIcon.RECYCLE.create());
    private Button resubmitButton = new TableButton(VaadinIcon.FILE_REFRESH.create());
    private Button ignoreButton = new TableButton(VaadinIcon.CLOSE.create());
    private Button downloadButton = new TableButton(VaadinIcon.DOWNLOAD.create());

    private boolean initialised = false;

    private HashMap<String, Checkbox> selectionBoxes = new HashMap<>();
    private boolean selected = false;

    private HorizontalLayout buttonLayout = new HorizontalLayout();


    /**
     * Constructor
     */
    public SearchView()
    {
        this.setMargin(true);
        this.setSizeFull();

    }

    protected void createSearchForm()
    {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("100%");

        LocalDate nowDate = LocalDate.now();

//        ComponentSecurityVisibility.applySecurity(replayCheckbox, SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY);
//        ComponentSecurityVisibility.applySecurity(errorCheckbox, SecurityConstants.SEARCH_WRITE, SecurityConstants.SEARCH_ADMIN,SecurityConstants.ALL_AUTHORITY);
//        ComponentSecurityVisibility.applySecurity(exclusionCheckbox, SecurityConstants.SEARCH_WRITE, SecurityConstants.SEARCH_ADMIN,SecurityConstants.ALL_AUTHORITY);
//        ComponentSecurityVisibility.applySecurity(replayCheckbox, SecurityConstants.SEARCH_WRITE, SecurityConstants.SEARCH_ADMIN,SecurityConstants.ALL_AUTHORITY);

        startDate = new DatePicker(nowDate);
        startDate.setLabel(getTranslation("label.start-date", UI.getCurrent().getLocale(), null));
        searchLayout.add(startDate);

        startTimePicker.setLabel(getTranslation("label.start-time", UI.getCurrent().getLocale(), null));
        startTimePicker.setStep(Duration.ofMinutes(15l));
        startTimePicker.setValue(LocalTime.of(0, 0));

        searchLayout.add(startTimePicker);

        endDate = new DatePicker(nowDate.plus(1, ChronoUnit.DAYS));
        endDate.setLabel(getTranslation("label.end-date", UI.getCurrent().getLocale(), null));
        searchLayout.add(endDate);


        endTimePicker.setLabel(getTranslation("label.end-time", UI.getCurrent().getLocale(), null));
        endTimePicker.setStep(Duration.ofMinutes(15l));
        endTimePicker.setValue(LocalTime.of(0, 0));

        searchLayout.add(endTimePicker);

        searchText.setWidth("600px");

        Button searchButton = new Button(getTranslation("button.search", UI.getCurrent().getLocale(), null));
        searchButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            search(searchText.getValue(),
                Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() + this.getMilliFromTime(this.startTimePicker.getValue()),
                Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime() + this.getMilliFromTime(this.endTimePicker.getValue())));

        searchLayout.add(searchText);

        EntityRadioEntry allEntry = new EntityRadioEntry(getTranslation("radio-button-label.all", UI.getCurrent().getLocale(), null), ALL);
        EntityRadioEntry wiretapEntry = new EntityRadioEntry(getTranslation("radio-button-label.wiretap", UI.getCurrent().getLocale(), null), WIRETAP);
        EntityRadioEntry errorEntry = new EntityRadioEntry(getTranslation("radio-button-label.error", UI.getCurrent().getLocale(), null), ERROR);
        EntityRadioEntry exclusionEntry = new EntityRadioEntry(getTranslation("radio-button-label.exclusion", UI.getCurrent().getLocale(), null), EXCLUSION);
        EntityRadioEntry replayEntry = new EntityRadioEntry(getTranslation("radio-button-label.replay", UI.getCurrent().getLocale(), null), REPLAY);

        this.searchTypeRadioGroup.setItems(allEntry, wiretapEntry, errorEntry, exclusionEntry, replayEntry);
        this.searchTypeRadioGroup.setRenderer(new TextRenderer<>(EntityRadioEntry::getI18nLabel));

        HorizontalLayout checkBoxLayout = new HorizontalLayout();
        checkBoxLayout.add(this.searchTypeRadioGroup);

        this.add(searchLayout, checkBoxLayout, searchButton);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchLayout);
        this.setHorizontalComponentAlignment(Alignment.CENTER, checkBoxLayout);
        this.setHorizontalComponentAlignment(Alignment.CENTER, searchButton);
    }

    protected void createSearchResultGridLayout()
    {
        createSearchResultsGrid();

        this.resultsLabel.setVisible(false);

        selectAllButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> toggleSelected());

        addReplayButtonEventListener();
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

    private void createSearchResultsGrid()
    {
        SearchFilter searchFilter = new SearchFilter();

        this.searchResultsGrid = new SolrSearchFilteringGrid(this.solrSearchService, searchFilter, this.resultsLabel);

        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

            if(ikasanSolrDocument.getType().equalsIgnoreCase(WIRETAP))
            {
                Button wiretap = new TableButton(VaadinIcon.BOLT.create());
                horizontalLayout.add(wiretap);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(ERROR))
            {
                Button wiretap = new TableButton(VaadinIcon.EXCLAMATION.create());
                horizontalLayout.add(wiretap);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(EXCLUSION))
            {
                Button wiretap = new TableButton(VaadinIcon.DOCTOR_BRIEFCASE.create());
                horizontalLayout.add(wiretap);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(REPLAY))
            {
                Button wiretap = new TableButton(VaadinIcon.RECYCLE.create());
                horizontalLayout.add(wiretap);
            }


            return horizontalLayout;
        })).setWidth("40px");
        this.searchResultsGrid.addColumn(IkasanSolrDocument::getModuleName).setKey("modulename").setHeader(getTranslation("table-header.module-name", UI.getCurrent().getLocale())).setSortable(true).setFlexGrow(4);
        this.searchResultsGrid.addColumn(IkasanSolrDocument::getFlowName).setKey("flowname").setHeader(getTranslation("table-header.flow-name", UI.getCurrent().getLocale())).setSortable(true).setFlexGrow(6);
        this.searchResultsGrid.addColumn(IkasanSolrDocument::getComponentName).setKey("componentname").setHeader(getTranslation("table-header.component-name", UI.getCurrent().getLocale())).setSortable(true).setFlexGrow(6);
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.eventIdentifier]]</div>")
            .withProperty("eventIdentifier",
                ikasanSolrDocument -> Optional.ofNullable(ikasanSolrDocument.getErrorUri()).orElse(ikasanSolrDocument.getEventId()))).setKey("eventId").setHeader(getTranslation("table-header.event-id", UI.getCurrent().getLocale())).setSortable(true).setFlexGrow(8);
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.event]]</div>")
            .withProperty("event",
                ikasanSolrDocument -> {
                    if(ikasanSolrDocument.getEvent() == null)
                    {
                        return "";
                    }
                    else
                    {
                        int endIndex = ikasanSolrDocument.getEvent().length() > 200 ? 200 : ikasanSolrDocument.getEvent().length();
                        return ikasanSolrDocument.getEvent().substring(0, endIndex);
                    }
                })).setKey("event").setHeader(getTranslation("table-header.event-details", UI.getCurrent().getLocale())).setSortable(true).setFlexGrow(12);
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.date]]</div>")
            .withProperty("date",
                ikasanSolrDocument -> DateFormatter.getFormattedDate(ikasanSolrDocument.getTimeStamp()))).setHeader(getTranslation("table-header.timestamp", UI.getCurrent().getLocale())).setSortable(true).setFlexGrow(2);
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();

            Checkbox checkbox = new Checkbox();
            checkbox.setId(ikasanSolrDocument.getId());
            horizontalLayout.add(checkbox);

            if(!this.selectionBoxes.containsKey(ikasanSolrDocument.getId()))
            {
                checkbox.setValue(selected);
                this.selectionBoxes.put(ikasanSolrDocument.getId(), checkbox);
            }
            else
            {
                checkbox.setValue(selectionBoxes.get(ikasanSolrDocument.getId()).getValue());
                this.selectionBoxes.put(ikasanSolrDocument.getId(), checkbox);
            }

            return horizontalLayout;
        })).setWidth("20px");

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

        HeaderRow hr = searchResultsGrid.appendHeaderRow();
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setModuleNameFilter, "modulename");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setFlowNameFilter, "flowname");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setComponentNameFilter, "componentname");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setEventIdFilter, "eventId");

        this.searchResultsGrid.setSizeFull();
    }

    protected void search(String searchTerm, long startDate, long endDate)
    {
        ArrayList<String> types = new ArrayList<>();

        if(this.searchTypeRadioGroup.getValue().getEntity().equals(ALL))
        {
            if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY))
            {
                types.add(SolrReplayDao.REPLAY);
            }

            if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_WRITE, SecurityConstants.SEARCH_ADMIN, SecurityConstants.ALL_AUTHORITY))
            {
                types.add(SolrExclusionEventDao.EXCLUSION);
                types.add(SolrWiretapDao.WIRETAP);
                types.add(SolrErrorReportingServiceDao.ERROR);
            }
        }
        else
        {
            if(this.searchTypeRadioGroup.getValue().getEntity().equals(REPLAY))
            {
                types.add(SolrReplayDao.REPLAY);
            }

            if(this.searchTypeRadioGroup.getValue().getEntity().equals(WIRETAP))
            {
                types.add(SolrWiretapDao.WIRETAP);
            }

            if(this.searchTypeRadioGroup.getValue().getEntity().equals(EXCLUSION))
            {
                types.add(SolrExclusionEventDao.EXCLUSION);
            }

            if(this.searchTypeRadioGroup.getValue().getEntity().equals(ERROR))
            {
                types.add(SolrErrorReportingServiceDao.ERROR);
            }
        }

        this.selectionBoxes = new HashMap<>();

        this.searchResultsGrid.init(startDate, endDate, searchTerm, types);

        if(selected)
        {
            toggleSelected();
        }
        searchGroupSetup();
        this.resultsLabel.setVisible(true);
    }

    private long getMilliFromTime(LocalTime localTime)
    {
        long milli = 0;
        if(localTime.getMinute() > 0)
        {
            milli += localTime.getMinute()  * 60 * 1000;
        }
        if(localTime.getHour() > 0)
        {
            milli = localTime.getHour() * 60  * 60 * 1000;
        }

        return milli;
    }

    private void searchGroupSetup()
    {
        buttonLayout.removeAll();

        if(searchTypeRadioGroup.getValue().getEntity().equals(REPLAY))
        {
            buttonLayout.add(replayButton, selectAllButton);
            buttonLayout.setWidth("80px");
        }
        else if(searchTypeRadioGroup.getValue().getEntity().equals(EXCLUSION))
        {
            buttonLayout.add(this.resubmitButton, this.ignoreButton, this.selectAllButton);
            buttonLayout.setWidth("120px");
        }
        else if(searchTypeRadioGroup.getValue().getEntity().equals(ERROR) || searchTypeRadioGroup.getValue().getEntity().equals(WIRETAP))
        {
            buttonLayout.add(this.downloadButton, selectAllButton);
            buttonLayout.setWidth("80px");
        }
    }



    private void addReplayButtonEventListener()
    {
        this.replayButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog();

            progressIndicatorDialog.open(String.format("Replaying %s events", searchResultsGrid.getResultSize()));

            final UI current = UI.getCurrent();
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try
                {
                    for(int i=0; i<searchResultsGrid.getResultSize(); i+=100)
                    {
                        List<IkasanSolrDocument> docs = (List<IkasanSolrDocument>)searchResultsGrid.getDataProvider().fetch
                            (new Query<>(i, i+100, Collections.EMPTY_LIST, null, null)).collect(Collectors.toList());

                        for(IkasanSolrDocument document: docs)
                        {
                            logger.info("replaying [{}]", document.getEventId());
                        }
                    }

                    current.access(() ->
                    {
                        progressIndicatorDialog.close();
                        NotificationHelper.showUserNotification("Replay complete.");
                    });
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    current.access(() ->
                    {
                        progressIndicatorDialog.close();
                        NotificationHelper.showErrorNotification("Error occurred while replaying! " + e.getLocalizedMessage());
                    });

                    return;
                }
            });
        });
    }

    private void toggleSelected()
    {
        if(selected)
        {
            selectionBoxes.keySet().forEach(key -> selectionBoxes.get(key).setValue(false));
            selectAllButton.setIcon(VaadinIcon.CHECK_SQUARE_O.create());

            selected = false;
        }
        else
        {
            selectionBoxes.keySet().forEach(key -> selectionBoxes.get(key).setValue(true));
            selectAllButton.setIcon(VaadinIcon.CHECK_SQUARE.create());

            selected = true;
        }
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        if(!initialised)
        {
            this.createSearchForm();
            this.createSearchResultGridLayout();

            this.exclusionDialog = new ExclusionDialog(this.errorReportingService);

            this.initialised = true;
        }
    }

    private class EntityRadioEntry
    {
        private String i18nLabel;
        private String entity;

        public EntityRadioEntry(String i18nLabel, String entity)
        {
            this.i18nLabel = i18nLabel;
            this.entity = entity;
        }

        public String getI18nLabel()
        {
            return i18nLabel;
        }

        public String getEntity()
        {
            return entity;
        }
    }
}

