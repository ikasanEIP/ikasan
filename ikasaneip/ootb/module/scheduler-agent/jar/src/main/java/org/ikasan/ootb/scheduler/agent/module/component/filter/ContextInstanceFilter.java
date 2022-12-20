package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.quartz.impl.JobExecutionContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionContext;

import static org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache.*;

/**
 * This is how we control whether a Flow (Trigger Job) is allowed to run
 * @param <T>
 */
public class ContextInstanceFilter<T> implements Filter<T>, ConfiguredResource<ContextInstanceFilterConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(ContextInstanceFilter.class);
    protected static final String CORRELATION_ID = "correlationId";
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

            if (event instanceof JobExecutionContextImpl) {
                JobExecutionContextImpl jobExecutionContext = (JobExecutionContextImpl)event;
                String correlationId = (String)jobExecutionContext.getMergedJobDataMap().get(CORRELATION_ID);
                // If we have a trigger with a correlation ID not in the Cache - error
                // If we have a trigger with a correlation ID in cache or correlation ID is blank (the scenario to prevent job going into recovery) - allow through
                if (correlationId == null || correlationId.isEmpty()) {
                    LOG.warn("The correlation ID was [" + correlationId + "] for cron ID " +
                        (String)jobExecutionContext.getMergedJobDataMap().get(CORRELATION_ID) + "] and job [" +
                        jobExecutionContext.getJobDetail().getDescription() + "]");
                } else if (!existsInCache(correlationId)) {
                    String error = String.format("Expected to find the correlation ID [%s] in the ContextInstanceCache but it was not there, could only find id,plans [%s]", correlationId, getCorrelationIds());
                    LOG.error(error);
                    throw new ContextInstanceFilterException(error);
                } else {
                    return event;
                }
            } else {
                LOG.info("Event of type " + event + " ignored");
                // TODO Review with Mick, should be returning null?, perhaps tests not right ?
            }
        } else {
            ;
            // TODO Review with Mick, should be returning null?, perhaps tests not right ?
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
