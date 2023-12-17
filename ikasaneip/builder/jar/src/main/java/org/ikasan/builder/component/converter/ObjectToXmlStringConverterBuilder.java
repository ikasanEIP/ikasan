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
import org.ikasan.builder.component.Builder;
import org.ikasan.component.converter.xml.XmlConfiguration;
import org.ikasan.spec.component.transformation.Converter;

import java.util.List;
import java.util.Map;

/**
 * Contract for a default ObjectToXmlString converter builder.
 *
 * @author Ikasan Development Team.
 */
public interface ObjectToXmlStringConverterBuilder extends Builder<Converter>
{
    /** Class of the object to be serialised */
    ObjectToXmlStringConverterBuilder setObjectClass(Class cls);

    /** Classes of the objects to be serialised */
    ObjectToXmlStringConverterBuilder setObjectClasses(List<Class> classes);

    /** actual schema against which validation will occur */
    ObjectToXmlStringConverterBuilder setSchema(String schema);

    /** schema location as put in the root attribute of the generated XML */
    ObjectToXmlStringConverterBuilder setSchemaLocation(String schemaLocation);

    /** is this a no namespace schema */
    ObjectToXmlStringConverterBuilder setNoNamespaceSchema(boolean noNamespaceSchema);

    /** whether to validate the generated XML against the schema */
    ObjectToXmlStringConverterBuilder setValidate(boolean validate);

    /** should we use namespace prefixes */
    ObjectToXmlStringConverterBuilder setUseNamespacePrefix(boolean useNamespacePrefix);

    /** optionally set root name (QNAME) */
    ObjectToXmlStringConverterBuilder setRootName(String rootName);

    /** optionally set namespace URI (for QNAME) */
    ObjectToXmlStringConverterBuilder setNamespaceURI(String namespaceURI);

    /** optionally set namespace prefix (for QNAME) */
    ObjectToXmlStringConverterBuilder setNamespacePrefix(String namespacePrefix);

    /** optionally set root class name (QNAME) */
    ObjectToXmlStringConverterBuilder setRootClassName(String rootClassName);

    /** whether to route an XML validation failure to the next component (true) or throw an exception and rollback (false) */
    ObjectToXmlStringConverterBuilder setRouteOnValidationException(boolean routeOnValidationException);

    /**
     * determines whether we let the component fail if the initial
     * setting and loading of configuration fails.
     */
    ObjectToXmlStringConverterBuilder setFastFailOnConfigurationLoad(boolean fastFailOnConfigurationLoad);

    ObjectToXmlStringConverterBuilder setXmlAdapterMap(Map<Class, XmlAdapter> xmlAdapterMap);

    ObjectToXmlStringConverterBuilder setConfiguration(XmlConfiguration xmlConfiguration);

    ObjectToXmlStringConverterBuilder setConfiguredResourceId(String configuredResourceId);
}

