 /* 
 * $Id: ExceptionThrowingErrorListener.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/ExceptionThrowingErrorListener.java $
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

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Simple implementation of <code>ErrorListener</code> capable of directly rethrowing the exception
 * 
 * @author Ikasan Development Team
 *
 */
public class ExceptionThrowingErrorListener implements ErrorListener {

	/**
	 * Control flag for 'error' category of errors, defining whether or not such should be rethrown
	 */
	private boolean throwExceptionOnError = true;

	/**
	 * Control flag for 'fatalError' category of errors, defining whether or not such should be rethrown
	 */
	private boolean throwExceptionOnFatalError = true;

	/**
	 * Control flag for 'warning' category of errors, defining whether or not such should be rethrown
	 */
	private boolean throwExceptionOnWarning = true;
	
	
	/**
	 * Constructor
	 * 
	 * @param throwExceptionOnError
	 * @param throwExceptionOnFatalError
	 * @param throwExceptionOnWarning
	 */
	public ExceptionThrowingErrorListener(boolean throwExceptionOnError,
			boolean throwExceptionOnFatalError, boolean throwExceptionOnWarning) {
		super();
		this.throwExceptionOnError = throwExceptionOnError;
		this.throwExceptionOnFatalError = throwExceptionOnFatalError;
		this.throwExceptionOnWarning = throwExceptionOnWarning;
	}

	/**
	 * Constructor
	 */
	public ExceptionThrowingErrorListener() {
		super();
	}

	/**
	 * Setter method for throwExceptionOnError flag
	 * @param throwExceptionOnError
	 */
	public void setThrowExceptionOnError(boolean throwExceptionOnError) {
		this.throwExceptionOnError = throwExceptionOnError;
	}
	
	/**
	 * Setter method for throwExceptionOnFatalError flag
	 * @param throwExceptionOnFatalError
	 */
	public void setThrowExceptionOnFatalError(boolean throwExceptionOnFatalError) {
		this.throwExceptionOnFatalError = throwExceptionOnFatalError;
	}
	
	/**
	 * Setter method for throwExceptionOnWarning flag
	 * @param throwExceptionOnWarning
	 */
	public void setThrowExceptionOnWarning(boolean throwExceptionOnWarning) {
		this.throwExceptionOnWarning = throwExceptionOnWarning;
	}

	/* (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	public void error(TransformerException exception)
			throws TransformerException {
		if (throwExceptionOnError){
			throw exception;
		}

	}

	/* (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	public void fatalError(TransformerException exception)
			throws TransformerException {
		if (throwExceptionOnFatalError){
			throw exception;
		}

	}

	/* (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	public void warning(TransformerException exception)
			throws TransformerException {
		if (throwExceptionOnWarning){
			throw exception;
		}

	}

}
