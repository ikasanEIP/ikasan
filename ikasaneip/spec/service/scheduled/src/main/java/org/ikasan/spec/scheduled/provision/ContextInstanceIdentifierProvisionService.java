package org.ikasan.spec.scheduled.provision;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

import java.util.Map;

public interface ContextInstanceIdentifierProvisionService {

    /**
     * Provision a context instance. This will provide the correlating identifier
     * to all relevant components.
     *
     * @param contextInstance
     */
    void provision(ContextInstance contextInstance);

    /**
     * Remove this correlation ID from the components. Usually called when the dashboard identified
     * a context instance as finished.
     *
     * @param correlationId to be removed.
     */
    void remove(String correlationId);

    /**
     * Reset all components so that the only context instances they will deal with are within the supplied Map
     * This usually happens when the agent is restarted and has asked the dashboard what instances it should be handling.
     * Even an empty list is actioned i.e. removal of any correlationIDs
     *
     * @param liveContextInstances to be used for components.
     */
    void reset(Map<String, ContextInstance> liveContextInstances);

    /**
     * Manage all relevant endpoints so that there are no correlating identifiers associated
     * with any of them.
     */
    void removeAll();
}
