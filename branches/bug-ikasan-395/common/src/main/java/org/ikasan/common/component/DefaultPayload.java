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
package org.ikasan.common.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.common.Payload;

/**
 * Default implementation of the Payload interface.
 * 
 * @author Ikasan Development Team
 */
public class DefaultPayload implements Payload
{
    /** Serialise ID */
    private static final long serialVersionUID = 1L;


    /** id for payload **/
    private String id;

    /** optional attributes **/
    private Map<String, String> attributes = new HashMap<String, String>();

	/** Actual content of the payload to be delivered */
    private byte[] content;
    
    private Long persistenceId;
    

	/**
     * No args constructor required for ORM
     */
    @SuppressWarnings("unused")
    private DefaultPayload(){}


    /**
     * Constructor
     * 
     * @param id
     * @param content
     */
    public DefaultPayload(String id, byte[] content) {
		this.id = id;
		this.content = content;
	}
    

    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getId()
     */
    public String getId(){
    	return id;
    }
    
    /**
     * Mutator for id, required by ORM
     * 
     * @param id
     */
    @SuppressWarnings("unused")
	private void setId(String id){
    	this.id=id;
    } 



    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#setContent(byte[])
     */
    public void setContent(final byte[] content)
    {
        this.content = content;
    }




    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getContent()
     */
    public byte[] getContent()
    {
        return this.content;
    }



    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.Payload#clone()
     */
    @Override
    public Payload clone() throws CloneNotSupportedException
    {
        DefaultPayload clone = (DefaultPayload) super.clone();
        // sort out non-cloneable objects


        byte[] copiedContent = new byte[content.length];
        System.arraycopy(content, 0, copiedContent, 0, content.length);
        clone.setContent(copiedContent);

        return clone;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getSize()
     */
    public long getSize(){
    	return content.length;
    }



	/* (non-Javadoc)
	 * @see org.ikasan.common.Payload#spawnChild(int)
	 */
	public Payload spawnChild(int siblingNo) {
		byte[] copiedContent = new byte[content.length];
        System.arraycopy(content, 0, copiedContent, 0, content.length);
		return new DefaultPayload(id+"_"+siblingNo,  copiedContent);
	}


	/* (non-Javadoc)
	 * @see org.ikasan.common.Payload#getAttribute(java.lang.String)
	 */
	public String getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}


	/* (non-Javadoc)
	 * @see org.ikasan.common.Payload#getAttributeNames()
	 */
	public List<String> getAttributeNames() {
		List<String> attributeNames = new ArrayList<String>(attributes.keySet());
		Collections.sort(attributeNames);
		return attributeNames;
	}


	/* (non-Javadoc)
	 * @see org.ikasan.common.Payload#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String attributeName, String attributeValue) {
		attributes.put(attributeName,attributeValue);
	}
	
    /**
     * Accessor for persistence Id
     * Note this field should only be used by ORM
     * 
     * @return persistenceId
     */
    public Long getPersistenceId() {
		return persistenceId;
	}

    /**
     * Mutator for persistence Id
     * Note this field should only be used by ORM
     * 
	 * @param persistenceId
	 */
	public void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}

    /**
     * Creates a <code>String<code> representation of the <code>Payload</code>
     * @return A formatted <code>String</code> representing the <code>Payload</code> object.
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer("DefaultPayload [");
        sb.append("id = [");sb.append(id);sb.append("], ");
        sb.append("persistenceId = [");sb.append(persistenceId);sb.append("], ");
        sb.append("content = [");
        sb.append(new String(this.content));
        sb.append("], ");
        sb.append("class = [");sb.append(getClass());sb.append("]");
        sb.append("]");
        return sb.toString();
    }


	/* (non-Javadoc)
	 * @see org.ikasan.common.Payload#getAttributeMap()
	 */
	public Map<String, String> getAttributeMap() {
		Map<String, String> result = new HashMap<String, String>();
		result.putAll(attributes);
		return result;
	}

}
