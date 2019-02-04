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
package org.ikasan.configurationService.model;

import org.ikasan.spec.configuration.ConfigurationParameter;

import java.io.Serializable;

/**
 * String based configuration parameter.
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("serial")
public class ConfigurationParameterObjectImpl extends AbstractComponentParameter<Object> implements Serializable
{

    /**
     * Constructor
     * @param name
     * @param value
     */
    public ConfigurationParameterObjectImpl(String name, Object value)
    {
        this(name, value, null, null);
    }

    /**
     * Constructor
     * @param name
     * @param value
     */
    public ConfigurationParameterObjectImpl(String name, Object value, byte[] serialisedValue)
    {
        this(name, value, serialisedValue, null);
    }


    /**
     * Constructor
     * @param name
     * @param value
     * @param description
     */
    public ConfigurationParameterObjectImpl(String name, Object value, String description)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }

        this.value = value;
        this.serialisedValue = null;
        this.description = description;
    }

    /**
     * Constructor
     * @param name
     * @param value
     * @param description
     */
    public ConfigurationParameterObjectImpl(String name, Object value, byte[] serialisedValue, String description)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }

        this.value = value;
        this.serialisedValue = serialisedValue;
        this.description = description;
    }


    /**
     * Constructor for cloning
     */
    public ConfigurationParameterObjectImpl(ConfigurationParameter object)
    {
        this.name = object.getName();
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }

        this.value = object.getValue();
        this.serialisedValue = object.getSerialisedValue();
        this.description = object.getDescription();
    }


    /**
     * Constructor
     */
    protected ConfigurationParameterObjectImpl()
    {
        // required by ORM
    }




}
