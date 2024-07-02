package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface LogicalOperator extends Serializable {

    /**
     * Retrieves the identifier associated with an object.
     *
     * @return The identifier of the object.
     */
    String getIdentifier();

    /**
     * Sets the identifier for the object.
     *
     * @param identifier the identifier to set
     */
    void setIdentifier(String identifier);

    /**
     * Retrieves the logical grouping associated with this logical operator.
     *
     * @return The logical grouping associated with this logical operator.
     */
    LogicalGrouping getLogicalGrouping();

    /**
     * Sets the logical grouping for the given LogicalGrouping object.
     *
     * @param logicalGrouping the logical grouping to be set
     */
    void setLogicalGrouping(LogicalGrouping logicalGrouping);
}
