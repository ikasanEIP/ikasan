package org.ikasan.exclusion.service;

import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrService;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrExclusionServiceImpl extends SolrServiceBase implements SolrService<ExclusionEvent>, BatchInsert<ExclusionEvent>
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
    public void insert(List<ExclusionEvent> entities)
    {
        this.save(entities);
    }
}
