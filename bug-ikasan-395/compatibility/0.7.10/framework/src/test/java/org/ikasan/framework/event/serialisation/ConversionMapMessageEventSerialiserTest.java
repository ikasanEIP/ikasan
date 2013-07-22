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
package org.ikasan.framework.event.serialisation;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

public class ConversionMapMessageEventSerialiserTest
{
	/**
	 * Class under test
	 */
	private ConversionMapMessageEventSerialiser conversionJmsMessageEventSerialiser;
	
	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
	private PayloadFactory payloadFactory = mockery.mock(PayloadFactory.class);
	
	final String moduleName = "moduleName";
	final String componentName = "componentName";
    
	final Payload payload1 = mockery.mock(Payload.class, "payload1");
	final Payload payload2 = mockery.mock(Payload.class, "payload2");
	
	private List<Payload> payloads = null;
	
	final String payload1Prefix = ConversionMapMessageEventSerialiser.PAYLOAD_PREFIX+0;
	final String payload2Prefix = ConversionMapMessageEventSerialiser.PAYLOAD_PREFIX+1;
    final String payload1OldPrefix = ConversionMapMessageEventSerialiser.OLD_PAYLOAD_PREFIX+0;
    final String payload2OldPrefix = ConversionMapMessageEventSerialiser.OLD_PAYLOAD_PREFIX+1;
	
	//payload content
	final String payload1ContentKey = payload1Prefix + ConversionMapMessageEventSerialiser.PAYLOAD_CONTENT_SUFFIX;
	final String payload2ContentKey = payload2Prefix + ConversionMapMessageEventSerialiser.PAYLOAD_CONTENT_SUFFIX;
    final String payload1OldContentKey = payload1OldPrefix + ConversionMapMessageEventSerialiser.OLD_PAYLOAD_CONTENT_SUFFIX;
    final String payload2OldContentKey = payload2OldPrefix + ConversionMapMessageEventSerialiser.OLD_PAYLOAD_CONTENT_SUFFIX;
	final byte[] payload1Content = "payload1Content".getBytes();
	final byte[] payload2Content = "payload2Content".getBytes();
	
	//payload id
	final String payload1IdKey = payload1Prefix + ConversionMapMessageEventSerialiser.PAYLOAD_ID_SUFFIX;
	final String payload2IdKey = payload2Prefix + ConversionMapMessageEventSerialiser.PAYLOAD_ID_SUFFIX;
    final String payload1OldIdKey = payload1OldPrefix + ConversionMapMessageEventSerialiser.OLD_PAYLOAD_ID_SUFFIX;
    final String payload2OldIdKey = payload2OldPrefix + ConversionMapMessageEventSerialiser.OLD_PAYLOAD_ID_SUFFIX;
	final String payload1Id = "payload1Id";
	final String payload2Id = "payload2Id";
	
	//payload attributes
	final String colourPayloadAttributeName = "COLOUR";
	final String huePayloadAttributeName = "HUE";
	final String texturePayloadAttributeName = "TEXTURE";
	
	final List<String> payload1AttributeNames = new ArrayList<String>();
	final List<String> payload2AttributeNames = new ArrayList<String>();
	
	final String payload1ColourAttributeKey = payload1Prefix + ConversionMapMessageEventSerialiser.ATTRIBUTE_PREFIX+colourPayloadAttributeName;
	final String payload1HueAttributeKey = payload1Prefix + ConversionMapMessageEventSerialiser.ATTRIBUTE_PREFIX+huePayloadAttributeName;
	final String payload1TextureAttributeKey = payload1Prefix + ConversionMapMessageEventSerialiser.ATTRIBUTE_PREFIX+texturePayloadAttributeName;
    final String payload1OldColourAttributeKey = payload1OldPrefix + "_" + colourPayloadAttributeName;
    final String payload1OldHueAttributeKey = payload1OldPrefix + "_" + huePayloadAttributeName;
    final String payload1OldTextureAttributeKey = payload1OldPrefix + "_" + texturePayloadAttributeName;

