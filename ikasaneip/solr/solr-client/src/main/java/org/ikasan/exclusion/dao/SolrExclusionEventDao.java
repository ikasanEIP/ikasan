package org.ikasan.exclusion.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.exclusion.model.SolrExclusionEventImpl;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.wiretap.model.SolrWiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 05/08/2017.
 */
public class SolrExclusionEventDao extends SolrDaoBase implements ExclusionEventDao<String, ExclusionEvent>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrExclusionEventDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String EXCLUSION = "exclusion";

    @Override
    public void save(ExclusionEvent exclusionEvent)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "" + exclusionEvent.getErrorUri());
        document.addField(TYPE, EXCLUSION);
        document.addField(MODULE_NAME, exclusionEvent.getModuleName());
        document.addField(FLOW_NAME, exclusionEvent.getFlowName());
        document.addField(EVENT, exclusionEvent.getIdentifier());
        document.addField(PAYLOAD_CONTENT, new String(exclusionEvent.getEvent()));
        document.addField(CREATED_DATE_TIME, exclusionEvent.getTimestamp());
        document.setField(EXPIRY, expiry);

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            req.add(document);

            UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            logger.debug("Adding document: " + document + ". Response: " + rsp.toString());
            req.commit(solrClient, "ikasan");
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write an exclusion to Solr", e);
        }
    }


    public void save(List<ExclusionEvent> exclusionEvents)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            for(ExclusionEvent exclusionEvent: exclusionEvents)
            {
                SolrInputDocument document = new SolrInputDocument();
                document.addField(ID, "" + exclusionEvent.getErrorUri());
                document.addField(TYPE, EXCLUSION);
                document.addField(MODULE_NAME, exclusionEvent.getModuleName());
                document.addField(FLOW_NAME, exclusionEvent.getFlowName());
                document.addField(EVENT, exclusionEvent.getIdentifier());
                document.addField(PAYLOAD_CONTENT, new String(exclusionEvent.getEvent()));
                document.addField(CREATED_DATE_TIME, exclusionEvent.getTimestamp());
                document.setField(EXPIRY, expiry);

                req.add(document);

                logger.debug("Adding document: " + document);
            }

            UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);
            logger.debug("Response: " + rsp.toString());

            req.commit(solrClient, "ikasan");
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write an exclusion to Solr", e);
        }
    }

    @Override
    public void delete(String moduleName, String flowName, String s)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String errorUri)
    {
        StringBuffer query = new StringBuffer();
        query.append(ID).append(COLON).append(errorUri);

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            req.deleteByQuery(query.toString());

            UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            req.commit(solrClient, SolrConstants.CORE);

            logger.info("Deleted " + errorUri + " exclusion solr records. Response [" + rsp + "]." );
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error has occurred ");
        }
    }

    @Override
    public ExclusionEvent find(String moduleName, String flowName, String s)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long rowCount(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String identifier)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ExclusionEvent> findAll()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ExclusionEvent> find(List<String> moduleName, List<String> flowName, Date starteDate, Date endDate, String s, int size)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExclusionEvent find(String errorUri)
    {
        String queryString = "id:\"" + errorUri + "\" AND type:" + EXCLUSION;


        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<SolrExclusionEventImpl> beans = null;

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            beans = rsp.getBeans(SolrExclusionEventImpl.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving exclusion by id [" + errorUri + "] from ikasan solr index!", e);
        }

        if(beans.size() > 0)
        {
            return beans.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public List<ExclusionEvent> getHarvestableRecords(int housekeepingBatchSize)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllExpired()
    {
        super.removeExpired(EXCLUSION);
    }

    @Override
    public void updateAsHarvested(List<ExclusionEvent> exclusionEvents)
    {
        throw new UnsupportedOperationException();
    }
}
