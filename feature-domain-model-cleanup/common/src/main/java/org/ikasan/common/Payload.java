/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
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
