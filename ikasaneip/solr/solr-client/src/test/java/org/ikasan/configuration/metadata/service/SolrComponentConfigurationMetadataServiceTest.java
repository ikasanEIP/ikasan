package org.ikasan.configuration.metadata.service;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.configuration.metadata.dao.SolrComponentConfigurationMetadataDao;
import org.ikasan.configuration.metadata.model.SolrConfigurationMetaData;
import org.ikasan.configuration.metadata.model.SolrConfigurationParameterMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.persistence.BatchInsert;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SolrComponentConfigurationMetadataServiceTest extends SolrTestCaseJ4
{
    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_exception_null_dao()
    {
        new SolrComponentConfigurationMetadataServiceImpl(null);
    }

    @Test
    @DirtiesContext
    public void test_save_component_metadata_list() throws Exception {
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

            SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            BatchInsert<ConfigurationMetaData> batchInsert = new SolrComponentConfigurationMetadataServiceImpl(dao);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);
            dao.save(solrConfigurationMetaData);

            batchInsert.insert(solrConfigurationMetaData);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id() throws Exception {
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

            SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrComponentConfigurationMetadataServiceImpl solrComponentConfigurationMetadataService
                = new SolrComponentConfigurationMetadataServiceImpl(dao);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);
            dao.save(solrConfigurationMetaData);

            solrComponentConfigurationMetadataService.insert(solrConfigurationMetaData);

            ConfigurationMetaData result = solrComponentConfigurationMetadataService.findById("configurationId");

            Assert.assertEquals(event.getConfigurationId(), result.getConfigurationId());
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id_list() throws Exception {
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

            SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrComponentConfigurationMetadataServiceImpl solrComponentConfigurationMetadataService
                = new SolrComponentConfigurationMetadataServiceImpl(dao);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);
            dao.save(solrConfigurationMetaData);

            solrComponentConfigurationMetadataService.insert(solrConfigurationMetaData);

            List<ConfigurationMetaData> result = solrComponentConfigurationMetadataService.findByIdList(List.of("configurationId"));

            Assert.assertEquals(1, result.size());

            result = solrComponentConfigurationMetadataService.findByIdList(List.of("bad id"));

            Assert.assertEquals(0, result.size());
        }
    }

    @Test
    @DirtiesContext
    public void test_find_all() throws Exception {
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

            SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            SolrComponentConfigurationMetadataServiceImpl solrComponentConfigurationMetadataService
                = new SolrComponentConfigurationMetadataServiceImpl(dao);

            SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
                = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
            List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
            solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

            SolrConfigurationMetaData event = new SolrConfigurationMetaData("configurationId", solrConfigurationParameterMetaDataList,
                "description", "implementingClass");

            List<ConfigurationMetaData> solrConfigurationMetaData = new ArrayList<>();
            solrConfigurationMetaData.add(event);
            dao.save(solrConfigurationMetaData);

            solrComponentConfigurationMetadataService.insert(solrConfigurationMetaData);

            List<ConfigurationMetaData> result = solrComponentConfigurationMetadataService.findAll();

            Assert.assertEquals(1, result.size());
        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
