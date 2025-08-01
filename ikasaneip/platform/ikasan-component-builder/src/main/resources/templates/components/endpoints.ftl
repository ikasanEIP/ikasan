<#macro jmsConsumer component>
ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

return builderFactory.getComponentBuilder().jmsConsumer()
.setConnectionFactory(consumerConnectionFactory)
.setDestinationJndiName("source")
.setAutoContentConversion(true)
.setConfiguredResourceId("${component.configurationId}")
.build();
</#macro>

<#macro jmsProducer component>
    ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

    return builderFactory.getComponentBuilder().jmsProducer()
    .setConnectionFactory(producerConnectionFactory)
    .setDestinationJndiName("target")
    .setConfiguredResourceId("jmsProducer")
    .build();
</#macro>