/*
 * $Id: Md5ChecksumSupplier.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/util/checksum/Md5ChecksumSupplier.java $
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
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * MD5 implementation of the Md5ChecksumSupplier
 * 
 * Useful for reading md5 checksum files and extracting the checksum value
 * 
 * @author duncro
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
