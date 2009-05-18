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
 * @author Jeff Mitchell
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