package org.ikasan.error.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.ikasan.wiretap.model.SolrWiretapEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrErrorReportingServiceDaoTest extends SolrTestCaseJ4
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


            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", "event", 12345l);


            dao.save(event);

            assertEquals(2, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(2, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());


            Thread.sleep(2000);

            dao.deleteExpired();

            assertEquals(0, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(0, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_uri_success() throws Exception {
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


            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri");

            dao.save(event);

            Assert.assertNotNull(dao.find("uri"));

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success() throws Exception {
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


            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri1");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri2");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri3");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri4");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri5");

            dao.save(event);

            List<ErrorOccurrence> result = dao.find(null, null, null, null, null, 100);

            Assert.assertEquals(result.size(), 10);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_null_result() throws Exception {
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


            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri1");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri2");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri3");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri4");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri5");

            dao.save(event);

            ErrorOccurrence result = dao.find("blah");

            Assert.assertNull(result);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_module_name_success() throws Exception {
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


            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri1");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri2");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri3");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri4");

            dao.save(event);

            event = new SolrErrorOccurrence("unique", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri5");

            dao.save(event);

            List<String> moduleNames = new ArrayList<>();
            moduleNames.add("unique");

            List<ErrorOccurrence> result = dao.find(moduleNames, null, null, null, null, 100);

            Assert.assertEquals(result.size(), 2);

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_search_flow_name_success() throws Exception {
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


            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri1");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri2");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri3");

            dao.save(event);

            event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri4");

            dao.save(event);

            event = new SolrErrorOccurrence("unique", "flowName", "componentName",
                    "eventId", "relatedEventId", "event", 12345l);
            event.setUri("uri5");

            dao.save(event);

            List<String> flowNames = new ArrayList<>();
            flowNames.add("flowName");

            List<ErrorOccurrence> result = dao.find(null, flowNames, null, null, null, 100);

            Assert.assertEquals(result.size(), 10);

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

        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", "event", 12345l);
        event.setUri("uri1");

        dao.save(event);
    }

    @Test(expected = RuntimeException.class)
    @DirtiesContext
    public void test_find_by_id_exception() throws Exception
    {
        mockery.checking(new Expectations()
        {
            {
                oneOf(server).request(with(any(SolrRequest.class)));
                will(throwException(new RuntimeException("Error")));
            }
        });

        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", "event", 12345l);
        event.setUri("uri1");

        dao.find("");
    }

    @Test(expected = RuntimeException.class)
    @DirtiesContext
    public void test_find_exception() throws Exception
    {
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(server).request(with(any(SolrRequest.class)));
                will(throwException(new RuntimeException("Error")));

            }
        });

        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", "event", 12345l);
        event.setUri("uri1");

        dao.find(null, null, null, null, null, 100);
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_find_uris_exception() throws Exception
    {
        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.find(new ArrayList<>());
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_find_1_exception() throws Exception
    {
        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.find(null, null, null, null, null,null, null, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_row_count_exception() throws Exception
    {
        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.rowCount(null, null, null, null, null);
    }


    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
