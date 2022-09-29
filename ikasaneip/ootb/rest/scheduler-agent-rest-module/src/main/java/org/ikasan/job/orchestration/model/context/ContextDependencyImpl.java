package org.ikasan.job.orchestration.model.context;

import org.ikasan.spec.scheduled.context.model.ContextDependency;
import org.ikasan.spec.scheduled.context.model.LogicalGrouping;

public class ContextDependencyImpl implements ContextDependency {
    private String contextIdentifier;
    private String contextDependencyName;
    private LogicalGrouping logicalGrouping;

    public String getContextIdentifier() {
        return contextIdentifier;
    }

    public void setContextIdentifier(String contextIdentifier) {
        this.contextIdentifier = contextIdentifier;
    }

    public String getContextDependencyName() {
        return contextDependencyName;
    }

    public void setContextDependencyName(String contextDependencyName) {
        this.contextDependencyName = contextDependencyName;
    }

    public LogicalGrouping getLogicalGrouping() {
        return logicalGrouping;
    }

    public void setLogicalGrouping(LogicalGrouping logicalGrouping) {
        this.logicalGrouping = logicalGrouping;
    }

}
