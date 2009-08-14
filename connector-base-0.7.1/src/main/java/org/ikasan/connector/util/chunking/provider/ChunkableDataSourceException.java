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
package org.ikasan.connector.util.chunking.provider;


/**
 * Exception to indicate an problem accessing the data from
 * a Data Provider. 
 * 
 * Not to be used for connectivity issues, this represents a 
 * problem either finding, or sourcing a data resource 
 * from a properly connected provider
 * 
 * @author Ikasan Development Team
 *
 */
public class ChunkableDataSourceException extends Exception {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -670842184886559555L;

	/**
	 * Constructor
	 * 
	 * @param message
	 * @param t
	 */
	public ChunkableDataSourceException(String message, Throwable t) {
		super(message, t);
	}

	/**
	 * @param message
	 */
	public ChunkableDataSourceException(String message) {
		super(message);
	}

}
