package org.ikasan.dashboard.ui.housekeeping.panel;


import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.housekeeping.HousekeepingJob;
import org.ikasan.dashboard.housekeeping.HousekeepingSchedulerService;
import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
import org.ikasan.dashboard.ui.framework.validator.QuartzCronExpressionValidator;
import org.ikasan.spec.configuration.PlatformConfigurationService;

/**
 * Created by Ikasan Development Team on 24/08/2016.
 */
public class HousekeepingJobManagementPanel extends Panel
{
    private HousekeepingJob housekeepingjob;
    private PlatformConfigurationService platformConfigurationService;
    private HousekeepingSchedulerService housekeepingSchedulerService;

    public HousekeepingJobManagementPanel(HousekeepingJob housekeepingjob, PlatformConfigurationService platformConfigurationService,
                                          HousekeepingSchedulerService housekeepingSchedulerService)
    {
        this.housekeepingjob = housekeepingjob;
        this.platformConfigurationService = platformConfigurationService;
        this.housekeepingSchedulerService = housekeepingSchedulerService;

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

        Label wiretapDetailsLabel = new Label("Housekeeping Job Details");
        wiretapDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
        layout.addComponent(wiretapDetailsLabel);


        Label moduleNameLabel = new Label("Housekeeping Job Name:");
        moduleNameLabel.setSizeUndefined();

        layout.addComponent(moduleNameLabel, 0, 1);
        layout.setComponentAlignment(moduleNameLabel, Alignment.MIDDLE_RIGHT);

        final TextField tf1 = new TextField();
        tf1.setValue(this.housekeepingjob.getJobName());
        tf1.setReadOnly(true);
        tf1.setWidth("80%");
        layout.addComponent(tf1, 1, 1);

        Label flowNameLabel = new Label("Cron Expression:");
        flowNameLabel.setSizeUndefined();

        layout.addComponent(flowNameLabel, 0, 2);
        layout.setComponentAlignment(flowNameLabel, Alignment.MIDDLE_RIGHT);

        final TextField tf2 = new TextField();
        tf2.setValue(this.housekeepingjob.getCronExpression());
        tf2.addValidator(new QuartzCronExpressionValidator("Invalid cron expression!"));
        tf2.setReadOnly(false);
        tf2.setWidth("80%");
        tf2.setValidationVisible(false);
        layout.addComponent(tf2, 1, 2);


        Label dateTimeLabel = new Label("Batch size:");
        dateTimeLabel.setSizeUndefined();

        layout.addComponent(dateTimeLabel, 0, 3);
        layout.setComponentAlignment(dateTimeLabel, Alignment.MIDDLE_RIGHT);

        final TextField tf4 = new TextField();
        tf4.setValue(this.housekeepingjob.getBatchDeleteSize().toString());
        tf4.addValidator(new NonZeroLengthStringValidator("You must enter a batch delete size!"));
        tf4.setReadOnly(false);
        tf4.setValidationVisible(false);
        tf4.setWidth("80%");
        layout.addComponent(tf4, 1, 3);


        Label eventIdLabel = new Label("Transaction Size:");
        eventIdLabel.setSizeUndefined();

        layout.addComponent(eventIdLabel, 0, 4);
        layout.setComponentAlignment(eventIdLabel, Alignment.MIDDLE_RIGHT);

        final TextField tf5 = new TextField();
        tf5.setValue(this.housekeepingjob.getTransactionDeleteSize().toString());
        tf5.addValidator(new NonZeroLengthStringValidator("You must enter a transaction delete size!"));
        tf5.setReadOnly(false);
        tf5.setValidationVisible(false);
        tf5.setWidth("80%");
        layout.addComponent(tf5, 1, 4);

        Label enabledLabel = new Label("Enabled:");
        enabledLabel.setSizeUndefined();

        layout.addComponent(enabledLabel, 0, 5);
        layout.setComponentAlignment(enabledLabel, Alignment.MIDDLE_RIGHT);

        final CheckBox enabledCheckbox = new CheckBox();
        enabledCheckbox.setValue(housekeepingjob.isEnabled());
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

                housekeepingjob.setCronExpression(tf2.getValue());
                housekeepingjob.setBatchDeleteSize(new Integer(tf4.getValue()));
                housekeepingjob.setTransactionDeleteSize(new Integer(tf5.getValue()));
                housekeepingjob.setEnabled(enabledCheckbox.getValue());
                housekeepingjob.save();

                if(housekeepingjob.isEnabled())
                {
                    housekeepingSchedulerService.addJob(housekeepingjob.getJobName());
                }
                else
                {
                    housekeepingSchedulerService.removeJob(housekeepingjob.getJobName());
                }

                housekeepingjob.init();
                
                Window window = (Window)getParent();
                window.close();
            }
        });

        layout.addComponent(saveButton, 0, 6, 1, 6);
        layout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);

        panel.setContent(layout);

        return panel;
    }
}
