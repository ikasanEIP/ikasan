/*
 * $Id: 
 * $URL:
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
import org.junit.Test;

public class DefaultMapMessageEventSerialiserTest extends JmsMessageEventSerialiserTest{

	/**
	 * Class under test
	 */
	private DefaultMapMessageEventSerialiser defaultJmsMessageEventSerialiser;
	
	
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
	
	final String payload1Prefix = DefaultMapMessageEventSerialiser.PAYLOAD_PREFIX+0;
	final String payload2Prefix = DefaultMapMessageEventSerialiser.PAYLOAD_PREFIX+1;
	
	//payload content
	final String payload1ContentKey = payload1Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_CONTENT_SUFFIX;
	final String payload2ContentKey = payload2Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_CONTENT_SUFFIX;
	final byte[] payload1Content = "payload1Content".getBytes();
	final byte[] payload2Content = "payload2Content".getBytes();
	
	
	
	//payload id
	final String payload1IdKey = payload1Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_ID_SUFFIX;
	final String payload2IdKey = payload2Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_ID_SUFFIX;
	final String payload1Id = "payload1Id";
	final String payload2Id = "payload2Id";	
	
	
	//payload attributes
	final String colourPayloadAttributeName = "COLOUR";
	final String huePayloadAttributeName = "HUE";
	final String texturePayloadAttributeName = "TEXTURE";
	

	final List<String> payload1AttributeNames = new ArrayList<String>();
	final List<String> payload2AttributeNames = new ArrayList<String>();
	
	
	final String payload1ColourAttributeKey = payload1Prefix + DefaultMapMessageEventSerialiser.ATTRIBUTE_PREFIX+colourPayloadAttributeName;
	final String payload1HueAttributeKey = payload1Prefix + DefaultMapMessageEventSerialiser.ATTRIBUTE_PREFIX+huePayloadAttributeName;
	final String payload1TextureAttributeKey = payload1Prefix + DefaultMapMessageEventSerialiser.ATTRIBUTE_PREFIX+texturePayloadAttributeName;

	final String payload2ColourAttributeKey = payload2Prefix + DefaultMapMessageEventSerialiser.ATTRIBUTE_PREFIX+colourPayloadAttributeName;
	final String payload2HueAttributeKey = payload2Prefix + DefaultMapMessageEventSerialiser.ATTRIBUTE_PREFIX+huePayloadAttributeName;
	final String payload2TextureAttributeKey = payload2Prefix + DefaultMapMessageEventSerialiser.ATTRIBUTE_PREFIX+texturePayloadAttributeName;

	
	final String payload1ColourAttributeValue = "blue";
	final String payload1HueAttributeValue = "light";
	final String payload1TextureAttributeValue = "graded";

	final String payload2ColourAttributeValue = "green";
	final String payload2HueAttributeValue = "dark";
	
	//event id
	final String eventIdKey = DefaultMapMessageEventSerialiser.EVENT_FIELD_ID;
	final String eventId = "eventId";
	
	//event priority
	final String eventPriorityKey = DefaultMapMessageEventSerialiser.EVENT_FIELD_PRIORITY;
	final int priority = 8;

	//event timestamp
	final String eventTimestampKey = DefaultMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP;
	final long timestamp = 1000l;
	

	
	
	public DefaultMapMessageEventSerialiserTest(){
		defaultJmsMessageEventSerialiser = new DefaultMapMessageEventSerialiser();
		defaultJmsMessageEventSerialiser.setPayloadFactory(payloadFactory);
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
     * Tests the successful deserialisation
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
    	map.put(DefaultMapMessageEventSerialiser.EVENT_FIELD_ID, eventId);
    	map.put(DefaultMapMessageEventSerialiser.EVENT_FIELD_PRIORITY, priority);
    	map.put(DefaultMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP, timestamp);
    	
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

    	map.put(payload2Prefix + DefaultMapMessageEventSerialiser.ATTRIBUTE_PREFIX+colourPayloadAttributeName, payload2ColourAttributeValue);
    	map.put(payload2Prefix + DefaultMapMessageEventSerialiser.ATTRIBUTE_PREFIX+huePayloadAttributeName, payload2HueAttributeValue);

    	final Enumeration mapNamesEnumeration = new Vector(map.keySet()).elements();

    	
    	
    	//final List<String> mappedNames = new ArrayList<String>();

    	
    	mockery.checking(new Expectations()
        {
            {
            	one(mapMessage).getMapNames();will(returnValue(mapNamesEnumeration));
            	
            	//event fields
            	one(mapMessage).getString(DefaultMapMessageEventSerialiser.EVENT_FIELD_ID);will(returnValue(eventId));
            	one(mapMessage).getInt(DefaultMapMessageEventSerialiser.EVENT_FIELD_PRIORITY);will(returnValue(priority));
            	one(mapMessage).getLong(DefaultMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP);will(returnValue(timestamp));
            	
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
    	
    	Event event = defaultJmsMessageEventSerialiser.fromMessage(mapMessage, moduleName, componentName);
    	
    	mockery.assertIsSatisfied();
  	
    	
    	Assert.assertEquals("event should have id, obtained from appropriate field in mapMessage", eventId, event.getId());
    	Assert.assertEquals("event should have priority, obtained from appropriate field in mapMessage", priority, event.getPriority());
    	Assert.assertEquals("event should have timestamp, obtained from appropriate field in mapMessage", timestamp, event.getTimestamp());
    	
    	//check the payloads are present
    	Assert.assertEquals("event should have payloads as produced by payloadFactory", event.getPayloads(), payloads);

    }
    
    
    /**
     * Tests that encountering an unknown Event field in a MapMessage will cause an EventDeserialisationException during fromMapMessage
     * 
     * @throws EventDeserialisationException
     * @throws JMSException
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFromMapMessage_willThrowEventDeserialisationException_forUnknownEventField() throws EventDeserialisationException, JMSException
    {
    	final MapMessage mapMessage = mockery.mock(MapMessage.class);
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	//event fields
    	String unknownFieldName = "unknownFieldName";
		map.put(unknownFieldName, "unknownFieldValue");
    	

    	final Enumeration mapNamesEnumeration = new Vector(map.keySet()).elements();


    	
    	mockery.checking(new Expectations()
        {
            {
            	one(mapMessage).getMapNames();will(returnValue(mapNamesEnumeration));
            }
        });
    	
    	try{
    		defaultJmsMessageEventSerialiser.fromMessage(mapMessage, moduleName, componentName);
    		Assert.fail("EventDeserialisationException should have been thrown for MapMessage containing unknown field name");
    	} catch (EventDeserialisationException eventDeserialisationException){
    		Assert.assertTrue("exception message should reference problematic field name", eventDeserialisationException.getMessage().indexOf(unknownFieldName)>-1);
    	}
    	mockery.assertIsSatisfied();
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
    	
    	try{
    		defaultJmsMessageEventSerialiser.fromMessage(mapMessage, moduleName, componentName);
    		Assert.fail("EventDeserialisationException should have been thrown for MapMessage containing unknown field name");
    	} catch (EventDeserialisationException eventDeserialisationException){
    		Assert.assertTrue("exception message should reference problematic field name", eventDeserialisationException.getMessage().indexOf(unknownFieldName)>-1);
    	}
    	mockery.assertIsSatisfied();
    }
	@Test
	public void testToMapMessage() throws JMSException {
		final Event event = mockery.mock(Event.class);

		final Session session = mockery.mock(Session.class);
		final MapMessage mapMessage = mockery.mock(MapMessage.class);

		
        mockery.checking(new Expectations()
        {
            {
            	one(session).createMapMessage();will(returnValue(mapMessage));
            	
                one(event).getPayloads();will(returnValue(payloads));
                
                //payload content
                one(payload1).getContent();will(returnValue(payload1Content));
                one(mapMessage).setBytes(payload1ContentKey, payload1Content);

                one(payload2).getContent();will(returnValue(payload2Content));
                one(mapMessage).setBytes(payload2ContentKey, payload2Content);
                
                
                
                //payload id
                one(payload1).getId();will(returnValue(payload1Id));
                one(mapMessage).setString(payload1IdKey, payload1Id);

                one(payload2).getId();will(returnValue(payload2Id));
                one(mapMessage).setString(payload2IdKey, payload2Id);
                

                
                //payload attributes
                one(payload1).getAttributeNames();will(returnValue(payload1AttributeNames));
                one(payload2).getAttributeNames();will(returnValue(payload2AttributeNames));
                
                one(payload1).getAttribute(colourPayloadAttributeName);will(returnValue(payload1ColourAttributeValue));
                one(payload1).getAttribute(huePayloadAttributeName);will(returnValue(payload1HueAttributeValue));
                one(payload1).getAttribute(texturePayloadAttributeName);will(returnValue(payload1TextureAttributeValue));
                one(mapMessage).setString(payload1ColourAttributeKey, payload1ColourAttributeValue);
                one(mapMessage).setString(payload1HueAttributeKey, payload1HueAttributeValue);
                one(mapMessage).setString(payload1TextureAttributeKey, payload1TextureAttributeValue);
                
                one(payload2).getAttribute(colourPayloadAttributeName);will(returnValue(payload2ColourAttributeValue));
                one(payload2).getAttribute(huePayloadAttributeName);will(returnValue(payload2HueAttributeValue));
                one(mapMessage).setString(payload2ColourAttributeKey, payload2ColourAttributeValue);
                one(mapMessage).setString(payload2HueAttributeKey, payload2HueAttributeValue);
                
                
                //event Id
                one(event).getId();will(returnValue(eventId));
                one(mapMessage).setString(eventIdKey, eventId);
                
                //event priority
                one(event).getPriority();will(returnValue(priority));
                one(mapMessage).setInt(eventPriorityKey, priority);
                
                //event timestamp
                one(event).getTimestamp();will(returnValue(timestamp));
                one(mapMessage).setLong(eventTimestampKey, timestamp);

            }
        });
        Assert.assertEquals("produced MapMessage should be that obtained from session",mapMessage, defaultJmsMessageEventSerialiser.toMessage(event, session));
        
        mockery.assertIsSatisfied();
	}
	
	@Test
	public void testDefaultJmsMessageEventSerialiser() throws JMSException, EventDeserialisationException {
		DefaultMapMessageEventSerialiser defaultJmsMessageEventSerialiser = new DefaultMapMessageEventSerialiser();

		defaultJmsMessageEventSerialiser.setPayloadFactory(payloadFactory);
		testSerialisationDesrialisation(defaultJmsMessageEventSerialiser, payloadFactory, mockery);

	}

}
