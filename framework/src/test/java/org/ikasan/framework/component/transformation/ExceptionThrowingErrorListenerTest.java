/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
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
