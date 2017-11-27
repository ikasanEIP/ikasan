package org.ikasan.exclusion.service;

import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.solr.SolrService;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.Date;
import java.util.List;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrExclusionServiceImpl extends SolrServiceBase implements SolrService<ExclusionEvent>, ExclusionManagementService<ExclusionEvent, String>
{

    private SolrExclusionEventDao exclusionEventDao;

    public SolrExclusionServiceImpl(SolrExclusionEventDao exclusionEventDao)
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
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
        this.exclusionEventDao.save(save);
    }

    @Override
    public void save(List<ExclusionEvent> save)
    {
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
        this.exclusionEventDao.save(save);
    }

    @Override
    public ExclusionEvent find(String moduleName, String flowName, String s)
    {
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
        return exclusionEventDao.find(moduleName, flowName, s);
    }

    @Override
    public long count(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String s)
    {
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
        return exclusionEventDao.rowCount(moduleName, flowName, startDate, endDate, s);
    }

    @Override
    public List<ExclusionEvent> find(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String s, int size)
    {
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
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
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
        return this.exclusionEventDao.findAll();
    }

    @Override
    public void delete(String moduleName, String flowName, String s)
    {
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
        this.exclusionEventDao.delete(moduleName, flowName, s);
    }

    @Override
    public void delete(String errorUri)
    {
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
        this.exclusionEventDao.delete(errorUri);
    }

    @Override
    public ExclusionEvent find(String errorUri)
    {
        this.exclusionEventDao.setSolrUsername(this.solrUsername);
        this.exclusionEventDao.setSolrPassword(this.solrPassword);
        return this.exclusionEventDao.find(errorUri);
    }
}
