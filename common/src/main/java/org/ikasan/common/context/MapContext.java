/*
 * $Id: MapContext.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/context/MapContext.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.context;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.ikasan.common.CommonContext;

/**
 * Simple map fronting as a context for test operations.
 * 
 * @author Jeff Mitchell
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
