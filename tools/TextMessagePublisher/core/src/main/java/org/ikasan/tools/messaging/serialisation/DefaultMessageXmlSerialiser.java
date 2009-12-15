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
package org.ikasan.tools.messaging.serialisation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.ikasan.tools.messaging.model.MapMessageWrapper;
import org.ikasan.tools.messaging.model.MessageWrapper;
import org.ikasan.tools.messaging.model.TextMessageWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;



public class DefaultMessageXmlSerialiser implements MessageXmlSerialiser{
	
	private Logger logger = Logger.getLogger(DefaultMessageXmlSerialiser.class);

	public String toXml(MessageWrapper message) {
		
		String xml = "";


        // Document (Xerces implementation only).
        Document xmldoc = new DocumentImpl();
        Element root = xmldoc.createElement("Message");
        
        root.setAttribute("messageId", message.getMessageId());
        
        
        
        handleProperties(message, xmldoc, root);
        handleMessagingProperties(message, xmldoc, root);
        if (message instanceof TextMessageWrapper)
        {
            handleTextMessage((TextMessageWrapper) message, xmldoc, root);
        }
        else if (message instanceof MapMessageWrapper)
        {
            handleMapMessage((MapMessageWrapper) message, xmldoc, root);
        }
        xmldoc.appendChild(root);
        
        OutputFormat of = new OutputFormat("XML", "UTF-8", true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer(byteArrayOutputStream, of);
        try {
			serializer.asDOMSerializer();
			serializer.serialize(xmldoc.getDocumentElement());
			byteArrayOutputStream.close();
			xml=new String(byteArrayOutputStream.toByteArray(), "UTF-8");
		} catch (IOException ioException) {
			throw new RuntimeException();
		}

		return xml;
	}
	
	
	
	private void handleProperties(MessageWrapper message, Document xmldoc,
            Element root) 
    {
		Element propertiesElement = xmldoc.createElement("Properties");
		
		
		
        Set<String> propertyNames = message.getProperties().keySet();
        for (String propertyName : propertyNames)
        {
            Object propertyEntry = message.getProperties().get(propertyName);
            Class propertyClass = propertyEntry.getClass();
            Element entryElement = xmldoc.createElement("Property");
            entryElement.setAttribute("class", propertyClass.getName());
            entryElement.setAttribute("name", propertyName);
            entryElement.appendChild(xmldoc.createTextNode(propertyEntry
                .toString()));
            propertiesElement.appendChild(entryElement);
        }
        
        root.appendChild(propertiesElement);
    }
	
	private void handleMessagingProperties(MessageWrapper message, Document xmldoc,
            Element root) 
    {
		Element propertiesElement = xmldoc.createElement("MessagingProperties");
		
		
		
        Set<String> propertyNames = message.getMessagingProperties().keySet();
        for (String propertyName : propertyNames)
        {
            Object propertyEntry = message.getMessagingProperties().get(propertyName);
            Class propertyClass = propertyEntry.getClass();
            Element entryElement = xmldoc.createElement("Property");
            entryElement.setAttribute("class", propertyClass.getName());
            entryElement.setAttribute("name", propertyName);
            entryElement.appendChild(xmldoc.createTextNode(propertyEntry
                .toString()));
            propertiesElement.appendChild(entryElement);
        }
        
        root.appendChild(propertiesElement);
    }
	
    private void handleMapMessage(MapMessageWrapper mapMessage, Document xmldoc,
            Element root) 
    {
        root.setAttribute("type", "MapMessage");
        Element mapElement = xmldoc.createElement("Map");
        Set<String> mapNames = mapMessage.getMap().keySet();
        for (String entryName : mapNames)
        {

            Object mapEntry = mapMessage.getMap().get(entryName);
            Class entryClass = mapEntry.getClass();
            String className = entryClass.getName();
            boolean isByteArray = false;
            if (mapEntry instanceof byte[])
            {
                className = "byte[]";
                isByteArray = true;
            }
            Element entryElement = xmldoc.createElement("Entry");
            entryElement.setAttribute("class", className);
            entryElement.setAttribute("name", entryName);
            String value = mapEntry.toString();
            if (isByteArray)
            {
                byte[] fieldValue = (byte[])mapEntry;
                
                
                //value = new String(fieldValue);
                value = new String(fieldValue);
            }
            entryElement.appendChild(xmldoc.createTextNode(value));
            mapElement.appendChild(entryElement);
        }
        root.appendChild(mapElement);
    }
	
    private void handleTextMessage(TextMessageWrapper textMessage, Document xmldoc,
            Element root) 
    {
        root.setAttribute("type", "TextMessage");
        Element textElement = xmldoc.createElement("Text");
        textElement.appendChild(xmldoc.createTextNode(textMessage.getText()));
        root.appendChild(textElement);
    }



	public MessageWrapper getMessageObject(String xml) {

        DocumentBuilderFactory dFactoryImpl = DocumentBuilderFactory
            .newInstance();
        
        MessageWrapper result = null;
        try
        {
            Document doc = dFactoryImpl.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
            Element messageElement = doc.getDocumentElement();
            String typeAttribute = messageElement.getAttribute("type");
            
            Map<String, Object> messageProperties = handleForProperties(messageElement);
            Map<String, Object> messagingProperties = handleForMessagingProperties(messageElement);

            if (typeAttribute.equals("TextMessage"))
            {
               
                String text = handleForTextMessage(messageElement);
                
				result = new TextMessageWrapper(text,messageProperties);
            }
            else if (typeAttribute.equals("MapMessage"))
            {
                result = new MapMessageWrapper(handleForMapMessage(messageElement),messageProperties);
            }
            
            else{
            	throw new RuntimeException("Unsupported type [ "+typeAttribute+"]");
            }
            String messageId = messageElement.getAttribute("messageId");
            result.setMessageId(messageId);
            
            result.setMessagingProperties(messagingProperties);
          
        }
        catch (SAXException e)
        {
           throw new RuntimeException(e);
        }
        catch (IOException e)
        {
        	throw new RuntimeException(e);
        }
        catch (ParserConfigurationException e)
        {
        	throw new RuntimeException(e);
        }
        return result;
    }
	
	private Map<String, Object> handleForMapMessage(Element messageElement){
		logger.info("handling map message xml");
		Map<String, Object> result = new HashMap<String, Object>();
		NodeList elementsByTagName = messageElement.getElementsByTagName("Map");
		Element mapNode = (Element) elementsByTagName.item(0);
		NodeList mapEntries = mapNode.getElementsByTagName("Entry");
		int mapEntryCount = 0;
		while (mapEntryCount < mapEntries.getLength())
		{
		    Node mapEntry = mapEntries.item(mapEntryCount);
		    handleMapEntry((Element) mapEntry, result);
		    mapEntryCount++;
		}
		return result;
	}
	
	private Map<String, Object> handleForProperties(Element messageElement){
		Map<String, Object> result = new HashMap<String, Object>();
		NodeList elementsByTagName = messageElement.getElementsByTagName("Properties");
		Element mapNode = (Element) elementsByTagName.item(0);
		if (mapNode!=null){
			NodeList mapEntries = mapNode.getElementsByTagName("Property");
			int mapEntryCount = 0;
			while (mapEntryCount < mapEntries.getLength())
			{
			    Node mapEntry = mapEntries.item(mapEntryCount);
			    handleMapEntry((Element) mapEntry, result);
			    mapEntryCount++;
			}
		}
		return result;
	}
	
	private Map<String, Object> handleForMessagingProperties(Element messageElement){
		Map<String, Object> result = new HashMap<String, Object>();
		NodeList elementsByTagName = messageElement.getElementsByTagName("MessagingProperties");
		Element mapNode = (Element) elementsByTagName.item(0);
		if (mapNode!=null){
			NodeList mapEntries = mapNode.getElementsByTagName("Property");
			int mapEntryCount = 0;
			while (mapEntryCount < mapEntries.getLength())
			{
			    Node mapEntry = mapEntries.item(mapEntryCount);
			    handleMapEntry((Element) mapEntry, result);
			    mapEntryCount++;
			}
		}
		return result;
	}
	
	private void handleMapEntry(Element mapEntry, Map<String,Object> map){
		String clazz = mapEntry.getAttributes().getNamedItem("class")
				.getNodeValue();
		String mapEntryName = mapEntry.getAttribute("name");
		String value = null;

		Node node = mapEntry.getFirstChild();
		logger.info("node ["+node+"]");
		logger.info("node class for["+mapEntryName+"]:"+node.getClass());
		if (node != null) {
			value = node.getNodeValue();
		}

		if ("java.lang.String".equals(clazz)) {
			map.put(mapEntryName, value);
		} else if ("java.lang.Integer".equals(clazz)) {
			map.put(mapEntryName, Integer.parseInt(value));
		} else if ("java.lang.Long".equals(clazz)) {
			map.put(mapEntryName, Long.parseLong(value));
		} else if ("java.lang.Boolean".equals(clazz)) {
			map.put(mapEntryName, Boolean.parseBoolean(value));
		} else if ("java.lang.Byte".equals(clazz)) {
			map.put(mapEntryName, Byte.parseByte(value));
		} else if ("java.lang.Short".equals(clazz)) {
			map.put(mapEntryName, Short.parseShort(value));
		} else if ("java.lang.Double".equals(clazz)) {
			map.put(mapEntryName, Double.parseDouble(value));
		} else if ("byte[]".equals(clazz)) {
			byte[] bytes = ((String)value).getBytes();
			map.put(mapEntryName, bytes);

		} else {
			throw new RuntimeException("unhandled Map entry class:" + clazz);
		}
	}





	private String handleForTextMessage(Element messageElement){
		Node textNode = null;
		String text = null;
		
		
		NodeList childNodes = messageElement.getChildNodes();
		for (int i=0;i<childNodes.getLength();i++){
			Node item = childNodes.item(i);
			if (item.getNodeName().equalsIgnoreCase("Text")){
				textNode = item;
			}
		}
		
		if (textNode!=null){
			text = textNode.getTextContent();
		}

		return text;
	}

}
