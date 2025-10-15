<#macro jmsConsumer component>
    ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

    return this.builderFactory.getComponentBuilder().jmsConsumer()
    .setConnectionFactory(consumerConnectionFactory)
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>

<#macro jmsProducer component>
    ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

    return this.builderFactory.getComponentBuilder().jmsProducer()
    .setConnectionFactory(producerConnectionFactory)
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>

<#macro sftpProducer component>
    return this.builderFactory.getComponentBuilder()
    .sftpProducer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>

<#macro sftpConsumer component>
    return this.builderFactory.getComponentBuilder()
    .sftpConsumer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>