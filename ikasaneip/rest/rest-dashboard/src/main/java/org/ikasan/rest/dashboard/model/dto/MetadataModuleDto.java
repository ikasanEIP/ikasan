package org.ikasan.rest.dashboard.model.dto;

import java.util.List;

public class MetadataModuleDto
{
    private String name;
    private String url;
    private List<String> flows;

    public MetadataModuleDto(String name, String url, List<String> flows)
    {
        this.name = name;
        this.url = url;
        this.flows = flows;
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

    public List<String> getFlows()
    {
        return flows;
    }

    public void setFlows(List<String> flows)
    {
        this.flows = flows;
    }
}
