/*
 * $Id: RouteConverter.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/serialisation/RouteConverter.java $
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
public class RouteConverter implements Converter
{
    /**
     * Default constructor.
     */
    public RouteConverter()
    {
        //Does nothing.
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
    {
        Route route = (Route)obj;
        if (route.getSourceSystem() != null && route.getSourceSystem().trim().length() > 0)
        {
            writer.addAttribute("sourceSystem", route.getSourceSystem());
        }
        if (route.getTargetSystems() != null
                && route.getTargetSystems().size() > 0)
        {
            context.convertAnother(route.getTargetSystems());
        }
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
        Route route = new Route();
        String nodeName = null;
        String attributeValue = reader.getAttribute("sourceSystem");
        if (attributeValue != null && attributeValue.trim().length() > 0)
        {
            route.setSourceSystem(attributeValue);
        }
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            nodeName = reader.getNodeName();
            if (nodeName.equals("TargetSystem"))
            {
                TargetSystem targetSystem = (TargetSystem) context
                    .convertAnother(route, TargetSystem.class);
                route.addTargetSystem(targetSystem);
            }
            reader.moveUp();
        }
        return route;
    }
    
    /**
     * NOTE:  Parent class is JDK 1.4 based, so have to suppress this warning
     * @param type 
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type)
    {
        return type.equals(Route.class);
    }
}
