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
 *
 * This code has been modified from the original class which was sourced from the
 * Apache ActiveMQ project under the following license.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ikasan.serialiser.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import java.io.*;

/**
 * Light JMS bytes message implementation purely for serialiser usage.
 * 
 * @author Ikasan Development Team
 * 
 */
public class JmsBytesMessageDefaultImpl extends JmsMessageDefaultImpl implements BytesMessage
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(JmsBytesMessageDefaultImpl.class);

    protected byte[] content;
    protected transient DataOutputStream dataOut;
    protected transient ByteArrayOutputStream bytesOut;
    protected transient DataInputStream dataIn;
    protected transient int length;

    @Override
    public long getBodyLength() throws JMSException {
        this.initializeReading();
        return (long)this.length;
    }

    @Override
    public boolean readBoolean() throws JMSException {
        this.initializeReading();

        try
        {
            return this.dataIn.readBoolean();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public byte readByte() throws JMSException {
        this.initializeReading();

        try
        {
            return this.dataIn.readByte();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public int readUnsignedByte() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readUnsignedByte();
        }
        catch (EOFException e)
            {
                throw JmsExceptionFactory.getJmsException(e);
            }
        catch (IOException e)
            {
                throw JmsExceptionFactory.getJmsException(e);
            }
    }

    @Override
    public short readShort() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readShort();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }

    }

    @Override
    public int readUnsignedShort() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readUnsignedShort();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }

    }

    @Override
    public char readChar() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readChar();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public int readInt() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readInt();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public long readLong() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readLong();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public float readFloat() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readFloat();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public double readDouble() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readDouble();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public String readUTF() throws JMSException {
        this.initializeReading();

        try {
            return this.dataIn.readUTF();
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public int readBytes(byte[] value) throws JMSException {
        return this.readBytes(value, value.length);
    }

    @Override
    public int readBytes(byte[] value, int length) throws JMSException {
        this.initializeReading();

        try {
            int n;
            int count;
            for(n = 0; n < length; n += count) {
                count = this.dataIn.read(value, n, length - n);
                if (count < 0) {
                    break;
                }
            }

            if (n == 0 && length > 0) {
                n = -1;
            }

            return n;
        }
        catch (EOFException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeBoolean(boolean value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeBoolean(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeByte(byte value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeByte(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeShort(short value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeShort(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeChar(char value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeChar(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeInt(int value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeInt(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeLong(long value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeLong(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeFloat(float value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeFloat(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeDouble(double value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeDouble(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeUTF(String value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.writeUTF(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeBytes(byte[] value) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.write(value);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeBytes(byte[] value, int offset, int length) throws JMSException {
        this.initializeWriting();

        try {
            this.dataOut.write(value, offset, length);
        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    @Override
    public void writeObject(Object value) throws JMSException {
        if (value == null) {
            throw new NullPointerException();
        } else {
            this.initializeWriting();
            if (value instanceof Boolean) {
                this.writeBoolean(((Boolean)value).booleanValue());
            } else if (value instanceof Character) {
                this.writeChar(((Character)value).charValue());
            } else if (value instanceof Byte) {
                this.writeByte(((Byte)value).byteValue());
            } else if (value instanceof Short) {
                this.writeShort(((Short)value).shortValue());
            } else if (value instanceof Integer) {
                this.writeInt(((Integer)value).intValue());
            } else if (value instanceof Long) {
                this.writeLong(((Long)value).longValue());
            } else if (value instanceof Float) {
                this.writeFloat(((Float)value).floatValue());
            } else if (value instanceof Double) {
                this.writeDouble(((Double)value).doubleValue());
            } else if (value instanceof String) {
                this.writeUTF(value.toString());
            } else {
                if (!(value instanceof byte[])) {
                    throw new MessageFormatException("Cannot write non-primitive type:" + value.getClass());
                }

                this.writeBytes((byte[])((byte[])value));
            }

        }
    }

    @Override
    public void reset() throws JMSException
    {
        this.storeContent();

        try
        {
            if (this.bytesOut != null) {
                this.bytesOut.close();
                this.bytesOut = null;
            }

            if (this.dataIn != null) {
                this.dataIn.close();
                this.dataIn = null;
            }

            if (this.dataOut != null) {
                this.dataOut.close();
                this.dataOut = null;
            }

        }
        catch (IOException e)
        {
            throw JmsExceptionFactory.getJmsException(e);
        }
    }

    public String getJMSXMimeType()
    {
        return "jms/bytes-message";
    }

    private void initializeWriting() throws JMSException {
        if (this.dataOut == null) {
            this.bytesOut = new ByteArrayOutputStream();
            OutputStream os = this.bytesOut;
            this.dataOut = new DataOutputStream(os);
        }
    }

    protected void storeContent() throws JMSException
    {
        if (this.dataOut != null)
        {
            try
            {
                this.dataOut.close();
                byte[] bytes = this.bytesOut.toByteArray();
                this.setContent(bytes);
            }
            catch (IOException e)
            {
                throw  JmsExceptionFactory.getJmsException(e);
            }
            finally
            {
                try {
                    if (this.bytesOut != null) {
                        this.bytesOut.close();
                        this.bytesOut = null;
                    }

                    if (this.dataOut != null) {
                        this.dataOut.close();
                        this.dataOut = null;
                    }
                }
                catch (IOException e)
                {
                    // just log it as we are closing anyway
                    logger.warn("Exception on closing bytesOut on close()", e);
                }

            }
        }
    }

    private void initializeReading() throws JMSException
    {
        if (this.dataIn == null)
        {
            byte[] data = this.getContent();
            if (data == null) {
                data = new byte[0];
            }

            InputStream is = new ByteArrayInputStream(data);
            this.length = data.length;
            this.dataIn = new DataInputStream(is);
        }

    }

    protected byte[] getContent()
    {
        return this.content;
    }

    protected void setContent(byte[] content)
    {
        this.content = content;
    }
}
