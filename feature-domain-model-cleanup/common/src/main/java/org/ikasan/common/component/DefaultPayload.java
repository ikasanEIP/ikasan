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

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.ikasan.common.Payload;

/**
 * Default implementation of the Payload interface.
 * 
 * @author Ikasan Development Team
 */
public class DefaultPayload implements Payload, Cloneable
{
    /** Serialise ID */
    private static final long serialVersionUID = 1L;

    /** The logger instance */
    private static Logger logger = Logger.getLogger(DefaultPayload.class);

    /** id for payload **/
    private String id;
    
    /** name for payload **/
    private String name;
    
    /** spec for payload **/
    private Spec spec;    
    
    /** name for srcSystem **/
    private String srcSystem;
    
    private Map<String, String> attributes = new HashMap<String, String>();
    
    /**
     * indicator of the charset
     */
    private String charset;
    
    
   

	/** Actual content of the payload to be delivered */
    private byte[] content;
    
    
    
    

    /**
     * Do not let anyone create a payload based on a no-argument default constructor
     */
    @SuppressWarnings("unused")
    private DefaultPayload()
    {
        // empty
    }







    /**
     * Construtor
     * 
     * @param id
     * @param name
     * @param spec
     * @param srcSystem
     * @param content
     */
    public DefaultPayload(String id, String name, Spec spec, String srcSystem,
			byte[] content) {
		this.id = id;
		this.name=name;
		this.spec = spec;
		this.srcSystem = srcSystem;
		this.content = content;
		
	}
    
    /**
     * Accessor for id
     * 
     * @return id for payload
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
     * @see org.ikasan.common.Payload#getSpec()
     */
    public Spec getSpec(){
    	return spec;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#setSpec(org.ikasan.common.component.Spec)
     */
    public void setSpec(Spec spec){
    	this.spec = spec;
    }
    
    /**
     * Accessor for srcSystem
     * 
     * @return srcSystem
     */
    public String getSrcSystem(){
    	return srcSystem;
    }
    
    /**
     * Mutator for srcSystem
     * 
     * @param srcSystem
     */
    public void setSrcSystem(String srcSystem){
    	this.srcSystem = srcSystem;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getName()
     */
    public String getName(){
    	return name;
    }
    

	/* (non-Javadoc)
	 * @see org.ikasan.common.Payload#setName(java.lang.String)
	 */
	public void setName(String name){
    	this.name = name;
    }

	/**
     * Setter for content. This setter by default sets the size and checksum of the particular payload.
     * 
     * @param content The content of the payload to set
     */
    public void setContent(final byte[] content)
    {
        this.content = content;
    }



    /**
     * Getter for content
     * 
     * @return byte[]
     */
    public byte[] getContent()
    {
        return this.content;
    }
   
    
    /* (non-Javadoc)
     * @see org.ikasan.common.Payload#getCharSet()
     */
    public String getCharset() {
		return charset;
	}



	/* (non-Javadoc)
	 * @see org.ikasan.common.Payload#setCharset(java.lang.String)
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
    
    
    

    /**
     * Create a formatted string detailing the payload id of the incoming payload.
     * 
     * @return String
     */

    public String idToString()
    {
        return "Payload Id[" + this.getId() + "] "; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Creates a <code>String<code> representation of the <code>Payload</code>
     * @return A formatted <code>String</code> representing the <code>Payload</code> object.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(512);
        sb.append(super.toString());
        sb.append("Content  = [");//$NON-NLS-1$
        sb.append(new String(this.content));
        sb.append("]\n");//$NON-NLS-1$
        return sb.toString();
    }



    /**
     * Test equality of two payload instances based on the lifetime identifier.
     * 
     * Only the identifiers are compared as the payload content and associated attributes can change over the life of
     * the payload, however, the originally assigned id never changes.
     * 
     * @param payload The payload to check against
     * @return true if we can convert, else false
     */
    public boolean equals(Payload payload)
    {
        if (this.id.equals(payload.getId()))
        {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.Payload#base64EncodePayload()
     */
    public void base64EncodePayload()
    {
        setContent(Base64.encodeBase64(getContent()));
        if (logger.isDebugEnabled())
        {
            logger.debug("Binary payload encoded to base64");
        }
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
        if (srcSystem != null)
        {
            clone.setSrcSystem(new String(srcSystem));
        }
        if (name != null)
        {
            clone.setName(name);
        }
        if (spec != null)
        {
            clone.setSpec(spec);
        }

        byte[] copiedContent = new byte[content.length];
        System.arraycopy(content, 0, copiedContent, 0, content.length);
        clone.setContent(copiedContent);

        return clone;
    }
    
    public long getSize(){
    	return content.length;
    }



	public Payload spawnChild(int siblingNo) {
		byte[] copiedContent = new byte[content.length];
        System.arraycopy(content, 0, copiedContent, 0, content.length);
		return new DefaultPayload(id+"_"+siblingNo, new String(name), spec, new String(srcSystem), copiedContent);
	}







	public String getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}







	public List<String> getAttributeNames() {
		List<String> attributeNames = new ArrayList<String>(attributes.keySet());
		Collections.sort(attributeNames);
		return attributeNames;
	}







	public void setAttribute(String attributeName, String attributeValue) {
		attributes.put(attributeName,attributeValue);
	}


}
