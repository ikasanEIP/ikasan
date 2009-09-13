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
package org.ikasan.common.factory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * The default implementation for PayloadFactory
 * 
 * @author Ikasan Development Team
 */
public class PayloadFactoryImpl implements PayloadFactory
{
    /** The payload implementation class key */
    public static String PAYLOAD_IMPL_CLASS = "payloadImpl.class";

    /** Class to use for instantiating <code>Payload</code> instances */
    private Class<? extends Payload> payloadImplClass;

    /** XML document builder factory */
    private DocumentBuilderFactory documentBuilderFactory;

    /** The logger */
    private Logger logger = Logger.getLogger(PayloadFactoryImpl.class);

    /**
     * Constructor
     * 
     * @param payloadImplClass The payload implementation to use
     * @param documentBuilderFactory The parser to use
     */
    public PayloadFactoryImpl(Class<? extends Payload> payloadImplClass, DocumentBuilderFactory documentBuilderFactory)
    {
        super();
        this.payloadImplClass = payloadImplClass;
        this.documentBuilderFactory = documentBuilderFactory;
    }

    /**
     * Create a new instance of the Payload based on the incoming name and spec.
     * 
     * @param name The payload name
     * @param spec The payload Spec (as a String)
     * @param srcSystem The payload source system
     * @return Payload
     */
    public Payload newPayload(final String name, final String spec, final String srcSystem)
    {
        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
                + this.payloadImplClass + "]"); //$NON-NLS-1$
        Class<?>[] paramTypes = { String.class, String.class, String.class };
        Object[] params = { name, spec, srcSystem };
        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
    }

    /**
     * Create a new instance of the Payload based on the incoming name and spec.
     * 
     * @param name The payload name
     * @param spec The payload Spec
     * @param srcSystem The payload source system
     * @return Payload
     */
    public Payload newPayload(final String name, final Spec spec, final String srcSystem)
    {
        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
                + this.payloadImplClass + "]"); //$NON-NLS-1$
        Class<?>[] paramTypes = { String.class, String.class, String.class };
        Object[] params = { name, spec.toString(), srcSystem };
        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
    }

    /**
     * Create a new instance of the Payload based on the incoming Payload.
     * 
     * @param payload The incoming payload
     * @return Payload
     */
    public Payload newPayload(Payload payload)
    {
        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
                + this.payloadImplClass + "]"); //$NON-NLS-1$
        Class<?>[] paramTypes = { Payload.class };
        Object[] params = { payload };
        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
    }

    /**
     * Create a new instance of the Payload for the incoming content.
     * 
     * @param name The payload name
     * @param spec The payload Spec
     * @param srcSystem The payload source system
     * @param content The payload content
     * @return Payload
     */
    public Payload newPayload(final String name, final Spec spec, final String srcSystem, final byte[] content)
    {
        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
                + this.payloadImplClass + "]"); //$NON-NLS-1$
        Class<?>[] paramTypes = { String.class, String.class, String.class, byte[].class };
        Object[] params = { name, spec.toString(), srcSystem, content };
        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
    }

    /**
     * Create a new instance of the Payload for the incoming content.
     * 
     * @param name The payload name
     * @param spec The payload Spec (as a String)
     * @param srcSystem The payload source system
     * @param content The payload content
     * @return Payload
     */
    public Payload newPayload(final String name, final String spec, final String srcSystem, final byte[] content)
    {
        logger.debug("Instantiating payload based on class [" //$NON-NLS-1$
                + this.payloadImplClass + "]"); //$NON-NLS-1$
        Class<?>[] paramTypes = { String.class, String.class, String.class, byte[].class };
        Object[] params = { name, spec, srcSystem, content };
        return (Payload) ClassInstantiationUtils.instantiate(this.payloadImplClass, paramTypes, params);
    }

    /**
     * Get the payload concrete implementation class .
     * 
     * @return the payloadImplClass
     */
    public Class<? extends Payload> getPayloadImplClass()
    {
        logger.debug("Getting payloadImplClass [" //$NON-NLS-1$
                + this.payloadImplClass + "]"); //$NON-NLS-1$
        return this.payloadImplClass;
    }

    /**
     * Set the payload concrete implementation class .
     * 
     * @param payloadImplClass the payloadImplClass to set
     */
    public void setPayloadImplClass(final Class<? extends Payload> payloadImplClass)
    {
        this.payloadImplClass = payloadImplClass;
        logger.debug("Setting payloadImplClass [" //$NON-NLS-1$
                + this.payloadImplClass + "]"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.factory.PayloadFactory#newPayload(org.ikasan.common.component.Spec, java.lang.String,
     * byte[])
     */
    public Payload newPayload(Spec spec, String srcSystem, byte[] xmlContent)
    {
        return newPayload(getPayloadNameFromDocument(xmlContent), spec, srcSystem, xmlContent);
    }

    /**
     * Get the payload name from the original XML document, use the root element as the name
     * 
     * @param payloadBytes The payload content
     * 
     * @return name (root element name)
     */
    private String getPayloadNameFromDocument(byte[] payloadBytes)
    {
        String payloadName = MetaDataInterface.UNDEFINED;
        Document xmlDoc;
        try
        {
            DocumentBuilder documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
            xmlDoc = documentBuilder.parse(new ByteArrayInputStream(payloadBytes));
            payloadName = evaluateXPath(xmlDoc, "/*[1]");
            logger.debug("Payload name was evaluated to be: [" + payloadName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch (ParserConfigurationException e)
        {
            logger.warn("Could not parse document to get payload name.", e); //$NON-NLS-1$
        }
        catch (SAXException e)
        {
            logger.warn("SAX Exception when parsing document to get payload name.", e); //$NON-NLS-1$
        }
        catch (IOException e)
        {
            logger.warn("IO Exception when parsing document to get payload name."); //$NON-NLS-1$
        }
        catch (XPathExpressionException e)
        {
            logger.warn("XPath Exception when parsing document to get payload name."); //$NON-NLS-1$
        }
        return payloadName;
    }

    /**
     * Utility method for evaluating an XPath expression on a Document
     * 
     * @param expression The XPath expression
     * @param doc The document
     * @return String
     * @throws XPathExpressionException Exception if the XPath could not be realised
     */
    private String evaluateXPath(Document doc, String expression) throws XPathExpressionException
    {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node node = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
        return node.getLocalName();
    }
}
