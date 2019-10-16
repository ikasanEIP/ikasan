package org.ikasan.rest.client.dto;

import java.io.Serializable;
import java.util.StringJoiner;

public class FlowDto implements Serializable
{
    private String name;
    private String state;


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    @Override public String toString()
    {
        return new StringJoiner(", ", FlowDto.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("state='" + state + "'")
            .toString();
    }
}
