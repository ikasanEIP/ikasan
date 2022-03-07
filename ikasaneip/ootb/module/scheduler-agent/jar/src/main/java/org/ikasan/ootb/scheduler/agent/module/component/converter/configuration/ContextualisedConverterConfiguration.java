package org.ikasan.ootb.scheduler.agent.module.component.converter.configuration;

import java.util.List;

public class ContextualisedConverterConfiguration {

    private String contextId;
    private List<String> childContextIds;

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public List<String> getChildContextIds() {
        return childContextIds;
    }

    public void setChildContextIds(List<String> childContextId) {
        this.childContextIds = childContextId;
    }
}
