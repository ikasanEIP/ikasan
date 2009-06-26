/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc and individual contributors as indicated
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;

import org.apache.log4j.Logger;
import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.Envelope;
import org.ikasan.common.Payload;

/**
 * Default implementation of the Envelope interface.
 * 
 * @author Ikasan Development Team
 */
public class DefaultEnvelope extends MetaData implements Envelope
{
    /** Serialise ID */
    private static final long serialVersionUID = 1L;

    /** The logger instance */
    private static Logger logger = Logger.getLogger(DefaultEnvelope.class);

    /** Payloads transported within this envelope */
    private List<Payload> payloads;

    /** Make sure the default constructor cannot be invoked */
    @SuppressWarnings("unused")
    private DefaultEnvelope()
    {
        // empty
    }

    /**
     * Creates a new instance of <code>Payload</code> with the specified data content.
     * 
     * @param payload The payload to use
     */
    public DefaultEnvelope(final Payload payload)
    {
        if (payload == null)
        {
            throw new CommonRuntimeException("Envelope constructor called with " //$NON-NLS-1$
                    + "null Payload.", CommonExceptionType.ENVELOPE_INSTANTIATION_FAILED); //$NON-NLS-1$
        }
        // Specific to the envelope
        this.noNamespaceSchemaLocation = null;
        this.schemaInstanceNSURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
        // Create a list of payloads for the payloads setter
        List<Payload> payloadList = new ArrayList<Payload>();
        payloadList.add(payload);
        // Setting the payloads also updates the envelope header
        // Primary Payload populated fields
        this.setPayloads(payloadList);
        // These values are _not_ already set by the setPayloads call above
        this.spec = null;
        this.encoding = Encoding.NOENC.toString();
        this.format = null;
        this.charset = Charset.defaultCharset().toString();
        this.checksumAlg = DEFAULT_CHECKSUM_ALG;
        this.checksum = this.calculateChecksum();
        this.targetSystems = null;
        logger.debug("Envelope constructor created envelope."); //$NON-NLS-1$
    }

    /**
     * Creates a new instance of <code>DefaultEnvelope</code> with the existing payloads.
     * 
     * @param payloads List of payloads to use
     * @throws InstantiationException Exception if we could not create envelope
     */
    public DefaultEnvelope(final List<Payload> payloads) throws InstantiationException
    {
        if (payloads.size() == 0)
        {
            throw new InstantiationException("Envelope constructor called with " //$NON-NLS-1$
                    + "an empty payload list."); //$NON-NLS-1$
        }
        // specific to the envelope
        this.noNamespaceSchemaLocation = null;
        this.schemaInstanceNSURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
        // setting the payloads also updates the envelope header
        // Primary Payload populated fields
        this.setPayloads(payloads);
        // These values are _not_ already set by the setPayloads call above
        this.spec = null;
        this.encoding = Encoding.NOENC.toString();
        this.format = null;
        this.charset = Charset.defaultCharset().toString();
        this.checksumAlg = DEFAULT_CHECKSUM_ALG;
        this.checksum = this.calculateChecksum();
        this.targetSystems = null;
        logger.debug("Envelope constructor created envelope."); //$NON-NLS-1$
    }

    /**
     * Getter for Payloads
     * 
     * @return Payload List
     */
    public List<Payload> getPayloads()
    {
        logger.debug("Returning Envelope payloads."); //$NON-NLS-1$
        return this.payloads;
    }

    /**
     * Setter for Payloads This setter will also update the Primary Payload populated fields on the Envelope header.
     * 
     * @param payloads list of payloads to set
     */
    public void setPayloads(List<Payload> payloads)
    {
        // Check that payloads currently in the envelope are present in some form...
        // create new payloads list OR remove any existing payload entries
        if (this.payloads == null || this.payloads.size() == 0)
        {
            this.payloads = new ArrayList<Payload>();
        }
        else
        {
            this.payloads.clear();
        }
        // Check that payloads parameter to this method present in some form...
        // if the incoming payloads are null or empty then
        // clear down the current payload list and warn that the meta-data on
        // the envelope has not been updated.
        if (payloads == null || payloads.size() == 0)
        {
            this.payloads = new ArrayList<Payload>();
            logger
                .warn("Setting empty 'Payloads' on the envelope. Envelope meta-data cannot be updated as the is no primary payload. Returning envelope meta-data unchanged.");
            return;
        }
        // else add the new ones
        this.payloads.addAll(payloads);
        // update the envelope header for the possibly changed primary payload
        this.id = this.payloads.get(PRIMARY_PAYLOAD).getId();
        this.setTimezone(this.payloads.get(PRIMARY_PAYLOAD).getTimezone());
        this.timestamp = this.payloads.get(PRIMARY_PAYLOAD).getTimestamp();
        this.timestampFormat = this.payloads.get(PRIMARY_PAYLOAD).getTimestampFormat();
        this.priority = this.payloads.get(PRIMARY_PAYLOAD).getPriority();
        this.name = this.payloads.get(PRIMARY_PAYLOAD).getName();
        this.srcSystem = this.payloads.get(PRIMARY_PAYLOAD).getSrcSystem();
        this.processIds = this.payloads.get(PRIMARY_PAYLOAD).getProcessIds();
        logger.debug("Setting Envelope payloads."); //$NON-NLS-1$
    }

    /**
     * Test equality of two envelope instances based on the lifetime identifier.
     * 
     * Only the identifiers are compared as the payload content and associated attributes can change over the life of
     * the payload, however, the originally assigned id never changes.
     * 
     * @param envelope Envelope to check against
     * @return true if we can convert, else false
     */
    public boolean equals(Envelope envelope)
    {
        if (this.id.equals(envelope.getId()))
        {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.component.MetaData#calculateChecksum()
     */
    @Override
    protected String calculateChecksum()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.common.component.MetaData#idToString()
     */
    @Override
    public String idToString()
    {
        return "Envelope Id[" + this.getId() + "] "; //$NON-NLS-1$ //$NON-NLS-2$
    }

	/* (non-Javadoc)
	 * @see org.ikasan.common.MetaDataInterface#getSize()
	 */
	public Long getSize() {
		long size=0;
		for (Payload payload : payloads){
			size = size + payload.getSize();
		}
		return size;
	}


}
