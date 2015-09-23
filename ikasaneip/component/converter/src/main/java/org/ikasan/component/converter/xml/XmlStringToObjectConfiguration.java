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
package org.ikasan.component.converter.xml;

import javax.xml.bind.ValidationEventHandler;
import java.util.Map;

/**
 * Configuration for an XML to Object JAXB converter.
 * 
 * @author Ikasan Development Team
 */
public class XmlStringToObjectConfiguration
{
    
    private Class<?>[] classesToBeBound;
    
    private String contextPath;
    
    private String[] contextPaths;
    
    private String schema;

    private boolean autoConvertElementToValue;

    private Map<String, Object> unmarshallerProperties;

    private Map<String, Object> marshallerProperties;

    private ValidationEventHandler validationEventHandler;


    public String[] getContextPaths()
    {
        return contextPaths;
    }

    public void setContextPaths(String[] contextPaths)
    {
        this.contextPaths = contextPaths;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }

    public Class<?>[] getClassesToBeBound()
    {
        return classesToBeBound;
    }

    public void setClassesToBeBound(Class<?>[] classesToBeBound)
    {
        this.classesToBeBound = classesToBeBound;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public boolean isAutoConvertElementToValue() {
        return autoConvertElementToValue;
    }

    public void setAutoConvertElementToValue(boolean autoConvertElementToValue) {
        this.autoConvertElementToValue = autoConvertElementToValue;
    }

    public Map<String, Object> getUnmarshallerProperties()
    {
        return unmarshallerProperties;
    }

    public void setUnmarshallerProperties(Map<String, Object> unmarshallerProperties)
    {
        this.unmarshallerProperties = unmarshallerProperties;
    }

    public Map<String, Object> getMarshallerProperties()
    {
        return marshallerProperties;
    }

    public void setMarshallerProperties(Map<String, Object> marshallerProperties)
    {
        this.marshallerProperties = marshallerProperties;
    }

    public ValidationEventHandler getValidationEventHandler()
    {
        return validationEventHandler;
    }

    public void setValidationEventHandler(ValidationEventHandler validationEventHandler)
    {
        this.validationEventHandler = validationEventHandler;
    }
}
