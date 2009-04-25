/*
 * $Id: BaseFileTransferMappedRecord.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/net/BaseFileTransferMappedRecord.java $
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
package org.ikasan.connector.basefiletransfer.net;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.resource.cci.MappedRecord;
import org.apache.log4j.Logger;

/**
 * This class holds the file, its checksum and some meta data
 * 
 * @author Ikasan Development Team
 */
public class BaseFileTransferMappedRecord implements MappedRecord
{
    
    /** Key for storing the checksum */
    private final static String CHECKSUM_KEY = "checksum";
    /** Key for storing the checksum algorithm */
    private final static String CHECKSUM_ALGORITHM_KEY = "checksumAlgorithm";
    /** Key for storing the content */
    private final static String CONTENT_KEY = "content";
    /** Key for storing the created date time */
    private final static String CREATED_DATE_TIME_KEY = "createdDayTime";
    /** Key for storing the name */
    private final static String NAME_KEY = "name";
    /** Key for storing the size */
    private final static String SIZE_KEY = "size";
    
    /** GUID */
    private static final long serialVersionUID = 0L;

    /** Record Name */
    private String recordName;

    /** Record Description */
    private String recordDescription;

    /** Mapped record */
    private HashMap<String, Object> mappedRecord;

    /** Initialising the logger */
    private static Logger logger = Logger.getLogger(BaseFileTransferMappedRecord.class);

    /**
     * Default constructor
     */
    public BaseFileTransferMappedRecord()
    {
        this.mappedRecord = new HashMap<String, Object>();
    }

    /**
     * 
     * @param recordName
     * @param recordDescription
     */
    public BaseFileTransferMappedRecord(String recordName, String recordDescription)
    {
        this.setRecordName(recordName);
        this.setRecordShortDescription(recordDescription);
        this.mappedRecord = new HashMap<String, Object>(7);
    }

    /**
     * Complete constructor
     * 
     * @param name
     * @param size
     * @param checksum
     * @param checksumAlgorithmName
     * @param createdDayTime
     * @param payload
     * 
     */
    public BaseFileTransferMappedRecord(String name, long size, String checksum, String checksumAlgorithmName,
            Date createdDayTime, byte[] payload)
    {
        logger.debug("Constructor called with checksum = [" + checksum + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.mappedRecord = new HashMap<String, Object>(7);
        this.setName(name);
        this.setSize(size);
        this.setChecksum(checksum, checksumAlgorithmName);
        this.setCreatedDayTime(createdDayTime);
        this.setContent(payload);
    }

    /**
     * @return the checksum
     */
    public String getChecksum()
    {
        String checksum = (String) this.mappedRecord.get(CHECKSUM_KEY);
        logger.debug("Getting checksum = [" + checksum + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return checksum;
    }

    /**
     * @param checksum the checksum to set
     * @param algorithmName
     */
    public void setChecksum(String checksum, String algorithmName)
    {
        logger.debug("Setting checksum = [" + checksum + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.mappedRecord.put(CHECKSUM_KEY, checksum);
        this.mappedRecord.put(CHECKSUM_ALGORITHM_KEY, algorithmName);
    }

    /**
     * @return checksum algorithm
     */
    public String getChecksumAlgorithm()
    {
        return (String) this.mappedRecord.get(CHECKSUM_ALGORITHM_KEY);
    }

    /**
     * @return the createdDayTime
     */
    public Date getCreatedDayTime()
    {
        Date createdDayTime = (Date) this.mappedRecord.get(CREATED_DATE_TIME_KEY);
        logger.debug("Getting createdDayTime = [" + createdDayTime + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return createdDayTime;
    }

    /**
     * @param createdDayTime the createdDayTime to set
     */
    public void setCreatedDayTime(Date createdDayTime)
    {
        logger.debug("Setting createdDayTime = [" + createdDayTime + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.mappedRecord.put(CREATED_DATE_TIME_KEY, createdDayTime);
    }

    /**
     * @return the mappedRecord
     */
    public HashMap<String, Object> getMappedRecord()
    {
        logger.debug("Getting mappedRecord = [" + this.mappedRecord + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return mappedRecord;
    }

    /**
     * @param mappedRecord the mappedRecord to set
     */
    public void setMappedRecord(HashMap<String, Object> mappedRecord)
    {
        logger.debug("Setting this.mappedRecord = [" + mappedRecord + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.mappedRecord = mappedRecord;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        String name = (String) this.mappedRecord.get(NAME_KEY);
        logger.debug("Getting name = [" + name + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        logger.debug("Setting name = [" + name + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.mappedRecord.put(NAME_KEY, name);
    }

    /**
     * @return the payload
     */
    public byte[] getContent()
    {
        byte[] content = (byte[]) this.mappedRecord.get(CONTENT_KEY);
        logger.debug("Getting content = [" + content + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return content;
    }

    /**
     * @param content the payload to set
     */
    public void setContent(byte[] content)
    {
        logger.debug("Setting content = [" + content + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.mappedRecord.put(CONTENT_KEY, content);
    }

    /**
     * @return the size
     */
    public long getSize()
    {
        Long size = (Long) this.mappedRecord.get(SIZE_KEY);
        logger.debug("Getting size = [" + size + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return size.longValue();
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size)
    {
        logger.debug("Setting size = [" + size + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.mappedRecord.put(SIZE_KEY, size);
    }

    public void clear()
    {
        mappedRecord.clear();
    }

    public boolean containsValue(Object value)
    {
        return mappedRecord.containsValue(value);
    }

    public Set<Map.Entry<String, Object>> entrySet()
    {
        return mappedRecord.entrySet();
    }

    public boolean isEmpty()
    {
        return this.mappedRecord.isEmpty();
    }

    public Set<String> keySet()
    {
        return this.mappedRecord.keySet();
    }

    /**
     * Remove the key
     * 
     * @param key
     * @return The previous value or null
     */
    public Object remove(String key)
    {
        return mappedRecord.remove(key);
    }

    public Object remove(Object key)
    {
        return remove((String) key);
    }

    public int size()
    {
        return mappedRecord.size();
    }

    public Collection<Object> values()
    {
        return mappedRecord.values();
    }

    public boolean containsKey(Object key)
    {
        return this.mappedRecord.containsKey(key);
    }

    public Object get(Object key)
    {
        return this.mappedRecord.get(key);
    }

    public Object put(Object key, Object value)
    {
        return this.mappedRecord.put((String) key, value);
    }

    /*
     * As the put all must implement the MappedRecord interface we are unable 
     * to pass in a map of <String, Object>, hence suppressing warning 
     */ 
    @SuppressWarnings("unchecked")
    public void putAll(Map map)
    {
        this.mappedRecord.putAll(map);
    }

    public String getRecordName()
    {
        return this.recordName;
    }

    public String getRecordShortDescription()
    {
        return this.recordDescription;
    }

    public void setRecordName(String recordName)
    {
        this.recordName = recordName;
    }

    public void setRecordShortDescription(String recordDescription)
    {
        this.recordDescription = recordDescription;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return this.clone();
    }
}