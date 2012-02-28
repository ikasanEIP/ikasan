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
