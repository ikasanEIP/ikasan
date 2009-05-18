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
package org.ikasan.common.security.policy;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class provides XStream converter for the <code>Policy</code> class.
 *
 * @author <a href="mailto:jeff.mitchell@ikasan.org">Ikasan Development Team</a>
 */
public class EncryptionPoliciesConverter
    implements Converter
{
    /** logger */
    private static Logger logger = Logger.getLogger(EncryptionPoliciesConverter.class);
    
    /**
     * Creates a new <code>AlgorithmConverter</code> instance.
     *
     */
    public EncryptionPoliciesConverter()
    {
        // Do Nothing
    }

    /**
     * Converts an object to XML on the assumption that all class instance variables
     * will become XML attributes. If this is not the case then this method 
     * should be overridden.
     * @param object 
     * @param writer 
     * @param context 
     */
    public void marshal(Object object, HierarchicalStreamWriter writer,
                        MarshallingContext context)
    {
        logger.debug("Marshalling the input XML");
        
        EncryptionPolicies encryptionPolicies = (EncryptionPolicies)object;

        // policies version
        if (encryptionPolicies.getVersion() != null)
        {
            writer.addAttribute("version", encryptionPolicies.getVersion());
        }
        
        // XMLSchema Instance NS URI
        if (encryptionPolicies.getSchemaInstanceNSURI() != null)
        {
            writer.addAttribute("xmlns:xsi",
                    encryptionPolicies.getSchemaInstanceNSURI());
        }

        // No Namespace Schema Location
        if (encryptionPolicies.getNoNamespaceSchemaLocation() != null)
        {
            writer.addAttribute("xsi:noNamespaceSchemaLocation",
                    encryptionPolicies.getNoNamespaceSchemaLocation());
        }

        // policy
        if (encryptionPolicies.getEncryptionPolicies() != null)
        {
            context.convertAnother(encryptionPolicies.getEncryptionPolicies());
        }
    }

    /**
     * Converts textual data back into an object.
     * 
     * @param reader 
     * @param context 
     * @return EncryptionPolicies
     */
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context)
    {
        EncryptionPolicies encryptionPolicies = new EncryptionPolicies();

        String attrValue = null;

        // policies version
        attrValue = reader.getAttribute("version");
        encryptionPolicies.setVersion(attrValue);

        // XMLSchema Instance NS URI
        attrValue = reader.getAttribute("xmlns:xsi");
        encryptionPolicies.setSchemaInstanceNSURI(attrValue);

        // No Name space Schema Location
        attrValue = reader.getAttribute("xsi:noNamespaceSchemaLocation");
        encryptionPolicies.setNoNamespaceSchemaLocation(attrValue);

        String nodeName = null;
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            nodeName = reader.getNodeName();

            // supported algorithms
            if ("EncryptionPolicy".equals(nodeName))
            {
                EncryptionPolicy encryptionPolicy = (EncryptionPolicy)context.convertAnother(encryptionPolicies, EncryptionPolicy.class);
                encryptionPolicies.addEncryptionPolicy(encryptionPolicy);
            }

            reader.moveUp();
        }

        return encryptionPolicies;
    }

    /**
     * Determines whether the converter can marshal a particular class or
     * derivation thereof.
     * 
     * NOTE:  The method parameter type is forced to be a raw type of Class by its parent
     * 
     * @param type 
     * @return true if we can convert, else false
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(final Class type)
    {
        return type.equals(EncryptionPolicies.class);
    }
}
