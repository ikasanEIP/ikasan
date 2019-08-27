package org.ikasan.rest.dashboard.model.metadata.configuration;

import org.ikasan.spec.metadata.ConfigurationParameterMetaData;

public class ConfigurationParameterMetaDataImpl<T> implements ConfigurationParameterMetaData<T>
{
    protected Long id;

    /** configuration name */
    protected String name;

    /** configuration value */
    protected T value;

    /** configuration description */
    protected String description;

    /** configuration description */
    protected String implementingClass;

    public ConfigurationParameterMetaDataImpl() { }

    public ConfigurationParameterMetaDataImpl(Long id, String name, T value, String description,
        String implementingClass)
    {
        this.id = id;
        this.name = name;
        this.value = value;
        this.description = description;
        this.implementingClass = implementingClass;
    }

    @Override public Long getId()
    {
        return id;
    }

    @Override public String getName()
    {
        return name;
    }

    @Override public T getValue()
    {
        return value;
    }

    @Override public String getDescription()
    {
        return description;
    }

    @Override public String getImplementingClass()
    {
        return implementingClass;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(T value)
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
}
