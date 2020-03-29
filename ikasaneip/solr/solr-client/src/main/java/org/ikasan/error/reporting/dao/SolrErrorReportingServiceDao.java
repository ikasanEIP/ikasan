package org.ikasan.error.reporting.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrErrorReportingServiceDao extends SolrDaoBase<ErrorOccurrence> implements ErrorReportingServiceDao<ErrorOccurrence, String>
{
    private static Logger logger = LoggerFactory.getLogger(SolrErrorReportingServiceDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String ERROR = "error";

    @Override
    public ErrorOccurrence find(String uri)
    {
        String queryString = ERROR_URI + COLON + "\"" + uri + "\"";

        logger.debug("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<SolrErrorOccurrence> beans = null;

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            beans = rsp.getBeans(SolrErrorOccurrence.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving error occurrence by id [" + uri + "] from ikasan solr index!", e);
        }

        if(beans.size() > 0)
        {
            return (ErrorOccurrence)beans.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public Map<String, ErrorOccurrence> find(List<String> uris)
    {
        throw new UnsupportedOperationException();
    }


    public List<ErrorOccurrence> find(List<String> moduleNames, List<String> flowNames, List<String> componentNames, Date startDate, Date endDate, int size)
    {
        String queryString;
        try {
            queryString = this.buildQuery(moduleNames, flowNames, componentNames, startDate, endDate, null, null, ERROR);
        }
        catch (IOException e) {
            return new ArrayList<>();
        }

        logger.debug("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setRows(size);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.setFields(ID, MODULE_NAME, FLOW_NAME, COMPONENT_NAME, CREATED_DATE_TIME, TYPE);

        List<SolrErrorOccurrence> results;

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            results = rsp.getBeans(SolrErrorOccurrence.class);
        }
        catch (Exception e)
        {
           throw new RuntimeException("An error has occurred preforming error search against solr: " + e.getMessage(), e);
        }

        return new ArrayList<>(results);
    }

    @Override
    public List<ErrorOccurrence> find(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate, String action, String exceptionClass, int size)
    {
        throw new UnsupportedOperationException();
    }

    @Override public PagedSearchResult<ErrorOccurrence> find(int pageNo, int pageSize, String orderBy,
        boolean orderAscending, String moduleName, String flowName, String componentName, Date fromDate, Date untilDate)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long rowCount(List<String> moduleName, List<String> flowName, List<String> flowElementName, Date startDate, Date endDate)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected SolrInputDocument getSolrInputFields(Long expiry, ErrorOccurrence errorOccurrence)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "error-" + errorOccurrence.getUri());
        document.addField(ERROR_URI, errorOccurrence.getUri());
        document.addField(TYPE, ERROR);
        document.addField(MODULE_NAME, errorOccurrence.getModuleName());
        document.addField(FLOW_NAME, errorOccurrence.getFlowName());
        document.addField(COMPONENT_NAME, errorOccurrence.getFlowElementName());
        document.addField(EVENT, errorOccurrence.getEventLifeIdentifier());
        document.addField(RELATED_EVENT, errorOccurrence.getEventRelatedIdentifier());
        document.addField(PAYLOAD_CONTENT, errorOccurrence.getEventAsString());
        document.addField(CREATED_DATE_TIME, errorOccurrence.getTimestamp());
        document.addField(ERROR_DETAIL, errorOccurrence.getErrorDetail());
        document.addField(ERROR_ACTION, errorOccurrence.getAction());
        document.addField(ERROR_MESSAGE, errorOccurrence.getErrorMessage());
        document.addField(EXCEPTION_CLASS, errorOccurrence.getExceptionClass());
        document.setField(EXPIRY, expiry);

        return document;
    }

    @Override
    public void deleteExpired()
    {
        super.removeExpired(ERROR);
    }


}
