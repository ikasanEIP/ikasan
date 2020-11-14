package org.ikasan.error.reporting.dao;

import org.apache.solr.common.SolrInputDocument;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrErrorReportingServiceDao extends SolrDaoBase<ErrorOccurrence>
{
    private static Logger logger = LoggerFactory.getLogger(SolrErrorReportingServiceDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String ERROR = "error";


    @Override
    protected SolrInputDocument convertEntityToSolrInputDocument(Long expiry, ErrorOccurrence errorOccurrence)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, errorOccurrence.getModuleName() + "-error-" + errorOccurrence.getUri());
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

}
