package org.ikasan.wiretap.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.replay.model.SolrReplayEvent;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.SolrWiretapEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrWiretapDaoTest
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

        SolrWiretapDao dao = new SolrWiretapDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", System.currentTimeMillis(), "event");


        dao.save(event);
    }

    @Test
    public void test_convert_entity_to_solr_input_document() {
        SolrWiretapDao dao = new SolrWiretapDao();

        SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
            "eventId", "relatedEventId", System.currentTimeMillis(), "event");

        SolrInputDocument solrInputDocument = dao.convertEntityToSolrInputDocument(1L, event);

        Assert.assertEquals("moduleName-wiretap-1", solrInputDocument.getFieldValue(SolrDaoBase.ID));
        Assert.assertEquals("moduleName", solrInputDocument.getFieldValue(SolrDaoBase.MODULE_NAME));
        Assert.assertEquals("wiretap", solrInputDocument.getFieldValue(SolrDaoBase.TYPE));
        Assert.assertEquals("flowName", solrInputDocument.getFieldValue(SolrDaoBase.FLOW_NAME));
        Assert.assertEquals("componentName", solrInputDocument.getFieldValue(SolrDaoBase.COMPONENT_NAME));
        Assert.assertEquals("eventId", solrInputDocument.getFieldValue(SolrDaoBase.EVENT));
        Assert.assertEquals("event", solrInputDocument.getFieldValue(SolrDaoBase.PAYLOAD_CONTENT));
        Assert.assertEquals(1L, solrInputDocument.getFieldValue(SolrDaoBase.EXPIRY));
    }
}
