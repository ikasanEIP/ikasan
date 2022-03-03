package org.ikasan.ootb.scheduler.agent.module.component.converter.configuration;

public class ContextualisedConverterConfiguration {

    private String contextId;
    private String childContextId;

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getChildContextId() {
        return childContextId;
    }

    public void setChildContextId(String childContextId) {
        this.childContextId = childContextId;
    }
}
