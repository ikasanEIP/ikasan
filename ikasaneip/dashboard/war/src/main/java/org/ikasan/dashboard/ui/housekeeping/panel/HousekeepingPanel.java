package org.ikasan.dashboard.ui.housekeeping.panel;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;
import org.ikasan.dashboard.housekeeping.HousekeepingJob;
import org.ikasan.dashboard.housekeeping.HousekeepingSchedulerService;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.housekeeping.window.HousekeepingJobManagementWindow;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.monitor.component.MonitorIcons;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.quartz.*;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ikasan Development Team on 22/08/2016.
 */
public class HousekeepingPanel extends Panel implements View
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(HousekeepingPanel.class);

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    private ScheduledJobFactory scheduledJobFactory;

    private HousekeepingSchedulerService housekeepingSchedulerService;

    private List<JobDetail> houseKeepingJobDetails;
    private Map<String, HousekeepingJob> houseKeepingJobs;

    private FilterTable housekeepingTable;

    private IndexedContainer container = null;

    private Label controlLabel;
    private Button startButton;
    private Button stopButton;

    public HousekeepingPanel(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                             List<HousekeepingJob> houseKeepingJobs, HousekeepingSchedulerService housekeepingSchedulerService)
    {
        this.scheduler = scheduler;
        if(this.scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be null!");
        }
        this.scheduledJobFactory = scheduledJobFactory;
        if(this.scheduledJobFactory == null)
        {
            throw new IllegalArgumentException("scheduledJobFactory cannot be null!");
        }
        this.housekeepingSchedulerService = housekeepingSchedulerService;
        if(this.housekeepingSchedulerService == null)
        {
            throw new IllegalArgumentException("housekeepingSchedulerService cannot be null!");
        }

        this.houseKeepingJobs = new HashMap<String, HousekeepingJob>();
        this.houseKeepingJobDetails = new ArrayList<JobDetail>();

        for(HousekeepingJob job: houseKeepingJobs)
        {
            JobDetail jobDetail = this.scheduledJobFactory.createJobDetail
                    (job, HousekeepingJob.class, job.getJobName(), "housekeeping");

            houseKeepingJobDetails.add(jobDetail);
            this.houseKeepingJobs.put(jobDetail.getKey().toString(), job);
        }

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

        this.housekeepingTable.setColumnExpandRatio("Housekeeping Job Name", .32f);
        this.housekeepingTable.setColumnExpandRatio("Cron Expression", .32f);
        this.housekeepingTable.setColumnExpandRatio("Previous execution time", .32f);
        this.housekeepingTable.setColumnExpandRatio("Next execution time", .32f);
        this.housekeepingTable.setColumnExpandRatio("Batch delete size", .32f);
        this.housekeepingTable.setColumnExpandRatio("Transaction delete size", .32f);
        this.housekeepingTable.setColumnExpandRatio("Last Execution Status", .1f);
        this.housekeepingTable.setColumnExpandRatio("Enabled", .1f);

        this.housekeepingTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
        {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent)
            {
                if (itemClickEvent.isDoubleClick())
                {
                    final HousekeepingJobManagementWindow housekeepingJobManagementWindow = new HousekeepingJobManagementWindow((HousekeepingJob)itemClickEvent.getItemId(),
                            housekeepingSchedulerService);
                    UI.getCurrent().addWindow(housekeepingJobManagementWindow);

                    housekeepingJobManagementWindow.addCloseListener(new Window.CloseListener()
                    {
                        @Override
                        public void windowClose(Window.CloseEvent e)
                        {
                            refresh();
                        }
                    });
                }
            }
        });

        final GridLayout layout = new GridLayout(2, 6);
        layout.setWidth("100%");
        layout.setSpacing(true);
        layout.setMargin(true);

        layout.setColumnExpandRatio(0, .10f);
        layout.setColumnExpandRatio(1, .9f);


        Label configLabel = new Label("Housekeeping");
        configLabel.addStyleName(ValoTheme.LABEL_HUGE);
        configLabel.setSizeUndefined();


        Label statusLabel = new Label("Status:");
        statusLabel.addStyleName(ValoTheme.LABEL_LARGE);
        statusLabel.setSizeUndefined();

        final MonitorIcons runningIcon = MonitorIcons.CHECK_CIRCLE_O;
        runningIcon.setSizePixels(20);
        runningIcon.setColor("green");

        final MonitorIcons stoppedIcon = MonitorIcons.EXCLAMATION_CIRCLE_O;
        stoppedIcon.setSizePixels(20);
        stoppedIcon.setColor("red");

        final Label statusIconLabel = new Label();
        statusIconLabel.setCaptionAsHtml(true);

        layout.addComponent(configLabel, 0, 0);
        layout.addComponent(statusLabel, 0, 1);
        layout.addComponent(statusIconLabel, 1, 1);
        layout.setComponentAlignment(statusIconLabel, Alignment.MIDDLE_LEFT);

        try
        {
            if (this.scheduler.isStarted())
            {
                statusIconLabel.setCaption(runningIcon.getHtml());
            }
            else
            {
                statusIconLabel.setCaption(stoppedIcon.getHtml());
            }
        }
        catch (Exception e)
        {
            logger.warn("Cannot determine if scheduler is running!");
        }


        controlLabel = new Label("Schedular Control:");
        controlLabel.addStyleName(ValoTheme.LABEL_LARGE);
        controlLabel.setSizeUndefined();

        startButton = new Button("Start");
        startButton.setDescription("Start the scheduler");
        startButton.addStyleName(ValoTheme.BUTTON_SMALL);

        stopButton = new Button("Stop");
        stopButton.setDescription("Start the scheduler");
        stopButton.addStyleName(ValoTheme.BUTTON_SMALL);

        startButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                start();
                layout.removeComponent(startButton);
                layout.addComponent(stopButton, 1, 3);
                layout.setComponentAlignment(stopButton, Alignment.MIDDLE_LEFT);

                statusIconLabel.setCaption(runningIcon.getHtml());

                refresh();
            }
        });


        stopButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                stop();

                layout.removeComponent(stopButton);
                layout.addComponent(startButton, 1, 3);
                layout.setComponentAlignment(startButton, Alignment.MIDDLE_LEFT);

                statusIconLabel.setCaption(stoppedIcon.getHtml());

                refresh();
            }
        });


        try
        {
            if (this.scheduler.isStarted() && !this.scheduler.isInStandbyMode())
            {
                layout.addComponent(controlLabel, 0, 3);
                layout.addComponent(stopButton, 1, 3);
                layout.setComponentAlignment(stopButton, Alignment.MIDDLE_LEFT);
            }
            else
            {
                layout.addComponent(controlLabel, 0, 3);
                layout.addComponent(startButton, 1, 3);
                layout.setComponentAlignment(startButton, Alignment.MIDDLE_LEFT);
            }
        }
        catch (Exception e)
        {
            logger.warn("Cannot determine if scheduler is running!");
        }

        final Button refreshButton = new Button("Refresh");
        refreshButton.setDescription("Refresh jobs table");
        refreshButton.addStyleName(ValoTheme.BUTTON_SMALL);

        refreshButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                refresh();
            }
        });



        layout.addComponent(this.housekeepingTable, 0, 4, 1, 4);

        layout.addComponent(refreshButton, 0, 5, 1, 5);
        layout.setComponentAlignment(refreshButton, Alignment.MIDDLE_CENTER);

        this.setContent(layout);
    }

    protected IndexedContainer buildContainer()
    {
        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("Housekeeping Job Name", String.class,  null);
        cont.addContainerProperty("Cron Expression", String.class,  null);
        cont.addContainerProperty("Previous execution time", String.class,  null);
        cont.addContainerProperty("Next execution time", String.class,  null);
        cont.addContainerProperty("Batch delete size", Integer.class,  null);
        cont.addContainerProperty("Transaction delete size", Integer.class,  null);
        cont.addContainerProperty("Enabled", HorizontalLayout.class,  null);
        cont.addContainerProperty("Last Execution Status", HorizontalLayout.class,  null);

        return cont;
    }

    /**
     * Start the underlying tech
     */
    public void start()
    {
        try
        {
            this.scheduler.start();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException("Could not start house keeping scheduler!");
        }
    }

    public void stop()
    {
        try
        {
            this.scheduler.standby();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException("Could not shutdown house keeping scheduler!");
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent)
    {
        refresh();
    }

    private void refresh()
    {
        final IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
                authentication.hasGrantedAuthority(SecurityConstants.HOUSEKEEPING_ADMIN))
        {
            controlLabel.setVisible(true);
            startButton.setVisible(true);
            stopButton.setVisible(true);
        }
        else
        {
            controlLabel.setVisible(false);
            startButton.setVisible(false);
            stopButton.setVisible(false);
        }

        this.container.removeAllItems();

        for(JobDetail jobDetail: this.houseKeepingJobDetails)
        {
            HousekeepingJob job = this.houseKeepingJobs.get(jobDetail.getKey().toString());

            Item item = container.addItem(job);

            item.getItemProperty("Housekeeping Job Name").setValue(job.getJobName());
            item.getItemProperty("Cron Expression").setValue(job.getCronExpression());

            Date previousFireTime = null;
            Date nextFireTime = null;

            try
            {
                if(scheduler.getTriggersOfJob(jobDetail.getKey()).size() > 0)
                {
                    previousFireTime = scheduler.getTriggersOfJob(jobDetail.getKey()).get(0).getPreviousFireTime();
                    nextFireTime = scheduler.getTriggersOfJob(jobDetail.getKey()).get(0).getNextFireTime();
                }
            }
            catch (Exception e)
            {
                logger.warn("Could not get details from schedular!");
            }

            if (previousFireTime != null)
            {
                SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
                String timestamp = format.format(previousFireTime);
                item.getItemProperty("Previous execution time").setValue(timestamp);
            }

            if (nextFireTime != null)
            {
                SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
                String timestamp = format.format(nextFireTime);
                item.getItemProperty("Next execution time").setValue(timestamp);
            }

            item.getItemProperty("Batch delete size").setValue(job.getBatchDeleteSize());
            item.getItemProperty("Transaction delete size").setValue(job.getTransactionDeleteSize());

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);

            Label label = null;

            if(job.isEnabled())
            {
                label = new Label(VaadinIcons.CHECK.getHtml(), ContentMode.HTML);
            }
            else
            {
                label = new Label(VaadinIcons.BAN.getHtml(), ContentMode.HTML);
            }

            label.addStyleName(ValoTheme.LABEL_TINY);

            layout.addComponent(label);
            layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

            item.getItemProperty("Enabled").setValue(layout);

            HorizontalLayout statuslayout = new HorizontalLayout();
            layout.setSpacing(true);

            Label statusLabel = null;

            if(job.getLastExecutionSuccessful())
            {
                statusLabel = new Label(VaadinIcons.CHECK.getHtml(), ContentMode.HTML);
            }
            else
            {
                statusLabel = new Label(VaadinIcons.BAN.getHtml(), ContentMode.HTML);
                statusLabel.setDescription(job.getExecutionErrorMessage());
            }

            statusLabel.addStyleName(ValoTheme.LABEL_TINY);

            statuslayout.addComponent(statusLabel);
            statuslayout.setComponentAlignment(statusLabel, Alignment.MIDDLE_CENTER);

            item.getItemProperty("Last Execution Status").setValue(statuslayout);
        }
    }
}
