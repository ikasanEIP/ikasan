package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextInstanceFilter<T> implements Filter<T>, ConfiguredResource<ContextInstanceFilterConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(ContextInstanceFilter.class);


    private ContextInstanceFilterConfiguration contextInstanceFilterConfiguration;
    private String configurationId;
    private DryRunModeService dryRunModeService;

    public ContextInstanceFilter(DryRunModeService dryRunModeService) {
        this.dryRunModeService = dryRunModeService;
        if (this.dryRunModeService == null) {
            throw new IllegalArgumentException("dryRunModeService cannot be null!");
        }
    }

    @Override
    public T filter(T event) throws ContextInstanceFilterException {
        if (dryRunModeService.getDryRunMode()) {
            return event;
        }

        ContextInstance instance = ContextInstanceCache.instance().getByContextName(contextInstanceFilterConfiguration.getContextName());

        if (instance == null) {
            LOG.warn(String.format("ContextInstanceCache does not contain instance for %s!",
                contextInstanceFilterConfiguration.getContextName()));
            throw new ContextInstanceFilterException(String.format("ContextInstanceCache does not contain instance for %s!",
                contextInstanceFilterConfiguration.getContextName()));
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
