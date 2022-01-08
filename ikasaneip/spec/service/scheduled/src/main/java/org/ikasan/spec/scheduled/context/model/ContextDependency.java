package org.ikasan.spec.scheduled.context.model;

import java.util.List;

public interface ContextDependency {

    String getContextIdentifier();

    void setContextIdentifier(String contextIdentifier);

    String getContextDependencyName();

    void setContextDependencyName(String contextDependencyName);

    LogicalGrouping getLogicalGrouping();

    void setLogicalGrouping(LogicalGrouping logicalGrouping);

//    List<ContextDependency> getContextDependencies();
//
//    void setContextDependencies(List<ContextDependency> contextDependencies);
}
