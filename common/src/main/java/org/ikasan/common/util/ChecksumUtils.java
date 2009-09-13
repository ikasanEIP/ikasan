/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;

/**
 * A utility class for Checksum activities
 * 
 * @author Ikasan Development Team
 */
public class ChecksumUtils
{
    
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(ChecksumUtils.class);
   
    /** Default algorithm is MD5 */
    private static final String defaultAlgorithm = "MD5"; //$NON-NLS-1$
    
    /**
     * Get the checksum given an ImputStream and an algorithm
     * 
     * @param stream The stream for which to calculate the checksum
     * @param algorithm 
     * @return <code>String</code> representing the checksum
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    public static String getChecksum(InputStream stream, String algorithm)
            throws IOException, NoSuchAlgorithmException
    {
        MessageDigest checksum = null;
        
        checksum = MessageDigest.getInstance(algorithm);
        
        byte[] data = new byte[128];
        while (true)
        {
            int bytesRead = stream.read(data);
            if (bytesRead < 0)
            { // end of stream
                break;
            }
            checksum.update(data, 0, bytesRead);
        }
        byte[] byteDigest = checksum.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteDigest.length; i++)
        {
            String hex = Integer.toHexString(0xff & byteDigest[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return new String(sb.toString());
    }
    
    /**
     * Get the checksum
     * @param stream
     * @return checksum
     * @throws NoSuchAlgorithmException
     */
    public static String getChecksum(ByteArrayOutputStream stream) 
        throws NoSuchAlgorithmException
    {
        return getChecksum(stream, defaultAlgorithm);
    }
    
    /**
     * Get the checksum
     * 
     * @param stream
     * @param algorithm
     * @return checksum
     * @throws NoSuchAlgorithmException
     */
    public static String getChecksum(ByteArrayOutputStream stream, String algorithm)
        throws NoSuchAlgorithmException
    {
        MessageDigest checksum = null;
        checksum = MessageDigest.getInstance(algorithm);
        byte[] buf = stream.toByteArray();
        checksum.update(buf);
        byte[] byteDigest = checksum.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteDigest.length; i++)
        {
            String hex = Integer.toHexString(0xff & byteDigest[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return new String(sb.toString());
    }

    /**
     * Get the checksum
     * 
     * @param stream
     * @return checksum
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String getChecksum(InputStream stream)
            throws NoSuchAlgorithmException, IOException
    {
        return getChecksum(stream, defaultAlgorithm);
    }

    /**
     * Get checksum given a file
     * 
     * @param file
     * @return checksum
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getChecksum(File file) throws IOException,
            NoSuchAlgorithmException
    {
        return getChecksum(file, defaultAlgorithm);
    }

    /**
     * Get checksum given a file and an algorithm
     *  
     * @param file
     * @param algorithm
     * @return checksum
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getChecksum(File file, String algorithm) throws IOException,
            NoSuchAlgorithmException
    {
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        return getChecksum(stream, algorithm);
    }
    
    /**
     * Compare the two checksums as a straight String comparison throw a 
     * SFTPChecksumFailedException if they don't match, else do nothing
     * 
     * @param checksum1 The first checksum
     * @param checksum2 The second checksum
     * @return true if checksums match, else false
     */
    public static boolean checksumMatch(String checksum1, String checksum2)
    {
        logger.debug("Checksum 1: [" + checksum1 + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        logger.debug("Checksum 2: [" + checksum2 + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        if (checksum1.equals(checksum2))
        {
            logger.info("Checksums match."); //$NON-NLS-1$
            return true;
        }
        // Default else
        logger.warn("Checksums do not match."); //$NON-NLS-1$
        return false;
    }

}
