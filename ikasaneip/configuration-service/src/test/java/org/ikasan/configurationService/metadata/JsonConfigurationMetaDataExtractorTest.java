package org.ikasan.configurationService.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.ikasan.configurationService.metadata.components.ConfiguredConsumer;
import org.ikasan.configurationService.metadata.components.ConfiguredProducer;
import org.ikasan.configurationService.metadata.components.TestProducer;
import org.ikasan.configurationService.metadata.configuration.DummyConfiguration;
import org.ikasan.configurationService.metadata.flow.TestFlow;
import org.ikasan.configurationService.metadata.flow.TestFlowConfiguration;
import org.ikasan.configurationService.metadata.flow.TestFlowElement;
import org.ikasan.configurationService.metadata.module.TestModule;
import org.ikasan.configurationService.model.*;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JsonConfigurationMetaDataExtractorTest
{

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};
    private ObjectMapper mapper;

    ConfigurationManagement configurationManagement = mockery.mock(ConfigurationManagement.class);

    JsonConfigurationMetaDataExtractor uut;

    @Before
    public void setup() {

        uut = new JsonConfigurationMetaDataExtractor(configurationManagement);

        mapper = new ObjectMapper();

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(ConfigurationParameterMetaData.class,ConfigurationParameterMetaDataImpl.class);
        m.addAbstractTypeMapping(ConfigurationMetaData.class,ConfigurationMetaDataImpl.class);
        this.mapper.registerModule(m);
    }

    @Test
    public void getComponentsConfigurationOnFlowWithOneConfigurationElement() throws IOException, JSONException
    {

        Flow flow = createSimpleFlow();

        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getComponentsConfiguration(flow);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/simpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);
    }

    @Test
    public void getComponentsConfigurationOnFlowWithOneConfigurationElementWhenConfigurationDoesNotExist() throws IOException, JSONException
    {

        Flow flow = createSimpleFlow();

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(null));
                exactly(1).of(configurationManagement).createConfiguration(with(any(ConfiguredResource.class)));
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getComponentsConfiguration(flow);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/simpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }


    @Test
    public void getComponentsConfigurationOnFlowWithTwoConfigurationElementWithSameId() throws IOException, JSONException
    {

        FlowElement producer = new TestFlowElement(new ConfiguredProducer(), "Test Producer"
            , "Test Producer Description",null);

        TestFlowElement consumer = new TestFlowElement(new ConfiguredConsumer(), "Test Consumer",
            "Test Consumer Description", new DummyConfiguration());

        Flow flow = new TestFlow("Flow Name", "Module Name",
            Arrays.asList(consumer,producer));

        mockery.checking(new Expectations()
        {
            {

                exactly(2).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getComponentsConfiguration(flow);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/simpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    public void getComponentsConfigurationOnFlowWithTwoConfigurationElementWithDifferentId() throws IOException, JSONException
    {

        FlowElement producer = new TestFlowElement(new ConfiguredProducer("diffrentId",new DummyConfiguration()), "Test Producer"
            , "Test Producer Description",null);

        TestFlowElement consumer = new TestFlowElement(new ConfiguredConsumer(), "Test Consumer",
            "Test Consumer Description", null);

        Flow flow = new TestFlow("Flow Name", "Module Name",
            Arrays.asList(consumer,producer));

        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
                exactly(1).of(configurationManagement).getConfiguration("diffrentId");
                will(returnValue(getConfiguration("diffConfiguredResourceId")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getComponentsConfiguration(flow);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/twoConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    public void getComponentsConfigurationOnModuleWithOneConfigurationElement() throws IOException, JSONException
    {

        Flow flow = createSimpleFlow();
        TestModule testModule = new TestModule();
        testModule.getFlows().add(flow);

        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getComponentsConfiguration(testModule);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/simpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    public void getComponentsConfigurationOnModuleWithOneConfigurationElementWhenConfigurationManagmentReturnsNull() throws IOException, JSONException
    {

        Flow flow = createSimpleFlow();
        TestModule testModule = new TestModule();
        testModule.getFlows().add(flow);

        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(null));
                exactly(1).of(configurationManagement).createConfiguration(with(any(ConfiguredResource.class)));
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getComponentsConfiguration(testModule);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/simpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    public void getInvokersConfigurationOnFlowWithOneConfigurationElement() throws IOException, JSONException
    {

        Flow flow = createSimpleFlow();

        mockery.checking(new Expectations()
        {
            {
               
                exactly(2).of(configurationManagement).getConfiguration("FLOW_INVOKER_CONFIGURATION_ID");
                will(returnValue(getConfiguration("FLOW_INVOKER_CONFIGURATION_ID")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getInvokersConfiguration(flow);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/invokerSimpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    public void getInvokersConfigurationOnModuleWithOneConfigurationElement() throws IOException, JSONException
    {

        Flow flow = createSimpleFlow();
        TestModule testModule = new TestModule();
        testModule.getFlows().add(flow);

        mockery.checking(new Expectations()
        {
            {
               
                exactly(2).of(configurationManagement).getConfiguration("FLOW_INVOKER_CONFIGURATION_ID");
                will(returnValue(getConfiguration("FLOW_INVOKER_CONFIGURATION_ID")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getInvokersConfiguration(testModule);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/invokerSimpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    public void getFlowConfigurationWhenFlowDoesHaveConfiguration() throws IOException, JSONException
    {

        Flow flow = createSimpleFlow();

        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("FLOW_CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        ConfigurationMetaData resultConf = uut.getFlowConfiguration(flow);

        String result = describeConfiguredResource(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/flowDefaultConfigurationMetadata.json"), result, JSONCompareMode.STRICT);
    }

    @Test
    public void getFlowConfigurationOnModuleWithFlowDoesHaveConfiguration() throws IOException, JSONException
    {

        Flow flow = createSimpleFlow();
        TestModule testModule = new TestModule();
        testModule.getFlows().add(flow);

        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("FLOW_CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        List<ConfigurationMetaData> resultConf = uut.getFlowsConfiguration(testModule);

        String result = describeConfiguredResources(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/flowSimpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    public void getIndividualConfiguredResourceConfiguration() throws IOException, JSONException
    {
        Flow flow = createSimpleFlow();
        FlowElement flowElement = flow.getFlowElement("Test Consumer");

        mockery.checking(new Expectations()
        {
            {

                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        ConfigurationMetaData resultConf = uut.getConfiguration((ConfiguredResource)flowElement.getFlowComponent());

        String result = describeConfiguredResource(resultConf);
        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/individual-component-configuration.json"), result, JSONCompareMode.STRICT);
    }

        private Flow createSimpleFlow()
    {
        FlowElement producer = new TestFlowElement(new TestProducer(), "Test Producer"
            , "Test Producer Description",null);

        TestFlowElement consumer = new TestFlowElement(new ConfiguredConsumer(), "Test Consumer",
            "Test Consumer Description",  new DummyConfiguration());

        Flow flow = new TestFlow("Flow Name", "Module Name",
            Arrays.asList(consumer,producer));
        ((TestFlow) flow).setConfiguration(new TestFlowConfiguration(consumer));

        return flow;
    }

    private Configuration getConfiguration(String configuredResourceId){
        Configuration<List<ConfigurationParameter>> configuration =
            new DefaultConfiguration(configuredResourceId, new ArrayList<ConfigurationParameter>());
        configuration.getParameters().add( new ConfigurationParameterStringImpl("name", "value", "desc"));
        configuration.getParameters().add( new ConfigurationParameterIntegerImpl("name", Integer.valueOf(10), "desc"));
        configuration.getParameters().add( new ConfigurationParameterLongImpl("name", Long.valueOf(10), "desc"));

        List<String> listVals = new ArrayList<String>();
        listVals.add("one");
        listVals.add("two");
        listVals.add("three");
        configuration.getParameters().add( new ConfigurationParameterListImpl("name", listVals, "desc"));

        Map<String,String> mapVals = new HashMap<String,String>();
        mapVals.put("one", "1");
        mapVals.put("two", "2");
        mapVals.put("three", "3");
        configuration.getParameters().add( new ConfigurationParameterMapImpl("name", mapVals, "desc"));

        return configuration;
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


    private String describeConfiguredResources(List<ConfigurationMetaData> metadataConfigurations)
    {
        try
        {
            return this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadataConfigurations);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred invoker configuration meta data json!", e);
        }
    }

    private String describeConfiguredResource(ConfigurationMetaData metadataConfiguration){

        try
        {
            return this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadataConfiguration);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred invoker configuration meta data json!", e);
        }
    }

}
