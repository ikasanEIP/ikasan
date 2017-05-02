package org.ikasan.mapping.model;

import org.ikasan.spec.mapping.QueryParameter;

/**
 * Created by Ikasan Development Team on 02/04/2017.
 */
public class QueryParameterImpl implements QueryParameter
{
    private String name;
    private String value;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "QueryParameter{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
