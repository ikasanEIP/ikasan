package org.ikasan.exclusion.service;

import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.solr.SolrService;

import java.util.Date;
import java.util.List;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrExclusionServiceImpl implements SolrService<ExclusionEvent>, ExclusionManagementService<ExclusionEvent, String>
{

    private ExclusionEventDao<String,ExclusionEvent> exclusionEventDao;

    public SolrExclusionServiceImpl(ExclusionEventDao<String,ExclusionEvent> exclusionEventDao)
    {
        this.exclusionEventDao = exclusionEventDao;
        if(this.exclusionEventDao == null)
        {
            throw new IllegalArgumentException("exclusionEventDao cannot be null!");
        }
    }
    @Override
    public void save(ExclusionEvent save)
    {
        this.exclusionEventDao.save(save);
    }

    @Override
    public ExclusionEvent find(String moduleName, String flowName, String s)
    {
        return exclusionEventDao.find(moduleName, flowName, s);
    }

    @Override
    public long count(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String s)
    {
        return exclusionEventDao.rowCount(moduleName, flowName, startDate, endDate, s);
    }

    @Override
    public List<ExclusionEvent> find(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String s, int size)
    {
        return this.find(moduleName, flowName, startDate, endDate, s, size);
    }

    @Override
    public List<ExclusionEvent> find(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String s)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ExclusionEvent> findAll()
    {
        return this.exclusionEventDao.findAll();
    }

    @Override
    public void delete(String moduleName, String flowName, String s)
    {
        this.exclusionEventDao.delete(moduleName, flowName, s);
    }

    @Override
    public void delete(String errorUri)
    {
        this.exclusionEventDao.delete(errorUri);
    }

    @Override
    public ExclusionEvent find(String errorUri)
    {
        return this.exclusionEventDao.find(errorUri);
    }
}
