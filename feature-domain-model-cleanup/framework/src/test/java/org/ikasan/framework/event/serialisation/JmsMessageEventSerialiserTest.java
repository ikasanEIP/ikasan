package org.ikasan.framework.event.serialisation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * This class provides an abstract test harness for testing implementations of
 * JmsMessageEventSerialiser
 * 
 * Utilising the methods in this class should allow the implementor toe nsure
 * that all the necessary fields are serialised, desrialised
 * 
 * 
 * @author The Ikasan Development Team
 * 
 */
public abstract class JmsMessageEventSerialiserTest {



	protected void testSerialisationDesrialisation(
			JmsMessageEventSerialiser<MapMessage> jmsMessageEventSerialiser, final PayloadFactory payloadFactory, Mockery mockery)
			throws JMSException {

		// setup the Event with all important fields
		final Event originalEvent = mockery.mock(Event.class);
		final Session session = mockery.mock(Session.class);
		final MapMessage mapMessage = new MockMapMessage();
		final Payload reconstitutedPayload1 = mockery.mock(Payload.class, "reconstitutedPayload1");
		final Payload reconstitutedPayload2 = mockery.mock(Payload.class, "reconstitutedPayload2");
		
		final Payload payload1 = mockery.mock(Payload.class, "payload1");
		final Payload payload2 = mockery.mock(Payload.class, "payload2");

		final List<Payload> payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		final List<Payload> reconstitutedPayloads = new ArrayList<Payload>();
		reconstitutedPayloads.add(reconstitutedPayload1);
		reconstitutedPayloads.add(reconstitutedPayload2);

		//payload field content
		final byte[] payload1Content = "payload1Content".getBytes();
		final byte[] payload2Content = "payload2Content".getBytes();

		final String payload1SrcSystem = "payload1SrcSystem";
		final String payload2SrcSystem = "payload2SrcSystem";

		final String payload1Name = "payload1Name";
		final String payload2Name = "payload2Name";

		final Spec payload1Spec = Spec.BYTE_PLAIN;
		final Spec payload2Spec = Spec.TEXT_CSV;
		
		final String payload1Id = "payload1Id";
		final String payload2Id = "payload2Id";
		
		//payload attributes
		final String colourPayloadAttributeName = "COLOUR";
		final String huePayloadAttributeName = "HUE";
		final String texturePayloadAttributeName = "TEXTURE";
		
		//payload 1 knows about attributes colour, hue, and texture
		final List<String> payload1AttributeNames = new ArrayList<String>();
		payload1AttributeNames.add(colourPayloadAttributeName);
		payload1AttributeNames.add(huePayloadAttributeName);
		payload1AttributeNames.add(texturePayloadAttributeName);
		
		final String payload1ColourAttributeValue = "blue";
		final String payload1HueAttributeValue = "light";
		final String payload1TextureAttributeValue = "graded";
		
		//payload 2 only knows about colour and hue
		final List<String> payload2AttributeNames = new ArrayList<String>();
		payload2AttributeNames.add(colourPayloadAttributeName);
		payload2AttributeNames.add(huePayloadAttributeName);

		final String payload2ColourAttributeValue = "green";
		final String payload2HueAttributeValue = "dark";
		
		//event field content
		final String eventId = "eventId";
		final int eventPriority = 8;
		final long eventTimestamp = 1000l;
		final String eventSrcSystem = "eventSrcSystem";

		mockery.checking(new Expectations() {
			{

				one(session).createMapMessage();will(returnValue(mapMessage));
				
				one(originalEvent).getPayloads();will(returnValue(payloads));
				one(payload1).getContent();will(returnValue(payload1Content));
				one(payload2).getContent();will(returnValue(payload2Content));
				one(payload1).getSrcSystem();will(returnValue(payload1SrcSystem));
				one(payload2).getSrcSystem();will(returnValue(payload2SrcSystem));
				one(payload1).getName();will(returnValue(payload1Name));
				one(payload2).getName();will(returnValue(payload2Name));
				one(payload1).getSpec();will(returnValue(payload1Spec));
				one(payload2).getSpec();will(returnValue(payload2Spec));
				one(payload1).getId();will(returnValue(payload1Id));
				one(payload2).getId();will(returnValue(payload2Id));
				
				one(payload1).getAttributeNames();will(returnValue(payload1AttributeNames));
				one(payload1).getAttribute(colourPayloadAttributeName);will(returnValue(payload1ColourAttributeValue));
				one(payload1).getAttribute(huePayloadAttributeName);will(returnValue(payload1HueAttributeValue));
				one(payload1).getAttribute(texturePayloadAttributeName);will(returnValue(payload1TextureAttributeValue));
				
				one(payload2).getAttributeNames();will(returnValue(payload2AttributeNames));
				one(payload2).getAttribute(colourPayloadAttributeName);will(returnValue(payload2ColourAttributeValue));
				one(payload2).getAttribute(huePayloadAttributeName);will(returnValue(payload2HueAttributeValue));
				

				one(originalEvent).getId();will(returnValue(eventId));
				one(originalEvent).getPriority();will(returnValue(eventPriority));
				one(originalEvent).getTimestamp();will(returnValue(eventTimestamp));
				one(originalEvent).getSrcSystem();will(returnValue(eventSrcSystem));
				
				one(payloadFactory).newPayload(payload1Id, payload1Name, payload1Spec, payload1SrcSystem, payload1Content);will(returnValue(reconstitutedPayload1));
				one(payloadFactory).newPayload(payload2Id, payload2Name, payload2Spec, payload2SrcSystem, payload2Content);will(returnValue(reconstitutedPayload2));
				
				one(reconstitutedPayload1).setAttribute(colourPayloadAttributeName, payload1ColourAttributeValue);
				one(reconstitutedPayload1).setAttribute(huePayloadAttributeName, payload1HueAttributeValue);
				one(reconstitutedPayload1).setAttribute(texturePayloadAttributeName, payload1TextureAttributeValue);

				one(reconstitutedPayload2).setAttribute(colourPayloadAttributeName, payload2ColourAttributeValue);
				one(reconstitutedPayload2).setAttribute(huePayloadAttributeName, payload2HueAttributeValue);
			}
		});

		MapMessage producedMapMessage = jmsMessageEventSerialiser.toMessage(
				originalEvent, session);

		Event reconstitutedEvent = jmsMessageEventSerialiser.fromMessage(
				producedMapMessage, "moduleName", "componentName");

		// check that the reconstituted event is equivalent to the original
		// event
		Assert.assertEquals(eventId, reconstitutedEvent.getId());
		Assert.assertEquals(eventPriority, reconstitutedEvent.getPriority());
		Assert.assertEquals(eventTimestamp, reconstitutedEvent.getTimestamp());
		Assert.assertEquals(eventSrcSystem, reconstitutedEvent.getSrcSystem());
		
		Assert.assertEquals(reconstitutedPayloads, reconstitutedEvent.getPayloads());

		mockery.assertIsSatisfied();
	}



}

