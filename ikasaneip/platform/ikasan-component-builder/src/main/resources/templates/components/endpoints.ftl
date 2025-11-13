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

<#macro sftpConsumer component>
    return this.builderFactory.getComponentBuilder()
    .sftpConsumer()
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

<#macro ftpConsumer component>
    return this.builderFactory.getComponentBuilder()
    .ftpConsumer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>

<#macro ftpProducer component>
    return this.builderFactory.getComponentBuilder()
    .ftpProducer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>

<#macro fileConsumer component>
    return builderFactory.getComponentBuilder()
    .fileConsumer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>

<#macro eventGeneratingConsumer component>
    return builderFactory
    .getComponentBuilder()
    .eventGeneratingConsumer()
    .build();
</#macro>

<#macro unknownScheduledConsumer component>
    // cannot determine scheduled consumer for component ${component.name} so adding
    // vanilla ScheduledConsumer
    return builderFactory.getComponentBuilder().scheduledConsumer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>