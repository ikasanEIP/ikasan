package org.ikasan.filter.duplicate.service;

import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ikasan Development Team on 09/07/2016.
 */
public class DefaultEntityAgeFilterService implements EntityAgeFilterService
{
    private FilteredMessageDao filteredMessageDao;

    private ConcurrentHashMap<Integer,DefaultFilterEntry> filterEntryMap = new ConcurrentHashMap<Integer,DefaultFilterEntry>();

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
        DefaultFilterEntry cachedEntry = this.filterEntryMap.get(entry.getCriteria());

        if(cachedEntry == null)
        {
            this.filteredMessageDao.save(entry);
            filterEntryMap.put(entry.getCriteria(), (DefaultFilterEntry)entry);

            return false;
        }

        long cachedEntryUpdatedTimeMilliseconds = Long.parseLong(cachedEntry.getCriteriaDescription());
        long entryUpdatedTimeMilliseconds = Long.parseLong(entry.getCriteriaDescription());

        if(cachedEntryUpdatedTimeMilliseconds < entryUpdatedTimeMilliseconds)
        {
            this.filteredMessageDao.saveOrUpdate(entry);
            cachedEntry.setCriteriaDescription(entry.getCriteriaDescription());

            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void initialise(String clientId)
    {
        List<FilterEntry> filterEntries = this.filteredMessageDao.findMessages(clientId);

        for(FilterEntry filterEntry: filterEntries)
        {
            filterEntryMap.put(filterEntry.getCriteria(), (DefaultFilterEntry)filterEntry);
        }
    }

    @Override
    public void destroy()
    {
        this.filterEntryMap.clear();
    }
}
