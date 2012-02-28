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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Default implementation of <code>ElementRenderer</code>, for producing a simple String representation of an <errorEvent> element from an <errorOccurrence>
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultErrorEventElementRenderer implements ElementRenderer{

	public String renderElement(Element element) {
		if (!element.getTagName().equalsIgnoreCase("errorEvent")){
			throw new IllegalArgumentException("Encountered illegal element, was expecting 'errorEvent', but found ["+element.getTagName()+"]");
		}
		StringBuffer sb = new StringBuffer("errorEvent[");
		
		sb.append(renderSoleTextElement(element, "id"));
		sb.append(",");
		sb.append(renderSoleTextElement(element, "priority"));
		sb.append(",");
		sb.append(renderSoleTextElement(element, "timestamp"));
		for (Element payloadElement : XmlUtils.getElementsByTagName(element, "payload")){
			sb.append(",");
			sb.append(renderPayloadElement(payloadElement));
		}
		
		
		sb.append("]");
		return sb.toString();
	}

	private String renderPayloadElement(Element payloadElement) {
		StringBuffer sb = new StringBuffer("payload=[");
		sb.append(renderAttribute(payloadElement, "ordinal"));
		sb.append(",");
		sb.append(renderSoleTextElement(payloadElement, "id"));
		sb.append(",");
		sb.append(renderSoleTextElement(payloadElement, "content"));
		for (Element payloadAttributeElement : XmlUtils.getElementsByTagName(payloadElement, "attribute")){
			sb.append(",");
			sb.append(renderPayloadAttributeElement(payloadAttributeElement));
		}
		sb.append("]");
		return sb.toString();
	}
	
	private String renderPayloadAttributeElement(Element payloadAttributeElement) {
		StringBuffer sb = new StringBuffer("attribute=[");
		sb.append(renderAttribute(payloadAttributeElement, "name"));
		sb.append(",");
		sb.append(renderAttribute(payloadAttributeElement, "value"));

		sb.append("]");
		return sb.toString();
	}

	public static String renderSoleTextElement(Element errorEventElement,
			String tagName) {
		Element eventIdElement = XmlUtils.getFirstElementByTagName(errorEventElement,tagName);
		String textTagResult = renderTextElement(tagName, eventIdElement);
		return textTagResult;
	}
	
	public static String renderAttribute(Element element,
			String attributeName) {
		String attributeValue = "null";
		if (element.getAttribute(attributeName)!=null){
			attributeValue=element.getAttribute(attributeName);
		}
		return attributeName+"=["+attributeValue+"]";
	}

	public static String renderTextElement(String tagName, Element eventIdElement) {
		Element textElement = eventIdElement;
		String elementValueText = "null";
		if (textElement!=null){
			elementValueText = textElement.getTextContent();
		}
		String textTagResult = new String(tagName+"=["+elementValueText+"]");
		return textTagResult;
	}

	
	

	
	
	

}
