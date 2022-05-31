package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface JobDependency extends Serializable {

    public String getJobIdentifier();

    public void setJobIdentifier(String jobIdentifier);

    public LogicalGrouping getLogicalGrouping();

    public void setLogicalGrouping(LogicalGrouping logicalGrouping);
}
