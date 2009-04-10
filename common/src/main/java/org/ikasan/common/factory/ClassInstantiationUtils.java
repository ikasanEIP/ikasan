 /* 
 * $Id: ClassInstantiationUtils.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/factory/ClassInstantiationUtils.java $
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

package org.ikasan.common.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.CommonRuntimeException;

/**
 * @author duncro
 *
 */
public class ClassInstantiationUtils
{
    
    /** constant for Singleton method entry */
    private static String SINGLETON_ENTRY_POINT = "getInstance"; //$NON-NLS-1$
    
    /**
     * Instantiate the given classname based on the specified class.
     * 
     * @param classname
     * @return instantiated object
     */
    public static Object instantiate(String classname)
    {
        String failMsg = new String("Failed to instantiate classname [" //$NON-NLS-1$
                + classname + "()] "); //$NON-NLS-1$
        try
        {
            Class<?> cls = Class.forName(classname);
            return instantiate(cls);
        }
        catch (ClassNotFoundException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
    }

    /**
     * Instantiate the given classname based on the specified class.
     * 
     * @param classname
     * @return instantiated object
     */
    public static Object getSingleton(String classname)
    {
        String failMsg = new String("Failed to get singleton classname [" //$NON-NLS-1$
                + classname + "()] "); //$NON-NLS-1$
        try
        {
            Class<?> cls = Class.forName(classname);
            Class<?>[] paramTypes = {};
            Object[] paramArgs = {};
            Method method = cls.getMethod(SINGLETON_ENTRY_POINT, paramTypes);
            return method.invoke(null, paramArgs);
        }
        catch (InvocationTargetException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
        catch (ClassNotFoundException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
        catch (IllegalAccessException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
        catch (NoSuchMethodException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
    }

    /**
     * Instantiate the given classname based on the specified class.
     * 
     * @param cls
     * @return instantiated object
     */
    public static Object instantiate(Class<?> cls)
    {
        String failMsg = new String("Failed to instantiate classname [" //$NON-NLS-1$
                + cls.getName() + "()] "); //$NON-NLS-1$
        try
        {
            return cls.newInstance();
        }
        catch (IllegalAccessException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
        catch (Throwable e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
    }

    /**
     * Instantiate the given classname based on the specified class and
     * parameters.
     * 
     * @param classname
     * @param paramTypes Class[] of the parameter types
     * @param params Object[] object instances of the Class[] paramTypes
     * @return instantiated object
     * @throws CommonRuntimeException
     */
    public static Object instantiate(String classname, Class<?>[] paramTypes, Object[] params)
    {
        String failMsg = new String("Failed to instantiate classname [" //$NON-NLS-1$
                + classname + "(" + paramTypes + ")] "); //$NON-NLS-1$ //$NON-NLS-2$
        try
        {
            Class<?> cls = Class.forName(classname);
            return instantiate(cls, paramTypes, params);
        }
        catch (ClassNotFoundException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
    }

    /**
     * Instantiate the given classname based on the specified class and
     * parameters.
     * 
     * @param cls
     * @param paramTypes Class[] of the parameter types
     * @param params Object[] object instances of the Class[] paramTypes
     * @return instantiated object
     * @throws CommonRuntimeException
     */
    public static Object instantiate(Class<?> cls, Class<?>[] paramTypes, Object[] params)
    {
        String failMsg = new String("Failed to instantiate classname [" //$NON-NLS-1$
                + cls.getName() + "(" + paramTypes + ")] "); //$NON-NLS-1$ //$NON-NLS-2$
        try
        {
            Constructor<?> con = cls.getConstructor(paramTypes);
            return con.newInstance(params);
        }
        catch (NoSuchMethodException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
        catch (InstantiationException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
        catch (IllegalAccessException e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
        catch (InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if (cause != null) throw new CommonRuntimeException(failMsg + cause.getMessage(), cause, CommonExceptionType.RESOURCE_LOADER_FAILED);
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
        catch (Throwable e)
        {
            throw new CommonRuntimeException(failMsg + e.getMessage(), e, CommonExceptionType.RESOURCE_LOADER_FAILED);
        }
    }
}
