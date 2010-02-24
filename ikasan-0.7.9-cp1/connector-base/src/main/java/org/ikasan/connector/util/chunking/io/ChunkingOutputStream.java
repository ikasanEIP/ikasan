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
package org.ikasan.connector.util.chunking.io;

import java.io.IOException;
import java.io.OutputStream;

import org.ikasan.connector.util.chunking.process.ChunkHandleException;
import org.ikasan.connector.util.chunking.process.ChunkHandler;


/**
 * An implementation of output stream specifically intended to chunk the output into units
 * of a predefined size. All resulting chunks will be the same size, with the exception 
 * of the last one which me be a remainder of a lesser size
 * 
 * @author Ikasan Development Team
 *
 */
public class ChunkingOutputStream extends OutputStream {


	/**
	 * size of the resulting chunks
	 */
	private int chunkSize ;
	
	
	/**
	 * internal buffer used to construct chunk
	 */
	private byte[] buffer;
	
	
	/**
	 * pointer into the next byte in the buffer
	 */
	private int pointer;
	
	/**
	 * A handler for the resulting chunks
	 */
	private ChunkHandler chunkHandler;
	
	/**
	 * The number of chunks that we have processed so far
	 */
	private long completedChunks;
	
	/**
	 * The number of chunks that we expect in total
	 */
	private long sequenceLength;
	
	@Override
	public void flush() throws IOException {
		super.flush();
		try {
			handleBuffer();
		} catch (ChunkHandleException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Constructor
	 * @param chunkSize
	 * @param chunkHandler
	 * @param sequenceLength
	 * @param startingChunk 
	 */
	public ChunkingOutputStream(int chunkSize, ChunkHandler chunkHandler, long sequenceLength, long startingChunk) {
		this.chunkSize=chunkSize;
		reset();
		this.chunkHandler=chunkHandler;
		this.sequenceLength = sequenceLength;
		this.completedChunks = startingChunk ;
	}


	/**
	 * Reset
	 */
	private void reset() {
		resetBuffer();
		completedChunks=0;
	}

	/**
	 * Clears the buffer and returns the pointer to the beginning
	 */
	private void resetBuffer() {
		buffer = new byte[chunkSize];
		pointer = 0;
	}


	@Override
	public void write(int b) throws IOException {
		buffer[pointer] = (byte) b;
		pointer = pointer+1;

		if (pointer == chunkSize){
			try {
				handleBuffer();
			} catch (ChunkHandleException e) {
				throw new IOException(e.getMessage());
			}
		}
	}


	/**
	 * Tries to do something with the received chunk
	 * Delegates to the ChunkHandler
	 * @throws ChunkHandleException 
	 */
	private void handleBuffer() throws ChunkHandleException {
		byte[] handleable = buffer;
		if (pointer!=chunkSize){
			//this is a short chunk, probably the last one
			handleable = trim(buffer, pointer);
		}

		chunkHandler.handleChunk(handleable, completedChunks, new Long(sequenceLength));
		
		resetBuffer();
		
		completedChunks= completedChunks+1;
	}
	
	
	/**
	 * Trims the end of a byte array if we did not actually fill it
	 * @param untrimmedArray
	 * @param length
	 * @return trimmed byte[]
	 */
	protected byte[] trim(byte[] untrimmedArray, int length){
		//return a smaller array of only the bytes that were retrieved
		byte[] smaller = new byte[length];
		System.arraycopy(untrimmedArray, 0, smaller, 0, length);
		return smaller;
	}


	

}
