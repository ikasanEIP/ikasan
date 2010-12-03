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
package org.ikasan.framework.error.serialisation;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.junit.Test;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class DefaultErrorOccurrenceXmlConverterTest {

	private static final String PAYLOAD_1_ID = "myPayload";
	private static final String PAYLOAD_1_CONTENT_STRING = "payload1Content";
	private static final String PAYLOAD_1_ATTRIBUTE_1_NAME = "payload1Attribute1Name";
	private static final String PAYLOAD_1_ATTRIBUTE_1_VALUE = "payload1Attribute1Value";
	private static final String PAYLOAD_1_ATTRIBUTE_2_NAME = "payload1Attribute2Name";
	private static final String PAYLOAD_1_ATTRIBUTE_2_VALUE = "payload1Attribute2Value";
	private static final String PAYLOAD_2_ID = "myOtherPayload";
	private static final String PAYLOAD_2_CONTENT_STRING = "payload2Content";
	private static final String PAYLOAD_2_ATTRIBUTE_1_NAME = "payload2Attribute1Name";
	private static final String PAYLOAD_2_ATTRIBUTE_1_VALUE = "payload2Attribute1Value";
	private static final String PAYLOAD_2_ATTRIBUTE_2_NAME = "payload2Attribute2Name";
	private static final String PAYLOAD_2_ATTRIBUTE_2_VALUE = "payload2Attribute2Value";
	private static final int PRIORITY = 6;
	private static final String EVENT_ID = "eventId";
	private static final String FLOW_ELEMENT_NAME = "flowElementName";
	private static final String FLOW_NAME = "flowName";

	private static final String MODULE_NAME = "moduleName";
	private static final String ACTION_TAKEN = "action taken";
	
	
	@Test
	public void testToXml() throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
		DefaultErrorOccurrenceXmlConverter errorOccurrenceConverter = new DefaultErrorOccurrenceXmlConverter();
		Payload payload1 = new DefaultPayload(PAYLOAD_1_ID, PAYLOAD_1_CONTENT_STRING.getBytes());
		payload1.setAttribute(PAYLOAD_1_ATTRIBUTE_1_NAME, PAYLOAD_1_ATTRIBUTE_1_VALUE);
		payload1.setAttribute(PAYLOAD_1_ATTRIBUTE_2_NAME, PAYLOAD_1_ATTRIBUTE_2_VALUE);
		
		Payload payload2 = new DefaultPayload(PAYLOAD_2_ID, PAYLOAD_2_CONTENT_STRING.getBytes());
		payload2.setAttribute(PAYLOAD_2_ATTRIBUTE_1_NAME, PAYLOAD_2_ATTRIBUTE_1_VALUE);
		payload2.setAttribute(PAYLOAD_2_ATTRIBUTE_2_NAME, PAYLOAD_2_ATTRIBUTE_2_VALUE);
		
		List<Payload> payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		Event event = new Event(EVENT_ID, PRIORITY, new Date(), payloads);
		
		
		
		ErrorOccurrence errorOccurence = new ErrorOccurrence(new RuntimeException(),event, MODULE_NAME, FLOW_NAME,FLOW_ELEMENT_NAME, new Date(), ACTION_TAKEN);
		

		
		String xml = errorOccurrenceConverter.toXml(errorOccurence);
		    
        DocumentBuilderFactory dbf =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));

        Document doc = db.parse(is);
       
        Element errorOccurrenceElement = doc.getDocumentElement();
        Assert.assertEquals("errorOccurrence", errorOccurrenceElement.getNodeName());
        
        
        
        assertContainsTextElement(errorOccurrenceElement, "actionTaken", ACTION_TAKEN);
        assertContainsTextElement(errorOccurrenceElement, "eventId", EVENT_ID);
        assertContainsTextElement(errorOccurrenceElement, "flowElementName", FLOW_ELEMENT_NAME);
        assertContainsTextElement(errorOccurrenceElement, "flowName", FLOW_NAME);
        assertNotNullTextElement(errorOccurrenceElement, "logTime");
        assertContainsTextElement(errorOccurrenceElement, "moduleName", MODULE_NAME);
        assertNotNullTextElement(errorOccurrenceElement, "url");
        
        Element errorEventElement = (Element) getNode(errorOccurrenceElement, "errorEvent");
        Assert.assertNotNull(errorEventElement);
        

        assertContainsTextElement(errorEventElement, "id", EVENT_ID);
        assertContainsTextElement(errorEventElement, "priority", ""+PRIORITY);
        assertNotNullTextElement(errorEventElement, "timeStamp");
        
        List<Element> payloadElements = getNamedChildNodes(errorEventElement, "payload");
        Assert.assertEquals(2, payloadElements.size());
        
        //check payload 1
        Element payloadElement = payloadElements.get(0);
		assertElementContainsAttribute(payloadElement, "ordinal","0");
        assertContainsTextElement(payloadElement, "id", PAYLOAD_1_ID);
        assertContainsCDATASectionElement(payloadElement, "content", PAYLOAD_1_CONTENT_STRING);
        List<Element> payload1AttributeElements = getNamedChildNodes(payloadElement, "attribute");
        Assert.assertEquals(2, payload1AttributeElements.size());
        Element payload1Attribute1Element = payload1AttributeElements.get(0);
        assertElementContainsAttribute(payload1Attribute1Element, "name",PAYLOAD_1_ATTRIBUTE_1_NAME);
        assertElementContainsAttribute(payload1Attribute1Element, "value",PAYLOAD_1_ATTRIBUTE_1_VALUE);
        Element payload1Attribute2Element = payload1AttributeElements.get(1);
        assertElementContainsAttribute(payload1Attribute2Element, "name",PAYLOAD_1_ATTRIBUTE_2_NAME);
        assertElementContainsAttribute(payload1Attribute2Element, "value",PAYLOAD_1_ATTRIBUTE_2_VALUE);
        
        
        
        //check payload 2
        Element payload2Element = payloadElements.get(1);
        Assert.assertEquals("1", payload2Element.getAttribute("ordinal"));
        assertContainsTextElement(payload2Element, "id", PAYLOAD_2_ID);
        assertContainsCDATASectionElement(payload2Element, "content", PAYLOAD_2_CONTENT_STRING);
        List<Element> payload2AttributeElements = getNamedChildNodes(payload2Element, "attribute");
        Assert.assertEquals(2, payload2AttributeElements.size());
        Element payload2Attribute1Element = payload2AttributeElements.get(0);
        assertElementContainsAttribute(payload2Attribute1Element, "name",PAYLOAD_2_ATTRIBUTE_1_NAME);
        assertElementContainsAttribute(payload2Attribute1Element, "value",PAYLOAD_2_ATTRIBUTE_1_VALUE);
        Element payload2Attribute2Element = payload2AttributeElements.get(1);
        assertElementContainsAttribute(payload2Attribute2Element, "name",PAYLOAD_2_ATTRIBUTE_2_NAME);
        assertElementContainsAttribute(payload2Attribute2Element, "value",PAYLOAD_2_ATTRIBUTE_2_VALUE);

		        
		        
		    

	}
	
	
	
	private void assertElementContainsAttribute(Element element,
			String attributeName, String attributeValue) {
		Assert.assertEquals(attributeValue, element.getAttribute(attributeName));
	}
	private List<Element> getNamedChildNodes(Element element,
			String childElementName) {
		List<Element> result = new ArrayList<Element>();
		
		NodeList childNodes = element.getChildNodes();
		for (int i=0;i<childNodes.getLength();i++){
			Node node = childNodes.item(i);
			if (node instanceof Element){
				Element thisElement = (Element)node;
				if (thisElement.getNodeName().equalsIgnoreCase(childElementName)){
					result.add(thisElement);
				}
			}
		}
		
		return result;
	}
	private void assertContainsTextElement(Element parentElement,
			String elementName, String textValue) {
		Element element = (Element) getNode(parentElement, elementName);
		Assert.assertEquals(textValue, element.getTextContent());
	}
	
	private void assertContainsCDATASectionElement(Element parentElement,
			String elementName, String textValue) {
		Element element = (Element) getNode(parentElement, elementName);
		CDATASection cdataSection = (CDATASection) element.getFirstChild();
		
		Assert.assertEquals(textValue, cdataSection.getTextContent());
	}
	
	private void assertNotNullTextElement(Element element,
			String elementName) {
		Element childElement = (Element) getNode(element, elementName);
		Assert.assertNotNull( childElement.getTextContent());
	}
	
	
	private Node getNode(Element element, String nodeName) {
		NodeList nodeList = element.getChildNodes();
        for (int i=0;i<nodeList.getLength();i++){
        	Node node = nodeList.item(i);
        	if (node.getNodeName().equalsIgnoreCase(nodeName)){
        		return node;
        	}
        }
		return null;
	}
	public static String getCharacterDataFromElement(Element e) {
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData) {
	       CharacterData cd = (CharacterData) child;
	       return cd.getData();
	    }
	    return "?";
	  }

}
