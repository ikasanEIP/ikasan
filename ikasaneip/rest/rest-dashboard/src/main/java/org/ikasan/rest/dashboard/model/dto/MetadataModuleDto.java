package org.ikasan.rest.dashboard.model.dto;

public class MetadataModuleDto
{
    private String name;
    private String url;

    public MetadataModuleDto(String name, String url)
    {
        this.name = name;
        this.url = url;
    }

    public MetadataModuleDto()
    {
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
