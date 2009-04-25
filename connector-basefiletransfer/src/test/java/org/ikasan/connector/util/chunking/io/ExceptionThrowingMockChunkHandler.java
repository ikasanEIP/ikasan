/*
 * $Id: ExceptionThrowingMockChunkHandler.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/test/java/org/ikasan/connector/util/chunking/io/ExceptionThrowingMockChunkHandler.java $
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

import org.ikasan.connector.util.chunking.process.ChunkHandleException;
import org.ikasan.connector.util.chunking.process.ChunkHandler;

/**
 * Mock ChunkHandler that throws exceptions
 * 
 * @author Ikasan Development Team
 */
class ExceptionThrowingMockChunkHandler implements ChunkHandler
{

    /** An exception */
    private ChunkHandleException chunkHandleException;

    /**
     * Constructor
     * 
     * @param chunkHandleException
     */
    public ExceptionThrowingMockChunkHandler(ChunkHandleException chunkHandleException)
    {
        super();
        this.chunkHandleException = chunkHandleException;
    }

    public void handleChunk(byte[] chunk, long ordinal, long sequenceLength) throws ChunkHandleException
    {
        throw chunkHandleException;

    }

}