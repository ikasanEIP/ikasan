package com.sample.spring.component.custom;

import com.ikasan.component.factory.BindConfigurationHelper;
import com.ikasan.component.factory.ComponentFactory;
import com.ikasan.component.factory.JmsConsumerComponentFactoryConfiguration;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CustomConverterFactory implements ComponentFactory<CustomConverter> {

    @Autowired
    protected Environment env;

    @Value("${module.name}")
    private String moduleName;

    @Override
    public CustomConverter create(String nameSuffix, String configPrefix, String factoryConfigPrefix) {
        CustomConverterConfiguration configuration = BindConfigurationHelper.createConfig(configPrefix,
            CustomConverterConfiguration.class, env);
        CustomConverter customConverter = new CustomConverter();
        customConverter.setConfiguration(configuration);
        customConverter.setConfiguredResourceId(moduleName + "-" + nameSuffix);
        return customConverter;
    }
}
