package org.ikasan.spec.harvest;

/**
 * Created by Ikasan Development Team on 24/08/2016.
 */
public interface HarvestingSchedulerService
{

    void registerJobs();

    void removeJob(String jobName);

    void addJob(String jobName);

    void startScheduler();

    void shutdownScheduler();
}
