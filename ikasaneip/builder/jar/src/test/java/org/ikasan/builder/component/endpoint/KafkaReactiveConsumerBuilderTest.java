package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.kafka.client.reactive.consumer.KafkaConsumer;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedRelatedEventIdentifierService;
import org.ikasan.spec.event.MessageListener;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class KafkaReactiveConsumerBuilderTest {

    @Mock
    AopProxyProvider aopProxyProvider;
    @Mock
    EventFactory eventFactory;

    @Mock
    ManagedRelatedEventIdentifierService relatedEventIdentifierService;

    @Mock
    MessageListener messageListener;

    @Mock
    ResubmissionEventFactory resubmissionEventFactory;

    @Mock
    ConfigurationService configurationService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_build_success() {
        KafkaReactiveConsumerBuilder builder = new KafkaReactiveConsumerBuilderImpl(this.aopProxyProvider, this.configurationService);

        Mockito.when(this.aopProxyProvider.applyPointcut(Mockito.anyString(), Mockito.any())).thenReturn(this.messageListener);

        KafkaConsumer consumer = (KafkaConsumer) builder
            .setEventFactory(this.eventFactory)
            .setManagedEventIdentifierService(this.relatedEventIdentifierService)
            .setConfigurationId("configurationId")
            .setListener(this.messageListener)
            .setResubmissionEventFactory(this.resubmissionEventFactory)
            .build();

        Assert.assertEquals(this.eventFactory, ReflectionTestUtils.getField(consumer, "flowEventFactory"));
        Assert.assertEquals(this.relatedEventIdentifierService, ReflectionTestUtils.getField(consumer, "managedRelatedEventIdentifierService"));
        Assert.assertEquals("configurationId", consumer.getConfiguredResourceId());
        Assert.assertEquals(this.messageListener, ReflectionTestUtils.getField(consumer, "messageListener"));
        Assert.assertEquals(this.resubmissionEventFactory, ReflectionTestUtils.getField(consumer, "resubmissionEventFactory"));
    }

    @Test(expected = RuntimeException.class)
    public void test_exception_missing_event_identifier_service() {
        KafkaReactiveConsumerBuilder builder = new KafkaReactiveConsumerBuilderImpl(this.aopProxyProvider, this.configurationService);
        builder
            .setEventFactory(this.eventFactory)
            .setConfigurationId("configurationId")
            .setListener(this.messageListener)
            .setResubmissionEventFactory(this.resubmissionEventFactory)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_aop_proxy() {
        new KafkaReactiveConsumerBuilderImpl(null, this.configurationService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_null_configuration_service() {
        new KafkaReactiveConsumerBuilderImpl(this.aopProxyProvider, null);
    }
}
