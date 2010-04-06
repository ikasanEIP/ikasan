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


import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 * This class extends the <code>TransformerException</code>. It will be used
 * for throwing exceptions that can subsequently be ignored (i.e. no severe
 * measures required for the exception)
 * 
 * @author Ikasan Development Team
 */
public class TrivialTransformerException extends TransformerException
{
    /** Serial UID */
	private static final long serialVersionUID = -7739084238729829773L;

	/**
	 * Create a new TrivialTransformerException.
	 * @param message - The error or warning message.
	 */
	public TrivialTransformerException(String message)
	{
		super(message);
	}

	/**
	 * Create a new TrivialTransformerException wrapping an existing exception.
	 * @param ex - The exception to be wrapped.
	 */
	public TrivialTransformerException(Throwable ex)
	{
		super(ex);
	}
	
	/**
	 * Wrap an existing exception in a TrivialTransformerException.
	 * This is used for throwing processor exceptions before the processing has 
	 * started.
	 * @param message - The error or warning message, or null to use the message 
	 * from the embedded exception.
	 * @param ex - Any exception.
	 */
	public TrivialTransformerException(String message, Throwable ex)
	{
		super(message, ex);
	}

	/**
	 * Create a new TrivialTransformerException from a message and a Locator.
	 * This constructor is especially useful when an application is creating its 
	 * own exception from within a DocumentHandler callback.
	 * 
	 * @param message - The error or warning message.
	 * @param locator - The locator object for the error or warning.
	 */
	public TrivialTransformerException(String message, SourceLocator locator)
	{
		super(message, locator);
	}

	/**
	 * Wrap an existing exception in a TrivialTransformerException.
	 * 
	 * @param message - The error or warning message, or null to use the message 
	 * from the embedded exception.
	 * @param locator - The locator object for the error or warning.
	 * @param ex - Any exception.
	 */
	public TrivialTransformerException(String message, SourceLocator locator,
			Throwable ex)
	{
		super(message, locator, ex);
	}
}
