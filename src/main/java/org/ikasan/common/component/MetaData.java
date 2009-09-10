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

import java.io.Serializable;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang.time.*;
import org.ikasan.common.MetaDataInterface;

import javax.xml.XMLConstants;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * Meta Data extended by any data transport container, such as Payload or Envelope, to provide default implementation of
 * meta data attributes..
 * 
 * @author Ikasan Development Team
 */
public abstract class MetaData implements Serializable
{
    /** Serialise ID */
    private static final long serialVersionUID = 1L;

    /** The logger instance */
    private static Logger logger = Logger.getLogger(MetaData.class);

    /** standard XSD schema URL for when we serialise payload to XML */
    protected String schemaInstanceNSURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;

    /** XSD schema URL */
    protected String noNamespaceSchemaLocation;

    /** lifetime ID for the payload */
    protected String id;

    /** date/time the payload was originally created */
    protected Long timestamp = new Long(0L);

    /** date/time the payload was originally created */
    protected String timestampFormat = MetaDataInterface.DEFAULT_TIMESTAMP_FORMAT;

    /** timezone of date/time */
    protected String timezone = MetaDataInterface.DEFAULT_TIMEZONE;

    /** priority of this payload relative to other payloads */
    protected Integer priority = new Integer(0);

    /** content name */
    protected String name;

    /** content MIME type specification */
    protected String spec;

    /** content encoding ie base64, hex, noenc */
    protected String encoding;

    /** content format ie xml, csv */
    protected String format;

    /** content character set */
    protected String charset;

    /** content size */
    protected Long size = new Long(0L);

    /** content calculated checksum */
    protected String checksum;

    /** calculated checksum algorithm */
    protected String checksumAlg = MetaDataInterface.DEFAULT_CHECKSUM_ALG;

    /** originating system */
    protected String srcSystem;

    /** Defined destination systems as an XML string fragment */
    protected String targetSystems;

    /** defined process IDs as an XML string fragment */
    protected String processIds;

    /** defined where this data requires resubmission back into the flow */
    protected String resubmissionInfo;

    /**
     * Default Constructor
     */
    public MetaData()
    {
        // Do nothing
    }

    /**
     * Sets the W3C XML Schema instance namespace URI.
     * 
     * @param schemaInstanceNSURI - W3C XML Schema Instance Namespace URI.
     */
    public void setSchemaInstanceNSURI(String schemaInstanceNSURI)
    {
        this.schemaInstanceNSURI = schemaInstanceNSURI;
    }

    /**
     * Returns the W3C XML Schema instance namespace URI.
     * 
     * @return String
     * 
     */
    public String getSchemaInstanceNSURI()
    {
        return this.schemaInstanceNSURI;
    }

    /**
     * Sets the no namespace XML Schema location.
     * 
     * @param noNamespaceSchemaLocation - no namespace XML Schema location.
     */
    public void setNoNamespaceSchemaLocation(String noNamespaceSchemaLocation)
    {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
    }

    /**
     * Returns the no namespace XML Schema location.
     * 
     * @return String
     * 
     */
    public String getNoNamespaceSchemaLocation()
    {
        return this.noNamespaceSchemaLocation;
    }

