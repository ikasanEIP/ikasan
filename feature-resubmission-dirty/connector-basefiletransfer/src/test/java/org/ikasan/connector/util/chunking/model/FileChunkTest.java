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
