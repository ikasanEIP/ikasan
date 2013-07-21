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
