package org.ikasan.solr.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrCloudDaoBaseTest extends SolrTestCaseJ4
{

    @Test
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
            doc.addField("expiry", 0);
            server.add("newcore", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", 0);
            server.add("newcore", doc);
            doc = new SolrInputDocument();
            doc.addField("type", "type");
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("newcore", doc);
            server.commit();

            assertEquals(3, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(3  , server.query("newcore", new SolrQuery("*:*")).getResults().getNumFound());

            SolrCloudBaseImpl solrCloudBase = new SolrCloudBaseImpl();
            solrCloudBase.setSolrClient(server);

            solrCloudBase.removeExpired("type");

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("newcore", new SolrQuery("*:*")).getResults().getNumFound());

        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }

    private class SolrCloudBaseImpl extends SolrDaoBase
    {

    }

}
