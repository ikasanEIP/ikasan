package org.ikasan.spec.scheduled.context.model;

public interface JobDependency {

    public String getJobIdentifier();

    public void setJobIdentifier(String jobIdentifier);

    public LogicalGrouping getLogicalGrouping();

    public void setLogicalGrouping(LogicalGrouping logicalGrouping);
}
