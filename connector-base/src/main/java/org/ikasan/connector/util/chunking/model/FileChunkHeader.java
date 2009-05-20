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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.ikasan.common.xml.serializer.XMLSerializer;
import org.ikasan.common.xml.serializer.XStreamXmlSerializerImpl;

/**
 * Model class that contains header information common to all related File
 * Chunks
 * 
 * @author Ikasan Development Team
 */
public class FileChunkHeader
{

    /**
     * Primary key as set by persistence mechanism
     */
    private Long id;

    /**
     * no of chunks for this file
     */
    private Long sequenceLength;

    /**
     * checksum value for the entire file calculated internally when file was
     * sourced/chunked
     */
    private String internalMd5Hash;

    /**
     * checksum value (if any) supplied by the source system prior to upload
     */
    private String externalMd5Hash;

    /**
     * original name of the chunked file
     */
    private String fileName;

    /**
     * The time that this file was chunked
     */
    private Long chunkTimeStamp;

    /**
     * Optional client id
     */
    private String clientId;

    /**
     * serializer for serializing to and from XML
     */
    private static XMLSerializer xmlSerializer;

    static
    {
        Map<String, Class<?>> aliases = new HashMap<String, Class<?>>();
        aliases.put("fileChunkHeader", FileChunkHeader.class);
        xmlSerializer = new XStreamXmlSerializerImpl(aliases);
    }

    /**
     * Constructor
     * 
     * @param sequenceLength
     * @param externalMd5Hash
     * @param fileName
     * @param chunkTimeStamp
     * @param clientId
     */
    public FileChunkHeader(Long sequenceLength, String externalMd5Hash, String fileName, Long chunkTimeStamp,
            String clientId)
    {
        this();
        setSequenceLength(sequenceLength);
        setExternalMd5Hash(externalMd5Hash);
        setFileName(fileName);
        setChunkTimeStamp(chunkTimeStamp);
        setClientId(clientId);
    }

    /**
     * Constructor
     * 
     * @param sequenceLength
     * @param externalMd5Hash
     * @param fileName
     * @param chunkTimeStamp
     */
    public FileChunkHeader(Long sequenceLength, String externalMd5Hash, String fileName, Long chunkTimeStamp)
    {
        this(sequenceLength, externalMd5Hash, fileName, chunkTimeStamp, null);
    }

    /**
     * Reconstitutes an instance from XML
     * 
     * @param xmlString
     * @return new FileChunkHeader
     */
    public static FileChunkHeader fromXml(String xmlString)
    {
        return (FileChunkHeader) xmlSerializer.toObject(xmlString);
    }

    /**
     * Accessor method for Id
     * 
     * @return id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Accessor method for Sequence Length
     * 
     * @return sequenceLength
     */
    public Long getSequenceLength()
    {
        return sequenceLength;
    }

    /**
     * Accessor method for Internal MD5 hash
     * 
     * @return internal MD5 hash
     */
    public String getInternalMd5Hash()
    {
        return internalMd5Hash;
    }

    /**
     * Accessor method for External MD5 hash
     * 
     * @return externalChecksum
     */
    public String getExternalMd5Hash()
    {
        return externalMd5Hash;
    }

    /**
     * Accessor method for file name
     * 
     * @return fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Accessor method for chunk timestamp
     * 
     * @return chunkTimeStamp
     */
    public Long getChunkTimeStamp()
    {
        return chunkTimeStamp;
    }

    /**
     * Set required for Hibernate
     * 
     * @param internalMd5Hash
     */
    public void setInternalMd5Hash(String internalMd5Hash)
    {
        this.internalMd5Hash = internalMd5Hash;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("id", this.id).append("sequenceLength", this.sequenceLength).append( //$NON-NLS-1$ //$NON-NLS-2$
            "fileName", this.fileName).append("internalMd5Hash", this.internalMd5Hash).append("chunkTimeStamp",   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            this.chunkTimeStamp).append("externalMd5Hash", this.externalMd5Hash).toString(); //$NON-NLS-1$
    }

    /**
     * Generates an XML representation of this object
     * 
     * @return XML representation of this object
     */
    public String toXml()
    {
        return xmlSerializer.toXml(this);
    }

    /**
     * Accessor for clientId
     * 
     * @return clientId
     */
    public String getClientId()
    {
        return clientId;
    }

    /*
     * ===================================================== The following
     * methods exist only to support Hibernate
     * =====================================================
     */

    /**
     * No arg constructor required by Hibernate
     */
    protected FileChunkHeader()
    {
        super();
    }

    /**
     * Set required for Hibernate
     * 
     * @param id
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Set required for Hibernate
     * 
     * @param sequenceLength
     */
    private void setSequenceLength(Long sequenceLength)
    {
        this.sequenceLength = sequenceLength;
    }

    /**
     * Set required for Hibernate
     * 
     * @param externalMd5Hash
     */
    private void setExternalMd5Hash(String externalMd5Hash)
    {
        this.externalMd5Hash = externalMd5Hash;
    }

    /**
     * Set required for Hibernate
     * 
     * @param fileName
     */
    private void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Set required for Hibernate
     * 
     * @param chunkTimeStamp
     */
    private void setChunkTimeStamp(Long chunkTimeStamp)
    {
        this.chunkTimeStamp = chunkTimeStamp;
    }

    /**
     * Set required for Hibernate
     * 
     * @param clientId
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    /*
     * ===================================================== The preceding
     * methods exist only to support Hibernate
     * =====================================================
     */

}
