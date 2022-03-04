package org.ikasan.ootb.scheduler.agent.module.component.converter.configuration;

import java.util.List;

public class ContextualisedConverterConfiguration {

    private String contextId;
    private List<String> childContextId;

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public List<String> getChildContextId() {
        return childContextId;
    }

    public void setChildContextId(List<String> childContextId) {
        this.childContextId = childContextId;
    }
}
