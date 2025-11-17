package ${moduleBasePackage}.flow;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.RouteBuilder;
import org.ikasan.builder.Route;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;

import ${moduleBasePackage}.component.ComponentFactory;
<#--
    Entry point macro that initiates the recursive traversing of the flow graph and
     wireup the flow using the Ikasan builder classes
-->
<#macro navigateTransitions component>
        <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
        <#if instanceOf(component, "org.ikasan.module.builder.model.module.SingleTransition")>
            <@addSingleTransitionComponentToBuilder component "true"/>
        </#if>
        <#if instanceOf(component, "org.ikasan.module.builder.model.module.MultiTransition")>
            <@addMultiTransitionComponentToBuilder component/>
        </#if>
</#macro>

<#--
    todo
-->
<#macro addRouteMethods component>
    <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.SingleTransition")>
        <#if component.transition??>
            <@addRouteMethods component.transition />
        </#if>
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.MultiTransition")>
        <#if component.transitions??>
            <#list component.transitions as key, value >
                <@routeMethod component.name key value />
                <@addRouteMethods value />
            </#list>
        </#if>
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.SingleTransition")>
        <#if component.transitions??>
            <#list component.transitions as key, value >
                <@routeMethod component.name key value />
                <@addRouteMethods value />
            </#list>
        </#if>
    </#if>
</#macro>

<#--
    todo
-->
<#macro routeMethod routerName key component>
    /**
    * Route for path ${key} for router ${routerName}.
    *
    * @param routeBuilder the RouteBuilder used to configure the route.
    * @return the configured Route for path ${key} for router ${routerName}.
    */
    private Route route${routerName?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}${key?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}(RouteBuilder routeBuilder) {
        return routeBuilder
        <#if instanceOf(component, "org.ikasan.module.builder.model.module.SingleTransition")>
            <@addSingleTransitionComponentToBuilder component "false"/>
        </#if>
        <#if instanceOf(component, "org.ikasan.module.builder.model.module.MultiTransition")>
            <@addMultiTransitionComponentToBuilder component/>
        </#if>
    }

</#macro>

<#--
    todo
-->
<#macro addSingleTransitionComponentToBuilder component primaryRoute>
    <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.ConsumerComponent")>
        .consumer("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.ProducerComponent")>
        .producer("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())<#if primaryRoute == "false">;</#if>
        <#if primaryRoute == "true">
        .build();
        </#if>
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.BrokerComponent")>
        .broker("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.ConverterComponent")>
        .converter("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.SequencerComponent")>
        .sequencer("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.SplitterComponent")>
        .splitter("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.TranslatorComponent")>
        .translator("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.FilterComponent")>
        .filter("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
    </#if>
    <#if component.transition??>
        <#if instanceOf(component.transition, "org.ikasan.module.builder.model.module.SingleTransition")>
            <@addSingleTransitionComponentToBuilder component.transition primaryRoute/>
        </#if>
        <#if instanceOf(component.transition, "org.ikasan.module.builder.model.module.MultiTransition")>
            <@addMultiTransitionComponentToBuilder component.transition/>
        </#if>
    </#if>
</#macro>

<#--
    todo
-->
<#macro addMultiTransitionComponentToBuilder component>
    <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.MultiRecipientRouterComponent")>
        .multiRecipientRouter("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
        <#list component.transitions as key, value >
            .when("${key}", route${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}${key?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}(builderFactory.getRouteBuilder()))
        </#list>
        .build();
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.module.SingleRecipientRouterComponent")>
        .singleRecipientRouter("${component.name}", componentFactory.get${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}())
        <#list component.transitions as key, value >
            .when("${key}", route${component.name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}${key?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}(builderFactory.getRouteBuilder()))
        </#list>
        .build();
    </#if>
</#macro>
@Configuration
public class ${name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}Config
{
    @Value("${"$"}{module.name}")
    private String moduleName;
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    /**
    * Create flow bean for flow ${name}.

    * @return the flow bean.
    */
    @Bean(name = "${name?replace(" ", "")?replace(",", "")?replace("-", "_")?uncap_first}")
    public Flow ${name?replace(" ", "")?replace(",", "")?replace("-", "_")?cap_first}()
    {
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        return moduleBuilder.getFlowBuilder("${name}")
        <@navigateTransitions component=consumer/>
    }

<@addRouteMethods component=consumer/>
}
