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
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * This class provides XStream converter for an <code>Ikasan</code> class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanConverter
    extends AbstractIkasanConverter
{
//    /** XML structure constants */
//    private final static String VERSION = "version";
//    private final static String ENTRY = "Entry";
//    private final static String NS_URI = "xmlns:xsi";
//    private final static String NO_NS_SCHEMA_LOCATION = "xsi:noNamespaceSchemaLocation";
    
    /**
     * Creates a new <code>IkasanConverter</code> instance.
     *
     */
    public IkasanConverter()
    {
        // Do Nothing
    }

    /**
     * Converts an object to XML.
     * @param object 
     * @param writer 
     * @param context 
     */
    public void marshal(Object object, HierarchicalStreamWriter writer,
                        MarshallingContext context)
    {
        super.commonMarshal(object, writer, context);
//        logger.debug("Marshalling the input XML");
//        
//        Ikasan ikasan = (Ikasan)object;
//
//        // policies version
//        if (ikasan.getVersion() != null)
//        {
//            writer.addAttribute(VERSION, ikasan.getVersion());
//        }
//        
//        // XMLSchema Instance NS URI
//        if (ikasan.getSchemaInstanceNSURI() != null)
//        {
//            writer.addAttribute(NS_URI, ikasan.getSchemaInstanceNSURI());
//        }
//
//        // No Namespace Schema Location
//        if (ikasan.getNoNamespaceSchemaLocation() != null)
//        {
//            writer.addAttribute(NO_NS_SCHEMA_LOCATION,
//                ikasan.getNoNamespaceSchemaLocation());
//        }
//        
//        // entries
//        if (ikasan.getEntries() != null)
//        {
//            context.convertAnother(ikasan.getEntries());
//        }
    }

    /**
     * Converts textual data back into an object.
     * @param reader 
     * @param context 
     * @return Object
     */
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context)
    {
        Ikasan ikasan = new Ikasan();
        return super.commonUnmarshal(ikasan, reader, context);
//        Ikasan ikasan = new Ikasan();
//
//        String attrValue = null;
//
//        // policies version
//        attrValue = reader.getAttribute(VERSION);
//        ikasan.setVersion(attrValue);
//
//        // XMLSchema Instance NS URI
//        attrValue = reader.getAttribute(NS_URI);
//        ikasan.setSchemaInstanceNSURI(attrValue);
//
//        // No Name space Schema Location
//        attrValue = reader.getAttribute(NO_NS_SCHEMA_LOCATION);
//        ikasan.setNoNamespaceSchemaLocation(attrValue);
//
//        String nodeName = null;
//        while (reader.hasMoreChildren())
//        {
//            reader.moveDown();
//            nodeName = reader.getNodeName();
//
//            // Entry
//            if (nodeName.equals(ENTRY))
//            {
//                Entry entry = (Entry)context.convertAnother(ikasan, Entry.class);
//                ikasan.addEntry(entry);
//            }
//
//            reader.moveUp();
//        }
//
//        return ikasan;
    }

    /**
     * Determines whether the converter can marshal a particular class or
     * derivation thereof.
     * 
     * NOTE:  The method parameter type is forced to be a raw type of Class by its parent
     * 
     * @param type 
     * @return true if we can convert
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(final Class type)
    {
        return type.equals(Ikasan.class);
    }
}
