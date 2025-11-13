<#macro objectToXmlStringConverter component>
    return builderFactory.getComponentBuilder().objectToXmlStringConverter()
    .setConfiguration(configuration)
    .setObjectClass(Object.class)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>

<#macro xmlStringToObjectConverter component>
    return builderFactory.getComponentBuilder().xmlStringToObjectConverter()
    .setConfiguration(configuration)
    .setClassToBeBound(Object.class)
    .setConfiguredResourceId("${component.configurationId}")
    .build();
</#macro>
