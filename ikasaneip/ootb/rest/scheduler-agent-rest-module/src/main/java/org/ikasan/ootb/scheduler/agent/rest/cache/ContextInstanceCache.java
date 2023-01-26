package org.ikasan.ootb.scheduler.agent.rest.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
public class ContextInstanceCache {
    private static final Logger LOG = LoggerFactory.getLogger(ContextInstanceCache.class);

    private static final class InstanceHolder {
        private static final ContextInstanceCache INSTANCE = new ContextInstanceCache();
    }

    public static ContextInstanceCache instance() {
        return InstanceHolder.INSTANCE;
    }

    // correlationId -> ContextInstances
    private final ConcurrentHashMap<String, ContextInstance> contextInstanceMap;

    private ContextInstanceCache() {
        this.contextInstanceMap = new ConcurrentHashMap<>();
    }

    public void put(String correlationId, ContextInstance instance) {
        if (correlationId == null || instance == null) {
            return;
        }

        LOG.info(String.format("Adding correlationId [%s] to cache", correlationId));
        this.contextInstanceMap.put(correlationId, instance);
    }

    public void putAll(Map<String, ContextInstance> newInstances) {
        if (newInstances != null && ! newInstances.isEmpty()) {
            newInstances.keySet().forEach( ciKey -> this.put(ciKey, newInstances.get(ciKey)));
        }
    }

    public void remove(String correlationId) {
        if (correlationId == null) {
            return;
        }
        LOG.info(String.format("Removing correlationId [%s]", correlationId));
        this.contextInstanceMap.remove(correlationId);
    }

    public void removeAll(List<String> correlationIds) {
        if (correlationIds != null && ! correlationIds.isEmpty()) {
            correlationIds.forEach(this::remove);
        }
    }

    public void removeAll() {
        this.contextInstanceMap.clear();
    }

    public ContextInstance getByCorrelationId(String correlationId) {
        if (correlationId != null) {
            LOG.debug(String.format("Getting context parameters for correlationId [%s]", correlationId));
            return this.contextInstanceMap.get(correlationId);
        } else {
            return null;
        }
    }

    public static String getContextParameter(String correlationId, String contextParameterName) {
        if (correlationId != null && contextParameterName != null) {
            ContextInstance instance = ContextInstanceCache.instance().getByCorrelationId(correlationId);
            if (instance != null) {
                List<ContextParameterInstance> contextParameters = instance.getContextParameters();
                if (contextParameters != null) {
                    for (ContextParameterInstance contextParameter : contextParameters) {
                        if (contextParameter.getName() != null
                            && contextParameter.getValue() != null
                            && contextParameterName.equals(contextParameter.getName())) {
                            return contextParameter.getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean existsInCache(String correlationId) {
        return ContextInstanceCache.instance().getByCorrelationId(correlationId) != null;
    }

    public static boolean doesNotExistInCache(String correlationId) {
        return !existsInCache(correlationId);
    }

    public static Set<String> getCorrelationIds() {
        return ContextInstanceCache.instance().contextInstanceMap.keySet();
    }
}
