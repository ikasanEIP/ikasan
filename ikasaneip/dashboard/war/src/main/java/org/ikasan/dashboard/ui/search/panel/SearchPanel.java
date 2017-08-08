package org.ikasan.dashboard.ui.search.panel;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.icons.AtlassianIcons;
import org.ikasan.dashboard.ui.housekeeping.panel.HousekeepingPanel;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.search.window.ErrorOccurrenceViewWindow;
import org.ikasan.dashboard.ui.topology.window.WiretapPayloadViewWindow;
import org.ikasan.error.reporting.dao.ErrorReportingServiceDao;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.solr.dao.SolrGeneralSearchDao;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.dao.WiretapDao;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SearchPanel extends Panel implements View
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(HousekeepingPanel.class);

    private FilterTable housekeepingTable;
    private IndexedContainer container = null;
    private SolrGeneralSearchDao<IkasanSolrDocumentSearchResults> dao = null;
    private Label resultsLabel = new Label();
    private GridLayout layout = null;
    private PlatformConfigurationService platformConfigurationService;
    private PopupDateField fromDate;
    private PopupDateField toDate;
    private WiretapDao wiretapDao;
    private ErrorReportingServiceDao<ErrorOccurrence<byte[]>, String> errorReportingServiceDao;


    public SearchPanel(SolrGeneralSearchDao dao, PlatformConfigurationService platformConfigurationService,
                       WiretapDao wiretapDao, ErrorReportingServiceDao<ErrorOccurrence<byte[]>, String> errorReportingServiceDao)
    {
        this.dao = dao;
        this.platformConfigurationService =platformConfigurationService;
        this.wiretapDao = wiretapDao;
        this.errorReportingServiceDao = errorReportingServiceDao;
        init();
    }

    protected void init()
    {
        container = buildContainer();
        this.housekeepingTable = new FilterTable();
        this.housekeepingTable.setFilterBarVisible(true);
        this.housekeepingTable.setSizeFull();
        this.housekeepingTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
        this.housekeepingTable.addStyleName(ValoTheme.TABLE_SMALL);
        this.housekeepingTable.addStyleName("ikasan");
        this.housekeepingTable.setContainerDataSource(container);
        this.housekeepingTable.addStyleName("wordwrap-table");

        this.housekeepingTable.setColumnExpandRatio("", .05f);
        this.housekeepingTable.setColumnExpandRatio("Module Name", .32f);
        this.housekeepingTable.setColumnExpandRatio("Component Name", .32f);
        this.housekeepingTable.setColumnExpandRatio("Flow Name", .32f);
        this.housekeepingTable.setColumnExpandRatio("Event Id / Error URI", .45f);
        this.housekeepingTable.setColumnExpandRatio("Details", .70f);
        this.housekeepingTable.setColumnExpandRatio("Time Stamp", .1f);

        this.housekeepingTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
        {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent)
            {
                if (itemClickEvent.isDoubleClick())
                {
                    IkasanSolrDocument ikasanSolrDocument = (IkasanSolrDocument) itemClickEvent.getItemId();

                    if(ikasanSolrDocument.getType().equals("wiretap"))
                    {
                        WiretapEvent event = wiretapDao.findById(ikasanSolrDocument.getIdentifier());
                        WiretapPayloadViewWindow wiretapPayloadViewWindow = new WiretapPayloadViewWindow(event);
                        UI.getCurrent().addWindow(wiretapPayloadViewWindow);
                    }
                    else if(ikasanSolrDocument.getType().equals("error"))
                    {
                        ErrorOccurrence errorOccurrence = errorReportingServiceDao.find(ikasanSolrDocument.getErrorUri());
                        ErrorOccurrenceViewWindow errorOccurrenceViewWindow = new ErrorOccurrenceViewWindow(errorOccurrence,
                                platformConfigurationService);

                        UI.getCurrent().addWindow(errorOccurrenceViewWindow);
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

        GridLayout buttons = new GridLayout(1, 1);
        buttons.setWidth("25px");

        buttons.addComponent(jiraButton);

        layout.addComponent(buttons, 1, 3);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

        final Button searchButton = new Button("Search");
        searchButton.setDescription("Refresh jobs table");
        searchButton.addStyleName(ValoTheme.BUTTON_SMALL);

        searchButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
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
        tableLayout.addComponent(this.housekeepingTable);

        verticalSplitPanel.setSecondComponent(tableLayout);

        this.setSizeFull();
        this.setContent(verticalSplitPanel);
    }

    protected IndexedContainer buildContainer()
    {
        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("", Button.class,  null);
        cont.addContainerProperty("Module Name", String.class,  null);
        cont.addContainerProperty("Component Name", String.class,  null);
        cont.addContainerProperty("Flow Name", String.class,  null);
        cont.addContainerProperty("Event Id / Error URI", String.class,  null);
        cont.addContainerProperty("Details", String.class,  null);
        cont.addContainerProperty("Timestamp", String.class,  null);

        return cont;
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent)
    {
    }

    private void search(String searchString)
    {
        this.container.removeAllItems();

        IkasanSolrDocumentSearchResults results = this.dao.search(searchString,
                this.fromDate.getValue().getTime(), this.toDate.getValue().getTime(),
                platformConfigurationService.getSearchResultSetSize());

        if(results == null || results.getResultList().size() == 0)
        {
            Notification.show("The ikasan search returned no results!", Notification.Type.ERROR_MESSAGE);

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
            else if(doc.getType().equals("wiretap"))
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
            if(doc.getType().equals("error"))
            {
                icon.setIcon(VaadinIcons.EXCLAMATION);
            }


            item.getItemProperty("").setValue(icon);
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
