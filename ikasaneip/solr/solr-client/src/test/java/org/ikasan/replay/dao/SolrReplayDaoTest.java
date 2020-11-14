package org.ikasan.replay.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.hospital.dao.SolrHospitalDao;
import org.ikasan.hospital.model.SolrExclusionEventActionImpl;
import org.ikasan.replay.model.SolrReplayEvent;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.solr.SolrDaoBase;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Ikasan Development on 29/08/2017.
 */
public class SolrReplayDaoTest
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

    @Test
    public void test_convert_entity_to_solr_input_document() {
        SolrReplayDao dao = new SolrReplayDao();

        byte[] eventBytes = "event".getBytes();

        SolrReplayEvent event = new SolrReplayEvent("eventId", eventBytes, "eventAsString"
            , "moduleName", "flowName", 30);
        event.setId(12l);

        SolrInputDocument solrInputDocument = dao.convertEntityToSolrInputDocument(1L, event);

        Assert.assertEquals("moduleName-replay-12", solrInputDocument.getFieldValue(SolrDaoBase.ID));
        Assert.assertEquals("moduleName", solrInputDocument.getFieldValue(SolrDaoBase.MODULE_NAME));
        Assert.assertEquals("replay", solrInputDocument.getFieldValue(SolrDaoBase.TYPE));
        Assert.assertEquals("flowName", solrInputDocument.getFieldValue(SolrDaoBase.FLOW_NAME));
        Assert.assertEquals("eventId", solrInputDocument.getFieldValue(SolrDaoBase.EVENT));
        Assert.assertEquals(eventBytes, solrInputDocument.getFieldValue(SolrDaoBase.PAYLOAD_CONTENT_RAW));
        Assert.assertEquals("eventAsString", solrInputDocument.getFieldValue(SolrDaoBase.PAYLOAD_CONTENT));
        Assert.assertEquals(1L, solrInputDocument.getFieldValue(SolrDaoBase.EXPIRY));
    }
}
