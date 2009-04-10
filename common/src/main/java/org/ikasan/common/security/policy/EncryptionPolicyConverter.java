/*
 * $Id: EncryptionPolicyConverter.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/security/policy/EncryptionPolicyConverter.java $
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

import org.ikasan.common.security.algo.Blowfish;
import org.ikasan.common.security.algo.PBE;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class provides XStream converter for a <code>Policy</code> class.
 *
 * @author <a href="mailto:jeff.mitchell@ikasan.org">Ikasan Development Team</a>
 */
public class EncryptionPolicyConverter
    implements Converter
{
    
    /**
     * Creates a new <code>EncryptionPolicyConverter</code> instance.
     *
     */
    public EncryptionPolicyConverter()
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
        EncryptionPolicy policy = (EncryptionPolicy)object;

        // policy name
        if (policy.getName() != null)
        {
            writer.addAttribute("name", policy.getName());
        }
        
        // algorithm
        if (policy.getAlgorithm() != null)
        {
            writer.startNode(policy.getAlgorithm().getClass().getSimpleName());
            context.convertAnother(policy.getAlgorithm());
            writer.endNode();
        }
    }

    /**
     * Converts textual data back into an object.
     * @param reader 
     * @param context 
     * @return EncryptionPolicy
     */
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context)
    {
        EncryptionPolicy policy = new EncryptionPolicy();

        String attrValue = null;

        // policy name
        attrValue = reader.getAttribute("name");
        policy.setName(attrValue);

        String nodeName = null;
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            nodeName = reader.getNodeName();

            // supported algorithms
            if (nodeName.equals("PBE"))
            {
                PBE pbe = (PBE)context.convertAnother(policy, PBE.class);
                policy.setAlgorithm(pbe);
            }
            // Return System
            else
            if (nodeName.equals("Blowfish"))
            {
                Blowfish blowfish = (Blowfish)context.convertAnother(policy, Blowfish.class);
                policy.setAlgorithm(blowfish);
            }

            reader.moveUp();
        }

        return policy;
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
        return type.equals(EncryptionPolicy.class);
    }
}
