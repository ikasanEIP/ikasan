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
package org.ikasan.common.xml.parser;

// Imported java classes
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.ikasan.common.util.ResourceUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class implements interface <code>org.xml.sax.EntityResolver</code>
 * to resolve external entities.
 *
 * <pre>
 *    Usage Example:
 *
 *    import javax.xml.parsers.SAXParserFactory;
 *    import javax.xml.parsers.SAXParser;
 *    import org.xml.sax.InputSource;
 *    import org.xml.sax.XMLReader;
 *
 *    try
 *    {
 *       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 *       factory.setNamespaceAware(true);
 *       factory.setValidating(true);
 *       DocumentBuilder builder = this.factory.newDocumentBuilder();
 *       builder.setErrorHandler(new DefaultErrorHandler());
 *       builder.setEntityResolver(new DefaultEntityResolver);
 *       builder.parse(InputSource("example.xml"));
 *    }
 *    catch (Exception e)
 *    {
 *        e.printStackTrace();
 *    }
 *
 * </pre>
 *
 * author Jun Suetake
 *
 */
public class DefaultEntityResolver
    implements EntityResolver
{
    /**
     * The logger instance.
     */
    private static Logger logger =
        Logger.getLogger(DefaultEntityResolver.class);

    /**
     * Create a new instance of <code>DefaultEntityResolver</code>
     * with the default trace level.
     */
    public DefaultEntityResolver()
    {
        // Do Nothing
    }

    /**
     * This method is invoked if the default entityResolver is registered
     * on the parser instance.
     * 
     * It will try to resolve the resource identified via the systemId from
     * the classpath.
     * 
     * If the resource is not found return null to allow the default URL
     * resolver to handle it.
     *
     * @param publicId
     * @param systemId
     * @return InputSource
     * @throws SAXException
     * @throws IOException
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
    {
    	//boolean screamOnFail = true;
        URI uri = null;
        String resource = null;
        InputStream is = null;
        
        try
        {
            uri = new URI(systemId);
            resource = uri.getSchemeSpecificPart();
            is = this.getInputStream(resource);

            // try again
            if(is == null)
                is = this.getInputStream( resource.substring(resource.lastIndexOf('/')+1) );
                
            //
            // Ok, we have one so set and return it
            if(is != null)
            {
            	InputSource src = new InputSource(is);
                src.setSystemId(systemId);
                return src;
            }
        }
        catch(URISyntaxException e)
        {
            logger.debug("systemId [" + systemId + "] is not a valid URI", e);
        }
        
        //
        // No input streams loaded so revert to default entity by returning null
        // so that entity resolution will occur normally in non-special cases.
        return null;
    }
    
    /**
     * Utility method for loading the resource and returning as an input stream
     * @param resource
     * @return InputStream
     * @throws IOException
     */
    private InputStream getInputStream(final String resource)
    	throws IOException
    {
        URL url = ResourceUtils.getAsUrl(resource);
        if(url != null)
        	return url.openStream();
        
        return null;
    }

}
