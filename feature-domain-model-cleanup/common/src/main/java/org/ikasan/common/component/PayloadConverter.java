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
package org.ikasan.common.component;

// Imported xstream classes
import org.apache.log4j.Logger;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class provides a default exception message structure. <p/>
 * 
 * @author Ikasan Development Team
 */
public class PayloadConverter
    implements Converter
{
    /**
     * The logger instance.
     */
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(PayloadConverter.class);

    /**
     * The CDATA begin token.
     */
    protected static final String CDATA_BEGIN = "<![CDATA["; //$NON-NLS-1$

    /**
     * The CDATA end token.
     */
    protected static final String CDATA_END = "]]>"; //$NON-NLS-1$

    /** implementation class */
    protected Class<?> implementationClass;
    
    
    /**
     * Factory for instantiating <code>Payload</code>s
     */
    protected PayloadFactory payloadFactory;
    

    
    /**
     * Constructor
     * 
     * @param implementationClass
     * @param payloadFactory
     */
    public PayloadConverter(Class<?> implementationClass, PayloadFactory payloadFactory){
        this.implementationClass=implementationClass;
        this.payloadFactory = payloadFactory;
    }

    /**
     * Converts an object to textual data.
     * @param object 
     * @param writer 
     * @param context 
     */
    public void marshal(Object object, HierarchicalStreamWriter writer,
                        MarshallingContext context)
    {
        Payload payload = (Payload)object;



        if (payload.getId() != null)
        {
            writer.addAttribute("ID", payload.getId()); //$NON-NLS-1$
        }


        if (payload.getSpec() != null)
        {
            writer.addAttribute("SPEC", payload.getSpec().name()); //$NON-NLS-1$
        }

        if (payload.getSrcSystem() != null)
        {
            writer.addAttribute("SRC_SYSTEM", payload.getSrcSystem()); //$NON-NLS-1$
        }

        if (payload.getContent() != null)
        {
            writer.setValue(CDATA_BEGIN);
            writer.setValue(new String(payload.getContent()));
            writer.setValue(CDATA_END);
        }

    }

    /**
     * Converts textual data back into an object.
     * 
     * @param reader 
     * @param context 
     * @return A Payload
     */
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context)
    {
        try
        {
        	String id = null;

            Spec spec = null;
            String srcSystem = null;
            byte [] content = null;
            

            if (reader.getAttribute("SPEC") != null) //$NON-NLS-1$
            {
                spec = Spec.valueOf(reader.getAttribute("SPEC")); //$NON-NLS-1$
            }
            if (reader.getAttribute("SRC_SYSTEM") != null) //$NON-NLS-1$
            {
                srcSystem = reader.getAttribute("SRC_SYSTEM"); //$NON-NLS-1$
            }
            if (reader.getAttribute("ID") != null) //$NON-NLS-1$
            {
                id = reader.getAttribute("ID"); //$NON-NLS-1$
            }
            if (reader.getValue() != null)
            {
                String contentString = reader.getValue();
                contentString = this.removeCDATASection(contentString);
                content = contentString.getBytes();
            }

            Payload payload = payloadFactory.newPayload(id,  spec, srcSystem, content);
    

            


    
            return payload;
        }
        catch(CommonRuntimeException e)
        {
            throw new ConversionException(e);
        }
    }

    /**
     * Determines whether the converter can marshall a particular type.
     * 
     * NOTE:  The method parameter type is forced to be a raw type of Class by its parent
     * 
     * @param type The type to check for convertability
     * @return true if the converter can convert the type
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type)
    {
        return type.equals(implementationClass);
    }

    /**
     * Removes CDATA tokens from the given string and return it.
     * 
     * @param value The data to remove the CDATA from  
     * @return String
     */
    protected String removeCDATASection(String value)
    {
        if (value == null) return new String();

        String newValue = value;
        int indx = -1, start = -1, end = -1;
        indx = value.indexOf(CDATA_BEGIN);
        if (indx > -1)
        {
            start = indx + CDATA_BEGIN.length();
            indx = value.lastIndexOf(CDATA_END);
            if (indx > start) end = indx;
            if (start < end) newValue = value.substring(start, end);
        }

        return newValue;
    }
}
