package org.ikasan.exclusion.service;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.exclusion.model.SolrExclusionEventImpl;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrExclusionServiceTest extends SolrTestCaseJ4
{

    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_constructor_dao_null_exception()
    {
        new SolrExclusionServiceImpl(null);
    }

    @Test
    @DirtiesContext
    public void test_save() throws Exception {
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


            SolrExclusionEventDao  dao = new SolrExclusionEventDao ();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrExclusionServiceImpl solrExclusionService = new SolrExclusionServiceImpl(dao);

            SolrExclusionEventImpl event = new SolrExclusionEventImpl("moduleName", "flowName", "componentName",
                "event".getBytes(), "uri");


            solrExclusionService.save(event);

            assertEquals(2, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(2, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

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
