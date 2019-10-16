package org.ikasan.rest.dashboard.util;

import org.ikasan.spec.cache.FlowStateCacheAdapter;

import java.util.HashMap;

public class TestCacheAdapter implements FlowStateCacheAdapter
{
    private HashMap<String, String> hashMap = new HashMap();


    @Override
    public void put(String moduleName, String flowName, String state)
    {
        this.hashMap.put(moduleName+flowName, state);
    }

    public String get(String key)
    {
        return this.hashMap.get(key);
    }
}
