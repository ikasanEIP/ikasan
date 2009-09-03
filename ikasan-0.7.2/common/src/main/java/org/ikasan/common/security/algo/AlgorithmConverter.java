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
package org.ikasan.common.security.algo;

// Imported java classes
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

// Imported xstream classes
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * This class provides XStream converter for an <code>Algorithm</code> class.
 * The algorithm class is an abstract class therefore this converter will 
 * generically work across all implementations of the algorithm on the 
 * assumption that all implemented algorithm class vars become attributes of
 * the generated XML and vice versa. This is achieved by introspection on the 
 * implementation class and invoking the methods through bean info.
 * 
 * Any specific algorithm implementations that require a presentation other 
 * than simple attributes will need to extend this class and implement the xstream
 * marshalling as appropriate.
 * 
 * @author Ikasan Development Team
 */
public class AlgorithmConverter
    implements Converter
{
    /** logger */
    private static Logger logger = Logger.getLogger(AlgorithmConverter.class);
    /** we need to keep a handle on the implementation class */
    private Class<?> type;
    
    /**
     * Creates a new <code>AlgorithmConverter</code> instance.
     *
     */
    public AlgorithmConverter()
    {
        // Do Nothing
    }

    /**
     * Converts an object to XML on the assumption that all class instance vars
     * will become XML attributes. If this is not the case then this method 
     * should be overridden.
     * @param object 
     * @param writer 
     * @param context 
     */
    public void marshal(Object object, HierarchicalStreamWriter writer,
                        MarshallingContext context)
    {
        try
        {
            BeanInfo info = Introspector.getBeanInfo(object.getClass(), Object.class);
            for (PropertyDescriptor pd : info.getPropertyDescriptors())
            {
                String name = pd.getName();
                // Workaround to not cast null back to an Object[]
                Object[] temp = null;
                Object obj = pd.getReadMethod().invoke(object, temp);
                if(obj != null)
                {
                    if(isConvertible(obj))
                        writer.addAttribute(name, obj.toString());
                    else
                        throw new ConversionException("Unknown type for name [" 
                                + name + "]");
                }
                else
                    logger.debug("Getter method [" 
                            + pd.getReadMethod().getName() + "] returned 'null'");
            }
        }
        catch(ConversionException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new ConversionException(e);
        }
    }

    /**
     * Is the incoming object a representation of a convertible type.
     * 
     * @param object Object to check for convertability
     * @return boolean
     */
    protected boolean isConvertible(final Object object)
    {
        if(object instanceof Boolean   ||
           object instanceof Byte      ||
           object instanceof Short     ||
           object instanceof Character ||
           object instanceof Integer   ||
           object instanceof Long      ||
           object instanceof Float     ||
           object instanceof Double    ||
           object instanceof String    ||
           object instanceof byte[])
            return true;
        
        return false;
    }
    
    /**
     * Converts textual data back into an object.
     * @param reader 
     * @param context 
     * @return algorithm
     */
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context)
    {
        if(this.type == null)
            throw new ConversionException("class type cannot be 'null'.");
        
        try
        {
            Object algorithm = this.type.newInstance();
            BeanInfo info = Introspector.getBeanInfo(algorithm.getClass(), Object.class);
            for (PropertyDescriptor pd : info.getPropertyDescriptors())
            {
                String name = pd.getName();
                Object object = this.toObject(pd, reader.getAttribute(name));
                if(object != null)
                    pd.getWriteMethod().invoke(algorithm, object);
                else
                    logger.debug("XML value for [" + name + "] is 'null'");
            }

            return algorithm;
        }
        catch(ConversionException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new ConversionException(e);
        }
    }

    /**
     * Utility method for getting the string value back as its original
     * object form.
     * 
     * @param pd The property descriptor, e.g. its type
     * @param value The value of the property
     * @return Object
     */
    protected Object toObject(final PropertyDescriptor pd, final String value)
    {
        // just return if we don't have a real value
        if(value == null)
            return value;

        // get the class and name of the property
        Class<?> propertyType = pd.getPropertyType();
        String name = pd.getName();
        
        // create the appropriate object
        try
        {
            if(propertyType.equals(String.class))
                return value;
            else if(propertyType.equals(Long.class))
                return Long.valueOf(value);
            else if(propertyType.equals(Integer.class))
                return Integer.valueOf(value);
            else if(propertyType.equals(Boolean.class))
                return Boolean.valueOf(value);
            else if(propertyType.equals(Byte.class))
                return Byte.valueOf(value);
            else if(propertyType.equals(Short.class))
                return Short.valueOf(value);
            else if(propertyType.equals(Character.class))
            {
                if(value.length() > 1)
                    throw new ConversionException("Character type exceeds "
                            + "1 byte length [" + value + "].");
                return new Character(value.charAt(0));
            }      
            else if(propertyType.equals(Float.class))
                return Float.valueOf(value);
            else if(propertyType.equals(Double.class))
                return Double.valueOf(value);
            else if(propertyType.equals(Byte[].class))
                return value.getBytes();
        }
        catch(NumberFormatException e)
        {
            throw new ConversionException("Failed conversion for [" 
                    + name + "]", e);
        }
        
        throw new ConversionException("Unsupported class [" + propertyType 
                + "] for name [" + name 
                + "] with a value of [" + value + "].");
    }
    
    /**
     * Determines whether the converter can marshal a particular class or
     * derivation thereof.
     * 
     * NOTE:  The method parameter type is forced to be a raw type of Class by its parent
     * 
     * @param classType 
     * @return true if we can convert, else false
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(final Class classType)
    {
        if(Algorithm.class.isAssignableFrom(classType))
        {
            this.type = classType;
            return true;
        }
        
        this.type = null;
        return false;
    }
}
