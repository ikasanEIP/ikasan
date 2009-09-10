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

import java.util.ArrayList;
import java.util.List;

import org.ikasan.connector.util.chunking.process.ChunkHandler;

/**
 * A Mock object representing the ChunkHandler
 * 
 * @author Ikasan Development Team
 */
public class MockChunkHandler implements ChunkHandler
{

    /** The chunks of data */
    List<byte[]> chunks = new ArrayList<byte[]>();

    public void handleChunk(byte[] chunk, long ordinal, long sequenceLength)
    {
        chunks.add(chunk);
    }

    /**
     * Get the chunks
     * @return chunks
     */
    public List<byte[]> getChunks()
    {
        return chunks;
    }

}
