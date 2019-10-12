package org.ikasan.spec.cache;

public interface FlowStateCacheAdapter
{
    /**
     * Add a value to the cache.
     *
     * @param moduleName
     * @param flowName
     * @param state
     */
    public void put(String moduleName, String flowName, String state);
}
