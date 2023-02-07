package org.ikasan.spec.scheduled.instance.service;

import java.util.List;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

public interface ContextParametersInstanceService {

    /**
     * Method used to populate all context parameters on the service
     * from the underlying properties provider mechanism.
     */
    void populateContextParameters();

    /**
     * Get an individual context parameter value for a context.
     *
     * @param contextName
     * @param parameterValue
     * @return
     */
    String getContextParameterValue(String contextName, String parameterValue);

    /**
     * Get all parameter values for a context
     *
     * @param contextName
     * @return
     */
    List<ContextParameterInstance> getAllContextParameters(String contextName);

    /**
     * Method to populate all parameters on the context instance.
     *
     * @param contextInstance
     */
    void populateContextParametersOnContextInstance(ContextInstance contextInstance);

    // TODO can be removed when no longer being hardcoded
    boolean isSkipped(String contextName, String jobName);
}
