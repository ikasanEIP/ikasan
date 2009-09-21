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
