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
package org.ikasan.framework.component.transformation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class SimpleXslTransformerTest {

    private static final String CLASSPATH_XSL_RESOURCE = "copyAll.xsl";

	/** Mockery */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    private Event event = mockery.mock(Event.class);
    
    private Payload payload = mockery.mock(Payload.class);
    
    private String inputXml="<root><node1/><node2/></root>";
    
    private String outputXml = "<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>"+inputXml;
    
    private List<Payload> payloads = new ArrayList<Payload>();
    
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    
    public SimpleXslTransformerTest(){
    	payloads.add(payload);
    }
    
	/**
	 * Tests that a stylesheet can be loaded from the classpath and applied
	 * 
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 */
	@Test
	public void testOnEvent() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		SimpleXslTransformer simpleXslTransformer = new SimpleXslTransformer(transformerFactory, CLASSPATH_XSL_RESOURCE);
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();
                will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(inputXml.getBytes()));
                one(payload).setContent(outputXml.getBytes());
            }
        });
		
		simpleXslTransformer.onEvent(event);
		
		mockery.assertIsSatisfied();
	}
	
	/**
	 * Tests that constructor throws IllegalArgumentExcption if an unknown claspath resource is passed in
	 * 
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorWithMissingResourceWillThrowIllegalArgumentExcption() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		new SimpleXslTransformer(transformerFactory, "unknown");
	}

}
