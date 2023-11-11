package org.ikasan.topology.metadata;

import org.apache.commons.io.IOUtils;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.module.ModuleType;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerService;
import org.ikasan.topology.metadata.components.*;
import org.ikasan.topology.metadata.flow.TestFlow;
import org.ikasan.topology.metadata.flow.TestFlowConfiguration;
import org.ikasan.topology.metadata.flow.TestFlowElement;
import org.ikasan.topology.metadata.module.TestConfiguredModule;
import org.ikasan.topology.metadata.module.TestModule;
import org.jmock.Expectations;
import org.jmock.Mockery;
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


public class JsonModuleMetaDataProviderTest
{
    public static final String MODULE_RESULT_JSON = "/data/module.json";

    private Mockery mockery = new Mockery();

    TriggerService triggerService = mockery.mock(TriggerService.class);
    Map<String, List<Trigger>> triggers = new HashMap<>();


    @Test
    void test_module_serialisation() throws IOException, JSONException
    {
        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        JsonModuleMetaDataProvider jsonModuleMetaDataProvider = new JsonModuleMetaDataProvider(jsonFlowMetaDataProvider);

        TestModule testModule = new TestModule();
        testModule.getFlows().add(createSimpleFlow("Simple Flow 1"));
        testModule.getFlows().add(createSimpleFlow("Simple Flow 2"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 1"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 2"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 1"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 2"));

        String json = jsonModuleMetaDataProvider.describeModule(testModule, new HashMap<>());

        JSONAssert.assertEquals("JSON Result must equal!", loadDataFile(MODULE_RESULT_JSON), json, JSONCompareMode.STRICT);
    }


    @Test
    void test_module_json_to_object() throws IOException
    {
        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        JsonModuleMetaDataProvider jsonModuleMetaDataProvider = new JsonModuleMetaDataProvider(jsonFlowMetaDataProvider);

        TestModule testModule = new TestModule();
        testModule.getFlows().add(createSimpleFlow("Simple Flow 1"));
        testModule.getFlows().add(createSimpleFlow("Simple Flow 2"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 1"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 2"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 1"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 2"));

        String json = jsonModuleMetaDataProvider.describeModule(testModule, new HashMap<>());

        ModuleMetaData moduleMetaData = jsonModuleMetaDataProvider.deserialiseModule(json);

        assertEquals("module name", moduleMetaData.getName(), "Module name equals!");
        assertEquals(Integer.valueOf(8888), moduleMetaData.getPort(), "Port equals!");
        assertEquals("context", moduleMetaData.getContext(), "Context equals!");
        assertEquals("protocol", moduleMetaData.getProtocol(), "Protocol equals!");
        assertEquals("host", moduleMetaData.getHost(), "Host equals!");
        assertEquals(ModuleType.SCHEDULER_AGENT, moduleMetaData.getType(), "Module type equals!");
        assertEquals("module description", moduleMetaData.getDescription(), "Module description equals!");
        assertEquals("module version", moduleMetaData.getVersion(), "Module version equals!");
        assertEquals(6, moduleMetaData.getFlows().size(), "Number of flows == 6!");
    }

    @Test
    void test_configured_module_json_to_object() throws IOException
    {
        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        JsonModuleMetaDataProvider jsonModuleMetaDataProvider = new JsonModuleMetaDataProvider(jsonFlowMetaDataProvider);

        TestConfiguredModule testModule = new TestConfiguredModule();
        testModule.getFlows().add(createSimpleFlow("Simple Flow 1"));
        testModule.getFlows().add(createSimpleFlow("Simple Flow 2"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 1"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 2"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 1"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 2"));

        String json = jsonModuleMetaDataProvider.describeModule(testModule, new HashMap<>());

        ModuleMetaData moduleMetaData = jsonModuleMetaDataProvider.deserialiseModule(json);

        assertEquals("module name", moduleMetaData.getName(), "Module name equals!");
        assertEquals(Integer.valueOf(8888), moduleMetaData.getPort(), "Port equals!");
        assertEquals("context", moduleMetaData.getContext(), "Context equals!");
        assertEquals("protocol", moduleMetaData.getProtocol(), "Protocol equals!");
        assertEquals("host", moduleMetaData.getHost(), "Host equals!");
        assertEquals(ModuleType.SCHEDULER_AGENT, moduleMetaData.getType(), "Module type equals!");
        assertEquals("module description", moduleMetaData.getDescription(), "Module description equals!");
        assertEquals("module version", moduleMetaData.getVersion(), "Module version equals!");
        assertEquals(6, moduleMetaData.getFlows().size(), "Number of flows == 6!");
        assertEquals("configurationId", moduleMetaData.getConfiguredResourceId(), "Configured resource id equals!");
    }

