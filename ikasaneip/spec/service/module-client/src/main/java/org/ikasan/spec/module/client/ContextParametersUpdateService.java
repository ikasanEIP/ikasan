package org.ikasan.spec.module.client;

public interface ContextParametersUpdateService<CONTEXT_INSTANCE> {

    void update(String contextUrl, CONTEXT_INSTANCE instance);
}