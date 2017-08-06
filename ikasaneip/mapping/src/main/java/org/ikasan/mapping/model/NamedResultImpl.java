package org.ikasan.mapping.model;

import org.ikasan.spec.mapping.NamedResult;

/**
 * Created by Ikasan Development Team on 16/05/2017.
 */
public class NamedResultImpl implements NamedResult
{
    private String name;
    private String value;

    public NamedResultImpl(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "NamedResultImpl{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
