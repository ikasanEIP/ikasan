package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.ootb.scheduler.agent.module.configuration.HousekeepLogFilesProcessConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;

public class HousekeepLogFilesProcess<T> implements Producer<T>, ConfiguredResource<HousekeepLogFilesProcessConfiguration> {

    private HousekeepLogFilesProcessConfiguration configuration;
    private String configuredResourceId;

    @Override
    public void invoke(T payload) throws EndpointException {



    }

    @Override
    public HousekeepLogFilesProcessConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(HousekeepLogFilesProcessConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configuredResourceId = id;
    }
}
