package org.ikasan.dashboard.ui.harvesting.panel;


import com.vaadin.data.Validator;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.harvesting.HarvestingSchedulerService;
import org.ikasan.dashboard.harvesting.SolrHarvestingJob;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
import org.ikasan.dashboard.ui.framework.validator.QuartzCronExpressionValidator;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;

/**
 * Created by Ikasan Development Team on 24/08/2016.
 */
public class HarvestingJobManagementPanel extends Panel
{
    private SolrHarvestingJob harvestingJob;
    private PlatformConfigurationService platformConfigurationService;
    private HarvestingSchedulerService harvestingSchedulerService;

    public HarvestingJobManagementPanel(SolrHarvestingJob harvestingJob, PlatformConfigurationService platformConfigurationService,
                                        HarvestingSchedulerService harvestingSchedulerService)
    {
        this.harvestingJob = harvestingJob;
        this.platformConfigurationService = platformConfigurationService;
        this.harvestingSchedulerService = harvestingSchedulerService;

        init();
    }

    public void init()
    {
        this.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);

        layout.addComponent( createHousekeepingJobManagementPanel());

        this.setContent(layout);
    }

    protected Panel createHousekeepingJobManagementPanel()
    {
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setStyleName("dashboard");

        GridLayout layout = new GridLayout(2, 7);
        layout.setWidth("100%");
        layout.setSpacing(true);
        layout.setColumnExpandRatio(0, 0.2f);
        layout.setColumnExpandRatio(1, 0.8f);

        Label wiretapDetailsLabel = new Label("Harvesting Job Details");
        wiretapDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
        layout.addComponent(wiretapDetailsLabel);


        Label moduleNameLabel = new Label("Harvesting Job Name:");
        moduleNameLabel.setSizeUndefined();

        layout.addComponent(moduleNameLabel, 0, 1);
        layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);

        final TextField tf1 = new TextField();
        tf1.setValue(this.harvestingJob.getJobName());
        tf1.setReadOnly(true);
        tf1.setWidth("80%");
        layout.addComponent(tf1, 1, 1);

        Label flowNameLabel = new Label("Cron Expression:");
        flowNameLabel.setSizeUndefined();

        layout.addComponent(flowNameLabel, 0, 2);
        layout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);

        final TextField tf2 = new TextField();
        tf2.setValue(this.harvestingJob.getCronExpression());
        tf2.addValidator(new QuartzCronExpressionValidator("Invalid cron expression!"));
        tf2.setReadOnly(false);
        tf2.setWidth("80%");
        tf2.setValidationVisible(false);
        layout.addComponent(tf2, 1, 2);


        Label dateTimeLabel = new Label("Harvest size:");
        dateTimeLabel.setSizeUndefined();

        layout.addComponent(dateTimeLabel, 0, 3);
        layout.setComponentAlignment(dateTimeLabel, Alignment.MIDDLE_RIGHT);

        final TextField tf4 = new TextField();
        tf4.setValue(this.harvestingJob.getHarvestSize().toString());
        tf4.addValidator(new NonZeroLengthStringValidator("You must enter a harvest size!"));
        tf4.setReadOnly(false);
        tf4.setValidationVisible(false);
        tf4.setWidth("80%");
        layout.addComponent(tf4, 1, 3);


        Label eventIdLabel = new Label("Number of harvesting threads:");
        eventIdLabel.setSizeUndefined();

        layout.addComponent(eventIdLabel, 0, 4);
        layout.setComponentAlignment(eventIdLabel, Alignment.MIDDLE_RIGHT);

        final TextField tf5 = new TextField();
        tf5.setValue(this.harvestingJob.getThreadCount().toString());
        tf5.addValidator(new NonZeroLengthStringValidator("You must enter the number of harvesting threads!"));
        tf5.setReadOnly(false);
        tf5.setValidationVisible(false);
        tf5.setWidth("80%");
        layout.addComponent(tf5, 1, 4);

        Label enabledLabel = new Label("Enabled:");
        enabledLabel.setSizeUndefined();

        layout.addComponent(enabledLabel, 0, 5);
        layout.setComponentAlignment(enabledLabel, Alignment.MIDDLE_RIGHT);

        final CheckBox enabledCheckbox = new CheckBox();
        enabledCheckbox.setValue(harvestingJob.isEnabled());
        layout.addComponent(enabledCheckbox, 1, 5);

        final Button saveButton = new Button("Save");

        saveButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                try
                {
                    tf1.validate();
                    tf2.validate();
                    tf4.validate();
                    tf5.validate();
                }
                catch (Validator.InvalidValueException e)
                {
                    tf1.setValidationVisible(true);
                    tf2.setValidationVisible(true);
                    tf4.setValidationVisible(true);
                    tf5.setValidationVisible(true);
                    return;
                }

                harvestingJob.setCronExpression(tf2.getValue());
                harvestingJob.setHarvestSize(new Integer(tf4.getValue()));
                harvestingJob.setThreadCount(new Integer(tf5.getValue()));
                harvestingJob.setEnabled(enabledCheckbox.getValue());
                harvestingJob.save();

                if(harvestingJob.isEnabled())
                {
                    harvestingSchedulerService.addJob(harvestingJob.getJobName());
                }
                else
                {
                    harvestingSchedulerService.removeJob(harvestingJob.getJobName());
                }

                harvestingJob.init();
                
                Window window = (Window)getParent();
                window.close();
            }
        });

        final IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
                authentication.hasGrantedAuthority(SecurityConstants.HARVESTING_ADMIN)
                || authentication.hasGrantedAuthority(SecurityConstants.HARVESTING_WRITE))
        {
            saveButton.setVisible(true);
        }
        else
        {
            saveButton.setVisible(false);
        }

        layout.addComponent(saveButton, 0, 6, 1, 6);
        layout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);

        panel.setContent(layout);

        return panel;
    }
}
