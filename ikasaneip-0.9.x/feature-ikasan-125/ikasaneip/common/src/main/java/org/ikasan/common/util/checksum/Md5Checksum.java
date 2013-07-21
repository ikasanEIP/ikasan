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
 * @author Ikasan Development Team
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
