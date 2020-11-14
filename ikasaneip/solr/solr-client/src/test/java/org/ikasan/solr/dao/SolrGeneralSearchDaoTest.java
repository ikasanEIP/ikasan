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
import org.ikasan.solr.model.IkasanSolrDocument;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

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
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
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
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
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

    @Test
    @DirtiesContext
    public void test_find_by_id() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            assertEquals(3, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(3  , server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            IkasanSolrDocument document = dao.findById("type", "1");

            Assert.assertNotNull(document);
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_error_uri() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            doc.addField("errorUri", "1");
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            doc.addField("errorUri", "2");
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            doc.addField("errorUri", "3");
            server.add("ikasan", doc);
            server.commit();

            assertEquals(3, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(3  , server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            IkasanSolrDocument document = dao.findByErrorUri("type", "1");

            Assert.assertNotNull(document);
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

        dao.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100, false, null ,null );

    }

    @Test
    @DirtiesContext
    public void test_search_success() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();


            assertEquals(3, dao.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_sort_asc() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "a test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "b test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "c test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            List<IkasanSolrDocument> results = dao.search(null, null, "test", 0
                , System.currentTimeMillis() + 100000000l, 100, false, "payload"
                ,SolrGeneralDaoImpl.ASCENDING ).getResultList();

            assertEquals(3, results.size());

            Assert.assertEquals("1", results.get(0).getId());
            Assert.assertEquals("2", results.get(1).getId());
            Assert.assertEquals("3", results.get(2).getId());
        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_sort_desc() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "a test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "b test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "c test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            List<IkasanSolrDocument> results = dao.search(null, null, "test", 0
                , System.currentTimeMillis() + 100000000l, 100, false, "payload"
                ,SolrGeneralDaoImpl.DESCENDING ).getResultList();

            assertEquals(3, results.size());

            Assert.assertEquals("3", results.get(0).getId());
            Assert.assertEquals("2", results.get(1).getId());
            Assert.assertEquals("1", results.get(2).getId());
        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_empty_string() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();


            assertEquals(3, dao.search(null, null, "", 0, System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_offset_success() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            server.commit();


            assertEquals(2, dao.search("test", 0, System.currentTimeMillis() + 100000000l, 1,100, null, false, null ,null ).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_no_module_or_flow() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            assertEquals(3, dao.search("test", 0, System.currentTimeMillis() + 100000000l, 100, new ArrayList<>(), false, null ,null ).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_with_query_filter() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {

            init(server);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("moduleName", "test");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("moduleName", "test");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("moduleName", "test");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();


            Set<String> moduleNames = new HashSet<String>();
            moduleNames.add("test");

            assertEquals(3, dao.search(moduleNames, null, "test", 0, System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    /**
     * The idea of this test is to take a large and random json data set that contains
     * 88310 records and seed it with some key words are not found within the
     * dataset. Then perform queries against the dataset to confirm that the results are
     * narrowed accurately.
     */
    public void test_large_seeded_dataset() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {

            init(server);

            BufferedReader br = this.loadDataFileStream("/data/complexSearchData/australian_users_items.json.gz");
            String content;
            int i=0;
            while ((content = br.readLine()) != null)
            {
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", ""+i);
                doc.addField("moduleName", "test");
                doc.addField("flowName", "test");
                doc.addField("componentName", "test");
                doc.addField("type", "type");


                // insert variants of ikasan
                if(i<5)
                {
                    doc.addField("payload", "ikasan1" +content + "ikasan3 rocks");
                    doc.addField("event", "mrsquid1");
                }
                if(i>=5 && i<80)
                {
                    doc.addField("payload", "ikasan2 ikasan2 ikasan2"+content);
                    doc.addField("event", "mrsquid2");
                }

                // insert ikasan with some reserved characters
                if(i>=80 && i<150)
                {
                    doc.addField("payload", "b-ikasan2/"+content);
                    doc.addField("event", "mrsquid3");
                }

                // insert something that looks like a trade id
                if(i>=150 && i<200)
                {
                    doc.addField("payload", "b223648-bu-13442 "+content);
                    doc.addField("event", "mrsquid4");
                }

                // insert something that looks like a trade id somewhere in the middle of the content
                if(i>=5000 && i<5090)
                {
                    doc.addField("payload", content.substring(0, 50) + " 33454432 " + content.substring(51, content.length()-1));
                    doc.addField("event", "mrsquid5");
                }

                // insert something that looks like a trade id somewhere in the middle of the content that contains reserved characters
                if(i>=6000 && i<6035)
                {
                    doc.addField("payload", content.substring(0, 50) + "  3345:44932-bb:9 " + content.substring(51, content.length()-1));
                    doc.addField("event", "mrsquid6");
                }

                doc.addField("expiry", 100l);
                doc.addField("timestamp", 100l);
                server.add("ikasan", doc);

                i++;
            }

            server.commit();

            Set<String> moduleNames = new HashSet<String>();
            moduleNames.add("test");
            assertEquals(5, dao.search(moduleNames, null, "ikasan1", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(75, dao.search(moduleNames, null, "\"ikasan2 ikasan2 ikasan2\"", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(70, dao.search(moduleNames, null, "b-ikasan2/", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(50, dao.search(moduleNames, null, "b223648-bu-13442", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(5, dao.search(null, null, "ikasan3", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(5, dao.search(null, null, "ikasan3 rocks", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(90, dao.search(null, null, "33454432", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());

            // Note partial searches require the * wildcard character
            assertEquals(90, dao.search(null, null, "3345443*", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(90, dao.search(null, null, "*345443*", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());

            // Perform some queries with various logical elements to the query
            assertEquals(5, dao.search(moduleNames, null, "\"ikasan1\" AND ikasan3", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(55, dao.search(moduleNames, null, "ikasan1 OR b223648-bu-13442", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(5, dao.search(moduleNames, null, "ikasan3 AND (ikasan1 OR \"b223648-bu-13442\")", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(5, dao.search(moduleNames, null, "((ikasan3 AND (ikasan1 OR \"b223648-bu-13442\")))", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(0, dao.search(moduleNames, null, "ikasan3 AND NOT (ikasan1 OR \"b223648-bu-13442\")", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(50, dao.search(moduleNames, null, "(ikasan3 AND NOT ikasan1) OR \"b223648-bu-13442\"", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());

            // Perform some queries that are tokenised by the lexical analyser.
            assertEquals(35, dao.search(moduleNames, null, "\"3345:44932-bb:9\"", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(35, dao.search(moduleNames, null, "3345:44932-bb:9", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(35, dao.search(moduleNames, null, "\"44932-bb\"", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(35, dao.search(moduleNames, null, "44932-bb", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(35, dao.search(moduleNames, null, "3345:44932-bb", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());
            assertEquals(35, dao.search(moduleNames, null, "3345 AND :44932-bb:", 0
                , System.currentTimeMillis() + 100000000l, 100, false, null ,null ).getResultList().size());

            // Negate queries
            assertEquals(88305, dao.search(moduleNames, null, "ikasan1", 0
                , System.currentTimeMillis() + 100000000l, 0, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88235, dao.search(moduleNames, null, "\"ikasan2 ikasan2 ikasan2\"", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88240, dao.search(moduleNames, null, "b-ikasan2/", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88260, dao.search(moduleNames, null, "b223648-bu-13442", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88305, dao.search(null, null, "ikasan3", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88305, dao.search(null, null, "ikasan3 rocks", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88220, dao.search(null, null, "33454432", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());

            // Note partial searches require the * wildcard character
            assertEquals(88220, dao.search(null, null, "3345443*", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88220, dao.search(null, null, "*345443*", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());

            // Perform some queries with various logical elements to the query
            assertEquals(88305, dao.search(moduleNames, null, "\"ikasan1\" AND ikasan3", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88255, dao.search(moduleNames, null, "ikasan1 OR b223648-bu-13442", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88255, dao.search(moduleNames, null, "ikasan3 AND (ikasan1 OR \"b223648-bu-13442\")", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88255, dao.search(moduleNames, null, "((ikasan3 AND (ikasan1 OR \"b223648-bu-13442\")))", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(0, dao.search(moduleNames, null, "ikasan3 AND NOT (ikasan1 OR \"b223648-bu-13442\")", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88260, dao.search(moduleNames, null, "(ikasan3 AND NOT ikasan1) OR \"b223648-bu-13442\"", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());

            // Perform some queries that are tokenised by the lexical analyser.
            assertEquals(88275, dao.search(moduleNames, null, "\"3345:44932-bb:9\"", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88275, dao.search(moduleNames, null, "3345:44932-bb:9", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88275, dao.search(moduleNames, null, "\"44932-bb\"", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88275, dao.search(moduleNames, null, "44932-bb", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88275, dao.search(moduleNames, null, "3345:44932-bb", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());
            assertEquals(88275, dao.search(moduleNames, null, "3345 AND :44932-bb:", 0
                , System.currentTimeMillis() + 100000000l, 100, true, null ,null ).getTotalNumberOfResults());

            // Mix it up with some variants on the use of module, flow and component names.
            Set<String> flowNames = new HashSet<String>();
            moduleNames.add("test");
            Set<String> componentNames = new HashSet<String>();
            componentNames.add("test");
            List<String> entityTypes = new ArrayList<>();
            assertEquals(5, dao.search(moduleNames, flowNames, componentNames, null, "ikasan1", 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());

            componentNames = new HashSet<String>();
            componentNames.add("blah");
            assertEquals(0, dao.search(moduleNames, flowNames, componentNames, null, "ikasan1", 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());

            flowNames = new HashSet<String>();
            flowNames.add("test");
            componentNames = new HashSet<String>();
            componentNames.add("blah");
            assertEquals(0, dao.search(moduleNames, flowNames, componentNames, null, "ikasan1", 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());

            // Query using event id
            assertEquals(5, dao.search(null, null, null, "mrsquid1", null, 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());

            assertEquals(75, dao.search(null, null, null, "mrsquid2", null, 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());

            assertEquals(70, dao.search(null, null, null, "mrsquid3", null, 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());

            assertEquals(50, dao.search(null, null, null, "mrsquid4", null, 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());

            assertEquals(90, dao.search(null, null, null, "mrsquid5", null, 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());

            assertEquals(35, dao.search(null, null, null, "mrsquid6", null, 0
                , System.currentTimeMillis() + 100000000l, 0,  100, entityTypes, false, null ,null ).getResultList().size());
        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }

    protected BufferedReader loadDataFileStream(String fileName) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        GZIPInputStream gzip = new GZIPInputStream(inputStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(gzip));

        return br;
    }


}
