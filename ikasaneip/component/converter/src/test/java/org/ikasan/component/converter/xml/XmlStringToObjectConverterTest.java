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

import static org.junit.Assert.assertEquals;

import org.ikasan.component.converter.xml.jaxb.Example;
import org.ikasan.spec.component.transformation.TransformationException;
import org.junit.Test;

/**
 * Uses Jaxb2Marshaller to unmarshall xml to java object
 * 
 * @author Ikasan Development Team
 */
public class XmlStringToObjectConverterTest 
{
 
    @Test
    public void testConvertWithContextPath(){
        XmlStringToObjectConverter converter = new XmlStringToObjectConverter();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPath("org.ikasan.component.converter.xml.jaxb");
        converter.setConfiguration(configuration);
        Example converted = (Example)converter.convert(new ExampleEventFactory().getXmlEvent());
        assertEquals(new Example("1", "2"), converted);
    }
    
    @Test
    public void testConvertWithClass(){
        XmlStringToObjectConverter converter = new XmlStringToObjectConverter();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setClassesToBeBound(new Class[]{org.ikasan.component.converter.xml.jaxb.Example.class});
        converter.setConfiguration(configuration);
        Example converted = (Example)converter.convert(new ExampleEventFactory().getXmlEvent());
        assertEquals(new Example("1", "2"), converted);
    }
    
    @Test
    public void testConvertWithWithContextPaths(){
        XmlStringToObjectConverter converter = new XmlStringToObjectConverter();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPaths(new String[]{"org.ikasan.component.converter.xml.jaxb"});
        converter.setConfiguration(configuration);
        Example converted = (Example)converter.convert(new ExampleEventFactory().getXmlEvent());
        assertEquals(new Example("1", "2"), converted);
    }
    
    @Test(expected = TransformationException.class)
    public void testConvertWithWithTransformationException(){
        XmlStringToObjectConverter converter = new XmlStringToObjectConverter();
        XmlStringToObjectConfiguration configuration = new XmlStringToObjectConfiguration();
        configuration.setContextPath("org.ikasan.component.converter.xml.jaxb");
        converter.setConfiguration(configuration);
        Example converted = (Example)converter.convert("badly drawn xml");
    }


  



}