    @Test
    void test_module_json_to_object_disabled_startup_types() throws IOException
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
        JsonModuleMetaDataProvider jsonModuleMetaDataProvider = new JsonModuleMetaDataProvider(jsonFlowMetaDataProvider);

        TestModule testModule = new TestModule();
        testModule.getFlows().add(createSimpleFlow("Simple Flow 1"));
        testModule.getFlows().add(createSimpleFlow("Simple Flow 2"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 1"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 2"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 1"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 2"));

        HashMap<String, StartupControl> startupControlMap = new HashMap<>();
        startupControlMap.put("Simple Flow 1", startupControl);
        startupControlMap.put("Simple Flow 2", startupControl);
        startupControlMap.put("Multi Flow 1", startupControl);
        startupControlMap.put("Multi Flow 2", startupControl);
        startupControlMap.put("Single Flow 1", startupControl);
        startupControlMap.put("Single Flow 2", startupControl);

        String json = jsonModuleMetaDataProvider.describeModule(testModule, startupControlMap);

        ModuleMetaData moduleMetaData = jsonModuleMetaDataProvider.deserialiseModule(json);

        assertEquals("module name", moduleMetaData.getName(), "Module name equals!");
        assertEquals(Integer.valueOf(8888), moduleMetaData.getPort(), "Port equals!");
        assertEquals("context", moduleMetaData.getContext(), "Context equals!");
        assertEquals("protocol", moduleMetaData.getProtocol(), "Protocol equals!");
        assertEquals("host", moduleMetaData.getHost(), "Host equals!");
        assertEquals("module description", moduleMetaData.getDescription(), "Module description equals!");
        assertEquals("module version", moduleMetaData.getVersion(), "Module version equals!");
        assertEquals(6, moduleMetaData.getFlows().size(), "Number of flows == 6!");

