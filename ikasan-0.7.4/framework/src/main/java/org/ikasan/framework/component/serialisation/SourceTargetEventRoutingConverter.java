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
package org.ikasan.framework.component.serialisation;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Ikasan Development Team
 *
 */
public class SourceTargetEventRoutingConverter implements Converter
{
    /**
     * Default constructor.
     */
    public SourceTargetEventRoutingConverter()
    {
        //Does nothing.
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
    {
        SourceTargetEventRouting srcTgtEventRouting = (SourceTargetEventRouting)obj;
        if (srcTgtEventRouting.getRoutingList() != null
                && srcTgtEventRouting.getRoutingList().size() > 0)
        {
            context.convertAnother(srcTgtEventRouting.getRoutingList());
        }
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
        SourceTargetEventRouting srcTgtEventRouting = new SourceTargetEventRouting();
        String nodeName = null;
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            nodeName = reader.getNodeName();
            if (nodeName.equals("route"))
            {
                Route route = (Route) context
                    .convertAnother(srcTgtEventRouting, Route.class);
                srcTgtEventRouting.addRoute(route);
            }
            reader.moveUp();
        }
        return srcTgtEventRouting;
    }
    
    /**
     * NOTE:  Parent class is JDK 1.4 based, so have to suppress this warning
     * @param type 
     * @return true if we can convert, else false
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type)
    {
        return type.equals(SourceTargetEventRouting.class);
    }
}
