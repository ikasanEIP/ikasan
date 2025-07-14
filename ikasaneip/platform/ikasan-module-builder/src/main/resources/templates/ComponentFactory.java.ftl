package org.ikasan.module.generated;

import org.ikasan.spec.component.consumer.Consumer;
import org.ikasan.spec.component.producer.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComponentFactory {

<#list flows as flow>
    <#list flow.components as component>
    <#if component.type == "consumer">
    @Bean
    public Consumer get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    <#elseif component.type == "producer">
    @Bean
    public Producer get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    <#elseif component.type == "filter">
    @Bean
    public Filter get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    <#elseif component.type == "converter">
    @Bean
    public Converter get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    <#elseif component.type == "multiRecipientRouter">
    @Bean
    public MultiRecipientRouter get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    <#elseif component.type == "singleRecipientRouter">
    @Bean
    public SingleRecipientRouter get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    <#elseif component.type == "sequencer">
    @Bean
    public Sequencer get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    <#elseif component.type == "splitter">
    @Bean
    public Splitter get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    <#elseif component.type == "translator">
    @Bean
    public Translator get${component.name?replace(" ", "")}() {
        return new ${component.className}();
    }

    </#if>
    </#list>
</#list>
}
