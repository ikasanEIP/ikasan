package org.ikasan.spec.scheduled.provision;

import org.ikasan.spec.scheduled.context.model.ContextBundle;

public interface ContextProvisionService {

    /**
     * Service method to provision a context which is provided with all of its artifacts in a ContextBundle.
     *
     * @param contextBundle
     */
    void provisionContext(ContextBundle contextBundle);
}
