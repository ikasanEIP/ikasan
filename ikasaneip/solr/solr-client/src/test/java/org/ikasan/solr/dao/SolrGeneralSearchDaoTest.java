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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
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


            assertEquals(3, dao.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

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


            assertEquals(3, dao.search(null, null, "", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

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


            assertEquals(2, dao.search("test", 0, System.currentTimeMillis() + 100000000l, 1,100, null).getResultList().size());

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

            assertEquals(3, dao.search(moduleNames, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test() throws Exception {

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
                if(i<5)
                {
                    doc.addField("payload", "ikasan1"+content + "ikasan3 rocks");
                }
                if(i>=5 && i<80)
                {
                    doc.addField("payload", "ikasan2 ikasan2 ikasan2"+content);
                }
                if(i>=80 && i<150)
                {
                    doc.addField("payload", "b-ikasan2/"+content);
                }
                if(i>=150 && i<200)
                {
                    doc.addField("payload", "b223648-bu-13442 "+content);
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
                , System.currentTimeMillis() + 100000000l, 100).getResultList().size());
            assertEquals(75, dao.search(moduleNames, null, "ikasan2 ikasan2 ikasan2", 0
                , System.currentTimeMillis() + 100000000l, 100).getResultList().size());
            assertEquals(70, dao.search(moduleNames, null, "b-ikasan2/", 0
                , System.currentTimeMillis() + 100000000l, 100).getResultList().size());
            assertEquals(50, dao.search(moduleNames, null, "b223648-bu-13442", 0
                , System.currentTimeMillis() + 100000000l, 100).getResultList().size());
            assertEquals(5, dao.search(null, null, "ikasan3", 0
                , System.currentTimeMillis() + 100000000l, 100).getResultList().size());
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