class MockMapMessage implements MapMessage {
	
	Map map = new HashMap();
	Map properties = new HashMap();

	public boolean getBoolean(String arg0) throws JMSException {
		return (Boolean) map.get(arg0);
	}

	public byte getByte(String arg0) throws JMSException {
		return (Byte) map.get(arg0);
	}

	public byte[] getBytes(String arg0) throws JMSException {
		return (byte[]) map.get(arg0);
	}

	public char getChar(String arg0) throws JMSException {
		return (Character) map.get(arg0);
	}

	public double getDouble(String arg0) throws JMSException {
		return (Double) map.get(arg0);
	}

	public float getFloat(String arg0) throws JMSException {
		return (Float) map.get(arg0);
	}

	public int getInt(String arg0) throws JMSException {
		return (Integer) map.get(arg0);
	}

	public long getLong(String arg0) throws JMSException {
		return (Long) map.get(arg0);
	}

	public Enumeration getMapNames() throws JMSException {
		Vector mapNamesVector = new Vector(map.keySet());
		return mapNamesVector.elements();
	}

	public Object getObject(String arg0) throws JMSException {
		return map.get(arg0);
	}

	public short getShort(String arg0) throws JMSException {
		return (Short) map.get(arg0);
	}

	public String getString(String arg0) throws JMSException {
		return (String) map.get(arg0);
	}

	public boolean itemExists(String arg0) throws JMSException {
		return map.containsKey(arg0);
	}

	public void setBoolean(String arg0, boolean arg1) throws JMSException {
		map.put(arg0, arg1);

	}

