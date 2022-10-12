package org.ikasan.spec.scheduled.instance.model;

public interface ContextInstanceAggregateJobStatus {

    /**
     * Get the context instance id that the jobs status is being queried for.
     *
     * @return
     */
    String getContextInstanceId();

    /**
     * Get the context instance name that the status is being queried for.
     *
     * @return
     */
    String getContextInstanceName();

    /**
     * Get the job count for a given status.
     *
     * @param instanceStatus
     * @return
     */
    int getStatusCount(InstanceStatus instanceStatus);
}
