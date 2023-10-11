package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AgentRecoveryRunnable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AgentRecoveryRunnable.class);

    private final ContextInstanceRestService<ContextInstance> contextInstanceRestService;
    private final ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService;
    private final long minutesToKeepRetrying;
    private final String moduleName;
    private final ModuleService moduleService;

    public AgentRecoveryRunnable(ContextInstanceRestService contextInstanceRestService,
                                 ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService,
                                 long minutesToKeepRetrying, String moduleName, ModuleService moduleService) {
        this.contextInstanceRestService = contextInstanceRestService;
        this.contextInstanceIdentifierProvisionService = contextInstanceIdentifierProvisionService;
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
            long maxTimeToRetry = minutesToKeepRetrying * 60 * 1000;
            long startTimeMillis = System.currentTimeMillis();
            RuntimeException runtimeException = null;
            while (retryWaitTime < maxTimeToRetry) {
                if (System.currentTimeMillis() > startTimeMillis + maxTimeToRetry) {
                    break;
                }
                runtimeException = null;
                try {
                    // Reset instances to reflect what the dashboard thinks should be running
                    Map<String, ContextInstance> contextInstancesThatShouldBeLive = contextInstanceRestService.getAllInstancesDashboardThinksAgentShouldHandle(moduleName);
                    contextInstanceIdentifierProvisionService.reset(contextInstancesThatShouldBeLive);
                    LOG.info("Successfully recovered correlationId at start up for contexts: " + contextNames);
                    break;
                } catch (RuntimeException e) {
                    runtimeException = e;
                    retryWaitTime = 500L * attempts;
                    sleep(retryWaitTime);
                    attempts++;
                }
            }
            if (runtimeException != null) {
                String message
                    = String.format("Could not recover instances for agent in %d minutes. " +
                    "This is a fatal problem that needs to be resolved! Exception was %s", minutesToKeepRetrying, runtimeException.getMessage());
                LOG.error(message);
                throw new EndpointException(message);
            }
        }
        else {
            LOG.warn("Could not find module for: " + moduleName);
        }
    }

    private void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
