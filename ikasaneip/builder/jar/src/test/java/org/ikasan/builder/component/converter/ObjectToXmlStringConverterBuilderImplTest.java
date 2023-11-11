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

import org.ikasan.component.converter.xml.ObjectToXMLStringConverter;
import org.ikasan.component.converter.xml.XmlConfiguration;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>ObjectToXmlStringConverterBuilderImpl</code> class.
 *
 * @author Ikasan Development Team
 */
class ObjectToXmlStringConverterBuilderImplTest
{
    /**
     * Test successful builder creation.
     */
    @Test
    void objectToXmlStringConverterBuilder_without_constructor_classes()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ObjectToXmlStringConverterBuilderImpl(null, null);
        });
    }

    /**
     * Test successful builder creation.
     */
    @Test
    void objectToXmlStringConverterBuilder_without_constructor_configuration()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ObjectToXmlStringConverterBuilderImpl(new ArrayList(), null);
        });
    }

    /**
     * Test successful builder creation.
     */
    @Test
    void objectToXmlStringConverterBuilder_with_configuration()
    {
        List<Class> classes = new ArrayList<Class>();
        List<Class> objectClasses = new ArrayList<Class>();
        objectClasses.add(Integer.class);

        Map<Class,XmlAdapter> xmlAdapters = new HashMap<Class,XmlAdapter>();

        XmlConfiguration configuration = new XmlConfiguration();
        ObjectToXmlStringConverterBuilderImplExtendedForTest builder = new ObjectToXmlStringConverterBuilderImplExtendedForTest(classes, configuration);
        Converter converter = builder
                .setConfiguredResourceId("configuredResourceId")
                .setConfiguration(configuration)
                .setRootName("rootName")
                .setRootClassName("java.lang.String")
                .setFastFailOnConfigurationLoad(true)
                .setNamespacePrefix("namespacePrefix")
                .setNamespaceURI("namespaceURI")
                .setNoNamespaceSchema(true)
                .setObjectClass(String.class)
                .setObjectClasses(objectClasses)
                .setRouteOnValidationException(true)
                .setSchema("example.xsd")
                .setSchemaLocation("http://foo.com/domain example.xsd")
                .setUseNamespacePrefix(true)
                .setValidate(true)
                .setXmlAdapterMap(xmlAdapters)
                .build();


        assertTrue(converter instanceof Converter, "instance should be a Converter");
        assertEquals("configuredResourceId", ((ConfiguredResource)converter).getConfiguredResourceId(), "Converter configuredResourceId should be 'configuredResourceId'");
        assertTrue(((ObjectToXMLStringConverter) converter).getXmlAdapterMap() == xmlAdapters , "xmlAdapterMap should be the same object'");
        assertTrue(((ObjectToXMLStringConverter) converter).getXmlAdapterMap().isEmpty() , "xmlAdapterMap should be 'empty'");

        XmlConfiguration builtConfiguration = ((ConfiguredResource<XmlConfiguration>) converter).getConfiguration();
        assertEquals("rootName", builtConfiguration.getRootName(), "rootName should be 'rootName'");
        assertEquals("java.lang.String", builtConfiguration.getRootClassName(), "rootClassName should be 'java.lang.String'");
        assertTrue(builtConfiguration.isFastFailOnConfigurationLoad(), "fastFailOnConfigurationLoad should be 'true'");
        assertEquals("namespacePrefix", builtConfiguration.getNamespacePrefix(), "namespacePrefix should be 'namespacePrefix'");
        assertEquals("namespaceURI", builtConfiguration.getNamespaceURI(), "namespaceURI should be 'namespaceURI'");
        assertTrue(builtConfiguration.isNoNamespaceSchema(), "noNamespaceSchema should be 'true'");
        assertTrue(builtConfiguration.isRouteOnValidationException(), "routeOnValidationException should be 'true'");
        assertEquals("example.xsd", builtConfiguration.getSchema(), "schema should be 'example.xsd'");
        assertEquals("http://foo.com/domain example.xsd", builtConfiguration.getSchemaLocation(), "schemaLocation should be 'http://foo.com/domain example.xsd'");
        assertTrue(builtConfiguration.isUseNamespacePrefix(), "useNamespacePrefix should be 'useNamespacePrefix'");
        assertTrue(builtConfiguration.isValidate(), "validate should be 'true'");
        assertEquals(2, builder.getClasses().size(), "objectClasses size should be '2'");
        assertEquals(String.class, builder.getClasses().get(0), "objectClasses entry 1 should be 'String.class'");
        assertEquals(Integer.class, builder.getClasses().get(1), "objectClasses entry 2 should be 'Integer.class'");
    }

    /**
     * Extended class to access the classes passed on constructor to the ObjectToXmlStringConverter
     */
    class ObjectToXmlStringConverterBuilderImplExtendedForTest extends ObjectToXmlStringConverterBuilderImpl
    {
        /**
         * Constructor
         *
         * @param classes
         * @param xmlConfiguration
         */
        public ObjectToXmlStringConverterBuilderImplExtendedForTest(List<Class> classes, XmlConfiguration xmlConfiguration)
        {
            super(classes, xmlConfiguration);
        }

        protected ObjectToXMLStringConverter getObjectToXmlStringConverter(List<Class> classes)
        {
            return super.getObjectToXMLStringConverter(classes);
        }

        public List<Class> getClasses()
        {
            return super.classes;
        }
    }
}
