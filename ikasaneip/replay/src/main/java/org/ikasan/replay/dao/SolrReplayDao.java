package org.ikasan.replay.dao;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.spec.solr.SolrDaoBase;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by stewmi on 25/08/2017.
 */
public class SolrReplayDao extends SolrDaoBase implements ReplayDao
{
    private static Logger logger = Logger.getLogger(SolrReplayDao.class);

    /** handle to the serialiser factory */
    private SerialiserFactory serialiserFactory;

    /**
     * We need to give this dao it's context.
     */
    public static final String REPLAY = "replay";

    public SolrReplayDao(SerialiserFactory serialiserFactory)
    {
        this.serialiserFactory = serialiserFactory;
        if(this.serialiserFactory == null)
        {
            throw new IllegalArgumentException("serialiserFactory cannot be null!");
        }
    }

    @Override
    public void saveOrUpdate(ReplayEvent replayEvent)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        String event = null;
        try
        {
            event = (String)serialiserFactory.getDefaultSerialiser()
                    .deserialise(replayEvent.getEvent());
        }
        catch (Exception e)
        {
            // ignore attempt to deserialise.
        }

        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "" + replayEvent.getId());
        document.addField(TYPE, REPLAY);
        document.addField(MODULE_NAME, replayEvent.getModuleName());
        document.addField(FLOW_NAME, replayEvent.getFlowName());
        document.addField(EVENT, replayEvent.getEventId());
        if(event != null)
        {
            document.addField(PAYLOAD_CONTENT, event);
        }
        else
        {
            document.addField(PAYLOAD_CONTENT, new String(replayEvent.getEvent()));
        }
        document.addField(CREATED_DATE_TIME, replayEvent.getTimestamp());
        document.setField(EXPIRY, expiry);

        try
        {
            logger.debug("Adding document: " + document);
            solrClient.add(document);
            solrClient.commit();
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write an exclusion to Solr", e);
        }
    }

    @Override
    public void saveOrUpdate(ReplayAudit replayAudit)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveOrUpdate(ReplayAuditEvent replayAuditEvent)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ReplayEvent> getReplayEvents(String moduleName, String flowName, Date startDate, Date endDate)
    {
        return null;
    }

    @Override
    public List<ReplayEvent> getReplayEvents(List<String> moduleNames, List<String> flowNames, String eventId, Date fromDate, Date toDate)
    {
        return null;
    }

    @Override
    public List<ReplayAudit> getReplayAudits(List<String> moduleNames, List<String> flowNames, String eventId, String user, Date startDate, Date endDate)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReplayAudit getReplayAuditById(Long id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ReplayAuditEvent> getReplayAuditEventsByAuditId(Long id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getNumberReplayAuditEventsByAuditId(Long id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void housekeep(Integer numToHousekeep)
    {
        super.removeExpired(REPLAY);
    }

    @Override
    public List<ReplayEvent> getHarvestableRecords(int housekeepingBatchSize)
    {
        throw new UnsupportedOperationException();
    }
}
