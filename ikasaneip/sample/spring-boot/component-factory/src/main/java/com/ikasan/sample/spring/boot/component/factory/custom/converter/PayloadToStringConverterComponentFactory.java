package com.ikasan.sample.spring.boot.component.factory.custom.converter;

import org.ikasan.component.factory.spring.BaseComponentFactory;
import org.ikasan.spec.component.factory.ComponentFactory;
import org.springframework.stereotype.Component;

@Component
public class PayloadToStringConverterComponentFactory implements ComponentFactory<PayloadToStringConverter> {

    @Override
    public PayloadToStringConverter create(String nameSuffix, String configPrefix, String factoryConfigPrefix) {
        return new PayloadToStringConverter();
    }
}
