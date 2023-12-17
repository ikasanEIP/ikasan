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

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.ikasan.component.converter.xml.ObjectToXMLStringConverter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.component.converter.xml.XmlConfiguration;

import java.util.List;
import java.util.Map;

/**
 * Ikasan provided ObjectToXmlStringConverterBuilder default implementation.
 *
 * @author Ikasan Development Team
 */
public class ObjectToXmlStringConverterBuilderImpl implements ObjectToXmlStringConverterBuilder
{
    List<Class> classes;
    Map<Class, XmlAdapter> xmlAdapterMap;
    XmlConfiguration xmlConfiguration;
    String configuredResourceId;

    /**
     * Constructor
     * @param xmlConfiguration
     */
    public ObjectToXmlStringConverterBuilderImpl(List<Class> classes, XmlConfiguration xmlConfiguration)
    {
        this.classes = classes;
        if(classes == null)
        {
            throw new IllegalArgumentException("classes cannot be 'null'");
        }

        this.xmlConfiguration = xmlConfiguration;
        if(xmlConfiguration == null)
        {
            throw new IllegalArgumentException("xmlConfiguration cannot be 'null'");
        }
    }

    /**
     * Build component.
     * @return
     */
    public Converter build()
    {
        ObjectToXMLStringConverter objectToXMLStringConverter = getObjectToXMLStringConverter(classes);
        objectToXMLStringConverter.setConfiguration(xmlConfiguration);
        if(configuredResourceId != null)
        {
            objectToXMLStringConverter.setConfiguredResourceId(configuredResourceId);
        }
        if(xmlAdapterMap != null)
        {
            objectToXMLStringConverter.setXmlAdapterMap(xmlAdapterMap);
        }

        return objectToXMLStringConverter;
    }

    /**
     * Factory method to aid testing of this class.
     * @param classes
     * @return
     */
    protected ObjectToXMLStringConverter getObjectToXMLStringConverter(List<Class> classes)
    {
        return new ObjectToXMLStringConverter(classes);
    }

    @Override
    public ObjectToXmlStringConverterBuilder setObjectClass(Class cls)
    {
        this.classes.add(cls);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setObjectClasses(List<Class> classes)
    {
        this.classes.addAll(classes);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setSchema(String schema)
    {
        this.xmlConfiguration.setSchema(schema);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setSchemaLocation(String schemaLocation)
    {
        this.xmlConfiguration.setSchemaLocation(schemaLocation);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setNoNamespaceSchema(boolean noNamespaceSchema)
    {
        this.xmlConfiguration.setNoNamespaceSchema(noNamespaceSchema);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setValidate(boolean validate)
    {
        this.xmlConfiguration.setValidate(validate);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setUseNamespacePrefix(boolean useNamespacePrefix)
    {
        this.xmlConfiguration.setUseNamespacePrefix(useNamespacePrefix);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setRootName(String rootName)
    {
        this.xmlConfiguration.setRootName(rootName);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setNamespaceURI(String namespaceURI)
    {
        this.xmlConfiguration.setNamespaceURI(namespaceURI);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setNamespacePrefix(String namespacePrefix)
    {
        this.xmlConfiguration.setNamespacePrefix(namespacePrefix);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setRootClassName(String rootClassName)
    {
        this.xmlConfiguration.setRootClassName(rootClassName);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setRouteOnValidationException(boolean routeOnValidationException)
    {
        this.xmlConfiguration.setRouteOnValidationException(routeOnValidationException);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setFastFailOnConfigurationLoad(boolean fastFailOnConfigurationLoad)
    {
        this.xmlConfiguration.setFastFailOnConfigurationLoad(fastFailOnConfigurationLoad);
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setXmlAdapterMap(Map<Class, XmlAdapter> xmlAdapterMap)
    {
        this.xmlAdapterMap = xmlAdapterMap;
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setConfiguration(XmlConfiguration xmlConfiguration)
    {
        this.xmlConfiguration = xmlConfiguration;
        return this;
    }

    @Override
    public ObjectToXmlStringConverterBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
        return this;
    }
}

