/*
 * $Id: ResourceUtils.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/util/ResourceUtils.java $
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
package org.ikasan.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * A utility class for general resource IO activities
 * 
 * @author Jeff MItchell
 */
public class ResourceUtils
{
    /**
     * Load properties from the given name. Try loading these properties in the
     * following order, (1) load as XML properties from the classpath; (2) load
     * as NVP properties from the classpath; (3) load as XML properties from the
     * file system; (4) load as NVP properties from the file system; If all
     * above fail then throw IOException.
     * 
     * @param name - properties name
     * @return Properties
     * @throws IOException
     */
    public static Properties getAsProperties(final String name) 
        throws IOException
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
     * Firstly try loading from the classpath, if this fails try loading from the file system. 
     * If this fails throw an IOException.
     * 
     * @param name - resource name
     * @return InputStream
     * @throws IOException
     */
    public static InputStream loadResource(final String name) 
        throws IOException
    {
        //
        // try loading via URL from the CLASSPATH
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(name);
        if (url != null)
            return url.openStream();

        // nothing on the CLASSPATH, lets try the file system
        return new FileInputStream(name);
    }
    
    /**
     * Load URL from the classpath. If not found the url will be returned as
     * null.
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
     * Load URL from the classpath. If not found the url will be returned as
     * null.
     * 
     * @param name - properties name
     * @param screamOnFail - throw an IOException if we fail to load the
     *            resource name.
     * @return URL
     * @throws IOException
     */
    public static URL getAsUrl(final String name, final boolean screamOnFail) throws IOException
    {
        URL url = getAsUrl(name);
        if (url == null && screamOnFail) throw new IOException("Failed to load resource [" + name + "]. Not found on classpath.");
        return url;
    }
    
}
