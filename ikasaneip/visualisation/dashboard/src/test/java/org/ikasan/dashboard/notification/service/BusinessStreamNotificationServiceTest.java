package org.ikasan.dashboard.notification.service;

import org.apache.commons.io.IOUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.business.stream.metadata.dao.SolrBusinessStreamMetadataDao;
import org.ikasan.business.stream.metadata.service.SolrBusinessStreamMetaDataServiceImpl;
import org.ikasan.dashboard.notification.model.BusinessStreamExclusions;
import org.ikasan.dashboard.notification.service.BusinessStreamNotificationService;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.solr.dao.SolrGeneralDaoImpl;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class BusinessStreamNotificationServiceTest extends SolrTestCaseJ4 {

    public static final String BUSINESS_STREAM_PAYLOAD = "/data/graph/wriggle3.json";

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
    public void test_business_stream_no_exclusions() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            this.initialiseDataBusinessStream(server);

            BusinessStreamNotificationService businessStreamNotificationService = this.initialiseService(server);
            Optional<BusinessStreamExclusions> businessStreamExclusions
                = businessStreamNotificationService.getBusinessStreamExclusions("wriggle", 0L, 100);

            Assert.assertFalse("Business Stream Exclusions not found!", businessStreamExclusions.isPresent());
        }
    }

    @Test
    @DirtiesContext
    public void test_business_stream_exclusions() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            this.initialiseDataBusinessStream(server);
            this.initialiseDataExclusionsAndErrors(server);

            BusinessStreamNotificationService businessStreamNotificationService = this.initialiseService(server);
            Optional<BusinessStreamExclusions> businessStreamExclusions
                = businessStreamNotificationService.getBusinessStreamExclusions("wriggle", 0L, 100);

            Assert.assertTrue("Business Stream Exclusions found!", businessStreamExclusions.isPresent());
            Assert.assertEquals("Exclusions found!", 1, businessStreamExclusions.get().getBusinessStreamExclusions().size());
        }
    }

    @Test
    @DirtiesContext
    public void test_business_stream_exclusion_no_error() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            this.initialiseDataBusinessStream(server);
            this.initialiseDataExclusionNoError(server);

            BusinessStreamNotificationService businessStreamNotificationService = this.initialiseService(server);
            Optional<BusinessStreamExclusions> businessStreamExclusions
                = businessStreamNotificationService.getBusinessStreamExclusions("wriggle", 0L, 100);

            Assert.assertTrue("Business Stream Exclusions found!", businessStreamExclusions.isPresent());
            Assert.assertEquals("Exclusions found!", 1, businessStreamExclusions.get().getBusinessStreamExclusions().size());
        }
    }

    @Test
    @DirtiesContext
    public void test_no_business_stream_found() throws Exception {


        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            this.initialiseDataBusinessStream(server);

            BusinessStreamNotificationService businessStreamNotificationService = this.initialiseService(server);
            Optional<BusinessStreamExclusions> businessStreamExclusions = businessStreamNotificationService
                .getBusinessStreamExclusions("bad business stream", 0L, 100);

            Assert.assertFalse("Business Stream Exclusions not found!", businessStreamExclusions.isPresent());
        }
    }

    private BusinessStreamNotificationService initialiseService(EmbeddedSolrServer server) {
        SolrGeneralDaoImpl solrGeneralDao = new SolrGeneralDaoImpl();
        solrGeneralDao.setSolrClient(server);

        SolrBusinessStreamMetadataDao solrBusinessStreamMetadataDao = new SolrBusinessStreamMetadataDao();
        solrBusinessStreamMetadataDao.setSolrClient(server);

        SolrErrorReportingServiceDao solrErrorReportingServiceDao = new SolrErrorReportingServiceDao();
        solrErrorReportingServiceDao.setSolrClient(server);

        SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(solrGeneralDao);
        SolrBusinessStreamMetaDataServiceImpl solrBusinessStreamMetaDataService
            = new SolrBusinessStreamMetaDataServiceImpl(solrBusinessStreamMetadataDao);
        SolrErrorReportingManagementServiceImpl solrErrorReportingManagementService
            = new SolrErrorReportingManagementServiceImpl(solrErrorReportingServiceDao);

        return new BusinessStreamNotificationService(solrBusinessStreamMetaDataService, solrErrorReportingManagementService,
            solrGeneralService);
    }

    private void initialiseDataBusinessStream(EmbeddedSolrServer server) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "businessStream-wriggle");
        doc.addField("type", "businessStreamMetaData");
        doc.addField("moduleName", "wriggle");
        doc.addField("payload", this.loadDataFile(BUSINESS_STREAM_PAYLOAD));
        doc.addField("expiry", System.currentTimeMillis() + 10000000l);
        server.add("ikasan", doc);
        server.commit();
    }

    private void initialiseDataExclusionsAndErrors(EmbeddedSolrServer server) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "error-1");
        doc.addField("type", "error");
        doc.addField("moduleName", "wriggle-im");
        doc.addField("errorUri", "1234");
        doc.addField("flowName", "Wriggle Customer HTTP Request Flow");
        doc.addField("payload", "this is the error payload");
        doc.addField("expiry", System.currentTimeMillis() + 10000000l);
        doc.addField("timestamp", System.currentTimeMillis() - 10000000l);
        server.add("ikasan", doc);

        doc = new SolrInputDocument();
        doc.addField("id", "1234");
        doc.addField("type", "exclusion");
        doc.addField("moduleName", "wriggle-im");
        doc.addField("flowName", "Wriggle Customer HTTP Request Flow");
        doc.addField("payload", "this is the exclusion payload");
        doc.addField("expiry", System.currentTimeMillis() + 10000000l);
        doc.addField("timestamp", System.currentTimeMillis() - 10000000l);
        server.add("ikasan", doc);

        server.commit();
    }

    private void initialiseDataExclusionNoError(EmbeddedSolrServer server) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "exclusion-1");
        doc.addField("type", "exclusion");
        doc.addField("moduleName", "wriggle-im");
        doc.addField("flowName", "Wriggle Customer HTTP Request Flow");
        doc.addField("payload", "this is the exclusion payload");
        doc.addField("expiry", System.currentTimeMillis() + 10000000l);
        doc.addField("timestamp", System.currentTimeMillis() - 10000000l);
        server.add("ikasan", doc);

        server.commit();
    }


    protected String loadDataFile(String fileName) throws IOException
    {
        String contentToSend = IOUtils.toString(loadDataFileStream(fileName), "UTF-8");

        return contentToSend;
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
