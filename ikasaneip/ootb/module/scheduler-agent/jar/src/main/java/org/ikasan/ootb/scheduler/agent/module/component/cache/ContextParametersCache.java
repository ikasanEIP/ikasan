package org.ikasan.ootb.scheduler.agent.module.component.cache;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ContextParametersCache
{
    private Logger logger = LoggerFactory.getLogger(ContextParametersCache.class);

    private static ContextParametersCache INSTANCE;

    public static ContextParametersCache instance()
    {
        if(INSTANCE == null) {
            synchronized (ContextParametersCache.class) {
                if(INSTANCE == null) {
                    INSTANCE = new ContextParametersCache();
                }
            }
        }
        return INSTANCE;
    }

    private ConcurrentHashMap<String, List<ContextParameterInstance>> contextParametersByContextNameCache;

    private ContextParametersCache() {
        this.contextParametersByContextNameCache = new ConcurrentHashMap<>();
    }

    public void put(Map<String, List<ContextParameterInstance>> contextParameters)
    {
        for (String contextName : contextParameters.keySet()) {
            this.contextParametersByContextNameCache.put(contextName, contextParameters.get(contextName));
        }
    }

    public List<ContextParameterInstance> getByContextName(String contextName)
    {
        logger.debug(String.format("%s attempting to get context parameters using context name[%s]"
            , this, contextName));

        return this.contextParametersByContextNameCache.get(contextName);
    }

    public boolean containsContextName(String contextName)
    {
        boolean result = this.contextParametersByContextNameCache.containsKey(contextName);
        logger.debug(String.format("%s check contains[%s] - result [%s]",this
            , contextName, result));
        return result;
    }

    public Set contextNames() {
        return this.contextParametersByContextNameCache.keySet();
    }

}
