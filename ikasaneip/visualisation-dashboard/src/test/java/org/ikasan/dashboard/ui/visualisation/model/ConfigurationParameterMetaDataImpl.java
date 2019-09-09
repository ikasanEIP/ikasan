package org.ikasan.dashboard.ui.visualisation.model;

import org.ikasan.spec.metadata.ConfigurationParameterMetaData;

public class ConfigurationParameterMetaDataImpl implements ConfigurationParameterMetaData
{
    private Long id;
    private String name;
    private Object value;
    private String description;
    private String implementingClass;


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
}
