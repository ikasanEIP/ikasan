package org.ikasan.business.stream.metadata.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.business.stream.metadata.model.SolrBusinessStream;
import org.ikasan.configuration.metadata.dao.SolrComponentConfigurationMetadataDao;
import org.ikasan.configuration.metadata.model.SolrConfigurationMetaData;
import org.ikasan.configuration.metadata.model.SolrConfigurationParameterMetaData;
import org.ikasan.module.metadata.model.SolrFlowElementMetaDataImpl;
import org.ikasan.module.metadata.model.SolrFlowMetaDataImpl;
import org.ikasan.module.metadata.model.SolrModuleMetaDataImpl;
import org.ikasan.module.metadata.model.SolrTransitionImpl;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.solr.SolrDaoBase;
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

public class SolrBusinessStreamMetadataDaoTest extends SolrTestCaseJ4
{
    public static final String BUSINESS_STREAM_JSON = "/data/businessStream.json";

    private SolrBusinessStreamMetadataDao dao;

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

            SolrBusinessStream solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream");
            solrBusinessStream.setName("businessStream");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            dao.save(solrBusinessStream);

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());
        }
    }

    @Test
    @DirtiesContext
    public void test_save_and_delete() throws Exception {

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

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

            dao.delete(solrBusinessStream.getId());

            assertEquals(null, dao.findById(solrBusinessStream.getId()));
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

            BusinessStreamMetaData businessStreamMetaData = dao.findById("businessStream-businessStream");

            Assert.assertEquals("name equals","businessStream", businessStreamMetaData.getName());
            Assert.assertEquals("meta data equals", businessStream, businessStreamMetaData.getJson());
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id_no_result() throws Exception {

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

            BusinessStreamMetaData businessStreamMetaData = dao.findById("bad id");

            Assert.assertEquals(null, businessStreamMetaData);
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

            List<BusinessStreamMetaData> BusinessStreamMetaData = dao.findAll(0, 10);

            Assert.assertEquals("Number of results 1",1, BusinessStreamMetaData.size());
            Assert.assertEquals("name equals","businessStream", BusinessStreamMetaData.get(0).getName());
            Assert.assertEquals("meta data equals", businessStream, BusinessStreamMetaData.get(0).getJson());
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

            String businessStream = loadDataFile(BUSINESS_STREAM_JSON);

            List<SolrBusinessStream> businessStreams = new ArrayList<>();

            SolrBusinessStream solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream1");
            solrBusinessStream.setName("businessStream1");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);

            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream2");
            solrBusinessStream.setName("businessStream2");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);


            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream3");
            solrBusinessStream.setName("businessStream3");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);


            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream4");
            solrBusinessStream.setName("businessStream4");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);

            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream5");
            solrBusinessStream.setName("blah");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);

            dao.save(businessStreams);

            List<String> businessStreamNames = new ArrayList<>();
            businessStreamNames.add("businessStr*");

            BusinessStreamMetadataSearchResults businessStreamMetaData = dao.find(businessStreamNames,0, 3);

            Assert.assertEquals("Number of results 3",3, businessStreamMetaData.getResultList().size());
            Assert.assertEquals("Number of results total 4",4, businessStreamMetaData.getTotalNumberOfResults());

            businessStreamNames = new ArrayList<>();
            businessStreamNames.add("bla*");

            businessStreamMetaData = dao.find(businessStreamNames,0, 10);

            Assert.assertEquals("Number of results 1",1, businessStreamMetaData.getResultList().size());
            Assert.assertEquals("Number of results total 1",1, businessStreamMetaData.getTotalNumberOfResults());
        }
    }

    @Test
    @DirtiesContext
    public void test_find_all_size_5() throws Exception {

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

            List<SolrBusinessStream> businessStreams = new ArrayList<>();

            SolrBusinessStream solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream1");
            solrBusinessStream.setName("businessStream1");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);

            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream2");
            solrBusinessStream.setName("businessStream2");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);


            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream3");
            solrBusinessStream.setName("businessStream3");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);


            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream4");
            solrBusinessStream.setName("businessStream4");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);

            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream5");
            solrBusinessStream.setName("businessStream5");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            businessStreams.add(solrBusinessStream);

            dao.save(businessStreams);

            List<BusinessStreamMetaData> businessStreamMetaData = dao.findAll(0, 5);

            Assert.assertEquals("Number of results 5",5, businessStreamMetaData.size());
            Assert.assertEquals("name equals","businessStream1", businessStreamMetaData.get(0).getName());
            Assert.assertEquals("meta data equals", businessStream, businessStreamMetaData.get(0).getJson());
        }
    }

    @Test
    public void test_convert_entity_to_solr_input_document() throws IOException {

        SolrConfigurationParameterMetaData solrConfigurationParameterMetaData
            = new SolrConfigurationParameterMetaData(12345L, "name", "value", "description", "implementingClass");
        List<SolrConfigurationParameterMetaData> solrConfigurationParameterMetaDataList = new ArrayList<>();
        solrConfigurationParameterMetaDataList.add(solrConfigurationParameterMetaData);

        String businessStream = loadDataFile(BUSINESS_STREAM_JSON);

        SolrBusinessStream solrBusinessStream = new SolrBusinessStream();
        solrBusinessStream.setId("businessStream1");
        solrBusinessStream.setName("businessStream1");
        solrBusinessStream.setDescription("businessStream1Description");
        solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

        SolrBusinessStreamMetadataDao dao = new SolrBusinessStreamMetadataDao();
        SolrInputDocument solrInputDocument = dao.convertEntityToSolrInputDocument(1L, solrBusinessStream);

        Assert.assertEquals("businessStream-businessStream1", solrInputDocument.getFieldValue(SolrDaoBase.ID));
        Assert.assertEquals("businessStreamMetaData", solrInputDocument.getFieldValue(SolrDaoBase.TYPE));
        Assert.assertEquals("businessStream1", solrInputDocument.getFieldValue(SolrDaoBase.MODULE_NAME));
        Assert.assertEquals("businessStream1Description", solrInputDocument.getFieldValue(SolrDaoBase.FLOW_NAME));
        Assert.assertEquals(businessStream, solrInputDocument.getFieldValue(SolrDaoBase.PAYLOAD_CONTENT));
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
