package org.ikasan.spec.scheduled.context.model;

public interface LogicalOperator {

    public String getIdentifier();

    public void setIdentifier(String identifier);

    public LogicalGrouping getLogicalGrouping();

    public void setLogicalGrouping(LogicalGrouping logicalGrouping);
}
