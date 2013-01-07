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
package org.ikasan.connector.util.chunking;

/**
 * Util class for Chunk tests
 * 
 * @author Ikasan Development Team
 */
public class ChunkTestUtils
{

    /**
     * Set up an array of bytes such that:
     * 
     * <ul>
     * <li>The array is 1001 bytes long</li>
     * <li>each consecutive 100 bytes contains equal bytes, different to the
     * preceding, and following sets</li>
     * </ul>
     * 
     * @return byte array of test data
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
}
