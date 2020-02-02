package org.ikasan.spec.trigger;

import java.util.Map;

public interface Trigger
{
    boolean appliesToFlowElement();

    String getFlowElementName();

    String getFlowName();

    Long getId();

    String getJobName();

    String getModuleName();

    Map<String, String> getParams();

    TriggerRelationship getRelationship();
}