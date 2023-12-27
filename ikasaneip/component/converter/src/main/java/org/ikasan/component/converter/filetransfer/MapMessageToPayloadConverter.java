package org.ikasan.component.converter.filetransfer;

import org.ikasan.filetransfer.FilePayloadAttributeNames;
import org.ikasan.filetransfer.Payload;
import org.ikasan.filetransfer.component.DefaultPayload;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;

import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;

/**
 * Created by amajewski on 26/12/2017.
 */
public class MapMessageToPayloadConverter
    implements Converter<MapMessage, Payload>, ConfiguredResource<MapMessageToPayloadConverterConfiguration>
{
    private String configuredResourceId;

    private MapMessageToPayloadConverterConfiguration configuration = new MapMessageToPayloadConverterConfiguration();

    @Override
    public Payload convert(MapMessage message) throws TransformationException {
        Payload payload = null;
        try {
            String id = message.getString(configuration.getIdAttributeName());
            Object contentObject = message.getObject(configuration.getContentAttributeName());
            if (contentObject != null) {
                if (contentObject instanceof String string) {
                    payload = new DefaultPayload(id, string.getBytes());
                } else if (contentObject instanceof byte[] bytes) {
                    payload = new DefaultPayload(id, bytes);
                } else {
                    throw new TransformationException(
                        "Message property [" + configuration.getContentAttributeName() + "] type is not supported.");
                }
            } else {
                throw new TransformationException(
                    "Message property [" + configuration.getContentAttributeName() + "] is empty.");
            }
        } catch (JMSException e) {
            throw new TransformationException(
                "Error encountered when processing JMS message. Unable to extract file contents.", e);
        }
        try {
            String fileName = message.getString(configuration.getFileNameAttributeName());
            String referencePayload = message.getString("referencePayload");
            String relativePath = message.getString("relativePath");
            payload.setAttribute("fileName", fileName);
            payload.setAttribute("referencePayload", referencePayload);
            payload.setAttribute(FilePayloadAttributeNames.RELATIVE_PATH, relativePath);
            return payload;
        } catch (JMSException e) {
            return payload;
        }
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
    public MapMessageToPayloadConverterConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(MapMessageToPayloadConverterConfiguration configuration) {
        this.configuration = configuration;
    }
}
