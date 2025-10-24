package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.ootb.scheduler.agent.module.component.router.AgentRecoveryNotCompleteException;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of a filter to process and filter ContextualisedScheduledProcessEvent messages.
 */
public class ScheduledContextEventContextInstancesActiveFilter implements Filter<ContextualisedScheduledProcessEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledContextEventContextInstancesActiveFilter.class);
    private final DryRunModeService dryRunModeService;

    public ScheduledContextEventContextInstancesActiveFilter(DryRunModeService dryRunModeService) {
        this.dryRunModeService = dryRunModeService;
        if (this.dryRunModeService == null) {
            throw new IllegalArgumentException("dryRunModeService cannot be null!");
        }
    }

    @Override
    public ContextualisedScheduledProcessEvent filter(ContextualisedScheduledProcessEvent event) {
        // We don't want to do any work until the context instance cache is initialised!
        if(!ContextInstanceCache.instance().isInitialisationComplete()) {
            throw new AgentRecoveryNotCompleteException("Agent instance recovery not complete" +
                ". Cannot process message until agent has resolved all running context instances from the Ikasan Scheduler Dashboard.");
        }

        if (dryRunModeService.getDryRunMode()) {
            event.setDryRun(true);
            return event;
        }

        for (String id : ContextInstanceCache.getCorrelationIds()) {
            if (ContextInstanceCache.instance().getByCorrelationId(id).getName().equals(event.getContextName())) {
                LOG.info("The ContextInstanceCache contains some running instances for job plan[%s]".formatted(event.getContextName()));
                return event;
            }
        }

        // If we get here there are no instances for the job plan in the context instance cache!
        LOG.info("The ContextInstanceCache does NOT contain and running instances for job plan[%s]".formatted(event.getContextName()));
        return null;
    }
}
