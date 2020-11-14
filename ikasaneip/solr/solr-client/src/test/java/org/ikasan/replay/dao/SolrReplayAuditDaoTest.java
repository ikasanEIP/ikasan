package org.ikasan.replay.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.replay.model.SolrReplayAudit;
import org.ikasan.replay.model.SolrReplayAuditEvent;
import org.ikasan.replay.model.SolrReplayEvent;
import org.ikasan.spec.replay.ReplayAudit;
import org.ikasan.spec.replay.ReplayAuditEvent;
import org.ikasan.spec.solr.SolrDaoBase;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Ikasan Development on 29/08/2017.
 */
public class SolrReplayAuditDaoTest
{

    @Test
    public void test_convert_entity_to_solr_input_document() throws JsonProcessingException {
        SolrReplayAuditDao dao = new SolrReplayAuditDao();

        byte[] eventBytes = "event".getBytes();

        ReplayAudit replayAudit = new SolrReplayAudit(1L, "user", "replayReason"
            , "targetServer", 12345l);

        SolrReplayAuditEvent event = new SolrReplayAuditEvent("id", replayAudit, true, "resultMessage", 12345L);


        SolrInputDocument solrInputDocument = dao.convertEntityToSolrInputDocument(1L, event);

        Assert.assertEquals("replay_audit", solrInputDocument.getFieldValue(SolrDaoBase.TYPE));
        Assert.assertEquals(new ObjectMapper().writeValueAsString(event), solrInputDocument.getFieldValue(SolrDaoBase.PAYLOAD_CONTENT));
        Assert.assertEquals(1L, solrInputDocument.getFieldValue(SolrDaoBase.EXPIRY));
    }
}
