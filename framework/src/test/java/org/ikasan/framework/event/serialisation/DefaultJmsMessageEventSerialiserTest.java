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
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.UnknownMessageContentException;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class DefaultJmsMessageEventSerialiserTest {

	/**
	 * Class under test
	 */
	private DefaultJmsMessageEventSerialiser defaultJmsMessageEventSerialiser;
	
	
	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
	private PayloadFactory payloadFactory = mockery.mock(PayloadFactory.class);
    
	final Payload payload1 = mockery.mock(Payload.class, "payload1");
	final Payload payload2 = mockery.mock(Payload.class, "payload2");
	
	final String payload1Prefix = DefaultJmsMessageEventSerialiser.PAYLOAD_PREFIX+0;
	final String payload2Prefix = DefaultJmsMessageEventSerialiser.PAYLOAD_PREFIX+1;
	
	//payload content
	final String payload1ContentKey = payload1Prefix + DefaultJmsMessageEventSerialiser.PAYLOAD_CONTENT_SUFFIX;
	final String payload2ContentKey = payload2Prefix + DefaultJmsMessageEventSerialiser.PAYLOAD_CONTENT_SUFFIX;
	final byte[] payload1Content = "payload1Content".getBytes();
	final byte[] payload2Content = "payload2Content".getBytes();
	
	//payload srcSystem
	final String payload1SrcSystemKey = payload1Prefix + DefaultJmsMessageEventSerialiser.PAYLOAD_SRC_SYSTEM_SUFFIX;
	final String payload2SrcSystemKey = payload2Prefix + DefaultJmsMessageEventSerialiser.PAYLOAD_SRC_SYSTEM_SUFFIX;
	final String payload1SrcSystem = "payload1SrcSystem";
	final String payload2SrcSystem = "payload2SrcSystem";
	
	//payload name
	final String payload1NameKey = payload1Prefix + DefaultJmsMessageEventSerialiser.PAYLOAD_NAME_SUFFIX;
	final String payload2NameKey = payload2Prefix + DefaultJmsMessageEventSerialiser.PAYLOAD_NAME_SUFFIX;
	final String payload1Name = "payload1Name";
	final String payload2Name = "payload2Name";	
	
	//event id
	final String eventIdKey = DefaultJmsMessageEventSerialiser.EVENT_FIELD_ID;
	final String eventId = "eventId";
	


	
	public DefaultJmsMessageEventSerialiserTest(){
		defaultJmsMessageEventSerialiser = new DefaultJmsMessageEventSerialiser();
		defaultJmsMessageEventSerialiser.setPayloadFactory(payloadFactory);
	}
	
    /**
     * Tests the successful deserialisation
     * 
     * @throws EventSerialisationException
     * @throws UnknownMessageContentException
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     * @throws JMSException
     */
    @Test
    public void testFromMapMessage() throws EventSerialisationException, UnknownMessageContentException, PayloadOperationException,
            JMSException
    {
    	final MapMessage mapMessage = mockery.mock(MapMessage.class);
    	final String moduleName = "moduleName";
    	final String componentName = "componentName";

    	


    	
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put(DefaultJmsMessageEventSerialiser.EVENT_FIELD_ID, eventId);
    	
    	//payload content
    	map.put(payload1ContentKey, payload1Content);
    	map.put(payload2ContentKey, payload2Content);
    	
    	//payload srcSystem
    	map.put(payload1SrcSystemKey, payload1SrcSystem);
    	map.put(payload2SrcSystemKey, payload2SrcSystem);

    	//payload name
    	map.put(payload1NameKey, payload1Name);
    	map.put(payload2NameKey, payload2Name);
    	
    	final Enumeration mapNamesEnumeration = new Vector(map.keySet()).elements();

    	
    	
    	final List<String> mappedNames = new ArrayList<String>();

    	
    	mockery.checking(new Expectations()
        {
            {
            	one(mapMessage).getMapNames();will(returnValue(mapNamesEnumeration));
            	
            	one(mapMessage).getString(DefaultJmsMessageEventSerialiser.EVENT_FIELD_ID);will(returnValue(eventId));
            	
            	//payload content
            	one(mapMessage).getBytes(payload1ContentKey);will(returnValue(payload1Content));
            	one(mapMessage).getBytes(payload2ContentKey);will(returnValue(payload2Content));
            	
            	//payload srcSystem
            	one(mapMessage).getString(payload1SrcSystemKey);will(returnValue(payload1SrcSystem));
            	one(mapMessage).getString(payload2SrcSystemKey);will(returnValue(payload2SrcSystem));

            	//payload name
            	one(mapMessage).getString(payload1NameKey);will(returnValue(payload1Name));
            	one(mapMessage).getString(payload2NameKey);will(returnValue(payload2Name));

 	
                one(payloadFactory).newPayload(null, payload1Name, null, payload1SrcSystem, payload1Content);will(returnValue(payload1));
                one(payloadFactory).newPayload(null, payload2Name, null, payload2SrcSystem, payload2Content);will(returnValue(payload2));

                
            }
        });
    	
    	Event event = defaultJmsMessageEventSerialiser.fromMapMessage(mapMessage, moduleName, componentName);
    	
    	Assert.assertEquals("event should have id, obtained from appropriate field in mapMessage", eventId, event.getId());
    	
    	mockery.assertIsSatisfied();
    }

	@Test
	public void testToMapMessage() throws JMSException {
		final Event event = mockery.mock(Event.class);
		final List<Payload> payloads = new ArrayList<Payload>();

		

		
		final Session session = mockery.mock(Session.class);
		final MapMessage mapMessage = mockery.mock(MapMessage.class);

		
		payloads.add(payload1);
		payloads.add(payload2);
		
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
                

                
                //payload srcSystem
                one(payload1).getSrcSystem();will(returnValue(payload1SrcSystem));
                one(mapMessage).setString(payload1SrcSystemKey, payload1SrcSystem);

                one(payload2).getSrcSystem();will(returnValue(payload2SrcSystem));
                one(mapMessage).setString(payload2SrcSystemKey, payload2SrcSystem);
                
                //payload name
                one(payload1).getName();will(returnValue(payload1Name));
                one(mapMessage).setString(payload1NameKey, payload1Name);

                one(payload2).getName();will(returnValue(payload2Name));
                one(mapMessage).setString(payload2NameKey, payload2Name);
                
                
                //event Id
                one(event).getId();will(returnValue(eventId));
                one(mapMessage).setString(eventIdKey, eventId);

            }
        });
        Assert.assertEquals("produced MapMessage should be that obtained from session",mapMessage, defaultJmsMessageEventSerialiser.toMapMessage(event, session));
        
        mockery.assertIsSatisfied();
	}

}
