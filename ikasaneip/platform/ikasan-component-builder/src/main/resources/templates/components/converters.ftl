<#macro objectToXmlStringConverter component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    return builderFactory.getComponentBuilder().objectToXmlStringConverter()
    .setConfiguration(configuration)
    .setObjectClass(Object.class)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>

<#macro xmlStringToObjectConverter component>
    <#if component.isConfigured && component.configurationMetaData??>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}() {
    </#if>
    return builderFactory.getComponentBuilder().xmlStringToObjectConverter()
    .setConfiguration(configuration)
    .setClassToBeBound(Object.class)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
    }
</#macro>
