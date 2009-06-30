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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import javax.xml.XMLConstants;

// Imported log4j classes
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

// standard payload interface
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.util.ChecksumUtils;

/**
 * Default implementation of the Payload interface.
 * 
 * @author Ikasan Development Team
 */
public class DefaultPayload extends MetaData implements Payload, Cloneable
{
    /** Serialise ID */
    private static final long serialVersionUID = 1L;

    /** The logger instance */
    private static Logger logger = Logger.getLogger(DefaultPayload.class);

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
    
    private Long persistenceId;

    public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}

	/**
     * Default constructor Creates a new instance of <code>Payload</code> with the empty data content.
     * 
     * @param name Name of the payload
     * @param spec Spec of the payload
     * @param srcSystem Src System of the payload
     */
    public DefaultPayload(final String name, final String spec, final String srcSystem)
    {
        this(name, spec, srcSystem, new String("").getBytes()); //$NON-NLS-1$
    }

    /**
     * Creates a new instance of <code>Payload</code> with the specified data content.
     * 
     * @param name Name of the payload
     * @param spec Spec of the payload
     * @param srcSystem Src System of the payload
     * @param content Content of the payload
     */
    public DefaultPayload(final String name, final String spec, final String srcSystem, final byte content[])
    {
        this.noNamespaceSchemaLocation = null;
        this.schemaInstanceNSURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
        this.name = name;
        this.spec = spec;
        this.srcSystem = srcSystem;
        this.id = generateId();
        this.setTimezone(MetaDataInterface.DEFAULT_TIMEZONE);
        this.timestamp = generateTimestamp();
        this.timestampFormat = DEFAULT_TIMESTAMP_FORMAT;
        this.priority = new Integer(Priority.NORMAL.getLevel());
        this.content = content;
        this.encoding = Encoding.NOENC.toString();
        this.charset = Charset.defaultCharset().toString();
        this.checksumAlg = DEFAULT_CHECKSUM_ALG;
        this.checksum = this.calculateChecksum();
        if (logger.isDebugEnabled()) logger.debug("Payload constructor created payload."); //$NON-NLS-1$
    }

    /**
     * This constructor has a serious flaw and does not create a copy of the payload. DO NOT USE THIS CONSTRUCTOR.
     * 
     * @param payload The payload to clone off
     * @deprecated - this is not a deep copy and simply returns a pointer to the same object.
     */
    @Deprecated
    public DefaultPayload(final Payload payload)
    {
        this.noNamespaceSchemaLocation = payload.getNoNamespaceSchemaLocation();
        this.schemaInstanceNSURI = payload.getSchemaInstanceNSURI();
        this.id = payload.getId();
        this.priority = payload.getPriority();
        this.timestamp = payload.getTimestamp();
        this.timestampFormat = payload.getTimestampFormat();
        this.timezone = payload.getTimezone();
        this.content = payload.getContent();
        this.name = payload.getName();
        this.spec = payload.getSpec();
        this.encoding = payload.getEncoding();
        this.format = payload.getFormat();
        this.charset = payload.getCharset();
        this.checksumAlg = payload.getChecksumAlg();
        this.checksum = payload.getChecksum();
        this.srcSystem = payload.getSrcSystem();
        this.targetSystems = payload.getTargetSystems();
        this.processIds = payload.getProcessIds();
        if (logger.isDebugEnabled()) logger.debug("Payload constructor created payload."); //$NON-NLS-1$
    }

    /**
     * Setter for content. This setter by default sets the size and checksum of the particular payload.
     * 
     * @param content The content of the payload to set
     */
    public void setContent(final byte[] content)
    {
        this.content = content;
        if (logger.isDebugEnabled()) logger.debug("Content set to [" + this.content + "]."); //$NON-NLS-1$//$NON-NLS-2$
        this.setChecksum();
    }

    /**
     * Setter for content that allows to override the default functionality of setting the respective size and checksum
     * automatically.
     * 
     * @param content The content to set
     * @param contentOnly If true, set the content only, otherwise set the content and adjust the size and checksum
     *            automatically.
     */
    public void setContent(final byte[] content, boolean contentOnly)
    {
        if (contentOnly)
        {
            this.content = content;
        }
        else
        {
            setContent(content);
        }
    }

    /**
     * Getter for content
     * 
     * @return byte[]
     */
    public byte[] getContent()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Returning content [" + this.content + "]."); //$NON-NLS-1$//$NON-NLS-2$
        }
        return this.content;
    }


    /**
     * Method used to calculate the checksum of the <code>content</code> byte array. By default, the checksum is
     * calculated using the <code>MD5</code> algorithm.
     * 
     * @return The MD5 checksum of the <code>content</code> as a hexadecimal <code>String</code>.
     */
    @Override
    protected String calculateChecksum()
    {
        String result = null;
        // Check whether the content is null
        if (this.content == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Content was null, returning a null checksum for the payload.");
            }
        }
        else
        {
            try
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Calculating checksum for the payload.");
                }
                // we have content so lets get a checksum
                InputStream stream = new ByteArrayInputStream(this.content);
                result = ChecksumUtils.getChecksum(stream, this.getChecksumAlg());
            }
            catch (IOException e)
            {
                logger.warn("Exception encountered when calculating payload " //$NON-NLS-1$
                        + "content checksum. Setting checksum to [null].", e); //$NON-NLS-1$
            }
            catch (NoSuchAlgorithmException e)
            {
                logger.warn("Exception encountered when calculating payload " //$NON-NLS-1$
                        + "content checksum. Setting checksum to [null].", e); //$NON-NLS-1$
            }
        }
        return result;
    }

    /**
     * Create a formatted string detailing the payload id of the incoming payload.
     * 
     * @return String
     */
    @Override
    public String idToString()
    {
        return "Payload Id[" + this.getId() + "] "; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Creates a <code>String<code> representation of the <code>Payload</code> showing all the objects properties and an
     * many characters of the content as specified in the <code>cLength</code> parameter. If the <code>cLength
     * </code> is negative,
     * the content is display from the end of the content string.
     * 
     * @param cLength Used to restrict the length of the payload to a specific number of chars.
     * @return A formatted <code>String</code> representing the <code>Payload</code> object.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(512);
        sb.append(super.toString());
        sb.append("Content  = [");//$NON-NLS-1$
        if (content!=null){
        	sb.append(new String(this.content));
        }
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
        setEncoding(Encoding.BASE64.toString());
        if (logger.isDebugEnabled())
        {
            logger.debug("Binary payload encoded to [" //$NON-NLS-1$ 
                    + Encoding.BASE64.toString() + "]"); //$NON-NLS-1$
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
        Payload clone = (Payload) super.clone();
        // sort out non-cloneable objects
        if (this.getPriority() != null)
        {
            clone.setPriority(new Integer(this.getPriority()));
        }
        if (this.getTimestamp() != null)
        {
            clone.setTimestamp(new Long(this.getTimestamp()));
        }
        byte[] copiedContent = new byte[content.length];
        System.arraycopy(content, 0, copiedContent, 0, content.length);
        clone.setContent(copiedContent);
        clone.setChecksum();
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.Payload#spawn()
     */
    public Payload spawn() throws CloneNotSupportedException
    {
        Payload spawned = this.clone();
        // Stamp certain fields with new values
        spawned.setId(generateId());
        spawned.setTimestamp(generateTimestamp());
        return spawned;
    }

	/* (non-Javadoc)
	 * @see org.ikasan.common.MetaDataInterface#getSize()
	 */
	public Long getSize() {
		int size = 0;
		if (content!=null){
			size = content.length;
		}
		return Long.valueOf(size);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.common.Payload#getDisplayableContent()
	 */
	public String getDisplayableContent() {
		return new String(content);
	}
}
