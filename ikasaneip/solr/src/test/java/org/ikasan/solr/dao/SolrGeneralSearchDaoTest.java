package org.ikasan.solr.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.solr.SolrDaoBase;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrGeneralSearchDaoTest extends SolrTestCaseJ4
{
    SolrGeneralSearchDaoImpl dao;

    @Test
    @DirtiesContext
    public void test_delete_expired_records() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
                .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
                .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "newcore"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("newcore");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("newcore", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", 0l);
            server.add("newcore", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("newcore", doc);
            server.commit();

            assertEquals(3, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(3  , server.query("newcore", new SolrQuery("*:*")).getResults().getNumFound());

            dao = new SolrGeneralSearchDaoImpl();
            dao.setSolrClient(server);
            dao.removeExpired("type");

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("newcore", new SolrQuery("*:*")).getResults().getNumFound());



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

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "newcore"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("newcore");
            createRequest.setConfigSet("minimal-dismax");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("type", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("newcore", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("newcore", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("newcore", doc);
            server.commit();

            dao = new SolrGeneralSearchDaoImpl();
            dao.setSolrClient(server);

            assertEquals(3, dao.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }


}
