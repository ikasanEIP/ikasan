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

package org.ikasan.common.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.CommonRuntimeException;

/**
 * @author Ikasan Development Team
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
