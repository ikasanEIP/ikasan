/*
 * $Id: DocumentValidator.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/util/DocumentValidator.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/**
 * @author CMI2 Development Team
 *
 */
public class DocumentValidator
{
    /**
     * Method to validate the document against an XSD.
     * 
     * @param fileContents
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    public static SchemaValidationErrorHandler validateUploadedDocument(byte[] fileContents) throws SAXException
    , IOException, ParserConfigurationException, XPathExpressionException
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(true);
        builderFactory.setNamespaceAware(true);
        builderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
                "http://www.w3.org/2001/XMLSchema");

        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        SchemaValidationErrorHandler errorHandler = new SchemaValidationErrorHandler ();
        builder.setErrorHandler(errorHandler);
        builder.parse(new ByteArrayInputStream(fileContents));

        return errorHandler;
    }
}
