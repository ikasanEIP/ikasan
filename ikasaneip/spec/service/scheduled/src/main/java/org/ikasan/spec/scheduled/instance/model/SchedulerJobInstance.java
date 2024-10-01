package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;

public interface SchedulerJobInstance extends SchedulerJob, StatefulEntity, Serializable {

    /**
     * Returns the context instance ID of the SchedulerJobInstance.
     *
     * @return the context instance ID
     */
    String getContextInstanceId();

    /**
     * Sets the ID of the context instance.
     *
     * @param contextInstanceId The ID of the context instance to set.
     */
    void setContextInstanceId(String contextInstanceId);

    /**
     * Retrieves the name of the child context that this job instance belongs to.
     *
     * @return The name of the child context.
     */
    String getChildContextName();

    /**
     * Set the child context name for the SchedulerJobInstance.
     *
     * @param childContextName the name of the child context
     */
    void setChildContextName(String childContextName);

    /**
     * Checks whether the job is being held.
     *
     * @return true if the job is being held, false otherwise.
     */
    boolean isHeld();

    /**
     * Sets the hold flag for the job instance.
     *
     * @param held the value indicating if the job instance is on hold
     */
    void setHeld(boolean held);

    /**
     * Checks if the initiation event is raised for the given SchedulerJobInstance.
     *
     * @return true if the initiation event is raised, false otherwise.
     */
    boolean isInitiationEventRaised();

    /**
     * Sets the value indicating whether the initiation event has been raised for the SchedulerJobInstance.
     *
     * @param initiationEventRaised true if the initiation event has been raised; otherwise, false.
     */
    void setInitiationEventRaised(boolean initiationEventRaised) ;

    /**
     * Retrieves the status of the current instance.
     *
     * @return The current status of the instance.
     */
    InstanceStatus getStatus();

    /**
     * Sets the status of an instance.
     *
     * @param status The new status to be set.
     */
    void setStatus(InstanceStatus status);

    /**
     * Retrieves the ScheduledProcessEvent associated with the SchedulerJobInstance.
     *
     * @return the ScheduledProcessEvent of the SchedulerJobInstance
     */
    ScheduledProcessEvent getScheduledProcessEvent();

    /**
     * Sets the scheduled process event for the scheduler job instance.
     *
     * @param scheduledProcessEvent The scheduled process event to set.
     */
    void setScheduledProcessEvent(ScheduledProcessEvent scheduledProcessEvent);

    /**
     * Returns whether the job instance should be skipped.
     *
     * @return true if the job instance should be skipped, false otherwise.
     */
    boolean isSkip();

    /**
     * Sets whether the job should be skipped or not.
     *
     * @param skip true if the job should be skipped, false otherwise
     */
    void setSkip(boolean skip);

    /**
     * Sets the error acknowledged flag for the SchedulerJobInstance.
     *
     * @param errorAcknowledged true if the error is acknowledged; false otherwise.
     */
    void setErrorAcknowledged(Boolean errorAcknowledged);

    /**
     * Checks whether the error for this SchedulerJobInstance has been acknowledged.
     *
     * @return true if the error has been acknowledged, false otherwise
     */
    Boolean isErrorAcknowledged();
}
