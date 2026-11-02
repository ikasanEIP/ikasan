package org.ikasan.ootb.data.sharing.module.component.broker;

import org.ikasan.ootb.data.sharing.module.component.broker.configuration.ModuleMetaDataBrokerConfiguration;
import org.ikasan.ootb.data.sharing.module.serialiser.ConfigurationMetaDataTestFixtures;
import org.ikasan.ootb.data.sharing.module.serialiser.ModuleMetaDataTestFixtures;
import org.ikasan.ootb.data.sharing.module.serialiser.ModuleAndConfigMetaDataToBigQueueMessageSerialiser;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.ModuleMetaData;
import org.ikasan.spec.search.SearchResults;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ModuleMetaDataBrokerTest {
    private DataSharingRestService service;
    private ModuleMetaDataBroker broker;
    private ModuleMetaDataBrokerConfiguration configuration;

    @Before
    public void setUp() {
        service = mock(DataSharingRestService.class);
        broker = new ModuleMetaDataBroker(service);
        configuration = new ModuleMetaDataBrokerConfiguration();
        configuration.setModuleNames(List.of("module1", "module2", "module3"));
        configuration.setBatchSize(2);
        broker.setConfiguration(configuration);
    }

    @Test
    public void fetchesAllPagesAndStartsEachSnapshotAtZero() {
        ModuleMetaData first = moduleWithoutConfiguration(1);
        ModuleMetaData second = moduleWithoutConfiguration(2);
        ModuleMetaData third = moduleWithoutConfiguration(3);
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0))
            .thenReturn(page(List.of(first, second), 3));
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 2))
            .thenReturn(page(List.of(third), 3));
        assertEquals(List.of(first, second, third), broker.invoke("scheduled trigger").getModules());
        assertEquals(List.of(first, second, third), broker.invoke("next trigger").getModules());
        verify(service, times(2)).queryModuleMetadata(configuration.getModuleNames(), 2, 0);
        verify(service, times(2)).queryModuleMetadata(configuration.getModuleNames(), 2, 2);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void returnsEmptySnapshotWhenNoModulesMatch() {
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0))
            .thenReturn(page(List.of(), 0));
        var snapshot = broker.invoke(null);
        assertTrue(snapshot.getModules().isEmpty());
        assertTrue(snapshot.getConfigurations().isEmpty());
        verify(service).queryModuleMetadata(configuration.getModuleNames(), 2, 0);
        verifyNoMoreInteractions(service);
    }

    @Test(expected = EndpointException.class)
    public void rejectsEmptyPageBeforeTheEnd() {
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0))
            .thenReturn(page(List.of(), 3));
        broker.invoke(null);
    }

    @Test
    public void preservesRestFailureCause() {
        RuntimeException failure = new RuntimeException("Dashboard unavailable");
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0)).thenThrow(failure);
        try {
            broker.invoke(null);
            fail("Expected EndpointException");
        } catch (EndpointException e) {
            assertSame(failure, e.getCause());
        }
    }

    @Test
    public void rejectsInvalidBatchSizeBeforeCallingDashboard() {
        configuration.setBatchSize(0);
        try {
            broker.invoke(null);
            fail("Expected EndpointException");
        } catch (EndpointException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
        verifyNoInteractions(service);
    }

    @Test
    public void collectsAllReferenceTypesOnceAndBatchesConfigurationIds() {
        ModuleMetaData module = ModuleMetaDataTestFixtures.createModuleMetaData(1);
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0))
            .thenReturn(page(List.of(module), 1));
        List<String> ids = List.of("module1.configuration", "module1.flow1.configuration",
            "module1.flow1.Consumer.configuration", "module1.flow1.Consumer.invoker.configuration",
            "module1.flow1.consumer.wiretap.configuration", "module1.flow1.Producer.configuration",
            "module1.flow1.Producer.invoker.configuration");
        List<ConfigurationMetaData> expected = new ArrayList<>();
        for (int start = 0; start < ids.size(); start += 2) {
            List<String> batch = ids.subList(start, Math.min(start + 2, ids.size()));
            List<ConfigurationMetaData> values = new ArrayList<>();
            for (String id : batch) {
                values.add(ConfigurationMetaDataTestFixtures.createConfigurationMetaData(id));
            }
            expected.addAll(values);
            when(service.queryConfiguration(batch, 2, 0)).thenReturn(page(values, values.size()));
        }
        var snapshot = broker.invoke(null);
        assertEquals(List.of(module), snapshot.getModules());
        assertEquals(expected, snapshot.getConfigurations());
        verify(service).queryModuleMetadata(configuration.getModuleNames(), 2, 0);
        for (int start = 0; start < ids.size(); start += 2) {
            verify(service).queryConfiguration(ids.subList(start, Math.min(start + 2, ids.size())), 2, 0);
        }
        verifyNoMoreInteractions(service);
        var serialiser = new ModuleAndConfigMetaDataToBigQueueMessageSerialiser();
        var restored = serialiser.deserialise(serialiser.serialise(snapshot));
        ModuleMetaDataTestFixtures.assertModuleMetaDatasEqual(snapshot.getModules(), restored.getModules());
        ConfigurationMetaDataTestFixtures.assertConfigurationMetaDataEqual(
            snapshot.getConfigurations(), restored.getConfigurations());
    }

    @Test
    public void skipsBlankConfigurationReferences() {
        ModuleMetaData module = moduleWithoutConfiguration(1);
        module.setConfiguredResourceId("module1.configuration");
        configuration.setBatchSize(100);
        when(service.queryModuleMetadata(configuration.getModuleNames(), 100, 0))
            .thenReturn(page(List.of(module), 1));
        List<String> ids = List.of("module1.configuration");
        ConfigurationMetaData value = ConfigurationMetaDataTestFixtures.createConfigurationMetaData(ids.get(0));
        when(service.queryConfiguration(ids, 100, 0)).thenReturn(page(List.of(value), 1));
        assertEquals(List.of(value), broker.invoke(null).getConfigurations());
        verify(service).queryConfiguration(ids, 100, 0);
        module.setConfiguredResourceId(" ");
        assertTrue(broker.invoke(null).getConfigurations().isEmpty());
        verify(service, times(2)).queryModuleMetadata(configuration.getModuleNames(), 100, 0);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void followsConfigurationPagination() {
        configuration.setBatchSize(100);
        ModuleMetaData module = ModuleMetaDataTestFixtures.createModuleMetaData(1, 0);
        var flow = ModuleMetaDataTestFixtures.createModuleMetaData(2).getFlows().get(0);
        flow.setConsumer(null);
        flow.setFlowElements(null);
        module.setFlows(List.of(flow));
        List<String> ids = List.of(module.getConfiguredResourceId(), flow.getConfigurationId());
        ConfigurationMetaData first = ConfigurationMetaDataTestFixtures.createConfigurationMetaData(ids.get(0));
        ConfigurationMetaData second = ConfigurationMetaDataTestFixtures.createConfigurationMetaData(ids.get(1));
        when(service.queryModuleMetadata(configuration.getModuleNames(), 100, 0)).thenReturn(page(List.of(module), 1));
        when(service.queryConfiguration(ids, 100, 0)).thenReturn(page(List.of(first), 2));
        when(service.queryConfiguration(ids, 100, 1)).thenReturn(page(List.of(second), 2));
        assertEquals(List.of(first, second), broker.invoke(null).getConfigurations());
        verify(service).queryConfiguration(ids, 100, 1);
    }

    @Test
    public void configurationFailureStillReturnsModuleMetadata() {
        ModuleMetaData module = ModuleMetaDataTestFixtures.createModuleMetaData(1, 0);
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0)).thenReturn(page(List.of(module), 1));
        when(service.queryConfiguration(List.of(module.getConfiguredResourceId()), 2, 0))
            .thenThrow(new RuntimeException("Configuration lookup failed"));
        var result = broker.invoke(null);
        assertEquals(List.of(module), result.getModules());
        assertTrue(result.getConfigurations().isEmpty());
    }

    @Test
    public void missingConfigurationsStillReturnsModuleMetadata() {
        ModuleMetaData module = ModuleMetaDataTestFixtures.createModuleMetaData(1, 0);
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0)).thenReturn(page(List.of(module), 1));
        when(service.queryConfiguration(List.of(module.getConfiguredResourceId()), 2, 0))
            .thenReturn(page(List.of(), 0));
        var result = broker.invoke(null);
        assertEquals(List.of(module), result.getModules());
        assertTrue(result.getConfigurations().isEmpty());
    }

    @Test
    public void retainsSuccessfulPagesAndContinuesWithLaterIdBatches() {
        ModuleMetaData first = ModuleMetaDataTestFixtures.createModuleMetaData(1, 0);
        ModuleMetaData second = ModuleMetaDataTestFixtures.createModuleMetaData(2, 0);
        ModuleMetaData third = ModuleMetaDataTestFixtures.createModuleMetaData(3, 0);
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0))
            .thenReturn(page(List.of(first, second), 3));
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 2))
            .thenReturn(page(List.of(third), 3));
        List<String> firstBatch = List.of(first.getConfiguredResourceId(), second.getConfiguredResourceId());
        List<String> lastBatch = List.of(third.getConfiguredResourceId());
        ConfigurationMetaData firstConfig = ConfigurationMetaDataTestFixtures.createConfigurationMetaData(firstBatch.get(0));
        ConfigurationMetaData thirdConfig = ConfigurationMetaDataTestFixtures.createConfigurationMetaData(lastBatch.get(0));
        when(service.queryConfiguration(firstBatch, 2, 0)).thenReturn(page(List.of(firstConfig), 2));
        when(service.queryConfiguration(firstBatch, 2, 1)).thenThrow(new RuntimeException("Page unavailable"));
        when(service.queryConfiguration(lastBatch, 2, 0)).thenReturn(page(List.of(thirdConfig), 1));
        var result = broker.invoke(null);
        assertEquals(List.of(first, second, third), result.getModules());
        assertEquals(List.of(firstConfig, thirdConfig), result.getConfigurations());
        verify(service).queryConfiguration(lastBatch, 2, 0);
    }

    @Test
    public void malformedOrInconsistentConfigurationResponsesDoNotAbortModules() {
        ModuleMetaData module = ModuleMetaDataTestFixtures.createModuleMetaData(1, 0);
        when(service.queryModuleMetadata(configuration.getModuleNames(), 2, 0)).thenReturn(page(List.of(module), 1));
        when(service.queryConfiguration(List.of(module.getConfiguredResourceId()), 2, 0))
            .thenReturn(null, page(null, 1), page(List.of(), 1));
        for (int i = 0; i < 3; i++) {
            var result = broker.invoke(null);
            assertEquals(List.of(module), result.getModules());
            assertTrue(result.getConfigurations().isEmpty());
        }
    }

    private ModuleMetaData moduleWithoutConfiguration(int id) {
        ModuleMetaData module = ModuleMetaDataTestFixtures.createModuleMetaData(id, 0);
        module.setConfiguredResourceId(null);
        return module;
    }

    private <T> SearchResults<T> page(List<T> modules, long total) {
        return new SearchResults<T>() {
            public List<T> getResultList() { return modules; }
            public long getTotalNumberOfResults() { return total; }
            public long getQueryResponseTime() { return 0; }
        };
    }
}
