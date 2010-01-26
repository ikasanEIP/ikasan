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
package org.ikasan.common.util.checksum;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Interface for consuming checksum files of a known algorithm to extract the checksum value
 * 
 * @author Ikasan Development Team
 *
 */
public interface ChecksumSupplier
{

    /**
     * @param checksumFile
     * @return checksum value as a String
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    public String extractChecksumFromChecksumFile(InputStream checksumFile) throws UnsupportedEncodingException, IOException;

    /**
     * Returns the standard file extension for checksum files
     * 
     * Note that clients may need to ignore case
     * 
     * @return file extension
     */
    public String getFileExtension();
    
    
    /**
     * Calculates a new checksum
     * 
     * @param bytes 
     * @return checksum as a String value
     */
    public String calucluateChecksumString(byte[] bytes);

    /**
     * Returns the name of the checksum algorithm
     * @return name of the algorithm
     */
    public String getAlgorithmName();
    
}
