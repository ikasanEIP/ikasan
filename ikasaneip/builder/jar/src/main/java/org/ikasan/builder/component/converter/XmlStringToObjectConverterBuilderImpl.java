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
package org.ikasan.builder.component.converter;

import org.ikasan.component.converter.xml.XmlStringToObjectConfiguration;
import org.ikasan.component.converter.xml.XmlStringToObjectConverter;
import org.ikasan.spec.component.transformation.Converter;

import jakarta.xml.bind.ValidationEventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Ikasan provided XmlStringToObjectConverterBuilder default implementation.
 *
 * @author Ikasan Development Team
 */
public class XmlStringToObjectConverterBuilderImpl implements XmlStringToObjectConverterBuilder
{
    XmlStringToObjectConfiguration configuration;
    String configuredResourceId;
    List<Class> classesToBeBound = new ArrayList<Class>();

    /**
     * Constructor
     * @param configuration
     */
    public XmlStringToObjectConverterBuilderImpl(XmlStringToObjectConfiguration configuration)
    {
        this.configuration = configuration;
        if(configuration == null)
        {
            throw new IllegalArgumentException("configuration cannot be 'null");
        }
    }

    @Override
    public XmlStringToObjectConverterBuilder setConfiguration(XmlStringToObjectConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setClassToBeBound(Class classToBeBound)
    {
        this.classesToBeBound.add(classToBeBound);
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setClassesToBeBound(List<Class> classesToBeBound)
    {
        this.classesToBeBound.addAll(classesToBeBound);
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setContextPath(String contextPath)
    {
        this.configuration.setContextPath(contextPath);
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setContextPaths(String[] contextPaths)
    {
        this.configuration.setContextPaths(contextPaths);
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setSchema(String schema)
    {
        this.configuration.setSchema(schema);
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setAutoConvertElementToValue(boolean autoConvertElementToValue)
    {
        this.configuration.setAutoConvertElementToValue(autoConvertElementToValue);
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setUnmarshallerProperties(Map<String, Object> unmarshallerProperties)
    {
        this.configuration.setUnmarshallerProperties(unmarshallerProperties);
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setMarshallerProperties(Map<String, Object> marshallerProperties)
    {
        this.configuration.setMarshallerProperties(marshallerProperties);
        return this;
    }

    @Override
    public XmlStringToObjectConverterBuilder setValidationEventHandler(ValidationEventHandler validationEventHandler)
    {
        this.configuration.setValidationEventHandler(validationEventHandler);
        return this;
    }

    @Override
    public Converter build()
    {
        XmlStringToObjectConverter xmlStringToObjectConverter = new XmlStringToObjectConverter();
        if(configuredResourceId != null)
        {
            xmlStringToObjectConverter.setConfiguredResourceId(configuredResourceId);
        }
        if(configuration.getClassesToBeBound() == null)
        {
            if(classesToBeBound.size() > 0)
            {
                this.configuration.setClassesToBeBound( classesToBeBound.toArray(new Class<?>[0]) );
            }
        }
        else
        {
            if(classesToBeBound.size() > 0)
            {
                if(this.configuration.getClassesToBeBound().length > 0)
                {
                    classesToBeBound.addAll( new ArrayList(Arrays.asList( this.configuration.getClassesToBeBound() ) ) );
                }

                this.configuration.setClassesToBeBound( (Class<?>[])classesToBeBound.toArray() );
            }
        }

        xmlStringToObjectConverter.setConfiguration(configuration);
        return xmlStringToObjectConverter;
    }
}

