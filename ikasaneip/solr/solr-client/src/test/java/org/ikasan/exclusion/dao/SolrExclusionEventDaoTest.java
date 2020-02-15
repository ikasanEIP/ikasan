package org.ikasan.exclusion.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.exclusion.model.SolrExclusionEventImpl;
import org.ikasan.spec.exclusion.ExclusionEvent;
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

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrExclusionEventDaoTest extends SolrTestCaseJ4
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

    private SolrExclusionEventDao dao;

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

        dao = new SolrExclusionEventDao ();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);
    }

    @Test
    @DirtiesContext
    public void test_delete_expired_records() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            ExclusionEvent event = new SolrExclusionEventImpl("moduleName", "flowName", "componentName",
                "event".getBytes(), "uri");
            event.setId(1);


            dao.save(event);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());


            Thread.sleep(2000);

            dao.deleteAllExpired();

            assertEquals(0, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(0, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            server.close();

        }
    }

    @Test
    @DirtiesContext
    public void test_delete_by_error_uri() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            ExclusionEvent event = new SolrExclusionEventImpl("moduleName", "flowName", "componentName",
                    "event".getBytes(), "uri");
            event.setId(1);


            dao.save(event);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());


            Thread.sleep(2000);

            dao.delete("uri");

            assertEquals(0, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(0, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

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

        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        ExclusionEvent event = new SolrExclusionEventImpl("moduleName", "flowName", "componentName",
                "event".getBytes(), "uri");


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

        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.delete("");
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_find_exception() throws Exception
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.find(null, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_find_2_exception() throws Exception
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.find(null, null, null, null, null, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_row_count_exception() throws Exception
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.rowCount(null, null, null, null, null);
    }

    @Test
    @DirtiesContext
    public void test_find_uri_success() throws Exception
    {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            ExclusionEvent event = new SolrExclusionEventImpl("moduleName", "flowName", "componentName",
                    "event".getBytes(), "uri");
            event.setId(1);


            dao.save(event);

            Assert.assertNotNull(dao.find("uri"));

            server.close();

        }
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_find_all() throws Exception
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.findAll();
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_harvestable_records_exception() throws Exception
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.getHarvestableRecords(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    @DirtiesContext
    public void test_delete_exception() throws Exception
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        dao.delete(null, null, null);
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
