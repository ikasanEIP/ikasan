package org.ikasan.testing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.ikasan.attributes.AttributeResolver;
import org.ikasan.attributes.FirstStrikeAttributeResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
import org.xml.sax.SAXException;

/**
 * Base test class for testing exception XML files against AttributeResolver configuration
 * 
 * @author Ikasan Development Team
 *
 */
public class BaseAttributeResolverConfigTest implements BeanFactoryAware {

	
	private FirstStrikeAttributeResolver firstStrikeAttributeResolver;
	
	private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	
	private DocumentBuilder documentBuilder;
	
	public BaseAttributeResolverConfigTest(){
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	public Map<String, Object> resolveAttributesForErrorFile(String errorFileName) {
		byte[] bytes = null;
		try {
			bytes = loadFile(errorFileName);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		Document errorOccurrenceDocument = null;
		try {
			errorOccurrenceDocument = documentBuilder.parse(new ByteArrayInputStream(bytes));
		} catch (SAXException e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		Element errorOccurrenceElement = (Element) errorOccurrenceDocument.getFirstChild();
	
		Map<String, Object> resolvedAttributes = firstStrikeAttributeResolver.resolveAttributes(errorOccurrenceElement);
		return resolvedAttributes;
	}
	
	
	protected byte[] loadFile(String fileName) throws IOException
    {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (resourceAsStream==null){
        	throw new IOException("File not found:"+fileName);
        }
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read = 0;
        while (read>-1){
            read = resourceAsStream.read();
            if (read!=-1){
                byteArrayOutputStream.write(read);
            }
        }
        byte[] content = byteArrayOutputStream.toByteArray();
        return content;
    }
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		ListableBeanFactory listableBeanFactory = (ListableBeanFactory)beanFactory;
		
		Map beanNamesForType = listableBeanFactory.getBeansOfType(AttributeResolver.class);
		List<AttributeResolver> resolvers = new ArrayList<AttributeResolver>();
		resolvers.addAll(beanNamesForType.values());
		
		firstStrikeAttributeResolver = new FirstStrikeAttributeResolver(resolvers);
	}
}
