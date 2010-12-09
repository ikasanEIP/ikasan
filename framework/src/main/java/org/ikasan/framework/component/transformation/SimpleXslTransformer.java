/**
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
 * =============================================================================
 */

package org.ikasan.framework.component.transformation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
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
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.spec.transformation.TransformationException;
import org.ikasan.spec.transformation.Translator;
import org.xml.sax.InputSource;

/**
 * Simple Xsl Transformer implementation that only supports compiled (static) stylesheets
 * 
 * @author Ikasan Development Team
 *
 */
public class SimpleXslTransformer implements Translator{

	/**
	 * compiled stylesheets
	 */
	private Templates templates;
    
    /**
     * Constructor
     * 
     * @param transformerFactory
     * @param classpathXslResource
     * @throws TransformerConfigurationException
     */
    public SimpleXslTransformer(TransformerFactory transformerFactory, String classpathXslResource) throws TransformerConfigurationException{
    	this.templates = transformerFactory.newTemplates(new StreamSource(getInputStreamFromClassPath(classpathXslResource)));
    }
	
    private InputStream getInputStreamFromClassPath(String classpathXslResource) {
    	InputStream inputStream = getClass().getClassLoader().getResourceAsStream(classpathXslResource);
		if (inputStream==null){
			throw new IllegalArgumentException();
		}
    	return inputStream;
	}

	/**
     * Constructor
     * 
     * @param transformerFactory
     * @param streamSource
     * @throws TransformerConfigurationException
     */
    public SimpleXslTransformer(TransformerFactory transformerFactory, StreamSource streamSource) throws TransformerConfigurationException{
    	this.templates = transformerFactory.newTemplates(streamSource);
    }
    
    
	/* (non-Javadoc)
	 * @see org.ikasan.framework.component.transformation.Transformer#onEvent(org.ikasan.framework.component.Event)
	 */
	public void onEvent(Event event) throws TransformationException {
		for (Payload payload : event.getPayloads()){
			transformPayload(payload);
		}
	}
	
	/**
	 * Transform the payload
	 * 
	 * @param payload
	 */
	private void transformPayload(Payload payload) {
		ByteArrayOutputStream transformedDataStream = new ByteArrayOutputStream();
        InputSource inputSource = new InputSource(new ByteArrayInputStream(payload.getContent()));

        SAXSource saxSource = new SAXSource(inputSource);
 
		try {
			templates.newTransformer().transform(saxSource, new StreamResult(transformedDataStream));
			payload.setContent(transformedDataStream.toByteArray());
		
		} catch (TransformerConfigurationException e) {
			throw new TransformationException(e);
		} catch (TransformerException e) {
			throw new TransformationException(e);
		}
	}

}
