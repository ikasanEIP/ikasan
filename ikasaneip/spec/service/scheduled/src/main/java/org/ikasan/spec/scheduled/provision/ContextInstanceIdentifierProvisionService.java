package org.ikasan.spec.scheduled.provision;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

public interface ContextInstanceIdentifierProvisionService {
    public void provision(ContextInstance contextInstance);
    public void update(String correlationId);
}
