package org.ikasan.replay.service;

import org.ikasan.spec.replay.ReplayDao;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.replay.ReplayManagementService;
import org.ikasan.spec.solr.SolrService;

import java.util.Date;
import java.util.List;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrReplayServiceImpl implements SolrService<ReplayEvent>, ReplayManagementService<ReplayEvent, Object, Object> {

    private ReplayDao replayDao;

    public SolrReplayServiceImpl(ReplayDao replayDao)
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
    public List<Object> getReplayAudits(List<String> moduleNames, List<String> flowNames, String eventId,
                                        String user, Date startDate, Date endDate)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getReplayAuditById(Long id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Object> getReplayAuditEventsByAuditId(Long id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getNumberReplayAuditEventsByAuditId(Long id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReplayEvent getReplayEventById(Long id)
    {
        return this.replayDao.getReplayEventById(id);
    }
}
