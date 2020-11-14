package org.ikasan.replay.service;

import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.replay.*;
import org.ikasan.spec.solr.SolrService;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.Date;
import java.util.List;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrReplayServiceImpl extends SolrServiceBase implements SolrService<ReplayEvent>, BatchInsert<ReplayEvent>
{
    private SolrReplayDao replayDao;

    public SolrReplayServiceImpl(SolrReplayDao replayDao)
    {
        this.replayDao = replayDao;
        if (this.replayDao == null)
        {
            throw new IllegalArgumentException("replayDao cannot be null!");
        }
    }

    @Override
    public void save(ReplayEvent save)
    {
        this.replayDao.setSolrUsername(this.solrUsername);
        this.replayDao.setSolrPassword(this.solrPassword);
        this.replayDao.save(save);
    }

    @Override
    public void save(List<ReplayEvent> save)
    {
        this.replayDao.setSolrUsername(this.solrUsername);
        this.replayDao.setSolrPassword(this.solrPassword);
        this.replayDao.save(save);
    }

    @Override
    public void insert(List<ReplayEvent> entities)
    {
        this.save(entities);
    }
}
