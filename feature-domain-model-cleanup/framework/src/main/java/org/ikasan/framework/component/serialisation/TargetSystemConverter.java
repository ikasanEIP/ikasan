/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
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
 * ====================================================================
 */
package org.ikasan.framework.component.serialisation;

import org.ikasan.framework.FrameworkConst;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Ikasan Development Team
 *
 */
public class TargetSystemConverter implements Converter
{
    /**
     * Default constructor.
     */
    public TargetSystemConverter ()
    {
        //Does nothing.
    }
    
    /**
     * Converts a TargetSystem java object to xml text.
     * @param obj
     * @param writer
     * @param context
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
    {
        TargetSystem targetSystem = (TargetSystem) obj;
        if (targetSystem.getName() != null && targetSystem.getName().trim().length() > 0)
        {
            writer.setValue(targetSystem.getName());
        }
    }
    
    /**
     * Converts xml text into a TargetSystem java object.
     * @param reader
     * @param context
     * @return Object
     */
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
        String value = FrameworkConst.UNDEFINED;
        if (reader.getValue() != null && reader.getValue().trim().length() > 0)
        {
            value = reader.getValue();
        }
        return new TargetSystem(value);
    }
    
    /**
     * Determines if the converter can marshal a particular type
     * 
     * NOTE:  Parent class is JDK 1.4 based, so have to suppress this warning
     * @param type 
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type)
    {
        return type.equals(TargetSystem.class);
    }
    
}
