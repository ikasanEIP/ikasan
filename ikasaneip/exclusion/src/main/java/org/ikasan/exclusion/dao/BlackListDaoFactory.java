package org.ikasan.exclusion.dao;

import org.ikasan.exclusion.model.BlackListLinkedHashMap;

/**
 * Created by Ikasan Development Team on 18/05/2017.
 */
public class BlackListDaoFactory
{
    private Integer blackListSize;

    /**
     * Constructor
     *
     * @param blackListSize
     */
    public BlackListDaoFactory(Integer blackListSize)
    {
        this.blackListSize = blackListSize;
        if(this.blackListSize == null)
        {
            throw new IllegalArgumentException("blackListSize cannot be null!");
        }
    }

    /**
     * Get the BlackListDao
     *
     * @return
     */
    public BlackListDao getBlackListDao()
    {
        BlackListLinkedHashMap blackListLinkedHashMap = new BlackListLinkedHashMap(blackListSize);

        return new MapBlackListDao(blackListLinkedHashMap);
    }

}
