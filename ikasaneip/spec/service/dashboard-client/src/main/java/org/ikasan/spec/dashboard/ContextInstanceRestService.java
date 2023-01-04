package org.ikasan.spec.dashboard;

import java.util.Map;

public interface ContextInstanceRestService<T> {

    // @Mick I don't think we need these anymore
//    Map<String, T> getAll();
//
//    Map<String, T> getByContextId(String correlationId);
    Map<String, T> getByAgentName(String agentName);
}
