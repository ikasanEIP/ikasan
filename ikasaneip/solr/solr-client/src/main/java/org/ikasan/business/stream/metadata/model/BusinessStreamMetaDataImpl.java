package org.ikasan.business.stream.metadata.model;

import org.ikasan.spec.metadata.BusinessStreamMetaData;

public class BusinessStreamMetaDataImpl implements BusinessStreamMetaData
{
    private String id;
    private String name;
    private String json;

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getJson()
    {
        return this.json;
    }

    @Override
    public void setJson(String json)
    {
        this.json = json;
    }
}
