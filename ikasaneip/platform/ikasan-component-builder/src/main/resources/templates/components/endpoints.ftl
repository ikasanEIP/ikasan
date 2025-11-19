<#macro jmsConsumer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    ConnectionFactory consumerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

    return this.builderFactory.getComponentBuilder().jmsConsumer()
    .setConnectionFactory(consumerConnectionFactory)
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>

<#macro jmsProducer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    ConnectionFactory producerConnectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);

    return this.builderFactory.getComponentBuilder().jmsProducer()
    .setConnectionFactory(producerConnectionFactory)
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>

<#macro sftpConsumer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    return this.builderFactory.getComponentBuilder()
    .sftpConsumer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>

<#macro sftpProducer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    return this.builderFactory.getComponentBuilder()
    .sftpProducer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>

<#macro ftpConsumer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    return this.builderFactory.getComponentBuilder()
    .ftpConsumer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>

<#macro ftpProducer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    return this.builderFactory.getComponentBuilder()
    .ftpProducer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>

<#macro fileConsumer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    return builderFactory.getComponentBuilder()
    .fileConsumer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>

<#macro eventGeneratingConsumer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    return builderFactory
    .getComponentBuilder()
    .eventGeneratingConsumer()
    .build();
    }
</#macro>

<#macro unknownScheduledConsumer component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    // cannot determine scheduled consumer for component ${component.name} so adding
    // vanilla ScheduledConsumer
    return builderFactory.getComponentBuilder().scheduledConsumer()
    .setConfiguration(configuration)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>