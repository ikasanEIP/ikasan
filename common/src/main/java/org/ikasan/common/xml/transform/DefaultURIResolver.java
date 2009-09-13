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
package org.ikasan.common.xml.transform;

// Imported java classes
import java.util.HashMap;

// Imported TraX classes
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * This calss implements this interface
 * <code>javax.xml.transform.URIResolver</code>
 * that can be called by the processor to turn a URI used in
 * document(), xsl:import, or xsl:include into a Source object.
 *
 * @author Ikasan Development Team
 *
 */
public class DefaultURIResolver
    implements URIResolver
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(DefaultURIResolver.class);

    /**
     * The mapping table to map URIs to <code>Source</code>.
     */
    private HashMap<String, Source> uriToSourceMap =
        new HashMap<String, Source>(10);

    /**
     * Creates a new instance of <code>DefaultURIResolver</code>
     * with the default trace level.
     */
    public DefaultURIResolver()
    {
        // Do Nothing
    }

    /**
     * Maps URIs to <code>StreamSource</code>.
     *
     * @param uri    is the URI (i.e. relative reference).
     * @param source is the <code>StreamSource</code> to be mapped
     *                  to the specified URI.
     */
    public void mapURIToSource(String uri, Source source)
    {
        if (uri == null)
        {
            logger.warn("URI string can't null, returning...");
            return;
        }
        if (uri.trim().length() == 0)
        {
            logger.warn("URI string can't be empty, returning...");
            return;
        }
        if (source == null)
        {
            logger.warn("Stream source can't be null, returning...");
            return;
        }

        logger.debug("Mapping URI [" + uri + "] to Source...");
        this.uriToSourceMap.put(uri, source);
    }

    /**
     * Called by the processor when it encounters
     * an xsl:include, xsl:import or document() function.
     * @param href 
     * @param base 
     * @return Source
     * @throws TransformerException 
     *
     */
    public Source resolve(String href, String base)
        throws TransformerException
    {
        logger.debug("Returning the associated stream source using "
                    + "base:[" + base + "] and href:[" + href + "]...");
        return this.uriToSourceMap.get(href);
    }

}
