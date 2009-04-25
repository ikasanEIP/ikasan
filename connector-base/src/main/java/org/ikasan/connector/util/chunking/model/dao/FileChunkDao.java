/*
 * $Id: FileChunkDao.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/util/chunking/model/dao/FileChunkDao.java $
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
