package org.ikasan.job.orchestration.model.context;

import org.ikasan.spec.scheduled.context.model.JobDependency;
import org.ikasan.spec.scheduled.context.model.LogicalGrouping;

public class JobDependencyImpl implements JobDependency {
    private String jobIdentifier;
    private LogicalGrouping logicalGrouping;
    private boolean eventDependency = false;

    @Override
    public String getJobIdentifier() {
        return jobIdentifier;
    }

    @Override
    public void setJobIdentifier(String jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    @Override
    public LogicalGrouping getLogicalGrouping() {
        return logicalGrouping;
    }

    @Override
    public void setLogicalGrouping(LogicalGrouping logicalGrouping) {
        this.logicalGrouping = logicalGrouping;
    }
}
