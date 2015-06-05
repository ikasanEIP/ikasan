/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.serialiser.service;

import java.io.File;
import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

import org.ikasan.serialiser.converter.JmsMapMessageConverter;
import org.ikasan.serialiser.converter.JmsTextMessageConverter;
import org.ikasan.serialiser.converter.JobExecutionContextConverter;
import org.ikasan.serialiser.model.JmsMapMessageDefaultImpl;
import org.ikasan.serialiser.model.JmsTextMessageDefaultImpl;
import org.ikasan.serialiser.model.JobExecutionContextDefaultImpl;
import org.ikasan.spec.serialiser.Converter;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.impl.JobExecutionContextImpl;

import com.esotericsoftware.kryo.Serializer;

/**
 * Test class for SerialiserFactoryKryoImpl.
 * 
 * @author Ikasan Development Team
 */
public class GenericKryoToBytesSerialiserTest
{
    /** one serialiser service should provide all serialiser instances */

    /**
     * Test
     */
    @Test
    public void test_getSerialiser_for_string_successful()
    {
    	HashMap<Class,Serializer> serialisers = new HashMap<Class,Serializer>();
    	HashMap<Class,Converter> converters = new HashMap<Class,Converter>();
    	converters.put(TextMessage.class, new JmsTextMessageConverter());
    	converters.put(MapMessage.class, new JmsMapMessageConverter());
    	
    	SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl(serialisers, converters);
    	
        // object for serialise/deserialise test
        String str = new String("test");

        // get a serialiser
        Serialiser<String,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(str);

        // deserialise it
        String restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(str));
    }

    /**
     * Test
     */
    @Test
    public void test_getSerialiser_for_integer_successful()
    {
    	HashMap<Class,Serializer> serialisers = new HashMap<Class,Serializer>();
    	HashMap<Class,Converter> converters = new HashMap<Class,Converter>();
    	converters.put(TextMessage.class, new JmsTextMessageConverter());
    	converters.put(MapMessage.class, new JmsMapMessageConverter());
    	
    	SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl(serialisers, converters);    	
        // object for serialise/deserialise test
        Integer myInt = new Integer(10);

        // get a serialiser
        Serialiser<Integer,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(myInt);

        // deserialise it
        Integer restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(myInt));
    }

    /**
     * Test
     */
    @Test
    public void test_getSerialiser_for_primitiveClass_successful()
    {
    	HashMap<Class,Serializer> serialisers = new HashMap<Class,Serializer>();
    	HashMap<Class,Converter> converters = new HashMap<Class,Converter>();
    	converters.put(TextMessage.class, new JmsTextMessageConverter());
    	converters.put(MapMessage.class, new JmsMapMessageConverter());
    	
    	SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl(serialisers, converters);
    	
        // object for serialise/deserialise test
        PrimitiveClass primitiveClass = new PrimitiveClass();

        // get a serialiser
        Serialiser<PrimitiveClass,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(primitiveClass);

        // deserialise it
        PrimitiveClass restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(primitiveClass));
    }
    
    /**
     * Test
     * @throws JMSException 
     */
    @Test
    public void test_getSerialiser_for_textMessage_successful() throws JMSException
    {
    	HashMap<Class,Serializer> serialisers = new HashMap<Class,Serializer>();
    	HashMap<Class,Converter> converters = new HashMap<Class,Converter>();
    	converters.put(TextMessage.class, new JmsTextMessageConverter());
    	converters.put(MapMessage.class, new JmsMapMessageConverter());
    	SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl(serialisers, converters);
    	
        // object for serialise/deserialise test
        JmsTextMessageDefaultImpl message = new JmsTextMessageDefaultImpl();
        message.setText("This is some text");

        // get a serialiser
        Serialiser<JmsTextMessageDefaultImpl,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(message);

        // deserialise it
        JmsTextMessageDefaultImpl restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(message));
    }
    
    /**
     * Test
     * @throws JMSException 
     */
    @Test
    public void test_getSerialiser_for_mapMessage_successful() throws JMSException
    {
    	HashMap<Class,Serializer> serialisers = new HashMap<Class,Serializer>();
    	HashMap<Class,Converter> converters = new HashMap<Class,Converter>();
    	converters.put(TextMessage.class, new JmsTextMessageConverter());
    	converters.put(MapMessage.class, new JmsMapMessageConverter());
    	SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl(serialisers, converters);
    	
        // object for serialise/deserialise test
        JmsMapMessageDefaultImpl message = new JmsMapMessageDefaultImpl();
        message.setString("testString1", "string 1");
        message.setString("testString1", "string 1");
        message.setBoolean("Boolean1", true);
        message.setBoolean("Boolean2", false);

        // get a serialiser
        Serialiser<JmsMapMessageDefaultImpl,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(message);

        // deserialise it
        JmsMapMessageDefaultImpl restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(message));
    }
    
    /**
     * Test
     * @throws JMSException 
     */
    @Test
    public void test_getSerialiser_for_jobContextMessage_successful() throws JMSException
    {
    	HashMap<Class,Serializer> serialisers = new HashMap<Class,Serializer>();
    	HashMap<Class,Converter> converters = new HashMap<Class,Converter>();
    	converters.put(TextMessage.class, new JmsTextMessageConverter());
    	converters.put(MapMessage.class, new JmsMapMessageConverter());
    	converters.put(JobExecutionContextImpl.class, new JobExecutionContextConverter());
    	SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl(serialisers, converters);
    	
        // object for serialise/deserialise test
    	JobExecutionContextDefaultImpl message = new JobExecutionContextDefaultImpl();
        

        // get a serialiser
        Serialiser<JobExecutionContextDefaultImpl,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(message);

        // deserialise it
        JobExecutionContextDefaultImpl restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(message));
    }
    
    /**
     * Test
     * @throws JMSException 
     */
    @Test
    public void test_getSerialiser_for_file_successful() throws JMSException
    {
    	HashMap<Class,Serializer> serialisers = new HashMap<Class,Serializer>();
    	HashMap<Class,Converter> converters = new HashMap<Class,Converter>();
    	converters.put(TextMessage.class, new JmsTextMessageConverter());
    	converters.put(MapMessage.class, new JmsMapMessageConverter());

    	SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl(serialisers, converters);
    	
        // object for serialise/deserialise test
    	File file = new File(".");
        

        // get a serialiser
        Serialiser<File,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(file);

        // deserialise it
        File restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(file));
    }
    
    /**
     * Test
     * @throws JMSException 
     */
    @Test
    public void test_getSerialiser_for_hashmap_successful() throws JMSException
    {
    	HashMap<Class,Serializer> serialisers = new HashMap<Class,Serializer>();
    	HashMap<Class,Converter> converters = new HashMap<Class,Converter>();
    	converters.put(TextMessage.class, new JmsTextMessageConverter());
    	converters.put(MapMessage.class, new JmsMapMessageConverter());

    	SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl(serialisers, converters);
    	
        // object for serialise/deserialise test
    	HashMap<String, Object> hashMap = new HashMap<String, Object>();
    	hashMap.put("string 1", new Integer(1));
    	hashMap.put("string 2", new Long(1));
    	hashMap.put("string 3", new String("1"));
    	hashMap.put("string 4", new Integer(1));
    	hashMap.put("string 5", new Character('1'));

        // get a serialiser
        Serialiser<HashMap<String, Object>,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(hashMap);

        // deserialise it
        HashMap<String, Object> restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(hashMap));
    }
}
