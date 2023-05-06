package org.ikasan.spec.scheduled.instance.service;

import java.util.List;
import java.util.Map;

import org.ikasan.spec.scheduled.context.model.ContextTemplate;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

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
     * @param internalJobs
     */
    void populateContextParametersOnContextInstance(ContextInstance contextInstance
        , Map<String, InternalEventDrivenJobInstance> internalJobs);

    /**
     * Get list of context parameter instances for a context template.
     *
     * @param contextTemplate
     * @param internalJobs
     * @return
     */
    List<ContextParameterInstance> getContextParameterInstancesForContext(ContextTemplate contextTemplate
        , Map<String, InternalEventDrivenJob> internalJobs);
}
