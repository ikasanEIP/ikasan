/*
 * $Id: TargetSystemConverter.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/serialisation/TargetSystemConverter.java $
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
package org.ikasan.framework.component.serialisation;

import org.ikasan.framework.FrameworkConst;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Ikasan Development Team
 *
 */
public class TargetSystemConverter implements Converter
{
    /**
     * Default constructor.
     */
    public TargetSystemConverter ()
    {
        //Does nothing.
    }
    
    /**
     * Converts a TargetSystem java object to xml text.
     * @param obj
     * @param writer
     * @param context
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
    {
        TargetSystem targetSystem = (TargetSystem) obj;
        if (targetSystem.getName() != null && targetSystem.getName().trim().length() > 0)
        {
            writer.setValue(targetSystem.getName());
        }
    }
    
    /**
     * Converts xml text into a TargetSystem java object.
     * @param reader
     * @param context
     * @return Object
     */
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
        String value = FrameworkConst.UNDEFINED;
        if (reader.getValue() != null && reader.getValue().trim().length() > 0)
        {
            value = reader.getValue();
        }
        return new TargetSystem(value);
    }
    
    /**
     * Determines if the converter can marshal a particular type
     * 
     * NOTE:  Parent class is JDK 1.4 based, so have to suppress this warning
     * @param type 
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type)
    {
        return type.equals(TargetSystem.class);
    }
    
}
