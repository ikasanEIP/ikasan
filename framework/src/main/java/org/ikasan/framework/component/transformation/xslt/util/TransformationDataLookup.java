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
