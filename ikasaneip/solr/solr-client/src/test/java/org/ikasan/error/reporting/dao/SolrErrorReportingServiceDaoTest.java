package org.ikasan.error.reporting.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.spec.solr.SolrDaoBase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrErrorReportingServiceDaoTest
{

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private SolrClient server = mockery.mock(SolrClient.class);

    @Test(expected = RuntimeException.class)
    @DirtiesContext
    public void test_save_exception() throws Exception
    {
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(server).request(with(any(UpdateRequest.class)));
                will(throwException(new RuntimeException("Error")));

            }
        });

        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        SolrErrorOccurrence event = new SolrErrorOccurrence("uri", "moduleName", "flowName"
            , "componentName", "action", "detail", "message", "exceptionClass"
            , "eventId", "relatedEventId", "eventAsString", 12345L);

        dao.save(event);
    }

    @Test
    public void test_convert_entity_to_solr_input_document() {
        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();

        SolrErrorOccurrence event = new SolrErrorOccurrence("uri", "moduleName", "flowName"
            , "componentName", "action", "detail", "message", "exceptionClass"
            , "eventId", "relatedEventId", "eventAsString", 12345L);

        SolrInputDocument solrInputDocument = dao.convertEntityToSolrInputDocument(1L, event);

        Assert.assertEquals("moduleName-error-uri", solrInputDocument.getFieldValue(SolrDaoBase.ID));
        Assert.assertEquals("uri", solrInputDocument.getFieldValue(SolrDaoBase.ERROR_URI));
        Assert.assertEquals("moduleName", solrInputDocument.getFieldValue(SolrDaoBase.MODULE_NAME));
        Assert.assertEquals("flowName", solrInputDocument.getFieldValue(SolrDaoBase.FLOW_NAME));
        Assert.assertEquals("componentName", solrInputDocument.getFieldValue(SolrDaoBase.COMPONENT_NAME));
        Assert.assertEquals("action", solrInputDocument.getFieldValue(SolrDaoBase.ERROR_ACTION));
        Assert.assertEquals("detail", solrInputDocument.getFieldValue(SolrDaoBase.ERROR_DETAIL));
        Assert.assertEquals("message", solrInputDocument.getFieldValue(SolrDaoBase.ERROR_MESSAGE));
        Assert.assertEquals("exceptionClass", solrInputDocument.getFieldValue(SolrDaoBase.EXCEPTION_CLASS));
        Assert.assertEquals("eventId", solrInputDocument.getFieldValue(SolrDaoBase.EVENT));
        Assert.assertEquals("relatedEventId", solrInputDocument.getFieldValue(SolrDaoBase.RELATED_EVENT));
        Assert.assertEquals("eventAsString", solrInputDocument.getFieldValue(SolrDaoBase.PAYLOAD_CONTENT));
        Assert.assertEquals("error", solrInputDocument.getFieldValue(SolrDaoBase.TYPE));
        Assert.assertEquals(1L, solrInputDocument.getFieldValue(SolrDaoBase.EXPIRY));
        Assert.assertEquals(12345L, solrInputDocument.getFieldValue(SolrDaoBase.CREATED_DATE_TIME));
    }
}
