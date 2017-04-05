package org.ikasan.spec.mapping;

/**
 * Created by stewmi on 03/04/2017.
 */
public interface QueryParameter
{
    /**
     * Get the query parameter name.
     *
     * @return
     */
    public String getName();

    /**
     * Set the query parameter name.
     * @param name
     */
    public void setName(String name);

    /**
     * Get the query parameter value
     * @return
     */
    public String getValue();

    /**
     * Set the query parameter value.
     *
     * @param value
     */
    public void setValue(String value);
}
