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
