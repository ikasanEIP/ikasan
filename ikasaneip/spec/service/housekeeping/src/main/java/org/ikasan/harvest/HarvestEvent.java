package org.ikasan.harvest;

/**
 * Created by stewmi on 06/08/2017.
 */
public interface HarvestEvent
{
    /**
     * Set flag to indicate entity has been harvested.
     *
     * @param harvested
     */
    public void setHarvested(boolean harvested);
}
