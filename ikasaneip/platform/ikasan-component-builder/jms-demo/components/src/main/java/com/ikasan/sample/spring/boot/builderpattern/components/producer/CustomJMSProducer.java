package com.ikasan.sample.spring.boot.builderpattern.components.producer;

import org.ikasan.spec.component.endpoint.Producer;
    import org.ikasan.spec.configuration.ConfiguredResource;
    import com.ikasan.sample.spring.boot.builderpattern.components.producer.configuration.CustomJMSProducerConfiguration;

public class CustomJMSProducer implements Producer, ConfiguredResource<CustomJMSProducerConfiguration>  {

    private String configuredResourceId;
    private CustomJMSProducerConfiguration componentConfiguration;

    @Override
    public void invoke(Object payload) {
        // TODO: Implement custom logic for CustomJMSProducer invoke method
        // Note: The invoke method signature is based on the component type.
        //       You may need to adjust it based on the specific Ikasan interface.
        System.out.println("Invoking CustomJMSProducer");
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public CustomJMSProducerConfiguration getConfiguration() {
        return componentConfiguration;
    }

    @Override
    public void setConfiguration(CustomJMSProducerConfiguration componentConfiguration) {
        this.componentConfiguration = componentConfiguration;
    }
}