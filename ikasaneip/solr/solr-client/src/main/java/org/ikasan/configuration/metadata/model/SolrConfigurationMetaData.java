package org.ikasan.configuration.metadata.model;

import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;

import java.util.List;

public class SolrConfigurationMetaData implements ConfigurationMetaData<List<SolrConfigurationParameterMetaData>>
{
    private String configurationId;
    private List<SolrConfigurationParameterMetaData> parameters;
    private String description;
    private String implementingClass;

    /**
     * Constructor
     *
     * @param configurationId
     * @param parameters
     * @param description
     * @param implementingClass
     */
    public SolrConfigurationMetaData(String configurationId, List<SolrConfigurationParameterMetaData> parameters, String description, String implementingClass)
    {
        this.configurationId = configurationId;
        this.parameters = parameters;
        this.description = description;
        this.implementingClass = implementingClass;
    }

    /**
     * Default constructor
     */
    public SolrConfigurationMetaData()
    {
    }

    @Override
    public String getConfigurationId()
    {
        return this.configurationId;
    }

    @Override
    public List<SolrConfigurationParameterMetaData> getParameters()
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

    public void setParameters(List<SolrConfigurationParameterMetaData> parameters)
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

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("SolrConfigurationMetaData{");
        sb.append("configurationId='").append(configurationId).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append(", description='").append(description).append('\'');
        sb.append(", implementingClass='").append(implementingClass).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
