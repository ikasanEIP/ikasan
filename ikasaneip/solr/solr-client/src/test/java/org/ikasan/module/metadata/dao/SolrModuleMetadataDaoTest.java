package org.ikasan.module.metadata.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.module.metadata.model.SolrFlowElementMetaDataImpl;
import org.ikasan.module.metadata.model.SolrFlowMetaDataImpl;
import org.ikasan.module.metadata.model.SolrModuleMetaDataImpl;
import org.ikasan.module.metadata.model.SolrTransitionImpl;
import org.ikasan.spec.metadata.*;
import org.junit.Assert;
import org.junit.Before;
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

    private SolrModuleMetadataDao dao;

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

        dao = new SolrModuleMetadataDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);
    }

    @Test
    @DirtiesContext
    public void test_save_component_metadata_list() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
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

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            ObjectMapper objectMapper = new ObjectMapper();

            SimpleModule m = new SimpleModule();
            m.addAbstractTypeMapping(ModuleMetaData.class, SolrModuleMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowMetaData.class, SolrFlowMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowElementMetaData.class, SolrFlowElementMetaDataImpl.class);
            m.addAbstractTypeMapping(Transition.class, SolrTransitionImpl.class);

            objectMapper.registerModule(m);

            ModuleMetaData moduleMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);

            List<ModuleMetaData> moduleMetaDataList = new ArrayList<>();
            moduleMetaDataList.add(moduleMetaData);

            dao.save(moduleMetaDataList);

            List<ModuleMetaData> moduleMetaDataRes = dao.findAll(0, 10);

            Assert.assertEquals("Number of results 1",1, moduleMetaDataRes.size());
            Assert.assertEquals("name equals","module name", moduleMetaDataRes.get(0).getName());
            Assert.assertEquals("6 flows", 6, moduleMetaDataRes.get(0).getFlows().size());

            server.close();
        }
    }

    @Test
    @DirtiesContext
    public void test_find() throws Exception {

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);
            ObjectMapper objectMapper = new ObjectMapper();

            SimpleModule m = new SimpleModule();
            m.addAbstractTypeMapping(ModuleMetaData.class, SolrModuleMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowMetaData.class, SolrFlowMetaDataImpl.class);
            m.addAbstractTypeMapping(FlowElementMetaData.class, SolrFlowElementMetaDataImpl.class);
            m.addAbstractTypeMapping(Transition.class, SolrTransitionImpl.class);

            objectMapper.registerModule(m);

            List<ModuleMetaData> moduleMetaDataList = new ArrayList<>();

            ModuleMetaData moduleMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);
            moduleMetaData.setName("module1");
            moduleMetaDataList.add(moduleMetaData);

            moduleMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);
            moduleMetaData.setName("module2");
            moduleMetaDataList.add(moduleMetaData);

            moduleMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);
            moduleMetaData.setName("module3");
            moduleMetaDataList.add(moduleMetaData);

            moduleMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);
            moduleMetaData.setName("module4");
            moduleMetaDataList.add(moduleMetaData);

            moduleMetaData = objectMapper.readValue(loadDataFile(MODULE_RESULT_JSON), SolrModuleMetaDataImpl.class);
            moduleMetaData.setName("blah");
            moduleMetaDataList.add(moduleMetaData);

            dao.save(moduleMetaDataList);

            List<String> moduleNames = new ArrayList<>();
            moduleNames.add("modu*");

            ModuleMetadataSearchResults moduleMetaDataRes = dao.find(moduleNames, 0, 3);

            Assert.assertEquals("Number of results 3",3, moduleMetaDataRes.getResultList().size());
            Assert.assertEquals("Number of results total 4",4, moduleMetaDataRes.getTotalNumberOfResults());

            moduleNames = new ArrayList<>();
            moduleNames.add("bla*");

            moduleMetaDataRes = dao.find(moduleNames, 0, 5);

            Assert.assertEquals("Number of results 1",1, moduleMetaDataRes.getResultList().size());
            Assert.assertEquals("Number of results total 1",1, moduleMetaDataRes.getTotalNumberOfResults());

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
