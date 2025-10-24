package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterRule;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;

public class SchedulerFileFilter implements Filter<FileWatcherJobEvent>, ConfiguredResource<SchedulerFileFilterConfiguration> {

    private FilterRule filterRule;

    private SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();
    private String configurationId;

    /**
     * Constructor
     *
     * @param filterRule The {@link FilterRule} instance evaluating incoming fileWatcherJobEvent.
     */
    public SchedulerFileFilter(FilterRule filterRule) {
        this.filterRule = filterRule;
        if(this.filterRule == null) {
            throw new IllegalArgumentException("filterRule cannot be null!");
        }
    }

    @Override
    public FileWatcherJobEvent filter(FileWatcherJobEvent fileWatcherJobEvent) {
        if(fileWatcherJobEvent.isDryRun()) {
            return fileWatcherJobEvent;
        }

        else {
            if(this.filterRule.accept(fileWatcherJobEvent)) {
                return fileWatcherJobEvent;
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
