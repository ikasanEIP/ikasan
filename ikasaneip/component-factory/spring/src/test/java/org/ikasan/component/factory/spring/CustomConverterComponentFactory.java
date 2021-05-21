package org.ikasan.component.factory.spring;

import org.ikasan.spec.component.factory.ComponentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CustomConverterComponentFactory extends BaseComponentFactory<CustomConverter>
{


    @Override
    public CustomConverter create(String nameSuffix, String configPrefix, String factoryConfigPrefix) {
        CustomConverter customConverter = new CustomConverter();
        customConverter.setConfiguration(configuration(configPrefix, CustomConverterConfiguration.class));
        CustomConverterComponentFactoryConfiguration factoryConfiguration =
            factoryConfiguration(factoryConfigPrefix, CustomConverterComponentFactoryConfiguration.class);
        customConverter.setFlowName(factoryConfiguration.getFlowName());
        customConverter.setConfiguredResourceId(configuredResourceId(nameSuffix));
        return customConverter;
    }
}
