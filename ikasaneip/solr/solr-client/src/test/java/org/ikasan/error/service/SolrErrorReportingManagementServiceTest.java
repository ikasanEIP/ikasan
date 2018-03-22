package org.ikasan.error.service;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.replay.service.SolrReplayServiceImpl;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrErrorReportingManagementServiceTest extends SolrTestCaseJ4
{


    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_constructor_dao_null_exception()
    {
        new SolrErrorReportingManagementServiceImpl(null);
    }

    @Test
    @DirtiesContext
    public void test_save_success() throws Exception {
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

            SolrErrorReportingManagementServiceImpl solrErrorReportingManagementService
                    = new SolrErrorReportingManagementServiceImpl(dao);

            SolrErrorOccurrence event = new SolrErrorOccurrence("moduleName", "flowName", "componentName",
                "eventId", "relatedEventId", "event", 12345l);


            solrErrorReportingManagementService.save(event);

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
