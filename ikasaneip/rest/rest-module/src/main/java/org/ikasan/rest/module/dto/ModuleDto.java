package org.ikasan.rest.module.dto;

import java.io.Serializable;
import java.util.List;

public class ModuleDto implements Serializable
{
    private String name;
    private List<FlowDto> flows;

    protected ModuleDto()
    {}

    public ModuleDto(String name, List<FlowDto> flows)
    {
        this.name = name;
        this.flows = flows;
    }

    public String getName()
    {
        return name;
    }

    public List<FlowDto> getFlows()
    {
        return flows;
    }
}
