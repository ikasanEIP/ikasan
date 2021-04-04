package com.ikasan.sample.spring.boot.component.factory.custom.converter;

import org.ikasan.filetransfer.Payload;
import org.ikasan.filetransfer.component.DefaultPayload;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.nio.charset.StandardCharsets;

public class PayloadToStringConverter implements Converter<Payload, String>{

    private DateTimeFormatter dateTimeFormatter =  DateTimeFormat.forPattern("yyyyMMdHHmmss");

    public String convert(Payload payload) throws TransformationException {
        return new String(payload.getContent(), StandardCharsets.UTF_8);
    }

}
