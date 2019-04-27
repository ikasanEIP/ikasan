package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.discovery.DiscoverySchedulerService;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ikasan Development Team on 25/05/2017.
 */
public class DiscoveryWindow extends Window
{
    private DiscoverySchedulerService discoverySchedulerService;
    private SystemEventService systemEventService;
    private TopologyService topologyService;
    private Table systemEventTable;
    private FilterTable serverTable;
    private Label stateLabel = new Label();
    private Button discoverButton;

    private IndexedContainer tableContainer;

    public DiscoveryWindow(DiscoverySchedulerService discoverySchedulerService,
                           SystemEventService systemEventService,
                           TopologyService topologyService)
    {
        super("Discovery");
        this.discoverySchedulerService = discoverySchedulerService;
        if(this.discoverySchedulerService == null)
        {
            throw new IllegalArgumentException("discoverySchedulerService cannot be null!");
        }
        this.systemEventService = systemEventService;
        if(this.systemEventService == null)
        {
            throw new IllegalArgumentException("discoverySchedulerService cannot be null!");
        }
        this.topologyService = topologyService;
        if(this.topologyService == null)
        {
            throw new IllegalArgumentException("topologyService cannot be null!");
        }

        init();
    }

    public void init()
    {
        this.setWidth("80%");
        this.setHeight("80%");
        this.setModal(true);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

        GridLayout layout = new GridLayout(2, 5);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);

        HorizontalLayout headingLayout = new HorizontalLayout();
        headingLayout.setWidth("100%");
        headingLayout.setHeight("40px");
        headingLayout.setSpacing(true);

        Label discoveryLabel = new Label("Module Discovery");
        discoveryLabel.setStyleName(ValoTheme.LABEL_HUGE);

