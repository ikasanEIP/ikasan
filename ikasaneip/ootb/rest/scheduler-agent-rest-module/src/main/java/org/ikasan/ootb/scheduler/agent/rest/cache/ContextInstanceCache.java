package org.ikasan.ootb.scheduler.agent.rest.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
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

    public void put(ContextInstance instance) {
        put(instance.getName(), instance);
    }

    public void put(String contextName, ContextInstance instance) {
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
            LOG.info(String.format("Getting context parameters for context name [%s]", contextName));
            return this.contextInstanceMap.get(contextName);
        } else {
            return null;
        }
    }
}
