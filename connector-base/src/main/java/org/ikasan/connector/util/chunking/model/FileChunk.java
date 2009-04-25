/*
 * $Id: FileChunk.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/util/chunking/model/FileChunk.java $
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

import org.ikasan.common.util.checksum.DigestChecksum;
import org.ikasan.common.util.checksum.Md5Checksum;

/**
 * Class representing a constituent chunk of a larger file
 * 
 * @author Ikasan Development Team
 * 
 */
public class FileChunk implements FileConstituentHandle
{

    /**
     * Primary key set by persistence mechanism
     */
    private Long id;

    /**
     * payload of this chunk
     */
    private byte[] content;

    /**
     * ordinal of this chunk within the sequence
     */
    private long ordinal;

    /**
     * The previously calculated hash value for the contents of this chunk
     */
    private String md5Hash;

    /**
     * FileChunkHeader representing the File of which this chunk is a
     * constituent
     */
    private FileChunkHeader fileChunkHeader;

    /**
     * Constructor for creating unpersisted (eg no id) FileChunk objects
     * 
     * @param fileChunkHeader
     * @param ordinal
     * @param content
     */
    public FileChunk(FileChunkHeader fileChunkHeader, long ordinal, byte[] content)
    {
        this(ordinal, fileChunkHeader);
        this.content = content;
    }

    /**
     * Constructor that allows the id to be set, but not the content Useful for
     * creating lightweight references to the persistent, object without the
     * content payload
     * 
     * @param fileChunkHeader
     * @param ordinal
     * @param id
     */
    public FileChunk(FileChunkHeader fileChunkHeader, Long ordinal, Long id)
    {
        this(ordinal, fileChunkHeader);
        this.id = id;
    }

    /**
     * Constructor
     * 
     * @param fileChunkHeader
     * @param ordinal
     */
    private FileChunk(Long ordinal, FileChunkHeader fileChunkHeader)
    {
        this();
        setOrdinal(ordinal);
        setFileChunkHeader(fileChunkHeader);
    }

    /**
     * Constructor
     * 
     * @param fileChunkHeader
     * @param ordinal
     * @param content
     */
    public FileChunk(FileChunkHeader fileChunkHeader, Long ordinal, byte[] content)
    {
        this(ordinal, fileChunkHeader);
        setContent(content);
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
     * Setter method to be called by persistence mechanism
     * 
     * @param id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Accessor method for payload
     * 
     * @return content
     */
    public byte[] getContent()
    {
        return content;
    }

    /**
     * Accessor method for ordinal
     * 
     * @return ordinal
     */
    public long getOrdinal()
    {
        return ordinal;
    }

    /**
     * Returns the value of the md5Hash property
     * 
     * This method does not calculate the md5 hash and will return null, unless
     * the calculateChecksum() method is previously called.
     * 
     * @return md5Hash
     */
    public String getMd5Hash()
    {
        return md5Hash;
    }

    /**
     * Accessor method for FileChunkHeader
     * 
     * @return fileChunkHeader
     */
    public FileChunkHeader getFileChunkHeader()
    {
        return fileChunkHeader;
    }

    public int compareTo(FileConstituentHandle other)
    {
        return new Long(getOrdinal()).compareTo(new Long(other.getOrdinal()));
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer(getClass().getName() + "["); //$NON-NLS-1$
        sb.append("id="); //$NON-NLS-1$
        sb.append(id);
        sb.append(","); //$NON-NLS-1$
        sb.append("ordinal="); //$NON-NLS-1$
        sb.append(ordinal);
        sb.append(","); //$NON-NLS-1$
        sb.append("fileChunkHeader="); //$NON-NLS-1$
        sb.append(fileChunkHeader);
        sb.append(","); //$NON-NLS-1$
        sb.append("]"); //$NON-NLS-1$
        return sb.toString();
    }

    /**
     * Uses MD5 to calculate a checksum value for the payload of this chunk
     */
    public void calculateChecksum()
    {
        DigestChecksum md5Checksum = new Md5Checksum();
        md5Checksum.reset();
        md5Checksum.update(content);
        setMd5Hash(md5Checksum.digestToString());
    }

    /*
     * ===================================================== The following
     * methods exist only to support Hibernate
     * =====================================================
     */

    /**
     * No argument constructor necessary for certain O/R mapping tools
     */
    private FileChunk()
    {
        // required empty constructor
    }

    /**
     * Set required for Hibernate
     * 
     * @param content
     */
    private void setContent(byte[] content)
    {
        this.content = content;
    }

    /**
     * Set required for Hibernate
     * 
     * @param ordinal
     */
    private void setOrdinal(long ordinal)
    {
        this.ordinal = ordinal;
    }

    /**
     * Set required for Hibernate
     * 
     * @param hash
     */
    private void setMd5Hash(String hash)
    {
        this.md5Hash = hash;
    }

    /**
     * Set required for Hibernate
     * 
     * @param fileChunkHeader
     */
    private void setFileChunkHeader(FileChunkHeader fileChunkHeader)
    {
        this.fileChunkHeader = fileChunkHeader;
    }

    /*
     * ====================================================== The preceding
     * methods exist only to support Hibernate
     * ======================================================
     */

}
