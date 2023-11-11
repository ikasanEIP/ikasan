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
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.ValidationEventHandler;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>XmlStringToObjectConverterBuilderImpl</code> class.
 *
 * @author Ikasan Development Team
 */
class XmlStringToObjectConverterBuilderImplTest
{
    /**
     * Test successful builder creation.
     */
    @Test
    void xmlStringToObjectConverterBuilder_without_constructor_classes()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new XmlStringToObjectConverterBuilderImpl(null);
        });
    }

    /**
     * Test successful builder creation.
     */
    @Test
    void xmlStringToObjectConverterBuilder_with_configuration()
    {
        List<Class> classes = new ArrayList<Class>();
        classes.add(java.lang.Integer.class);
        ValidationEventHandler validationEventHandler = null;

        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        XmlStringToObjectConverterBuilderImpl builder = new XmlStringToObjectConverterBuilderImpl(configuration);
        Converter converter = builder
                .setConfiguredResourceId("configuredResourceId")
                .setConfiguration(configuration)
                .setClassToBeBound(org.ikasan.builder.component.converter.Example.class)
                .setAutoConvertElementToValue(true)
                .setClassesToBeBound(classes)
                .setContextPath("org.ikasan.builder.component.converter")
                .setContextPaths( new String[]{"org.ikasan.builder.component.converter", "org.ikasan.builder.component.converter" } )
                .setMarshallerProperties(null)
                .setSchema("schema")
                .setUnmarshallerProperties(null)
                .setValidationEventHandler(validationEventHandler)
                .build();


        assertTrue(converter instanceof Converter, "instance should be a Converter");
        assertEquals("configuredResourceId", ((ConfiguredResource)converter).getConfiguredResourceId(), "Converter configuredResourceId should be 'configuredResourceId'");

        XmlStringToObjectConfiguration builtConfiguration = ((ConfiguredResource<XmlStringToObjectConfiguration>) converter).getConfiguration();
        Class[] boundClasses = builtConfiguration.getClassesToBeBound();
        assertEquals(2, boundClasses.length, "classesToBeBound should contain " + boundClasses.length);
        assertEquals(org.ikasan.builder.component.converter.Example.class, boundClasses[0], "classesToBeBound should be " + boundClasses[0]);
        assertEquals(java.lang.Integer.class, boundClasses[1], "classesToBeBound should be " + boundClasses[1]);
        assertEquals("org.ikasan.builder.component.converter", builtConfiguration.getContextPath(), "contextPath should be 'true'");
        assertTrue(builtConfiguration.getContextPaths().length > 0, "contextPaths should contain 2");
        assertNull(builtConfiguration.getMarshallerProperties(), "MarshallerProperties should be 'null'");
        assertEquals("schema", builtConfiguration.getSchema(), "schema should be 'schema'");
        assertNull(builtConfiguration.getUnmarshallerProperties(), "UnmarshallerProperties should be 'null'");
        assertNull(builtConfiguration.getValidationEventHandler(), "ValidationEventHandler should be 'null'");
    }
}
