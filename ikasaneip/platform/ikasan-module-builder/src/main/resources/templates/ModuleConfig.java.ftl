package org.ikasan.module.generated;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ModuleConfig
{
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    @Bean
    public Module getModule()
    {

        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("${moduleMetaData.name}");

<#list moduleMetaData.flows as flow>
        Flow ${flow.name?replace(" ", "")?uncap_first} = moduleBuilder.getFlowBuilder("${flow.name}")
<#--            .withDescription("${flow.description}")-->
            <#list flow.flowElements as component>
                <#if component.componentType == "org.ikasan.spec.component.endpoint.Consumer">
            .consumer("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "org.ikasan.spec.component.endpoint.Producer">
            .producer("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "org.ikasan.spec.component.endpoint.Broker">
            .filter("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "org.ikasan.spec.component.transformation.Converter">
            .converter("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "org.ikasan.spec.component.routing.MultiRecipientRouter">
            .multiRecipientRouter("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "org.ikasan.spec.component.routing.SingleRecipientRouter">
            .singleRecipientRouter("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "sequencer">
            .sequencer("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "splitter">
            .splitter("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "translator">
            .translator("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                <#elseif component.componentType == "org.ikasan.spec.component.filter.Filter">
            .filter("${component.componentName}", componentFactory.get${component.componentName?replace(" ", "")}())
                </#if>
            </#list>

</#list>
        Module module = moduleBuilder.withDescription("${moduleMetaData.description}")
<#list moduleMetaData.flows as flow>
            .addFlow(${flow.name})
</#list>
            .build();

        return module;
    }
}
