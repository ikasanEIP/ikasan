package org.ikasan.spec.scheduled.context.service;

import org.ikasan.spec.scheduled.context.model.ContextTemplate;

public interface ContextInstanceSchedulerService {

    /**
     * This is the job for the start of context.
     * Note that we don't register the destroy job yet, we wait till the job actually fires so we can pass
     * the correct contextID to the destroy job.
     * @param contextTemplate to start
     * @param timezone for the tme window
     */
    void registerStartJobAndTrigger(ContextTemplate contextTemplate, String timezone);

    /**
     * This sets up the context instance destroy job and its trigger.
     * It will be typically called when the context instance is actually created / initialised
     * @param contextName for the starting context to which this will be paired
     * @param cronExpressionToTriggerJob for this instance
     * @param timezone for the cron expression
     * @param contextInstanceId used to pair the destroy context instance with the correct create context instance.
     */
    void registerEndJobAndTrigger(String contextName, String cronExpressionToTriggerJob, String timezone, String contextInstanceId);

    /**
     * Removes a job related to the given contextName.
     *
     * @param contextName the name of the context for which the job should be removed
     */
    void removeJob(String contextName);
}
