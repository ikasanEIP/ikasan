package ${classPackage};

import org.ikasan.spec.component.splitting.Splitter;
import java.util.List;
<#if isConfigured>
    import org.ikasan.spec.configuration.ConfiguredResource;
    import ${configurationMetaData.configurationPackageName}.${configurationMetaData.configurationClassName};
</#if>
<#if parameterizedType??>
<#list parameterizedType.typeParameters as typeParam>
    <#if typeParam.type?contains(".")>
        import ${typeParam.type};
    </#if>
</#list>
</#if>

public class ${className}<#list parameterizedType.typeParameters as typeParam>
    <#if !typeParam.type?contains(".")>
         <${typeParam.type}>
    </#if>
</#list> implements Splitter<#if parameterizedType??><<#list parameterizedType.typeParameters as typeParam><#if typeParam.name=="SOURCE">${typeParam.parameterClass}</#if></#list>, <#list parameterizedType.typeParameters as typeParam><#if typeParam.name=="TARGET">${typeParam.parameterClass}</#if></#list>></#if><#if isConfigured> , ConfiguredResource<${configurationMetaData.configurationClassName}> </#if> {

<#if isConfigured>
    private String configuredResourceId;
    private ${configurationMetaData.configurationClassName} componentConfiguration;
</#if>

    @Override
    public <#if parameterizedType??>List<<#list parameterizedType.typeParameters as typeParam><#if typeParam.name=="TARGET">${typeParam.parameterClass}</#if></#list>><#else>List<Object></#if> invoke(<#if parameterizedType??><#list parameterizedType.typeParameters as typeParam><#if typeParam.name=="SOURCE">${typeParam.parameterClass}</#if></#list><#else>Object</#if> payload) {
        // TODO: Implement custom logic for ${className} invoke method
        // Note: The invoke method signature is based on the component type.
        //       You may need to adjust it based on the specific Ikasan interface.
        System.out.println("Invoking ${className}");
        return null;
    }

<#if isConfigured>
    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public ${configurationMetaData.configurationClassName} getConfiguration() {
        return componentConfiguration;
    }

    @Override
    public void setConfiguration(${configurationMetaData.configurationClassName} componentConfiguration) {
        this.componentConfiguration = componentConfiguration;
    }
</#if>
}