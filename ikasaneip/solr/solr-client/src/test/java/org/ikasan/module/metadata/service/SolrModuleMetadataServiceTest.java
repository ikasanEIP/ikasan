package org.ikasan.module.metadata.service;

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
import org.ikasan.configuration.metadata.service.SolrComponentConfigurationMetadataServiceImpl;
import org.ikasan.module.metadata.dao.SolrModuleMetadataDao;
import org.ikasan.module.metadata.model.SolrFlowElementMetaDataImpl;
import org.ikasan.module.metadata.model.SolrFlowMetaDataImpl;
import org.ikasan.module.metadata.model.SolrModuleMetaDataImpl;
import org.ikasan.module.metadata.model.SolrTransitionImpl;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.persistence.BatchInsert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SolrModuleMetadataServiceTest extends SolrTestCaseJ4
{
    public static final String MODULE_RESULT_JSON = "/data/module.json";

    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_exception_null_dao()
    {
        new SolrModuleMetadataServiceImpl(null);
    }

    @Test
    @DirtiesContext
    public void test_save_module_metadata_list() throws Exception {
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

            BatchInsert<ModuleMetaData> batchInsert = new SolrModuleMetadataServiceImpl(dao);

            batchInsert.insert(moduleMetaDataList);

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
