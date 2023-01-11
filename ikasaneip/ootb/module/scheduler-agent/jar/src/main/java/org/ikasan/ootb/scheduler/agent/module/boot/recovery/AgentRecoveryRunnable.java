package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import java.util.*;
import java.util.stream.Collectors;

import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentRecoveryRunnable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AgentRecoveryRunnable.class);

    private final ContextInstanceRestService<ContextInstance> contextInstanceRestService;
    private final long minutesToKeepRetrying;
    private final String moduleName;
    private final ModuleService moduleService;

    public AgentRecoveryRunnable(ContextInstanceRestService contextInstanceRestService, long minutesToKeepRetrying, String moduleName, ModuleService moduleService) {
        this.contextInstanceRestService = contextInstanceRestService;
        this.minutesToKeepRetrying = minutesToKeepRetrying;
        this.moduleName = moduleName;
        this.moduleService = moduleService;
    }

    @Override
    public void run() {
        LOG.info("Importing context instances parameters to cache!");
        importInstances();
        LOG.info("Successfully recovered context instances at start up!");
    }

    /**
     * Note, by this point the standard Ikasan Recovery Manager has recovered the flows themselves, here we are merely
     * getting the instance information back from the dashboard.
     */
    private void importInstances() {
        Module<Flow> module = moduleService.getModule(moduleName);
        if (module != null) {
            ConfiguredResource<ConfiguredModuleConfiguration> configuredModule = (ConfiguredResource<ConfiguredModuleConfiguration>) module;
            ConfiguredModuleConfiguration configuredModuleConfiguration = configuredModule.getConfiguration();
            SchedulerAgentConfiguredModuleConfiguration configuration = (SchedulerAgentConfiguredModuleConfiguration) configuredModuleConfiguration;
            Map<String, String> flowContextMap = configuration.getFlowContextMap();

            Set<String> contextNames = flowContextMap.values().stream().filter(s -> !s.isBlank()).collect(Collectors.toSet());
            LOG.info("Recovering instances for " + contextNames);
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
                    // If the context instance from the dashboard relates to a plan that has been recovered, add it.
                    Map<String, ContextInstance> contextInstances = contextInstanceRestService.getAllInstancesDashboardThinksAgentShouldHandle(moduleName);
                    for (String correlationId : contextInstances.keySet()) {
                        ContextInstance contextInstance = contextInstances.get(correlationId);

                        // @TODO in theory we should have the correct parameters e.g. SPEL to inject back in if we need to.
                        if (contextNames.contains(contextInstance.getName())) {
                            ContextInstanceCache.instance().put(correlationId, contextInstances.get(correlationId));
                            LOG.info("Adding correlationId [" + correlationId + "] to the cache.");
                        } else {
                            LOG.error("The dashboard thinks this agent should be dealing correlationId " + contextInstance.getId() + " for the plan [" + contextInstance.getName() +
                                "] but there is no recovered plan to deal with it, the only plans available are " + contextNames);
                        }
                    }
                    LOG.info("Successfully recovered correlationId at start up for contexts: " + contextNames);
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
        } else {
            LOG.warn("Could not find module for: " + moduleName);
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
