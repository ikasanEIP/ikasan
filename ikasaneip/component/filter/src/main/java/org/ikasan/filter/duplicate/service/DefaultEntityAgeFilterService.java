package org.ikasan.filter.duplicate.service;

import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ikasan Development Team
 */
public class DefaultEntityAgeFilterService implements EntityAgeFilterService
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(DefaultEntityAgeFilterService.class);

    private FilteredMessageDao filteredMessageDao;

    private ConcurrentHashMap<String,DefaultFilterEntry> filterEntryMap = new ConcurrentHashMap<String,DefaultFilterEntry>();

    private boolean initialised = false;
    private boolean olderIfEquals = true;

    /**
     * Constructor
     *
     * @param filteredMessageDao
     */
    public DefaultEntityAgeFilterService(FilteredMessageDao filteredMessageDao)
    {
        this.filteredMessageDao = filteredMessageDao;
        if(this.filteredMessageDao == null)
        {
            throw new IllegalArgumentException("filteredMessageDao cannot be null!");
        }
    }

    @Override
    public boolean isOlderEntity(FilterEntry entry)
    {
        DefaultFilterEntry cachedEntry = this.filterEntryMap.get(entry.getCriteria() + entry.getClientId());

        if(cachedEntry == null)
        {
            this.filteredMessageDao.save(entry);
            filterEntryMap.put(entry.getCriteria() + entry.getClientId(), (DefaultFilterEntry)entry);

            return false;
        }

        long cachedEntryUpdatedTimeMilliseconds = Long.parseLong(cachedEntry.getCriteriaDescription());
        long entryUpdatedTimeMilliseconds = Long.parseLong(entry.getCriteriaDescription());

        logger.debug("Comparing cached time: [" + cachedEntryUpdatedTimeMilliseconds + "] to last updated time" +
                "[" + entryUpdatedTimeMilliseconds + "] for criteria: [" + entry.getCriteria() + "] and " +
                " client: [" + entry.getClientId() + "]");

        if((cachedEntryUpdatedTimeMilliseconds <= entryUpdatedTimeMilliseconds && !olderIfEquals)
                || (cachedEntryUpdatedTimeMilliseconds < entryUpdatedTimeMilliseconds))
        {
            if(cachedEntryUpdatedTimeMilliseconds != entryUpdatedTimeMilliseconds)
            {
                cachedEntry.setCriteriaDescription(entry.getCriteriaDescription());
                cachedEntry.setExpiry(entry.getExpiry());
                cachedEntry.setCreatedDateTime(entry.getCreatedDateTime());
                this.filteredMessageDao.saveOrUpdate(cachedEntry);

                logger.debug("Is older: [false]. Saving: " + cachedEntry);
            }
            else
            {
                logger.debug("Is older: [false].");
            }

            return false;
        }
        else
        {
            logger.debug("Is older: [true].");
            return true;
        }
    }

    @Override
    public void initialise(String clientId)
    {
        if(this.initialised)
        {
            logger.info("Entity age filter already initialised. There are "
                    + filterEntryMap.size() + " filter entries for client id:" + clientId);
        }

        List<FilterEntry> filterEntries = this.filteredMessageDao.findMessages(clientId);

        for(FilterEntry filterEntry: filterEntries)
        {
            filterEntryMap.put(filterEntry.getCriteria() + clientId, (DefaultFilterEntry)filterEntry);
        }

        logger.info("Initialising entity age filter cache. Added "
                + filterEntryMap.size() + " filter entries for client id:" + clientId);

        this.initialised = true;
    }

    @Override
    public void destroy()
    {
        this.filterEntryMap.clear();
        this.initialised = false;
    }

    public boolean isOlderIfEquals()
    {
        return olderIfEquals;
    }

    public void setOlderIfEquals(boolean olderIfEquals)
    {
        this.olderIfEquals = olderIfEquals;
    }
}
