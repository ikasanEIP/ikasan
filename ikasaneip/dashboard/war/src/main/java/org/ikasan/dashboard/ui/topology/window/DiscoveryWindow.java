package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.discovery.DiscoverySchedulerService;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.systemevent.model.SystemEvent;
import org.ikasan.systemevent.service.SystemEventService;
import org.vaadin.teemu.VaadinIcons;

import java.text.SimpleDateFormat;

/**
 * Created by stewmi on 25/05/2017.
 */
public class DiscoveryWindow extends Window
{
    private DiscoverySchedulerService discoverySchedulerService;
    private SystemEventService systemEventService;
    private Table systemEventTable;
    private Label stateLabel = new Label();
    private Button discoverButton;

    public DiscoveryWindow(DiscoverySchedulerService discoverySchedulerService,
                           SystemEventService systemEventService)
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

        init();
    }

    public void init()
    {
        this.setWidth("600px");
        this.setHeight("400px");
        this.setModal(true);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

        GridLayout layout = new GridLayout(2, 4);
        layout.setWidth("100%");
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

                discoverySchedulerService.addJob(authentication);

                discoverButton.setEnabled(false);
                stateLabel.setValue("Running");
            }
        });


        systemEventTable = new Table();
        systemEventTable.setWidth("100%");
        systemEventTable.setHeight("200px");
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

        layout.addComponent(systemEventTable, 0, 2, 1, 2);
        layout.setComponentAlignment(systemEventTable, Alignment.MIDDLE_CENTER);

        layout.addComponent(discoverButton, 0, 3, 1, 3);
        layout.setComponentAlignment(discoverButton, Alignment.MIDDLE_CENTER);

        panel.setContent(layout);

        this.setContent(panel);
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
    }


}
