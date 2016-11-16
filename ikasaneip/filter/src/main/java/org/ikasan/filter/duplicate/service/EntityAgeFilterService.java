package org.ikasan.filter.duplicate.service;

import org.ikasan.filter.duplicate.model.FilterEntry;

/**
 * Created by stewmi on 09/07/2016.
 */
public interface EntityAgeFilterService
{
    /**
     * Method to determine if the current message is older than what is in the cache.
     *
     * @param message the message whose age to check
     * @return true if message is older, false otherwise
     */
    public boolean isOlderEntity(FilterEntry message);

    /**
     * Method to initialise the internals of the service.
     */
    public void initialise(String clientId);

    /**
     * Method to destroy internals of the service.
     */
    public void destroy();

    /**
     * Get flag stating messages older if timestamp equals.
     * @return
     */
    public boolean isOlderIfEquals();

    /**
     * Messages considered older if timestamp equals.
     *
     * @param olderIfEquals
     */
    public void setOlderIfEquals(boolean olderIfEquals);

}
