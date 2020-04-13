package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.search.SearchConstants;
import org.ikasan.dashboard.ui.util.DateTimeUtil;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamVisualisation;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.solr.SolrGeneralService;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class GraphViewBusinessStreamVisualisation extends VerticalLayout
{
    Logger logger = LoggerFactory.getLogger(GraphViewBusinessStreamVisualisation.class);

    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    private ModuleControlRestServiceImpl moduleControlRestService;

    private ModuleMetaDataService moduleMetadataService;

    private ConfigurationRestServiceImpl configurationRestService;

    private TriggerRestServiceImpl triggerRestService;

    private ConfigurationMetaDataService configurationMetadataService;

    private BusinessStreamVisualisation businessStreamVisualisation;

    private HorizontalLayout headerLayout = new HorizontalLayout();

    private H2 moduleLabel = new H2();

    private Registration broadcasterRegistration;

    private Button allButton;
    private Tooltip allButtonTooltip;
    private Button wiretapButton;
    private Tooltip wiretapButtonTooltip;
    private Button hospitalButton;
    private Tooltip hospitalButtonTooltip;
    private Button errorButton;
    private Tooltip errorButtonTooltip;
    private Button replaySearchButton;
    private Tooltip replaySearchButtonTooltip;
    private Tooltip replayButtonTooltip;
    private Tooltip selectAllTooltip;
    private Tooltip resubmitButtonTooltip;
    private Tooltip ignoreButtonTooltip;

    private TextArea searchText = new TextArea();
    private DatePicker startDate;
    private DatePicker endDate;
    private TimePicker startTimePicker = new TimePicker();
    private TimePicker endTimePicker = new TimePicker();

    /**
     * Constructor
     */
    public GraphViewBusinessStreamVisualisation(SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService
        , ModuleControlRestServiceImpl moduleControlRestService, ModuleMetaDataService moduleMetadataService, ConfigurationRestServiceImpl configurationRestService
        , TriggerRestServiceImpl triggerRestService, ConfigurationMetaDataService configurationMetadataService)
    {
        this.setMargin(true);
        this.setSizeFull();

        this.solrSearchService = solrSearchService;
        if(this.solrSearchService == null){
            throw new IllegalArgumentException("solrSearchService cannot be null!");
        }
        this.moduleControlRestService = moduleControlRestService;
        if(this.moduleControlRestService == null){
            throw new IllegalArgumentException("moduleControlRestService cannot be null!");
        }
        this.moduleMetadataService = moduleMetadataService;
        if(this.moduleMetadataService == null){
            throw new IllegalArgumentException("moduleMetadataService cannot be null!");
        }
        this.configurationRestService = configurationRestService;
        if(this.configurationRestService == null){
            throw new IllegalArgumentException("configurationRestService cannot be null!");
        }
        this.triggerRestService = triggerRestService;
        if(this.triggerRestService == null){
            throw new IllegalArgumentException("triggerRestService cannot be null!");
        }
        this.configurationMetadataService = configurationMetadataService;
        if(this.configurationMetadataService == null){
            throw new IllegalArgumentException("configurationMetadataService cannot be null!");
        }

        init();
    }

    private void init() {
        this.headerLayout = new HorizontalLayout();
        headerLayout.add(this.moduleLabel);
        headerLayout.add(createSearchForm());

        this.add(this.headerLayout);
    }

    /**
     * Create the search form that appears at the top of the screen.
     */
    protected HorizontalLayout createSearchForm()
    {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("1000px");
        searchLayout.setSpacing(true);

        LocalDate nowDate = LocalDate.now();

        HorizontalLayout startDateTimeLayout = new HorizontalLayout();
        startDate = new DatePicker(nowDate);
        startDate.setLocale(UI.getCurrent().getLocale());
        startDate.setWidth("150px");

        startTimePicker.setStep(Duration.ofMinutes(15l));
        startTimePicker.setLocale(UI.getCurrent().getLocale());
        startTimePicker.setValue(LocalTime.of(0, 0));
        startTimePicker.setWidth("150px");

        startDateTimeLayout.add(startDate, startTimePicker);

        HorizontalLayout endDateTimeLayout = new HorizontalLayout();
        endDate = new DatePicker(nowDate.plus(1, ChronoUnit.DAYS));
        endDate.setLocale(UI.getCurrent().getLocale());
        endDate.setWidth("150px");


        endTimePicker.setStep(Duration.ofMinutes(15l));
        endTimePicker.setLocale(UI.getCurrent().getLocale());
        endTimePicker.setValue(LocalTime.of(0, 0));
        endTimePicker.setWidth("150px");

        endDateTimeLayout.add(endDate, endTimePicker);

        VerticalLayout dateTimePickersLayout = new VerticalLayout();
        dateTimePickersLayout.add(startDateTimeLayout, endDateTimeLayout);
        dateTimePickersLayout.setWidth("350px");

        searchText.setWidth("600px");
        searchText.setHeight("80px");
        searchText.setPlaceholder("search term");
        searchText.setRequired(true);

        HorizontalLayout searchTextLayout = new HorizontalLayout();
        searchTextLayout.setMargin(true);
        searchTextLayout.add(searchText);

        searchLayout.add(dateTimePickersLayout, searchTextLayout);

        Label allButtonLabel = new Label(getTranslation("radio-button-label.all", UI.getCurrent().getLocale()));
        Label wiretapButtonLabel = new Label(getTranslation("radio-button-label.wiretap", UI.getCurrent().getLocale()));
        Label errorButtonLabel = new Label(getTranslation("radio-button-label.error", UI.getCurrent().getLocale()));
        Label replayButtonLabel = new Label(getTranslation("radio-button-label.replay", UI.getCurrent().getLocale()));
        Label hospitalButtonLabel = new Label(getTranslation("radio-button-label.exclusion", UI.getCurrent().getLocale()));


        Image allButtonImage = new Image("frontend/images/all-services-icon.png", "");
        allButtonImage.setHeight("40px");
        allButton = new Button(allButtonImage);

        addButtonSearchListener(SearchConstants.ALL, allButton, allButtonLabel, wiretapButtonLabel, errorButtonLabel, replayButtonLabel, hospitalButtonLabel);

        allButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(allButton, getTranslation("tooltip.search-all-event-types"
            , UI.getCurrent().getLocale()));

        VerticalLayout allButtonLayout = createSearchButtonLayout(allButton);
        allButtonLayout.add(allButtonTooltip);

        Image wiretapImage = new Image("frontend/images/wiretap-service.png", "");
        wiretapImage.setHeight("40px");
        wiretapButton = new Button(wiretapImage);
        wiretapButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(wiretapButton, getTranslation("tooltip.search-wiretap-events", UI.getCurrent().getLocale()));

        addButtonSearchListener(SearchConstants.WIRETAP, wiretapButton, allButtonLabel, errorButtonLabel, replayButtonLabel, hospitalButtonLabel);

        VerticalLayout wiretapButtonLayout = createSearchButtonLayout(wiretapButton);
        wiretapButtonLayout.add(wiretapButtonTooltip);

        Image errorImage = new Image("frontend/images/error-service.png", "");
        errorImage.setHeight("40px");
        errorButton = new Button(errorImage);
        errorButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(errorButton, getTranslation("tooltip.search-error-events", UI.getCurrent().getLocale()));

        addButtonSearchListener(SearchConstants.ERROR, errorButton, errorButtonLabel, allButtonLabel, wiretapButtonLabel, replayButtonLabel, hospitalButtonLabel);

        VerticalLayout errorButtonLayout = createSearchButtonLayout(errorButton);
        errorButtonLayout.add(errorButtonTooltip);

        Image hospitalImage = new Image("frontend/images/hospital-service.png", "");
        hospitalImage.setHeight("40px");
        hospitalButton = new Button(hospitalImage);
        hospitalButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(hospitalButton, getTranslation("tooltip.search-hospital-events", UI.getCurrent().getLocale()));

        addButtonSearchListener(SearchConstants.EXCLUSION, hospitalButton,  allButtonLabel, wiretapButtonLabel, replayButtonLabel, errorButtonLabel);

        VerticalLayout hospitalButtonLayout = createSearchButtonLayout(hospitalButton);
        hospitalButtonLayout.add(hospitalButtonTooltip);

        Image replayImage = new Image("frontend/images/replay-service.png", "");
        replayImage.setHeight("40px");
        replaySearchButton = new Button(replayImage);
        replaySearchButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(replaySearchButton, getTranslation("tooltip.search-replay-events", UI.getCurrent().getLocale()));

        addButtonSearchListener(SearchConstants.REPLAY, replaySearchButton, allButtonLabel, wiretapButtonLabel, hospitalButtonLabel, errorButtonLabel);

        VerticalLayout replayButtonLayout = createSearchButtonLayout(replaySearchButton);
        replayButtonLayout.add(replaySearchButtonTooltip);

        searchLayout.add(allButtonLayout, wiretapButtonLayout, replayButtonLayout, hospitalButtonLayout, errorButtonLayout);
        searchLayout.setVerticalComponentAlignment(Alignment.CENTER, allButtonLayout, wiretapButtonLayout, replayButtonLayout, hospitalButtonLayout, errorButtonLayout);

        ComponentSecurityVisibility.applySecurity(replayButtonLayout, SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(errorButtonLayout, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_WRITE, SecurityConstants.ERROR_ADMIN,SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(hospitalButtonLayout, SecurityConstants.EXCLUSION_READ, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.EXCLUSION_ADMIN,SecurityConstants.ALL_AUTHORITY);
        ComponentSecurityVisibility.applySecurity(replayButtonLayout, SecurityConstants.REPLAY_READ, SecurityConstants.REPLAY_WRITE, SecurityConstants.REPLAY_ADMIN, SecurityConstants.ALL_AUTHORITY);

        return searchLayout;
    }

    /**
     * Create the button layout
     *
     * @param button
     * @return
     */
    private VerticalLayout createSearchButtonLayout(Button button)
    {
        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setHeight("40px");
        buttonLayout.setWidth("40px");
        button.setHeight("40px");
        button.setWidth("40px");

        buttonLayout.add(button);
        buttonLayout.setHorizontalComponentAlignment(Alignment.CENTER, button);;

        buttonLayout.setFlexGrow(4.0, button);

        return buttonLayout;
    }


    /**
     * Add the search listener to a button.
     *
     * @param searchType
     * @param button
     * @param selectedLabel
     * @param notSelected
     */
    private void addButtonSearchListener(String searchType, Button button, Label selectedLabel, Label ... notSelected)
    {
        button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            search(searchType, this.searchText.getValue(),
                Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
                    + DateTimeUtil.getMilliFromTime(this.startTimePicker.getValue()),
                Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
                    + DateTimeUtil.getMilliFromTime(this.endTimePicker.getValue()));
        });
    }

    /**
     * Method to perform the search.
     *
     * @param type the entity type
     * @param searchTerm the search term
     * @param startDate the start date/time of the search
     * @param endDate the end date/time of the search
     */
    protected void search(String type, String searchTerm, long startDate, long endDate)
    {
        ArrayList<String> types = new ArrayList<>();

        if(type.equals(SearchConstants.ALL))
        {
            if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY))
            {
                types.add(SolrReplayDao.REPLAY);
            }

            if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_WRITE, SecurityConstants.SEARCH_ADMIN, SecurityConstants.SEARCH_READ, SecurityConstants.ALL_AUTHORITY))
            {
                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.WIRETAP_READ, SecurityConstants.WIRETAP_WRITE, SecurityConstants.WIRETAP_ADMIN, SecurityConstants.ALL_AUTHORITY))
                {
                    types.add(SolrWiretapDao.WIRETAP);
                }

                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.EXCLUSION_READ, SecurityConstants.EXCLUSION_ADMIN, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.ALL_AUTHORITY))
                {
                    types.add(SolrExclusionEventDao.EXCLUSION);
                }

                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ERROR_WRITE, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_ADMIN, SecurityConstants.ALL_AUTHORITY))
                {
                    types.add(SolrErrorReportingServiceDao.ERROR);
                }
            }
        }
        else
        {
            if(type.equals(SearchConstants.REPLAY))
            {
                types.add(SolrReplayDao.REPLAY);
            }

            if(type.equals(SearchConstants.WIRETAP))
            {
                types.add(SolrWiretapDao.WIRETAP);
            }

            if(type.equals(SearchConstants.EXCLUSION))
            {
                types.add(SolrExclusionEventDao.EXCLUSION);
            }

            if(type.equals(SearchConstants.ERROR))
            {
                types.add(SolrErrorReportingServiceDao.ERROR);
            }
        }

        this.businessStreamVisualisation.search(types, searchTerm, startDate, endDate);
    }

    /**
     *
     * @param json
     */
    protected void createBusinessStreamGraph(String name, String json) throws IOException {

        if (this.businessStreamVisualisation != null) {
            this.remove(businessStreamVisualisation);
        }

        businessStreamVisualisation = new BusinessStreamVisualisation(this.moduleControlRestService,
            this.configurationRestService, this.triggerRestService, this.moduleMetadataService
            , this.configurationMetadataService, this.solrSearchService);

        businessStreamVisualisation.createBusinessStreamGraphGraph(json);

        this.moduleLabel.setText(name);

        this.add(businessStreamVisualisation);
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

