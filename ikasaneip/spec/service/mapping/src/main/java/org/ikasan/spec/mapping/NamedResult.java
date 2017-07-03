package org.ikasan.spec.mapping;

/**
 * Created by Ikasan Development Team on 16/05/2017.
 */
public interface NamedResult
{
    /**
     * Get the query parameter name.
     *
     * @return
     */
    public String getName();

    /**
     * Get the query parameter value
     * @return
     */
    public String getValue();

}
