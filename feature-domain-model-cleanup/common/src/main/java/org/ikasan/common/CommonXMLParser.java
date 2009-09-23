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
 * @author Ikasan Development Team
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
