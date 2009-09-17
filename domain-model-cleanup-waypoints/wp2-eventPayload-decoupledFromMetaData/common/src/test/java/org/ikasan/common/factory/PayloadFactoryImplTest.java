package org.ikasan.common.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.junit.Test;

/**
 * JUnit test class for the PayloadFactoryImpl class 
 */
public class PayloadFactoryImplTest
{



    @Test
    public void testNewPayloadStringSpecStringByteArray()
    {
        PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl();
        String id = "id";
        String name = "name";
        Spec spec = Spec.TEXT_HTML;
        String srcSystem = "srcSystem";
        byte[] content = new byte[]{1,2,3};
        
        Payload newPayload = payloadFactoryImpl.newPayload(id, name, Spec.TEXT_HTML, srcSystem, content);


        assertEquals("Payload id should be that which was passed into the newInstance method", id, newPayload.getId());
        assertEquals("Payload name should be that which was passed into the newInstance method", name, newPayload.getName());
        assertEquals("Payload srcSystem should be that which was passed into the newInstance method", srcSystem, newPayload.getSrcSystem());
        assertEquals("Payload spec should be that which was passed into the newInstance method", spec, newPayload.getSpec());
        assertTrue("Payload content should be that which was passed into the newInstance method", Arrays.equals(content, newPayload.getContent()) );

    }

   
}
