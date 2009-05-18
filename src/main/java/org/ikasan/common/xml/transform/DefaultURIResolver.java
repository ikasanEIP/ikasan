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
 * @author Jun Suetake
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
