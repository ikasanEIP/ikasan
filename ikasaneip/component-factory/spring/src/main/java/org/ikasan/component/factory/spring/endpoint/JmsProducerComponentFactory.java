package org.ikasan.component.factory.spring.endpoint;

import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.factory.spring.BindConfigurationHelper;
import org.ikasan.spec.component.factory.ComponentFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JmsProducerComponentFactory extends JmsComponentFactory implements ComponentFactory<JmsTemplateProducer>
{



    @Override
    public JmsTemplateProducer create(String nameSuffix, String configPrefix, String factoryConfigPrefix) {
        SpringMessageConsumerConfiguration configuration = BindConfigurationHelper.createConfig(configPrefix,
            SpringMessageConsumerConfiguration.class, env);
        JmsConsumerComponentFactoryConfiguration factoryConfiguration  = BindConfigurationHelper.createConfig(
            factoryConfigPrefix,
            JmsConsumerComponentFactoryConfiguration.class, env);
        Map<String,String> jndiProperties;
        boolean pubSub = false;
        String componentName = moduleName + "-" + nameSuffix;
        jndiProperties = getJndiProperties(componentName, configPrefix, true );
        JmsTemplateProducer jmsTemplateProducer = (JmsTemplateProducer) builderFactory.getComponentBuilder().jmsProducer()
            .setDestinationJndiProperties(jndiProperties)
            .setConnectionFactoryJndiProperties(jndiProperties)
            .setConnectionFactoryName(configuration.getConnectionFactoryName())
            .setConnectionFactoryUsername(configuration.getConnectionFactoryUsername())
            .setConnectionFactoryPassword(configuration.getConnectionFactoryPassword())
            .setDestinationJndiName(factoryConfiguration.getDestination())
            .setPubSubDomain(pubSub)
            .setDeliveryPersistent(true)
            .setSessionTransacted(true)
            .setMessageIdEnabled(true)
            .setConfiguredResourceId(componentName)
            .build();
        return jmsTemplateProducer;
    }



}