	final String payload2ColourAttributeKey = payload2Prefix + ConversionMapMessageEventSerialiser.ATTRIBUTE_PREFIX+colourPayloadAttributeName;
	final String payload2HueAttributeKey = payload2Prefix + ConversionMapMessageEventSerialiser.ATTRIBUTE_PREFIX+huePayloadAttributeName;
	final String payload2TextureAttributeKey = payload2Prefix + ConversionMapMessageEventSerialiser.ATTRIBUTE_PREFIX+texturePayloadAttributeName;
    final String payload2OldColourAttributeKey = payload2OldPrefix + "_" + colourPayloadAttributeName;
    final String payload2OldHueAttributeKey = payload2OldPrefix + "_" + huePayloadAttributeName;
    final String payload2OldTextureAttributeKey = payload2OldPrefix + "_" + texturePayloadAttributeName;
	
	final String payload1ColourAttributeValue = "blue";
	final String payload1HueAttributeValue = "light";
	final String payload1TextureAttributeValue = "graded";

	final String payload2ColourAttributeValue = "green";
	final String payload2HueAttributeValue = "dark";
	
	//event id
	final String eventIdKey = ConversionMapMessageEventSerialiser.EVENT_FIELD_ID;
	final String eventId = "eventId";
	
	//event priority
	final String eventPriorityKey = ConversionMapMessageEventSerialiser.EVENT_FIELD_PRIORITY;
	final int priority = 8;

	//event timestamp
	final String eventTimestampKey = ConversionMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP;
	final Date timestamp = new Date();
	
	@Before
	public void setup()
	{
		conversionJmsMessageEventSerialiser = new ConversionMapMessageEventSerialiser();
		conversionJmsMessageEventSerialiser.setPayloadFactory(payloadFactory);
		payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		//payload 1 knows about colour, hue and texture
		payload1AttributeNames.add(colourPayloadAttributeName);
		payload1AttributeNames.add(huePayloadAttributeName);
		payload1AttributeNames.add(texturePayloadAttributeName);
		
		//payload 2 just knows about colour and hue 
		payload2AttributeNames.add(colourPayloadAttributeName);
		payload2AttributeNames.add(huePayloadAttributeName);
	}
	
