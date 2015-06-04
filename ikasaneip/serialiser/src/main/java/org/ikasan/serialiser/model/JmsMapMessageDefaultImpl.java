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

import java.util.Enumeration;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * Light JMS map message implementation purely for serialiser usage.
 * 
 * @author Ikasan Development Team
 * 
 */
public class JmsMapMessageDefaultImpl extends JmsMessageDefaultImpl implements MapMessage
{
    /** map being maintained */
    private Properties content = new Properties();

    @Override
    public boolean getBoolean(String s) throws JMSException 
    {
        return (Boolean) this.content.get(s);
    }

    @Override
    public byte getByte(String s) throws JMSException 
    {
        return (Byte) this.content.get(s);
    }

    @Override
    public short getShort(String s) throws JMSException 
    {
        return (Short) this.content.get(s);
    }

    @Override
    public char getChar(String s) throws JMSException
    {
        return (Character) this.content.get(s);
    }

    @Override
    public int getInt(String s) throws JMSException 
    {
        return (Integer) this.content.get(s);
    }

    @Override
    public long getLong(String s) throws JMSException
    {
        return (Long) this.content.get(s);
    }

    @Override
    public float getFloat(String s) throws JMSException 
    {
        return (Float) this.content.get(s);
    }

    @Override
    public double getDouble(String s) throws JMSException
    {
        return (Double) this.content.get(s);
    }

    @Override
    public String getString(String s) throws JMSException 
    {
        return (String) this.content.get(s);
    }

    @Override
    public byte[] getBytes(String s) throws JMSException 
    {
        return (byte[]) this.content.get(s);
    }

    @Override
    public Object getObject(String s) throws JMSException
    {
        return this.content.get(s);
    }

    @Override
    public Enumeration getMapNames() throws JMSException 
    {
        return this.content.keys();
    }

    @Override
    public void setBoolean(String s, boolean b) throws JMSException
    {
    	this.content.put(s, b);
    }

    @Override
    public void setByte(String s, byte b) throws JMSException 
    {
    	this.content.put(s, b);
    }

    @Override
    public void setShort(String s, short i) throws JMSException 
    {
    	this.content.put(s, i);
    }

    @Override
    public void setChar(String s, char c) throws JMSException 
    {
    	this.content.put(s, c);
    }

    @Override
    public void setInt(String s, int i) throws JMSException 
    {
    	this.content.put(s, i);
    }

    @Override
    public void setLong(String s, long l) throws JMSException 
    {
    	this.content.put(s, l);
    }

    @Override
    public void setFloat(String s, float v) throws JMSException 
    {
    	this.content.put(s, v);
    }

    @Override
    public void setDouble(String s, double v) throws JMSException 
    {
    	this.content.put(s, v);
    }

    @Override
    public void setString(String s, String s1) throws JMSException 
    {
    	this.content.put(s, s1);
    }

    @Override
    public void setBytes(String s, byte[] bytes) throws JMSException
    {
    	this.content.put(s, bytes);
    }

    @Override
    public void setBytes(String s, byte[] bytes, int i, int i1) throws JMSException 
    {
    	this.content.put(s, bytes);
    }

    @Override
    public void setObject(String s, Object o) throws JMSException 
    {
    	this.content.put(s, o);
    }

    @Override
    public boolean itemExists(String s) throws JMSException 
    {
        return this.content.containsKey(s);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((content == null) ? 0 : content.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JmsMapMessageDefaultImpl other = (JmsMapMessageDefaultImpl) obj;
		if (content == null)
		{
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		return true;
	}
}
