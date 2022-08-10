package org.ikasan.ootb.scheduler.agent.rest.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextInstanceCache {
    private static final Logger LOG = LoggerFactory.getLogger(ContextInstanceCache.class);

    private static final class InstanceHolder {
        private static final ContextInstanceCache INSTANCE = new ContextInstanceCache();
    }

    public static ContextInstanceCache instance() {
        return InstanceHolder.INSTANCE;
    }

    private final ConcurrentHashMap<String, ContextInstance> contextInstanceMap;

    private ContextInstanceCache() {
        this.contextInstanceMap = new ConcurrentHashMap<>();
    }

    public void put(String contextName, ContextInstance instance) {
        if (contextName == null || instance == null) {
            return;
        }

        LOG.info(String.format("Adding context instance [%s]", contextName));
        this.contextInstanceMap.put(contextName, instance);
    }

    public void remove(String contextName) {
        if (contextName == null) {
            return;
        }
        LOG.info(String.format("Removing context instance [%s]", contextName));
        this.contextInstanceMap.remove(contextName);
    }

    public ContextInstance getByContextName(String contextName) {
        if (contextName != null) {
            LOG.debug(String.format("Getting context parameters for context name [%s]", contextName));
            return this.contextInstanceMap.get(contextName);
        } else {
            return null;
        }
    }

    public static String getContextParameter(String contextName, String contextParameterName) {
        if (contextName != null && contextParameterName != null) {
            ContextInstance instance = ContextInstanceCache.instance().getByContextName(contextName);
            if (instance != null) {
                List<ContextParameterInstance> contextParameters = instance.getContextParameters();
                if (contextParameters != null) {
                    for (ContextParameterInstance contextParameter : contextParameters) {
                        if (contextParameter.getName() != null
                            && contextParameter.getValue() != null
                            && contextParameterName.equals(contextParameter.getName())) {
                            return contextParameter.getValue().toString();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean existsInCache(String contextName) {
        return ContextInstanceCache.instance().getByContextName(contextName) != null;
    }

    public static boolean doesNotExistInCache(String contextName) {
        return !existsInCache(contextName);
    }

}