	public void setByte(String arg0, byte arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setBytes(String arg0, byte[] arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setBytes(String arg0, byte[] arg1, int arg2, int arg3)
			throws JMSException {
		throw new UnsupportedOperationException();
	}

	public void setChar(String arg0, char arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setDouble(String arg0, double arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setFloat(String arg0, float arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setInt(String arg0, int arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setLong(String arg0, long arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setObject(String arg0, Object arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setShort(String arg0, short arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void setString(String arg0, String arg1) throws JMSException {
		map.put(arg0, arg1);
	}

	public void acknowledge() throws JMSException {
		throw new UnsupportedOperationException();
	}

	public void clearBody() throws JMSException {
		throw new UnsupportedOperationException();
	}

	public void clearProperties() throws JMSException {
		throw new UnsupportedOperationException();
	}

	public boolean getBooleanProperty(String arg0) throws JMSException {
		return (Boolean) properties.get(arg0);
	}

	public byte getByteProperty(String arg0) throws JMSException {
		return (Byte) properties.get(arg0);
	}

	public double getDoubleProperty(String arg0) throws JMSException {
		return (Double) properties.get(arg0);
	}

	public float getFloatProperty(String arg0) throws JMSException {
		return (Float) properties.get(arg0);
	}

	public int getIntProperty(String arg0) throws JMSException {
		return (Integer) properties.get(arg0);
	}

	private String jmsCorrelationID;
	public String getJMSCorrelationID() throws JMSException {
		return jmsCorrelationID;
	}

	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		return jmsCorrelationID.getBytes();
	}

	private int jmsDeliveryMode;
	public int getJMSDeliveryMode() throws JMSException {
		return jmsDeliveryMode;
	}

	private Destination jmsDestination;
	public Destination getJMSDestination() throws JMSException {
		return jmsDestination;
	}

	long jmsExpiration;
	public long getJMSExpiration() throws JMSException {
		return jmsExpiration;
	}

	private String jmsMessageID;
	public String getJMSMessageID() throws JMSException {
		return jmsMessageID;
	}

	private int jmsPriority = 4;
	public int getJMSPriority() throws JMSException {
		return jmsPriority;
	}

	private boolean jmsRedelivered;
	public boolean getJMSRedelivered() throws JMSException {
		return jmsRedelivered;
	}

	private Destination jmsReplyTo;
	public Destination getJMSReplyTo() throws JMSException {
		return jmsReplyTo;
	}

	private long jmsTimestamp;
	public long getJMSTimestamp() throws JMSException {
		return jmsTimestamp;
	}

	private String jmsType;
	public String getJMSType() throws JMSException {
		return jmsType;
	}

	public long getLongProperty(String arg0) throws JMSException {
		return (Long)properties.get(arg0);
	}

	public Object getObjectProperty(String arg0) throws JMSException {
		return properties.get(arg0);
	}

	public Enumeration getPropertyNames() throws JMSException {
		return new Vector(properties.keySet()).elements();
	}

	public short getShortProperty(String arg0) throws JMSException {
		return (Short)properties.get(arg0);
	}

	public String getStringProperty(String arg0) throws JMSException {
		return (String)properties.get(arg0);
	}

	public boolean propertyExists(String arg0) throws JMSException {
		return properties.containsKey(arg0);
	}

	public void setBooleanProperty(String arg0, boolean arg1)
			throws JMSException {
		properties.put(arg0, arg1);

	}

	public void setByteProperty(String arg0, byte arg1) throws JMSException {
		properties.put(arg0, arg1);

	}

	public void setDoubleProperty(String arg0, double arg1) throws JMSException {
		properties.put(arg0, arg1);

	}

	public void setFloatProperty(String arg0, float arg1) throws JMSException {
		properties.put(arg0, arg1);
	}

	public void setIntProperty(String arg0, int arg1) throws JMSException {
		properties.put(arg0, arg1);
	}

	public void setJMSCorrelationID(String arg0) throws JMSException {
		this.jmsCorrelationID = arg0;
	}

	public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
		this.jmsCorrelationID = new String(arg0);
	}

	public void setJMSDeliveryMode(int arg0) throws JMSException {
		this.jmsDeliveryMode = arg0;
	}

	public void setJMSDestination(Destination arg0) throws JMSException {
		this.jmsDestination = arg0;
	}

	public void setJMSExpiration(long arg0) throws JMSException {
		this.jmsExpiration = arg0;
	}

	public void setJMSMessageID(String arg0) throws JMSException {
		this.jmsMessageID = arg0;
	}

	public void setJMSPriority(int arg0) throws JMSException {
		this.jmsPriority = arg0;
	}

	public void setJMSRedelivered(boolean arg0) throws JMSException {
		this.jmsRedelivered = arg0;
	}

	public void setJMSReplyTo(Destination arg0) throws JMSException {
		this.jmsReplyTo = arg0;
	}

	public void setJMSTimestamp(long arg0) throws JMSException {
		this.jmsTimestamp = arg0;
	}

	public void setJMSType(String arg0) throws JMSException {
		this.jmsType = arg0;
	}

	public void setLongProperty(String arg0, long arg1) throws JMSException {
		properties.put(arg0, arg1);
	}

	public void setObjectProperty(String arg0, Object arg1) throws JMSException {
		properties.put(arg0, arg1);
	}

	public void setShortProperty(String arg0, short arg1) throws JMSException {
		properties.put(arg0, arg1);
	}

	public void setStringProperty(String arg0, String arg1) throws JMSException {
		properties.put(arg0, arg1);
	}
}
