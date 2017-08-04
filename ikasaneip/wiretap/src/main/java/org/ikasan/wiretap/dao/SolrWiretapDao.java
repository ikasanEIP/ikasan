package org.ikasan.wiretap.dao;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.solr.dao.SolrDaoBase;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.ArrayListPagedSearchResult;
import org.ikasan.wiretap.model.SolrWiretapEvent;
import org.ikasan.wiretap.model.WiretapFlowEvent;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrWiretapDao extends SolrDaoBase implements WiretapDao
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(SolrWiretapDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String WIRETAP = "wiretap";

    @Override
    public void save(WiretapEvent wiretapEvent)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "" + wiretapEvent.getIdentifier());
        document.addField(TYPE, "wiretap");
        document.addField(MODULE_NAME, wiretapEvent.getModuleName());
        document.addField(FLOW_NAME, wiretapEvent.getFlowName());
        document.addField(COMPONENT_NAME, wiretapEvent.getComponentName());
        document.addField(EVENT, ((WiretapFlowEvent)wiretapEvent).getEventId());
        document.addField(PAYLOAD_CONTENT, wiretapEvent.getEvent());
        document.addField(CREATED_DATE_TIME, wiretapEvent.getTimestamp());
        document.setField(EXPIRY, expiry);

        logger.info("this.daysToKeep = " + this.daysToKeep + " Setting expiry to: " + expiry);

        try
        {
            logger.debug("Adding document: " + document);
            solrClient.add(document);
            solrClient.commit();
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to wrie a wiretap to Solr", e);
        }
    }

    @Override
    public PagedSearchResult<WiretapEvent> findWiretapEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, Set<String> moduleNames, String moduleFlow, String componentName, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent)
    {
        PagedSearchResult results = null;

        HashSet<String> flowNames = new HashSet<String>();
        if(moduleFlow != null && moduleFlow.length() > 0)
        {
            flowNames.add(moduleFlow);
        }

        HashSet<String> componentNames = new HashSet<String>();
        if(componentName != null && componentName.length() > 0)
        {
            componentNames.add(moduleFlow);
        }

        String queryString = this.buildQuery(moduleNames, flowNames, componentNames, fromDate, untilDate, payloadContent, eventId, WIRETAP);

        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setStart(pageNo * pageSize);
        query.setRows(pageSize);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.setFields(ID, MODULE_NAME, FLOW_NAME, COMPONENT_NAME, CREATED_DATE_TIME, EVENT, PAYLOAD_CONTENT);

        try
        {
            QueryResponse rsp = this.solrClient.query(query);

            List<SolrWiretapEvent> beans = rsp.getBeans(SolrWiretapEvent.class);

            results = new ArrayListPagedSearchResult(beans, beans.size(), rsp.getResults().getNumFound());
        }
        catch (SolrServerException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public PagedSearchResult<WiretapEvent> findWiretapEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, Set<String> moduleNames, Set<String> flowNames, Set<String> componentNames, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent)
    {
        PagedSearchResult results = null;

        String queryString = this.buildQuery(moduleNames, flowNames, componentNames, fromDate, untilDate, payloadContent, eventId, WIRETAP);

        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setStart(pageNo * pageSize);
        query.setRows(pageSize);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.setFields(ID, MODULE_NAME, FLOW_NAME, COMPONENT_NAME, CREATED_DATE_TIME, EVENT, PAYLOAD_CONTENT);

        try
        {
            QueryResponse rsp = this.solrClient.query( query );

            List<SolrWiretapEvent> beans = rsp.getBeans(SolrWiretapEvent.class);

            results = new ArrayListPagedSearchResult(beans, beans.size(), rsp.getResults().getNumFound());
        }
        catch (SolrServerException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public WiretapEvent findById(Long id)
    {
        return null;
    }

    @Override
    public void deleteAllExpired()
    {
        super.removeExpired(WIRETAP);
    }

    @Override
    public boolean isBatchHousekeepDelete()
    {
        return false;
    }

    @Override
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {
        // Not implemented.
    }

    @Override
    public Integer getHousekeepingBatchSize()
    {
        // Not implemented.
        return null;
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {
        // Not implemented.
    }

    @Override
    public Integer getTransactionBatchSize()
    {
        // Not implemented.
        return null;
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize)
    {
        // Not implemented.
    }

    @Override
    public boolean housekeepablesExist()
    {
        return true;
    }

    @Override
    public void setHousekeepQuery(String housekeepQuery)
    {
        // Not implemented.
    }

    @Override
    public List<WiretapEvent> getHarvestableRecords(int housekeepingBatchSize)
    {
        // Not implemented.
        return null;
    }
}
