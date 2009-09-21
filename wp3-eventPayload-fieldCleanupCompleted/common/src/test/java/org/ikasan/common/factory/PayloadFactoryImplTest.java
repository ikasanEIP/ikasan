package org.ikasan.common.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.ikasan.common.Payload;
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
        byte[] content = new byte[]{1,2,3};
        
        Payload newPayload = payloadFactoryImpl.newPayload(id,  content);


        assertEquals("Payload id should be that which was passed into the newInstance method", id, newPayload.getId());
        assertTrue("Payload content should be that which was passed into the newInstance method", Arrays.equals(content, newPayload.getContent()) );

    }

   
}
