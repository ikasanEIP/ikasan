package org.ikasan.ootb.data.sharing.module;

import org.ikasan.bigqueue.BigArrayImpl;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.ootb.data.sharing.module.boot.*;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.ootb.data.sharing.module.util.BigQueueNameHelper;
import org.ikasan.spec.flow.Flow;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataSharingFlowFactoryTest {
    @Rule
    public TemporaryFolder queueDirectory = new TemporaryFolder();

    private DataSharingFlowFactory factory;
    private DataSharingConfiguredModuleConfiguration configuration;
    private Flow consumer;
    private Flow producer;

    @Before
    public void setup() {
        factory = new DataSharingFlowFactory();
        configuration = mock(DataSharingConfiguredModuleConfiguration.class);
        consumer = mock(Flow.class);
        producer = mock(Flow.class);
        factory.dataSharingConfiguredModuleConfiguration = configuration;
        ReflectionTestUtils.setField(factory, "queueDir", queueDirectory.getRoot().getAbsolutePath());
        ReflectionTestUtils.setField(factory, "bigQueuePageSize", BigArrayImpl.MINIMUM_DATA_PAGE_SIZE);
    }

    @Test
    public void createsErrorQueueAndBothFlowsForEachDashboard() throws Exception {
        factory.errorEntityConsumerFlowFactory = mock(ErrorEntityConsumerFlowFactory.class);
        factory.errorEntityProducerFlowFactory = mock(ErrorEntityProducerFlowFactory.class);
        for (String dashboard : List.of("target", "another-target")) {
            when(factory.errorEntityConsumerFlowFactory.create(dashboard)).thenReturn(consumer);
            when(factory.errorEntityProducerFlowFactory.create(dashboard)).thenReturn(producer);
        }

        assertRegistrationForEachDashboard(DataSharingEntity.ERROR_ENTITY);

        for (String dashboard : List.of("target", "another-target")) {
            verify(factory.errorEntityConsumerFlowFactory).create(dashboard);
            verify(factory.errorEntityProducerFlowFactory).create(dashboard);
        }
    }

    @Test
    public void createsExclusionQueueAndBothFlowsForEachDashboard() throws Exception {
        factory.exclusionEntityConsumerFlowFactory = mock(ExclusionEntityConsumerFlowFactory.class);
        factory.exclusionEntityProducerFlowFactory = mock(ExclusionEntityProducerFlowFactory.class);
        for (String dashboard : List.of("target", "another-target")) {
            when(factory.exclusionEntityConsumerFlowFactory.create(dashboard)).thenReturn(consumer);
            when(factory.exclusionEntityProducerFlowFactory.create(dashboard)).thenReturn(producer);
        }

        assertRegistrationForEachDashboard(DataSharingEntity.EXCLUSION_ENTITY);

        for (String dashboard : List.of("target", "another-target")) {
            verify(factory.exclusionEntityConsumerFlowFactory).create(dashboard);
            verify(factory.exclusionEntityProducerFlowFactory).create(dashboard);
        }
    }

    @Test
    public void createsReplayQueueAndBothFlowsForEachDashboard() throws Exception {
        factory.replayEntityConsumerFlowFactory = mock(ReplayEntityConsumerFlowFactory.class);
        factory.replayEntityProducerFlowFactory = mock(ReplayEntityProducerFlowFactory.class);
        for (String dashboard : List.of("target", "another-target")) {
            when(factory.replayEntityConsumerFlowFactory.create(dashboard)).thenReturn(consumer);
            when(factory.replayEntityProducerFlowFactory.create(dashboard)).thenReturn(producer);
        }

        assertRegistrationForEachDashboard(DataSharingEntity.REPLAY_ENTITY);

        for (String dashboard : List.of("target", "another-target")) {
            verify(factory.replayEntityConsumerFlowFactory).create(dashboard);
            verify(factory.replayEntityProducerFlowFactory).create(dashboard);
        }
    }

    @Test
    public void createsWiretapQueueAndBothFlowsForEachDashboard() throws Exception {
        factory.wiretapEntityConsumerFlowFactory = mock(WiretapEntityConsumerFlowFactory.class);
        factory.wiretapEntityProducerFlowFactory = mock(WiretapEntityProducerFlowFactory.class);
        for (String dashboard : List.of("target", "another-target")) {
            when(factory.wiretapEntityConsumerFlowFactory.create(dashboard)).thenReturn(consumer);
            when(factory.wiretapEntityProducerFlowFactory.create(dashboard)).thenReturn(producer);
        }

        assertRegistrationForEachDashboard(DataSharingEntity.WIRETAP_ENTITY);

        for (String dashboard : List.of("target", "another-target")) {
            verify(factory.wiretapEntityConsumerFlowFactory).create(dashboard);
            verify(factory.wiretapEntityProducerFlowFactory).create(dashboard);
        }
    }

    @Test
    public void createsModuleMetadataQueueAndBothFlowsForEachDashboard() throws Exception {
        factory.metadataConsumerFlowFactory = mock(MetaDataConsumerFlowFactory.class);
        factory.metadataProducerFlowFactory = mock(MetaDataProducerFlowFactory.class);
        for (String dashboard : List.of("target", "another-target")) {
            when(factory.metadataConsumerFlowFactory.create(dashboard)).thenReturn(consumer);
            when(factory.metadataProducerFlowFactory.create(dashboard)).thenReturn(producer);
        }

        assertRegistrationForEachDashboard(DataSharingEntity.MODULE_METADATA_ENTITY);

        for (String dashboard : List.of("target", "another-target")) {
            verify(factory.metadataConsumerFlowFactory).create(dashboard);
            verify(factory.metadataProducerFlowFactory).create(dashboard);
        }
    }

    private void assertRegistrationForEachDashboard(String entity) throws Exception {
        when(configuration.getDownStreamDashboardEntities())
            .thenReturn(Map.of("target", entity, "another-target", entity));
        EntityBigQueueCache cache = EntityBigQueueCache.instance();
        IBigQueue previousDashboardQueue = null;
        try {
            for (String dashboard : List.of("target", "another-target")) {
                String queueName = BigQueueNameHelper.getQueueName(dashboard, entity);
                assertFalse("Test queue must not already be registered", cache.contains(queueName));
                assertEquals(List.of(consumer, producer), factory.create(dashboard, null));
                IBigQueue queue = cache.get(queueName);
                assertNotNull(queue);
                assertNotSame("Dashboards must have separate queues", previousDashboardQueue, queue);
                previousDashboardQueue = queue;
            }
        } finally {
            for (String dashboard : List.of("target", "another-target")) {
                String queueName = BigQueueNameHelper.getQueueName(dashboard, entity);
                IBigQueue queue = cache.get(queueName);
                cache.remove(queueName);
                if (queue != null) {
                    try {
                        queue.removeAll();
                    } finally {
                        queue.close();
                    }
                }
            }
        }
    }
}
