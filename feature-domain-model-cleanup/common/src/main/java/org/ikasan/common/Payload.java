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

import org.ikasan.common.component.Spec;

/**
 * Payload providing the generic facade for all data to be moved around as a common object.
 * 
 * @author Ikasan Development Team
 */
public interface Payload 
{
	
	/**
	 * Accessor for id
	 * 
	 * @return id
	 */
	public String getId();
	

	
	/**
	 * Accessor for <code>Spec</code>
	 * 
	 * @return Spec
	 */
	public Spec getSpec();
	
	
	/**
	 * Mutator for the spec
	 * 
	 * @param spec
	 */
	public void setSpec(Spec spec);
	
	/**
	 * Accessor for charSet
	 * 
	 * @return indication of the character set if this is a textual payload
	 */
	public String getCharset();
	
	/**
	 * Mutator for charset
	 * 
	 * @param charset
	 */
	public void setCharset(String charset);

    /**
     * Set the content of the payload
     * 
     * @param content content to set
     */
    public void setContent(final byte[] content);

    /**
     * Get the content of the payload
     * 
     * @return content of payload
     */
    public byte[] getContent();

    /**
     * Test the equality of two payload instances
     * 
     * @param payload payload to test against
     * @return boolean
     */
    public boolean equals(Payload payload);

    public String idToString();



    /** Base64 encode the payload */
    @Deprecated
    public void base64EncodePayload();

    /**
     * Returns a completely new instance of the payload with a deep copy of all fields. Note the subtle difference in
     * comparison with spawn() which changes some field values to reflect a newly created instance.
     * 
     * @return a Payload
     * @throws CloneNotSupportedException Exception if clone is not supported by implementer
     */
    public Payload clone() throws CloneNotSupportedException;

	/**
	 * Convenience method giving the size of the payload contents
	 * 
	 * @return size of payload contents in bytes
	 */
	public long getSize();
	
	public Payload spawnChild(int siblingNo);
	
	public String getAttribute(String attributeName);
	
	public void setAttribute(String attributeName, String attributeValue);
	
	public List<String> getAttributeNames();


}
