package org.ikasan.spec.scheduled.provision;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

import java.util.Map;

public interface ContextInstanceIdentifierProvisionService {
    void provision(ContextInstance contextInstance);
    void remove(String correlationId);
    void reset(Map<String, ContextInstance> liveContextInstances);
}
