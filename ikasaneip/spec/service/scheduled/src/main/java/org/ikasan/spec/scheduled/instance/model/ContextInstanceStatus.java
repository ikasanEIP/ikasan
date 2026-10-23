package org.ikasan.spec.scheduled.instance.model;


import java.util.List;

public class ContextInstanceStatus {
    private String contextName;
    private InstanceStatus instanceStatus;
    private List<ContextInstanceStatus> contextInstanceStatuses;
    private List<SchedulerJobInstanceStatus> schedulerJobInstanceStatuses;

    /**
     * Retrieves the name of the context associated with this instance.
     *
     * @return the context name
     */
    public String getContextName() {
        return contextName;
    }

    /**
     * Sets the name of the context.
     *
     * @param contextName the name to be assigned to the context
     */
    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    /**
     * Retrieves the status of the instance.
     *
     * @return the current {@link InstanceStatus} of the instance.
     */
    public InstanceStatus getInstanceStatus() {
        return instanceStatus;
    }

    /**
     * Updates the status of the instance.
     *
     * @param instanceStatus the new {@link InstanceStatus} to be assigned to the instance
     */
    public void setInstanceStatus(InstanceStatus instanceStatus) {
        this.instanceStatus = instanceStatus;
    }

    /**
     * Retrieves a list of {@code ContextInstanceStatus} objects associated with this instance.
     *
     * @return a list of {@code ContextInstanceStatus} representing the statuses of child contexts.
     */
    public List<ContextInstanceStatus> getContextInstanceStatuses() {
        return contextInstanceStatuses;
    }

    /**
     * Sets the list of context instance statuses.
     *
     * @param contextInstanceStatuses the list of {@code ContextInstanceStatus} objects to be assigned
     */
    public void setContextInstanceStatuses(List<ContextInstanceStatus> contextInstanceStatuses) {
        this.contextInstanceStatuses = contextInstanceStatuses;
    }

    /**
     * Retrieves a list of {@code SchedulerJobInstanceStatus} objects associated with the current context.
     * This list represents the statuses of various scheduler job instances.
     *
     * @return a list of {@code SchedulerJobInstanceStatus} objects
     */
    public List<SchedulerJobInstanceStatus> getSchedulerJobInstanceStatuses() {
        return schedulerJobInstanceStatuses;
    }

    /**
     * Sets the list of SchedulerJobInstanceStatus objects associated with the scheduler job.
     *
     * @param schedulerJobInstanceStatuses the list of SchedulerJobInstanceStatus instances to be set
     */
    public void setSchedulerJobInstanceStatuses(List<SchedulerJobInstanceStatus> schedulerJobInstanceStatuses) {
        this.schedulerJobInstanceStatuses = schedulerJobInstanceStatuses;
    }
}
