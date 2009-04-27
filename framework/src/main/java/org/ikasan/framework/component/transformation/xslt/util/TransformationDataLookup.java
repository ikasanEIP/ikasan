/* 
 * $Id: TransformationDataLookup.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/xslt/util/TransformationDataLookup.java $
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
package org.ikasan.framework.component.transformation.xslt.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.transformation.ThreadLocalBeansWrapper;

/**
 * Exposed to the XSLT stylesheet, provides static access to underlying data
 * access functionality
 * 
 * @author Ikasan Development Team
 * 
 */
public class TransformationDataLookup
{
    /**
     * Default delimiter for string arguments passed as a single argument
     */
    public static final String DEFAULT_DELIMITER = "#";
    /**
     * Logger instance for this class
     */
    private static final Logger logger = Logger.getLogger(TransformationDataLookup.class);

    /**
     * Looks up a value from a named bean using a named bean method, with the
     * string arguments specified, and delimited with the default delimiter
     * 
     * @param beanName - name of bean within bean scope
     * @param methodName - name of method to call on bean
     * @param args - possibly delimited set of String arguments
     * @return String value of the lookup result
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static String lookupData(String beanName, String methodName, String args) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException
    {
        return lookupData(beanName, methodName, args, DEFAULT_DELIMITER);
    }

    /**
     * Looks up a value from a named bean using a named bean method, with the
     * string arguments specified, and delimited with the specified delimiter
     * 
     * @param beanName - name of bean within bean scope
     * @param methodName - name of method to call on bean
     * @param args - possibly delimited set of String arguments
     * @param delimiter - delimiter with which to parse the args for String
     *            arguments
     * @return String value of the lookup result
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static String lookupData(String beanName, String methodName, String args, String delimiter) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        return lookupData(beanName, methodName, getStringArgs(args, delimiter));
    }

    /**
     * Looks up a value from a named bean using a named bean method, with the
     * string arguments specified
     * 
     * @param beanName - name of bean within bean scope
     * @param methodName - name of method to call on bean
     * @param stringArgs - array of String arguments for this bean method
     * @return String value of the lookup result
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static String lookupData(String beanName, String methodName, String... stringArgs) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        String result = null;
        Object bean = getBean(beanName);
        if (bean == null)
        {
            throw new IllegalStateException("beanName [" + beanName + "] must be in beans map for this thread prior to execution");
        }
        Method beanMethod = getBeanMethod(bean, methodName, stringArgs.length);
        logger.info("about to invoke method of name [" + methodName + "] on bean [" + beanName + "]");
        result = (String) beanMethod.invoke(bean, (Object[])stringArgs);
        return result;
    }

    /**
     * Resolves a bean method, given the bean, method name, and number of String
     * arguments
     * 
     * @param methodName
     * @param bean
     * @param noOfStringArguments
     * @return The method
     * @throws NoSuchMethodException
     */
    private static Method getBeanMethod(Object bean, String methodName, int noOfStringArguments) throws NoSuchMethodException
    {
        Class<?>[] types = new Class[noOfStringArguments];
        for (int i = 0; i < types.length; i++)
        {
            types[i] = String.class;
        }
        Method beanMethod = bean.getClass().getDeclaredMethod(methodName, types);
        return beanMethod;
    }

    /**
     * Breaks up an aggregated parameter String into an array of String
     * parameters
     * 
     * @param args
     * @param delimiter
     * @return arguments
     */
    private static String[] getStringArgs(String args, String delimiter)
    {
        StringTokenizer st = new StringTokenizer(args, delimiter);
        List<String> arguments = new ArrayList<String>();
        while (st.hasMoreTokens())
        {
            arguments.add(st.nextToken());
        }
        return arguments.toArray(new String[0]);
    }

    /**
     * Retrieves the bean from a <code>ThreadLocalBeansWrapper</code>
     * 
     * @param beanName
     * @return The bean
     */
    protected static Object getBean(String beanName)
    {
        return ThreadLocalBeansWrapper.getBeans().get(beanName);
    }
}
