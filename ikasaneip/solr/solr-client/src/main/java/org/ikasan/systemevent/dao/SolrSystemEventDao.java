package org.ikasan.systemevent.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.solr.util.SolrSpecialCharacterEscapeUtil;
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
        document.addField(TYPE, SYSTEM_EVENT);
        document.addField(PAYLOAD_CONTENT, getSystemEventContent(systemEvent));
        if(systemEvent instanceof SolrSystemEvent){
            document.addField(ID, ((SolrSystemEvent) systemEvent).getModuleName() + "-" + SYSTEM_EVENT + "-" + systemEvent.getId());
            document.addField(MODULE_NAME, ((SolrSystemEvent) systemEvent).getModuleName());
        }
        else {
            document.addField(ID, SYSTEM_EVENT + "-" + systemEvent.getId());
        }
        document.addField(CREATED_DATE_TIME, systemEvent.getTimestamp().getTime());
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


        String queryString = this
            .buildQuery(moduleNamesSet, fromDate, toDate, subjects, actor, SYSTEM_EVENT);

        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.setFields(ID, MODULE_NAME, CREATED_DATE_TIME, PAYLOAD_CONTENT);

        logger.info("query: " + query.toString());

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

    protected String buildQuery(Collection<String> moduleNames, Date fromDate
        , Date untilDate, List<String> subjects, String actor, String type)
    {
        StringBuffer moduleNamesBuffer = new StringBuffer();
        StringBuffer dateBuffer = new StringBuffer();
        StringBuffer actorBuffer = new StringBuffer();
        StringBuffer typeBuffer = new StringBuffer();
        StringBuffer subjectsBuffer = new StringBuffer();

        if(moduleNames != null && moduleNames.size() > 0)
        {
            moduleNamesBuffer.append(this.buildPredicate(MODULE_NAME, moduleNames));
        }

        if(type != null && !type.isEmpty())
        {
            typeBuffer.append(this.buildPredicate(TYPE, Arrays.asList(type)));
        }

        if(fromDate != null && untilDate != null)
        {
            dateBuffer.append(CREATED_DATE_TIME + COLON).append("[").append(fromDate.getTime()).append(TO).append(untilDate.getTime()).append("]");
        }

        if(actor != null && !actor.trim().isEmpty())
        {
            actorBuffer.append(PAYLOAD_CONTENT + COLON).append("\"").append(
                SolrSpecialCharacterEscapeUtil.escape(actor)).append("\"");

        }

//        if(subjects != null && !subjects.isEmpty())
//        {
//            subjectsBuffer.append(this.buildPredicate(PAYLOAD_CONTENT, subjects));
//        }


        StringBuffer bufferFinalQuery = new StringBuffer();

        boolean hasPrevious = false;

        if(moduleNames != null && moduleNames.size() > 0)
        {
            bufferFinalQuery.append(moduleNamesBuffer);
            hasPrevious = true;
        }

        if(actor != null && actor.length() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(actorBuffer);
            hasPrevious = true;
        }

//        if(subjects != null && !subjects.isEmpty()){
//            if(hasPrevious)
//            {
//                bufferFinalQuery.append(AND);
//            }
//
//            bufferFinalQuery.append(subjectsBuffer);
//            hasPrevious = true;
//        }

        if(typeBuffer.length() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(typeBuffer);
            hasPrevious = true;
        }

        if(fromDate != null && untilDate != null)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(dateBuffer);
        }

        return bufferFinalQuery.toString();
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
            sb.append(systemEvent.getAction());
        }
        sb.append("}");
        return sb.toString();
    }
}
