package org.ikasan.component.factory.spring.endpoint;

import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.component.factory.spring.BindConfigurationHelper;
import org.springframework.stereotype.Component;
import org.ikasan.spec.component.factory.ComponentFactory;

import java.util.Map;

@Component public class JmsConsumerComponentFactory extends JmsComponentFactory
    implements ComponentFactory<JmsContainerConsumer>
{
    @Override public JmsContainerConsumer create(String nameSuffix, String configPrefix, String factoryConfigPrefix)
    {
        SpringMessageConsumerConfiguration configuration = BindConfigurationHelper
            .createConfig(configPrefix, SpringMessageConsumerConfiguration.class, env);
        JmsConsumerComponentFactoryConfiguration factoryConfiguration = BindConfigurationHelper
            .createConfig(factoryConfigPrefix, JmsConsumerComponentFactoryConfiguration.class, env);
        String destination = factoryConfiguration.getDestination();
        Map<String, String> jndiProperties;
        boolean pubSub = false;
        String componentName = moduleName + "-" + nameSuffix;
        if (destination.startsWith("dynamicTopics") || destination.startsWith("/jms/topic"))
        {
            pubSub = true;
            jndiProperties = getJndiProperties(componentName, configPrefix, false);
        }
        else
        {
            jndiProperties = getJndiProperties(componentName, configPrefix, true);
        }
        JmsContainerConsumer jmsContainerConsumer = (JmsContainerConsumer) builderFactory.getComponentBuilder()
            .jmsConsumer().setDestinationJndiProperties(jndiProperties)
            .setConnectionFactoryJndiProperties(jndiProperties)
            .setConnectionFactoryName(configuration.getConnectionFactoryName())
            .setConnectionFactoryUsername(configuration.getConnectionFactoryUsername())
            .setConnectionFactoryPassword(configuration.getConnectionFactoryPassword())
            .setDestinationJndiName(factoryConfiguration.getDestination()).setDurable(true).setPubSubDomain(pubSub)
            .setDurableSubscriptionName(componentName)
            .setAutoContentConversion(factoryConfiguration.isAutoContentConversion())
            .setConfiguredResourceId(componentName).build();
        return jmsContainerConsumer;
    }
}
