package org.ikasan.dashboard.ui.housekeeping.window;


import com.vaadin.ui.Window;
import org.ikasan.dashboard.housekeeping.HousekeepingJob;
import org.ikasan.dashboard.housekeeping.HousekeepingSchedulerService;
import org.ikasan.dashboard.ui.housekeeping.panel.HousekeepingJobManagementPanel;
import org.ikasan.spec.configuration.PlatformConfigurationService;

/**
 * Created by Ikasan Development Team on 24/08/2016.
 */
public class HousekeepingJobManagementWindow extends Window
{
    private HousekeepingJob housekeepingjob;
    private PlatformConfigurationService platformConfigurationService;
    private HousekeepingSchedulerService housekeepingSchedulerService;

    /**
     * Constructor
     *
     * @param housekeepingjob
     */
    public HousekeepingJobManagementWindow(HousekeepingJob housekeepingjob,
                                           HousekeepingSchedulerService housekeepingSchedulerService)
    {
        super();
        this.housekeepingjob = housekeepingjob;
        if(this.housekeepingjob == null)
        {
            throw new IllegalArgumentException("housekeepingjob cannot be null!");
        }
        this.platformConfigurationService = housekeepingjob.getPlatformConfigurationService();
        if(this.platformConfigurationService == null)
        {
            throw new IllegalArgumentException("platformConfigurationService cannot be null!");
        }
        this.housekeepingSchedulerService = housekeepingSchedulerService;
        if(this.housekeepingSchedulerService == null)
        {
            throw new IllegalArgumentException("housekeepingSchedulerService cannot be null!");
        }

        this.init();
    }


    public void init()
    {
        this.setModal(true);
        this.setResizable(false);
        this.setHeight("300px");
        this.setWidth("500px");

        HousekeepingJobManagementPanel panel = new HousekeepingJobManagementPanel(this.housekeepingjob, this.platformConfigurationService,
                this.housekeepingSchedulerService);

        this.setContent(panel);
    }
}
