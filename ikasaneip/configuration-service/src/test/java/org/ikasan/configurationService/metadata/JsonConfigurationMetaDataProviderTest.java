package org.ikasan.configurationService.metadata;

import org.apache.commons.io.IOUtils;
import org.ikasan.configurationService.metadata.components.ConfiguredConsumer;
import org.ikasan.configurationService.metadata.components.ConfiguredProducer;
import org.ikasan.configurationService.metadata.configuration.DummyConfiguration;
import org.ikasan.configurationService.model.*;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonConfigurationMetaDataProviderTest
{

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};
    ConfigurationManagement configurationManagement = mockery.mock(ConfigurationManagement.class);

    JsonConfigurationMetaDataProvider uut;

    @BeforeEach
    void setup() {
        uut = new JsonConfigurationMetaDataProvider(configurationManagement);
    }

    @Test
    void describeComponentsOnFlowWithOneConfigurationElement() throws IOException, JSONException
    {

        ConfiguredResource consumer = new ConfiguredConsumer();

        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        String result = uut.describeConfiguredResources(Arrays.asList(consumer));

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/simpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);
    }

    @Test
    void describeConfiguredResourcesWithOneConfigurationResourceWhenConfigurationDoesNotExist() throws IOException, JSONException
    {

       ConfiguredResource consumer = new ConfiguredConsumer();

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(null));
                exactly(1).of(configurationManagement).createConfiguration(with(any(ConfiguredResource.class)));
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        String result = uut.describeConfiguredResources(Arrays.asList(consumer));

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/simpleConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }


    @Test
    void ddescribeConfiguredResourceWithTwoConfigurationResources() throws IOException, JSONException
    {

        ConfiguredResource producer = new ConfiguredProducer("diffrentId",new DummyConfiguration());

        ConfiguredResource consumer = new ConfiguredConsumer();


        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
                exactly(1).of(configurationManagement).getConfiguration("diffrentId");
                will(returnValue(getConfiguration("diffConfiguredResourceId")));
            }
        });
        String result = uut.describeConfiguredResources(Arrays.asList(consumer,producer));

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/twoConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    void describeConfiguredResourceWhenConfigurationManagementReturnsNull() throws IOException, JSONException
    {

        ConfiguredResource configuredResource = new ConfiguredConsumer();

        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(null));
                exactly(1).of(configurationManagement).createConfiguration(with(any(ConfiguredResource.class)));
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        String result = uut.describeConfiguredResource(configuredResource);

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/flowConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }


    @Test
    void describeConfiguredResource() throws IOException, JSONException
    {

        ConfiguredResource configuredResource = new ConfiguredConsumer();
        mockery.checking(new Expectations()
        {
            {
               
                exactly(1).of(configurationManagement).getConfiguration("CONFIGURATION_ID");
                will(returnValue(getConfiguration("configuredResourceId")));
            }
        });
        String result = uut.describeConfiguredResource(configuredResource);

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile("/data/flowConfigurationMetadata.json"), result, JSONCompareMode.STRICT);

    }

    @Test
    void deserialiseMetadataConfiguration() throws IOException, JSONException
    {

        ConfigurationMetaData<List<ConfigurationParameterMetaData>> result = uut.deserialiseMetadataConfiguration(loadDataFile("/data/flowConfigurationMetadata.json"));

        assertEquals("configuredResourceId" , result.getConfigurationId());
        assertNull(result.getDescription());

        assertEquals("name" , result.getParameters().get(0).getName());
        assertEquals("value" , result.getParameters().get(0).getValue());
        assertEquals("desc" , result.getParameters().get(0).getDescription());
        assertEquals("org.ikasan.configurationService.model.ConfigurationParameterStringImpl" , result.getParameters().get(0).getImplementingClass());

        assertEquals("name" , result.getParameters().get(1).getName());
        assertEquals(10 , result.getParameters().get(1).getValue());
        assertEquals("desc" , result.getParameters().get(1).getDescription());
        assertEquals("org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl" , result.getParameters().get(1).getImplementingClass());

        assertEquals("name" , result.getParameters().get(2).getName());
        assertEquals(10 , result.getParameters().get(2).getValue());
        assertEquals("desc" , result.getParameters().get(2).getDescription());
        assertEquals("org.ikasan.configurationService.model.ConfigurationParameterLongImpl" , result.getParameters().get(2).getImplementingClass());

        assertEquals("name" , result.getParameters().get(3).getName());
        assertEquals(Arrays.asList("one","two","three"), result.getParameters().get(3).getValue());
        assertEquals("desc" , result.getParameters().get(3).getDescription());
        assertEquals("org.ikasan.configurationService.model.ConfigurationParameterListImpl" , result.getParameters().get(3).getImplementingClass());

        assertEquals("name" , result.getParameters().get(4).getName());
        assertEquals(new LinkedHashMap<String, String>(){{put("one","1"); put("two","2");put("three","3");}}, result.getParameters().get(4).getValue());
        assertEquals("desc" , result.getParameters().get(4).getDescription());
        assertEquals("org.ikasan.configurationService.model.ConfigurationParameterMapImpl" , result.getParameters().get(4).getImplementingClass());

    }

    @Test
    void deserialiseMetadataConfigurations() throws IOException, JSONException
    {
        List<ConfigurationMetaData> result = uut
            .deserialiseMetadataConfigurations(loadDataFile("/data/twoConfigurationMetadata.json"));
        assertEquals(2, result.size());
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

}
