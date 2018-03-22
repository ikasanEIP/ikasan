package org.ikasan.replay.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.replay.model.SolrReplayEvent;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.replay.*;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 25/08/2017.
 */
public class SolrReplayDao extends SolrDaoBase implements ReplayDao, ReplayAuditDao<ReplayAudit, ReplayAuditEvent>
{
    private static Logger logger = LoggerFactory.getLogger(SolrReplayDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String REPLAY = "replay";

    public SolrReplayDao()
    {

    }

    @Override
    public void saveOrUpdate(ReplayEvent replayEvent)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "" + replayEvent.getId());
        document.addField(TYPE, REPLAY);
        document.addField(MODULE_NAME, replayEvent.getModuleName());
        document.addField(FLOW_NAME, replayEvent.getFlowName());
        document.addField(EVENT, replayEvent.getEventId());
        if(replayEvent.getEventAsString() != null && !replayEvent.getEventAsString().isEmpty())
        {
            document.addField(PAYLOAD_CONTENT, replayEvent.getEventAsString());
        }
        else
        {
            document.addField(PAYLOAD_CONTENT, new String(replayEvent.getEvent()));
        }
        document.addField(PAYLOAD_CONTENT_RAW, replayEvent.getEvent());
        document.addField(CREATED_DATE_TIME, replayEvent.getTimestamp());
        document.setField(EXPIRY, expiry);

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            req.add(document);

            UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            logger.debug("Adding document: " + document + ". Response: " + rsp.toString());

            req.commit(solrClient, SolrConstants.CORE);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write an exclusion to Solr", e);
        }
    }

    public void saveOrUpdate(List<ReplayEvent> replayEvents)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            for(ReplayEvent replayEvent: replayEvents)
            {
                SolrInputDocument document = new SolrInputDocument();
                document.addField(ID, "" + replayEvent.getId());
                document.addField(TYPE, REPLAY);
                document.addField(MODULE_NAME, replayEvent.getModuleName());
                document.addField(FLOW_NAME, replayEvent.getFlowName());
                document.addField(EVENT, replayEvent.getEventId());
                if(replayEvent.getEventAsString() != null && !replayEvent.getEventAsString().isEmpty())
                {
                    document.addField(PAYLOAD_CONTENT, replayEvent.getEventAsString());
                }
                else
                {
                    document.addField(PAYLOAD_CONTENT, new String(replayEvent.getEvent()));
                }
                document.addField(PAYLOAD_CONTENT_RAW, replayEvent.getEvent());
                document.addField(CREATED_DATE_TIME, replayEvent.getTimestamp());
                document.setField(EXPIRY, expiry);

                req.add(document);

                logger.debug("Adding document: " + document);
            }

            UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            logger.debug("Solr Response: " + rsp.toString());

            req.commit(solrClient, SolrConstants.CORE);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write an exclusion to Solr", e);
        }
    }

    @Override
    public List<ReplayEvent> getReplayEvents(String moduleName, String flowName, Date startDate, Date endDate)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ReplayEvent> getReplayEvents(List<String> moduleNames, List<String> flowNames, String eventId, String payloadContent, Date fromDate, Date toDate)
    {
        List<SolrReplayEvent> results = null;

        Set<String> moduleNamesSet = null;

        if(moduleNames == null)
        {
            moduleNamesSet = new HashSet<String>();
        }
        else
        {
            moduleNamesSet = new HashSet<String>(moduleNames);
        }

        Set<String> flowNamesSet = null;

        if(flowNames == null)
        {
            flowNamesSet = new HashSet<String>();
        }
        else
        {
            flowNamesSet = new HashSet<String>(flowNames);
        }

        String queryString = this.buildQuery(moduleNamesSet, flowNamesSet, null, fromDate, toDate, payloadContent, eventId, REPLAY);

        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setRows(100);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.setFields(ID, MODULE_NAME, FLOW_NAME, COMPONENT_NAME, CREATED_DATE_TIME, EVENT, PAYLOAD_CONTENT, PAYLOAD_CONTENT_RAW);

        logger.info("query: " + query.toString());

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            results = rsp.getBeans(SolrReplayEvent.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception performing solr query: " + query, e);
        }

        return new ArrayList<ReplayEvent>(results);
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

    @Override
    public ReplayEvent getReplayEventById(Long id)
    {
        String queryString = super.buildIdQuery(id, REPLAY);

        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setRows(100);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.setFields(ID, MODULE_NAME, FLOW_NAME, COMPONENT_NAME, CREATED_DATE_TIME, EVENT, PAYLOAD_CONTENT, PAYLOAD_CONTENT_RAW);

        logger.info("query: " + query.toString());

        List<SolrReplayEvent> results = null;

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            results = rsp.getBeans(SolrReplayEvent.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception performing solr query: " + query, e);
        }

        if(results.size() > 0)
        {
            return results.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void saveOrUpdateAudit(ReplayAudit replayAudit)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveOrUpdate(ReplayAuditEvent replayAuditEvent)
    {
        throw new UnsupportedOperationException();
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
    public void updateAsHarvested(List<ReplayEvent> events)
    {
        throw new UnsupportedOperationException();
    }
}
