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

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ikasan.common.Payload;
import org.ikasan.spec.flow.event.FlowEvent;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultErrorOccurrenceXmlConverter implements ErrorOccurrenceXmlConverter{

	private DocumentBuilder documentBuilder;
	
	private TransformerFactory transformerFactory;
	
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
	
	public DefaultErrorOccurrenceXmlConverter() throws ParserConfigurationException, TransformerConfigurationException{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		this.transformerFactory = transformerFactory = TransformerFactory.newInstance();
		
	}

	
	public String toXml(ErrorOccurrence errorOccurence) throws TransformerException {
		Document dom = documentBuilder.newDocument();
		Element errorOccurrenceElement = dom.createElement("errorOccurrence");
		dom.appendChild(errorOccurrenceElement);
		
		
		addChildTextElement(dom, errorOccurrenceElement, "errorDetail", errorOccurence.getErrorDetail());
		addChildTextElement(dom, errorOccurrenceElement, "actionTaken", errorOccurence.getActionTaken());
		addChildTextElement(dom, errorOccurrenceElement, "eventId", errorOccurence.getFlowEventId());
		addChildTextElement(dom, errorOccurrenceElement, "flowElementName", errorOccurence.getFlowElementName());
		addChildTextElement(dom, errorOccurrenceElement, "flowName", errorOccurence.getFlowName());
		addChildTextElement(dom, errorOccurrenceElement, "initiatorName", errorOccurence.getInitiatorName());
		addChildTextElement(dom, errorOccurrenceElement, "logTime", dateFormat.format(errorOccurence.getLogTime()));
		addChildTextElement(dom, errorOccurrenceElement, "moduleName", errorOccurence.getModuleName());
		addChildTextElement(dom, errorOccurrenceElement, "url", errorOccurence.getUrl());
		
		FlowEvent errorFlowEvent = errorOccurence.getErrorFlowEvent();
		if (errorFlowEvent!=null){
			Element errorFlowEventElement = dom.createElement("errorFlowEvent");
			errorOccurrenceElement.appendChild(errorFlowEventElement);
			
			addChildTextElement(dom, errorFlowEventElement, "id", errorFlowEvent.getIdentifier());

			// TODO - where do we get priority
//			addChildTextElement(dom, errorFlowEventElement, "priority", ""+ errorFlowEvent.getPriority());
			addChildTextElement(dom, errorFlowEventElement, "timestamp",dateFormat.format(errorFlowEvent.getTimestamp()));
	
			// TODO - genericise payload correctly, until then just make this compile
//            List<Payload> payloads = errorFlowEvent.getPayloads();
            List<Payload> payloads = new ArrayList<Payload>();
			for (int i=0;i<payloads.size();i++){
				Payload payload = payloads.get(i);
				Element payloadElement = dom.createElement("payload");
				payloadElement.setAttribute("ordinal",""+i);
				addChildTextElement(dom, payloadElement, "id", payload.getId());
				
				//payload content
				Element payloadContent = dom.createElement("content");
				CDATASection payloadContentCDATASection =null;
				try {
					payloadContentCDATASection = dom.createCDATASection(new String(payload.getContent(), "UTF8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				payloadContent.appendChild(payloadContentCDATASection);
				payloadElement.appendChild(payloadContent);
				
				//payload attributes
				for (String payloadAttributeName : payload.getAttributeNames()){
					String payloadAttributeValue = payload.getAttribute(payloadAttributeName);
					
					//create payload attribute element
					Element payloadAttributeElement = dom.createElement("attribute");
					payloadAttributeElement.setAttribute("name",payloadAttributeName);
					payloadAttributeElement.setAttribute("value",payloadAttributeValue);
					
					payloadElement.appendChild(payloadAttributeElement);
				}
				
				
				errorFlowEventElement.appendChild(payloadElement);

			}
		}
		
		
		
		
		
		
		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);
		
		transformerFactory.newTransformer().transform(new DOMSource(dom), streamResult);
		
       
        return stringWriter.toString();

	}



	private void addChildTextElement(Document dom, Element parentElement,
			String elementName, String text) {
		Element element = dom.createElement(elementName);
		if (text!=null){
			element.appendChild(dom.createTextNode(text));
		}
		parentElement.appendChild(element);
	}

}
