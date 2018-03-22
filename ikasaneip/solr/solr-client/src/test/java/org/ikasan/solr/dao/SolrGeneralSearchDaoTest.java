package org.ikasan.solr.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrGeneralSearchDaoTest extends SolrTestCaseJ4
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

    SolrGeneralDaoImpl dao;

    @Test
    @DirtiesContext
    public void test_delete_expired_records_by_type() throws Exception {
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


            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            assertEquals(3, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(3  , server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);
            dao.removeExpired("type");

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());


            server.close();
        }
    }

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


            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            assertEquals(3, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(3  , server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);
            dao.removeExpired();

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());


            server.close();
        }
    }


    @Test(expected = RuntimeException.class)
    @DirtiesContext
    public void test_search_exception() throws Exception
    {
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(server).request(with(any(SolrRequest.class)));
                will(throwException(new RuntimeException("Error")));

            }
        });

        dao = new SolrGeneralDaoImpl();
        dao.setSolrClient(server);

        dao.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100);

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
            createRequest.setConfigSet("minimal-dismax");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("type", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            assertEquals(3, dao.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_with_query_filter() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
                .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
                .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal-dismax");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("moduleName", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("moduleName", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("moduleName", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            Set<String> moduleNames = new HashSet<String>();
            moduleNames.add("test");

            assertEquals(3, dao.search(moduleNames, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }


}
