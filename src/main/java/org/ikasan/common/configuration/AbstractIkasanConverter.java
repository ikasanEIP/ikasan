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
package org.ikasan.common.configuration;

// Imported xstream classes
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * This class provides XStream converter for an <code>Ikasan</code> class.
 * 
 * @author Jeff Mitchell
 */
public abstract class AbstractIkasanConverter
    implements Converter
{
    /** logger */
    private static Logger logger = Logger.getLogger(AbstractIkasanConverter.class);
    // XML structure constants 
    /** version */
    protected final static String VERSION = "version";
    /** entry */
    protected final static String ENTRY = "Entry";
    /** xmlns:xsi */
    protected final static String NS_URI = "xmlns:xsi";
    /** NO_NS_SCHEMA_LOCATION */
    protected final static String NO_NS_SCHEMA_LOCATION = "xsi:noNamespaceSchemaLocation";
    
    /**
     * Converts an object to XML.
     * @param object 
     * @param writer 
     * @param context 
     */
    protected void commonMarshal(Object object, 
                        HierarchicalStreamWriter writer,
                        MarshallingContext context)
    {
        logger.debug("Marshalling the input XML");
        
        AbstractIkasan abstractIkasan = (AbstractIkasan)object;

        // version
        if (abstractIkasan.getVersion() != null)
        {
            writer.addAttribute(VERSION, abstractIkasan.getVersion());
        }
        
        // XMLSchema Instance NS URI
        if (abstractIkasan.getSchemaInstanceNSURI() != null)
        {
            writer.addAttribute(NS_URI, abstractIkasan.getSchemaInstanceNSURI());
        }

        // No Namespace Schema Location
        if (abstractIkasan.getNoNamespaceSchemaLocation() != null)
        {
            writer.addAttribute(NO_NS_SCHEMA_LOCATION,
                    abstractIkasan.getNoNamespaceSchemaLocation());
        }
        
        // entries
        if (abstractIkasan.getEntries() != null)
        {
            context.convertAnother(abstractIkasan.getEntries());
        }
        
    }

    /**
     * Converts textual data back into an object.
     * 
     * @param abstractIkasan 
     * @param reader 
     * @param context 
     * @return AbstractIkasan
     */
    protected AbstractIkasan commonUnmarshal(AbstractIkasan abstractIkasan,
                            HierarchicalStreamReader reader,
                            UnmarshallingContext context)
    {
        String attrValue = null;

        // policies version
        attrValue = reader.getAttribute(VERSION);
        abstractIkasan.setVersion(attrValue);

        // XMLSchema Instance NS URI
        attrValue = reader.getAttribute(NS_URI);
        abstractIkasan.setSchemaInstanceNSURI(attrValue);

        // No Name space Schema Location
        attrValue = reader.getAttribute(NO_NS_SCHEMA_LOCATION);
        abstractIkasan.setNoNamespaceSchemaLocation(attrValue);

        String nodeName = null;
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            nodeName = reader.getNodeName();

            // Entry
            if (nodeName.equals(ENTRY))
            {
                Entry entry = (Entry)context.convertAnother(abstractIkasan, Entry.class);
                abstractIkasan.addEntry(entry);
            }

            reader.moveUp();
        }

        return abstractIkasan;
    }
    
}
