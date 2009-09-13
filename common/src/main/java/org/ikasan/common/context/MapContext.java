/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common.context;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.ikasan.common.CommonContext;

/**
 * Simple map fronting as a context for test operations.
 * 
 * @author Ikasan Development Team
 */
public class MapContext implements CommonContext
{

    /** map fronting as a context */
    private Map<String, Object> map = null;

    /** Constructor */
    public MapContext()
    {
        this.map = new HashMap<String, Object>();
    }

    /**
     * Lookup based on the provided context for the given object name.
     * 
     * @param object context
     * @param objectName
     * @return Object
     * @throws NamingException
     */
    public Object lookup(Object object, String objectName) throws NamingException
    {
        if (object instanceof Map)
        {
            return ((Map<?, ?>) object).get(objectName);
        }
        // Default else
        throw new NamingException("Object was not a valid Map for " + "lookup of [" + objectName + "].");
    }

    /**
     * Lookup on the local context for the given object name.
     * 
     * @param objectName
     * @return Object
     * @throws NamingException
     */
    public Object lookup(String objectName) throws NamingException
    {
        return lookup(this.map, objectName);
    }

    public void bind(String objectName, Object objectValue) throws NamingException
    {
        bind(this.map, objectName, objectValue);
    }

    public void bind(Object object, String objectName, Object objectValue) throws NamingException
    {
        if (object instanceof Map)
        {
            Map<String, Object> newMap = (Map<String, Object>)object;
            newMap.put(objectName, objectValue);
        }
        else
        {
            throw new NamingException("Object was not a valid Map for " + "bind of [" + objectName + "].");
        }
    }

    public void rebind(String objectName, Object objectValue) throws NamingException
    {
        bind(objectName, objectValue);
    }

    public void rebind(Object object, String objectName, Object objectValue) throws NamingException
    {
        bind(object, objectName, objectValue);
    }

    public void unbind(String objectName) throws NamingException
    {
        unbind(this.map, objectName);
    }

    public void unbind(Object object, String objectName) throws NamingException
    {
        if (object instanceof Map)
        {
            ((Map<?, ?>) object).remove(objectName);
        }
        else
        {
            throw new NamingException("Object was not a valid Map for " + "unbind of [" + objectName + "].");
        }
    }

}