    /**
     * Setter for id
     * 
     * @param id id to set
     */
    public void setId(final String id)
    {
        this.id = id;
        logger.debug("ID set to [" + this.id + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for id
     * 
     * @return id
     */
    public String getId()
    {
        logger.debug("Returning ID [" + this.id + "]."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.id;
    }

    /**
     * Setter for timestamp
     * 
     * @param timestamp timestamp to set
     */
    public void setTimestamp(final Long timestamp)
    {
        this.timestamp = timestamp;
        logger.debug("Timestamp set to [" + this.timestamp + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for timestamp
     * 
     * @return timestamp
     */
    public Long getTimestamp()
    {
        logger.debug("Returning timestamp [" + this.timestamp + "]."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.timestamp;
    }

    /**
     * @return the timestampFormat
     */
    public String getTimestampFormat()
    {
        logger.debug("Getting timestampFormat [" + this.timestampFormat + "]"); //$NON-NLS-1$//$NON-NLS-2$
        return this.timestampFormat;
    }

    /**
     * @param timestampFormat the timestampFormat to set
     */
    public void setTimestampFormat(String timestampFormat)
    {
        this.timestampFormat = timestampFormat;
        logger.debug("Setting timestampFormat [" + this.timestampFormat + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Setter for timezone
     * 
     * @param timezone timezone to set
     */
    public void setTimezone(final String timezone)
    {
        this.timezone = TimeZone.getTimeZone(timezone).getDisplayName(true, TimeZone.SHORT);
        logger.debug("Timezone set to [" + this.timezone + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for timezone
     * 
     * @return timezone
     */
    public String getTimezone()
    {
        logger.debug("Returning timezone [" + this.timezone + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.timezone;
    }

    /**
     * Setter for priority
     * 
     * @param priority priority to set
     */
    public void setPriority(final Integer priority)
    {
        this.priority = priority;
        logger.debug("Priority set to [" + this.priority + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for priority
     * 
     * @return Priority
     */
    public Integer getPriority()
    {
        logger.debug("Returning priority [" + this.priority + "]."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.priority;
    }

    /**
     * Setter for name attribute
     * 
     * @param name name to set
     */
    public void setName(final String name)
    {
        this.name = name;
        logger.debug("Name set to [" + this.name + "]."); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Getter for name
     * 
     * @return String
     */
    public String getName()
    {
        logger.debug("Returning name [" + this.name + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.name;
    }

    /**
     * Setter for spec attribute
     * 
     * @param spec spec to set
     */
    public void setSpec(final String spec)
    {
        this.spec = spec;
        logger.debug("Spec set to [" + this.spec + "]."); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Getter for spec
     * 
     * @return String
     */
    public String getSpec()
    {
        logger.debug("Returning spec [" + this.spec + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.spec;
    }

    /**
     * Setter for encoding
     * 
     * @param encoding encoding to set
     */
    public void setEncoding(final String encoding)
    {
        this.encoding = encoding;
        logger.debug("Encoding set to [" + this.encoding + "]."); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Getter for encoding
     * 
     * @return Encoding
     */
    public String getEncoding()
    {
        logger.debug("Returning encoding [" + this.encoding + "]."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.encoding;
    }

    /**
     * Setter for format
     * 
     * @param format format to set
     */
    public void setFormat(final String format)
    {
        this.format = format;
        logger.debug("Format set to [" + this.format + "]."); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Getter for format
     * 
     * @return Format
     */
    public String getFormat()
    {
        logger.debug("Returning format [" + this.format + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.format;
    }

    /**
     * Setter for character set
     * 
     * @param charset character set to set
     */
    public void setCharset(final String charset)
    {
        this.charset = charset;
        logger.debug("Charset set to [" + this.charset + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for character set
     * 
     * @return Charset
     */
    public String getCharset()
    {
        logger.debug("Returning charset [" + this.charset + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.charset;
    }

    /**
     * Setter for size attribute. This can be used to set the size attribute to an explicit value.
     * 
     * @param size size to set
     */
    public void setSize(final Long size)
    {
        this.size = size;
        logger.debug("Size set to [" + this.size + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Utility setter for <code>size</code>.
     */
    public abstract void setSize();

    /**
     * Getter for size
     * 
     * @return Long
     */
    public Long getSize()
    {
        logger.debug("Returning size [" + this.size + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.size;
    }

    /**
     * Setter for <code>checksum</code>. This can be used to set checksum to an explicit value as provided by the
     * <code>checksum</code> parameter.
     * 
     * @param checksum checksum to set
     */
    public void setChecksum(final String checksum)
    {
        this.checksum = checksum;
        logger.debug("Checksum set to [" + this.checksum + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Utility setter for checksum. When this setter is used, the checksum is calculated and set automatically. The
     * checksum is calculated based on the class default <code>MD5</code> algorithm.
     */
    public void setChecksum()
    {
        this.checksum = this.calculateChecksum();
        logger.debug("Checksum set to [" + this.checksum + "]."); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Getter for checksum
     * 
     * @return String
     */
    public String getChecksum()
    {
        logger.debug("Returning checksum [" + this.checksum + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.checksum;
    }

    /**
     * Utility setter for checksumAlg. When this setter is used, the checksum algorithm is set. The checksumAlg default
     * is <code>MD5</code> algorithm.
     * 
     * @param checksumAlg checksum algorithm to set
     */
    public void setChecksumAlg(final String checksumAlg)
    {
        this.checksumAlg = checksumAlg;
        logger.debug("ChecksumAlg set to [" + this.checksumAlg + "]."); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Getter for checksumAlg
     * 
     * @return String
     */
    public String getChecksumAlg()
    {
        logger.debug("Returning checksumAlg [" + this.checksumAlg + "]."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.checksumAlg;
    }

    /**
     * Setter for srcSystem
     * 
     * @param srcSystem source system to set
     */
    public void setSrcSystem(final String srcSystem)
    {
        this.srcSystem = srcSystem;
        logger.debug("SrcSystem set to [" + this.srcSystem + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for srcSystem
     * 
     * @return String
     */
    public String getSrcSystem()
    {
        logger.debug("Returning srcSystem [" + this.srcSystem + "]."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.srcSystem;
    }

    /**
     * Getter for targetSystems
     * 
     * @return String
     */
    public String getTargetSystems()
    {
        logger.debug("Returning targetSystems [" + this.targetSystems + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.targetSystems;
    }

    /**
     * Setter for targetSystems
     * 
     * @param targetSystems target systems to set
     */
    public void setTargetSystems(String targetSystems)
    {
        this.targetSystems = targetSystems;
        logger.debug("TargetSystems set to [" + this.targetSystems + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for processIds
     * 
     * @return String
     */
    public String getProcessIds()
    {
        logger.debug("Returning processIds [" + this.processIds + "]."); //$NON-NLS-1$//$NON-NLS-2$
        return this.processIds;
    }

    /**
     * Setter for processIds
     * 
     * @param processIds process ids to set
     */
    public void setProcessIds(String processIds)
    {
        this.processIds = processIds;
        logger.debug("ProcessIds set to [" + this.processIds + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns the formatted timestamp
     * 
     * @return formatted timestamp
     */
    public String getFormattedTimestamp()
    {
        return DateFormatUtils.format(this.timestamp.longValue(), this.timestampFormat, TimeZone
            .getTimeZone(this.timezone));
    }

    /**
     * @return the resubmissionInfo
     */
    public String getResubmissionInfo()
    {
        logger.debug("Getting resubmissionInfo [" + this.resubmissionInfo + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.resubmissionInfo;
    }

    /**
     * @param resubmissionInfo the resubmissionInfo to set
     */
    public void setResubmissionInfo(String resubmissionInfo)
    {
        this.resubmissionInfo = resubmissionInfo;
        logger.debug("Setting resubmissionInfo [" + this.resubmissionInfo + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * no implementation required - method needed only as a requirement of introspection on this class.
     * 
     * @param formattedTimestamp formatted timestamp to set
     */
    public void setFormattedTimestamp(@SuppressWarnings("unused") String formattedTimestamp)
    {
        // no implementation required - method needed only
        // as a requirement of introspection on this class.
    }

    /**
     * Method used to calculate the checksum of the <code>content</code> byte array. By defaul, the checksum is
     * calculated using the <code>MD5</code> algorithm.
     * 
     * @return The MD5 checksum of the <code>content</code> as a hexadecimal <code>String</code>.
     */
    protected abstract String calculateChecksum();

    /**
     * Create a formatted string detailing the payload id of the incoming payload.
     * 
     * @return String
     */
    public abstract String idToString();

    /**
     * Default id generator
     * 
     * @return String
     */
    protected static String generateId()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * Default timestamp generator
     * 
     * @return Long
     */
    protected static Long generateTimestamp()
    {
        return new Long(new java.util.Date().getTime());
    }

    /**
     * Creates a <code>String<code> representation of the <code>Payload</code> showing all the objects properties and an
     * many characters of the content as specified in the <code>cLength</code> parameter. If the <code>cLength
     * </code> is negative, the
     * content is display from the end of the content string.
     * 
     * @param cLength Used to restrict the length of the payload to a specific number of chars.
     * 
     * @return A formatted <code>String</code> representing the <code>Payload</code> object.
     */
    public String toString(int cLength)
    {
        StringBuilder sb = new StringBuilder(512);
        logger.debug("cLength [" + cLength + "]");
        sb.append("Id               = ["); //$NON-NLS-1$
        sb.append(this.id);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("timestamp        = [");//$NON-NLS-1$
        sb.append(this.timestamp);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("timestampFormat  = [");//$NON-NLS-1$
        sb.append(this.timestampFormat);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Timezone         = [");//$NON-NLS-1$
        sb.append(this.timezone);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Priority         = [");//$NON-NLS-1$
        sb.append(this.priority);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Name             = [");//$NON-NLS-1$
        sb.append(this.name);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Specification    = [");//$NON-NLS-1$
        sb.append(this.spec);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Encoding         = [");//$NON-NLS-1$
        sb.append(this.encoding);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Format           = [");//$NON-NLS-1$
        sb.append(this.format);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Charset          = [");//$NON-NLS-1$
        sb.append(this.charset);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Size(bytes)      = [");//$NON-NLS-1$
        sb.append(this.size);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Checksum (Hex)   = [");//$NON-NLS-1$
        sb.append(this.checksum);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Source System    = [");//$NON-NLS-1$
        sb.append(this.srcSystem);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Target Systems   = [");//$NON-NLS-1$
        sb.append(this.targetSystems);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("Process IDs      = [");//$NON-NLS-1$
        sb.append(this.processIds);
        sb.append("]\n");//$NON-NLS-1$
        sb.append("ResubmissionInfo = [");//$NON-NLS-1$
        sb.append(this.resubmissionInfo);
        sb.append("]\n");//$NON-NLS-1$
        return sb.toString();
    }
}
