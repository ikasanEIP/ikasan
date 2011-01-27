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
