/*
 * $Id: ChunkableDataProvider.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/util/chunking/provider/ChunkableDataProvider.java $
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
