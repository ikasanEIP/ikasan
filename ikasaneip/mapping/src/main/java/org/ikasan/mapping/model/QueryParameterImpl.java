package org.ikasan.mapping.model;

import org.ikasan.spec.mapping.QueryParameter;

/**
 * Created by Ikasan Development Team on 02/04/2017.
 */
public class QueryParameterImpl implements QueryParameter
{
    private String name;
    private String value;

    public QueryParameterImpl(String name, String value)
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
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
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
