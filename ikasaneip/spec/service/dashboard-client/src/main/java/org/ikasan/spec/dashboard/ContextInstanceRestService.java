package org.ikasan.spec.dashboard;

import java.util.Map;

public interface ContextInstanceRestService<T> {
    Map<String, T> getAllInstancesDashboardThinksAgentShouldHandle(String agentName);
}
