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


            byte [] content = null;
            


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

            Payload payload = payloadFactory.newPayload(id,  content);
    

            


    
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
