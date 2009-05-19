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
package org.ikasan.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Common XML Parser provides the base interface for all XML parsing.
 *
 * @author Jeff Mitchell
 * @deprecated - This has been deprecated, use DocumentBuilderFactory instead
 */  
@Deprecated
public interface CommonXMLParser
{
    /**
     * Parse resource given by URI into a document
     * 
     * @param uri
     * @return Document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document parse(String uri)
        throws ParserConfigurationException, SAXException, IOException;

    /**
     * Parse byte stream into a document
     * 
     * @param xmlDoc
     * @return Document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document parse(byte[] xmlDoc)
        throws ParserConfigurationException, SAXException, IOException;
    
    /**
     * Parse file into a document
     * 
     * @param file
     * @return Document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document parse(File file)
        throws ParserConfigurationException, SAXException, IOException;
    
    /**
     * Parse InputSource into a document
     * 
     * @param is
     * @return Document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document parse(InputSource is)
        throws ParserConfigurationException, SAXException, IOException;
    
    /**
     * Parse InputStream into a document
     * 
     * @param is
     * @return Document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document parse(InputStream is)
        throws ParserConfigurationException, SAXException, IOException;
    
    /**
     * Return true if it's a validating parse
     * @return true if it's a validating parse
     */
    public Boolean isValidating();
    
    /**
     * Set whether validation is on or not (and it's schema Type)
     * @deprecated - use the individual setter methods instead
     * @param validation
     * @param schemaType
     */
    @Deprecated
    public void setValidation(Boolean validation, String schemaType);
    
    /**
     * Set whether validation is on or not
     * 
     * @param validation
     */
    public void setValidation(Boolean validation);
    
    /**
     * Set the required schemaType for validation.
     * 
     * @param schemaType
     */
    public void setSchemaType(String schemaType);
    
    /**
     * True if the parse is name space aware, else false
     * @return True if the parse is name space aware, else false
     */
    public Boolean isNamspaceAware();
    
    /**
     * Set whether the parse is name space aware
     * @param namespaceAware
     */
    public void setNamespaceAware(Boolean namespaceAware);
    
    /**
     * Set default entity resolver
     */
    public void setEntityResolver();
    
    /**
     * Set specified entity resolver
     * @param entityResolver 
     */
    public void setEntityResolver(EntityResolver entityResolver);
    
    /**
     * Get the XML schema type
     * @return XML schema type
     */
    public String getXMLSchemaType();
    
    /**
     * Get the root name of the URI
     * 
     * @param uri
     * @return root name
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String getRootName(String uri)
        throws ParserConfigurationException, IOException, SAXException;
    
    /**
     * Get the root name from the xml byte stream
     * 
     * @param xmlDoc
     * @return root name
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String getRootName(byte xmlDoc[])
        throws ParserConfigurationException, IOException, SAXException;
    
    /**
     * Get the root name from the file
     *  
     * @param file
     * @return root name
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String getRootName(File file)
        throws ParserConfigurationException, IOException, SAXException;
    
    /**
     * Get the root name from the InputSource
     * 
     * @param is
     * @return root name
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String getRootName(InputSource is)
        throws ParserConfigurationException, IOException, SAXException;

    /**
     * Get the root name from the InputStream
     * 
     * @param is
     * @return root name
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String getRootName(InputStream is)
        throws ParserConfigurationException, IOException, SAXException;

    /**
     * Get the root name from the document
     * 
     * @param doc
     * @return root name
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String getRootName(Document doc)
        throws ParserConfigurationException, IOException, SAXException;

    /**
     * Removes indentation.
     * 
     * @param node
     */
    public void removeIndent(Node node);

} 
