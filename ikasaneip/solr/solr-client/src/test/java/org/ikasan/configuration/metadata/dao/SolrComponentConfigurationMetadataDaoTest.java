package org.ikasan.configuration.metadata.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.configuration.metadata.model.SolrConfigurationMetaData;
import org.ikasan.configuration.metadata.model.SolrConfigurationParameterMetaData;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.ikasan.spec.solr.SolrDaoBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SolrComponentConfigurationMetadataDaoTest extends SolrTestCaseJ4
{
    private NodeConfig config;

    private SolrComponentConfigurationMetadataDao dao;

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

        dao = new SolrComponentConfigurationMetadataDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);
    }

    @Test
    @DirtiesContext
    public void test_save_component_metadata_list() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);
            dao.save(solrConfigurationMetaData);

            dao.save(solrConfigurationMetaData);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            server.close();
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);

            dao.save(solrConfigurationMetaData);

            SolrConfigurationMetaData configurationMetaData = (SolrConfigurationMetaData)dao.findById("configurationId");

            Assert.assertEquals("id equals","configurationId", configurationMetaData.getConfigurationId());
            Assert.assertEquals("description equals","description", configurationMetaData.getDescription());
            Assert.assertEquals("implementingClass equals","implementingClass", configurationMetaData.getImplementingClass());
            Assert.assertEquals("1 configuration parameter", 1, configurationMetaData.getParameters().size());

            server.close();
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id_not_found() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);

            dao.save(solrConfigurationMetaData);

            SolrConfigurationMetaData configurationMetaData = (SolrConfigurationMetaData)dao.findById("bad id");

            Assert.assertEquals(null, configurationMetaData);
        }
    }

    @Test
    @DirtiesContext
    public void test_find_all() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);

            dao.save(solrConfigurationMetaData);

            List<ConfigurationMetaData> configurationMetaData = dao.findAll();

            Assert.assertEquals("id equals","configurationId", configurationMetaData.get(0).getConfigurationId());
            Assert.assertEquals("description equals","description", configurationMetaData.get(0).getDescription());
            Assert.assertEquals("implementingClass equals","implementingClass", configurationMetaData.get(0).getImplementingClass());
            Assert.assertEquals("1 configuration parameter", 1, ((List<ConfigurationParameterMetaData>)configurationMetaData.get(0).getParameters()).size());

            server.close();
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_ids() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);

            dao.save(solrConfigurationMetaData);

            List<String> ids = new ArrayList<>();
            ids.add("configurationId");

            List<ConfigurationMetaData> configurationMetaData = dao.findInIdList(ids);

            Assert.assertEquals("id equals","configurationId", configurationMetaData.get(0).getConfigurationId());
            Assert.assertEquals("description equals","description", configurationMetaData.get(0).getDescription());
            Assert.assertEquals("implementingClass equals","implementingClass", configurationMetaData.get(0).getImplementingClass());
            Assert.assertEquals("1 configuration parameter", 1, ((List<ConfigurationParameterMetaData>)configurationMetaData.get(0).getParameters()).size());
        }
    }

    @Test
    public void test_convert_entity_to_solr_input_document() {

        SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
            = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
        List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
        solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

        SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
            "description", "implementingClass");

        SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao();
        SolrInputDocument solrInputDocument = dao.convertEntityToSolrInputDocument(1L, event);

        String metadata;
        try
        {
            metadata = new ObjectMapper().writeValueAsString(event);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException("Unable to convert ["+event+"] to json format.");
        }
        Assert.assertEquals("configurationId", solrInputDocument.getFieldValue(SolrDaoBase.ID));
        Assert.assertEquals("componentConfiguration", solrInputDocument.getFieldValue(SolrDaoBase.TYPE));
        Assert.assertEquals(metadata, solrInputDocument.getFieldValue(SolrDaoBase.PAYLOAD_CONTENT));
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
