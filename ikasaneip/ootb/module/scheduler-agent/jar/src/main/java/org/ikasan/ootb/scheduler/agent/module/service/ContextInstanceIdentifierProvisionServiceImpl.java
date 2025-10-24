package org.ikasan.ootb.scheduler.agent.module.service;

import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.InstanceStatus;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ContextInstanceIdentifierProvisionServiceImpl implements ContextInstanceIdentifierProvisionService {
    Logger logger = LoggerFactory.getLogger(ContextInstanceIdentifierProvisionServiceImpl.class);

    @Override
    public void provision(ContextInstance contextInstance) {

        try {
            ContextInstanceCache.instance().put(contextInstance.getId(), contextInstance);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new ContextInstanceIdentifierProvisionServiceException(e);
        }
    }

    /**
     * Remove this correlation ID from the components. Usually called when the dashboard identified a context instance as finished.
     * @param correlationId to be removed.
     */
    public void remove(String correlationId) {
        try {
            ContextInstanceCache.instance().remove(correlationId);
        }
        catch (Exception e)
        {
            logger.error(String.format("An error has occurred removing context with id[%s] from the context instance cache!"
                , correlationId), e);
            throw new ContextInstanceIdentifierProvisionServiceException(e);
        }
    }

    /**
     * Reset all components so that the only context instances they will deal with are within the supplied Map
     * This usually happens when the agent is restarted and has asked the dashboard what instances it should be handling.
     * Even an empty list is actioned i.e. removal of any correlationIDs
     *
     * @param liveContextInstances to be used for components.
     */
    public void reset(Map<String, ContextInstance> liveContextInstances) {
        try {
            this.removeAll();
            liveContextInstances.values().forEach(contextInstance -> {
                if(contextInstance.getStatus() != null && !contextInstance.getStatus().equals(InstanceStatus.PREPARED)) {
                    this.provision(contextInstance);
                }
            });
        }
        catch (Exception e)
        {
            logger.error("An error has occurred resetting all entries in the context instance cache!", e);
            throw new ContextInstanceIdentifierProvisionServiceException(e);
        }
    }

    @Override
    public void removeAll() {
        try {
            ContextInstanceCache.instance().removeAll();
        }
        catch (Exception e)
        {
            logger.error("An error has occurred removing all entries for the context instance cache!", e);
            throw new ContextInstanceIdentifierProvisionServiceException(e);
        }
    }
}
