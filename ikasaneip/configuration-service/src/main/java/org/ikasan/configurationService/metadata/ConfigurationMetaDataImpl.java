package org.ikasan.configurationService.metadata;

import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;

import java.util.List;
import java.util.StringJoiner;

public class ConfigurationMetaDataImpl implements ConfigurationMetaData<List<ConfigurationParameterMetaData>>
{
    /**
     * runtime configuration identifier
     */
    protected String configurationId;

    /**
     * runtime configuration description
     */
    protected String description;

    /**
     * configuration implementingClass
     */
    protected String implementingClass;

    /**
     * configuration parameters within this configuration
     */
    protected List<ConfigurationParameterMetaData> parameters;

    public ConfigurationMetaDataImpl()
    {
    }

    public ConfigurationMetaDataImpl(String configurationId, String description,String implementingClass,
        List<ConfigurationParameterMetaData> parameters)
    {
        this.configurationId = configurationId;
        this.description = description;
        this.implementingClass = implementingClass;
        this.parameters = parameters;
    }

    @Override public String getConfigurationId()
    {
        return configurationId;
    }

    public void setConfigurationId(String configurationId)
    {
        this.configurationId = configurationId;
    }

    @Override public List<ConfigurationParameterMetaData> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<ConfigurationParameterMetaData> parameters)
    {
        this.parameters = parameters;
    }

    @Override public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override public String getImplementingClass()
    {
        return implementingClass;
    }

    public void setImplementingClass(String implementingClass)
    {
        this.implementingClass = implementingClass;
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", ConfigurationMetaDataImpl.class.getSimpleName() + "[", "]")
            .add("configurationId='" + configurationId + "'").add("description='" + description + "'")
            .add("implementingClass='" + implementingClass + "'").add("parameters=" + parameters).toString();
    }
}
