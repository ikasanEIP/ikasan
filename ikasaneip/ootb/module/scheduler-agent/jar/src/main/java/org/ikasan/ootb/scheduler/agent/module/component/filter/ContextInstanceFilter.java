package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache.noneExistInCache;

/**
 * This is how we control whether a Flow (Trigger Job) is allowed to run
 * @param <T>
 */
public class ContextInstanceFilter<T> implements Filter<T>, ConfiguredResource<ContextInstanceFilterConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(ContextInstanceFilter.class);

    private ContextInstanceFilterConfiguration contextInstanceFilterConfiguration;
    private String configurationId;
    private DryRunModeService dryRunModeService;
    private boolean agentRecoveryActive;

    public ContextInstanceFilter(DryRunModeService dryRunModeService, boolean agentRecoveryActive) {
        this.dryRunModeService = dryRunModeService;
        if (this.dryRunModeService == null) {
            throw new IllegalArgumentException("dryRunModeService cannot be null!");
        }
        this.agentRecoveryActive = agentRecoveryActive;
    }

    @Override
    public T filter(T event) throws ContextInstanceFilterException {
        if (agentRecoveryActive) {
            if (dryRunModeService.getDryRunMode()) {
                return event;
            }

            // If just one contextIntanceId is found, allow through
            if (noneExistInCache(contextInstanceFilterConfiguration.getContextInstanceIds())) {
                String error = String.format("ContextInstanceCache does not contain instance for any of %s!",
                    contextInstanceFilterConfiguration.getContextInstanceIds());
                LOG.warn(error);
                throw new ContextInstanceFilterException(error);
            }
        }
        return event;
    }

    @Override
    public ContextInstanceFilterConfiguration getConfiguration() {
        return contextInstanceFilterConfiguration;
    }

    @Override
    public void setConfiguration(ContextInstanceFilterConfiguration configuration) {
        this.contextInstanceFilterConfiguration = configuration;
    }

    @Override
    public String getConfiguredResourceId() {
        return configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }
}
