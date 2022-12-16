package org.ikasan.ootb.scheduler.agent.module.component.filter.configuration;

import java.util.*;

public class ContextInstanceFilterConfiguration {

    private List<String> contextInstanceIds = new ArrayList<>();
    private String jobName;

    public void setContextInstanceIds(List<String> contextInstanceIds) {
        this.contextInstanceIds = contextInstanceIds;
    }
    public List<String> getContextInstanceIds() { return contextInstanceIds; }
    public void removeContextInstanceId(String contextInstanceId) {
        contextInstanceIds.remove(contextInstanceId);
    }

    public void addContextInstanceId(String contextInstanceId) {
        this.contextInstanceIds.add(contextInstanceId);
    }

    public void addContextInstanceIds(List<String> newContextInstanceIds) {
        this.contextInstanceIds.addAll(newContextInstanceIds);
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
