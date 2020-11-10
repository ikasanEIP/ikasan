package org.ikasan.systemevent.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.replay.model.SolrReplayEvent;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.systemevent.model.SolrSystemEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
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
public class SolrSystemEventDaoTest extends SolrTestCaseJ4
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

    private NodeConfig config;

    private SolrSystemEventDao dao;

    @Before
    public void setup()
    {

        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString()).build();


    }

    private void init(EmbeddedSolrServer server) throws IOException, SolrServerException
    {
        CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
        createRequest.setCoreName("ikasan");
        createRequest.setConfigSet("minimal");
        server.request(createRequest);

        dao = new SolrSystemEventDao();
        dao.setSolrClient(server);
    }


    @Test
    @DirtiesContext
    public void test_query_system_events() throws Exception
    {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrSystemEvent systemEvent = new SolrSystemEvent();
            systemEvent.setModuleName("moduleName");
            systemEvent.setTimestampLong(System.currentTimeMillis());
            systemEvent.setActor("TestUser");
            systemEvent.setExpiryLong(0);
            systemEvent.setId("1");

            dao.save(systemEvent);

            List<SystemEvent> systemEvents = dao.list(new ArrayList<>(), null, null, null);

            assertEquals(1, systemEvents.size());

        }
    }

    @Test
    @DirtiesContext
    public void test_query_system_events_with_payload_content() throws Exception
    {
        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrSystemEvent systemEvent = new SolrSystemEvent();
            systemEvent.setModuleName("moduleName");
            systemEvent.setTimestampLong(System.currentTimeMillis());
            systemEvent.setActor("testUser");
            systemEvent.setExpiryLong(0);
            systemEvent.setId("1");

            dao.save(systemEvent);

            SolrSystemEvent systemEvent1 = new SolrSystemEvent();
            systemEvent1.setModuleName("moduleName");
            systemEvent1.setTimestampLong(System.currentTimeMillis());
            systemEvent1.setActor("adminUser");
            systemEvent1.setExpiryLong(0);
            systemEvent1.setId("2");

            dao.save(systemEvent1);

            List<SystemEvent> systemEvents = dao.list(null, "test*",null,null);

            assertEquals(1, systemEvents.size());

        }
    }


    @Test
    @DirtiesContext
    public void test_housekeep_success() throws Exception
    {
        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrSystemEvent systemEvent = new SolrSystemEvent(1l, "moduleName",
                "actor", "action", "subject", System.currentTimeMillis());

            dao.save(systemEvent);

            List<SystemEvent> replayEventList = dao.list(null, "*actor*",null,null);

            assertEquals(1, replayEventList.size());

            dao.deleteExpired();

            replayEventList = dao.list(null, "actor",null,null);

            assertEquals(0, replayEventList.size());
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

        dao = new SolrSystemEventDao();
        dao.setSolrClient(server);

        SolrSystemEvent systemEvent = new SolrSystemEvent();
        systemEvent.setModuleName("moduleName");
        systemEvent.setTimestampLong(System.currentTimeMillis());
        systemEvent.setExpiryLong(0);
        systemEvent.setId("1");

        dao.save(systemEvent);
    }


    public static String TEST_HOME()
    {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH()
    {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
