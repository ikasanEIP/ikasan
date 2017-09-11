package org.ikasan.spec.solr;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public abstract class SolrDaoBase implements SolrInitialisationService
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(SolrDaoBase.class);

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

    /**
     * Method to initialise all solr cloud DAO objects.
     *
     * @param solrCloudUrls
     * @param daysToKeep
     */
    public void init(List<String> solrCloudUrls, int daysToKeep)
    {
        solrClient = new CloudSolrClient.Builder().withSolrUrl(solrCloudUrls).build();
        ((CloudSolrClient)solrClient).setDefaultCollection("ikasan");

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
     * @return
     */
    public String buildQuery(Set<String> moduleNames, Set<String> flowNames, Set<String> componentNames, Date fromDate
            , Date untilDate, String payloadContent, String eventId, String type)
    {
        StringBuffer moduleNamesBuffer = new StringBuffer();
        StringBuffer flowNamesBuffer = new StringBuffer();
        StringBuffer componentNamesBuffer = new StringBuffer();
        StringBuffer dateBuffer = new StringBuffer();
        StringBuffer payloadBuffer = new StringBuffer();
        StringBuffer eventIdBuffer = new StringBuffer();
        StringBuffer typeBuffer = new StringBuffer();

        String delim = "";

        if(moduleNames != null && moduleNames.size() > 0)
        {
            moduleNamesBuffer.append(MODULE_NAME + COLON);

            moduleNamesBuffer.append(OPEN_BRACKET);

            for (String moduleName : moduleNames)
            {
                moduleNamesBuffer.append(delim).append("\"").append(moduleName).append("\" ");
                delim = OR;
            }

            moduleNamesBuffer.append(CLOSE_BRACKET);
        }

        if(flowNames != null && flowNames.size() > 0)
        {
            delim = "";

            flowNamesBuffer.append(FLOW_NAME + COLON);

            flowNamesBuffer.append(OPEN_BRACKET);

            for (String moduleFlowName : flowNames)
            {
                flowNamesBuffer.append(delim).append("\"").append(moduleFlowName).append("\" ");
                delim = OR;
            }

            flowNamesBuffer.append(CLOSE_BRACKET);
        }

        if(componentNames != null)
        {
            delim = "";

            componentNamesBuffer.append(COMPONENT_NAME + COLON);

            componentNamesBuffer.append(OPEN_BRACKET);

            for (String componentName : componentNames)
            {
                componentNamesBuffer.append(delim).append("\"").append(componentName).append("\" ");
                delim = OR;
            }

            componentNamesBuffer.append(CLOSE_BRACKET);
        }

        if(eventId != null && !eventId.trim().isEmpty())
        {
            delim = "";

            eventIdBuffer.append(EVENT + COLON);

            eventIdBuffer.append(delim).append("\"").append(eventId).append("\" ");
        }

        if(type != null && !type.trim().isEmpty())
        {
            delim = "";

            typeBuffer.append(TYPE + COLON);

            typeBuffer.append(delim).append("\"").append(type).append("\" ");
        }

        if(fromDate != null && untilDate != null)
        {
            dateBuffer.append(CREATED_DATE_TIME + COLON).append("[").append(fromDate.getTime()).append(TO).append(untilDate.getTime()).append("]");
        }

        if(payloadContent != null && !payloadContent.trim().isEmpty())
        {
            payloadBuffer.append(PAYLOAD_CONTENT + COLON).append("").append(payloadContent).append("");

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

        try
        {
            UpdateResponse response = this.solrClient.deleteByQuery(query.toString());
            this.solrClient.commit();

            logger.info("Deleted " + type + " solr records. Response [" + response + "]." );
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error has occurred ");
        }
    }

}
