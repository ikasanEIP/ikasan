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

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.w3c.dom.Document;

/**
 * Common XML Transformer provides the base interface for all XML Transformation.
 *
 * @author Ikasan Development Team
 */  
public interface CommonXSLTransformer
{
    /**
     * Set the stylesheet given a xslURI
     * @param xslURI
     * @throws TransformerConfigurationException
     */
    public void setStylesheet(String xslURI)
        throws TransformerConfigurationException;

    /**
     * Set the stylesheet given a xslURI and a use XSLT flag
     * 
     * @param xslURI
     * @param useXSLTC 
     * @throws TransformerConfigurationException
     */
    public void setStylesheet(String xslURI, boolean useXSLTC)
        throws TransformerConfigurationException;

    /**
     * Set the URIResolver
     * @param resolver
     */
    public void setURIResolver(URIResolver resolver);
    
    /**
     * Set the URIResolver given a map of sources
     * @param resolverMap
     */
    public void setURIResolver(Map<String, Source> resolverMap);
    
    /**
     * Set the URIResolver given a uri and an XML source
     * @param uri
     * @param xml
     */
    public void setURIResolver(String uri, Source xml);

    /**
     * Set the output property
     * @param name
     * @param value
     */
    public void setOutputProperty(String name, String value);

    /**
     * Set output properties
     * @param outputProps
     */
    public void setOutputProperties(Properties outputProps);

    /**
     * Transform Source (in) into Result (out)
     * 
     * @param in
     * @param out
     * @throws TransformerException
     */
    public void transform(Source in, Result out)
        throws TransformerException;

    /**
     * Transform Source (in) into a String
     * 
     * @param in
     * @return String representing transformed Source
     * @throws TransformerException
     * @throws IOException
     */
    public String transformToString(Source in)
        throws TransformerException, IOException;
    
    /**
     * Transform Document into a String
     * 
     * @param xmlIn
     * @return String representing transformed Document
     * @throws TransformerException
     * @throws IOException
     */
    public String transformToString(Document xmlIn)
        throws TransformerException, IOException;
    
    /**
     * Transform XML String into a String
     * 
     * @param xmlIn
     * @return String representing transformed XML String
     * @throws TransformerException
     * @throws IOException
     */
    public String transformToString(String xmlIn)
        throws TransformerException, IOException;
    
    /**
     * Transform Source into a Document
     * 
     * @param in
     * @return Document
     * @throws TransformerException
     * @throws IOException
     */
    public Document transformToDocument(Source in)
        throws TransformerException, IOException;
    
    /**
     * Transform Document into a Document
     * 
     * @param xmlIn
     * @return Document representing transformed Document
     * @throws TransformerException
     * @throws IOException
     */
    public Document transformToDocument(Document xmlIn)
        throws TransformerException, IOException;
    
    /**
     * Transform XML String into a Document
     * 
     * @param xmlIn
     * @return Document representing transformed XML String
     * @throws TransformerException
     * @throws IOException
     */
    public Document transformToDocument(String xmlIn)
        throws TransformerException, IOException;
    
    /**
     * Set the parameter
     * 
     * @param name
     * @param value
     */
    public void setParameter(String name, String value);

    /**
     * Set the parameters
     * 
     * @param parameterMap
     */
    public void setParameters(Map<String, String> parameterMap);
}