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
package org.ikasan.common.component;

// Imported xstream classes
import org.apache.log4j.Logger;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.Payload;
import org.ikasan.common.ResourceLoader;
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
     * Creates a new <code>PayloadConverter</code> instance.
     * 
     * @param implementationClass 
     */
    public PayloadConverter(Class<?> implementationClass)
    {
        this(implementationClass, ResourceLoader.getInstance().getPayloadFactory());
    }
    
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

        // XMLSchema Instance NS URI
        if (payload.getSchemaInstanceNSURI() != null)
        {
            writer.addAttribute("xmlns:xsi", //$NON-NLS-1$
                    payload.getSchemaInstanceNSURI());
        }

        // No Namespace Schema Location
        if (payload.getNoNamespaceSchemaLocation() != null)
        {
            writer.addAttribute("xsi:noNamespaceSchemaLocation", //$NON-NLS-1$
                    payload.getNoNamespaceSchemaLocation());
        }

        if (payload.getId() != null)
        {
            writer.addAttribute("ID", payload.getId()); //$NON-NLS-1$
        }
        if (payload.getTimestamp().longValue() > 0L)
        {
            writer.addAttribute("TIMESTAMP_FORMATTED", payload.getFormattedTimestamp()); //$NON-NLS-1$
            writer.addAttribute("TIMESTAMP_FORMAT", payload.getTimestampFormat()); //$NON-NLS-1$
            writer.addAttribute("TIMESTAMP", String.valueOf(payload.getTimestamp())); //$NON-NLS-1$
        }
        if (payload.getTimezone() != null)
        {
            writer.addAttribute("TIMEZONE", payload.getTimezone()); //$NON-NLS-1$
        }
        if (payload.getPriority().intValue() > -1)
        {
            writer.addAttribute("PRIORITY", String.valueOf(payload //$NON-NLS-1$
                    .getPriority()));
        }
        if (payload.getName() != null)
        {
            writer.addAttribute("NAME", payload.getName()); //$NON-NLS-1$
        }
        if (payload.getSpec() != null)
        {
            writer.addAttribute("SPEC", payload.getSpec()); //$NON-NLS-1$
        }
        if (payload.getFormat() != null)
        {
            writer.addAttribute("FORMAT", payload.getFormat()); //$NON-NLS-1$
        }
        if (payload.getEncoding() != null)
        {
            writer.addAttribute("ENCODING", payload.getEncoding()); //$NON-NLS-1$
        }
        if (payload.getCharset() != null)
        {
            writer.addAttribute("CHARSET", payload.getCharset()); //$NON-NLS-1$
        }
        if (payload.getSize().longValue() > 0L)
        {
            writer.addAttribute("SIZE", String.valueOf(payload.getSize())); //$NON-NLS-1$
        }
        if (payload.getChecksum() != null)
        {
            writer.addAttribute("CHECKSUM", payload.getChecksum()); //$NON-NLS-1$
        }
        if (payload.getChecksumAlg() != null)
        {
            writer.addAttribute("CHECKSUM_ALG", payload.getChecksumAlg()); //$NON-NLS-1$
        }
        if (payload.getProcessIds() != null)
        {
            writer.addAttribute("PROCESS_IDS", payload.getProcessIds()); //$NON-NLS-1$
        }
        if (payload.getSrcSystem() != null)
        {
            writer.addAttribute("SRC_SYSTEM", payload.getSrcSystem()); //$NON-NLS-1$
        }
        if (payload.getTargetSystems() != null)
        {
            writer.addAttribute("TARGET_SYSTEMS", payload.getTargetSystems()); //$NON-NLS-1$
        }
        if (payload.getResubmissionInfo() != null)
        {
            writer.addAttribute("RESUBMISSION_INFO", payload.getResubmissionInfo()); //$NON-NLS-1$
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
            String name = null;
            String spec = null;
            String srcSystem = null;
            
            if (reader.getAttribute("NAME") != null) //$NON-NLS-1$
            {
                name = reader.getAttribute("NAME"); //$NON-NLS-1$
            }
            if (reader.getAttribute("SPEC") != null) //$NON-NLS-1$
            {
                spec = reader.getAttribute("SPEC"); //$NON-NLS-1$
            }
            if (reader.getAttribute("SRC_SYSTEM") != null) //$NON-NLS-1$
            {
                srcSystem = reader.getAttribute("SRC_SYSTEM"); //$NON-NLS-1$
            }

            Payload payload = payloadFactory.newPayload(name, spec, srcSystem);
    
            // XMLSchema Instance NS URI
            if (reader.getAttribute("xmlns:xsi") != null) //$NON-NLS-1$
            {
                payload.setSchemaInstanceNSURI(reader.getAttribute("xmlns:xsi")); //$NON-NLS-1$
            }
            // No Namespace Schema Location
            if (reader.getAttribute("xsi:noNamespaceSchemaLocation") != null) //$NON-NLS-1$
            {
                payload.setNoNamespaceSchemaLocation(reader.
                                    getAttribute("xsi:noNamespaceSchemaLocation")); //$NON-NLS-1$
            }
            if (reader.getAttribute("ID") != null) //$NON-NLS-1$
            {
                payload.setId(reader.getAttribute("ID")); //$NON-NLS-1$
            }
            if (reader.getAttribute("TIMESTAMP") != null) //$NON-NLS-1$
            {
                String attrVal = reader.getAttribute("TIMESTAMP"); //$NON-NLS-1$
                try
                {
                    long timestamp = Long.parseLong(attrVal);
                    payload.setTimestamp(new Long(timestamp));
                }
                catch (NumberFormatException e)
                {
                    throw new ConversionException(e);
                }
                attrVal = reader.getAttribute("TIMESTAMP_FORMAT"); //$NON-NLS-1$
                payload.setTimestampFormat(attrVal);
            }
            if (reader.getAttribute("TIMEZONE") != null) //$NON-NLS-1$
            {
                payload.setTimezone(reader.getAttribute("TIMEZONE")); //$NON-NLS-1$
            }
            
            if (reader.getAttribute("TIMESTAMP_FORMATTED") != null) //$NON-NLS-1$
            {
                payload.setFormattedTimestamp(reader.getAttribute("TIMESTAMP_FORMATTED")); //$NON-NLS-1$
            }
            if (reader.getAttribute("PRIORITY") != null) //$NON-NLS-1$
            {
                String attrVal = reader.getAttribute("PRIORITY"); //$NON-NLS-1$
                try
                {
                    int priority = Integer.parseInt(attrVal);
                    payload.setPriority(new Integer(priority));
                }
                catch (NumberFormatException e)
                {
                    throw new ConversionException(e);
                }
            }
            if (reader.getAttribute("ENCODING") != null) //$NON-NLS-1$
            {
                payload.setEncoding(reader.getAttribute("ENCODING")); //$NON-NLS-1$
            }
            if (reader.getAttribute("FORMAT") != null) //$NON-NLS-1$
            {
                payload.setFormat(reader.getAttribute("FORMAT")); //$NON-NLS-1$
            }
            if (reader.getAttribute("CHARSET") != null) //$NON-NLS-1$
            {
                payload.setCharset(reader.getAttribute("CHARSET")); //$NON-NLS-1$
            }
            if (reader.getAttribute("SIZE") != null) //$NON-NLS-1$
            {
                String attrVal = reader.getAttribute("SIZE"); //$NON-NLS-1$
                try
                {
                    long size = Long.parseLong(attrVal);
                    payload.setSize(new Long(size));
                }
                catch (NumberFormatException e)
                {
                    throw new ConversionException(e);
                }
            }
            if (reader.getAttribute("CHECKSUM") != null) //$NON-NLS-1$
            {
                payload.setChecksum(reader.getAttribute("CHECKSUM")); //$NON-NLS-1$
            }
            if (reader.getAttribute("CHECKSUM_ALG") != null) //$NON-NLS-1$
            {
                payload.setChecksumAlg(reader.getAttribute("CHECKSUM_ALG")); //$NON-NLS-1$
            }
            if (reader.getAttribute("PROCESS_IDS") != null) //$NON-NLS-1$
            {
                payload.setProcessIds(reader.getAttribute("PROCESS_IDS")); //$NON-NLS-1$
            }
            if (reader.getAttribute("TARGET_SYSTEMS") != null) //$NON-NLS-1$
            {
                payload.setTargetSystems(reader.getAttribute("TARGET_SYSTEMS")); //$NON-NLS-1$
            }
            if (reader.getAttribute("RESUBMISSION_INFO") != null) //$NON-NLS-1$
            {
                payload.setResubmissionInfo(reader.getAttribute("RESUBMISSION_INFO")); //$NON-NLS-1$
            }
            if (reader.getValue() != null)
            {
                String content = reader.getValue();
                content = this.removeCDATASection(content);
                payload.setContent(content.getBytes());
            }
    
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
