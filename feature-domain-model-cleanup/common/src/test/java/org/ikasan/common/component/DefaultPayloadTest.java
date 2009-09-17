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
package org.ikasan.common.component;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.junit.Assert;
import org.junit.Test;

/**
 * PayloadHelper JUnit test class
 * 
 * @author Ikasan Development Team
 * 
 */
public class DefaultPayloadTest {
	/** The logger */
	private static Logger logger = Logger.getLogger(DefaultPayloadTest.class);

	/** Payload instance being tested */
	Payload payload;
	/** Payload is required as a minimum to create an envelope - payload name */
	String payloadName = "testPayload"; //$NON-NLS-1$
	/**
	 * Payload is required as a minimum to create an envelope - payload source
	 * system
	 */
	String srcSystem = "JUnit"; //$NON-NLS-1$

	String payloadId = "payloadId";

	/**
	 * Test Payload Cloning
	 * 
	 * @throws CloneNotSupportedException
	 */
	@Test
	public void testPayloadClone() throws CloneNotSupportedException {

		this.payload = new DefaultPayload(payloadId, payloadName,
				Spec.TEXT_PLAIN, this.srcSystem, "This is a test".getBytes());

		Payload clonePayload = this.payload.clone();

		// check we have a different object
		Assert.assertFalse(this.payload == clonePayload);

		Assert.assertEquals(this.payload.getId(), clonePayload.getId());

		String originalContent = new String(this.payload.getContent());
		String cloneContent = new String(clonePayload.getContent());
		Assert.assertEquals(originalContent, cloneContent);

		Assert.assertEquals(this.payload.getName(), clonePayload.getName());

		Assert.assertEquals(this.payload.getCharset(), clonePayload
				.getCharset());

		Assert.assertEquals(this.payload.getSpec(), clonePayload.getSpec());
		Assert.assertEquals(this.payload.getSrcSystem(), clonePayload
				.getSrcSystem());

	}
	
	@Test
	public void testSetGetAttribute(){
		DefaultPayload defaultPayload = new DefaultPayload(null, null, Spec.BYTE_JAR, srcSystem, new byte[]{});
		String someAttributeName = "someAttribute";
		String someAttributeValue = "someAttributeValue";
		
		Assert.assertNull("someAttribute should be null before it has been set", defaultPayload.getAttribute(someAttributeName));
		defaultPayload.setAttribute(someAttributeName, someAttributeValue);
		Assert.assertEquals("someAttribute should be available after it has been set", someAttributeValue, defaultPayload.getAttribute(someAttributeName));
		
	}

}
