package org.ikasan.wiretap.dao;

import org.apache.solr.common.SolrInputDocument;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrWiretapDao extends SolrDaoBase<WiretapEvent> //implements WiretapDao<String>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrWiretapDao.class);

    /**
     * We need to give this hibernate it's context.
     */
    public static final String WIRETAP = "wiretap";

//    private boolean isBatchDelete = false;
//    private Integer transactionBatchSize;
//    private Integer housekeepingBatchSize;


    @Override
    protected SolrInputDocument getSolrInputFields(Long expiry, WiretapEvent wiretapEvent)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, wiretapEvent.getModuleName() + "-wiretap-"
            + wiretapEvent.getIdentifier());
        document.addField(TYPE, WIRETAP);
        document.addField(MODULE_NAME, wiretapEvent.getModuleName());
        document.addField(FLOW_NAME, wiretapEvent.getFlowName());
        document.addField(COMPONENT_NAME, wiretapEvent.getComponentName());
        document.addField(EVENT, wiretapEvent.getEventId());
        document.addField(PAYLOAD_CONTENT, wiretapEvent.getEvent());
        document.addField(CREATED_DATE_TIME, wiretapEvent.getTimestamp());
        document.setField(EXPIRY, expiry);

        return document;
    }

//    @Override
//    public PagedSearchResult<WiretapEvent> findWiretapEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, Set<String> moduleNames, String moduleFlow
//            , String componentName, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent)
//    {
//        HashSet<String> flowNames = new HashSet<>();
//        if(moduleFlow != null)
//        {
//            flowNames.add(moduleFlow);
//        }
//
//        HashSet<String> componentNames = new HashSet<>();
//        if(componentName != null)
//        {
//            componentNames.add(componentName);
//        }
//
//        return this.findWiretapEvents(pageNo, pageSize, orderBy, orderAscending, moduleNames, flowNames, componentNames, eventId, payloadId, fromDate, untilDate, payloadContent);
//    }
//
//    @Override
//    public PagedSearchResult<WiretapEvent> findWiretapEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, Set<String> moduleNames
//            , Set<String> flowNames, Set<String> componentNames, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent)
//    {
//        PagedSearchResult results;
//        String queryString;
//
//        try {
//            queryString = this.buildQuery(moduleNames, flowNames, componentNames, fromDate, untilDate, payloadContent, eventId, WIRETAP, false);
//        }
//        catch (IOException e) {
//           return new ArrayListPagedSearchResult<WiretapEvent>(new ArrayList<>(), 0, 0L);
//        }
//
//        logger.debug("queryString: " + queryString);
//
//        SolrQuery query = new SolrQuery();
//        query.setQuery(queryString);
//        query.setStart(pageNo * pageSize);
//        query.setRows(pageSize);
//        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
//        query.setFields(ID, MODULE_NAME, FLOW_NAME, COMPONENT_NAME, CREATED_DATE_TIME, EVENT, PAYLOAD_CONTENT);
//
//        try
//        {
//            QueryRequest req = new QueryRequest(query);
//            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);
//
//            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);
//
//            List<SolrWiretapEvent> beans = rsp.getBeans(SolrWiretapEvent.class);
//
//            results = new ArrayListPagedSearchResult(beans, beans.size(), rsp.getResults().getNumFound());
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException("An error has occurred preforming a persistence search against solr: " + e.getMessage(), e);
//        }
//
//        return results;
//    }
//
//    @Override
//    public WiretapEvent findById(String id)
//    {
//        String queryString = "id:" + id;
//
//        logger.debug("queryString: " + queryString);
//
//        SolrQuery query = new SolrQuery();
//        query.setQuery(queryString);
//
//        List<SolrWiretapEvent> beans = null;
//
//        try
//        {
//            QueryRequest req = new QueryRequest(query);
//            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);
//
//            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);
//
//            beans = rsp.getBeans(SolrWiretapEvent.class);
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException("Error resolving persistence by id [" + id + "] from ikasan solr index!", e);
//        }
//
//        if(beans.size() > 0)
//        {
//            return beans.get(0);
//        }
//        else
//        {
//            return null;
//        }
//    }
//
//    @Override
//    public void deleteAllExpired()
//    {
//        super.removeExpired(WIRETAP);
//    }
//
//    @Override
//    public boolean isBatchHousekeepDelete()
//    {
//        return this.isBatchDelete;
//    }
//
//    @Override
//    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
//    {
//        this.isBatchDelete = batchHousekeepDelete;
//    }
//
//    @Override
//    public Integer getHousekeepingBatchSize()
//    {
//        return this.housekeepingBatchSize;
//    }
//
//    @Override
//    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
//    {
//        this.housekeepingBatchSize = housekeepingBatchSize;
//    }
//
//    @Override
//    public Integer getTransactionBatchSize()
//    {
//        return this.transactionBatchSize;
//    }
//
//    @Override
//    public void setTransactionBatchSize(Integer transactionBatchSize)
//    {
//        this.transactionBatchSize = transactionBatchSize;
//    }
//
//    @Override
//    public boolean housekeepablesExist()
//    {
//        return true;
//    }
//
//    @Override
//    public void setHousekeepQuery(String housekeepQuery)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public List<WiretapEvent> getHarvestableRecords(int housekeepingBatchSize)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public void updateAsHarvested(List<WiretapEvent> events)
//    {
//        throw new UnsupportedOperationException();
//    }
}
