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
package org.ikasan.connector.util.chunking.model;


/**
 * This interface defines the methods used to find and manage related file chunks
 * without their content payloads
 * 
 * @author Ikasan Development Team
 *
 */
public interface FileConstituentHandle extends Comparable<FileConstituentHandle>{
	
	/**
	 * @return the order of this chunk in the chunk sequence
	 */
	public long getOrdinal();

	/**
	 * @return the primary key for this chunk
	 */
	public Long getId();
	
	/**
	 * @return the FileChunkHeader for this chunk
	 */
	public FileChunkHeader getFileChunkHeader();
	




	

	


}
