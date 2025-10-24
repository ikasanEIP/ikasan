package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.ootb.scheduler.agent.module.component.router.AgentRecoveryNotCompleteException;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileWatcherJobEventContextInstancesActiveFilter implements Filter<FileWatcherJobEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(FileWatcherJobEventContextInstancesActiveFilter.class);
    private final DryRunModeService dryRunModeService;

    public FileWatcherJobEventContextInstancesActiveFilter(DryRunModeService dryRunModeService) {
        this.dryRunModeService = dryRunModeService;
        if (this.dryRunModeService == null) {
            throw new IllegalArgumentException("dryRunModeService cannot be null!");
        }
    }

    @Override
    public FileWatcherJobEvent filter(FileWatcherJobEvent event) {
        // We don't want to do any work until the context instance cache is initialised!
        if(!ContextInstanceCache.instance().isInitialisationComplete()) {
            throw new AgentRecoveryNotCompleteException("Agent instance recovery not complete" +
                ". Cannot process message until agent has resolved all running context instances from the Ikasan Scheduler Dashboard.");
        }

        if (dryRunModeService.getDryRunMode()) {
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
