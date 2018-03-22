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
package org.ikasan.configurationService.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Reflection utility class.
 *
 * Ikasan Development Team.
 */
public class ReflectionUtils
{
	private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

	/**
	 * Return a setter method instance for the given class for the specified field.
	 * NOTE if no valid method is found, including adherence to the JavaBean standards, then
	 * throw a NoSuchMethodException.
	 *
	 * @param cls
	 * @param field
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getSetterMethod(Class cls, Field field) throws NoSuchMethodException
	{
		String fieldName = field.getName();
		Class fieldType = field.getType();

		String partialMethodName = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
		Method method = cls.getMethod("set" + partialMethodName, fieldType);

		// do we have a setter method in accordance with JavaBean definition
		if( !(void.class.equals(method.getReturnType())) )
		{
			throw new NoSuchMethodException("Setter method does not adhere to JavaBean standards for method [" + method.getName() + "] in class [" + cls.getName() + "]");
		}

		return method;
	}

	/**
	 * Get the value from the target object for the given field name.
	 *
	 * @param target
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static Object getProperty(Object target, String fieldName)
			throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		Class cls = target.getClass();
		Field field = getDeclaredFields(cls).get(fieldName);
		Method method = getGetterMethod(cls,field);
		return method.invoke(target, null);
	}

	/**
	 * Get the type for the given field name in the given target object.
	 *
	 * @param target
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static Class getPropertyType(Object target, String fieldName)
			throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		Class cls = target.getClass();
		Field field = getDeclaredFields(cls).get(fieldName);
		return field.getType();
	}

	/**
	 * Get all non-static field names and values for the given target object.
	 * Any failures to access fields or methods are logged, but not thrown.
	 *
	 * @param target
	 * @return
	 */
	public static Map<String,Object> getPropertiesIgnoringExceptions(Object target)
	{
		Map<String,Object> properties = new HashMap<String,Object>();
		Class cls = target.getClass();

		for(Map.Entry<String,Field> entry:getDeclaredFields(cls).entrySet())
		{
			try
			{
				if( !(java.lang.reflect.Modifier.isStatic(entry.getValue().getModifiers())) )
				{
					Method method = getGetterMethod(cls, entry.getValue());
					properties.put(entry.getValue().getName(), method.invoke(target, null));
				}
			}
			catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
			{
				logger.warn("Unable to access method for field [" + entry.getKey() + "]. Only this field will not be set", e);
			}
		}

		return properties;
	}

	/**
	 * Set property on target object based on the field name and given value.
	 * @param target
	 * @param fieldName
	 * @param value
	 * @throws NoSuchFieldException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static void setProperty(Object target, String fieldName, Object value)
			throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		Class cls = target.getClass();
		Map<String,Field> fields = getDeclaredFields(cls);
		Method method = getSetterMethod(cls,fields.get(fieldName));
		method.invoke(target, value);
	}

	/**
	 * Return a getter method instance in the given class for the specified field.
	 * NOTE if no valid method is found, including adherence to the JavaBean standards, then
	 * throw a NoSuchMethodException.
	 *
	 * @param cls
	 * @param field
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getGetterMethod(Class cls, Field field) throws NoSuchMethodException
	{
		String partialMethodName = field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
		Method method;
		if(field.getType().isInstance(Boolean.class) || field.getType() == boolean.class)
		{
			try
			{
				method = cls.getMethod("is" + partialMethodName, null);
			}
			catch(NoSuchMethodException e)
			{
				try
				{
					method = cls.getMethod("get" + partialMethodName, null);
				}
				catch(NoSuchMethodException e1)
				{
					// final option is to just try the field name as the method
					method = cls.getMethod(field.getName(), null);
				}
			}
		}
		else
		{
			method = cls.getMethod("get" + partialMethodName, null);
		}

		// do we have a getter method in accordance with JavaBean definition
		if(void.class.equals(method.getReturnType()))
		{
			throw new NoSuchMethodException("Getter method does not adhere to JavaBean standards for method [" + method.getName() + "] in class [" + cls.getName() + "]");
		}

		return method;
	}


	/**
	 * Get all fields of the given class and all parent classes regardless of access (public, protected, private, ..).
	 *
	 * @param cls
	 * @return Map<String,Field>
	 */
	protected static Map<String,Field> getDeclaredFields(Class cls)
	{
		Map<String,Field> fields = new HashMap<String,Field>();

		while(cls != null)
		{
			Field[] declaredFields = cls.getDeclaredFields();
			for(Field declaredField:declaredFields)
			{
				fields.put(declaredField.getName(), declaredField);
			}

			cls = cls.getSuperclass();
		}

		return fields;
	}

}
