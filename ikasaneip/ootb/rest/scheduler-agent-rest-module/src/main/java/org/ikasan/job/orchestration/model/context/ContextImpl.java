package org.ikasan.job.orchestration.model.context;

import org.ikasan.spec.scheduled.context.model.*;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.util.List;
import java.util.Map;

public class ContextImpl<CONTEXT extends Context, CONTEXT_PARAM, JOB extends SchedulerJob, JOB_LOCK extends JobLock>
    extends AbstractContext<CONTEXT, JOB, JOB_LOCK>
    implements Context<CONTEXT, CONTEXT_PARAM, JOB, JOB_LOCK> {
    protected String name;
    protected String description;
    protected String timezone;
    protected Map<Long, Long> blackoutWindowDateTimeRanges;
    protected List<String> blackoutWindowCronExpressions;
    protected List<JobDependency> jobDependencies;
    protected List<ContextDependency> contextDependencies;
    protected List<CONTEXT_PARAM> contextParameters;
    protected String timeWindowStart;
    protected long ttl;
    protected String environmentGroup;
    private boolean isQuartzScheduleDrivenJobsDisabledForContext = false;
    int treeViewExpandLevel = 1;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getTimezone() {
        return timezone;
    }

    @Override
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public Map<Long, Long> getBlackoutWindowDateTimeRanges() {
        return blackoutWindowDateTimeRanges;
    }

    @Override
    public void setBlackoutWindowDateTimeRanges(Map<Long, Long> blackoutWindowDateTimeRanges) {
        this.blackoutWindowDateTimeRanges = blackoutWindowDateTimeRanges;
    }

    @Override
    public List<String> getBlackoutWindowCronExpressions() {
        return blackoutWindowCronExpressions;
    }

    @Override
    public void setBlackoutWindowCronExpressions(List<String> blackoutWindowCronExpressions) {
        this.blackoutWindowCronExpressions = blackoutWindowCronExpressions;
    }

    @Override
    public List<CONTEXT_PARAM> getContextParameters() {
        return contextParameters;
    }

    @Override
    public void setContextParameters(List<CONTEXT_PARAM> contextParameters) {
        this.contextParameters = contextParameters;
    }

    @Override
    public List<JobDependency> getJobDependencies() {
        return jobDependencies;
    }

    @Override
    public void setJobDependencies(List<JobDependency> jobDependencies) {
        this.jobDependencies = jobDependencies;
    }

    @Override
    public List<ContextDependency> getContextDependencies() {
        return contextDependencies;
    }

    @Override
    public void setContextDependencies(List<ContextDependency> contextDependencies) {
        this.contextDependencies = contextDependencies;
    }

    @Override
    public Map<String, CONTEXT> getContextsMap() {
        return contextsMap;
    }

    @Override
    public String getTimeWindowStart() {
        return timeWindowStart;
    }

    @Override
    public void setTimeWindowStart(String timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }

    @Override
    public long getContextTtlMilliseconds() {
        return this.ttl;
    }

    @Override
    public void setContextTtlMilliseconds(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public String getEnvironmentGroup() {
        return environmentGroup;
    }

    @Override
    public void setEnvironmentGroup(String environmentGroup) {
        this.environmentGroup = environmentGroup;
    }

    @Override
    public boolean isQuartzScheduleDrivenJobsDisabledForContext() {
        return isQuartzScheduleDrivenJobsDisabledForContext;
    }

    @Override
    public void setQuartzScheduleDrivenJobsDisabledForContext(boolean quartzScheduleDrivenJobsDisabledForContext) {
        isQuartzScheduleDrivenJobsDisabledForContext = quartzScheduleDrivenJobsDisabledForContext;
    }

    @Override
    public int getTreeViewExpandLevel() {
        return treeViewExpandLevel;
    }

    @Override
    public void setTreeViewExpandLevel(int treeViewExpandLevel) {
        this.treeViewExpandLevel = treeViewExpandLevel;
    }
}
