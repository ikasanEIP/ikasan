/*
 * $Id: XPathKeyLocationQueryProcessor.java 31879 2013-07-30 15:03:33Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/keyQueryProcessor/impl/XPathKeyLocationQueryProcessor.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.keyQueryProcessor.impl;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessor;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessorException;
import org.w3c.dom.Document;


/**
 * @author CMI2 Development Team
 *
 */
public class XPathKeyLocationQueryProcessor implements KeyLocationQueryProcessor
{
    /** The XPath Factory that we will use to evaluate the various XPaths */
    private final XPathFactory xpathFactory = XPathFactory.newInstance();

    /** The DocumentBuilderFactory used to resolve the payload from byte[] into a Document  */
    private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.keyQueryProcessor.KeyLocationQueryProcessor#getKeyValueFromPayload(java.lang.String, byte[])
     */
    @Override
    public String getKeyValueFromPayload(String keyLocation, byte[] payload) throws KeyLocationQueryProcessorException
    {
        try
        {
            DocumentBuilder parser = this.documentBuilderFactory.newDocumentBuilder();

            Document document = parser.parse(new ByteArrayInputStream(payload));
            XPath xpath = this.xpathFactory.newXPath();

            // Evaluate the XPath
            return (String)xpath.evaluate(keyLocation, document, XPathConstants.STRING);
        }
        catch (Exception e)
        {
            throw new KeyLocationQueryProcessorException(e);
        }
    }
}
