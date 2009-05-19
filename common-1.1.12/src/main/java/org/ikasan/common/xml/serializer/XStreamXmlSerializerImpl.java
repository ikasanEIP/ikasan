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
package org.ikasan.common.xml.serializer;

import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * Implementation of the XMLSerializer interface that uses XStream as the
 * underlying serializer/deserializer
 * 
 * @author duncro
 * 
 */
public class XStreamXmlSerializerImpl implements XMLSerializer<Object>
{
    /** The xstream */
    private XStream xstream;

    /**
     * Constructor
     * 
     * @param aliases Map of aliases
     */
    public XStreamXmlSerializerImpl(Map<String, Class<?>> aliases)
    {
        xstream = new XStream(new DomDriver()); // does not require XPP3 library

        Iterator<String> iterator = aliases.keySet().iterator();
        while (iterator.hasNext())
        {
            String alias = iterator.next();
            xstream.alias(alias, aliases.get(alias));
        }

    }

    public Object toObject(String xml)
    {
        return xstream.fromXML(xml);
    }

    public String toXml(Object subject)
    {
        return xstream.toXML(subject);
    }
}
