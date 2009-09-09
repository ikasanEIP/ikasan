/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.factory;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.common.component.Spec;

/**
 * The default implementation for PayloadFactory
 * 
 * @author Ikasan Development Team
 */
public class PayloadFactoryImpl implements PayloadFactory
{
    /** The payload implementation class key */
    public static String PAYLOAD_IMPL_CLASS = "payloadImpl.class";



//    /** XML document builder factory */
//    private DocumentBuilderFactory documentBuilderFactory;

    /** The logger */
    private Logger logger = Logger.getLogger(PayloadFactoryImpl.class);



//    /**
//     * Create a new instance of the Payload based on the incoming name and spec.
//     * 
//     * @param name The payload name
//     * @param spec The payload Spec (as a String)
//     * @param srcSystem The payload source system
//     * @return Payload
//     */
//    public Payload newPayload(final String name, final String spec, final String srcSystem)
//    {
//        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
//                + this.payloadImplClass + "]"); //$NON-NLS-1$
//        Class<?>[] paramTypes = { String.class, String.class, String.class };
//        Object[] params = { name, spec, srcSystem };
//        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
//    }

//    /**
//     * Create a new instance of the Payload based on the incoming name and spec.
//     * 
//     * @param name The payload name
//     * @param spec The payload Spec
//     * @param srcSystem The payload source system
//     * @return Payload
//     */
//    public Payload newPayload(final String name, final Spec spec, final String srcSystem)
//    {
//        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
//                + this.payloadImplClass + "]"); //$NON-NLS-1$
//        Class<?>[] paramTypes = { String.class, String.class, String.class };
//        Object[] params = { name, spec.toString(), srcSystem };
//        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
//    }

//    /**
//     * Create a new instance of the Payload based on the incoming Payload.
//     * 
//     * @param payload The incoming payload
//     * @return Payload
//     */
//    public Payload newPayload(Payload payload)
//    {
//        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
//                + this.payloadImplClass + "]"); //$NON-NLS-1$
//        Class<?>[] paramTypes = { Payload.class };
//        Object[] params = { payload };
//        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
//    }

    /**
     * Create a new instance of the Payload for the incoming content.
     * 
     * @param name The payload name
     * @param spec The payload Spec
     * @param srcSystem The payload source system
     * @param content The payload content
     * @return Payload
     */
    public Payload newPayload(final String id, final String name, final Spec spec, final String srcSystem, final byte[] content)
    {
//        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
//                + this.payloadImplClass + "]"); //$NON-NLS-1$
//        Class<?>[] paramTypes = { String.class, String.class, String.class, byte[].class };
//        Object[] params = { name, spec.toString(), srcSystem, content };
//        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
    	return new DefaultPayload(id, name, spec, srcSystem, content);
    }

//    /**
//     * Create a new instance of the Payload for the incoming content.
//     * 
//     * @param name The payload name
//     * @param spec The payload Spec (as a String)
//     * @param srcSystem The payload source system
//     * @param content The payload content
//     * @return Payload
//     */
//    public Payload newPayload(final String name, final String spec, final String srcSystem, final byte[] content)
//    {
//        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
//                + this.payloadImplClass + "]"); //$NON-NLS-1$
//        Class<?>[] paramTypes = { String.class, String.class, String.class, byte[].class };
//        Object[] params = { name, spec, srcSystem, content };
//        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
//    }

    /**
     * Get the payload concrete implementation class .
     * 
     * @return the payloadImplClass
     */
    public Class<? extends Payload> getPayloadImplClass()
    {
        return DefaultPayload.class;
    }

//    /**
//     * Set the payload concrete implementation class .
//     * 
//     * @param payloadImplClass the payloadImplClass to set
//     */
//    public void setPayloadImplClass(final Class<? extends Payload> payloadImplClass)
//    {
//        this.payloadImplClass = payloadImplClass;
//        logger.debug("Setting payloadImplClass [" //$NON-NLS-1$
//                + this.payloadImplClass + "]"); //$NON-NLS-1$
//    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see org.ikasan.common.factory.PayloadFactory#newPayload(org.ikasan.common.component.Spec, java.lang.String,
//     * byte[])
//     */
//    public Payload newPayload(Spec spec, String srcSystem, byte[] xmlContent)
//    {
//        return newPayload(getPayloadNameFromDocument(xmlContent), spec, srcSystem, xmlContent);
//    }

//    /**
//     * Get the payload name from the original XML document, use the root element as the name
//     * 
//     * @param payloadBytes The payload content
//     * 
//     * @return name (root element name)
//     */
//    private String getPayloadNameFromDocument(byte[] payloadBytes)
//    {
//        String payloadName = MetaDataInterface.UNDEFINED;
//        Document xmlDoc;
//        try
//        {
//            DocumentBuilder documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
//            xmlDoc = documentBuilder.parse(new ByteArrayInputStream(payloadBytes));
//            payloadName = evaluateXPath(xmlDoc, "/*[1]");
//            logger.debug("Payload name was evaluated to be: [" + payloadName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//        catch (ParserConfigurationException e)
//        {
//            logger.warn("Could not parse document to get payload name.", e); //$NON-NLS-1$
//        }
//        catch (SAXException e)
//        {
//            logger.warn("SAX Exception when parsing document to get payload name.", e); //$NON-NLS-1$
//        }
//        catch (IOException e)
//        {
//            logger.warn("IO Exception when parsing document to get payload name."); //$NON-NLS-1$
//        }
//        catch (XPathExpressionException e)
//        {
//            logger.warn("XPath Exception when parsing document to get payload name."); //$NON-NLS-1$
//        }
//        return payloadName;
//    }

//    /**
//     * Utility method for evaluating an XPath expression on a Document
//     * 
//     * @param expression The XPath expression
//     * @param doc The document
//     * @return String
//     * @throws XPathExpressionException Exception if the XPath could not be realised
//     */
//    private String evaluateXPath(Document doc, String expression) throws XPathExpressionException
//    {
//        XPath xpath = XPathFactory.newInstance().newXPath();
//        Node node = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
//        return node.getLocalName();
//    }
}
