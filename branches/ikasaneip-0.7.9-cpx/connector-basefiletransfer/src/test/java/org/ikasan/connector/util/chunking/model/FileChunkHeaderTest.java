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

/**
 * Test class for FileChunkHeader
 * 
 * @author Ikasan Development Team
 */
public class FileChunkHeaderTest extends TestCase
{

    /**
     * Tests the constructor and accessors
     */
    public void testConstructor()
    {
        String fileName = "fileName";
        Long chunkTimestamp = 1l;
        String externalMd5Hash = "testHash";
        Long sequenceLength = 100l;
        FileChunkHeader fileChunkHeader = new FileChunkHeader(sequenceLength, externalMd5Hash, fileName, chunkTimestamp);

        assertEquals("fileName should equal fileName constructor argument", fileName, fileChunkHeader.getFileName()); //$NON-NLS-1$
        assertEquals(
            "chunkTimestamp should equal chunkTimestamp constructor argument", chunkTimestamp.longValue(), fileChunkHeader.getChunkTimeStamp().longValue()); //$NON-NLS-1$
        assertEquals(
            "externalMd5Hash should equal externalMd5Hash constructor argument", externalMd5Hash, fileChunkHeader.getExternalMd5Hash()); //$NON-NLS-1$
        assertEquals(
            "sequenceLength should equal sequenceLength constructor argument", sequenceLength.longValue(), fileChunkHeader.getSequenceLength().longValue()); //$NON-NLS-1$
        assertNull("id should be null", fileChunkHeader.getId()); //$NON-NLS-1$
    }

    /**
     * Trivially tests that the toString has been implemented
     */
    public void testToString()
    {
        String fileName = "fileName";
        Long chunkTimestamp = 1l;
        String externalMd5Hash = "testHash";
        Long sequenceLength = 100l;
        FileChunkHeader fileChunkHeader = new FileChunkHeader(sequenceLength, externalMd5Hash, fileName, chunkTimestamp);

        assertTrue("toString should include the fileName's toString()", fileChunkHeader.toString().indexOf( //$NON-NLS-1$
            fileName.toString()) > -1);
        assertTrue("toString should include the chunkTimestamp's toString()", fileChunkHeader.toString().indexOf( //$NON-NLS-1$
            chunkTimestamp.toString()) > -1);
        assertTrue("toString should include the externalMd5Hash's toString()", fileChunkHeader.toString().indexOf( //$NON-NLS-1$
            externalMd5Hash.toString()) > -1);
        assertTrue("toString should include the sequenceLength's toString()", fileChunkHeader.toString().indexOf( //$NON-NLS-1$
            sequenceLength.toString()) > -1);

    }

}
