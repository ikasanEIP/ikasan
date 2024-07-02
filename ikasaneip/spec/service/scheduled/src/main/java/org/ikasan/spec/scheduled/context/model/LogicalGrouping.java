package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;
import java.util.List;

public interface LogicalGrouping extends Serializable {

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

    /**
     * Retrieves the list of And objects associated with this LogicalGrouping.
     *
     * @return The list of And objects.
     */
    List<And> getAnd();

    /**
     * Sets the list of And objects for the LogicalGrouping.
     *
     * @param and the list of And objects to be set
     */
    void setAnd(List<And> and);

    /**
     * Retrieves the list of Or objects associated with this LogicalGrouping.
     *
     * @return The list of Or objects associated with this LogicalGrouping.
     */
    List<Or> getOr();

    /**
     * Sets the list of Or objects for the logical grouping.
     *
     * @param or the list of Or objects to be set
     */
    void setOr(List<Or> or);

    /**
     * Retrieves the list of Not objects associated with this LogicalGrouping.
     *
     * @return The list of Not objects.
     */
    List<Not> getNot();

    /**
     * Sets the list of "not" logical operators for this logical grouping.
     *
     * @param not the list of "not" logical operators to be set
     */
    void setNot(List<Not> not);
}
