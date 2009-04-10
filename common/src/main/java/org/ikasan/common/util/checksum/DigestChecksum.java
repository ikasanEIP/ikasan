/*
 * $Id: DigestChecksum.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/util/checksum/DigestChecksum.java $
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

/**
 * Definition of a Digest backed DigestChecksum calculator
 *  
 * @author duncro
 *
 */
public interface DigestChecksum {

	/**
	 * Returns the value of the cryptographic hash as a <code>String</code>
	 * 
	 * Note that this also resets the digest to its initial state
	 * @return String representation of the resultant hash
	 */
	public abstract String digestToString();

	/**
	 * Returns the value of the cryptographic hash as a <code>BigInteger</code>
	 * 
	 * Note that this also resets the digest to its initial state
	 * @return BigInteger representation of the resultant hash
	 */
	public abstract BigInteger digestToBigInteger();

	/**
	 * Resets the digest to its initial state
	 */
	public abstract void reset();

	/**
	 * Updates the digest with the specified byte
	 * @param b
	 */
	public abstract void update(byte b);

	/**
	 * Updates the digest with the specified bytes
	 * @param b byte array
	 * @param off offset
	 * @param len length
	 */
	public abstract void update(byte[] b, int off, int len);

	/**
	 * Updates the digest with the specified bytes
	 * @param bytes byte array
	 */
	public abstract void update(byte[] bytes);

}
