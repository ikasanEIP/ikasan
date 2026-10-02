package org.ikasan.spec.cache;

import org.ikasan.spec.flow.FlowState;

public interface FlowStateCacheAdapter
{

    /**
     * Stores a state value associated with a specific module and flow combination.
     *
     * @param moduleName the name of the module for which the state is to be stored
     * @param flowName the name of the flow for which the state is to be stored
     * @param state the state value to be stored
     */
    void put(String moduleName, String flowName, String state);

    /**
     * Retrieves a FlowState instance associated with the specified module and flow names.
     *
     * @param moduleName the name of the module for which the FlowState is retrieved
     * @param flowName the name of the flow for which the FlowState is retrieved
     * @return the FlowState instance associated with the given module and flow names,
     *         or null if no matching FlowState is found
     */
    FlowState get(String moduleName, String flowName);
}
