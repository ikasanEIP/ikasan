package org.ikasan.ootb.data.sharing.module.serialiser;

import org.ikasan.dashboard.dto.metadata.configuration.ConfigurationMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.configuration.ConfigurationParameterMetaDataImpl;
import org.ikasan.spec.metadata.model.ConfigurationParameterMetaData;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.DecoratorMetaData;
import org.ikasan.spec.metadata.model.FlowElementMetaData;
import org.ikasan.spec.metadata.model.FlowMetaData;
import org.ikasan.spec.metadata.model.ModuleMetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Configuration DTO fixtures matching the dashboard configuration-response.json schema.
 * Parameter names and values are illustrative, not executable component configuration.
 * Every call creates fresh mutable objects and collections.
 */
public final class ConfigurationMetaDataTestFixtures {
    private static final String MODEL_PACKAGE = "org.ikasan.configurationService.model.";

    private ConfigurationMetaDataTestFixtures() {
    }

    /** The single configuration body returned by GET /rest/configuration/module. */
    public static ConfigurationMetaDataImpl createModuleConfiguration(ModuleMetaData module) {
        return createConfigurationMetaData(module.getConfiguredResourceId());
    }

    /** Creates a configuration with an ID but null description, implementingClass and parameters. */
    public static ConfigurationMetaDataImpl createConfigurationMetaDataWithNullFields(String configurationId) {
        if (configurationId == null || configurationId.isBlank()) {
            throw new IllegalArgumentException("configurationId must not be blank");
        }
        return new ConfigurationMetaDataImpl(configurationId, null, null, null);
    }

    /** Creates representative string, integer, long, boolean, list and map parameters. */
    public static ConfigurationMetaDataImpl createConfigurationMetaData(String configurationId) {
        if (configurationId == null || configurationId.isBlank()) {
            throw new IllegalArgumentException("configurationId must not be blank");
        }
        List<ConfigurationParameterMetaData> parameters = new ArrayList<>();
        parameters.add(parameter("destination", "test-destination", "String"));
        parameters.add(parameter("batchSize", 100, "Integer"));
        // Above Integer.MAX_VALUE so an untyped JSON reader retains a Long value.
        parameters.add(parameter("retentionMilliseconds", 2592000000L, "Long"));
        parameters.add(parameter("enabled", true, "Boolean"));
        parameters.add(parameter("moduleNames", new ArrayList<>(Arrays.asList("module1", "module2")), "List"));
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("businessUnit", "test-business-unit");
        properties.put("environment", "test");
        parameters.add(parameter("properties", properties, "Map"));

        return new ConfigurationMetaDataImpl(configurationId, "Test configuration for " + configurationId,
            MODEL_PACKAGE + "DefaultConfiguration", parameters);
    }

    private static <T> ConfigurationParameterMetaDataImpl<T> parameter(String name, T value, String type) {
        // The existing REST fixture uses null parameter IDs; configurationId identifies the resource.
        return new ConfigurationParameterMetaDataImpl<>(null, name, value, "Test " + name,
            MODEL_PACKAGE + "ConfigurationParameter" + type + "Impl");
    }

    /**
     * Creates one configuration for each distinct nonblank reference in the supplied metadata.
     * Includes module, flow, component, invoker and decorator references. Consumer references
     * also present in flowElements are included only once.
     */
    public static List<ConfigurationMetaDataImpl> createConfigurationsForModule(ModuleMetaData module) {
        Set<String> ids = new LinkedHashSet<>();
        addId(ids, module.getConfiguredResourceId());
        if (module.getFlows() != null) {
            for (FlowMetaData flow : module.getFlows()) {
                addId(ids, flow.getConfigurationId());
                addComponentIds(ids, flow.getConsumer());
                if (flow.getFlowElements() != null) {
                    for (FlowElementMetaData element : flow.getFlowElements()) {
                        addComponentIds(ids, element);
                    }
                }
            }
        }
        List<ConfigurationMetaDataImpl> configurations = new ArrayList<>();
        for (String id : ids) {
            configurations.add(createConfigurationMetaData(id));
        }
        return configurations;
    }

