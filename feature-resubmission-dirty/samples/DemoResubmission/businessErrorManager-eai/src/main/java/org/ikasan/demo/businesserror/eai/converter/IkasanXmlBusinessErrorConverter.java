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
