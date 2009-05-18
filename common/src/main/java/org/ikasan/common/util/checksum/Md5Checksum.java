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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Simple DigestChecksum implementation utilising an MD5 Digest
 * 
 * Public interface is similar to the java.util.zip.Checksum
 * interface, except that rather than implementing the 
 * getValue method returning a long, we are instead interested
 * in the String value of the resultant hash on digest.
 * 
 * Also note that calling any of the digest methods resets 
 * the digest itself to its initial state
 * 
 * @author duncro
 *
 */
public class Md5Checksum implements DigestChecksum {

	/**
	 * String representation of the Algorithm itself
	 */
	private static final String MD5_ALGORITHM = "MD5";
	
	
	/**
	 * Message Digest
	 */
	private MessageDigest messageDigest;
	

	/**
	 * Constructor
	 */
	public Md5Checksum() {
		super();
		reset();
	}

	/* (non-Javadoc)
	 * @see fileChunk.util.DigestChecksum#digestToString()
	 */
	public String digestToString() {
		byte[] byteDigest = messageDigest.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteDigest.length; i++)
        {
            String hex = Integer.toHexString(0xff & byteDigest[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
       
        return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see fileChunk.util.DigestChecksum#digestToBigInteger()
	 */
	public BigInteger digestToBigInteger(){
		return new BigInteger(digestToString(), 16);
	}
	
	

	/* (non-Javadoc)
	 * @see fileChunk.util.DigestChecksum#reset()
	 */
	public void reset() {
		try {
			messageDigest = MessageDigest.getInstance(MD5_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}

	/* (non-Javadoc)
	 * @see fileChunk.util.DigestChecksum#update(byte)
	 */
	public void update(byte b) {
		messageDigest.update(b);

	}

	/* (non-Javadoc)
	 * @see fileChunk.util.DigestChecksum#update(byte[], int, int)
	 */
	public void update(byte[] b, int off, int len) {

	    if (off < 0 || len < 0 || off > b.length - len) {
		    throw new ArrayIndexOutOfBoundsException();
		}
		messageDigest.update(b, off, len);
	}
	
	/* (non-Javadoc)
	 * @see fileChunk.util.DigestChecksum#update(byte[])
	 */
	public void update(byte[] bytes){
		messageDigest.update(bytes);
	}

}
