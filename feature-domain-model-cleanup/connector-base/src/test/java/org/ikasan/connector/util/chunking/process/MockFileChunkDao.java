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
