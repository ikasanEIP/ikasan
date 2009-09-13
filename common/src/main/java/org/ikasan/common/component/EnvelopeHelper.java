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
