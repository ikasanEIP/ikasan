package org.ikasan.error.reporting.dao;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.error.reporting.model.ErrorCategorisation;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.solr.dao.SolrDaoBase;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrErrorReportingServiceDao extends SolrDaoBase implements ErrorReportingServiceDao<ErrorOccurrence<byte[]>, String>
{
    private static Logger logger = Logger.getLogger(SolrErrorReportingServiceDao.class);

    @Override
    public ErrorOccurrence<byte[]> find(String uri)
    {
        String queryString = ERROR_URI + COLON + "\"" + uri + "\"";

        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<ErrorOccurrence> beans = null;

        try
        {
            QueryResponse rsp = this.solrClient.query( query );

            beans = rsp.getBeans(ErrorOccurrence.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving error occurrence by id [" + uri + "] from ikasan solr index!", e);
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
    public Map<String, ErrorOccurrence<byte[]>> find(List<String> uris)
    {
        return null;
    }

    @Override
    public List<ErrorOccurrence<byte[]>> find(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate, int size)
    {
        return null;
    }

    @Override
    public List<ErrorOccurrence<byte[]>> find(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate, String action, String exceptionClass, int size)
    {
        return null;
    }

    @Override
    public Long rowCount(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate)
    {
        return null;
    }

    @Override
    public void save(ErrorOccurrence<byte[]> errorOccurrence)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, errorOccurrence.getUri());
        document.addField(ERROR_URI, errorOccurrence.getUri());
        document.addField(TYPE, "error");
        document.addField(MODULE_NAME, errorOccurrence.getModuleName());
        document.addField(FLOW_NAME, errorOccurrence.getFlowName());
        document.addField(COMPONENT_NAME, errorOccurrence.getFlowElementName());
        document.addField(EVENT, errorOccurrence.getEventLifeIdentifier());
        document.addField(RELATED_EVENT, errorOccurrence.getEventRelatedIdentifier());
        document.addField(PAYLOAD_CONTENT, errorOccurrence.getEventAsString());
        document.addField(CREATED_DATE_TIME, errorOccurrence.getTimestamp());
        document.addField(ERROR_DETAIL, errorOccurrence.getErrorDetail());
        document.addField(ERROR_MESSAGE, errorOccurrence.getErrorMessage());
        document.addField(EXCEPTION_CLASS, errorOccurrence.getExceptionClass());
        document.setField(EXPIRY, expiry);

        try
        {
            logger.info("Adding document: " + document);
            solrClient.add(document);
            solrClient.commit();
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to wrie a wiretap to Solr", e);
        }
    }

    @Override
    public void deleteExpired()
    {

    }
}
