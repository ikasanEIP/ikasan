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
     * Creates a <code>String<code> representation of the <code>Payload</code>
     * @return A formatted <code>String</code> representing the <code>Payload</code> object.
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer("DefaultPayload [");
        sb.append("id = [");
        sb.append(id);
        sb.append("], ");
        sb.append("content = [");
        sb.append(new String(this.content));
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

}
