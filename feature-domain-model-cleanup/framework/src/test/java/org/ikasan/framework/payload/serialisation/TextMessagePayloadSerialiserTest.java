package org.ikasan.framework.payload.serialisation;

import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class TextMessagePayloadSerialiserTest {
	
	private Mockery mockery = new Mockery();
	
	
	/**
	 * Class under test
	 */
	private TextMessagePayloadSerialiser textMessagePayloadSerialiser = new TextMessagePayloadSerialiser();

	@Test
	public void testGetSupportedMessageType() {
		Assert.assertEquals("Supported message type should be TextMessage", TextMessage.class, textMessagePayloadSerialiser.getSupportedMessageType());
	}

	@Test
	public void testSupports_withAnyImplementationOfTextMessage_willReturnTrue() {
		Assert.assertTrue("Should support any implementation of TextMessage",textMessagePayloadSerialiser.supports(DummyTextMessage.class));
	}
	
	@Test
	public void testSupports_withAnyImplementationOfAnotherMessageType_willReturnFalse() {
		Assert.assertFalse("Should not support any other implementation of Message",textMessagePayloadSerialiser.supports(DummyMapMessage.class));
	}
	

	@Test
	public void testToMessage() throws JMSException {
		final Session session = mockery.mock(Session.class);
		final Payload payload = mockery.mock(Payload.class);
		final TextMessage textMessage = mockery.mock(TextMessage.class);
		final String payloadContentString = "payloadContent";
		final byte[] payloadContent = payloadContentString.getBytes();
		
	      mockery.checking(new Expectations()
	      {
	          {
	              one(session).createTextMessage();will(returnValue(textMessage));
	              one(payload).getContent();will(returnValue(payloadContent));
	              one(textMessage).setText(with(equal(payloadContentString)));
	          }
	      });
		
		TextMessage message = textMessagePayloadSerialiser.toMessage(payload, session);
		Assert.assertEquals(textMessage, message);
		
		
		mockery.assertIsSatisfied();
	}

	@Test
	public void testToPayload() throws JMSException {
		final TextMessage textMessage = mockery.mock(TextMessage.class);
		final String payloadContentString = "payloadContent";
		final byte[] payloadContent = payloadContentString.getBytes();
		final PayloadFactory payloadFactory = mockery.mock(PayloadFactory.class);
		final Payload payload = mockery.mock(Payload.class);
		final String messageId = "123";
		
		textMessagePayloadSerialiser.setPayloadFactory(payloadFactory);
		
		
	      mockery.checking(new Expectations()
	      {
	          {
	              one(textMessage).getText();will(returnValue(payloadContentString));
	              one(textMessage).getJMSMessageID();will(returnValue(messageId));
	              one(payloadFactory).newPayload(messageId, payloadContent);will(returnValue(payload));

	          }
	      });
		textMessagePayloadSerialiser.toPayload(textMessage);
		
	}
	
	class DummyMapMessage implements MapMessage{

		public boolean getBoolean(String arg0) throws JMSException {
			
			return false;
		}

		public byte getByte(String arg0) throws JMSException {
			
			return 0;
		}

		public byte[] getBytes(String arg0) throws JMSException {
			
			return null;
		}

		public char getChar(String arg0) throws JMSException {
			
			return 0;
		}

		public double getDouble(String arg0) throws JMSException {
			
			return 0;
		}

		public float getFloat(String arg0) throws JMSException {
			
			return 0;
		}

		public int getInt(String arg0) throws JMSException {
			
			return 0;
		}

		public long getLong(String arg0) throws JMSException {
			
			return 0;
		}

		@SuppressWarnings("unchecked")
		public Enumeration getMapNames() throws JMSException {
			
			return null;
		}

		public Object getObject(String arg0) throws JMSException {
			
			return null;
		}

		public short getShort(String arg0) throws JMSException {
			
			return 0;
		}

		public String getString(String arg0) throws JMSException {
			
			return null;
		}

		public boolean itemExists(String arg0) throws JMSException {
			
			return false;
		}

		public void setBoolean(String arg0, boolean arg1) throws JMSException {
			
			
		}

		public void setByte(String arg0, byte arg1) throws JMSException {
			
			
		}

		public void setBytes(String arg0, byte[] arg1) throws JMSException {
			
			
		}

		public void setBytes(String arg0, byte[] arg1, int arg2, int arg3)
				throws JMSException {
			
			
		}

		public void setChar(String arg0, char arg1) throws JMSException {
			
			
		}

		public void setDouble(String arg0, double arg1) throws JMSException {
			
			
		}

		public void setFloat(String arg0, float arg1) throws JMSException {
			
			
		}

		public void setInt(String arg0, int arg1) throws JMSException {
			
			
		}

		public void setLong(String arg0, long arg1) throws JMSException {
			
			
		}

		public void setObject(String arg0, Object arg1) throws JMSException {
			
			
		}

		public void setShort(String arg0, short arg1) throws JMSException {
			
			
		}

		public void setString(String arg0, String arg1) throws JMSException {
			
			
		}

		public void acknowledge() throws JMSException {
			
			
		}

		public void clearBody() throws JMSException {
			
			
		}

		public void clearProperties() throws JMSException {
			
			
		}

		public boolean getBooleanProperty(String arg0) throws JMSException {
			
			return false;
		}

		public byte getByteProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public double getDoubleProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public float getFloatProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public int getIntProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public String getJMSCorrelationID() throws JMSException {
			
			return null;
		}

		public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
			
			return null;
		}

		public int getJMSDeliveryMode() throws JMSException {
			
			return 0;
		}

		public Destination getJMSDestination() throws JMSException {
			
			return null;
		}

		public long getJMSExpiration() throws JMSException {
			
			return 0;
		}

		public String getJMSMessageID() throws JMSException {
			
			return null;
		}

		public int getJMSPriority() throws JMSException {
			
			return 0;
		}

		public boolean getJMSRedelivered() throws JMSException {
			
			return false;
		}

		public Destination getJMSReplyTo() throws JMSException {
			
			return null;
		}

		public long getJMSTimestamp() throws JMSException {
			
			return 0;
		}

		public String getJMSType() throws JMSException {
			
			return null;
		}

		public long getLongProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public Object getObjectProperty(String arg0) throws JMSException {
			
			return null;
		}

		@SuppressWarnings("unchecked")
		public Enumeration getPropertyNames() throws JMSException {
			
			return null;
		}

		public short getShortProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public String getStringProperty(String arg0) throws JMSException {
			
			return null;
		}

		public boolean propertyExists(String arg0) throws JMSException {
			
			return false;
		}

		public void setBooleanProperty(String arg0, boolean arg1)
				throws JMSException {
			
			
		}

		public void setByteProperty(String arg0, byte arg1) throws JMSException {
			
			
		}

		public void setDoubleProperty(String arg0, double arg1)
				throws JMSException {
			
			
		}

		public void setFloatProperty(String arg0, float arg1)
				throws JMSException {
			
			
		}

		public void setIntProperty(String arg0, int arg1) throws JMSException {
			
			
		}

		public void setJMSCorrelationID(String arg0) throws JMSException {
			
			
		}

		public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
			
			
		}

		public void setJMSDeliveryMode(int arg0) throws JMSException {
			
			
		}

		public void setJMSDestination(Destination arg0) throws JMSException {
			
			
		}

		public void setJMSExpiration(long arg0) throws JMSException {
			
			
		}

		public void setJMSMessageID(String arg0) throws JMSException {
			
			
		}

		public void setJMSPriority(int arg0) throws JMSException {
			
			
		}

		public void setJMSRedelivered(boolean arg0) throws JMSException {
			
			
		}

		public void setJMSReplyTo(Destination arg0) throws JMSException {
			
			
		}

		public void setJMSTimestamp(long arg0) throws JMSException {
			
			
		}

		public void setJMSType(String arg0) throws JMSException {
			
			
		}

		public void setLongProperty(String arg0, long arg1) throws JMSException {
			
			
		}

		public void setObjectProperty(String arg0, Object arg1)
				throws JMSException {
			
			
		}

		public void setShortProperty(String arg0, short arg1)
				throws JMSException {
			
			
		}

		public void setStringProperty(String arg0, String arg1)
				throws JMSException {
			
			
		}
		
	}
	
	
	class DummyTextMessage implements TextMessage{

		public String getText() throws JMSException {
			
			return null;
		}

		public void setText(String arg0) throws JMSException {
			
			
		}

		public void acknowledge() throws JMSException {
			
			
		}

		public void clearBody() throws JMSException {
			
			
		}

		public void clearProperties() throws JMSException {
			
			
		}

		public boolean getBooleanProperty(String arg0) throws JMSException {
			
			return false;
		}

		public byte getByteProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public double getDoubleProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public float getFloatProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public int getIntProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public String getJMSCorrelationID() throws JMSException {
			
			return null;
		}

		public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
			
			return null;
		}

		public int getJMSDeliveryMode() throws JMSException {
			
			return 0;
		}

		public Destination getJMSDestination() throws JMSException {
			
			return null;
		}

		public long getJMSExpiration() throws JMSException {
			
			return 0;
		}

		public String getJMSMessageID() throws JMSException {
			
			return null;
		}

		public int getJMSPriority() throws JMSException {
			
			return 0;
		}

		public boolean getJMSRedelivered() throws JMSException {
			
			return false;
		}

		public Destination getJMSReplyTo() throws JMSException {
			
			return null;
		}

		public long getJMSTimestamp() throws JMSException {
			
			return 0;
		}

		public String getJMSType() throws JMSException {
			
			return null;
		}

		public long getLongProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public Object getObjectProperty(String arg0) throws JMSException {
			
			return null;
		}

		@SuppressWarnings("unchecked")
		public Enumeration getPropertyNames() throws JMSException {
			
			return null;
		}

		public short getShortProperty(String arg0) throws JMSException {
			
			return 0;
		}

		public String getStringProperty(String arg0) throws JMSException {
			
			return null;
		}

		public boolean propertyExists(String arg0) throws JMSException {
			
			return false;
		}

		public void setBooleanProperty(String arg0, boolean arg1)
				throws JMSException {
			
			
		}

		public void setByteProperty(String arg0, byte arg1) throws JMSException {
			
			
		}

		public void setDoubleProperty(String arg0, double arg1)
				throws JMSException {
			
			
		}

		public void setFloatProperty(String arg0, float arg1)
				throws JMSException {
			
			
		}

		public void setIntProperty(String arg0, int arg1) throws JMSException {
			
			
		}

		public void setJMSCorrelationID(String arg0) throws JMSException {
			
			
		}

		public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
			
			
		}

		public void setJMSDeliveryMode(int arg0) throws JMSException {
			
			
		}

		public void setJMSDestination(Destination arg0) throws JMSException {
			
			
		}

		public void setJMSExpiration(long arg0) throws JMSException {
			
			
		}

		public void setJMSMessageID(String arg0) throws JMSException {
			
			
		}

		public void setJMSPriority(int arg0) throws JMSException {
			
			
		}

		public void setJMSRedelivered(boolean arg0) throws JMSException {
			
			
		}

		public void setJMSReplyTo(Destination arg0) throws JMSException {
			
			
		}

		public void setJMSTimestamp(long arg0) throws JMSException {
			
			
		}

		public void setJMSType(String arg0) throws JMSException {
			
			
		}

		public void setLongProperty(String arg0, long arg1) throws JMSException {
			
			
		}

		public void setObjectProperty(String arg0, Object arg1)
				throws JMSException {
			
			
		}

		public void setShortProperty(String arg0, short arg1)
				throws JMSException {
			
			
		}

		public void setStringProperty(String arg0, String arg1)
				throws JMSException {
			
			
		}
		
	}

}
