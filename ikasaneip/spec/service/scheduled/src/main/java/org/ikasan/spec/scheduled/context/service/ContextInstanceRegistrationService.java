package org.ikasan.spec.scheduled.context.service;

public interface ContextInstanceRegistrationService {

    void register(String contextName);

    void deRegister(String contextName);
}
