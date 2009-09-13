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
