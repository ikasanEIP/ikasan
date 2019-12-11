package org.ikasan.dashboard.cache;

import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.dto.FlowDto;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.function.Consumer;

import java.util.concurrent.ConcurrentHashMap;

public class FlowStateCache implements Consumer<FlowState>
{
    private Logger logger = LoggerFactory.getLogger(FlowStateCache.class);

    private static FlowStateCache INSTANCE;

    public static FlowStateCache instance()
    {
        if(INSTANCE == null)
        {
            synchronized (FlowStateCache.class)
            {
                if(INSTANCE == null)
                {
                    INSTANCE = new FlowStateCache();
                }
            }
        }
        return INSTANCE;
    }

    private ConcurrentHashMap<String, FlowState> cache;
    private ModuleControlRestServiceImpl moduleControlRestService;

    private FlowStateCache()
    {
        cache = new ConcurrentHashMap<>();
        FlowStateBroadcaster.register(this);
    }

    public void put(FlowState flowState)
    {
        String key = flowState.getModuleName() + flowState.getFlowName();
        this.cache.put(key, flowState);

        CacheStateBroadcaster.broadcast(flowState);
    }

    public FlowState get(Module module, Flow flow)
    {
        if(!this.contains(module, flow))
        {
            refreshFromSource(module.getName(), flow.getName(), module.getUrl());
        }

        return this.cache.get(module.getName()+flow.getName());
    }

    public boolean contains(Module module, Flow flow)
    {
        logger.debug("Check contains: " + module + flow);
        return this.cache.containsKey(module.getName()+flow.getName());
    }

    @Override
    public void accept(FlowState flowState)
    {
        logger.debug("Received state change: " + flowState);
        this.put(flowState);
    }

    public void setModuleControlRestService(ModuleControlRestServiceImpl moduleControlRestService)
    {
        this.moduleControlRestService = moduleControlRestService;
    }

    private FlowState refreshFromSource(String moduleName, String flowName, String contextUrl)
    {
        Optional<FlowDto> flowDto;

        try
        {
            flowDto = this.moduleControlRestService.getFlowState(contextUrl, moduleName, flowName);
        }
        catch (Exception e)
        {
            logger.warn(String.format("Could not load flow state for module[%s], flow[%s] using URL[%s].", moduleName, flowName, contextUrl));
            return null;
        }

        FlowState state = null;
        if(flowDto.isPresent())
        {
            state = new FlowState(moduleName, flowName, State.getState(flowDto.get().getState()));
            FlowStateCache.instance().put(state);
        }

        return state;
    }
}
