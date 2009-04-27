/* 
 * $Id: ExceptionThrowingErrorListenerTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/component/transformation/ExceptionThrowingErrorListenerTest.java $
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
package org.ikasan.framework.component.transformation;

import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for ExceptionThrowingErrorListener
 * 
 * @author Ikasan Development Team
 *
 */
public class ExceptionThrowingErrorListenerTest {

	/**
	 * An exception to handle
	 */
	private TransformerException transformerException = new TransformerException("blah");
	
	/**
	 * Tests that with default constructor arguments, error method rethrows same exception
	 */
	@Test
	public void testError_WithDefautSettings() {
		ExceptionThrowingErrorListener errorListener = new ExceptionThrowingErrorListener();
		TransformerException thrownException = null;
		try{
			errorListener.error(transformerException);
			Assert.fail("exception should have been thrown");
		}catch(TransformerException t){
			thrownException=t;
		}
		Assert.assertEquals("thrown exception should have been the exception passed to listener", transformerException, thrownException);
	}

	/**
	 * Tests that with default constructor arguments, fatalError method rethrows same exception
	 */
	@Test
	public void testFatalError_WithDefautSettings() {
		ExceptionThrowingErrorListener errorListener = new ExceptionThrowingErrorListener();
		TransformerException thrownException = null;
		try{
			errorListener.fatalError(transformerException);
			Assert.fail("exception should have been thrown");
		}catch(TransformerException t){
			thrownException=t;
		}
		Assert.assertEquals("thrown exception should have been the exception passed to listener", transformerException, thrownException);
	}

	/**
	 * Tests that with default constructor arguments, warning method rethrows same exception
	 */
	@Test
	public void testWarning_WithDefautSettings() {
		ExceptionThrowingErrorListener errorListener = new ExceptionThrowingErrorListener();
		TransformerException thrownException = null;
		try{
			errorListener.warning(transformerException);
			Assert.fail("exception should have been thrown");
		}catch(TransformerException t){
			thrownException=t;
		}
		Assert.assertEquals("thrown exception should have been the exception passed to listener", transformerException, thrownException);
	}
	
	/**
	 * Tests that with constructor argument set to false, error method does not rethrow exception
	 * @throws TransformerException 
	 */
	@Test
	public void testError_WithRethrowingDisabled() throws TransformerException {
		ExceptionThrowingErrorListener errorListener = new ExceptionThrowingErrorListener(false, true, true);
		errorListener.error(transformerException);
	}
	
	/**
	 * Tests that with constructor argument set to false, fatalError method does not rethrow exception
	 * @throws TransformerException 
	 */
	@Test
	public void testFatalError_WithRethrowingDisabled() throws TransformerException {
		ExceptionThrowingErrorListener errorListener = new ExceptionThrowingErrorListener(true, false, true);
		errorListener.fatalError(transformerException);
	}

}
