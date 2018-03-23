package org.ikasan.component.converter.filetransfer;

import org.ikasan.filetransfer.Payload;
import org.ikasan.serialiser.model.JmsMapMessageDefaultImpl;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import static org.junit.Assert.assertEquals;

/**
 * Created by amajewski on 23/03/2018.
 */
public class MapMessageToPayloadConverterTest
{
    MapMessageToPayloadConverter uut = new MapMessageToPayloadConverter();

    @Test
    public void convertMessageWithContent() throws JMSException
    {

        MapMessage mapMessage = new JmsMapMessageDefaultImpl();
        mapMessage.setString("content","TEST");
        mapMessage.setString("fileName","myTestFile.log");

        Payload result = uut.convert(mapMessage);

        assertEquals("myTestFile.log",result.getAttribute("fileName"));
        assertEquals("TEST", new String(result.getContent()));


    }

    @Test
    public void convertMessageWithContentIsByte() throws JMSException
    {

        MapMessage mapMessage = new JmsMapMessageDefaultImpl();
        mapMessage.setBytes("content","TEST".getBytes());
        mapMessage.setString("fileName","myTestFile.log");

        Payload result = uut.convert(mapMessage);

        assertEquals("myTestFile.log",result.getAttribute("fileName"));
        assertEquals("TEST", new String(result.getContent()));


    }
}
