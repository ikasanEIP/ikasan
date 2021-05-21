package org.ikasan.component.factory.spring;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.springframework.stereotype.Component;

@Component
public class ConverterWithoutFactory implements Converter<String, String>
{
    @Override public String convert(String payload) throws TransformationException
    {
        return payload;
    }
}
