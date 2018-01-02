package org.ikasan.dashboard.ui.search.panel;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.icons.AtlassianIcons;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.housekeeping.panel.HousekeepingPanel;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.search.window.ErrorOccurrenceViewWindow;
import org.ikasan.dashboard.ui.search.window.ExclusionEventViewWindow;
import org.ikasan.dashboard.ui.search.window.ReplayEventViewWindow;
import org.ikasan.dashboard.ui.topology.window.WiretapPayloadViewWindow;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.exclusion.service.SolrExclusionServiceImpl;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.replay.service.SolrReplayServiceImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.hospital.service.HospitalManagementService;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.replay.ReplayService;
import org.ikasan.spec.solr.SolrSearchService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.ikasan.wiretap.service.SolrWiretapServiceImpl;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SearchPanel extends Panel implements View
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HousekeepingPanel.class);

    private FilterTable searchResultsTable;
    private IndexedContainer container = null;
    private SolrSearchService<IkasanSolrDocumentSearchResults> solrSearchService = null;
    private Label resultsLabel = new Label();
    private GridLayout layout = null;
    private PlatformConfigurationService platformConfigurationService;
    private PopupDateField fromDate;
    private PopupDateField toDate;
    private SolrWiretapServiceImpl wiretapService;
    private SolrErrorReportingManagementServiceImpl errorReportingService;
    private SolrReplayServiceImpl replayManagementService;
    private ReplayService replayService;
    private TopologyService topologyService;
    private SolrExclusionServiceImpl exclusionManagementService;
    private HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService;
    private ErrorReportingManagementService errorReportingManagementService;
    private HospitalService<byte[]> hospitalService;
    private String solrUsername;
    private String solrPassword;
    private List<Module> modules;
    private List<Flow> flows;
    private Button bulkReplayButton;
    private Button selectAllButton;

    public SearchPanel(SolrSearchService<IkasanSolrDocumentSearchResults> solrSearchService, PlatformConfigurationService platformConfigurationService,
                       SolrWiretapServiceImpl wiretapService,SolrErrorReportingManagementServiceImpl errorReportingService, SolrReplayServiceImpl replayManagementService,
                       ReplayService replayService, TopologyService topologyService, SolrExclusionServiceImpl exclusionManagementService,
                       HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService,
                       ErrorReportingManagementService errorReportingManagementService, HospitalService<byte[]> hospitalService)
    {
        this.solrSearchService = solrSearchService;
        this.platformConfigurationService = platformConfigurationService;
        this.wiretapService = wiretapService;
        this.errorReportingService = errorReportingService;
        this.replayManagementService = replayManagementService;
        this.replayService = replayService;
        this.topologyService = topologyService;
        this.exclusionManagementService = exclusionManagementService;
        this.hospitalManagementService = hospitalManagementService;
        this.errorReportingManagementService = errorReportingManagementService;
        this.hospitalService = hospitalService;
        init();
    }

    protected void init()
    {
        container = buildContainer();
        this.searchResultsTable = new FilterTable();
        this.searchResultsTable.setFilterBarVisible(true);
        this.searchResultsTable.setSizeFull();
        this.searchResultsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
        this.searchResultsTable.addStyleName(ValoTheme.TABLE_SMALL);
        this.searchResultsTable.addStyleName("ikasan");
        this.searchResultsTable.setContainerDataSource(container);
        this.searchResultsTable.addStyleName("wordwrap-table");

        this.searchResultsTable.setColumnExpandRatio("", .05f);
        this.searchResultsTable.setColumnExpandRatio("Module Name", .32f);
        this.searchResultsTable.setColumnExpandRatio("Component Name", .32f);
        this.searchResultsTable.setColumnExpandRatio("Flow Name", .32f);
        this.searchResultsTable.setColumnExpandRatio("Event Id / Error URI", .45f);
        this.searchResultsTable.setColumnExpandRatio("Details", .70f);
        this.searchResultsTable.setColumnExpandRatio("Time Stamp", .1f);

        this.searchResultsTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
        {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent)
            {
                if (itemClickEvent.isDoubleClick())
                {
                    IkasanSolrDocument ikasanSolrDocument = (IkasanSolrDocument) itemClickEvent.getItemId();

                    if(ikasanSolrDocument.getType().equals("wiretap"))
                    {
                        WiretapEvent event = wiretapService.getWiretapEvent(ikasanSolrDocument.getIdentifier());
                        WiretapPayloadViewWindow wiretapPayloadViewWindow = new WiretapPayloadViewWindow(event);
                        UI.getCurrent().addWindow(wiretapPayloadViewWindow);
                    }
                    else if(ikasanSolrDocument.getType().equals("error"))
                    {
                        ErrorOccurrence errorOccurrence = errorReportingService.find(ikasanSolrDocument.getErrorUri());
                        ErrorOccurrenceViewWindow errorOccurrenceViewWindow = new ErrorOccurrenceViewWindow(errorOccurrence,
                                platformConfigurationService);

                        UI.getCurrent().addWindow(errorOccurrenceViewWindow);
                    }
                    else if(ikasanSolrDocument.getType().equals("replay"))
                    {
                        ReplayEvent replayEvent = replayManagementService.getReplayEventById(new Long(ikasanSolrDocument.getId()));
                        ReplayEventViewWindow replayViewWindow = new ReplayEventViewWindow(replayEvent, (ReplayService) replayService,
                                platformConfigurationService, topologyService);

                        UI.getCurrent().addWindow(replayViewWindow);
                    }
                    else if(ikasanSolrDocument.getType().equals("exclusion"))
                    {
                        ExclusionEvent exclusionEvent = exclusionManagementService.find(ikasanSolrDocument.getId());
                        ErrorOccurrence errorOccurrence = errorReportingService.find(ikasanSolrDocument.getId());
                        ExclusionEventAction action = hospitalManagementService.getExclusionEventActionByErrorUri(ikasanSolrDocument.getId());
                        ExclusionEventViewWindow exclusionEventViewWindow = new ExclusionEventViewWindow(exclusionEvent, errorOccurrence
                                , action, hospitalManagementService, topologyService, (ErrorReportingManagementService) errorReportingManagementService, hospitalService);

                        UI.getCurrent().addWindow(exclusionEventViewWindow);
                    }

                }
            }
        });

        layout = new GridLayout(2, 4);
        layout.setWidth("100%");
        layout.setSpacing(true);
        layout.setMargin(true);

        layout.setColumnExpandRatio(0, .10f);
        layout.setColumnExpandRatio(1, .9f);


        Label configLabel = new Label("Ikasan Search");
        configLabel.addStyleName(ValoTheme.LABEL_HUGE);
        configLabel.setSizeUndefined();


        layout.addComponent(configLabel, 0, 0);

        GridLayout searchLayout = new GridLayout(3, 1);
        searchLayout.setHeight("100%");
        searchLayout.setWidth(400, Unit.PIXELS);
        searchLayout.setSpacing(true);

        final TextField searchField = new TextField();
        searchField.setWidth(350, Unit.PIXELS);

        searchLayout.addComponent(searchField, 0, 0);
        searchLayout.setComponentAlignment(searchField, Alignment.BOTTOM_CENTER);
        this.fromDate = new PopupDateField("From date");
        this.fromDate.setResolution(Resolution.MINUTE);
        this.fromDate.setValue(this.getMidnightToday());
        this.fromDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
        searchLayout.addComponent(this.fromDate, 1, 0);
        searchLayout.setComponentAlignment(this.fromDate, Alignment.BOTTOM_CENTER);
        this.toDate = new PopupDateField("To date");
        this.toDate.setResolution(Resolution.MINUTE);
        this.toDate.setValue(this.getTwentyThreeFixtyNineToday());
        this.toDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
        searchLayout.addComponent(this.toDate, 2, 0);
        searchLayout.setComponentAlignment(this.toDate, Alignment.BOTTOM_CENTER);

        layout.addComponent(searchLayout, 0, 1, 1, 1);
        layout.setComponentAlignment(searchLayout, Alignment.MIDDLE_CENTER);

        Button jiraButton = new Button();
        jiraButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        jiraButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        jiraButton.setIcon(AtlassianIcons.JIRA);
        jiraButton.setImmediate(true);
        jiraButton.setDescription("Export JIRA table");

        jiraButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
