/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
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
 * ====================================================================
 */
package org.ikasan.connector.util.chunking.model;

import java.util.HashMap;
import java.util.Map;

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
    	StringBuffer sb = new StringBuffer("FileChunkHeader [");
    	sb.append("id");sb.append(this.id);sb.append(",");
    	sb.append("sequenceLength");sb.append(this.sequenceLength);sb.append(",");
    	sb.append("fileName");sb.append(this.fileName);sb.append(",");
    	sb.append("internalMd5Hash");sb.append(this.internalMd5Hash);sb.append(",");
    	sb.append("chunkTimeStamp");sb.append(this.chunkTimeStamp);sb.append(",");
    	sb.append("externalMd5Hash");sb.append(this.externalMd5Hash);

    	sb.append("]");
    	return sb.toString();

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
