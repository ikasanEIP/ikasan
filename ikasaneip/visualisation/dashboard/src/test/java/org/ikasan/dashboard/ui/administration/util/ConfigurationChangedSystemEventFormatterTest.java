package org.ikasan.dashboard.ui.administration.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

public class ConfigurationChangedSystemEventFormatterTest {
    public static final String SYSTEM_EVENT = "Configuration Updated OldConfig [{\"configurationId\":\"murex-trade-tradeConsumer\"," +
        "\"description\":null,\"parameters\":[{\"id\":54,\"name\":\"autoContentConversion\",\"value\":true,\"description\":null}," +
        "{\"id\":55,\"name\":\"autoSplitBatch\",\"value\":true,\"description\":null},{\"id\":56,\"name\":\"batchMode\",\"value\":true,\"description\":null}" +
        ",{\"id\":57,\"name\":\"batchSize\",\"value\":0,\"description\":null},{\"id\":58,\"name\":\"cacheLevel\",\"value\":1,\"description\":null}" +
        ",{\"id\":59,\"name\":\"concurrentConsumers\",\"value\":1,\"description\":null},{\"id\":60,\"name\":\"connectionFactoryJndiProperties\",\"value\"" +
        ":{\"java.naming.security.principal\":\"\",\"java.naming.factory.initial\":\"org.apache.activemq.jndi.ActiveMQInitialContextFactory\"," +
        "\"java.naming.provider.url\":\"tcp://localhost:61616?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=-1&jms.clientIDPrefix=murex-trade-tradeConsumer\"," +
        "\"java.naming.security.credentials\":\"\"},\"description\":null},{\"id\":61,\"name\":\"connectionFactoryName\",\"value\":\"XAConnectionFactory\",\"description\":null}," +
        "{\"id\":62,\"name\":\"connectionFactoryPassword\",\"value\":null,\"description\":null},{\"id\":63,\"name\":\"connectionFactoryUsername\",\"value\":null,\"description\":null}," +
        "{\"id\":64,\"name\":\"destinationJndiName\",\"value\":\"dynamicQueues/com.caixa.bank.murex.out\",\"description\":null},{\"id\":65,\"name\":\"destinationJndiProperties\"," +
        "\"value\":{\"java.naming.security.principal\":\"\",\"java.naming.factory.initial\":\"org.apache.activemq.jndi.ActiveMQInitialContextFactory\"," +
        "\"java.naming.provider.url\":\"tcp://localhost:61616?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=-1&jms.clientIDPrefix=murex-trade-tradeConsumer\"," +
        "\"java.naming.security.credentials\":\"\"},\"description\":null},{\"id\":66,\"name\":\"durable\",\"value\":true,\"description\":null},{\"id\":67," +
        "\"name\":\"durableSubscriptionName\",\"value\":\"murex-trade-tradeConsumer\",\"description\":null},{\"id\":68,\"name\":\"maxConcurrentConsumers\"," +
        "\"value\":1,\"description\":null},{\"id\":69,\"name\":\"pubSubDomain\",\"value\":false,\"description\":null},{\"id\":70,\"name\":\"sessionAcknowledgeMode\"," +
        "\"value\":null,\"description\":null},{\"id\":71,\"name\":\"sessionTransacted\",\"value\":false,\"description\":null}]}] NewConfig " +
        "[{\"configurationId\":\"murex-trade-tradeConsumer\",\"description\":null,\"parameters\":[{\"id\":54,\"name\":\"autoContentConversion\"," +
        "\"value\":true,\"description\":null},{\"id\":55,\"name\":\"autoSplitBatch\",\"value\":true,\"description\":null},{\"id\":56,\"name\":\"batchMode\"," +
        "\"value\":true,\"description\":null},{\"id\":57,\"name\":\"batchSize\",\"value\":0,\"description\":null},{\"id\":58,\"name\":\"cacheLevel\"," +
        "\"value\":1,\"description\":null},{\"id\":59,\"name\":\"concurrentConsumers\",\"value\":1,\"description\":null},{\"id\":60,\"name\":\"connectionFactoryJndiProperties\"," +
        "\"value\":{\"java.naming.security.principal\":\"\",\"java.naming.factory.initial\":\"org.apache.activemq.jndi.ActiveMQInitialContextFactory\"," +
        "\"java.naming.provider.url\":\"tcp://localhost:61616?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=-1&jms.clientIDPrefix=murex-trade-tradeConsumer\"," +
        "\"java.naming.security.credentials\":\"\"},\"description\":null},{\"id\":61,\"name\":\"connectionFactoryName\",\"value\":\"XAConnectionFactory\",\"description\":null}," +
        "{\"id\":62,\"name\":\"connectionFactoryPassword\",\"value\":null,\"description\":null},{\"id\":63,\"name\":\"connectionFactoryUsername\",\"value\":null,\"description\":null}," +
        "{\"id\":64,\"name\":\"destinationJndiName\",\"value\":\"dynamicQueues/com.caixa.bank.murex.out\",\"description\":null},{\"id\":65,\"name\":\"destinationJndiProperties\"," +
        "\"value\":{\"java.naming.security.principal\":\"\",\"java.naming.factory.initial\":\"org.apache.activemq.jndi.ActiveMQInitialContextFactory\",\"java.naming.provider.url\":" +
        "\"tcp://localhost:61616?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=-1&jms.clientIDPrefix=murex-trade-tradeConsumer\",\"java.naming.security.credentials\":" +
        "\"\"},\"description\":null},{\"id\":66,\"name\":\"durable\",\"value\":true,\"description\":null},{\"id\":67,\"name\":\"durableSubscriptionName\",\"value\":" +
        "\"murex-trade-tradeConsumer\",\"description\":null},{\"id\":68,\"name\":\"maxConcurrentConsumers\",\"value\":1,\"description\":null},{\"id\":69,\"name\":" +
        "\"pubSubDomain\",\"value\":false,\"description\":null},{\"id\":70,\"name\":\"sessionAcknowledgeMode\",\"value\":null,\"description\":null},{\"id\":71,\"name\":" +
        "\"sessionTransacted\",\"value\":false,\"description\":null}]}]";

    public static final String SYSTEM_EVENT_2 = "Configuration Updated OldConfig [{\"configurationId\":\"murex-tradeTradeConsumerFlowexceptionBroker\",\"description\":null,\"parameters\":[{\"id\":19,\"name\":\"shouldThrowExclusionException\",\"value\":false,\"description\":null},{\"id\":20,\"name\":\"shouldThrowRecoveryException\",\"value\":false,\"description\":null},{\"id\":21,\"name\":\"shouldThrowStoppedInErrorException\",\"value\":false,\"description\":null},{\"id\":22,\"name\":\"testLong\",\"value\":7,\"description\":null}]}] NewConfig [{\"configurationId\":\"murex-tradeTradeConsumerFlowexceptionBroker\",\"description\":null,\"parameters\":[{\"id\":19,\"name\":\"shouldThrowExclusionException\",\"value\":false,\"description\":null},{\"id\":20,\"name\":\"shouldThrowRecoveryException\",\"value\":false,\"description\":null},{\"id\":21,\"name\":\"shouldThrowStoppedInErrorException\",\"value\":true,\"description\":null},{\"id\":22,\"name\":\"testLong\",\"value\":7,\"description\":null}]}]";
    @Test
    public void test() throws JsonProcessingException {
        System.out.println(ConfigurationChangedSystemEventFormatter.format(SYSTEM_EVENT_2));
    }
}
