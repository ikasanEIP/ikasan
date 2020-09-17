package org.ikasan.dashboard.schedule;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class DashboardSchedulerLifeCycleService {

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    public DashboardSchedulerLifeCycleService(Scheduler scheduler) {
        this.scheduler = scheduler;
        if(this.scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be null!");
        }
    }

    /**
     * Start the underlying tech
     */
    @PostConstruct
    public void startScheduler()
    {
        try
        {
            this.scheduler.start();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException("Could not start scheduler!", e);
        }
    }

    /**
     * Start the underlying tech
     */
    @PreDestroy
    public void stopScheduler()
    {
        try
        {
            this.scheduler.isShutdown();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException("Could not stop scheduler!", e);
        }
    }

}
