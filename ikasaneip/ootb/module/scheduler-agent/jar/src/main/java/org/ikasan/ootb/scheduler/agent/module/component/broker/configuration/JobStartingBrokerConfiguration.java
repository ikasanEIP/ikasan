package org.ikasan.ootb.scheduler.agent.module.component.broker.configuration;

import java.util.List;

public class JobStartingBrokerConfiguration {

    /**
     * A list of execution environment to add a space to the context parameters if the context parameter value is 
     * empty
     */
    private List<String> environmentToAddSpaceForEmptyContextParam;

    public List<String> getEnvironmentToAddSpaceForEmptyContextParam() {
        return environmentToAddSpaceForEmptyContextParam;
    }

    public void setEnvironmentToAddSpaceForEmptyContextParam(List<String> environmentToAddSpaceForEmptyContextParam) {
        this.environmentToAddSpaceForEmptyContextParam = environmentToAddSpaceForEmptyContextParam;
    }
}