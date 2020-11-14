package org.ikasan.error.reporting.service;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.persistence.BatchInsert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrErrorReportingServiceTest extends SolrTestCaseJ4
{


    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_constructor_dao_null_exception()
    {
        new SolrErrorReportingServiceImpl(null);
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

            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrErrorReportingServiceImpl solrErrorReportingManagementService
                    = new SolrErrorReportingServiceImpl(dao);

            SolrErrorOccurrence event = new SolrErrorOccurrence("uri", "moduleName", "flowName"
                , "componentName", "action", "detail", "message", "exceptionClass"
                , "eventId", "relatedEventId", "eventAsString", 12345L);



            solrErrorReportingManagementService.save(event);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

        }
    }

    @Test
    @DirtiesContext
    public void test_save_bulk_success() throws Exception {
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

            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrErrorReportingServiceImpl solrErrorReportingManagementService
                = new SolrErrorReportingServiceImpl(dao);

            SolrErrorOccurrence event = new SolrErrorOccurrence("uri", "moduleName", "flowName"
                , "componentName", "action", "detail", "message", "exceptionClass"
                , "eventId", "relatedEventId", "eventAsString", 12345L);


            List<ErrorOccurrence> errorOccurrences = new ArrayList<>();
            errorOccurrences.add(event);


            solrErrorReportingManagementService.save(errorOccurrences);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

        }
    }

    @Test
    @DirtiesContext
    public void test_save_batch_insert_success() throws Exception {
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

            SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            BatchInsert<ErrorOccurrence> batchInsert
                = new SolrErrorReportingServiceImpl(dao);

            SolrErrorOccurrence event = new SolrErrorOccurrence("uri", "moduleName", "flowName"
                , "componentName", "action", "detail", "message", "exceptionClass"
                , "eventId", "relatedEventId", "eventAsString", 12345L);


            List<ErrorOccurrence> errorOccurrences = new ArrayList<>();
            errorOccurrences.add(event);


            batchInsert.insert(errorOccurrences);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

        }
    }




    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
