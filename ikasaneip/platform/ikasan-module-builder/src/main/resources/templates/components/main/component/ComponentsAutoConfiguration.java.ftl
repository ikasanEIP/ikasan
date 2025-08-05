<#import "/components/endpoints.ftl" as endpoints>
package ${packageName};

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.ikasan.builder.BuilderFactory;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
<#list components as component>
    import ${component.componentType};
    import ${component.implementingClass};
    <#if component.implementingClass == "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer">
        import org.apache.activemq.ActiveMQXAConnectionFactory;
        import jakarta.jms.ConnectionFactory;
    <#elseif component.implementingClass == "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer">
        import org.apache.activemq.ActiveMQXAConnectionFactory;
        import jakarta.jms.ConnectionFactory;
    </#if>
</#list>
<#list componentConfigurations as componentConfiguration>
    import ${componentConfiguration.packageName}.${componentConfiguration.className};
</#list>
@Configuration
public class ComponentsAutoConfiguration {
@Resource
private BuilderFactory builderFactory;
@Value("${"$"}{jms.provider.url}")
private String brokerUrl;
<#list components as component>
    /**
    * Create the ${component.name?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first} bean.
    *
    * @return the ${component.name?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first} bean.
    */
    @Bean("${component.name?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first}")
    <#if component.isConfigured >
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first}() {
    </#if>
    <#if component.implementingClass == "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer">
        <@endpoints.jmsConsumer component/>
    <#elseif component.implementingClass == "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer">
        <@endpoints.jmsProducer component/>
    <#else>
        <#if component.isConfigured >
            ${component.className} component = new ${component.className}();
            component.setConfiguredResourceId("${component.configurationId}");
            component.setConfiguration(configuration);

            return component;
        <#else>
            return new ${component.className}();
        </#if>
    </#if>
    }

</#list>

<#list componentConfigurations as componentConfiguration>
    /**
    * Create the ${componentConfiguration.className?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first} bean.
    *
    * @return the ${componentConfiguration.className?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first} bean.
    */
    @Bean("${componentConfiguration.componentName?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first}Configuration")
    @ConfigurationProperties(prefix = "${componentConfiguration.componentName?replace(" ", "")?replace("[^A-Za-z0-9]", "")?lower_case}")
    public ${componentConfiguration.className?replace(" ", "")?replace("[^A-Za-z0-9]", "")} ${componentConfiguration.componentName?replace(" ", "")?replace("[^A-Za-z0-9]", "")?uncap_first}Configuration() {
        return new ${componentConfiguration.className}();
    }

</#list>
}
