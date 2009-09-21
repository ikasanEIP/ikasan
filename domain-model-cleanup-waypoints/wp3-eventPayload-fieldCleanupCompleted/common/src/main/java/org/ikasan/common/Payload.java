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
package org.ikasan.common;

import java.util.List;

/**
 * Payload providing the generic facade for all data to be moved around as a common object.
 * 
 * @author Ikasan Development Team
 */
public interface Payload extends Cloneable
{
		

    /**
	 * Returns the value of the attribute named, or null if no such attribute exists
	 * 
	 * @param attributeName
	 * @return value of the attribute named, or null if no such attribute exists
	 */
	public String getAttribute(String attributeName);

    /**
	 * Returns a List of names of all attributes of this Paylaod
	 * 
	 * @return List<String> attribute names
	 */
	public List<String> getAttributeNames();

    /**
     * Get the content of the payload
     * 
     * @return content of payload
     */
    public byte[] getContent();

 

	/**
	 * Accessor for id
	 * 
	 * @return id
	 */
	public String getId();
	
	/**
	 * Convenience method giving the size of the payload contents
	 * 
	 * @return size of payload contents in bytes
	 */
	public long getSize();
	
	/**
	 * Sets an attribute on the Payload, overriding any prior value
	 * 
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setAttribute(String attributeName, String attributeValue);
	
	/**
     * Set the content of the payload
     * 
     * @param content content to set
     */
    public void setContent(final byte[] content);
	
	/**
	 * Spawns a new child Payload based on this Payload
	 * 
	 * @param siblingNo
	 * @return child Payload
	 */
	public Payload spawnChild(int siblingNo);


	/**
	 * Clones the Payload
	 * 
	 * @return cloned Payload
	 * @throws CloneNotSupportedException
	 */
	public Payload clone() throws CloneNotSupportedException;


}
