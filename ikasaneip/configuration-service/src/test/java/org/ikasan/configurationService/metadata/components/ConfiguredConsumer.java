package org.ikasan.configurationService.metadata.components;

import org.ikasan.configurationService.metadata.configuration.DummyConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;

public class ConfiguredConsumer implements Consumer, ConfiguredResource<DummyConfiguration>
{
    private String configurationId = "CONFIGURATION_ID";
    private DummyConfiguration configuration = new DummyConfiguration();

    @Override
    public String getConfiguredResourceId()
    {
        return configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id)
    {

    }

    @Override
    public DummyConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(DummyConfiguration configuration)
    {

    }

    @Override
    public void setListener(Object o)
    {

    }

    @Override
    public void setEventFactory(Object o)
    {

    }

    @Override
    public Object getEventFactory()
    {
        return null;
    }

    @Override
    public void start()
    {

    }

    @Override
    public boolean isRunning()
    {
        return false;
    }

    @Override
    public void stop()
    {

    }
}