    /**
     * Tests the successful deserialisation for latest named properties
     * 
     * @throws EventDeserialisationException
     * @throws JMSException
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFromMapMessage() throws EventDeserialisationException, JMSException
    {
    	final MapMessage mapMessage = mockery.mock(MapMessage.class);
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	//event fields
    	map.put(ConversionMapMessageEventSerialiser.EVENT_FIELD_ID, eventId);
    	map.put(ConversionMapMessageEventSerialiser.EVENT_FIELD_PRIORITY, priority);
    	map.put(ConversionMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP, timestamp);
    	
    	//payload content
    	map.put(payload1ContentKey, payload1Content);
    	map.put(payload2ContentKey, payload2Content);
    	
    	//payload id
    	map.put(payload1IdKey, payload1Id);
    	map.put(payload2IdKey, payload2Id);
    	
    	//payload attributes
		map.put(payload1ColourAttributeKey, payload1ColourAttributeValue);
    	map.put(payload1HueAttributeKey, payload1HueAttributeValue);
    	map.put(payload1TextureAttributeKey, payload1TextureAttributeValue);

    	map.put(payload2Prefix + ConversionMapMessageEventSerialiser.ATTRIBUTE_PREFIX+colourPayloadAttributeName, payload2ColourAttributeValue);
    	map.put(payload2Prefix + ConversionMapMessageEventSerialiser.ATTRIBUTE_PREFIX+huePayloadAttributeName, payload2HueAttributeValue);

    	final Enumeration mapNamesEnumeration = new Vector(map.keySet()).elements();
    	
    	mockery.checking(new Expectations()
        {
            {
            	one(mapMessage).getMapNames();will(returnValue(mapNamesEnumeration));
            	
            	//event fields
            	one(mapMessage).getString(ConversionMapMessageEventSerialiser.EVENT_FIELD_ID);will(returnValue(eventId));
            	one(mapMessage).getInt(ConversionMapMessageEventSerialiser.EVENT_FIELD_PRIORITY);will(returnValue(priority));
            	one(mapMessage).getLong(ConversionMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP);will(returnValue(timestamp.getTime()));
            	
            	//payload content
            	one(mapMessage).getBytes(payload1ContentKey);will(returnValue(payload1Content));
            	one(mapMessage).getBytes(payload2ContentKey);will(returnValue(payload2Content));

           	
            	//payload id
            	one(mapMessage).getString(payload1IdKey);will(returnValue(payload1Id));
            	one(mapMessage).getString(payload2IdKey);will(returnValue(payload2Id));
            	

            	//payload attributes
            	one(mapMessage).getString(payload1ColourAttributeKey);will(returnValue(payload1ColourAttributeValue));
            	one(mapMessage).getString(payload1HueAttributeKey);will(returnValue(payload1HueAttributeValue));
            	one(mapMessage).getString(payload1TextureAttributeKey);will(returnValue(payload1TextureAttributeValue));

            	one(mapMessage).getString(payload2ColourAttributeKey);will(returnValue(payload2ColourAttributeValue));
            	one(mapMessage).getString(payload2HueAttributeKey);will(returnValue(payload2HueAttributeValue));
            	
                one(payloadFactory).newPayload(payload1Id, payload1Content);will(returnValue(payload1));
                one(payloadFactory).newPayload(payload2Id, payload2Content);will(returnValue(payload2));

                one(payload1).setAttribute(colourPayloadAttributeName, payload1ColourAttributeValue);
                one(payload1).setAttribute(huePayloadAttributeName, payload1HueAttributeValue);
                one(payload1).setAttribute(texturePayloadAttributeName, payload1TextureAttributeValue);

                one(payload2).setAttribute(colourPayloadAttributeName, payload2ColourAttributeValue);
                one(payload2).setAttribute(huePayloadAttributeName, payload2HueAttributeValue);
            }
        });
    	
    	Event event = conversionJmsMessageEventSerialiser.fromMessage(mapMessage, moduleName, componentName);
    	
    	mockery.assertIsSatisfied();
  	
    	
    	Assert.assertEquals("event should have id, obtained from appropriate field in mapMessage", eventId, event.getId());
    	Assert.assertEquals("event should have priority, obtained from appropriate field in mapMessage", priority, event.getPriority());
    	Assert.assertEquals("event should have timestamp, obtained from appropriate field in mapMessage", timestamp, event.getTimestamp());
    	
    	//check the payloads are present
    	Assert.assertEquals("event should have payloads as produced by payloadFactory", event.getPayloads(), payloads);
    }
    
    /**
     * Tests the successful deserialisation for 0.7.x named properties
     * 
     * @throws EventDeserialisationException
     * @throws JMSException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFromMapMessage_0_7_x() throws EventDeserialisationException, JMSException
    {
        final MapMessage mapMessage = mockery.mock(MapMessage.class);
        
        Map<String, Object> map = new HashMap<String, Object>();
        //event fields
        map.put(ConversionMapMessageEventSerialiser.ENVELOPE_ID, eventId);
        map.put(ConversionMapMessageEventSerialiser.ENVELOPE_PRIORITY, priority);
        map.put(ConversionMapMessageEventSerialiser.ENVELOPE_TIMESTAMP, timestamp);
        
        //payload content
        map.put(payload1OldContentKey, payload1Content);
        map.put(payload2OldContentKey, payload2Content);
        
        //payload id
        map.put(payload1OldIdKey, payload1Id);
        map.put(payload2OldIdKey, payload2Id);
        
        //payload attributes
        map.put(payload1OldColourAttributeKey, payload1ColourAttributeValue);
        map.put(payload1OldHueAttributeKey, payload1HueAttributeValue);
        map.put(payload1OldTextureAttributeKey, payload1TextureAttributeValue);

        map.put(payload2OldPrefix + "_" + colourPayloadAttributeName, payload2ColourAttributeValue);
        map.put(payload2OldPrefix + "_" + huePayloadAttributeName, payload2HueAttributeValue);

        final Enumeration mapNamesEnumeration = new Vector(map.keySet()).elements();
        
        mockery.checking(new Expectations()
        {
            {
                one(mapMessage).getMapNames();will(returnValue(mapNamesEnumeration));
                
                //event fields
                one(mapMessage).getString(ConversionMapMessageEventSerialiser.ENVELOPE_ID);will(returnValue(eventId));
                one(mapMessage).getInt(ConversionMapMessageEventSerialiser.ENVELOPE_PRIORITY);will(returnValue(priority));
                one(mapMessage).getLong(ConversionMapMessageEventSerialiser.ENVELOPE_TIMESTAMP);will(returnValue(timestamp.getTime()));
                
                //payload content
                one(mapMessage).getBytes(payload1OldContentKey);will(returnValue(payload1Content));
                one(mapMessage).getBytes(payload2OldContentKey);will(returnValue(payload2Content));

            
                //payload id
                one(mapMessage).getString(payload1OldIdKey);will(returnValue(payload1Id));
                one(mapMessage).getString(payload2OldIdKey);will(returnValue(payload2Id));
                

                //payload attributes
                one(mapMessage).getString(payload1OldColourAttributeKey);will(returnValue(payload1ColourAttributeValue));
                one(mapMessage).getString(payload1OldHueAttributeKey);will(returnValue(payload1HueAttributeValue));
                one(mapMessage).getString(payload1OldTextureAttributeKey);will(returnValue(payload1TextureAttributeValue));

                one(mapMessage).getString(payload2OldColourAttributeKey);will(returnValue(payload2ColourAttributeValue));
                one(mapMessage).getString(payload2OldHueAttributeKey);will(returnValue(payload2HueAttributeValue));
                
                one(payloadFactory).newPayload(payload1Id, payload1Content);will(returnValue(payload1));
                one(payloadFactory).newPayload(payload2Id, payload2Content);will(returnValue(payload2));

                one(payload1).setAttribute(colourPayloadAttributeName, payload1ColourAttributeValue);
                one(payload1).setAttribute(huePayloadAttributeName, payload1HueAttributeValue);
                one(payload1).setAttribute(texturePayloadAttributeName, payload1TextureAttributeValue);

                one(payload2).setAttribute(colourPayloadAttributeName, payload2ColourAttributeValue);
                one(payload2).setAttribute(huePayloadAttributeName, payload2HueAttributeValue);
            }
        });
        
        Event event = conversionJmsMessageEventSerialiser.fromMessage(mapMessage, moduleName, componentName);
        
        mockery.assertIsSatisfied();
        
        Assert.assertEquals("event should have id, obtained from appropriate field in mapMessage", eventId, event.getId());
        Assert.assertEquals("event should have priority, obtained from appropriate field in mapMessage", priority, event.getPriority());
        Assert.assertEquals("event should have timestamp, obtained from appropriate field in mapMessage", timestamp, event.getTimestamp());
        
        //check the payloads are present
        Assert.assertEquals("event should have payloads as produced by payloadFactory", event.getPayloads(), payloads);
    }
    
    /**
     * Tests that encountering an unknown Payload field in a MapMessage will cause an EventDeserialisationException during fromMapMessage
     * 
     * @throws EventDeserialisationException
     * @throws JMSException
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFromMapMessage_willThrowEventDeserialisationException_forUnknownPayloadField() throws EventDeserialisationException, JMSException
    {
    	final MapMessage mapMessage = mockery.mock(MapMessage.class);
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	//event fields
    	String unknownFieldName = "unknownFieldName";
		map.put(payload1Prefix+"_"+unknownFieldName, "unknownFieldValue");

    	final Enumeration mapNamesEnumeration = new Vector(map.keySet()).elements();
    	
    	mockery.checking(new Expectations()
        {
            {
            	one(mapMessage).getMapNames();will(returnValue(mapNamesEnumeration));
            }
        });
    	
    	try
    	{
    		conversionJmsMessageEventSerialiser.fromMessage(mapMessage, moduleName, componentName);
    		Assert.fail("EventDeserialisationException should have been thrown for MapMessage containing unknown field name");
    	} 
    	catch (EventDeserialisationException eventDeserialisationException)
    	{
    		Assert.assertTrue("exception message should reference problematic field name", eventDeserialisationException.getMessage().indexOf(unknownFieldName)>-1);
    	}
    	mockery.assertIsSatisfied();
    }

}
