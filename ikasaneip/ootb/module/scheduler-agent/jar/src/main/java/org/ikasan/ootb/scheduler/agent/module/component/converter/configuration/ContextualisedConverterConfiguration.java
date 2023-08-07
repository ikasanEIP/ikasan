package org.ikasan.ootb.scheduler.agent.module.component.converter.configuration;

import java.util.List;

public class ContextualisedConverterConfiguration {

    private String contextName;
    private String jobName;
    private List<String> childContextNames;

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<String> getChildContextNames() {
        return childContextNames;
    }

    public void setChildContextNames(List<String> childContextId) {
        this.childContextNames = childContextId;
    }
}
