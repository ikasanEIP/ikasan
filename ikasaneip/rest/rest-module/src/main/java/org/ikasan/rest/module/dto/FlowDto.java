package org.ikasan.rest.module.dto;

import java.io.Serializable;

public class FlowDto implements Serializable
{
    private String name;
    private String state;

    protected FlowDto()
    {}

    public FlowDto(String name, String state)
    {
        this.name = name;
        this.state = state;
    }

    public String getName()
    {
        return name;
    }

    public String getState()
    {
        return state;
    }
}
