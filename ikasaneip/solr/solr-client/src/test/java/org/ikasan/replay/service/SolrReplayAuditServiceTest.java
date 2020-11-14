package org.ikasan.replay.service;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.replay.dao.SolrReplayAuditDao;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.replay.model.SolrReplayAudit;
import org.ikasan.replay.model.SolrReplayAuditEvent;
import org.ikasan.replay.model.SolrReplayEvent;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.replay.ReplayAudit;
import org.ikasan.spec.replay.ReplayEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ikasan Development on 29/08/2017.
 */
public class SolrReplayAuditServiceTest extends SolrTestCaseJ4
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

    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_constructor_dao_null_exception()
    {
        new SolrReplayServiceImpl(null);
    }

    @Test
    @DirtiesContext
    public void test_save_events_list_batch_insert() throws Exception
    {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
            .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);


            SolrReplayAuditDao dao = new SolrReplayAuditDao();
            dao.setSolrClient(server);

            SolrReplayAuditServiceImpl solrReplayAuditService = new SolrReplayAuditServiceImpl(dao);

            ReplayAudit replayAudit = new SolrReplayAudit(1L, "user", "replayReason"
                , "targetServer", 12345l);

            SolrReplayAuditEvent event = new SolrReplayAuditEvent("id", replayAudit, true, "resultMessage", 12345L);

            List<SolrReplayAuditEvent> events = new ArrayList<>();
            events.add(event);

            solrReplayAuditService.insert(events);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

        }
    }


    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
