package org.ikasan.dashboard.ui.visualisation.model;

import org.ikasan.spec.metadata.ConfigurationMetaData;

import java.util.List;

public class ConfigurationMetaDataImpl implements ConfigurationMetaData<List<ConfigurationParameterMetaDataImpl>>
{
    private String configurationId;
    private List<ConfigurationParameterMetaDataImpl> parameters;
    private String description;
    private String implementingClass;


    @Override
    public String getConfigurationId()
    {
        return this.configurationId;
    }

    @Override
    public List<ConfigurationParameterMetaDataImpl> getParameters()
    {
        return this.parameters;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public String getImplementingClass()
    {
        return this.implementingClass;
    }

    public void setConfigurationId(String configurationId)
    {
        this.configurationId = configurationId;
    }

    public void setParameters(List<ConfigurationParameterMetaDataImpl> parameters)
    {
        this.parameters = parameters;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setImplementingClass(String implementingClass)
    {
        this.implementingClass = implementingClass;
    }
}
