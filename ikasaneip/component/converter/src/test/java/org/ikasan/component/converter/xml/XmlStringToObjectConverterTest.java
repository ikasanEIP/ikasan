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

import org.ikasan.component.converter.xml.jaxb.Example;
import org.ikasan.spec.component.transformation.TransformationException;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import static org.junit.Assert.assertEquals;

/**
 * Uses Jaxb2Marshaller to unmarshall xml to java object
 * 
 * @author Ikasan Development Team
 */
public class XmlStringToObjectConverterTest 
{

    @Test
    public void testConvertWithContextPath(){
        XmlStringToObjectConverter<String,Example> converter = new XmlStringToObjectConverter<>();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPath("org.ikasan.component.converter.xml.jaxb");
        converter.setConfiguration(configuration);
        Example converted = converter.convert(new ExampleEventFactory().getXmlEvent());
        assertEquals(new Example("1", "2"), converted);
    }

    @Test
    public void testConvertWithContextPathAndPayloadByteArray(){
        XmlStringToObjectConverter<byte[],Example> converter = new XmlStringToObjectConverter<>();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPath("org.ikasan.component.converter.xml.jaxb");
        converter.setConfiguration(configuration);
        Example converted = converter.convert(new ExampleEventFactory().getXmlEvent().getBytes());
        assertEquals(new Example("1", "2"), converted);
    }
    
    @Test(expected = TransformationException.class)
    public void testConvertWithContextPathAndPayloadCantBeConvertedToString(){
        XmlStringToObjectConverter<Object,Example> converter = new XmlStringToObjectConverter<>();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPath("org.ikasan.component.converter.xml.jaxb");
        converter.setConfiguration(configuration);
        Example converted = converter.convert(new Object());
    }
    
    @Test
    public void testConvertWithClass(){
        XmlStringToObjectConverter<String,Example> converter = new XmlStringToObjectConverter<>();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setClassesToBeBound(new Class[] { org.ikasan.component.converter.xml.jaxb.Example.class });
        converter.setConfiguration(configuration);
        Example converted = converter.convert(new ExampleEventFactory().getXmlEvent());
        assertEquals(new Example("1", "2"), converted);
    }
    
    @Test
    public void testConvertWithWithContextPaths(){
        XmlStringToObjectConverter<String,Example> converter = new XmlStringToObjectConverter<>();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPaths(new String[] { "org.ikasan.component.converter.xml.jaxb" });
        converter.setConfiguration(configuration);
        Example converted = converter.convert(new ExampleEventFactory().getXmlEvent());
        assertEquals(new Example("1", "2"), converted);
    }
    
    @Test(expected = TransformationException.class)
    public void testConvertWithWithTransformationException(){
        XmlStringToObjectConverter<String,Example> converter = new XmlStringToObjectConverter<>();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPath("org.ikasan.component.converter.xml.jaxb");
        converter.setConfiguration(configuration);
        Example converted = converter.convert("badly drawn xml");
    }

    @Test(expected = XmlValidationException.class)
    public void testConvertInvalidJaxbEventHandler()
    {
        XmlStringToObjectConverter<String,Example> converter = new XmlStringToObjectConverter<>();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPath("org.ikasan.component.converter.xml.jaxb");
        configuration.setValidationEventHandler(new ValidationEventHandler(){
            @Override
            public boolean handleEvent(ValidationEvent event)
            {
                if(ValidationEvent.ERROR == event.getSeverity())
                {
                    throw new XmlValidationException(event);
                }
                else
                {
                    Assert.fail("Should be a ValidationEvent.ERROR (1) type event : " + event.getSeverity());
                    return false;
                }
            }
        });

        converter.setConfiguration(configuration);
        converter.convert(new ExampleEventFactory().getXmlInvalidJaxb());
    }
}
