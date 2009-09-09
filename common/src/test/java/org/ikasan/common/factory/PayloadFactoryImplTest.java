package org.ikasan.common.factory;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.common.component.Spec;
import org.junit.Test;

/**
 * JUnit test class for the PayloadFactoryImpl class 
 */
public class PayloadFactoryImplTest
{
	
	
	private PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl();
	


    /**
     * Test the new Payload (String, String, String) 
     */
    @Test
    public void testNewPayload()
    {

    	String id = "id";
        String name = "name";
        Spec spec = Spec.BYTE_ZIP;
        String srcSystem = "srcSystem";
        byte[] content = "content".getBytes();
        
        Payload newPayload = payloadFactoryImpl.newPayload(id, name, spec, srcSystem, content);
        
        assertEquals("Payload name should be that which was passed into the newInstance method", name, newPayload.getName());
        assertEquals("Payload srcSystem should be that which was passed into the newInstance method", srcSystem, newPayload.getSrcSystem());
        assertEquals("Payload spec should be that which was passed into the newInstance method", spec, newPayload.getSpec());
        assertEquals("Payload id should be that which was passed into the newInstance method", id, newPayload.getId());
        assertTrue("Payload content should be that which was passed into the newInstance method", Arrays.equals(content, newPayload.getContent()) );
    }

   



   
}