    /** Complete, single-page GET /rest/data-sharing/configuration response for JSON/WireMock stubs. */
    public static Map<String, Object> createConfigurationResponse(ModuleMetaData module) {
        List<ConfigurationMetaDataImpl> configurations = createConfigurationsForModule(module);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", configurations);
        response.put("totalCount", (long) configurations.size());
        response.put("hasMore", false);
        return response;
    }

    private static void addComponentIds(Set<String> ids, FlowElementMetaData element) {
        if (element == null) {
            return;
        }
        addId(ids, element.getConfigurationId());
        addId(ids, element.getInvokerConfigurationId());
        if (element.getDecorators() != null) {
            for (DecoratorMetaData decorator : element.getDecorators()) {
                addId(ids, decorator.getConfigurationId());
            }
        }
    }

    private static void addId(Set<String> ids, String id) {
        if (id != null && !id.isBlank()) {
            ids.add(id);
        }
    }

    /** Compares configurations in order, reporting the property path on failure. */
    public static void assertConfigurationMetaDataEqual(List<? extends ConfigurationMetaData> expected,
                                                        List<? extends ConfigurationMetaData> actual) {
        if (expected == null) {
            assertNull("configurations", actual);
            return;
        }
        assertNotNull("configurations", actual);
        assertEquals("configurations.size", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertConfigurationEqual("configurations[" + i + "]", expected.get(i), actual.get(i));
        }
    }

    /**
     * Compares all configuration and parameter fields. List order and numeric Java types
     * are significant; map entry order is not. Null collections differ from empty ones.
     */
    public static void assertConfigurationMetaDataEqual(ConfigurationMetaData<?> expected,
                                                       ConfigurationMetaData<?> actual) {
        assertConfigurationEqual("configuration", expected, actual);
    }

    private static void assertConfigurationEqual(String path, ConfigurationMetaData<?> expected,
                                                 ConfigurationMetaData<?> actual) {
        if (expected == null) {
            assertNull(path, actual);
            return;
        }
        assertNotNull(path, actual);
        assertEquals(path + ".configurationId", expected.getConfigurationId(), actual.getConfigurationId());
        assertEquals(path + ".description", expected.getDescription(), actual.getDescription());
        assertEquals(path + ".implementingClass", expected.getImplementingClass(), actual.getImplementingClass());
        if (expected.getParameters() == null) {
            assertNull(path + ".parameters", actual.getParameters());
            return;
        }
        assertTrue(path + ".parameters: expected a parameter list", expected.getParameters() instanceof List<?>);
        assertTrue(path + ".parameters: actual must be a parameter list", actual.getParameters() instanceof List<?>);
        List<?> expectedParameters = (List<?>) expected.getParameters();
        List<?> actualParameters = (List<?>) actual.getParameters();
        assertEquals(path + ".parameters.size", expectedParameters.size(), actualParameters.size());
        for (int i = 0; i < expectedParameters.size(); i++) {
            String parameterPath = path + ".parameters[" + i + "]";
            Object expectedParameter = expectedParameters.get(i);
            Object actualParameter = actualParameters.get(i);
            if (expectedParameter == null) {
                assertNull(parameterPath, actualParameter);
                continue;
            }
            assertTrue(parameterPath + ": expected parameter metadata",
                expectedParameter instanceof ConfigurationParameterMetaData<?>);
            assertTrue(parameterPath + ": actual must be parameter metadata",
                actualParameter instanceof ConfigurationParameterMetaData<?>);
            ConfigurationParameterMetaData<?> original = (ConfigurationParameterMetaData<?>) expectedParameter;
            ConfigurationParameterMetaData<?> restored = (ConfigurationParameterMetaData<?>) actualParameter;
            assertEquals(parameterPath + ".id", original.getId(), restored.getId());
            assertEquals(parameterPath + ".name", original.getName(), restored.getName());
            assertEquals(parameterPath + ".description", original.getDescription(), restored.getDescription());
            assertEquals(parameterPath + ".implementingClass", original.getImplementingClass(), restored.getImplementingClass());
            // List.equals and Map.equals compare their contents, including nested lists/maps.
            assertEquals(parameterPath + ".value", original.getValue(), restored.getValue());
        }
    }
}
