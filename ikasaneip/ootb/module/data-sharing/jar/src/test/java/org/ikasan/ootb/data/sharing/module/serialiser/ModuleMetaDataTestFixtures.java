package org.ikasan.ootb.data.sharing.module.serialiser;

import org.ikasan.dashboard.dto.metadata.module.DecoratorMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.module.FlowElementMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.module.FlowMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.module.ModuleMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.module.TransitionImpl;
import org.ikasan.spec.module.ModuleType;
import org.ikasan.spec.metadata.model.ModuleMetaData;
import org.ikasan.spec.metadata.model.FlowMetaData;
import org.ikasan.spec.metadata.model.FlowElementMetaData;
import org.ikasan.spec.metadata.model.DecoratorMetaData;
import org.ikasan.spec.metadata.model.Transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Deterministic dashboard metadata fixtures. Configuration IDs are references;
 * configuration parameter values are fetched separately from the dashboard.
 * Each call returns fresh objects and mutable collections for individual tests to customise.
 */
public final class ModuleMetaDataTestFixtures {

    private ModuleMetaDataTestFixtures() {
    }

    /** Creates a module with one consumer-to-producer flow. */
    public static ModuleMetaDataImpl createModuleMetaData(int id) {
        return createModuleMetaData(id, 1);
    }

    /** Creates a module with the requested number of independently configured flows. */
    public static ModuleMetaDataImpl createModuleMetaData(int id, int flowCount) {
        if (flowCount < 0) {
            throw new IllegalArgumentException("flowCount must not be negative");
        }

        String moduleName = "module" + id;
        ModuleMetaDataImpl module = new ModuleMetaDataImpl();
        module.setName(moduleName);
        module.setDescription("Test module " + id);
        module.setType(ModuleType.INTEGRATION_MODULE);
        module.setVersion("1.0.0");
        module.setIkasanVersion("5.0.1-SNAPSHOT");
        module.setProtocol("http");
        module.setHost("localhost");
        module.setPort(8080);
        module.setContext(moduleName);
        module.setUrl("http://localhost:8080/" + moduleName);
        module.setConfiguredResourceId(moduleName + ".configuration");
        module.setFlows(new ArrayList<>());

        for (int flowIndex = 1; flowIndex <= flowCount; flowIndex++) {
            module.getFlows().add(createFlow(moduleName, flowIndex));
        }
        return module;
    }

    private static FlowMetaDataImpl createFlow(String moduleName, int flowIndex) {
        String flowName = "flow" + flowIndex;
        String prefix = moduleName + "." + flowName;
        FlowElementMetaDataImpl consumer = createComponent(prefix, "Consumer");
        FlowElementMetaDataImpl producer = createComponent(prefix, "Producer");

        DecoratorMetaDataImpl wiretap = new DecoratorMetaDataImpl();
        wiretap.setName("Test Wiretap");
        wiretap.setType("Wiretap");
        wiretap.setConfigurable(true);
        wiretap.setConfigurationId(prefix + ".consumer.wiretap.configuration");
        consumer.getDecorators().add(wiretap);

        TransitionImpl transition = new TransitionImpl();
        transition.setFrom(consumer.getComponentName());
        transition.setTo(producer.getComponentName());
        transition.setName("default");

        FlowMetaDataImpl flow = new FlowMetaDataImpl();
        flow.setName(flowName);
        flow.setConfigurationId(prefix + ".configuration");
        flow.setFlowStartupType("MANUAL");
        flow.setFlowStartupComment("Started manually for testing");
        // The consumer is also present in flowElements, as in the module API.
        flow.setConsumer(consumer);
        flow.setFlowElements(new ArrayList<>(Arrays.asList(consumer, producer)));
        flow.setTransitions(new ArrayList<>(Collections.singletonList(transition)));
        return flow;
    }

    private static FlowElementMetaDataImpl createComponent(String flowPrefix, String type) {
        String prefix = flowPrefix + "." + type;
        FlowElementMetaDataImpl component = new FlowElementMetaDataImpl();
        component.setComponentName("Test " + type);
        component.setDescription("Test " + type + " for " + flowPrefix);
        component.setComponentType("org.ikasan.spec.component.endpoint." + type);
        // Illustrative class names, matching the existing topology-response.json fixture.
        component.setImplementingClass("org.ikasan.metadata.components.Test" + type);
        component.setConfigurable(true);
        component.setConfigurationId(prefix + ".configuration");
        component.setInvokerConfigurationId(prefix + ".invoker.configuration");
        component.setDecorators(new ArrayList<>());
        return component;
    }

    /** Deep comparison of all metadata fields; list order and null versus empty are significant. */
    public static void assertModuleMetaDatasEqual(List<ModuleMetaData> expected, List<ModuleMetaData> actual) {
        assertListsEqual("modules", expected, actual, ModuleMetaDataTestFixtures::assertModuleEqual);
    }

