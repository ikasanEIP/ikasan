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
package org.ikasan.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * A utility class for general resource IO activities
 * 
 * @author Ikasan Development Team
 */
public class ResourceUtils
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(ResourceUtils.class);

    /**
     * Load properties from the given name. Try loading these properties in the following order, (1) load as XML
     * properties from the classpath; (2) load as NVP properties from the classpath; (3) load as XML properties from the
     * file system; (4) load as NVP properties from the file system; If all above fail then throw IOException.
     * 
     * @param name - properties name
     * @return Properties
     * @throws IOException - IOException in case of failure
     */
    public static Properties getAsProperties(final String name) throws IOException
    {
        Properties props = new Properties();
        //
        // try loading via URL from the CLASSPATH
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(name);
        if (url != null)
        {
            //
            // try XML properties, failing that try standard NVP.
            try
            {
                props.loadFromXML(url.openStream());
            }
            catch (InvalidPropertiesFormatException e)
            {
                // NOTE: we have to re-open the input stream on exception
                props.load(url.openStream());
            }
        }
        else
        // nothing on the CLASSPATH, lets try the file system
        {
            //
            // try XML properties, failing that try standard NVP.
            try
            {
                props.loadFromXML(new FileInputStream(name));
            }
            catch (InvalidPropertiesFormatException e)
            {
                // NOTE: we have to re-open the input stream on exception
                props.load(new FileInputStream(name));
            }
        }
        return props;
    }

    /**
     * Load given resource and return as an input stream.
     * 
     * Firstly try loading from the classpath, if this fails try loading from the file system. If this fails throw an
     * IOException.
     * 
     * @param name - resource name
     * @return InputStream
     * @throws IOException - Exception if we cannot read source
     */
    public static InputStream loadResource(final String name) throws IOException
    {
        // Try loading via URL from the CLASSPATH
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(name);
        if (url != null)
        {
            return url.openStream();
        }
        // Nothing on the CLASSPATH, lets try the file system
        return new FileInputStream(name);
    }

    /**
     * Load URL from the classpath. If not found the url will be returned as null.
     * 
     * @param name - resource name
     * @return URL
     */
    public static URL getAsUrl(final String name)
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(name);
    }

    /**
     * Load URL from the classpath. If not found the url will be returned as null.
     * 
     * @param name - properties name
     * @param screamOnFail - throw an IOException if we fail to load the resource name.
     * @return URL
     * @throws IOException - Exception if we cannot read
     */
    public static URL getAsUrl(final String name, final boolean screamOnFail) throws IOException
    {
        URL url = getAsUrl(name);
        if (url == null && screamOnFail)
        {
            throw new IOException("Failed to load resource [" + name + "]. Not found on classpath.");
        }
        return url;
    }
}
