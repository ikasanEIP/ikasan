package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface JobDependency extends Serializable {

    /**
     * Checks if the current object has a dependency on an event.
     *
     * @return {@code true} if the job is dependent on an event, {@code false} otherwise
     */
    boolean isEventDependency();

    /**
     * Sets the event dependency for the JobDependency object.
     *
     * The event dependency indicates whether the job has a dependency on an external event.
     * If set to true, it means the job is dependent on an event, otherwise false.
     *
     * @param eventDependency the boolean value indicating the event dependency
     */
    void setEventDependency(boolean eventDependency);

    /**
     * Retrieves the job identifier associated with this JobDependency object.
     *
     * @return The job identifier.
     */
    String getJobIdentifier();

    /**
     * Sets the job identifier for this object.
     *
     * @param jobIdentifier the job identifier to be set
     */
    void setJobIdentifier(String jobIdentifier);

    /**
     * Retrieves the logical grouping associated with this object.
     *
     * @return The logical grouping associated with this object.
     */
    LogicalGrouping getLogicalGrouping();

    /**
     * Sets the logical grouping for the given LogicalGrouping object.
     *
     * @param logicalGrouping the logical grouping to be set
     */
    void setLogicalGrouping(LogicalGrouping logicalGrouping);
}
