package org.ikasan.ootb.scheduler.agent.rest.cache;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    private boolean initialisationComplete = false;

    private ContextInstanceCache() {
        this.contextInstanceMap = new ConcurrentHashMap<>();
    }

    /**
     * Adds a ContextInstance to the cache with the provided correlationId.
     *
     * @param correlationId The correlationId to associate with the ContextInstance
     * @param instance The ContextInstance to be added to the cache
     */
    public void put(String correlationId, ContextInstance instance) {
        if (correlationId == null || instance == null) {
            return;
        }

        LOG.info("Adding correlationId [%s] to cache".formatted(correlationId));
        this.contextInstanceMap.put(correlationId, instance);
    }

    /**
     * Adds all entries from the provided map of new instances to the ContextInstanceCache.
     *
     * @param newInstances A map containing new ContextInstance objects to be added to the cache
     */
    public void putAll(Map<String, ContextInstance> newInstances) {
        if (newInstances != null && ! newInstances.isEmpty()) {
            newInstances.keySet().forEach( ciKey -> this.put(ciKey, newInstances.get(ciKey)));
        }
    }

    /**
     * Removes the ContextInstance associated with the provided correlationId from the cache.
     *
     * @param correlationId The correlationId of the ContextInstance to be removed
     */
    public void remove(String correlationId) {
        if (correlationId == null) {
            return;
        }
        LOG.info("Removing correlationId [%s]".formatted(correlationId));
        this.contextInstanceMap.remove(correlationId);
    }

    /**
     * Removes all the ContextInstances associated with the provided list of correlationIds from the cache.
     *
     * @param correlationIds A list of correlationIds of the ContextInstances to be removed from the cache
     */
    public void removeAll(List<String> correlationIds) {
        if (correlationIds != null && ! correlationIds.isEmpty()) {
            correlationIds.forEach(this::remove);
        }
    }

    /**
     * Removes all entries from the contextInstanceMap, effectively clearing the cache.
     */
    public void removeAll() {
        this.contextInstanceMap.clear();
    }

    /**
     * Retrieves the ContextInstance associated with the provided correlation ID.
     *
     * @param correlationId The correlation ID to search for the ContextInstance.
     * @return The ContextInstance object matching the correlation ID. Returns null if not found.
     */
    public ContextInstance getByCorrelationId(String correlationId) {
        if (correlationId != null) {
            LOG.debug("Getting context parameters for correlationId [%s]".formatted(correlationId));
            return this.contextInstanceMap.get(correlationId);
        } else {
            return null;
        }
    }

    /**
     * Retrieves the value of a specific context parameter by the provided correlation ID and parameter name.
     *
     * @param correlationId The correlation ID to search for the context parameter.
     * @param contextParameterName The name of the context parameter to retrieve.
     * @return The value of the context parameter matching the correlation ID and parameter name. If not found, returns an empty string.
     */
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

        // We return an empty String if nothing found!
        LOG.info(String.format("Could not find context parameter for job plan instance instance[%s] and parameter name [%s]. " +
            "Returning empty String.", correlationId, contextParameterName));
        return "";
    }

    /**
     * Checks if a specific correlationId exists in the cache.
     *
     * @param correlationId The correlationId to check in the cache
     * @return true if the correlationId exists in the cache, false otherwise
     */
    public static boolean existsInCache(String correlationId) {
        return ContextInstanceCache.instance().getByCorrelationId(correlationId) != null;
    }

    /**
     * Checks if a specific correlationId does not exist in the cache.
     *
     * @param correlationId The correlationId to check in the cache
     * @return true if the correlationId does not exist in the cache, false otherwise
     */
    public static boolean doesNotExistInCache(String correlationId) {
        return !existsInCache(correlationId);
    }

    /**
     * Retrieves a set of correlation IDs from the ContextInstanceCache.
     *
     * @return A set of strings representing the correlation IDs present in the ContextInstanceCache
     */
    public static Set<String> getCorrelationIds() {
        return ContextInstanceCache.instance().contextInstanceMap.keySet();
    }

    /**
     * Check if the initialisation process is complete.
     *
     * @return true if the initialisation process is complete, false otherwise
     */
    public boolean isInitialisationComplete() {
        return initialisationComplete;
    }

    /**
     * Sets the flag indicating whether the initialisation process is complete.
     *
     * @param initialisationComplete A boolean value indicating if the initialisation process is complete
     */
    public void setInitialisationComplete(boolean initialisationComplete) {
        this.initialisationComplete = initialisationComplete;
    }
}
