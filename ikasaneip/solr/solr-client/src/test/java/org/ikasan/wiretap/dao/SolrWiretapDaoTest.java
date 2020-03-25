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
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.spec.search.PagedSearchResult;
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
public class SolrWiretapDaoTest extends SolrTestCaseJ4
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

    private SolrWiretapDao dao;

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

        dao = new SolrWiretapDao();
        dao.setSolrClient(server);
    }

    @Test
    @DirtiesContext
    public void test_delete_expired_records() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrDocumentList solrDocuments = server.query(new SolrQuery("*:*")).getResults();
            System.out.println(solrDocuments);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", 12345l, "event");


            dao.save(event);

            solrDocuments = server.query(new SolrQuery("*:*")).getResults();
            System.out.println(solrDocuments);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());


            Thread.sleep(2000);

            dao.deleteAllExpired();

            assertEquals(0, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(0, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleName_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            HashSet<String> componentNames = new HashSet<String>();


            PagedSearchResult<WiretapEvent> results =  dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    componentNames, null, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 5);

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleName_componentNames_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            HashSet<String> flowNames = new HashSet<String>();
            flowNames.add("flowName");

            PagedSearchResult<WiretapEvent> results =  dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    flowNames, null, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 5);

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleName_componentNames_flowNames_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            HashSet<String> componentNames = new HashSet<String>();
            componentNames.add("componentName");

            HashSet<String> flowNames = new HashSet<String>();
            flowNames.add("flowName");

            PagedSearchResult<WiretapEvent> results =  dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    flowNames, componentNames, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 5);

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleName_componentNames_flowNames_keyword_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            HashSet<String> componentNames = new HashSet<String>();
            componentNames.add("componentName");

            HashSet<String> flowNames = new HashSet<String>();
            flowNames.add("flowName");

            PagedSearchResult<WiretapEvent> results =  dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    flowNames, componentNames, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "odd one out");

            Assert.assertEquals("Results must equal", results.getResultSize(), 1);

        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            WiretapEvent wiretapEvent = dao.findById(3l);

            Assert.assertEquals("Results must equal", wiretapEvent.getEvent(), "odd one out");
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id_not_found() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            WiretapEvent wiretapEvent = dao.findById(20l);

            Assert.assertEquals("Results must equal", wiretapEvent, null);
        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleNames_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            String flowName = null;


            PagedSearchResult<WiretapEvent> results =  dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    flowName, null, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 5);

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleNames_componentName_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            PagedSearchResult<WiretapEvent> results =  dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    "flowName", null, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 5);
        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleNames_componentName_flowName_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            PagedSearchResult<WiretapEvent> results =  dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    "flowName", "componentName", null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 5);
        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleNames_componentName_flowName_keyword_success() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            dao.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            dao.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            dao.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            PagedSearchResult<WiretapEvent> results =  dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    "flowName", "componentName", null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "odd one out");

            Assert.assertEquals("Results must equal", results.getResultSize(), 1);

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

        SolrWiretapDao dao = new SolrWiretapDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", System.currentTimeMillis(), "event");


        dao.save(event);
    }

    @Test(expected = RuntimeException.class)
    @DirtiesContext
    public void test_find_by_id_exception() throws Exception
    {
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(server).request(with(any(SolrRequest.class)));
                will(throwException(new RuntimeException("Error")));

            }
        });

        SolrWiretapDao dao = new SolrWiretapDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);


        dao.findById(1l);
    }

    @Test(expected = RuntimeException.class)
    @DirtiesContext
    public void test_search_with_moduleNames_componentName_flowName_keyword_exception() throws Exception {
        Path path = createTempDir();

        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(server).request(with(any(SolrRequest.class)));
                will(throwException(new RuntimeException("Error")));

            }
        });

        SolrWiretapDao dao = new SolrWiretapDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        HashSet<String> moduleNames = new HashSet<String>();
        moduleNames.add("moduleName");

        dao.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    "flowName", "componentName", null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "odd one out");

    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
