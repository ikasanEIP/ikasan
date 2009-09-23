/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
 */
package org.ikasan.common.configuration;

// Imported xstream classes
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class provides XStream converter for an <code>Ikasan</code> class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanSecurityConverter
    extends AbstractIkasanConverter
{
    
    /**
     * Creates a new <code>IkasanConverter</code> instance.
     *
     */
    public IkasanSecurityConverter()
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
//        logger.debug("Marshalling the input XML");
//        
//        IkasanSecurity ikasanSecurity = (IkasanSecurity)object;
        super.commonMarshal(object, writer, context);

//        // policies version
//        if (ikasanSecurity.getVersion() != null)
//        {
//            writer.addAttribute(VERSION, ikasanSecurity.getVersion());
//        }
//        
//        // XMLSchema Instance NS URI
//        if (ikasan.getSchemaInstanceNSURI() != null)
//        {
//            writer.addAttribute(NS_URI, ikasanSecurity.getSchemaInstanceNSURI());
//        }
//
//        // No Namespace Schema Location
//        if (ikasanSecurity.getNoNamespaceSchemaLocation() != null)
//        {
//            writer.addAttribute(NO_NS_SCHEMA_LOCATION,
//                    ikasanSecurity.getNoNamespaceSchemaLocation());
//        }
//        
//        // entries
//        if (ikasanSecurity.getEntries() != null)
//        {
//            context.convertAnother(ikasanSecurity.getEntries());
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
        IkasanSecurity ikasanSecurity = new IkasanSecurity();
        return super.commonUnmarshal(ikasanSecurity, reader, context);
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
        return type.equals(IkasanSecurity.class);
    }
}
