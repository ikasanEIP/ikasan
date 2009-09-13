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
package org.ikasan.common.util.checksum;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * MD5 implementation of the Md5ChecksumSupplier
 * 
 * Useful for reading md5 checksum files and extracting the checksum value
 * 
 * @author Ikasan Development Team
 *
 */
public class Md5ChecksumSupplier implements ChecksumSupplier
{
	
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(Md5ChecksumSupplier.class);

    /** Name of the algorithm, used by external callers */
	public static final String MD5 = "MD5";

    /** The pattern for a MD5 algorithm (32 alphanumerics) */
    private static final String MD5_PATTERN = "[0-9a-zA-Z]{32}";
    
    /**
     * File extension for md5 files
     * Note that client may need to ignore case
     */
    private static final String MD5_FILE_EXTENSION = ".md5";
    
    /** Name of the algorithm */
    private static final String algorithmName = MD5;
    
    public String extractChecksumFromChecksumFile(InputStream checksumFile) throws IOException
    {
        String result = null;
        
        // Read in the raw content from the checksum file
        InputStreamReader reader = new InputStreamReader(checksumFile,
            "UTF-8");
        StringBuffer stringBuffer = new StringBuffer();
        
        // Different OS's generate the checksum in a different place in the file 
        // so we need to be a bit cunning and use regular expressions to go and find it.
        
        int length = 0;
        int character;
        logger.debug("Reading in checksum file..."); //$NON-NLS-1$
        while ((character = reader.read()) != -1)
        {
            length++;
            stringBuffer.append((char) character);
        }
        logger.debug("Checksum file read: [" + stringBuffer.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        Pattern pattern = Pattern.compile(MD5_PATTERN);
        logger.info("Extracting Checksum usign regular expression pattern [" + pattern.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        Matcher matcher = pattern.matcher(stringBuffer);
        if (matcher.find())
        {
        	logger.debug("Found Checksum"); //$NON-NLS-1$
        	result = matcher.group();
        }
        else
        {
        	logger.info("Checksum was not found, checksum match will almost certainly fail."); //$NON-NLS-1$
        }
        
        return result;
    }

    public String getFileExtension()
    {
        return MD5_FILE_EXTENSION;
    }

    public String calucluateChecksumString(byte[] bytes)
    {
        DigestChecksum digestChecksum = new Md5Checksum();
        digestChecksum.update(bytes);
        return digestChecksum.digestToString();
    }

    public String getAlgorithmName()
    {
        return algorithmName;
    }
    
    /**
     * TODO Unit Test
     * @param args
     * @throws IOException
     */
    /*
    public static void main(String[] args) throws IOException
    {
    	File file = new File(args[0]);
    	FileInputStream fis = new FileInputStream(file);
    	Md5ChecksumSupplier supplier = new Md5ChecksumSupplier(); 
    	supplier.extractChecksumFromChecksumFile(fis);
    }
    */
    
}
