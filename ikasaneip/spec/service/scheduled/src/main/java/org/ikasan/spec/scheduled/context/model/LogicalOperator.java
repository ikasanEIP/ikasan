package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface LogicalOperator extends Serializable {

    public String getIdentifier();

    public void setIdentifier(String identifier);

    public LogicalGrouping getLogicalGrouping();

    public void setLogicalGrouping(LogicalGrouping logicalGrouping);
}
