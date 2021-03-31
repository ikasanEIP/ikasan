package com.ikasan.sample.spring.boot.component.factory.custom.converter;

import org.ikasan.component.factory.spring.BaseComponentFactory;
import org.springframework.stereotype.Component;

@Component
public class StringToPayloadConverterComponentFactory extends BaseComponentFactory<StringToPayloadConverter> {

    @Override
    public StringToPayloadConverter create(String nameSuffix, String configPrefix, String factoryConfigPrefix) {
        StringToPayloadConverter stringToPayloadConverter = new StringToPayloadConverter();
        stringToPayloadConverter.setConfiguration(configuration(configPrefix,
            StringToPayloadConverterConfiguration.class));
        stringToPayloadConverter.setConfiguredResourceId(configuredResourceId(nameSuffix));
        return stringToPayloadConverter;
    }
}
