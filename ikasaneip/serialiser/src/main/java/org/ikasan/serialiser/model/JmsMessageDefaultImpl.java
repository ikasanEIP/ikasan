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
package org.ikasan.serialiser.model;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Light JMS message implementation purely for serialiser usage.
 * 
 * @author Ikasan Development Team
 * 
 */
public class JmsMessageDefaultImpl implements Message
{
	protected String jmsMessageId;
	private long timestamp;
	private byte[] jmsCorrelationIdAsBytes;
	private String jmsCorrelationId;
	private Destination jmsReplyTo;
	private Destination destination;
	private int deliveryMode;
	private boolean jmsRedelivered;
	private String jmsType;
	private long jmsExpiration;
	private int jmsPriority;
	private Properties properties;
	
	public JmsMessageDefaultImpl()
    {
    	super();
    	properties = new Properties();
    }
	
    @Override
    public String getJMSMessageID() throws JMSException 
    {
        return this.jmsMessageId;
    }

    @Override
    public void setJMSMessageID(String jmsMessageId) throws JMSException 
    {
    	this.jmsMessageId = jmsMessageId;
    }

    @Override
    public long getJMSTimestamp() throws JMSException 
    {
        return this.timestamp;
    }

    @Override
    public void setJMSTimestamp(long timestamp) throws JMSException 
    {
    	this.timestamp = timestamp;
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException 
    {
        return this.jmsCorrelationIdAsBytes;
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] jmsCorrelationIdAsBytes) throws JMSException 
    {
    	this.jmsCorrelationIdAsBytes = jmsCorrelationIdAsBytes;
    }

    @Override
    public void setJMSCorrelationID(String jmsCorrelationId) throws JMSException 
    {
    	this.jmsCorrelationId = jmsCorrelationId;
    }

