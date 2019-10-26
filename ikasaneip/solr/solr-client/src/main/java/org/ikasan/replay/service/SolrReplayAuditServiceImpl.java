package org.ikasan.replay.service;

import org.ikasan.replay.dao.SolrReplayAuditDao;
import org.ikasan.replay.model.SolrReplayAuditEvent;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;

public class SolrReplayAuditServiceImpl extends SolrServiceBase implements BatchInsert<SolrReplayAuditEvent>
{
    private SolrReplayAuditDao solrReplayAuditDao;

    public SolrReplayAuditServiceImpl(SolrReplayAuditDao solrReplayAuditDao)
    {
        this.solrReplayAuditDao = solrReplayAuditDao;
        if(this.solrReplayAuditDao == null)
        {
            throw new IllegalArgumentException("solrReplayAuditDao cannot be null!");
        }
    }

    @Override
    public void insert(List<SolrReplayAuditEvent> entities)
    {
        this.solrReplayAuditDao.setSolrUsername(this.solrUsername);
        this.solrReplayAuditDao.setSolrPassword(this.solrPassword);
        this.solrReplayAuditDao.insert(entities);
    }
}