        moduleMetaData.getFlows().forEach(
            flowMetaData -> assertEquals(StartupType.DISABLED.name(), flowMetaData.getFlowStartupType(), "Flow start up type equals!")
        );
    }

    @Test
    void test_module_json_to_object_automatic_statup_types() throws IOException
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
        JsonModuleMetaDataProvider jsonModuleMetaDataProvider = new JsonModuleMetaDataProvider(jsonFlowMetaDataProvider);

        TestModule testModule = new TestModule();
        testModule.getFlows().add(createSimpleFlow("Simple Flow 1"));
        testModule.getFlows().add(createSimpleFlow("Simple Flow 2"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 1"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 2"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 1"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 2"));

        HashMap<String, StartupControl> startupControlMap = new HashMap<>();
        startupControlMap.put("Simple Flow 1", startupControl);
        startupControlMap.put("Simple Flow 2", startupControl);
        startupControlMap.put("Multi Flow 1", startupControl);
        startupControlMap.put("Multi Flow 2", startupControl);
        startupControlMap.put("Single Flow 1", startupControl);
        startupControlMap.put("Single Flow 2", startupControl);

        String json = jsonModuleMetaDataProvider.describeModule(testModule, startupControlMap);

        ModuleMetaData moduleMetaData = jsonModuleMetaDataProvider.deserialiseModule(json);

        assertEquals("module name", moduleMetaData.getName(), "Module name equals!");
        assertEquals(Integer.valueOf(8888), moduleMetaData.getPort(), "Port equals!");
        assertEquals("context", moduleMetaData.getContext(), "Context equals!");
        assertEquals("protocol", moduleMetaData.getProtocol(), "Protocol equals!");
        assertEquals("host", moduleMetaData.getHost(), "Host equals!");
        assertEquals("module description", moduleMetaData.getDescription(), "Module description equals!");
        assertEquals("module version", moduleMetaData.getVersion(), "Module version equals!");
        assertEquals(6, moduleMetaData.getFlows().size(), "Number of flows == 6!");

        moduleMetaData.getFlows().forEach(
            flowMetaData -> assertEquals(StartupType.AUTOMATIC.name(), flowMetaData.getFlowStartupType(), "Flow start up type equals!")
        );
    }

    @Test
    void test_module_json_to_object_manual_statup_types() throws IOException
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
        JsonModuleMetaDataProvider jsonModuleMetaDataProvider = new JsonModuleMetaDataProvider(jsonFlowMetaDataProvider);

        TestModule testModule = new TestModule();
        testModule.getFlows().add(createSimpleFlow("Simple Flow 1"));
        testModule.getFlows().add(createSimpleFlow("Simple Flow 2"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 1"));
        testModule.getFlows().add(createMultiRecipientListFlow("Multi Flow 2"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 1"));
        testModule.getFlows().add(createSingleRecipientListFlow("Single Flow 2"));

        HashMap<String, StartupControl> startupControlMap = new HashMap<>();
        startupControlMap.put("Simple Flow 1", startupControl);
        startupControlMap.put("Simple Flow 2", startupControl);
        startupControlMap.put("Multi Flow 1", startupControl);
        startupControlMap.put("Multi Flow 2", startupControl);
        startupControlMap.put("Single Flow 1", startupControl);
        startupControlMap.put("Single Flow 2", startupControl);

        String json = jsonModuleMetaDataProvider.describeModule(testModule, startupControlMap);

        ModuleMetaData moduleMetaData = jsonModuleMetaDataProvider.deserialiseModule(json);

        assertEquals("module name", moduleMetaData.getName(), "Module name equals!");
        assertEquals(Integer.valueOf(8888), moduleMetaData.getPort(), "Port equals!");
        assertEquals("context", moduleMetaData.getContext(), "Context equals!");
        assertEquals("protocol", moduleMetaData.getProtocol(), "Protocol equals!");
        assertEquals("host", moduleMetaData.getHost(), "Host equals!");
        assertEquals("module description", moduleMetaData.getDescription(), "Module description equals!");
        assertEquals("module version", moduleMetaData.getVersion(), "Module version equals!");
        assertEquals(6, moduleMetaData.getFlows().size(), "Number of flows == 6!");

        moduleMetaData.getFlows().forEach(
            flowMetaData -> assertEquals(StartupType.MANUAL.name(), flowMetaData.getFlowStartupType(), "Flow start up type equals!")
        );
    }

    private Flow createSimpleFlow(String flowName)
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

        Flow flow = new TestFlow(flowName, "Module Name", flowConfiguration);

        flow.setTriggerService(triggerService);

        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerService).getTriggers("Module Name",flowName);
                will(returnValue(triggers));
            }
        });
        return flow;
    }

    private Flow createMultiRecipientListFlow(String flowName)
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

        Flow flow = new TestFlow(flowName, "Module Name", flowConfiguration);
        flow.setTriggerService(triggerService);

        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerService).getTriggers("Module Name",flowName);
                will(returnValue(triggers));
            }
        });
        return flow;
    }

    private Flow createSingleRecipientListFlow(String flowName)
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

        Flow flow = new TestFlow(flowName, "Module Name", flowConfiguration);

        flow.setTriggerService(triggerService);

        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerService).getTriggers("Module Name",flowName);
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
