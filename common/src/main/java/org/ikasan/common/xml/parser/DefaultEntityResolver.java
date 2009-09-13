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
package org.ikasan.common.xml.parser;

// Imported java classes
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

// Imported sax classes
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Imported log4j classes
import org.apache.log4j.Logger;

import org.ikasan.common.util.ResourceUtils;

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
