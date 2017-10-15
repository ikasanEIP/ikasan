package org.ikasan.wiretap.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrWiretapDaoTest extends SolrTestCaseJ4
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

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("id", new Integer(1));

            SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fields);
            server.request(schemaRequest);


            SolrWiretapDao solrCloudBase = new SolrWiretapDao();
            solrCloudBase.setSolrClient(server);

            WiretapFlowEvent event = new WiretapFlowEvent("moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", 12345l, "event", 0l);


            solrCloudBase.save(event);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("newcore", new SolrQuery("*:*")).getResults().getNumFound());

            server.close();

        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
