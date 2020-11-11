package org.ikasan.systemevent.service;

import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrService;
import org.ikasan.spec.solr.SolrServiceBase;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.systemevent.dao.SolrSystemEventDao;

import java.util.List;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrSystemEventServiceImpl extends SolrServiceBase implements SolrService<SystemEvent>, BatchInsert<SystemEvent>
{

    private SolrSystemEventDao systemEventDao;

    public SolrSystemEventServiceImpl(SolrSystemEventDao systemEventDao)
    {
        this.systemEventDao = systemEventDao;
        if(this.systemEventDao == null)
        {
            throw new IllegalArgumentException("systemEventDao cannot be null!");
        }
    }

    @Override
    public void insert(List<SystemEvent> systemEvents)
    {
        this.save(systemEvents);
    }

    @Override
    public void save(SystemEvent systemEvent)
    {
        this.systemEventDao.setSolrUsername(this.solrUsername);
        this.systemEventDao.setSolrPassword(this.solrPassword);
        systemEventDao.save(systemEvent);
    }

    @Override
    public void save(List<SystemEvent> systemEvents)
    {
        this.systemEventDao.setSolrUsername(this.solrUsername);
        this.systemEventDao.setSolrPassword(this.solrPassword);
        systemEventDao.save(systemEvents);

    }
}
