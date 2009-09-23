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
package org.ikasan.framework.plugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Abstract class for POJO wrapper plugins
 * 
 * @author Ikasan Development Team
 */
public abstract class PojoWrapperPlugin
{
    /** POJO that is being wrapped */
    protected Object pojo;

    /** Name of method that this POJO supports */
    private String pojoMethodName;

    /** Logger */
    private static Logger logger = Logger.getLogger(PojoWrapperPlugin.class);

    /**
     * Constructor
     * 
     * @param pojo The POJO being wrapped
     * @param pojoMethodName The name of the method on the POJO to be executed
     */
    public PojoWrapperPlugin(Object pojo, String pojoMethodName)
    {
        super();
        this.pojo = pojo;
        this.pojoMethodName = pojoMethodName;
    }

    /**
     * finds and invokes the method
     * 
     * @param params The method parameters
     * @param paramTypes THe method parameter types
     * @throws PluginInvocationException Exception if the plugin could not be invoked
     */
    protected void invoke(Object[] params, Class<?>[] paramTypes) throws PluginInvocationException
    {
        logger.info("about to call wrapped plugin [" + pojo + "] ");
        Class<?> clazz = pojo.getClass();
        Method method;
        try
        {
            method = clazz.getMethod(pojoMethodName, paramTypes);
            method.invoke(pojo, params);
        }
        catch (SecurityException e)
        {
            handleException(params, e);
        }
        catch (NoSuchMethodException e)
        {
            handleException(params, e);
        }
        catch (IllegalArgumentException e)
        {
            handleException(params, e);
        }
        catch (IllegalAccessException e)
        {
            handleException(params, e);
        }
        catch (InvocationTargetException e)
        {
            handleException(params, e);
        }
    }

    /**
     * Handles Exceptions
     * 
     * @param params The parameters
     * @param e The exception to handle
     * @throws PluginInvocationException Exception if the Exception could not be handled
     */
    private void handleException(Object[] params, Exception e) throws PluginInvocationException
    {
        throw new PluginInvocationException("Exception invoking [" + pojoMethodName + "] on wrapped pojo [" + pojo
                + "] with arguments [" + formatParams(params) + "]", e);
    }

    /**
     * Utility method for formatting debug statements
     * 
     * @param params parameters
     * 
     * @return formatted string
     */
    private String formatParams(Object[] params)
    {
        StringBuffer sb = new StringBuffer("{");
        for (int i = 0; i < params.length; i++)
        {
            Object object = params[i];
            sb.append(object);
            if (i < params.length - 1)
            {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
