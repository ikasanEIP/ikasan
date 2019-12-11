package org.ikasan.dashboard.ui.visualisation.adapter.model;

public class Flow
{
    private Correlator correlator;
    private String id;
    private String name;
    private Integer x;
    private Integer y;


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getX()
    {
        return x;
    }

    public void setX(Integer x)
    {
        this.x = x;
    }

    public Integer getY()
    {
        return y;
    }

    public void setY(Integer y)
    {
        this.y = y;
    }

    public Correlator getCorrelator()
    {
        return correlator;
    }

    public void setCorrelator(Correlator correlator)
    {
        this.correlator = correlator;
    }
}
