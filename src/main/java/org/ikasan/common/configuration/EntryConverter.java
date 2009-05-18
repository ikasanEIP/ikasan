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

/**
 * This class provides XStream converter for the <code>Entry</code> class.
 *
 * @author Jeff Mitchell
 */
public class EntryConverter
    implements Converter
{
    /** XML structure constants */
    private final static String KEY = "key";
    
    /**
     * Creates a new <code>TargetConverter</code> instance.
     */
    public EntryConverter()
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
        Entry entry = (Entry)object;

        // entry key
        if (entry.getKey() != null)
        {
            writer.addAttribute(EntryConverter.KEY, entry.getKey());
        }

        // entry value
        if (entry.getValue() != null)
        {
            writer.setValue(entry.getValue());
        }
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
        Entry entry = new Entry();
        entry.setKey(reader.getAttribute(EntryConverter.KEY));
        entry.setValue(reader.getValue());
        return entry;
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
        return type.equals(Entry.class);
    }
}
