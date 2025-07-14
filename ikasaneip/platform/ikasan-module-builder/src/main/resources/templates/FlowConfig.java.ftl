package org.ikasan.module.generated;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

<#macro navigateTransitions component>
        <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
        <#if instanceOf(component, "org.ikasan.module.builder.model.SingleTransition")>
            <@addComponentToBuilder component=component/>
            <#if component.transition??>
                <@navigateTransitions component.transition />
            </#if>
        </#if>
        <#if instanceOf(component, "org.ikasan.module.builder.model.MultiTransition")>
            multi
            <#if component.transitions??>
                <#list component.transitions as key, value >
                    <@navigateTransitions value />
                </#list>
            </#if>
        </#if>
</#macro>

<#macro addRouteMethod component>
    <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
    <#if instanceOf(component, "org.ikasan.module.builder.model.SingleTransition")>
        <#if component.transition??>
            <@addRouteMethod component.transition />
        </#if>
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.MultiTransition")>
        <#if component.transitions??>
            <#list component.transitions as key, value >
                <@routeMethod key value />
            </#list>
        </#if>
    </#if>
</#macro>

<#macro routeMethod key component>
    private Route route${key}(RouteBuilder routeBuilder) {
        return routeBuilder.build();
    }
</#macro>

<#macro addComponentToBuilder component>
    <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
    <#if instanceOf(component, "org.ikasan.module.builder.model.ConsumerComponent")>
        .consumer("${component.name}", componentFactory.getConsumer())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.ConsumerComponent")>
    </#if>
</#macro>

@Configuration
public class ${name?replace(" ", "")?cap_first}Config
{
    @Value("${"$"}{module.name}")
    private String moduleName;
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    @Bean
    public Flow ${name?replace(" ", "")?cap_first}()
    {
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        return moduleBuilder.getFlowBuilder("${name}")
        <@navigateTransitions component=consumer/>
    }

<@addRouteMethod component=consumer/>
}
