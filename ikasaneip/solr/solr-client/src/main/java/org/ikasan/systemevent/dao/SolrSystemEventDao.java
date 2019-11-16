package org.ikasan.systemevent.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventDao;
import org.ikasan.systemevent.model.SolrSystemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SolrSystemEventDao extends SolrDaoBase<SystemEvent> implements SystemEventDao<SystemEvent>
{
    /**
     * Logger for this class
     */
    private static Logger logger = LoggerFactory.getLogger(SolrSystemEventDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String SYSTEM_EVENT = "systemEvent";

    protected SolrInputDocument getSolrInputFields(Long expiry, SystemEvent systemEvent)
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
