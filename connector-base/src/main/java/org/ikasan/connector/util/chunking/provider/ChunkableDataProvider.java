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
package org.ikasan.connector.util.chunking.provider;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents an underlying transport, protocol or system capable of sourcing files for chunking
 * 
 * @author Ikasan Development Team
 */
public interface ChunkableDataProvider {
	
	/**
	 * Connects to the underlying transport
	 * @throws ChunkableDataProviderAccessException
	 */
	public void connect() throws ChunkableDataProviderAccessException;

	/**
	 * Disconnects from the underlying transport
	 * @throws ChunkableDataProviderAccessException
	 */
	public void disconnect() throws ChunkableDataProviderAccessException;



	/**
	 * Accesses the chunkable data as an InputStream
	 * 
	 * @param remoteDir
	 * @param fileName
	 * @return InputStream on the chunkable data
	 * @throws ChunkableDataSourceException
	 */
	public InputStream sourceChunkableData(String remoteDir,
			String fileName) throws ChunkableDataSourceException;


	/**
	 * Accesses the chunkable data providing an OutputStream
	 * 
	 * @param remoteDir
	 * @param fileName
	 * @param outputStream
	 * @param offset
	 * @throws ChunkableDataSourceException
	 */
	public void sourceChunkableData(String remoteDir, String fileName,
			OutputStream outputStream, long offset) throws ChunkableDataSourceException;
	

	/**
	 * Gets the file size of the resource we are chunking
	 * @param remoteDir
	 * @param fileName
	 * @return file size in bytes
	 * @throws ChunkableDataSourceException
	 */
	public long getFileSize(String remoteDir, String fileName) throws ChunkableDataSourceException;




}
