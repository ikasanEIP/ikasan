package org.ikasan.spec.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.solr.util.SolrTokenizerQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public abstract class SolrDaoBase<T> implements SolrInitialisationService
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrDaoBase.class);

    private static final List<String> ALL = Arrays.asList("*");

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
            , Date untilDate, String payloadContent, String eventId, String type) throws IOException {
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
     * @param searchTerm
     * @param eventId
     * @param types
     * @return String
     */
    protected String buildQuery(Collection<String> moduleNames, Collection<String> flowNames, Collection<String> componentNames, Date fromDate
            , Date untilDate, String searchTerm, String eventId, List<String> types) throws IOException
    {
        // Setup the predicates
        StringBuffer moduleNamesBuffer =  this.buildStringListQueryPart(moduleNames, MODULE_NAME);
        StringBuffer flowNamesBuffer = this.buildStringListQueryPart(flowNames, FLOW_NAME);
        StringBuffer componentNamesBuffer = this.buildStringListQueryPart(componentNames, COMPONENT_NAME);
        StringBuffer dateBuffer = this.buildDatePredicate(CREATED_DATE_TIME, fromDate, untilDate);
        StringBuffer payloadBuffer = this.buildSearchStringPredicate(searchTerm, PAYLOAD_CONTENT);
        StringBuffer eventIdBuffer = this.buildFieldPredicate(eventId, EVENT);
        StringBuffer typeBuffer =  this.buildStringListQueryPart(types, TYPE);

        // Construct the query
        StringBuffer bufferFinalQuery = new StringBuffer();
        Boolean hasPrevious = this.addQueryPart(bufferFinalQuery, moduleNamesBuffer, false);
        hasPrevious = this.addQueryPart(bufferFinalQuery, flowNamesBuffer, hasPrevious);
        hasPrevious = this.addQueryPart(bufferFinalQuery, componentNamesBuffer, hasPrevious);
        hasPrevious = this.addQueryPart(bufferFinalQuery, payloadBuffer, hasPrevious);
        hasPrevious = this.addQueryPart(bufferFinalQuery, eventIdBuffer, hasPrevious);
        hasPrevious = this.addQueryPart(bufferFinalQuery, typeBuffer, hasPrevious);
        this.addQueryPart(bufferFinalQuery, dateBuffer, hasPrevious);

        return bufferFinalQuery.toString();
    }

    /**
     * Helper method to build query parts.
     *
     * @param values
     * @param field
     * @return
     */
    protected StringBuffer buildStringListQueryPart(Collection<String> values, String field)
    {
        StringBuffer queryPart = new StringBuffer();
        if(values != null && values.size() > 0)
        {
            queryPart.append(this.buildPredicate(field,  values));
        }
        else if(values != null && values.size() == 0)
        {
            queryPart.append(this.buildPredicate(field, ALL));
        }

        return queryPart;
    }

    /**
     * Helper method to add query part to the solr query.
     *
     * @param bufferFinalQuery
     * @param queryPart
     * @param hasPrevious
     * @return
     */
    protected boolean addQueryPart(StringBuffer bufferFinalQuery, StringBuffer queryPart, Boolean hasPrevious)
    {
        if(queryPart != null && queryPart.length() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(AND);
            }

            bufferFinalQuery.append(queryPart);
            hasPrevious = true;
        }

        return hasPrevious;
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
            if(predicateValue.contains("*"))
            {
                predicate.append(delim).append(predicateValue).append(" ");
            }
            else
            {
                predicate.append(delim).append("\"").append(predicateValue).append("\"").append(" ");
            }
            delim = OR;
        }

        predicate.append(CLOSE_BRACKET);

        return predicate;
    }

    /**
     * Helper method to build date predicate.
     *
     * @param field
     * @param fromDate
     * @param untilDate
     * @return
     */
    protected StringBuffer buildDatePredicate(String field, Date fromDate, Date untilDate)
    {
        StringBuffer dateBuffer = new StringBuffer();

        if(fromDate != null && untilDate != null)
        {
            dateBuffer.append(field + COLON).append("[").append(fromDate.getTime())
                .append(TO).append(untilDate.getTime()).append("]");
        }

        return dateBuffer;
    }

    /**
     * Helper method to build the search string predicate.
     *
     * @param searchTerm
     * @param field
     * @return
     * @throws IOException
     */
    protected StringBuffer buildSearchStringPredicate(String searchTerm, String field) throws IOException
    {
        StringBuffer searchTermBuffer = new StringBuffer();
        if(searchTerm != null && !searchTerm.trim().isEmpty())
        {
            searchTermBuffer.append("(").append(SolrTokenizerQueryBuilder.buildQuery(searchTerm, field)).append(")");
        }

        return searchTermBuffer;
    }

    /**
     * Helper method to build general field predicates.
     *
     * @param value
     * @param field
     * @return
     */
    protected StringBuffer buildFieldPredicate(String value, String field)
    {
        StringBuffer predicateBuffer = new StringBuffer();

        if(value != null && !value.trim().isEmpty())
        {
            predicateBuffer.append(field + COLON);

            predicateBuffer.append(value).append(" ");
        }

        return predicateBuffer;
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

    /**
     * Set the entity days to keep.
     * @param daysToKeep
     */
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
