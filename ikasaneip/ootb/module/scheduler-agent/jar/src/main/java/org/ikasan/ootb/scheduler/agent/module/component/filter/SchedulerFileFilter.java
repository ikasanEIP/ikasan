package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.filter.DefaultMessageFilter;
import org.ikasan.spec.component.filter.FilterRule;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;

import java.io.File;
import java.util.List;

public class SchedulerFileFilter extends DefaultMessageFilter<List<File>> {

    private DryRunModeService dryRunModeService;

    /**
     * Constructor
     *
     * @param filterRule The {@link FilterRule} instance evaluating incoming message.
     */
    public SchedulerFileFilter(FilterRule filterRule, DryRunModeService dryRunModeService) {
        super(filterRule);
        this.dryRunModeService = dryRunModeService;
        if(this.dryRunModeService == null) {
            throw new IllegalArgumentException("dryRunModeService cannot be null!");
        }
    }

    @Override
    public List<File> filter(List<File> message) {
        if(dryRunModeService.getDryRunMode()) {
            return message;
        }
        else {
            return super.filter(message);
        }
    }
}
