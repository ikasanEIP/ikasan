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
package org.ikasan.component.endpoint.jms.spring.consumer;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.*;

/**
 * A List based JMS message provided by a batching JMS consumer.
 *
 * @author Ikasan Development Team
 */
public class IkasanListMessage extends ArrayList<Message> implements Message
{
    String jmsMessageId;
    long jmsTimestamp = System.currentTimeMillis();
    String jmsCorrelationeId;
    byte[] jmsCorrelationeIdAsBytes;
    Destination jmsReplyTo;
    Destination jmsDestination;
    int jmsDeliveryMode;
    boolean jmsRedelivered;
    String jmsType;
    long jmsExpiration;
    int jmsPriority;
    Map<String, Object> properties = new HashMap<String,Object>();

    @Override
    public String getJMSMessageID() throws JMSException
    {
        return jmsMessageId;
    }

    @Override
    public void setJMSMessageID(String jmsMessageId) throws JMSException
    {
        this.jmsMessageId = jmsMessageId;
    }

    @Override
    public long getJMSTimestamp() throws JMSException
    {
        return jmsTimestamp;
    }

    @Override
    public void setJMSTimestamp(long jmsTimestamp) throws JMSException
    {
        this.jmsTimestamp = jmsTimestamp;
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException
    {
        if(this.jmsCorrelationeIdAsBytes != null)
        {
            return this.jmsCorrelationeIdAsBytes;
        }

        if(this.jmsCorrelationeId != null)
        {
            return this.jmsCorrelationeId.getBytes();
        }

        return new byte[0];
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] jmsCorrelationeIdAsBytes) throws JMSException
    {
        this.jmsCorrelationeIdAsBytes = jmsCorrelationeIdAsBytes;
    }

    @Override
    public void setJMSCorrelationID(String jmsCorrelationeId) throws JMSException
    {
        this.jmsCorrelationeId = jmsCorrelationeId;
    }

    @Override
    public String getJMSCorrelationID() throws JMSException
    {
        return jmsCorrelationeId;
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
        return jmsDestination;
    }

    @Override
    public void setJMSDestination(Destination jmsDestination) throws JMSException
    {
        this.jmsDestination = jmsDestination;
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException
    {
        return this.jmsDeliveryMode;
    }

    @Override
    public void setJMSDeliveryMode(int jmsDeliveryMode) throws JMSException
    {
        this.jmsDeliveryMode = jmsDeliveryMode;
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
        return jmsType;
    }

    @Override
    public void setJMSType(String jmsType) throws JMSException
    {
        this.jmsType = jmsType;
    }

    @Override
    public long getJMSExpiration() throws JMSException
    {
        return jmsExpiration;
    }

    @Override
    public void setJMSExpiration(long jmsExpiration) throws JMSException
    {
        this.jmsExpiration = jmsExpiration;
    }

    @Override
    public int getJMSPriority() throws JMSException
    {
        return jmsPriority;
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
    public boolean propertyExists(String property) throws JMSException
    {
        return this.properties.containsKey(property);
    }

    @Override
    public boolean getBooleanProperty(String name) throws JMSException
    {
        return (boolean)this.properties.get(name);
    }

    @Override
    public byte getByteProperty(String name) throws JMSException
    {
        return (byte)this.properties.get(name);
    }

    @Override
    public short getShortProperty(String name) throws JMSException
    {
        return (short)this.properties.get(name);
    }

    @Override
    public int getIntProperty(String name) throws JMSException
    {
        return (int)this.properties.get(name);
    }

    @Override
    public long getLongProperty(String name) throws JMSException
    {
        return (long)this.properties.get(name);
    }

    @Override
    public float getFloatProperty(String name) throws JMSException
    {
        return (float)this.properties.get(name);
    }

    @Override
    public double getDoubleProperty(String name) throws JMSException
    {
        return (double)this.properties.get(name);
    }

    @Override
    public String getStringProperty(String name) throws JMSException
    {
        return (String)this.properties.get(name);
    }

    @Override
    public Object getObjectProperty(String name) throws JMSException
    {
        return this.properties.get(name);
    }

    @Override
    public Enumeration getPropertyNames()
    {
        return Collections.enumeration( properties.keySet() );
    }

    @Override
    public void setBooleanProperty(String name, boolean value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void setByteProperty(String name, byte value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void setShortProperty(String name, short value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void setIntProperty(String name, int value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void setLongProperty(String name, long value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void setFloatProperty(String name, float value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void setDoubleProperty(String name, double value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void setStringProperty(String name, String value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void setObjectProperty(String name, Object value) throws JMSException
    {
        this.properties.put(name, value);
    }

    @Override
    public void acknowledge() throws JMSException
    {
        // does nothing
    }

    @Override
    public void clearBody() throws JMSException
    {
        // does nothing
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        IkasanListMessage messages = (IkasanListMessage) o;

        return !(jmsMessageId != null ? !jmsMessageId.equals(messages.jmsMessageId) : messages.jmsMessageId != null);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (jmsMessageId != null ? jmsMessageId.hashCode() : 0);
        return result;
    }
}