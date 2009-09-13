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
 * @author Ikasan Development Team
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
