package org.ikasan.spec.dashboard;

import java.util.Map;

public interface ContextInstanceRestService<T> {

    Map<String, T> getAll();

    Map<String, T> getByContextName(String contextName);
}
