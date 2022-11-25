package org.ikasan.spec.scheduled.instance.service;

import java.util.List;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

public interface ContextParametersInstanceService {

    void populateContextParameters();

    String getContextParameterValue(String contextName, String parameterValue);

    List<ContextParameterInstance> getAllContextParameters(String contextName);

    // TODO can be removed when no longer being hardcoded
    boolean isSkipped(String contextName, String jobName);
}
