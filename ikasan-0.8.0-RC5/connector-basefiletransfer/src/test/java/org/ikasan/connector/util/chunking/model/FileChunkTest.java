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
package org.ikasan.connector.util.chunking.model;

import junit.framework.TestCase;

import org.ikasan.common.util.checksum.Md5Checksum;
import org.ikasan.connector.util.chunking.ChunkTestUtils;

/**
 * Test class for FileChunk
 * 
 * @author Ikasan Development Team
 */
public class FileChunkTest extends TestCase
{

    /**
     * Tests that the internal Md5 hash works
     */
    public void testGetMd5Hash()
    {
        byte[] testData = ChunkTestUtils.getTestData();

        Md5Checksum checksum = new Md5Checksum();
        checksum.update(testData);
        String checksumValue = checksum.digestToString();

        FileChunk fileChunk = new FileChunk(null, 0, testData);

        assertNull(
            "DigestChecksum value should be null before the calculateChecksum method is called", fileChunk.getMd5Hash()); //$NON-NLS-1$
        fileChunk.calculateChecksum();
        assertNotNull(
            "DigestChecksum value should not be null after the calculateChecksum method is called", fileChunk.getMd5Hash()); //$NON-NLS-1$

        assertEquals(
            "DigestChecksum value of FileChunk, should equal checksum value of payload", checksumValue, fileChunk.getMd5Hash()); //$NON-NLS-1$
        assertEquals(
            "DigestChecksum value of FileChunk, should equal checksum value of payload", checksumValue, fileChunk.getMd5Hash()); //$NON-NLS-1$
    }

    /**
     * Tests that the constructor and accessors work correctly
     */
    public void testConstruction()
    {
        byte[] content = new byte[] { 1, 2, 3 };
        Long ordinal = 1l;
        FileChunkHeader fileChunkHeader = new FileChunkHeader(null, null, null, null);
        FileChunk fileChunk = new FileChunk(fileChunkHeader, ordinal, content);

        assertEquals("ordinal should equal ordinal constructor argument", ordinal.longValue(), fileChunk.getOrdinal()); //$NON-NLS-1$
        assertEquals(
            "fileChunkHeader should equal fileChunkHeader constructor argument", fileChunkHeader, fileChunk.getFileChunkHeader()); //$NON-NLS-1$
        assertEquals("content should equal content constructor argument", content, fileChunk.getContent()); //$NON-NLS-1$
        assertNull("id should be null", fileChunk.getId()); //$NON-NLS-1$
    }

    /**
     * asserts the toString has some implementation
     */
    public void testToString()
    {
        byte[] content = new byte[] { 1, 2, 3 };
        Long ordinal = 1l;
        FileChunkHeader fileChunkHeader = new FileChunkHeader(null, null, null, null);
        FileChunk fileChunk = new FileChunk(fileChunkHeader, ordinal, content);

        assertTrue(
            "toString should include the fileChunkHeader's toString()", fileChunk.toString().indexOf(fileChunkHeader.toString()) > -1); //$NON-NLS-1$
    }

    /**
     * Checks that the compareTo method simply defers to a compare to on the
     * ordinal
     */
    public void testCompareTo()
    {
        assertOrdinalCompareTo(1l, 2l);
        assertOrdinalCompareTo(2l, 2l);
        assertOrdinalCompareTo(2l, 1l);
    }

    /**
     * Checks that the compareTo method simply defers to a compare to on the
     * ordinal
     * 
     * @param ordinal1
     * @param ordinal2
     */
    private void assertOrdinalCompareTo(Long ordinal1, Long ordinal2)
    {
        int chunkCompare = new FileChunk(null, ordinal1, new byte[] {}).compareTo(new FileChunk(null, ordinal2,
            new byte[] {}));
        int ordinalCompare = ordinal1.compareTo(ordinal2);
        assertEquals(chunkCompare, ordinalCompare);
    }

}
