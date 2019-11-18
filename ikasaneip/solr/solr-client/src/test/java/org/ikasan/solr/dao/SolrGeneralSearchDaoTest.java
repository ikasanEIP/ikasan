package org.ikasan.solr.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private SolrGeneralDaoImpl dao;

    private NodeConfig config;

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

        dao = new SolrGeneralDaoImpl();
        dao.setSolrClient(server);
    }


    @Test
    @DirtiesContext
    public void test_delete_expired_records_by_type() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

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


        }
    }

    @Test
    @DirtiesContext
    public void test_delete_expired_records() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

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


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

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


            assertEquals(3, dao.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_offset_success() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

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


            assertEquals(2, dao.search(new HashSet<>(), new HashSet<>(),new HashSet<>(), null,"test", 0, System.currentTimeMillis() + 100000000l, 1,100, null).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_no_module_or_flow() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

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

            assertEquals(3, dao.search("test", 0, System.currentTimeMillis() + 100000000l, 100, new ArrayList<>()).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_with_query_filter() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {

            init(server);

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
