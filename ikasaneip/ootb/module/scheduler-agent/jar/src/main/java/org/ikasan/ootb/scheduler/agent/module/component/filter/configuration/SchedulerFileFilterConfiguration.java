package org.ikasan.ootb.scheduler.agent.module.component.filter.configuration;

public class SchedulerFileFilterConfiguration {

    private String jobName;
    private boolean useCorrelationIdInCriteria = true;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public boolean isUseCorrelationIdInCriteria() {
        return useCorrelationIdInCriteria;
    }

    public void setUseCorrelationIdInCriteria(boolean useCorrelationIdInCriteria) {
        this.useCorrelationIdInCriteria = useCorrelationIdInCriteria;
    }
}
