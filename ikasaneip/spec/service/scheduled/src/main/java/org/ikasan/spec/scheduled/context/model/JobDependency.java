package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface JobDependency extends Serializable {

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
