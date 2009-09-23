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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.ikasan.connector.basefiletransfer.outbound.command.util.FileHandle;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UnzipNotSupportedException;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UnzippingFileProvider;

import junit.framework.TestCase;

/**
 * Test class for UnzippingFileProvider
 * 
 * @author Ikasan Development Team
 */
public class UnzippingFileProviderTest extends TestCase
{
    /**
     * File name for the zip entry
     */
    private static final String ZIP_ENTRY_FILE_NAME = "file.ext";
    /**
     * Underlying zip archive for these tests
     */
    byte[] zipArchive = null;

    /**
     * Constructor
     */
    public UnzippingFileProviderTest()
    {
        // create a zip archive
        try
        {
            zipArchive = zipBytes(new byte[1000], ZIP_ENTRY_FILE_NAME);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Answer a byte array compressed in the Zip format from bytes.
     * 
     * @param bytes a byte array
     * @param aName a String the represents a file name
     * @return byte[] compressed bytes
     * @throws IOException
     */
    public static byte[] zipBytes(byte[] bytes, String aName)
            throws IOException
    {
        ByteArrayOutputStream tempOStream = null;
        BufferedOutputStream tempBOStream = null;
        ZipOutputStream tempZStream = null;
        ZipEntry tempEntry = null;
        byte[] tempBytes = null;
        CRC32 tempCRC = null;
        tempOStream = new ByteArrayOutputStream(bytes.length);
        tempBOStream = new BufferedOutputStream(tempOStream);
        tempZStream = new ZipOutputStream(tempBOStream);
        tempCRC = new CRC32();
        tempCRC.update(bytes, 0, bytes.length);
        tempEntry = new ZipEntry(aName);
        tempEntry.setMethod(ZipEntry.STORED);
        tempEntry.setSize(bytes.length);
        tempEntry.setCrc(tempCRC.getValue());
        tempZStream.putNextEntry(tempEntry);
        tempZStream.write(bytes, 0, bytes.length);
        tempZStream.flush();
        tempBytes = tempOStream.toByteArray();
        tempZStream.close();
        return tempBytes;
    }

    /**
     * Simply tests the contructor
     * @throws UnzipNotSupportedException 
     */
    public void testUnzippingFileProvider() throws UnzipNotSupportedException
    {
        createUnzippingFileProvider();

    }

    /**
     * Creates an instance of the class to test
     * @return new instance of UnzippingFileProvider backed by a zip archive
     * @throws UnzipNotSupportedException 
     */
    private UnzippingFileProvider createUnzippingFileProvider() throws UnzipNotSupportedException
    {
        InputStream zippedInputStream = new ByteArrayInputStream(zipArchive);
        UnzippingFileProvider unzippingFileProvider = new UnzippingFileProvider(
            zippedInputStream);
        return unzippingFileProvider;
    }

    /**
     * Tests the hasNext method
     * @throws UnzipNotSupportedException 
     */
    public void testHasNext() throws UnzipNotSupportedException
    {
        UnzippingFileProvider fileProvider = createUnzippingFileProvider();
        assertTrue("fileProvider should have a next entry", fileProvider.hasNext()); //$NON-NLS-1$
    }

    /**
     * Tests the next method
     * @throws UnzipNotSupportedException 
     */
    public void testNext() throws UnzipNotSupportedException
    {
        UnzippingFileProvider fileProvider = createUnzippingFileProvider();
        FileHandle next = fileProvider.next();
        assertNotNull("object returned from next method should not be null, when there is a legitimate next object",next ); //$NON-NLS-1$
        
        assertEquals("path for FileHandle should match the name of the zip entry",ZIP_ENTRY_FILE_NAME, next.getPath()); //$NON-NLS-1$
        
        assertFalse("fileProvider should not have a next entry, when the only entry has already been retrieved", fileProvider.hasNext()); //$NON-NLS-1$
    }
    
    /**
     * Test the illegal Input Stream
     */
    public void testIlleagalInputStream(){
        byte[] bytes = new byte[] {1,2,3,4,5,6,7,8};
        
        try
        {
            new UnzippingFileProvider(new ByteArrayInputStream(bytes));
            fail("shoud have thrown exception"); //$NON-NLS-1$
        }
        catch (UnzipNotSupportedException e)
        {
            // TOdO deal with this
        }
    }
}