//                createJiraTable();
            }
        });

        selectAllButton = new Button();
        selectAllButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        selectAllButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
        selectAllButton.setImmediate(true);
        selectAllButton.setDescription("Select / deselect all records below.");

        selectAllButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                Collection<IkasanSolrDocument> items = (Collection<IkasanSolrDocument>)container.getItemIds();

                Resource r = selectAllButton.getIcon();

                if(r.equals(VaadinIcons.CHECK_SQUARE_O))
                {
                    selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE);

                    for(IkasanSolrDocument eo: items)
                    {
                        Item item = container.getItem(eo);

                        CheckBox cb = (CheckBox)item.getItemProperty("Select").getValue();

                        if(cb != null)
                        {
                            cb.setValue(true);
                        }
                    }
                }
                else
                {
                    selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);

                    for(IkasanSolrDocument eo: items)
                    {
                        Item item = container.getItem(eo);

                        CheckBox cb = (CheckBox)item.getItemProperty("Select").getValue();

                        if(cb != null)
                        {
                            cb.setValue(false);
                        }
                    }
                }
            }
        });

        bulkReplayButton = new Button();
        bulkReplayButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        bulkReplayButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        bulkReplayButton.setIcon(VaadinIcons.RECYCLE);
        bulkReplayButton.setImmediate(true);
        bulkReplayButton.setDescription("Bulk replay selected events.");

        bulkReplayButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                ReplayStatusPanel panel = new ReplayStatusPanel(getReplayEvents(), (ReplayService) replayService, platformConfigurationService, topologyService);

                Window window = new Window("Replay Events");
                window.setHeight("80%");
                window.setWidth("80%");
                window.setModal(true);

                window.setContent(panel);

                UI.getCurrent().addWindow(window);
            }
        });

        GridLayout buttons = new GridLayout(2, 1);
        buttons.setWidth("25px");

        buttons.addComponent(selectAllButton);
        buttons.addComponent(bulkReplayButton);


        layout.addComponent(buttons, 1, 3);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        final Button searchButton = new Button("Search");
        searchButton.setDescription("Refresh jobs table");
        searchButton.addStyleName(ValoTheme.BUTTON_SMALL);

        searchButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
                search(searchField.getValue());
            }
        });


        layout.addComponent(searchButton, 0, 2, 1, 2);
        layout.setComponentAlignment(searchButton, Alignment.MIDDLE_CENTER);

        VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();
        verticalSplitPanel.setSizeFull();

        verticalSplitPanel.setSplitPosition(195, Unit.PIXELS);
        verticalSplitPanel.setLocked(true);

        verticalSplitPanel.setFirstComponent(layout);

        HorizontalLayout tableLayout = new HorizontalLayout();
        tableLayout.setSizeFull();
        tableLayout.addComponent(this.searchResultsTable);

        verticalSplitPanel.setSecondComponent(tableLayout);

        this.setSizeFull();
        this.setContent(verticalSplitPanel);
    }

    /**
     * Helper method to resubmit all selected replay events.
     */
    protected List<ReplayEvent> getReplayEvents()
    {
        Collection<IkasanSolrDocument> items = (Collection<IkasanSolrDocument>)container.getItemIds();

        final List<ReplayEvent> myItems = new ArrayList<ReplayEvent>();

        for(IkasanSolrDocument ikasanSolrDocument: items)
        {
            if(ikasanSolrDocument.getType().equals("replay"))
            {
                Item item = container.getItem(ikasanSolrDocument);

                CheckBox cb = (CheckBox) item.getItemProperty("Select").getValue();

                if (cb.getValue() == true)
                {
                    myItems.add(replayManagementService.getReplayEventById(new Long(ikasanSolrDocument.getId())));
                }
            }
        }

        // We need to sort so that we can resubmit the oldest events first!
        Comparator<ReplayEvent> comparator = new Comparator<ReplayEvent>()
        {
            public int compare(ReplayEvent c1, ReplayEvent c2)
            {
                if (c2.getTimestamp() < c1.getTimestamp())
                {
                    return 1;
                }
                else if (c1.getTimestamp() < c2.getTimestamp())
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        };

        Collections.sort(myItems, comparator);

        return myItems;
    }

    protected IndexedContainer buildContainer()
    {
        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("", Button.class,  null);
        cont.addContainerProperty("Module Name", String.class,  null);
        cont.addContainerProperty("Flow Name", String.class,  null);
        cont.addContainerProperty("Component Name", String.class,  null);
        cont.addContainerProperty("Event Id / Error URI", String.class,  null);
        cont.addContainerProperty("Details", String.class,  null);
        cont.addContainerProperty("Timestamp", String.class,  null);
        cont.addContainerProperty("Select", CheckBox.class,  null);

        return cont;
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent)
    {
        IkasanAuthentication ikasanAuthentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        if(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_REPLAY_WRITE)
                || ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            this.selectAllButton.setVisible(true);
            this.bulkReplayButton.setVisible(true);
        }
        else
        {
            this.selectAllButton.setVisible(false);
            this.bulkReplayButton.setVisible(false);
        }
    }

    private void search(String searchString)
    {
        this.container.removeAllItems();

        IkasanAuthentication ikasanAuthentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        List<Long> moduleIds = ikasanAuthentication.getLinkedModuleIds();

        HashSet<String> moduleNames = new HashSet<>();

        if(this.modules == null)
        {
            this.modules = topologyService.getAllModules();
        }

        for(Module module: modules)
        {
            if(moduleIds.contains(module.getId()))
            {
                moduleNames.add(module.getName());
            }
        }

        List<Long> flowIds = ikasanAuthentication.getLinkedFlowIds();

        if(this.flows == null)
        {
            this.flows = topologyService.getAllFlows();
        }

        HashSet<String> flowNames = new HashSet<>();

        for(Flow flow: flows)
        {
            if(flowIds.contains(flow.getId()))
            {
                flowNames.add(flow.getName());
            }
        }

        if(moduleNames.isEmpty() && flowNames.isEmpty() && !ikasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_WRITE)
                && !ikasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_ADMIN)
                && !ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            flowNames.add("no flow name");
            moduleNames.add("no module name");
        }

        ArrayList<String> types = new ArrayList<>();

        if(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_REPLAY_WRITE)
                || ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            types.add(SolrReplayDao.REPLAY);
        }

        if(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_WRITE)
                || ikasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_ADMIN)
                || ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            types.add(SolrExclusionEventDao.EXCLUSION);
            types.add(SolrWiretapDao.WIRETAP);
            types.add(SolrErrorReportingServiceDao.ERROR);
        }

        if(types.isEmpty())
        {
            types.add("DUMMY");
        }

        if(this.solrUsername == null || this.solrUsername.isEmpty())
        {
            this.solrUsername = this.platformConfigurationService.getSolrUsername();
        }

        if(this.solrPassword == null || this.solrPassword.isEmpty())
        {
            this.solrPassword = this.platformConfigurationService.getSolrPassword();
        }

        this.solrSearchService.setSolrUsername(this.solrUsername);
        this.solrSearchService.setSolrPassword(this.solrPassword);
        this.wiretapService.setSolrUsername(this.solrUsername);
        this.wiretapService.setSolrPassword(this.solrPassword);
        this.errorReportingService.setSolrUsername(this.solrUsername);
        this.errorReportingService.setSolrPassword(this.solrPassword);
        this.replayManagementService.setSolrUsername(this.solrUsername);
        this.replayManagementService.setSolrPassword(this.solrPassword);
        this.exclusionManagementService.setSolrUsername(this.solrUsername);
        this.exclusionManagementService.setSolrPassword(this.solrPassword);

        IkasanSolrDocumentSearchResults results = this.solrSearchService.search(moduleNames, flowNames, searchString,
                this.fromDate.getValue().getTime(), this.toDate.getValue().getTime(),
                platformConfigurationService.getSearchResultSetSize(), types);

        if(results == null || results.getResultList().size() == 0)
        {
            Notification.show("The Ikasan search returned no results!", Notification.Type.ERROR_MESSAGE);

            layout.removeComponent(this.resultsLabel);
            resultsLabel = new Label("Number of records returned: 0 of 0");
            layout.addComponent(this.resultsLabel, 0, 3);

            return;
        }

        layout.removeComponent(this.resultsLabel);
        resultsLabel = new Label("Number of records returned: "
                + results.getResultList().size() + " of " + results.getTotalNumberOfResults()
                + " in " + results.getQueryResponseTime() + " milliseconds.");
        resultsLabel.setWidth("100%");

        if(results.getTotalNumberOfResults() > platformConfigurationService.getSearchResultSetSize())
        {
            Notification notif = new Notification(
                    "Warning",
                    "The number of results returned by this search exceeds the configured search " +
                            "result size of " + platformConfigurationService.getSearchResultSetSize() + " records. " +
                            "You can narrow the search with a filter or by being more accurate with the date and time range. ",
                    Notification.Type.HUMANIZED_MESSAGE);
            notif.setDelayMsec(-1);
            notif.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
            notif.setPosition(Position.MIDDLE_CENTER);

            notif.show(Page.getCurrent());
        }

        layout.addComponent(resultsLabel, 0, 3);

        for(IkasanSolrDocument doc: results.getResultList())
        {
            Item item = this.container.addItem(doc);

            Date date = new Date(doc.getTimestamp());
            SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
            String timestamp = format.format(date);

            item.getItemProperty("Module Name").setValue(doc.getModuleName());
            item.getItemProperty("Flow Name").setValue(doc.getFlowName());
            item.getItemProperty("Component Name").setValue(doc.getComponentName());

            if(doc.getType().equals("error"))
            {
                item.getItemProperty("Event Id / Error URI").setValue(doc.getErrorUri());
                item.getItemProperty("Details").setValue((doc.getErrorDetail().length() > 150) ? doc.getErrorDetail().substring(0, 150) : doc.getErrorDetail());
            }
            else if(doc.getType().equals("wiretap") || doc.getType().equals("exclusion") || doc.getType().equals("replay"))
            {
                item.getItemProperty("Event Id / Error URI").setValue(doc.getEventId());
                item.getItemProperty("Details").setValue((doc.getEvent().length() > 150) ? doc.getEvent().substring(0, 150) : doc.getEvent());
            }

            item.getItemProperty("Timestamp").setValue(timestamp);

            Button icon = new Button();
            icon.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            icon.addStyleName(ValoTheme.BUTTON_BORDERLESS);

            if(doc.getType().equals("wiretap"))
            {
                icon.setIcon(VaadinIcons.BOLT);
            }
            else if(doc.getType().equals("error"))
            {
                icon.setIcon(VaadinIcons.EXCLAMATION);
            }
            else if(doc.getType().equals("exclusion"))
            {
                icon.setIcon(VaadinIcons.DOCTOR);
                item.getItemProperty("Component Name").setValue("Not applicable");
            }
            else if(doc.getType().equals("replay"))
            {
                icon.setIcon(VaadinIcons.RECYCLE);
                item.getItemProperty("Component Name").setValue("Not applicable");
            }


            item.getItemProperty("").setValue(icon);

            if(doc.getType().equals("replay"))
            {
                CheckBox cb = new CheckBox();
                cb.setImmediate(true);
                cb.setDescription("Select in order to bulk replay.");

                item.getItemProperty("Select").setValue(cb);
            }
        }
    }

    protected Date getMidnightToday()
    {
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date.getTime();
    }

    protected Date getTwentyThreeFixtyNineToday()
    {
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date.getTime();
    }
}
