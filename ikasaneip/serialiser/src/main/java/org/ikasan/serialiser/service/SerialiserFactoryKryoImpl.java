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

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;

import com.esotericsoftware.kryo.kryo5.util.Pool;
import org.ikasan.spec.serialiser.Converter;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;

import java.util.Map;

/**
 * Kryo Pool Factory for creating pooled kryo instances.
 * Kryo instances are expensive to craate so managing a pool of them (as they are not thread safe) if the most
 * efficient option.
 * 
 * @author Ikasan Development Team
 * 
 */
public class SerialiserFactoryKryoImpl implements SerialiserFactory
{
    /** additional registered serializers */
    private Map<Class,Serializer> serializers;
    private Map<Class, Converter> converters;

    private Integer kryoPoolSize = 10;

    /**
     * Default constructor
     */
    public SerialiserFactoryKryoImpl()
    {
    	
    }

    /**
     * Constructor
     * @param serializers
     */
    public SerialiserFactoryKryoImpl(Map<Class, Serializer> serializers,
    		Map<Class, Converter> converters)
    {
        this.serializers = serializers;
        if(serializers == null)
        {
            throw new IllegalArgumentException("serializers cannot be 'null'");
        }
        this.converters = converters;
        if(converters == null)
        {
            throw new IllegalArgumentException("converters cannot be 'null'");
        }
    }

    @Override
    public Serialiser getDefaultSerialiser()
    {
        return new GenericKryoToBytesSerialiser(this.getPool(), this.converters);
    }

    @Override
    public Serialiser getSerialiser(Class cls)
    {
        return getDefaultSerialiser();
    }

    public void setKryoPoolSize(Integer kryoPoolSize) {
        this.kryoPoolSize = kryoPoolSize;
    }

    /**
     * Get a new pool for kryo instances
     * @return
     */
    protected Pool getPool()
    {
        // Pool constructor arguments: thread safe, soft references, maximum capacity
        Pool<Kryo> kryoPool = new Pool<>(true, true, kryoPoolSize) {
            protected Kryo create () {
                Kryo kryo = new Kryo();
                kryo.setRegistrationRequired(false);
                kryo.setWarnUnregisteredClasses(false);
                configure(kryo);
                return kryo;
            }
        };

        return kryoPool;
    }

    /**
     * Configure the pooled kryo instance
     * @param kryo
     */
    protected void configure(Kryo kryo)
    {
        if(serializers != null && serializers.size() > 0)
        {
            for(Map.Entry<Class,Serializer> entry:serializers.entrySet())
            {
                kryo.addDefaultSerializer(entry.getKey(),entry.getValue());
            }
        }
    }

}
