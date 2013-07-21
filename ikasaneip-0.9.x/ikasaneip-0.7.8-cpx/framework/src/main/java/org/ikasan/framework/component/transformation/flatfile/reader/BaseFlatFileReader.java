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
package org.ikasan.framework.component.transformation.flatfile.reader;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Abstract base class for FlatFile implementations of <code>XMLReader</code>
 * 
 * Subclasses of this class will be capable of reading various types of flat
 * files as if they were XML files.
 * 
 * @author Ikasan Development Team
 * 
 */
public abstract class BaseFlatFileReader implements XMLReader
{
    /** Handler to the content */
    protected ContentHandler contentHandler = null;
    
    /** Handler to the DTD */
    private DTDHandler dtdHandler = null;
    
    /** A resolver for entities */
    private EntityResolver entityResolver = null;
    
    /** The error handler */
    private ErrorHandler errorHandler = null;
    
    /** The URI for the namespace */
    protected String namespaceURI = "";
    
    /**
     * Name of the root element for the produced document
     */
    private String rootElementName;
    

	/**
	 * Constructor
	 * 
	 * @param rootElementName - The name of the root element
	 */
	public BaseFlatFileReader(String rootElementName) {
		super();
		this.rootElementName = rootElementName;
	}

	/** Attributes */
    protected Attributes attributes = new AttributesImpl();
    

    /**
     * Accessor method for ContentHandler
     * 
     * @return contentHandler
     */
    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    /**
     * Accessor method for DTDHandler
     * 
     * @return dtdHandler
     */
    public DTDHandler getDTDHandler()
    {
        return dtdHandler;
    }

    /**
     * Accessor method for EntityResolver
     * 
     * @return entityResolver
     */
    public EntityResolver getEntityResolver()
    {
        return entityResolver;
    }

    /**
     * Accessor method for ErrorHandler
     * 
     * @return errorHandler
     */
    public ErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    /**
     * Accessor method for features.
     * 
     * @param name
     * @return throws SAXNotSupportedException as not currently supported
     * @throws SAXNotSupportedException 
     */
    public boolean getFeature(String name)
        throws SAXNotSupportedException
    {
        throw new SAXNotSupportedException("Feature '"
            + name + "' not supported.");
    }

    /**
     * Accessor for named properties
     * 
     * @param name
     * 
     * @return null in all cases
     */
    public Object getProperty(String name)
    {
        return null;
    }

    /**
     * Unimplemented method from <code>XMLReader</code>
     * 
     * @param systemId
     */
    public void parse(String systemId)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Setter method for ContentHandler
     * 
     * @param handler
     */
    public void setContentHandler(ContentHandler handler)
    {
        this.contentHandler = handler;
    }

    /**
     * Setter method for DTDHandler
     * 
     * @param handler
     */
    public void setDTDHandler(DTDHandler handler)
    {
        this.dtdHandler = handler;
    }

    /**
     * Setter method for EntityResolver
     * 
     * @param resolver
     */
    public void setEntityResolver(EntityResolver resolver)
    {
        this.entityResolver = resolver;
    }

    /**
     * Setter method for ErrorHandler
     * 
     * @param handler
     */
    public void setErrorHandler(ErrorHandler handler)
    {
        this.errorHandler = handler;
    }

    /**
     * Unimplemented method from <code>XMLReader</code>
     * 
     * @param name
     * @param value
     * @throws SAXNotSupportedException 
     */
    public void setFeature(String name, boolean value)
        throws SAXNotSupportedException
    {
        throw new SAXNotSupportedException("Feature '"
            + name + "' not supported.");
    }

    /**
     * Unimplemented method from <code>XMLReader</code>
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, Object value)
    {
        // No implementation required
    }
    
    /**
     * Setter for namespaceURI
     * @param namespaceURI
     */
    public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

    /**
     * Parses the InputSource creating an empty document 
     * 
     * Delegates to subclasses to parse the InputSource in type specific manner
     * 
     * @param inputSource
     * @throws IOException
     * @throws SAXException
     */
    public void parse(InputSource inputSource) throws IOException, SAXException
    {
        contentHandler.startDocument();
        contentHandler.startElement(namespaceURI, rootElementName, rootElementName, attributes);
        parseInputSource(inputSource);
        contentHandler.endElement(namespaceURI, rootElementName, rootElementName);
        contentHandler.endDocument();
    }

    /**
     * Type specific method implemented by subclasses
     * 
     * @param inputSource
     * @throws IOException
     * @throws SAXException
     */
    protected abstract void parseInputSource(InputSource inputSource) throws IOException, SAXException;
}
