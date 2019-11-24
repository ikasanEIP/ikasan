package org.ikasan.spec.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.solr.util.SolrSpecialCharacterEscapeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public abstract class SolrDaoBase<T> implements SolrInitialisationService
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrDaoBase.class);

    public static final String ID = "id";
    public static final String ERROR_URI = "errorUri";
    public static final String TYPE = "type";
    public static final String MODULE_NAME = "moduleName";
    public static final String FLOW_NAME = "flowName";
    public static final String COMPONENT_NAME = "componentName";
    public static final String CREATED_DATE_TIME = "timestamp";
    public static final String PAYLOAD_CONTENT = "payload";
    public static final String PAYLOAD_CONTENT_RAW = "payloadRaw";
    public static final String EVENT = "event";
    public static final String RELATED_EVENT = "relatedEventId";
    public static final String EXPIRY = "expiry";
    public static final String ERROR_DETAIL = "errorDetail";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String EXCEPTION_CLASS = "exceptionClass";


    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String TO = " TO ";

    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";

    public static final String COLON = ":";

    protected SolrClient solrClient = null;
    protected int daysToKeep = 7;

    protected String solrUsername;
    protected String solrPassword;

    /**
     * Method to initialise all solr cloud DAO objects.
     *
     * @param solrCloudUrls
     * @param daysToKeep
     */
    public void initCloud(List<String> solrCloudUrls, int daysToKeep)
    {
        solrClient = new CloudSolrClient.Builder().withSolrUrl(solrCloudUrls).build();
        ((CloudSolrClient)solrClient).setDefaultCollection("ikasan");

        this.daysToKeep = daysToKeep;
    }

    @Override
    public void initStandalone(String solrCloudUrl, int daysToKeep)
    {
        solrClient = new HttpSolrClient.Builder().withBaseSolrUrl(solrCloudUrl).build();

        this.daysToKeep = daysToKeep;
    }

    /**
     * Set the solr client
     * 
     * @param solrClient
     */
    public void setSolrClient(SolrClient solrClient)
    {
        this.solrClient = solrClient;
    }

    /**
     * Helper method to build the query that is issued to Solr.
     *
     * @param moduleNames
     * @param flowNames
     * @param componentNames
     * @param fromDate
     * @param untilDate
     * @param payloadContent
     * @param eventId
     * @param type
     * @return String
     */
    protected String buildQuery(Collection<String> moduleNames, Collection<String> flowNames, Collection<String> componentNames, Date fromDate
            , Date untilDate, String payloadContent, String eventId, String type)
    {
        ArrayList<String> types = new ArrayList<String>();
        types.add(type);

        return this.buildQuery(moduleNames, flowNames, componentNames, fromDate, untilDate, payloadContent, eventId, types);
    }

    /**
     * Helper method to build the query that is issued to Solr.
     *
     * @param moduleNames
     * @param flowNames
     * @param componentNames
     * @param fromDate
     * @param untilDate
     * @param payloadContent
     * @param eventId
     * @param types
     * @return String
     */
    protected String buildQuery(Collection<String> moduleNames, Collection<String> flowNames, Collection<String> componentNames, Date fromDate
            , Date untilDate, String payloadContent, String eventId, List<String> types)
    {
        StringBuffer moduleNamesBuffer = new StringBuffer();
        StringBuffer flowNamesBuffer = new StringBuffer();
        StringBuffer componentNamesBuffer = new StringBuffer();
        StringBuffer dateBuffer = new StringBuffer();
        StringBuffer payloadBuffer = new StringBuffer();
        StringBuffer eventIdBuffer = new StringBuffer();
        StringBuffer typeBuffer = new StringBuffer();

        if(moduleNames != null && moduleNames.size() > 0)
        {
            moduleNamesBuffer.append(this.buildPredicate(MODULE_NAME, moduleNames));
        }

        if(flowNames != null && flowNames.size() > 0)
        {
            flowNamesBuffer.append(this.buildPredicate(FLOW_NAME, flowNames));
        }

        if(componentNames != null)
        {
            componentNamesBuffer.append(this.buildPredicate(COMPONENT_NAME, componentNames));
        }

        if(eventId != null && !eventId.trim().isEmpty())
        {
            eventIdBuffer.append(EVENT + COLON);

            eventIdBuffer.append(eventId).append(" ");
        }

        if(types != null && !types.isEmpty())
        {
            typeBuffer.append(this.buildPredicate(TYPE, types));
        }

        if(fromDate != null && untilDate != null)
        {
            dateBuffer.append(CREATED_DATE_TIME + COLON).append("[").append(fromDate.getTime()).append(TO).append(untilDate.getTime()).append("]");
        }

        if(payloadContent != null && !payloadContent.trim().isEmpty())
        {
            payloadBuffer.append(PAYLOAD_CONTENT + COLON).append("\"").append(SolrSpecialCharacterEscapeUtil.escape(payloadContent)).append("\"");

        }

        StringBuffer bufferFinalQuery = new StringBuffer();

        boolean hasPrevious = false;

        if(moduleNames != null && moduleNames.size() > 0)
        {
            bufferFinalQuery.append(moduleNamesBuffer);
            hasPrevious = true;
        }

        if(flowNames != null && flowNames.size() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(flowNamesBuffer);
            hasPrevious = true;
        }

        if(componentNames != null && componentNames.size() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(componentNamesBuffer);
            hasPrevious = true;
        }

        if(payloadContent != null && payloadContent.length() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(payloadBuffer);
            hasPrevious = true;
        }

        if(eventIdBuffer.length() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(eventIdBuffer);
            hasPrevious = true;
        }

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

    /**
     * Helper method to build a field predicate.
     *
     * @param field
     * @param predicateValues
     * @return
     */
    protected StringBuffer buildPredicate(String field, Collection<String> predicateValues)
    {
        String delim = "";

        StringBuffer predicate = new StringBuffer();
        predicate.append(field + COLON);

        predicate.append(OPEN_BRACKET);

        for (String predicateValue : predicateValues)
        {
            predicate.append(delim).append(predicateValue).append(" ");
            delim = OR;
        }

        predicate.append(CLOSE_BRACKET);

        return predicate;
    }

    /**
     * Query solr index by id for a given type
     *
     * @param id
     * @param type
     * @return String
     */
    protected String buildIdQuery(Long id, String type)
    {
        StringBuffer idBuffer = new StringBuffer();
        StringBuffer typeBuffer = new StringBuffer();


        idBuffer.append(ID + COLON + id);

        if(type != null && !type.trim().isEmpty())
        {
            typeBuffer.append(TYPE + COLON);

            typeBuffer.append("\"").append(type).append("\" ");
        }


        StringBuffer bufferFinalQuery = new StringBuffer(idBuffer);

        boolean hasPrevious = true;

        if(typeBuffer.length() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(typeBuffer);
            hasPrevious = true;
        }


        return bufferFinalQuery.toString();
    }

    /**
     * Method to remove expired records from the solr index by type.
     *
     * @param type
     */
    public void removeExpired(String type)
    {
        long currentTime = System.currentTimeMillis();

        StringBuffer query = new StringBuffer();
        query.append(TYPE).append(COLON).append(type);
        query.append(AND);
        query.append(EXPIRY).append(COLON).append("{").append("*").append(TO).append(currentTime).append("}");

        this.deleteByQuery(query.toString());
    }

    /**
     * Method to remove records from the solr index by type and id.
     *
     * @param type
     */
    public void removeById(String type, String id)
    {
        StringBuffer query = new StringBuffer();
        query.append(TYPE).append(COLON).append("\"").append(type).append("\"");
        query.append(AND);
        query.append(ID).append(COLON).append("\"").append(id).append("\"");

        this.deleteByQuery(query.toString());
    }

    /**
     * Method to remove expired records from the solr index.
     */
    public void removeExpired()
    {
        long currentTime = System.currentTimeMillis();

        StringBuffer query = new StringBuffer();
        query.append(EXPIRY).append(COLON).append("{").append("*").append(TO).append(currentTime).append("}");

        this.deleteByQuery(query.toString());
    }

    public void setDaysToKeep(int daysToKeep)
    {
        this.daysToKeep = daysToKeep;
    }

    /**
     * Set the solr username
     *
     * @param solrUsername
     */
    public void setSolrUsername(String solrUsername)
    {
        this.solrUsername = solrUsername;
    }


    /**
     * Set the solr password
     *
     * @param solrPassword
     */
    public void setSolrPassword(String solrPassword)
    {
        this.solrPassword = solrPassword;
    }

    /**
     * Helper method to delete records based on query.
     *
     * @param query
     */
    protected void deleteByQuery(String query)
    {
        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            req.deleteByQuery(query.toString());

            if(this.solrClient == null)
            {
                logger.warn("Solr client has not been initialised. This indicates that the platform has not been configured for solr.");
                return;
            }

            commitSolrRequest(req);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error has occurred deleting using query [" + query + "].: " + e.getMessage(), e);
        }
    }

    public void save(T event)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        SolrInputDocument document = getSolrInputFields(expiry, event);

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

    public void save(List<T> events)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            for (T event : events)
            {
                SolrInputDocument document = getSolrInputFields(expiry, event);

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

    protected void commitSolrRequest(UpdateRequest req)
        throws org.apache.solr.client.solrj.SolrServerException, java.io.IOException
    {
        UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

        logger.debug("Solr Response: " + rsp.toString());

        rsp = req.commit(solrClient, SolrConstants.CORE);

        logger.debug("Solr Commit Response: " + rsp.toString());

    }


    protected abstract SolrInputDocument getSolrInputFields(Long expiry, T event);


}
