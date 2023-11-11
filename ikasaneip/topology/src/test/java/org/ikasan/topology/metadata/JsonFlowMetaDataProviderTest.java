package org.ikasan.topology.metadata;

import org.apache.commons.io.IOUtils;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.spec.trigger.TriggerService;
import org.ikasan.topology.metadata.components.*;
import org.ikasan.topology.metadata.flow.TestFlow;
import org.ikasan.topology.metadata.flow.TestFlowConfiguration;
import org.ikasan.topology.metadata.flow.TestFlowElement;
import org.ikasan.trigger.model.TriggerImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonFlowMetaDataProviderTest
{
    public static final String SIMPLE_FLOW_RESULT_JSON = "/data/simpleFlow.json";
    public static final String MULTI_RECIPIENT_FLOW_RESULT_JSON = "/data/multiRecipientFlow.json";
    public static final String SINGLE_RECIPIENT_FLOW_RESULT_JSON = "/data/singleRecipientFlow.json";
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery();

    TriggerService triggerService = mockery.mock(TriggerService.class);
    Map<String, List<Trigger>> triggers = new HashMap<>();


    @Test
    void test_simple_flow() throws IOException, JSONException
    {
        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        String json = jsonFlowMetaDataProvider.describeFlow(createSimpleFlow(), null);

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile(SIMPLE_FLOW_RESULT_JSON), json, JSONCompareMode.LENIENT);
    }

    @Test
    void test_multi_recipient_flow() throws IOException, JSONException
    {
        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        String json = jsonFlowMetaDataProvider.describeFlow(createMultiRecipientListFlow(), null);

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile(MULTI_RECIPIENT_FLOW_RESULT_JSON), json, JSONCompareMode.LENIENT);
    }

    @Test
    void test_single_recipient_flow() throws IOException, JSONException
    {
        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        String json = jsonFlowMetaDataProvider.describeFlow(createSingleRecipientListFlow(), null);

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile(SINGLE_RECIPIENT_FLOW_RESULT_JSON), json, JSONCompareMode.LENIENT);
    }

    @Test
    void test_single_json_to_object() throws IOException
    {
        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        String json = jsonFlowMetaDataProvider.describeFlow(createMultiRecipientListFlow(), null);

        FlowMetaData flowMetaData = jsonFlowMetaDataProvider.deserialiseFlow(json);

        assertEquals("Flow Name", flowMetaData.getName(), "Flow name equals!");
        assertEquals(StartupType.MANUAL.name(), flowMetaData.getFlowStartupType(), "Flow start up type equals!");
        assertEquals("Test Consumer", flowMetaData.getConsumer().getComponentName(), "Consumer name equals!");
        assertEquals("org.ikasan.spec.component.endpoint.Consumer", flowMetaData.getConsumer().getComponentType(), "Consumer component type equals!");
        assertEquals("CONFIGURATION_ID", flowMetaData.getConsumer().getConfigurationId(), "Consumer configuration id equals!");
        assertEquals(8, flowMetaData.getFlowElements().size(), "Number of flow elements equals!");
        assertEquals(7, flowMetaData.getTransitions().size(), "Number of transitions equals!");

    }

    @Test
    void test_single_json_to_object_disabled_startup_type() throws IOException
    {
        StartupControl startupControl = new StartupControl() {
            @Override
            public String getModuleName() {
                return null;
            }

            @Override
            public String getFlowName() {
                return null;
            }

            @Override
            public StartupType getStartupType() {
                return StartupType.DISABLED;
            }

            @Override
            public void setStartupType(StartupType startupType) {

            }

            @Override
            public boolean isAutomatic() {
                return false;
            }

            @Override
            public boolean isManual() {
                return false;
            }

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public String getComment() {
                return null;
            }

            @Override
            public void setComment(String comment) {

            }
        };

        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        String json = jsonFlowMetaDataProvider.describeFlow(createMultiRecipientListFlow(), startupControl);

        FlowMetaData flowMetaData = jsonFlowMetaDataProvider.deserialiseFlow(json);

        assertEquals("Flow Name", flowMetaData.getName(), "Flow name equals!");
        assertEquals(StartupType.DISABLED.name(), flowMetaData.getFlowStartupType(), "Flow start up type equals!");
        assertEquals("Test Consumer", flowMetaData.getConsumer().getComponentName(), "Consumer name equals!");
        assertEquals("org.ikasan.spec.component.endpoint.Consumer", flowMetaData.getConsumer().getComponentType(), "Consumer component type equals!");
        assertEquals("CONFIGURATION_ID", flowMetaData.getConsumer().getConfigurationId(), "Consumer configuration id equals!");
        assertEquals(8, flowMetaData.getFlowElements().size(), "Number of flow elements equals!");
        assertEquals(7, flowMetaData.getTransitions().size(), "Number of transitions equals!");

    }

    @Test
    void test_single_json_to_object_automatic_startup_type() throws IOException
    {
        StartupControl startupControl = new StartupControl() {
            @Override
            public String getModuleName() {
                return null;
            }

            @Override
            public String getFlowName() {
                return null;
            }

            @Override
            public StartupType getStartupType() {
                return StartupType.AUTOMATIC;
            }

            @Override
            public void setStartupType(StartupType startupType) {

            }

            @Override
            public boolean isAutomatic() {
                return false;
            }

            @Override
            public boolean isManual() {
                return false;
            }

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public String getComment() {
                return null;
            }

            @Override
            public void setComment(String comment) {

            }
        };

        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        String json = jsonFlowMetaDataProvider.describeFlow(createMultiRecipientListFlow(), startupControl);

        FlowMetaData flowMetaData = jsonFlowMetaDataProvider.deserialiseFlow(json);

        assertEquals("Flow Name", flowMetaData.getName(), "Flow name equals!");
        assertEquals(StartupType.AUTOMATIC.name(), flowMetaData.getFlowStartupType(), "Flow start up type equals!");
        assertEquals("Test Consumer", flowMetaData.getConsumer().getComponentName(), "Consumer name equals!");
        assertEquals("org.ikasan.spec.component.endpoint.Consumer", flowMetaData.getConsumer().getComponentType(), "Consumer component type equals!");
        assertEquals("CONFIGURATION_ID", flowMetaData.getConsumer().getConfigurationId(), "Consumer configuration id equals!");
        assertEquals(8, flowMetaData.getFlowElements().size(), "Number of flow elements equals!");
        assertEquals(7, flowMetaData.getTransitions().size(), "Number of transitions equals!");

    }

    @Test
    void test_single_json_to_object_manual_startup_type() throws IOException
    {
        StartupControl startupControl = new StartupControl() {
            @Override
            public String getModuleName() {
                return null;
            }

            @Override
            public String getFlowName() {
                return null;
            }

            @Override
            public StartupType getStartupType() {
                return StartupType.MANUAL;
            }

            @Override
            public void setStartupType(StartupType startupType) {

            }

            @Override
            public boolean isAutomatic() {
                return false;
            }

            @Override
            public boolean isManual() {
                return false;
            }

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public String getComment() {
                return null;
            }

            @Override
            public void setComment(String comment) {

            }
        };

        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        String json = jsonFlowMetaDataProvider.describeFlow(createMultiRecipientListFlow(), startupControl);

        FlowMetaData flowMetaData = jsonFlowMetaDataProvider.deserialiseFlow(json);

        assertEquals("Flow Name", flowMetaData.getName(), "Flow name equals!");
        assertEquals(StartupType.MANUAL.name(), flowMetaData.getFlowStartupType(), "Flow start up type equals!");
        assertEquals("Test Consumer", flowMetaData.getConsumer().getComponentName(), "Consumer name equals!");
        assertEquals("org.ikasan.spec.component.endpoint.Consumer", flowMetaData.getConsumer().getComponentType(), "Consumer component type equals!");
        assertEquals("CONFIGURATION_ID", flowMetaData.getConsumer().getConfigurationId(), "Consumer configuration id equals!");
        assertEquals(8, flowMetaData.getFlowElements().size(), "Number of flow elements equals!");
        assertEquals(7, flowMetaData.getTransitions().size(), "Number of transitions equals!");

    }

    private Flow createSimpleFlow()
    {
        FlowElement producer = new TestFlowElement(new TestProducer(), "Test Producer"
            , "Test Producer Description", null);

        Map<String, FlowElement> transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, producer);

        TestFlowElement converter = new TestFlowElement(new TestConverter(), "Test Converter",
            "Test Converter Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, converter);

        TestFlowElement broker = new TestFlowElement(new TestBroker(),
            "Test Broker", "Test Broker Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, broker);

        TestFlowElement splitter = new TestFlowElement(new TestSplitter(), "Test Splitter",
            "Test Splitter Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, splitter);

        TestFlowElement filter = new TestFlowElement(new TestFilter(), "Test Filter",
            "Test Filter Description", transitions);


        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, filter);

        TestFlowElement consumer = new TestFlowElement(new TestConsumer(), "Test Consumer", "Test Consumer Description", transitions);

        FlowConfiguration flowConfiguration = new TestFlowConfiguration(consumer);

        Flow flow = new TestFlow("Flow Name", "Module Name", flowConfiguration);

        flow.setTriggerService(triggerService);
        Trigger beforeConsumer = new TriggerImpl("Module Name", "Flow Name", TriggerRelationship.BEFORE.getDescription(), "beforeConsumer", "Test Consumer");
        Trigger beforeConsumer2 = new TriggerImpl("Module Name", "Flow Name", TriggerRelationship.BEFORE.getDescription(), "beforeConsumer2", "Test Consumer");
        Trigger afterProducer = new TriggerImpl("Module Name", "Flow Name", TriggerRelationship.AFTER.getDescription(), "afterProducer", "Test Producer");
        Trigger afterProducer2 = new TriggerImpl("Module Name", "Flow Name", TriggerRelationship.AFTER.getDescription(), "afterProducer2", "Test Producer");
        Trigger afterConverter = new TriggerImpl("Module Name", "Flow Name", TriggerRelationship.AFTER.getDescription(), "afterConverter", "Test Converter");
        Trigger beforeConverter = new TriggerImpl("Module Name", "Flow Name", TriggerRelationship.BEFORE.getDescription(), "beforeConverter", "Test Converter");
        triggers.put("beforeTest Consumer", Arrays.asList(beforeConsumer,beforeConsumer2));
        triggers.put("afterTest Producer", Arrays.asList(afterProducer,afterProducer2));
        triggers.put("beforeTest Converter", Arrays.asList(beforeConverter));
        triggers.put("afterTest Converter", Arrays.asList(afterConverter));
        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerService).getTriggers("Module Name","Flow Name");
                will(returnValue(triggers));
            }
        });
        return flow;
    }

    private Flow createMultiRecipientListFlow()
    {
        FlowElement producer = new TestFlowElement(new TestProducer(), "Test Producer"
            , "Test Producer Description", null);

        Map<String, FlowElement> transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, producer);

        FlowElement producer2 = new TestFlowElement(new TestProducer(), "Test Producer 2"
            , "Test Producer 2 Description", null);

        Map<String, FlowElement> transitions2 = new HashMap<>();
        transitions2.put(FlowElement.DEFAULT_TRANSITION_NAME, producer2);

        TestFlowElement converter = new TestFlowElement(new TestConverter(), "Test Converter",
            "Test Converter Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, converter);

        TestFlowElement broker = new TestFlowElement(new TestBroker(),
            "Test Broker", "Test Broker Description", transitions2);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, broker);

        TestFlowElement splitter = new TestFlowElement(new TestSplitter(), "Test Splitter",
            "Test Splitter Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, splitter);

        TestFlowElement filter = new TestFlowElement(new TestFilter(), "Test Filter",
            "Test Filter Description", transitions);

        transitions = new HashMap<>();
        transitions.put("route 1", converter);
        transitions.put("route 2", filter);

        TestFlowElement multiRecipientRouter = new TestFlowElement(new TestMultiRecipientRouter(), "Test Multi Recipient Router",
            "Test Multi Recipient Router Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, multiRecipientRouter);

        TestFlowElement consumer = new TestFlowElement(new ConfiguredConsumer(), "Test Consumer", "Test Consumer Description", transitions);

        FlowConfiguration flowConfiguration = new TestFlowConfiguration(consumer);

        Flow flow = new TestFlow("Flow Name", "Module Name", flowConfiguration);
        flow.setTriggerService(triggerService);

        Trigger beforeMMR = new TriggerImpl("Module Name", "Flow Name", TriggerRelationship.BEFORE.getDescription(), "beforeTest Multi Recipient Router", "Test Multi Recipient Router");
        Trigger afterMMR = new TriggerImpl("Module Name", "Flow Name", TriggerRelationship.AFTER.getDescription(), "afterTest Multi Recipient Router", "Test Multi Recipient Router");

        triggers.put("beforeTest Multi Recipient Router", Arrays.asList(beforeMMR));
        triggers.put("afterTest Multi Recipient Router", Arrays.asList(afterMMR));

        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerService).getTriggers("Module Name","Flow Name");
                will(returnValue(triggers));
            }
        });
        return flow;
    }

    private Flow createSingleRecipientListFlow()
    {
        FlowElement producer = new TestFlowElement(new TestProducer(), "Test Producer"
            , "Test Producer Description", null);

        Map<String, FlowElement> transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, producer);

        FlowElement producer2 = new TestFlowElement(new TestProducer(), "Test Producer 2"
            , "Test Producer 2 Description", null);

        Map<String, FlowElement> transitions2 = new HashMap<>();
        transitions2.put(FlowElement.DEFAULT_TRANSITION_NAME, producer2);

        TestFlowElement converter = new TestFlowElement(new TestConverter(), "Test Converter",
            "Test Converter Description", transitions2);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, converter);

        TestFlowElement broker = new TestFlowElement(new TestBroker(),
            "Test Broker", "Test Broker Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, broker);

        TestFlowElement splitter = new TestFlowElement(new TestSplitter(), "Test Splitter",
            "Test Splitter Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, splitter);

        TestFlowElement filter = new TestFlowElement(new TestFilter(), "Test Filter",
            "Test Filter Description", transitions);

        transitions = new HashMap<>();
        transitions.put("route 1", converter);
        transitions.put("route 2", filter);

        TestFlowElement multiRecipientRouter = new TestFlowElement(new TestSingleRecipientRouter(), "Test Single Recipient Router",
            "Test Single Recipient Router Description", transitions);

        transitions = new HashMap<>();
        transitions.put(FlowElement.DEFAULT_TRANSITION_NAME, multiRecipientRouter);

        TestFlowElement consumer = new TestFlowElement(new TestConsumer(), "Test Consumer", "Test Consumer Description", transitions);

        FlowConfiguration flowConfiguration = new TestFlowConfiguration(consumer);

        Flow flow = new TestFlow("Flow Name", "Module Name", flowConfiguration);
        flow.setTriggerService(triggerService);


        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerService).getTriggers("Module Name","Flow Name");
                will(returnValue(triggers));
            }
        });
        return flow;
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
