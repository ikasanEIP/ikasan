package com.ikasan.sample.spring.boot.component;

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.component.endpoint.Broker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComponentFactory {

<#list flows as flow>
    <#list flow.flowElements as component>
    <#if component.componentType == "org.ikasan.spec.component.endpoint.Consumer">
    @Bean
    public Consumer get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.endpoint.Producer">
    @Bean
    public Producer get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.filter.Filter">
    @Bean
    public Filter get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.transformation.Converter">
    @Bean
    public Converter get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.routing.MultiRecipientRouter">
    @Bean
    public MultiRecipientRouter get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.routing.SingleRecipientRouter">
    @Bean
    public SingleRecipientRouter get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.sequencing.Sequencer">
    @Bean
    public Sequencer get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.splitting.Splitter">
    @Bean
    public Splitter get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.transformation.Translator">
    @Bean
    public Translator get${component.componentName?replace(" ", "")}() {
        return null;
    }

    <#elseif component.componentType == "org.ikasan.spec.component.endpoint.Broker">
    @Bean
    public Broker get${component.componentName?replace(" ", "")}() {
        return null;
    }

    </#if>
    </#list>
</#list>
}
