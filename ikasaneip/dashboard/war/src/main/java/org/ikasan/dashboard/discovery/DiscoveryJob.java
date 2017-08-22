package org.ikasan.dashboard.discovery;

import com.ikasan.topology.exception.DiscoveryException;
import org.apache.log4j.Logger;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.systemevent.service.SystemEventService;
import org.ikasan.topology.service.TopologyService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by Ikasan Development Team on 09/08/2016.
 */
@DisallowConcurrentExecution
public class DiscoveryJob implements Job
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(DiscoveryJob.class);


    private String jobName;
    private TopologyService topologyService;
    private SystemEventService systemEventService;
    private Boolean isRunning = false;
    private IkasanAuthentication authentication;


    public DiscoveryJob(String jobName, TopologyService topologyService,
                        SystemEventService systemEventService)
    {
        this.jobName = jobName;
        if(this.jobName == null)
        {
            throw new IllegalArgumentException("job name cannot be null!");
        }
        this.topologyService = topologyService;
        if(this.topologyService == null)
        {
            throw new IllegalArgumentException("topologyService cannot be null!");
        }
        this.systemEventService = systemEventService;
        if(this.systemEventService == null)
        {
            throw new IllegalArgumentException("systemEventService cannot be null!");
        }

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        isRunning = true;

        try
        {
            logger.info("Executing Topology Discovery: Initiated By: " + authentication.getName());

            systemEventService.logSystemEvent("Discovery", "Module Discovery Started", authentication.getName());

            try
            {
                topologyService.discover(authentication);
            }
            catch (DiscoveryException e)
            {
                logger.error("An error occurred trying to auto discover modules!", e);
                isRunning = false;
            }


            systemEventService.logSystemEvent("Discovery", "Module Discovery Completed", authentication.getName());

            logger.info("Finished Executing Topology Discovery");

            this.authentication = null;
        }
        catch (RuntimeException e)
        {
            isRunning = false;
            throw e;
        }


        isRunning = false;
    }

    public void setAuthentication(IkasanAuthentication authentication)
    {
        this.authentication = authentication;
    }

    public String getJobName()
    {
        return jobName;
    }

    public Boolean getRunning()
    {
        return isRunning;
    }
}
