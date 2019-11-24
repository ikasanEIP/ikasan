package org.ikasan.business.stream.metadata.service;

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
import org.ikasan.business.stream.metadata.dao.SolrBusinessStreamMetadataDao;
import org.ikasan.business.stream.metadata.model.BusinessStreamMetaDataImpl;
import org.ikasan.business.stream.metadata.model.SolrBusinessStream;
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
import java.util.List;

public class SolrBusinessStreamMetadataServiceImplTest extends SolrTestCaseJ4
{
    public static final String BUSINESS_STREAM_JSON = "/data/businessStream.json";

    private SolrBusinessStreamMetadataDao dao;
    private SolrBusinessStreamMetaDataServiceImpl solrBusinessStreamMetaDataService;

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

        dao = new SolrBusinessStreamMetadataDao();
        dao.setSolrClient(server);
        dao.setDaysToKeep(0);

        this.solrBusinessStreamMetaDataService = new SolrBusinessStreamMetaDataServiceImpl(dao);
    }

    @Test
    @DirtiesContext
    public void test_save() throws Exception {

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

            String businessStream = loadDataFile(BUSINESS_STREAM_JSON);

            BusinessStreamMetaData solrBusinessStream = new BusinessStreamMetaDataImpl();
            solrBusinessStream.setId("businessStream");
            solrBusinessStream.setName("businessStream");
            solrBusinessStream.setJson(businessStream);

            solrBusinessStreamMetaDataService.save(solrBusinessStream);

            assertEquals(2, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(2, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());
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

            String businessStream = loadDataFile(BUSINESS_STREAM_JSON);

            SolrBusinessStream solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream");
            solrBusinessStream.setName("businessStream");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            dao.save(solrBusinessStream);

            BusinessStreamMetaData businessStreamMetaData = solrBusinessStreamMetaDataService.findById("businessStream-businessStream");

            Assert.assertEquals("name equals","businessStream", businessStreamMetaData.getName());
            Assert.assertEquals("meta data equals", businessStream, businessStreamMetaData.getJson());

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

            String businessStream = loadDataFile(BUSINESS_STREAM_JSON);

            SolrBusinessStream solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream");
            solrBusinessStream.setName("businessStream");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            dao.save(solrBusinessStream);

            List<BusinessStreamMetaData> BusinessStreamMetaData = solrBusinessStreamMetaDataService.findAll();

            Assert.assertEquals("Number of results 2",2, BusinessStreamMetaData.size());
            Assert.assertEquals("name equals","businessStream", BusinessStreamMetaData.get(0).getName());
            Assert.assertEquals("meta data equals", businessStream, BusinessStreamMetaData.get(0).getJson());

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
