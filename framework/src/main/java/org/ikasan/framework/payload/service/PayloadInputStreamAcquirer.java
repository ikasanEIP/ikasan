/* 
 * $Id: PayloadInputStreamAcquirer.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/payload/service/PayloadInputStreamAcquirer.java $
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
package org.ikasan.framework.payload.service;

import java.io.IOException;
import java.io.InputStream;

import org.ikasan.common.Payload;

/**
 * Capable of providing an <code>InputStream</code> in response to a specified <code>Payload</code>
 * 
 * @author Ikasan Development Team
 *
 */
public interface PayloadInputStreamAcquirer {

	/**
	 * Provides an InputStream for this Payload
	 * 
	 * @param payload
	 * @return InputStream
	 * @throws IOException
	 */
	public InputStream acquireInputStream(Payload payload) throws IOException;
}
