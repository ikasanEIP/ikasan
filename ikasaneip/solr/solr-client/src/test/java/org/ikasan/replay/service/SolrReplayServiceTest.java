package org.ikasan.replay.service;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.replay.dao.SolrReplayAuditDao;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.replay.model.SolrReplayEvent;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.ikasan.wiretap.service.SolrWiretapServiceImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Ikasan Development on 29/08/2017.
 */
public class SolrReplayServiceTest extends SolrTestCaseJ4
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


            SolrReplayDao dao = new SolrReplayDao();
            dao.setSolrClient(server);

            BatchInsert<ReplayEvent> batchInsert = new SolrReplayServiceImpl(dao);

            SolrReplayEvent replayEvent = new SolrReplayEvent();
            replayEvent.setModuleName("moduleName");
            replayEvent.setFlowName("flowName");
            replayEvent.setEvent("event".getBytes());
            replayEvent.setTimestamp(System.currentTimeMillis());
            replayEvent.setExpiry(0);
            replayEvent.setId(1l);

            List<ReplayEvent> events = new ArrayList<>();
            events.add(replayEvent);

            batchInsert.insert(events);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

        }
    }

    @Test
    @DirtiesContext
    public void test_save_event() throws Exception
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


            SolrReplayDao dao = new SolrReplayDao();
            dao.setSolrClient(server);

            SolrReplayServiceImpl solrReplayServiceImpl = new SolrReplayServiceImpl(dao);

            SolrReplayEvent replayEvent = new SolrReplayEvent();
            replayEvent.setModuleName("moduleName");
            replayEvent.setFlowName("flowName");
            replayEvent.setEvent("event".getBytes());
            replayEvent.setTimestamp(System.currentTimeMillis());
            replayEvent.setExpiry(0);
            replayEvent.setId(1l);
            List<ReplayEvent> events = new ArrayList<>();
            events.add(replayEvent);

            solrReplayServiceImpl.save(replayEvent);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

        }
    }

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

        SolrReplayDao dao = new SolrReplayDao();
        dao.setSolrClient(server);

        SolrReplayEvent replayEvent = new SolrReplayEvent();
        replayEvent.setModuleName("moduleName");
        replayEvent.setFlowName("flowName");
        replayEvent.setEventAsString("");
        replayEvent.setEvent("event".getBytes());
        replayEvent.setTimestamp(System.currentTimeMillis());
        replayEvent.setExpiry(0);
        replayEvent.setId(1l);

        SolrReplayServiceImpl solrReplayService = new SolrReplayServiceImpl(dao);

        solrReplayService.save(replayEvent);
    }


    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
