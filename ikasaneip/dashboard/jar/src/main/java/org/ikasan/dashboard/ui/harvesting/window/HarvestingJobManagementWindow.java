package org.ikasan.dashboard.ui.harvesting.window;


import com.vaadin.ui.Window;
import org.ikasan.dashboard.harvesting.HarvestingSchedulerService;
import org.ikasan.dashboard.harvesting.SolrHarvestingJob;
import org.ikasan.dashboard.ui.harvesting.panel.HarvestingJobManagementPanel;
import org.ikasan.spec.configuration.PlatformConfigurationService;

/**
 * Created by Ikasan Development Team on 08/08/2017.
 */
public class HarvestingJobManagementWindow extends Window
{
    private SolrHarvestingJob harvestingJob;
    private PlatformConfigurationService platformConfigurationService;
    private HarvestingSchedulerService harvestingSchedulerService;

    /**
     * Constructor
     *
     * @param harvestingJob
     * @param harvestingSchedulerService
     */
    public HarvestingJobManagementWindow(SolrHarvestingJob harvestingJob,
                                         HarvestingSchedulerService harvestingSchedulerService)
    {
        super();
        this.harvestingJob = harvestingJob;
        if(this.harvestingJob == null)
        {
            throw new IllegalArgumentException("harvestingJob cannot be null!");
        }
        this.platformConfigurationService = harvestingJob.getPlatformConfigurationService();
        if(this.platformConfigurationService == null)
        {
            throw new IllegalArgumentException("platformConfigurationService cannot be null!");
        }
        this.harvestingSchedulerService = harvestingSchedulerService;
        if(this.harvestingSchedulerService == null)
        {
            throw new IllegalArgumentException("harvestingSchedulerService cannot be null!");
        }

        this.init();
    }


    public void init()
    {
        this.setModal(true);
        this.setResizable(false);
        this.setHeight("300px");
        this.setWidth("500px");

        HarvestingJobManagementPanel panel = new HarvestingJobManagementPanel(this.harvestingJob, this.platformConfigurationService,
                this.harvestingSchedulerService);

        this.setContent(panel);
    }
}
