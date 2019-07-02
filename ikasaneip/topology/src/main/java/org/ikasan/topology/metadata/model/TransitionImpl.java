package org.ikasan.topology.metadata.model;

import org.ikasan.spec.metadata.Transition;

public class TransitionImpl implements Transition
{
    private String from;
    private String to;
    private String name;

    @Override
    public String getFrom()
    {
        return from;
    }

    @Override
    public void setFrom(String from)
    {
        this.from = from;
    }

    @Override
    public String getTo()
    {
        return to;
    }

    @Override
    public void setTo(String to)
    {
        this.to = to;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }
}
