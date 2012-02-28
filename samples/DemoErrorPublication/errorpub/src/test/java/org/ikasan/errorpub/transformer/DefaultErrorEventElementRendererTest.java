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
package org.ikasan.errorpub.transformer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;




/**
 * @author Ikasan Development Team
 *
 */
public class DefaultErrorEventElementRendererTest {

	private DefaultErrorEventElementRenderer renderer = new DefaultErrorEventElementRenderer();
	
	private DocumentBuilder documentBuilder;
	
	public DefaultErrorEventElementRendererTest() throws ParserConfigurationException{
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testRender_withNonErrorEventElement_willThrowIllegalArgumentException(){
		Document document = documentBuilder.newDocument();
		Element someOtherElement = document.createElement("somethingElse");
		renderer.renderElement(someOtherElement);
	}
	
	@Test()
	public void testRender_withErrorEventElement_willReturnSimple(){
		Document document = documentBuilder.newDocument();
		Element errorEventElement = document.createElement("errorEvent");
		
		addChildTextElement(errorEventElement, "id", "eventId", document);
		addChildTextElement(errorEventElement, "priority", "6", document);
		addChildTextElement(errorEventElement, "timestamp", "10/11/2009 16:52:35.378", document);
		
		errorEventElement.appendChild(createPayloadElement(document, 0, "payload1Id", "payload1Content", new Element[]{
				createPayloadAttributeElement("payload1Attribute1Name", "payload1Attribute1Value", document),
				createPayloadAttributeElement("payload1Attribute2Name", "payload1Attribute2Value", document)
				}));
		errorEventElement.appendChild(createPayloadElement(document, 1, "payload2Id", "payload2Content", new Element[]{
				createPayloadAttributeElement("payload2Attribute1Name", "payload2Attribute1Value", document),
				createPayloadAttributeElement("payload2Attribute2Name", "payload2Attribute2Value", document)
				}));
		
		String text = renderer.renderElement(errorEventElement);
		
		Assert.assertNotNull(text);
		Assert.assertTrue(text.startsWith("errorEvent["));
		Assert.assertTrue(text.endsWith("]"));
		
		String expected = 
				"errorEvent[" +
					"id=[eventId]," +
					"priority=[6]," +
					"timestamp=[10/11/2009 16:52:35.378]," +
					"payload=[" +
						"ordinal=[0],id=[payload1Id],content=[payload1Content]," +
							"attribute=[" +
								"name=[payload1Attribute1Name]," +
								"value=[payload1Attribute1Value]" +
							"]," +
							"attribute=[" +
								"name=[payload1Attribute2Name]," +
								"value=[payload1Attribute2Value]" +
							"]" +
					"]," +
					"payload=[" +
					"ordinal=[1],id=[payload2Id],content=[payload2Content]," +
						"attribute=[" +
							"name=[payload2Attribute1Name]," +
							"value=[payload2Attribute1Value]" +
						"]," +
						"attribute=[" +
							"name=[payload2Attribute2Name]," +
							"value=[payload2Attribute2Value]" +
						"]" +
				"]" +

				"]";
		
		Assert.assertEquals(expected,text);
		
		
		
	}


	private Element createPayloadAttributeElement(String attributeName,
			String attributeValue, Document document) {
		Element result = document.createElement("attribute");
		result.setAttribute("name", attributeName);
		result.setAttribute("value", attributeValue);
		return result;
	}


	private Element createPayloadElement(Document document, int ordinal,
			String payloadId, String payloadContent, Element[] payloadAttributeElements) {
		Element payloadElement = document.createElement("payload");
		payloadElement.setAttribute("ordinal", ""+ordinal);
		addChildTextElement(payloadElement, "id", payloadId, document);
		Element contentElement = document.createElement("content");
		CDATASection payloadContentCDataSection = document.createCDATASection(payloadContent);
		contentElement.appendChild(payloadContentCDataSection);
		payloadElement.appendChild(contentElement);
		for (Element payloadAttributeElement : payloadAttributeElements){
			payloadElement.appendChild(payloadAttributeElement);
		}
		return payloadElement;
	}


	private void addChildTextElement(Element parentElement, String tagName,
			String textContent, Document document) {
		Element textElement = document.createElement(tagName);
		textElement.setTextContent(textContent);
		parentElement.appendChild(textElement);
	}
}
