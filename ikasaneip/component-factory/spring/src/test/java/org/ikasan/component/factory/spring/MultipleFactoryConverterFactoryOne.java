package org.ikasan.component.factory.spring;

import org.ikasan.spec.component.factory.ComponentFactory;
import org.springframework.stereotype.Component;

@Component
public class MultipleFactoryConverterFactoryOne implements ComponentFactory<MultipleFactoryConverter>
{
    @Override public MultipleFactoryConverter create(String nameSuffix, String configPrefix, String factoryConfigPrefix)
    {
        return new MultipleFactoryConverter();
    }
}
