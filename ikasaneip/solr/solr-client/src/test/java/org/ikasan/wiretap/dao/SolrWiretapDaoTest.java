package org.ikasan.wiretap.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.SolrWiretapEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
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

    @Test
    @DirtiesContext
    public void test_delete_expired_records() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", 12345l, "event");


            solrCloudBase.save(event);

            assertEquals(2, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(2, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());


            Thread.sleep(2000);

            solrCloudBase.deleteAllExpired();

            assertEquals(0, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(0, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleName_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            HashSet<String> componentNames = new HashSet<String>();


            PagedSearchResult<WiretapEvent> results =  solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    componentNames, null, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 10);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleName_componentNames_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            HashSet<String> flowNames = new HashSet<String>();
            flowNames.add("flowName");

            PagedSearchResult<WiretapEvent> results =  solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    flowNames, null, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 10);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleName_componentNames_flowNames_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            HashSet<String> componentNames = new HashSet<String>();
            componentNames.add("componentName");

            HashSet<String> flowNames = new HashSet<String>();
            flowNames.add("flowName");

            PagedSearchResult<WiretapEvent> results =  solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    flowNames, componentNames, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 10);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleName_componentNames_flowNames_keyword_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            HashSet<String> componentNames = new HashSet<String>();
            componentNames.add("componentName");

            HashSet<String> flowNames = new HashSet<String>();
            flowNames.add("flowName");

            PagedSearchResult<WiretapEvent> results =  solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    flowNames, componentNames, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "odd one out");

            Assert.assertEquals("Results must equal", results.getResultSize(), 2);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            WiretapEvent wiretapEvent = solrCloudBase.findById(3l);

            Assert.assertEquals("Results must equal", wiretapEvent.getEvent(), "odd one out");

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id_not_found() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            WiretapEvent wiretapEvent = solrCloudBase.findById(20l);

            Assert.assertEquals("Results must equal", wiretapEvent, null);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleNames_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            String flowName = null;


            PagedSearchResult<WiretapEvent> results =  solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    flowName, null, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 10);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleNames_componentName_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            PagedSearchResult<WiretapEvent> results =  solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    "flowName", null, null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 10);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleNames_componentName_flowName_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            PagedSearchResult<WiretapEvent> results =  solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    "flowName", "componentName", null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "");

            Assert.assertEquals("Results must equal", results.getResultSize(), 10);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_moduleNames_componentName_flowName_keyword_success() throws Exception {
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);
            solrCloudBase.setDaysToKeep(0);

            SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(2l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(3l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "odd one out");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(4l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            event = new SolrWiretapEvent(5l, "moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", System.currentTimeMillis(), "event");


            solrCloudBase.save(event);

            HashSet<String> moduleNames = new HashSet<String>();
            moduleNames.add("moduleName");

            PagedSearchResult<WiretapEvent> results =  solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
                    "flowName", "componentName", null, null, new Date(System.currentTimeMillis() - 100000000)
                    , new Date(System.currentTimeMillis() + 100000000), "odd one out");

            Assert.assertEquals("Results must equal", results.getResultSize(), 2);

            server.close();

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

        SolrWiretapDao solrCloudBase = new SolrWiretapDao();
        solrCloudBase.setSolrClient(server);
        solrCloudBase.setDaysToKeep(0);

        SolrWiretapEvent event = new SolrWiretapEvent(1l, "moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", System.currentTimeMillis(), "event");


        solrCloudBase.save(event);
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

        SolrWiretapDao solrCloudBase = new SolrWiretapDao();
        solrCloudBase.setSolrClient(server);
        solrCloudBase.setDaysToKeep(0);


        solrCloudBase.findById(1l);
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

        SolrWiretapDao solrCloudBase = new SolrWiretapDao();
        solrCloudBase.setSolrClient(server);
        solrCloudBase.setDaysToKeep(0);

        HashSet<String> moduleNames = new HashSet<String>();
        moduleNames.add("moduleName");

        solrCloudBase.findWiretapEvents(0,10,"timestamp", true,  moduleNames,
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
