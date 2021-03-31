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

public class StringToPayloadConverter implements Converter<String, DefaultPayload>,
    ConfiguredResource<StringToPayloadConverterConfiguration> {

    private String configuredResourceId;

    private StringToPayloadConverterConfiguration configuration;

    private DateTimeFormatter dateTimeFormatter =  DateTimeFormat.forPattern("yyyyMMdHHmmss");

    @Override
    public DefaultPayload convert(String content) throws TransformationException {
        DefaultPayload payload = new DefaultPayload(content.hashCode() + "",
            content.getBytes(StandardCharsets.UTF_8));
        payload.setAttribute("fileName", this.getConfiguration().getFileNamePrefix() +
            dateTimeFormatter.print(new DateTime()) + ".xml");
        return payload;
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
    public StringToPayloadConverterConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(StringToPayloadConverterConfiguration configuration) {
        this.configuration = configuration;
    }
}
