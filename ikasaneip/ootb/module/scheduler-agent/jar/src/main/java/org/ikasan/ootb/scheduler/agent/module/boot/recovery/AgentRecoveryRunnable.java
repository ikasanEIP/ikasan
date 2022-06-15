package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import java.util.Map;

import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentRecoveryRunnable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AgentRecoveryRunnable.class);

    private final ContextInstanceRestService<ContextInstance> contextInstanceRestService;
    private final long minutesToKeepRetrying;

    public AgentRecoveryRunnable(ContextInstanceRestService contextInstanceRestService, long minutesToKeepRetrying) {
        this.contextInstanceRestService = contextInstanceRestService;
        this.minutesToKeepRetrying = minutesToKeepRetrying;
    }

    @Override
    public void run() {
        LOG.info("Importing context instances parameters to cache!");
        importInstances();
    }

    private void importInstances() {
        long attempts = 0;
        long retryWaitTime = 0;
        boolean exception = false;
        long maxTimeToRetry = minutesToKeepRetrying * 60 * 1000;
        long startTimeMillis = System.currentTimeMillis();
        while (retryWaitTime < maxTimeToRetry) {
            if (System.currentTimeMillis() > startTimeMillis + maxTimeToRetry) {
                break;
            }
            exception = false;
            try {
                Map<String, ContextInstance> contextInstances = contextInstanceRestService.getAll();
                for (String contextName : contextInstances.keySet()) {
                    ContextInstanceCache.instance().put(contextName, contextInstances.get(contextName));
                }
                LOG.info("Successfully recovered context instances at start up");
                break;
            } catch (Exception e) {
                exception = true;
                retryWaitTime = 500L * attempts;
                sleep(retryWaitTime);
                attempts++;
            }
        }
        if (exception) {
            String message
                = String.format("Could not recover instances for agent in %d minutes. This is a fatal problem that needs to be resolved!", minutesToKeepRetrying);
            LOG.error(message);
            throw new EndpointException(message);
        }
    }

    private void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
