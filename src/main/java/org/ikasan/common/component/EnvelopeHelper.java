/*
 * $Id$
 * $URL$
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
package org.ikasan.common.component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.common.Envelope;
import org.ikasan.common.MetaDataInterface;

/**
 * The EnvelopeHelper class 
 * @author Ikasan Development Team
 */
public class EnvelopeHelper
{
    /** Serialize ID */
    private static final long serialVersionUID = 1L;
    
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(EnvelopeHelper.class);

    /**
     * Valid message selector properties for all JMS Messages
     */
    private static Map<String,Object> defaultMessageSelector = 
        new HashMap<String,Object>();
    {
        defaultMessageSelector.put(MetaDataInterface.ID, null);
        defaultMessageSelector.put(MetaDataInterface.NAME, null);
        defaultMessageSelector.put(MetaDataInterface.SRC_SYSTEM, null);
    }
    


    
    /**
     * @param envelope
     * @param toString
     * @return Map
     * @throws EnvelopeOperationException 
     */
    public static Map<String,Object> getEnvelopeMap(Envelope envelope, boolean toString)
        throws EnvelopeOperationException
    {
        Map<String,Object> envelopeMap = new HashMap<String,Object>();
    
        try
        {
            // Use introspection to get bean attributes
            BeanInfo info = Introspector.getBeanInfo(envelope.getClass(), Object.class);
    
            // Iterate over the payload attributes
            for (PropertyDescriptor pd : info.getPropertyDescriptors())
            {
                Object [] methodParams = {};
                Object obj = pd.getReadMethod().invoke(envelope, methodParams);
    
                // No valid data to return
                if (obj == null) 
                {
                    continue;
                }
    
                // Got data - can we populate the map with it?
                if (toString)
                {
                    if (obj instanceof Boolean   ||
                        obj instanceof Byte      ||
                        obj instanceof Short     ||
                        obj instanceof Character ||
                        obj instanceof Integer   ||
                        obj instanceof Long      ||
                        obj instanceof Float     ||
                        obj instanceof Double)
                    {
                        envelopeMap.put(pd.getName(), obj.toString());
                    }
                    else if (obj instanceof String)
                    {
                        envelopeMap.put(pd.getName(), obj);
                    }
                    else if (obj instanceof byte[])
                    {
                        envelopeMap.put(pd.getName(), new String((byte[])obj));
                    }
                    else if (obj instanceof List)
                    {
                        logger.debug("Assuming encountered List is Payload<List>. Ignoring..."); //$NON-NLS-1$
                    }
                    else
                    {
                        logger.warn("Unsupported attribute found in " //$NON-NLS-1$
                                  + envelope.getClass().getName()
                                  + " on creation of Map [" //$NON-NLS-1$
                                  + obj.getClass().getName() + "]. This " //$NON-NLS-1$
                                  + "attribute of the envelope will be ignored."); //$NON-NLS-1$
                    }
                }
                else
                {
                    envelopeMap.put(pd.getName(), obj);
                }
            }
    
            return envelopeMap;
        }
        catch (IntrospectionException e)
        {
            throw new EnvelopeOperationException(e);
        }
        catch (InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if (cause != null)
            {
                // Go through the possible (checked) exceptions
                if (cause instanceof EnvelopeOperationException)
                {
                    throw (EnvelopeOperationException)cause;
                }
    
                throw new EnvelopeOperationException(cause);
            }
    
            throw new EnvelopeOperationException(e);
    
        }
        catch (IllegalAccessException e)
        {
            throw new EnvelopeOperationException(e);
        }
        
    }
    


    

    
}
