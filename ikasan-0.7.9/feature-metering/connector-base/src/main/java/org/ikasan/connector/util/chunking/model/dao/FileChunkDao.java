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
package org.ikasan.connector.util.chunking.model.dao;

import java.util.List;

import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;

/**
 * Data Access Object for persisting, querying and reloading file chunks
 * @author Ikasan Development Team
 *
 */
public interface FileChunkDao {

	/**
	 * persists a FileChunk to storage
	 * @param fileChunk
	 */
	public void save(FileChunk fileChunk);
	
	/**
	 * Loads a FileChunk from persistent storage
	 * @param fileConstituentHandle
	 * @return FileChunk
	 * @throws ChunkLoadException 
	 */
	public FileChunk load(FileConstituentHandle fileConstituentHandle) throws ChunkLoadException;
	
	/**
	 * Retrieves a set of FileConstituentHandles representing a complete reconsitutable set
	 * @param fileName
	 * @param fileChunkTimeStamp timestamp or latest if null
	 * @param maxAge 
	 * @param noOfChunks 
	 * @return unordered List of FileConstituentHandles referencing chunks matching the fileName
	 */
	public List<FileConstituentHandle> findChunks(String fileName, Long fileChunkTimeStamp, Long noOfChunks, Long maxAge);

	
	/**
	 * Persists a FileChunkHeader
	 * @param fileChunkHeader
	 */
	public void save(FileChunkHeader fileChunkHeader);
	
	/**
	 * Loads an existing FileChunkHeader by id
	 * 
	 * @param id
	 * @return FileChunkHeader
	 * @throws ChunkHeaderLoadException 
	 */
	public FileChunkHeader load(Long id) throws ChunkHeaderLoadException;

    /**
     * Deletes the FileChunkHeader and all related FileChunks
     * 
     * @param fileChunkHeader
     */
    public void delete(FileChunkHeader fileChunkHeader);
}
