package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterRule;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;

import java.io.File;
import java.util.List;

public class SchedulerFileFilter implements Filter<CorrelatedFileList>, ConfiguredResource<SchedulerFileFilterConfiguration> {

    private DryRunModeService dryRunModeService;
    private FilterRule filterRule;

    private SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
    private String configurationId;

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
    public CorrelatedFileList filter(CorrelatedFileList message) {
        if(this.dryRunModeService.getDryRunMode() || this.dryRunModeService.isJobDryRun(this.configuration.getJobName())) {
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

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }

    @Override
    public SchedulerFileFilterConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(SchedulerFileFilterConfiguration configuration) {
        this.configuration = configuration;
    }
}