        Button refreshButton = new Button();
        refreshButton.setIcon(VaadinIcons.REFRESH);
        refreshButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        refreshButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);

        headingLayout.addComponent(discoveryLabel);
        headingLayout.setComponentAlignment(discoveryLabel, Alignment.MIDDLE_LEFT);
        headingLayout.addComponent(refreshButton);
        headingLayout.setComponentAlignment(refreshButton, Alignment.MIDDLE_LEFT);

        layout.setRowExpandRatio(0, .025f);
        layout.setRowExpandRatio(1, .025f);
        layout.setRowExpandRatio(2, .45f);
        layout.setRowExpandRatio(3, .45f);
        layout.setRowExpandRatio(4, .05f);

        layout.addComponent(headingLayout, 0, 0, 1, 0);

        final Label statusLabel = new Label("Status");
        stateLabel = new Label();

        layout.addComponent(statusLabel, 0, 1);
        layout.addComponent(stateLabel, 1, 1);

        discoverButton = new Button("Discover");
        discoverButton.setStyleName(ValoTheme.BUTTON_SMALL);

        discoverButton.addClickListener(new Button.ClickListener()
        {
            @SuppressWarnings("unchecked")
            public void buttonClick(Button.ClickEvent event)
            {
                final IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                        .getAttribute(DashboardSessionValueConstants.USER);

                List<Server> servers = getServers();

                if(servers.isEmpty())
                {
                    Notification.show("At least one server should be selected!", Notification.Type.HUMANIZED_MESSAGE);
                    return;
                }

                discoverySchedulerService.addJob(servers, authentication);

                discoverButton.setEnabled(false);
                stateLabel.setValue("Running");
            }
        });

        this.tableContainer = buildContainer();
        this.serverTable = new FilterTable();
        this.serverTable.setFilterBarVisible(true);
        this.serverTable.setContainerDataSource(this.tableContainer);
        this.serverTable.setWidth("100%");
        this.serverTable.setHeight("100%");
        this.serverTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
        this.serverTable.setColumnExpandRatio("Server Name", .3f);
        this.serverTable.setColumnExpandRatio("Server Description", .3f);
        this.serverTable.setColumnExpandRatio("URL", .2f);
        this.serverTable.setColumnExpandRatio("Port", .15f);
        this.serverTable.setColumnExpandRatio("", .05f);

        this.serverTable.setStyleName("wordwrap-table");

        layout.addComponent(serverTable, 0, 2, 1, 2);
        layout.setComponentAlignment(serverTable, Alignment.MIDDLE_CENTER);


        systemEventTable = new Table();
        systemEventTable.setWidth("100%");
        systemEventTable.setHeight("100%");
        systemEventTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
        systemEventTable.addContainerProperty("Subject", String.class,  null);
        systemEventTable.setColumnExpandRatio("Subject", .3f);
        systemEventTable.addContainerProperty("Action", String.class,  null);
        systemEventTable.setColumnExpandRatio("Action", .4f);
        systemEventTable.addContainerProperty("Actioned By", String.class,  null);
        systemEventTable.setColumnExpandRatio("Actioned By", .15f);
        systemEventTable.addContainerProperty("Timestamp", String.class,  null);
        systemEventTable.setColumnExpandRatio("Timestamp", .15f);

        systemEventTable.setStyleName("wordwrap-table");

        refreshButton.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent)
            {
                if(discoverySchedulerService.isRunnung())
                {
                    stateLabel.setValue("Running");
                }
                else
                {
                    stateLabel.setValue("Stopped");
                }

                if(discoverySchedulerService.isRunnung())
                {
                    discoverButton.setEnabled(false);
                }
                else
                {
                    discoverButton.setEnabled(true);
                }

                populate();
            }
        });

        layout.addComponent(systemEventTable, 0, 3, 1, 3);
        layout.setComponentAlignment(systemEventTable, Alignment.MIDDLE_CENTER);

        layout.addComponent(discoverButton, 0, 4, 1, 4);
        layout.setComponentAlignment(discoverButton, Alignment.MIDDLE_CENTER);

        panel.setContent(layout);

        this.setContent(panel);
    }

    protected IndexedContainer buildContainer()
    {
        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("Server Name", String.class,  null);
        cont.addContainerProperty("Server Description", String.class,  null);
        cont.addContainerProperty("URL", String.class,  null);
        cont.addContainerProperty("Port", String.class,  null);
        cont.addContainerProperty("", CheckBox.class,  null);

        return cont;
    }

    public void populate()
    {
        if(this.discoverySchedulerService.isRunnung())
        {
            discoverButton.setEnabled(false);
        }
        else
        {
            discoverButton.setEnabled(true);
        }

        if(discoverySchedulerService.isRunnung())
        {
            stateLabel.setValue("Running");
        }
        else
        {
            stateLabel.setValue("Stopped");
        }

        systemEventTable.removeAllItems();

        PagedSearchResult<SystemEvent> systemEvents = systemEventService.listSystemEvents(0, 100, "timestamp", true, "Discovery", null, null
                , null, null);

        for(SystemEvent systemEvent: systemEvents.getPagedResults())
        {
            SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
            String timestamp = format.format(systemEvent.getTimestamp());

            systemEventTable.addItem(new Object[]{systemEvent.getSubject(), systemEvent.getAction()
                    , systemEvent.getActor(), timestamp}, systemEvent);
        }

        this.tableContainer.removeAllItems();

        List<Server> servers = this.topologyService.getAllServers();

        servers.forEach(server ->
            {
                Item item = tableContainer.addItem(server);

                item.getItemProperty("Server Name").setValue(server.getName());
                item.getItemProperty("Server Description").setValue(server.getDescription());
                item.getItemProperty("URL").setValue(server.getUrl());
                item.getItemProperty("Port").setValue(server.getPort().toString());

                CheckBox cb = new CheckBox();
                cb.setImmediate(true);
                cb.setDescription("Select to discover server.");

                item.getItemProperty("").setValue(cb);
            }
        );
    }

    /**
     * Helper method to resubmit all selected excluded events.
     */
    protected List<Server> getServers()
    {
        Collection<Server> items = (Collection<Server>)tableContainer.getItemIds();

        final List<Server> myItems = new ArrayList<>(items);

        for(Server server: items)
        {
            Item item = tableContainer.getItem(server);

            CheckBox cb = (CheckBox)item.getItemProperty("").getValue();

            if(cb.getValue() == false)
            {
                myItems.remove(server);
            }
        }

        return myItems;
    }
}
