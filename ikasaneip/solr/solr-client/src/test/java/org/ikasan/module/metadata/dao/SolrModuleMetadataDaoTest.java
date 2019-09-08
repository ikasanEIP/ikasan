package org.ikasan.module.metadata.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.configuration.metadata.dao.SolrComponentConfigurationMetadataDao;
import org.ikasan.configuration.metadata.model.SolrConfigurationMetaData;
import org.ikasan.configuration.metadata.model.SolrConfigurationParameterMetaData;
import org.ikasan.module.metadata.model.SolrFlowElementMetaDataImpl;
import org.ikasan.module.metadata.model.SolrFlowMetaDataImpl;
import org.ikasan.module.metadata.model.SolrModuleMetaDataImpl;
import org.ikasan.module.metadata.model.SolrTransitionImpl;
import org.ikasan.spec.metadata.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SolrModuleMetadataDaoTest extends SolrTestCaseJ4
{
    public static final String MODULE_RESULT_JSON = "/data/module.json";

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

            SolrModuleMetadataDao dao = new SolrModuleMetadataDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            ObjectMapper objectMapper = new ObjectMapper();

            SimpleModule m = new SimpleModule();
            m.addAbstractTypeMapping(ModuleMetaData.class, SolrModuleMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowMetaData.class, SolrFlowMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowElementMetaData.class, SolrFlowElementMetaDataImpl.class);
            m.addAbstractTypeMapping(Transition.class, SolrTransitionImpl.class);

            objectMapper.registerModule(m);

            ModuleMetaData solrConfigurationMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);

            List<ModuleMetaData> moduleMetaData = new ArrayList<>();
            moduleMetaData.add(solrConfigurationMetaData);

            dao.save(moduleMetaData);

            // do twice to make sure we are removing and updating.
            dao.save(moduleMetaData);

            assertEquals(2, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(2, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            server.close();
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

            SolrModuleMetadataDao dao = new SolrModuleMetadataDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            ObjectMapper objectMapper = new ObjectMapper();

            SimpleModule m = new SimpleModule();
            m.addAbstractTypeMapping(ModuleMetaData.class, SolrModuleMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowMetaData.class, SolrFlowMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowElementMetaData.class, SolrFlowElementMetaDataImpl.class);
            m.addAbstractTypeMapping(Transition.class, SolrTransitionImpl.class);

            objectMapper.registerModule(m);

            ModuleMetaData solrConfigurationMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);

            List<ModuleMetaData> moduleMetaDataList = new ArrayList<>();
            moduleMetaDataList.add(solrConfigurationMetaData);

            dao.save(moduleMetaDataList);

            ModuleMetaData moduleMetaData = dao.findById("module name");

            Assert.assertEquals("name equals","module name", moduleMetaData.getName());
            Assert.assertEquals("6 flows", 6, moduleMetaData.getFlows().size());

            server.close();
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

            SolrModuleMetadataDao dao = new SolrModuleMetadataDao();
            dao.setSolrClient(server);
            dao.setDaysToKeep(0);

            ObjectMapper objectMapper = new ObjectMapper();

            SimpleModule m = new SimpleModule();
            m.addAbstractTypeMapping(ModuleMetaData.class, SolrModuleMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowMetaData.class, SolrFlowMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowElementMetaData.class, SolrFlowElementMetaDataImpl.class);
            m.addAbstractTypeMapping(Transition.class, SolrTransitionImpl.class);

            objectMapper.registerModule(m);

            ModuleMetaData solrConfigurationMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);

            List<ModuleMetaData> moduleMetaDataList = new ArrayList<>();
            moduleMetaDataList.add(solrConfigurationMetaData);

            dao.save(moduleMetaDataList);

            List<ModuleMetaData> moduleMetaData = dao.findAll();

            Assert.assertEquals("Number of results 2",2, moduleMetaData.size());
            Assert.assertEquals("name equals","module name", moduleMetaData.get(0).getName());
            Assert.assertEquals("6 flows", 6, moduleMetaData.get(0).getFlows().size());

            server.close();
        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
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
}
