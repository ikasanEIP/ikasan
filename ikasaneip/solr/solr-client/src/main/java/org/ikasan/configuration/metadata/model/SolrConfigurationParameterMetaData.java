package org.ikasan.configuration.metadata.model;

import org.ikasan.spec.metadata.ConfigurationParameterMetaData;

public class SolrConfigurationParameterMetaData implements ConfigurationParameterMetaData
{
    private Long id;
    private String name;
    private Object value;
    private String description;
    private String implementingClass;

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param value
     * @param description
     * @param implementingClass
     */
    public SolrConfigurationParameterMetaData(Long id, String name, Object value, String description, String implementingClass)
    {
        this.id = id;
        this.name = name;
        this.value = value;
        this.description = description;
        this.implementingClass = implementingClass;
    }

    /**
     * Default constructor
     */
    public SolrConfigurationParameterMetaData()
    {
    }

    @Override
    public Long getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Object getValue()
    {
        return this.value;
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

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(Object value)
    {
        this.value = value;
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
        final StringBuffer sb = new StringBuffer("SolrConfigurationParameterMetaData{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", value=").append(value);
        sb.append(", description='").append(description).append('\'');
        sb.append(", implementingClass='").append(implementingClass).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