    /** Compares one module and reports the property path when a value differs. */
    public static void assertModuleMetaDataEqual(ModuleMetaData expected, ModuleMetaData actual) {
        assertModuleEqual("module", expected, actual);
    }

    private static void assertModuleEqual(String path, ModuleMetaData expected, ModuleMetaData actual) {
        if (expected == null) {
            assertNull(path, actual);
            return;
        }
        assertNotNull(path, actual);
        assertEquals(path + ".type", expected.getType(), actual.getType());
        assertEquals(path + ".url", expected.getUrl(), actual.getUrl());
        assertEquals(path + ".name", expected.getName(), actual.getName());
        assertEquals(path + ".description", expected.getDescription(), actual.getDescription());
        assertEquals(path + ".version", expected.getVersion(), actual.getVersion());
        assertEquals(path + ".ikasanVersion", expected.getIkasanVersion(), actual.getIkasanVersion());
        assertEquals(path + ".configuredResourceId", expected.getConfiguredResourceId(), actual.getConfiguredResourceId());
        assertEquals(path + ".host", expected.getHost(), actual.getHost());
        assertEquals(path + ".port", expected.getPort(), actual.getPort());
        assertEquals(path + ".context", expected.getContext(), actual.getContext());
        assertEquals(path + ".protocol", expected.getProtocol(), actual.getProtocol());
        assertListsEqual(path + ".flows", expected.getFlows(), actual.getFlows(),
            ModuleMetaDataTestFixtures::assertFlowEqual);
    }

    private static void assertFlowEqual(String path, FlowMetaData expected, FlowMetaData actual) {
        if (expected == null) {
            assertNull(path, actual);
            return;
        }
        assertNotNull(path, actual);
        assertEquals(path + ".name", expected.getName(), actual.getName());
        assertEquals(path + ".configurationId", expected.getConfigurationId(), actual.getConfigurationId());
        assertEquals(path + ".flowStartupType", expected.getFlowStartupType(), actual.getFlowStartupType());
        assertEquals(path + ".flowStartupComment", expected.getFlowStartupComment(), actual.getFlowStartupComment());
        assertComponentEqual(path + ".consumer", expected.getConsumer(), actual.getConsumer());
        assertListsEqual(path + ".flowElements", expected.getFlowElements(), actual.getFlowElements(),
            ModuleMetaDataTestFixtures::assertComponentEqual);
        assertListsEqual(path + ".transitions", expected.getTransitions(), actual.getTransitions(),
            ModuleMetaDataTestFixtures::assertTransitionEqual);
    }

    private static void assertComponentEqual(String path, FlowElementMetaData expected, FlowElementMetaData actual) {
        if (expected == null) {
            assertNull(path, actual);
            return;
        }
        assertNotNull(path, actual);
        assertEquals(path + ".componentName", expected.getComponentName(), actual.getComponentName());
        assertEquals(path + ".description", expected.getDescription(), actual.getDescription());
        assertEquals(path + ".componentType", expected.getComponentType(), actual.getComponentType());
        assertEquals(path + ".implementingClass", expected.getImplementingClass(), actual.getImplementingClass());
        assertEquals(path + ".configurable", expected.isConfigurable(), actual.isConfigurable());
        assertEquals(path + ".configurationId", expected.getConfigurationId(), actual.getConfigurationId());
        assertEquals(path + ".invokerConfigurationId", expected.getInvokerConfigurationId(), actual.getInvokerConfigurationId());
        assertListsEqual(path + ".decorators", expected.getDecorators(), actual.getDecorators(),
            ModuleMetaDataTestFixtures::assertDecoratorEqual);
    }

    private static void assertDecoratorEqual(String path, DecoratorMetaData expected, DecoratorMetaData actual) {
        if (expected == null) {
            assertNull(path, actual);
            return;
        }
        assertNotNull(path, actual);
        assertEquals(path + ".name", expected.getName(), actual.getName());
        assertEquals(path + ".type", expected.getType(), actual.getType());
        assertEquals(path + ".configurable", expected.isConfigurable(), actual.isConfigurable());
        assertEquals(path + ".configurationId", expected.getConfigurationId(), actual.getConfigurationId());
    }

    private static void assertTransitionEqual(String path, Transition expected, Transition actual) {
        if (expected == null) {
            assertNull(path, actual);
            return;
        }
        assertNotNull(path, actual);
        assertEquals(path + ".from", expected.getFrom(), actual.getFrom());
        assertEquals(path + ".to", expected.getTo(), actual.getTo());
        assertEquals(path + ".name", expected.getName(), actual.getName());
    }

    private static <T> void assertListsEqual(String path, List<T> expected, List<T> actual,
                                             MetadataAssertion<T> assertion) {
        if (expected == null) {
            assertNull(path, actual);
            return;
        }
        assertNotNull(path, actual);
        assertEquals(path + ".size", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertion.compare(path + "[" + i + "]", expected.get(i), actual.get(i));
        }
    }

    @FunctionalInterface
    private interface MetadataAssertion<T> {
        void compare(String path, T expected, T actual);
    }
}
