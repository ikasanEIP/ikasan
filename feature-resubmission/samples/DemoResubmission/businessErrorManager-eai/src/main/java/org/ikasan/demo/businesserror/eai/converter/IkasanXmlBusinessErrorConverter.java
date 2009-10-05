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
package org.ikasan.demo.businesserror.eai.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ikasan.demo.businesserror.model.BusinessError;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class IkasanXmlBusinessErrorConverter implements BusinessErrorConverter<String> {

	private DocumentBuilder documentBuilder;
	
	private TransformerFactory transformerFactory;
	
	public IkasanXmlBusinessErrorConverter(TransformerFactory transformerFactory)throws ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		documentBuilder = dbf.newDocumentBuilder();
		this.transformerFactory = transformerFactory;
	}
	public BusinessError convertFrom(String xmlString, String originatingSystem) {
		
		String externalReference = null;
		String errorMessage = null;
	
		Document document;
		try {
			document = documentBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
			Element rootElement = document.getDocumentElement();
			NodeList childNodes = rootElement.getChildNodes();
			for (int i=0;i<childNodes.getLength();i++){
				Node childNode = childNodes.item(i);
				if (childNode.getNodeName().equalsIgnoreCase("id")){
					externalReference = childNode.getTextContent();
				} 
				if (childNode.getNodeName().equalsIgnoreCase("message")){
					errorMessage = childNode.getTextContent();
				}
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return new BusinessError(originatingSystem, externalReference, errorMessage);
	}

	public String convertTo(BusinessError businessError) {
		DOMImplementation impl = documentBuilder.getDOMImplementation();
		Document doc = impl.createDocument(null, "error", null);
		Element root = doc.getDocumentElement();

		createTextElement(doc, root, "id", businessError.getExternalReference());
		createTextElement(doc, root, "message", businessError.getErrorMessage());


		StringWriter stringWriter = new StringWriter();
		try {
			DOMSource domSource = new DOMSource(doc);
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult streamResult = new StreamResult(stringWriter);
			transformer.transform(domSource, streamResult);

		} catch (TransformerException transformerException) {
			throw new RuntimeException(transformerException);
		}

		return stringWriter.toString();
	}
	
	private void createTextElement(Document doc, Element parent,
			String elementName, String elementValue) {
		Element element = doc.createElement(elementName);
		element.setTextContent(elementValue);
		parent.appendChild(element);

	}

}
