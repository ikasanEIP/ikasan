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
package org.ikasan.connector.util.chunking.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ikasan.common.util.checksum.DigestChecksum;
import org.ikasan.common.util.checksum.Md5Checksum;

import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;

/**
 * Simple Mock implementation of the FileChunkDao
 * 
 * @author Ikasan Development Team
 * 
 */
public class MockFileChunkDao implements FileChunkDao
{

    /**
     * Preset list of Chunks
     */
    private List<FileChunk> chunks;

    /**
     * Preset list of handles
     */
    private List<FileConstituentHandle> handles;

    /**
     * Internal map
     */
    Map<FileConstituentHandle, FileChunk> mappedChunks = new HashMap<FileConstituentHandle, FileChunk>();

    /**
     * Accessible last saved object
     */
    private FileChunk lastSaved = null;

    /**
     * @return newMockFileChunkDao
     */
    public static MockFileChunkDao create()
    {
        FileChunkHeader fileChunkHeader = new FileChunkHeader(2l, null, null, null);
        byte[] testData = MockFileChunkDao.getTestData();
        fileChunkHeader.setInternalMd5Hash(getChecksum(testData));

        List<FileChunk> fileChunks = new ArrayList<FileChunk>();

        byte[] chunk0 = new byte[1000];
        byte[] chunk1 = new byte[1];

        System.arraycopy(testData, 0, chunk0, 0, 1000);
        System.arraycopy(testData, 1000, chunk1, 0, 1);

        fileChunks.add(new FileChunk(fileChunkHeader, 0, chunk0));
        fileChunks.add(new FileChunk(fileChunkHeader, 1, chunk1));

        return new MockFileChunkDao(fileChunks);

    }

    /**
     * Constructor
     * 
     * @param chunks
     */
    public MockFileChunkDao(List<FileChunk> chunks)
    {
        super();
        this.chunks = chunks;
        handles = new ArrayList<FileConstituentHandle>();
        for (Iterator<FileChunk> iterator = chunks.iterator(); iterator.hasNext();)
        {
            FileChunk fileChunk = iterator.next();
            handles.add(fileChunk);
        }
    }

    public List<FileConstituentHandle> findChunks(String fileName, Long fileChunkTimeStamp, Long noOfChunks, Long maxAge)
    {
        return handles;
    }

    public FileChunk load(FileConstituentHandle fileConstituentHandle)
    {
        int index = handles.indexOf(fileConstituentHandle);
        return chunks.get(index);
    }

    public void save(FileChunk fileChunk)
    {
        lastSaved = fileChunk;
    }

    public void save(FileChunkHeader fileChunkHeader)
    {
        // TODO Auto-generated method stub

    }

    /**
     * Accessor method for checksum
     * 
     * @param chunk
     * @return checksum
     */
    public static String getChecksum(byte[] chunk)
    {
        DigestChecksum checksum = new Md5Checksum();
        checksum.reset();
        checksum.update(chunk);
        return checksum.digestToString();
    }

    /**
     * Test data generation method
     * 
     * @return byte[]
     */
    public static byte[] getTestData()
    {
        byte[] testData = new byte[1001];

        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 100; j++)
            {
                int index = (100 * i) + j;
                testData[index] = (byte) i;
            }
        }
        testData[1000] = (byte) 1;
        return testData;
    }

    /**
     * Accessor method for lastSaved
     * 
     * @return lastSaved
     */
    public FileChunk getLastSaved()
    {
        return lastSaved;
    }

    public void delete(FileChunkHeader fileChunkHeader)
    {
        // TODO Auto-generated method stub

    }

    public FileChunkHeader load(Long id)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
