package org.ikasan.configurationService.metadata.components;

import org.ikasan.configurationService.metadata.configuration.DummyConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;

public class ConfiguredProducer implements Producer<String>, ConfiguredResource<DummyConfiguration>
{
    private String configurationId = "CONFIGURATION_ID";

    private DummyConfiguration configuration = new DummyConfiguration();

    public ConfiguredProducer()
    {
    }

    public ConfiguredProducer(String configurationId, DummyConfiguration configuration)
    {
        this.configurationId = configurationId;
        this.configuration = configuration;
    }

    @Override public void invoke(String payload) throws EndpointException
    {
    }

    @Override public String getConfiguredResourceId()
    {
        return configurationId;
    }

    @Override public void setConfiguredResourceId(String id)
    {
    }

    @Override public DummyConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override public void setConfiguration(DummyConfiguration configuration)
    {
    }
}
