package ${moduleBasePackage}.flow;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.annotation.Value;
<#--
    Entry point macro that initiates the recursive traversing of the flow graph and
     wireup the flow using the Ikasan builder classes
-->
<#macro navigateTransitions component>
        <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
        <#if instanceOf(component, "org.ikasan.module.builder.model.SingleTransition")>
            <@addSingleTransitionComponentToBuilder component/>
        </#if>
        <#if instanceOf(component, "org.ikasan.module.builder.model.MultiTransition")>
            <@addMultiTransitionComponentToBuilder component/>
        </#if>
</#macro>

<#--
    todo
-->
<#macro addRouteMethods component>
    <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
    <#if instanceOf(component, "org.ikasan.module.builder.model.SingleTransition")>
        <#if component.transition??>
            <@addRouteMethods component.transition />
        </#if>
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.MultiTransition")>
        <#if component.transitions??>
            <#list component.transitions as key, value >
                <@routeMethod component.name key value />
                <@addRouteMethods value />
            </#list>
        </#if>
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.SingleTransition")>
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
    private Route route${routerName?replace(" ", "")?cap_first}${key?replace(" ", "")?cap_first}(RouteBuilder routeBuilder) {
        return routeBuilder
        <#if instanceOf(component, "org.ikasan.module.builder.model.SingleTransition")>
            <@addSingleTransitionComponentToBuilder component/>
        </#if>
        <#if instanceOf(component, "org.ikasan.module.builder.model.MultiTransition")>
            <@addMultiTransitionComponentToBuilder component/>
        </#if>
    }

</#macro>

<#macro addSingleTransitionComponentToBuilder component>
    <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
    <#if instanceOf(component, "org.ikasan.module.builder.model.ConsumerComponent")>
        .consumer("${component.name}", componentFactory.getConsumer())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.ProducerComponent")>
        .producer("${component.name}", componentFactory.getProducer())
        .build();
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.BrokerComponent")>
        .broker("${component.name}", componentFactory.getBroker())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.ConverterComponent")>
        .converter("${component.name}", componentFactory.getConverter())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.SequencerComponent")>
        .sequencer("${component.name}", componentFactory.getSequencer())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.SplitterComponent")>
        .splitter("${component.name}", componentFactory.getSplitter())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.TranslatorComponent")>
        .translator("${component.name}", componentFactory.getTranslator())
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.FilterComponent")>
        .filter("${component.name}", componentFactory.getFilter())
    </#if>
    <#if component.transition??>
        <#if instanceOf(component.transition, "org.ikasan.module.builder.model.SingleTransition")>
            <@addSingleTransitionComponentToBuilder component.transition/>
        </#if>
        <#if instanceOf(component.transition, "org.ikasan.module.builder.model.MultiTransition")>
            <@addMultiTransitionComponentToBuilder component.transition/>
        </#if>
    </#if>
</#macro>

<#macro addMultiTransitionComponentToBuilder component>
    <#assign instanceOf = "org.ikasan.module.builder.template.InstanceOfMethod"?new()>
    <#if instanceOf(component, "org.ikasan.module.builder.model.MultiRecipientRouterComponent")>
        .multiRecipientRouter("${component.name}", componentFactory.getMultiRecipientRouter())
        <#list component.transitions as key, value >
            .when("${key}", route${component.name?replace(" ", "")?cap_first}${key?replace(" ", "")?cap_first}(builderFactory.getRouteBuilder()))
        </#list>
        .build();
    </#if>
    <#if instanceOf(component, "org.ikasan.module.builder.model.SingleRecipientRouterComponent")>
        .singleRecipientRouter("${component.name}", componentFactory.getSingleRecipientRouter())
        <#list component.transitions as key, value >
            .when("${key}", route${component.name?replace(" ", "")?cap_first}${key?replace(" ", "")?cap_first}(builderFactory.getRouteBuilder()))
        </#list>
        .build();
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

    /**
    * Create flow bean for flow ${name}.

    * @return the flow bean.
    */
    @Bean
    public Flow ${name?replace(" ", "")?cap_first}()
    {
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        return moduleBuilder.getFlowBuilder("${name}")
        <@navigateTransitions component=consumer/>
    }

<@addRouteMethods component=consumer/>
}
