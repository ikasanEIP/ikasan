package org.ikasan.replay.service;

import org.ikasan.spec.replay.*;
import org.ikasan.spec.solr.SolrService;

import java.util.Date;
import java.util.List;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrReplayServiceImpl implements SolrService<ReplayEvent>, ReplayManagementService<ReplayEvent, ReplayAudit, ReplayAuditEvent> {

    private ReplayDao replayDao;
    private ReplayAuditDao<ReplayAudit, ReplayAuditEvent> replayAuditDao;

    public SolrReplayServiceImpl(ReplayDao replayDao, ReplayAuditDao<ReplayAudit, ReplayAuditEvent> replayAuditDao)
    {
        this.replayDao = replayDao;
        if (this.replayDao == null)
        {
            throw new IllegalArgumentException("replayDao cannot be null!");
        }
        this.replayAuditDao = replayAuditDao;
        if (this.replayAuditDao == null)
        {
            throw new IllegalArgumentException("replayAuditDao cannot be null!");
        }
    }

    @Override
    public void save(ReplayEvent save)
    {
        this.replayDao.saveOrUpdate(save);
    }

    /**
     * (non-Javadoc)
     * @see org.ikasan.spec.replay.ReplayManagementService#getReplayEvents(List, List, String, String, Date, Date)
     */
    @Override
    public List<ReplayEvent> getReplayEvents(List<String> moduleNames, List<String> flowNames, String eventId
            , String payloadContent, Date fromDate, Date toDate)
    {
        return this.replayDao.getReplayEvents(moduleNames, flowNames, eventId, payloadContent, fromDate, toDate);
    }

    @Override
    public List<ReplayAudit> getReplayAudits(List<String> moduleNames, List<String> flowNames, String eventId,
                                        String user, Date startDate, Date endDate)
    {
        return this.replayAuditDao.getReplayAudits(moduleNames, flowNames, eventId, user, startDate, endDate);
    }

    @Override
    public ReplayAudit getReplayAuditById(Long id)
    {
        return this.replayAuditDao.getReplayAuditById(id);
    }

    @Override
    public List<ReplayAuditEvent> getReplayAuditEventsByAuditId(Long id)
    {
        return this.replayAuditDao.getReplayAuditEventsByAuditId(id);
    }

    @Override
    public Long getNumberReplayAuditEventsByAuditId(Long id)
    {
        return this.replayAuditDao.getNumberReplayAuditEventsByAuditId(id);
    }

    @Override
    public ReplayEvent getReplayEventById(Long id)
    {
        return this.replayDao.getReplayEventById(id);
    }
}
