package org.ikasan.component.converter.filetransfer;

import org.ikasan.filetransfer.Payload;
import org.ikasan.serialiser.model.JmsMapMessageDefaultImpl;
import org.ikasan.spec.component.transformation.TransformationException;
import org.junit.jupiter.api.Test;

import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by amajewski on 23/03/2018.
 */
class MapMessageToPayloadConverterTest
{
    MapMessageToPayloadConverter uut = new MapMessageToPayloadConverter();

    @Test
    void convertMessageWithContent() throws JMSException
    {
        MapMessage mapMessage = new JmsMapMessageDefaultImpl();
        mapMessage.setString("content", "TEST");
        mapMessage.setString("fileName", "myTestFile.log");
        Payload result = uut.convert(mapMessage);
        assertEquals("myTestFile.log", result.getAttribute("fileName"));
        assertEquals("TEST", new String(result.getContent()));
    }

    @Test
    void convertMessageWithContentIsByte() throws JMSException
    {
        MapMessage mapMessage = new JmsMapMessageDefaultImpl();
        mapMessage.setBytes("content", "TEST".getBytes());
        mapMessage.setString("fileName", "myTestFile.log");
        Payload result = uut.convert(mapMessage);
        assertEquals("myTestFile.log", result.getAttribute("fileName"));
        assertEquals("TEST", new String(result.getContent()));
    }

    @Test
    void convertMessageWithContentIsLong() throws JMSException
    {
        Throwable exception = assertThrows(TransformationException.class, () -> {
            MapMessage mapMessage = new JmsMapMessageDefaultImpl();
            mapMessage.setLong("content", 100L);
            mapMessage.setString("fileName", "myTestFile.log");
            Payload result = uut.convert(mapMessage);
        });
        assertTrue(exception.getMessage().contains("Message property [content] type is not supported"));
    }
}
