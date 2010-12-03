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
