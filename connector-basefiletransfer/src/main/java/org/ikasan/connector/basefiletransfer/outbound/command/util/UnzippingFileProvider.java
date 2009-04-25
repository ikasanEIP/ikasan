/*
 * $Id: UnzippingFileProvider.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/outbound/command/util/UnzippingFileProvider.java $
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
package org.ikasan.connector.basefiletransfer.outbound.command.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

/**
 * Class that provides <code>Iterator</code> type access to the entries in a
 * Zipped InputStream
 * 
 * @author Ikasan Development Team 
 * 
 */
public class UnzippingFileProvider implements BatchedFileProvider
{

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(UnzippingFileProvider.class);

    /**
     * The next entry (if any)
     */
    private FileHandle next = null;

    /**
     * Zip Input Stream
     */
    private ZipInputStream zin;

    /**
     * Constructor
     * 
     * @param zippedInputStream InputStream of zipped data
     * @throws UnzipNotSupportedException
     */
    public UnzippingFileProvider(InputStream zippedInputStream) throws UnzipNotSupportedException
    {

        zin = new ZipInputStream(new BufferedInputStream(zippedInputStream));

        initialiseNext();
        if (next == null)
        {
            throw new UnzipNotSupportedException("Could not find any file entries"); //$NON-NLS-1$
        }
    }

    /**
     * Initialises the next object, if any exists
     */
    private void initialiseNext()
    {
        ZipEntry entry;

        try
        {
            entry = zin.getNextEntry();

            if (entry != null)
            {
                next = new FileHandle(entry.getName(), zin, entry.isDirectory());
            }
        }
        catch (IOException ioe)
        {
            logger.error(ioe);
            throw new RuntimeException("Exception writting decompressed output", ioe); //$NON-NLS-1$
        }
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        if (next == null)
        {
            initialiseNext();
        }
        return (next != null);
    }

    /**
     * @see java.util.Iterator#next()
     */
    public FileHandle next()
    {
        if (next == null)
        {
            initialiseNext();
        }

        FileHandle result = next;
        next = null;
        return result;
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        // No implementation
    }

}
