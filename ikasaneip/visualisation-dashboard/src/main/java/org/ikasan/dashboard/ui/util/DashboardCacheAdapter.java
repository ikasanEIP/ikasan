package org.ikasan.dashboard.ui.util;

import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.spec.cache.FlowStateCacheAdapter;

public class DashboardCacheAdapter implements FlowStateCacheAdapter
{

    @Override
    public void put(String moduleName, String flowName, String state)
    {
        FlowState flowState = new FlowState(moduleName, flowName, State.getState(state));

        FlowStateCache.instance().put(flowState);
    }
}
