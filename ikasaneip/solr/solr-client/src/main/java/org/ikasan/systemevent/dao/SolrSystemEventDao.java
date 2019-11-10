package org.ikasan.systemevent.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.replay.model.SolrReplayEvent;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventDao;
import org.ikasan.systemevent.model.SolrSystemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SolrSystemEventDao extends SolrDaoBase implements SystemEventDao<SystemEvent>
{
    /**
     * Logger for this class
     */
    private static Logger logger = LoggerFactory.getLogger(SolrSystemEventDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String SYSTEM_EVENT = "systemEvent";

    @Override
    public void save(SystemEvent systemEvent)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        SolrInputDocument document = getSolrInputFields(expiry, systemEvent);

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            req.add(document);

            commitSolrRequest(req);

        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write an exclusion to Solr", e);
        }

    }

    public void save(List<SystemEvent> systemEvents)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            for (SystemEvent systemEvent : systemEvents)
            {
                SolrInputDocument document = getSolrInputFields(expiry, systemEvent);

                req.add(document);

                logger.debug("Adding document: " + document);
            }

            commitSolrRequest(req);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write an exclusion to Solr", e);
        }
    }

    private void commitSolrRequest(UpdateRequest req)
        throws org.apache.solr.client.solrj.SolrServerException, java.io.IOException
    {
        UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

        logger.debug("Solr Response: " + rsp.toString());

        rsp = req.commit(solrClient, SolrConstants.CORE);

        logger.debug("Solr Commit Response: " + rsp.toString());

    }

    private SolrInputDocument getSolrInputFields(long expiry, SystemEvent systemEvent)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, SYSTEM_EVENT + "-" + systemEvent.getId());
        document.addField(TYPE, SYSTEM_EVENT);
        document.addField(PAYLOAD_CONTENT, getSystemEventContent(systemEvent));
        document.addField(CREATED_DATE_TIME, systemEvent.getTimestamp());
        document.setField(EXPIRY, expiry);
        return document;
    }

    @Override
    public PagedSearchResult<SystemEvent> find(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                               String subject, String action, Date timestampFrom, Date timestampTo,
                                               String actor)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SystemEvent> list(List<String> subjects, String actor, Date fromDate, Date toDate)
    {
        List<SolrSystemEvent> results = null;

        Set<String> moduleNamesSet = new HashSet<String>();

        Set<String> flowNamesSet = new HashSet<String>();

        String queryString = this
            .buildQuery(moduleNamesSet, flowNamesSet, null, fromDate, toDate, null, null, SYSTEM_EVENT);

        logger.debug("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.setFields(ID, MODULE_NAME, FLOW_NAME, COMPONENT_NAME, CREATED_DATE_TIME, EVENT, PAYLOAD_CONTENT,
            PAYLOAD_CONTENT_RAW
                       );

        logger.debug("query: " + query.toString());

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            results = rsp.getBeans(SolrSystemEvent.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception performing solr query: " + query, e);
        }

        return new ArrayList<>(results);
    }

    @Override
    public void deleteExpired()
    {
        super.removeExpired(SYSTEM_EVENT);
    }

    @Override
    public boolean isBatchHousekeepDelete()
    {
        return false;
    }

    @Override
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {

    }

    @Override
    public Integer getHousekeepingBatchSize()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean housekeepablesExist()
    {
        return false;
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHousekeepQuery(String housekeepQuery)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SystemEvent> getHarvestableRecords(int housekeepingBatchSize)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsHarvested(List<SystemEvent> systemEvents)
    {
        throw new UnsupportedOperationException();
    }

    private String getSystemEventContent(SystemEvent systemEvent)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if ( systemEvent.getActor() != null )
        {

            sb.append("actor:");
            sb.append(systemEvent.getActor());
            sb.append(",");
        }
        if ( systemEvent.getSubject() != null )
        {
            sb.append("subject:");
            sb.append(systemEvent.getSubject());
            sb.append(",");
        }

        if ( systemEvent.getAction() != null )
        {
            sb.append("action:");
            sb.append(systemEvent.getSubject());
        }
        sb.append("}");
        return sb.toString();
    }
}
