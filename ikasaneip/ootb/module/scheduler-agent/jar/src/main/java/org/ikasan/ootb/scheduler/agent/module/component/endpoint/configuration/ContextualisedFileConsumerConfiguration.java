package org.ikasan.ootb.scheduler.agent.module.component.endpoint.configuration;

import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;

public class ContextualisedFileConsumerConfiguration extends FileConsumerConfiguration {

    private String contextId;

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
}
