package org.ikasan.common.factory;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.common.component.Spec;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * JUnit test class for the PayloadFactoryImpl class 
 */
public class PayloadFactoryImplTest
{
    /** A Payload class */
    private Class<? extends Payload> payloadClass;
    
    /**
     * Constructor
     */
    public PayloadFactoryImplTest()
    {
        super();
        payloadClass=DefaultPayload.class;
    }

    /**
     * Test the constructor
     */
    @Test
    public void testPayloadFactoryImpl()
    {
        Class<Payload> ploadClass = Payload.class;
        
        PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl(ploadClass, null);
        assertEquals("classname passed to constructor should be set on the class", ploadClass, payloadFactoryImpl.getPayloadImplClass());
    }

    /**
     * Test the new Payload (String, String, String) 
     */
    @Test
    public void testNewPayloadStringStringString()
    {
        
        

        
        
        PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl(payloadClass, null);
        String name = "name";
        String spec = "spec";
        String srcSystem = "srcSystem";
        
        Payload newPayload = payloadFactoryImpl.newPayload(name, spec, srcSystem);
        
        assertEquals("Payload name should be that which was passed into the newInstance method", name, newPayload.getName());
        assertEquals("Payload srcSystem should be that which was passed into the newInstance method", srcSystem, newPayload.getSrcSystem());
        assertEquals("Payload spec should be that which was passed into the newInstance method", spec, newPayload.getSpec());
    }

    /**
     * Test the newPayload (String, Spec, String) 
     */
    @Test
    public void testNewPayloadStringSpecString()
    {
        PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl(payloadClass, null);
        String name = "name";
        String spec = Spec.TEXT_HTML.toString();
        String srcSystem = "srcSystem";
        
        Payload newPayload = payloadFactoryImpl.newPayload(name, Spec.TEXT_HTML, srcSystem);
        
        assertEquals("Payload name should be that which was passed into the newInstance method", name, newPayload.getName());
        assertEquals("Payload srcSystem should be that which was passed into the newInstance method", srcSystem, newPayload.getSrcSystem());
        assertEquals("Payload spec should be that which was passed into the newInstance method", spec, newPayload.getSpec());

    }

    /**
     * Test the newPayload (Payload) 
     */
    @Test
    public void testNewPayloadPayload()
    {
        PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl(payloadClass, null);

        final String name = "name";
        final String spec = "spec";
        final String srcSystem = "srcSystem";
        
        Mockery mockery = new Mockery();
        final Payload payload = mockery.mock(Payload.class);
        
        mockery.checking(new Expectations()
        {
            {
               one(payload).getName();will(returnValue(name));
               one(payload).getSpec();will(returnValue(spec));
               one(payload).getSrcSystem();will(returnValue(srcSystem));
               one(payload).getNoNamespaceSchemaLocation();will(returnValue(""));
               one(payload).getSchemaInstanceNSURI();will(returnValue(""));
               one(payload).getId();will(returnValue(""));
               one(payload).getPriority();
               one(payload).getTimestamp();
               one(payload).getTimestampFormat();
               one(payload).getTimezone();
               one(payload).getContent();
               one(payload).getEncoding();
               one(payload).getFormat();
               one(payload).getCharset();
               one(payload).getSize();
               one(payload).getChecksumAlg();
               one(payload).getChecksum();
               one(payload).getTargetSystems();
               one(payload).getProcessIds();
            }
        });
        
        Payload newPayload = payloadFactoryImpl.newPayload(payload);
        
        assertEquals("Payload name should be that which was passed into the newInstance method", name, newPayload.getName());
        assertEquals("Payload srcSystem should be that which was passed into the newInstance method", srcSystem, newPayload.getSrcSystem());
        assertEquals("Payload spec should be that which was passed into the newInstance method", spec, newPayload.getSpec());

        
    }

    /**
     * Test the newPayload (String, Spec, String, Byte[]) 
     */
    @Test
    public void testNewPayloadStringSpecStringByteArray()
    {
        PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl(payloadClass, null);
        String name = "name";
        String spec = Spec.TEXT_HTML.toString();
        String srcSystem = "srcSystem";
        byte[] content = new byte[]{1,2,3};
        
        Payload newPayload = payloadFactoryImpl.newPayload(name, Spec.TEXT_HTML, srcSystem, content);
        
        assertEquals("Payload name should be that which was passed into the newInstance method", name, newPayload.getName());
        assertEquals("Payload srcSystem should be that which was passed into the newInstance method", srcSystem, newPayload.getSrcSystem());
        assertEquals("Payload spec should be that which was passed into the newInstance method", spec, newPayload.getSpec());
        assertTrue("Payload content should be that which was passed into the newInstance method", Arrays.equals(content, newPayload.getContent()) );

    }

    /**
     * Test the newPayload (String, String, String, Byte[]) 
     */
    @Test
    public void testNewPayloadStringStringStringByteArray()
    {
        PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl(payloadClass, null);
        String name = "name";
        String spec = "spec";
        String srcSystem = "srcSystem";
        byte[] content = new byte[]{1,2,3};
        
        Payload newPayload = payloadFactoryImpl.newPayload(name, spec, srcSystem, content);
        
        assertEquals("Payload name should be that which was passed into the newInstance method", name, newPayload.getName());
        assertEquals("Payload srcSystem should be that which was passed into the newInstance method", srcSystem, newPayload.getSrcSystem());
        assertEquals("Payload spec should be that which was passed into the newInstance method", spec, newPayload.getSpec());
        assertTrue("Payload content should be that which was passed into the newInstance method", Arrays.equals(content, newPayload.getContent()) );

    }

    /**
     * Test the setting of the implementation class 
     */
    @Test
    public void testSetPayloadImplClass()
    {
        PayloadFactoryImpl payloadFactoryImpl = new PayloadFactoryImpl(payloadClass, null);
        assertEquals("before setPayloadImplClass, class should equal that passed in on constructor", DefaultPayload.class, payloadFactoryImpl.getPayloadImplClass());
        Class<Payload> someOtherClass = Payload.class;
        payloadFactoryImpl.setPayloadImplClass(someOtherClass);
        assertEquals("after setPayloadImplClass, class should equal that passed into set method", someOtherClass, payloadFactoryImpl.getPayloadImplClass());
    }
}
