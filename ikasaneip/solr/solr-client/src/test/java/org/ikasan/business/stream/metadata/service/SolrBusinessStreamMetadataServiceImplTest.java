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
import org.ikasan.business.stream.metadata.model.BusinessStream;
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

            assertEquals(1, server.query(new SolrQuery("*:*")).getResults().getNumFound());
            assertEquals(1, server.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());
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

            BusinessStreamMetaData<BusinessStream> businessStreamMetaData = solrBusinessStreamMetaDataService.findById("businessStream-businessStream");

            Assert.assertEquals("name equals","businessStream", businessStreamMetaData.getName());
            Assert.assertEquals("meta data equals", businessStream, businessStreamMetaData.getJson());

            BusinessStream businessStreamModel = businessStreamMetaData.getBusinessStream();

            Assert.assertEquals("Number of flows is 17",17, businessStreamModel.getFlows().size());
            Assert.assertEquals("Flow Id equals","tradeSystem1-trade.tradeSystem1 FIX Messages Consumer Flow", businessStreamModel.getFlows().get(0).getId());
            Assert.assertEquals("Module name equals","tradeSystem1-trade", businessStreamModel.getFlows().get(0).getModuleName());
            Assert.assertEquals("Flow name equals","tradeSystem1 FIX Messages Consumer Flow", businessStreamModel.getFlows().get(0).getFlowName());
            Assert.assertEquals("Flow x equals",Integer.valueOf(0), businessStreamModel.getFlows().get(0).getX());
            Assert.assertEquals("Flow y equals", Integer.valueOf(-400), businessStreamModel.getFlows().get(0).getY());
            Assert.assertEquals("Correlator type equals", "xpath", businessStreamModel.getFlows().get(0).getCorrelator().getType());
            Assert.assertEquals("Correlator query equals", "/messagingTrade/tradeDetails/tradeIdentifiers/bloombergTicketNumber/text()", businessStreamModel.getFlows().get(0).getCorrelator().getQuery());

            Assert.assertEquals("Number of edges is 27",27, businessStreamModel.getEdges().size());
            Assert.assertEquals("Edge from equals","tradeSystem1", businessStreamModel.getEdges().get(0).getFrom());
            Assert.assertEquals("Edge to equals","tradeSystem1-trade.tradeSystem1 FIX Messages Consumer Flow", businessStreamModel.getEdges().get(0).getTo());

            Assert.assertEquals("Number of integrated systems is 8",8, businessStreamModel.getIntegratedSystems().size());
            Assert.assertEquals("Integrated system id equals","tradeSystem1", businessStreamModel.getIntegratedSystems().get(0).getId());
            Assert.assertEquals("Integrated system name equals","tradeSystem1", businessStreamModel.getIntegratedSystems().get(0).getName());
            Assert.assertEquals("Integrated system x equals",Integer.valueOf(0), businessStreamModel.getIntegratedSystems().get(0).getX());
            Assert.assertEquals("Integrated system y equals", Integer.valueOf(-600), businessStreamModel.getIntegratedSystems().get(0).getY());

            Assert.assertEquals("Number of destinations is 3",3, businessStreamModel.getDestinations().size());
            Assert.assertEquals("Destination id equals","bdm.acme.trade.messaging.prebook", businessStreamModel.getDestinations().get(0).getId());
            Assert.assertEquals("Destination name equals","bdm.acme.trade.messaging.prebook", businessStreamModel.getDestinations().get(0).getName());
            Assert.assertEquals("Destination x equals",Integer.valueOf(0), businessStreamModel.getDestinations().get(0).getX());
            Assert.assertEquals("Destination y equals", Integer.valueOf(0), businessStreamModel.getDestinations().get(0).getY());
        }
    }

    @Test
    @DirtiesContext
    public void test_find_by_id_and_delete() throws Exception {

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

            this.solrBusinessStreamMetaDataService.delete("businessStream-businessStream");

            Assert.assertEquals(null, solrBusinessStreamMetaDataService.findById("businessStream-businessStream"));
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

            List<BusinessStreamMetaData> businessStreamMetaData = solrBusinessStreamMetaDataService.findAll(0, 10);

            Assert.assertEquals("Number of results 1",1, businessStreamMetaData.size());
            Assert.assertEquals("name equals","businessStream", businessStreamMetaData.get(0).getName());
            Assert.assertEquals("meta data equals", businessStream, businessStreamMetaData.get(0).getJson());
        }
    }

    @Test
    @DirtiesContext
    public void test_find_all_business_stream_name_filter() throws Exception {

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

            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("businessStream2");
            solrBusinessStream.setName("businessStream2");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            dao.save(solrBusinessStream);

            solrBusinessStream = new SolrBusinessStream();
            solrBusinessStream.setId("anotherBusinessStream");
            solrBusinessStream.setName("anotherBusinessStream");
            solrBusinessStream.setRawBusinessStreamMetadata(businessStream);

            dao.save(solrBusinessStream);

            BusinessStreamMetadataSearchResults businessStreamMetadataSearchResults = solrBusinessStreamMetaDataService.find(List.of("anotherBusinessStream"),0, 10);

            Assert.assertEquals("Number of results 1",1, businessStreamMetadataSearchResults.getTotalNumberOfResults());
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
