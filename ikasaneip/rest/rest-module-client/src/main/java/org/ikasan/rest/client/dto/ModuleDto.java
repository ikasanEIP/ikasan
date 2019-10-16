package org.ikasan.rest.client.dto;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

public class ModuleDto implements Serializable
{
    private String name;
    private List<FlowDto> flows;



    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<FlowDto> getFlows()
    {
        return flows;
    }

    public void setFlows(List<FlowDto> flows)
    {
        this.flows = flows;
    }

    @Override public String toString()
    {
        return new StringJoiner(", ", ModuleDto.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("flows=" + flows)
            .toString();
    }
}
