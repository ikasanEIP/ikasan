<#import "/components/endpoints.ftl" as endpoints>
<#import "/components/converters.ftl" as converters>
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

    <#assign constructorMetaData = component.constructorMetaData?first!null>

    /**
    * Create the ${component.name?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return the ${component.name?replace(" ", "")?replace(",", "")?uncap_first} bean.
    */
    @Bean("${component.name?replace(" ", "")?replace(",", "")?uncap_first}")
    <#if component.isConfigured >
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(@Qualifier("${component.name?replace(" ", "")?replace(",", "")?uncap_first}Configuration") ${component.configurationMetaData.configurationClassName} configuration<#if constructorMetaData?? && constructorMetaData.constructorArguments?? && constructorMetaData.constructorArguments?has_content>, <#list constructorMetaData.constructorArguments as item>${item.type} ${item.name}<#sep>, </#list></#if>) {
    <#else>
        public ${component.componentTypeClassName} ${component.name?replace(" ", "")?replace(",", "")?uncap_first}(<#if constructorMetaData?? && constructorMetaData.constructorArguments??><#list constructorMetaData.constructorArguments as item>${item.type} ${item.name}<#sep>, </#list></#if>) {
    </#if>
    <#if component.implementingClass == "org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer">
        <@endpoints.jmsConsumer component/>
    <#elseif component.implementingClass == "org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer">
        <@endpoints.jmsProducer component/>
    <#elseif component.implementingClass == "org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer">
        <#-- For SchduledConsumers we need to delegate to the messageProviderClass to get context -->
        <#if component.messageProviderClass == "org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider">
            <@endpoints.fileConsumer component/>
        <#elseif component.messageProviderClass == "org.ikasan.endpoint.sftp.consumer.SftpMessageProvider">
            <@endpoints.sftpConsumer component/>
        <#elseif component.messageProviderClass == "org.ikasan.endpoint.ftp.consumer.FtpMessageProvider">
            <@endpoints.ftpConsumer component/>
        <#else>
            <@endpoints.unknownScheduledConsumer component/>
        </#if>
    <#elseif component.implementingClass == "org.ikasan.endpoint.sftp.producer.SftpProducer">
        <@endpoints.sftpProducer component/>
    <#elseif component.implementingClass == "org.ikasan.endpoint.ftp.producer.FtpProducer">
        <@endpoints.ftpProducer component/>
    <#elseif component.implementingClass == "org.ikasan.component.endpoint.consumer.EventGeneratingConsumer">
        <@endpoints.eventGeneratingConsumer component/>
    <#elseif component.implementingClass == "org.ikasan.component.converter.xml.XmlStringToObjectConverter">
        <@converters.xmlStringToObjectConverter component/>
    <#elseif component.implementingClass == "org.ikasan.component.converter.xml.ObjectToXMLStringConverter">
        <@converters.objectToXmlStringConverter component/>
    <#else>
        <#if component.isConfigured >
            ${component.className} component = new ${component.className}(<#if constructorMetaData?? && constructorMetaData.constructorArguments??><#list constructorMetaData.constructorArguments as item>${item.name}<#sep>, </#list></#if>);
            component.setConfiguredResourceId("${component.configurationId}");
            component.setConfiguration(configuration);

            return component;
        <#else>
            return new ${component.className}(<#if constructorMetaData?? && constructorMetaData.constructorArguments??><#list constructorMetaData.constructorArguments as item>${item.name}<#sep>, </#list></#if>);
        </#if>
    </#if>
    }

</#list>

<#list componentConfigurations as componentConfiguration>
    /**
    * Create the ${componentConfiguration.className?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return the ${componentConfiguration.className?replace(" ", "")?replace(",", "")?uncap_first} bean.
    */
    @Bean("${componentConfiguration.componentName?replace(" ", "")?replace(",", "")?uncap_first}Configuration")
    @ConfigurationProperties(prefix = "${componentConfiguration.componentName?replace(" ", "")?replace(",", "")?lower_case}")
    public ${componentConfiguration.className?replace(" ", "")?replace(",", "")} ${componentConfiguration.componentName?replace(" ", "")?replace(",", "")?uncap_first}Configuration() {
        return new ${componentConfiguration.className}();
    }

</#list>
}