    @Override
    public String getJMSCorrelationID() throws JMSException 
    {
        return this.jmsCorrelationId;
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException 
    {
        return this.jmsReplyTo;
    }

    @Override
    public void setJMSReplyTo(Destination jmsReplyTo) throws JMSException 
    {
    	this.jmsReplyTo = jmsReplyTo;
    }

    @Override
    public Destination getJMSDestination() throws JMSException 
    {
        return this.destination;
    }

    @Override
    public void setJMSDestination(Destination destination) throws JMSException 
    {
    	this.destination = destination;
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException 
    {
        return this.deliveryMode;
    }

    @Override
    public void setJMSDeliveryMode(int deliveryMode) throws JMSException 
    {
    	this.deliveryMode = deliveryMode;
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException 
    {
        return this.jmsRedelivered;
    }

    @Override
    public void setJMSRedelivered(boolean jmsRedelivered) throws JMSException 
    {
    	this.jmsRedelivered = jmsRedelivered;
    }

    @Override
    public String getJMSType() throws JMSException 
    {
        return this.jmsType;
    }

    @Override
    public void setJMSType(String jmsType) throws JMSException 
    {
    	this.jmsType = jmsType;
    }

    @Override
    public long getJMSExpiration() throws JMSException 
    {
        return this.jmsExpiration;
    }

    @Override
    public void setJMSExpiration(long jmsExpiration) throws JMSException 
    {
    	this.jmsExpiration = jmsExpiration;
    }

    @Override
    public int getJMSPriority() throws JMSException 
    {
        return this.jmsPriority;
    }

    @Override
    public void setJMSPriority(int jmsPriority) throws JMSException 
    {
    	this.jmsPriority = jmsPriority;
    }

    @Override
    public void clearProperties() throws JMSException 
    {
    	this.properties.clear();
    }

    @Override
    public boolean propertyExists(String s) throws JMSException 
    {
        return this.propertyExists(s);
    }

    @Override
    public boolean getBooleanProperty(String s) throws JMSException 
    {
        return (Boolean)this.properties.get(s);
    }

    @Override
    public byte getByteProperty(String s) throws JMSException 
    {
        return (Byte)this.properties.get(s);
    }

    @Override
    public short getShortProperty(String s) throws JMSException
    {
        return (Short)this.properties.get(s);
    }

    @Override
    public int getIntProperty(String s) throws JMSException 
    {
        return (Integer)this.properties.get(s);
    }

    @Override
    public long getLongProperty(String s) throws JMSException 
    {
        return (Long)this.properties.get(s);
    }

    @Override
    public float getFloatProperty(String s) throws JMSException 
    {
        return (Float)this.properties.get(s);
    }

    @Override
    public double getDoubleProperty(String s) throws JMSException 
    {
        return (Double)this.properties.get(s);
    }

    @Override
    public String getStringProperty(String s) throws JMSException 
    {
        return (String)this.properties.get(s);
    }

    @Override
    public Object getObjectProperty(String s) throws JMSException 
    {
        return this.properties.get(s);
    }

    @Override
    public Enumeration getPropertyNames() throws JMSException 
    {
        return this.properties.keys();
    }

    @Override
    public void setBooleanProperty(String s, boolean b) throws JMSException 
    {
    	this.properties.put(s, b);
    }

    @Override
    public void setByteProperty(String s, byte b) throws JMSException
    {
    	this.properties.put(s, b);
    }

    @Override
    public void setShortProperty(String s, short i) throws JMSException 
    {
    	this.properties.put(s, i);
    }

    @Override
    public void setIntProperty(String s, int i) throws JMSException
    {
    	this.properties.put(s, i);
    }

    @Override
    public void setLongProperty(String s, long l) throws JMSException 
    {
    	this.properties.put(s, l);
    }

    @Override
    public void setFloatProperty(String s, float v) throws JMSException 
    {
    	this.properties.put(s, v);
    }

    @Override
    public void setDoubleProperty(String s, double v) throws JMSException 
    {
    	this.properties.put(s, v);
    }

    @Override
    public void setStringProperty(String s, String s1) throws JMSException 
    {
    	this.properties.put(s, s1);
    }

    @Override
    public void setObjectProperty(String s, Object o) throws JMSException 
    {
    	this.properties.put(s, o);
    }

    @Override
    public void acknowledge() throws JMSException 
    {
    	// Not implemented
    }

    @Override
    public void clearBody() throws JMSException 
    {
    	// Not implemented
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + deliveryMode;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime
				* result
				+ ((jmsCorrelationId == null) ? 0 : jmsCorrelationId.hashCode());
		result = prime * result + Arrays.hashCode(jmsCorrelationIdAsBytes);
		result = prime * result
				+ (int) (jmsExpiration ^ (jmsExpiration >>> 32));
		result = prime * result
				+ ((jmsMessageId == null) ? 0 : jmsMessageId.hashCode());
		result = prime * result + jmsPriority;
		result = prime * result + (jmsRedelivered ? 1231 : 1237);
		result = prime * result
				+ ((jmsReplyTo == null) ? 0 : jmsReplyTo.hashCode());
		result = prime * result + ((jmsType == null) ? 0 : jmsType.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JmsMessageDefaultImpl other = (JmsMessageDefaultImpl) obj;
		if (deliveryMode != other.deliveryMode)
			return false;
		if (destination == null)
		{
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (jmsCorrelationId == null)
		{
			if (other.jmsCorrelationId != null)
				return false;
		} else if (!jmsCorrelationId.equals(other.jmsCorrelationId))
			return false;
		if (!Arrays.equals(jmsCorrelationIdAsBytes,
				other.jmsCorrelationIdAsBytes))
			return false;
		if (jmsExpiration != other.jmsExpiration)
			return false;
		if (jmsMessageId == null)
		{
			if (other.jmsMessageId != null)
				return false;
		} else if (!jmsMessageId.equals(other.jmsMessageId))
			return false;
		if (jmsPriority != other.jmsPriority)
			return false;
		if (jmsRedelivered != other.jmsRedelivered)
			return false;
		if (jmsReplyTo == null)
		{
			if (other.jmsReplyTo != null)
				return false;
		} else if (!jmsReplyTo.equals(other.jmsReplyTo))
			return false;
		if (jmsType == null)
		{
			if (other.jmsType != null)
				return false;
		} else if (!jmsType.equals(other.jmsType))
			return false;
		if (properties == null)
		{
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}
}
