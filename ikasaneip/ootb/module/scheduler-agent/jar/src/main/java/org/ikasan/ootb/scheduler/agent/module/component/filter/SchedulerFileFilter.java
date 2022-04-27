package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterRule;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;

import java.io.File;
import java.util.List;

public class SchedulerFileFilter implements Filter<List<File>> {

    private DryRunModeService dryRunModeService;
    private FilterRule filterRule;

    /**
     * Constructor
     *
     * @param filterRule The {@link FilterRule} instance evaluating incoming message.
     */
    public SchedulerFileFilter(FilterRule filterRule, DryRunModeService dryRunModeService) {
        this.filterRule = filterRule;
        if(this.filterRule == null) {
            throw new IllegalArgumentException("filterRule cannot be null!");
        }
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
            if(this.filterRule.accept(message)) {
                return message;
            }
            else {
                return null;
            }
        }
    }
}
