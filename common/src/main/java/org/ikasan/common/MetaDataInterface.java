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
package org.ikasan.common;

/**
 * Default meta-data required operations across all transport containers
 * such as Payload and Envelope.
 * 
 * @author Ikasan Development Team
 */
public interface MetaDataInterface
{
    /** Payload general constant for undefined values */
    public static final String UNDEFINED = "undefined"; //$NON-NLS-1$

    /** The default date/time pattern */
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyyMMddHHmmssSSS"; //$NON-NLS-1$
    
    /** The default timezone */
    public static final String DEFAULT_TIMEZONE = "UTC"; //$NON-NLS-1$

    /** The default checksum algorithm */
    public static final String DEFAULT_CHECKSUM_ALG = "MD5"; //$NON-NLS-1$

    /** Meta-data identifier constant for selector use */
    public static final String ID = "id"; //$NON-NLS-1$
    
    /** Meta-data name constant for selector use */
    public static final String NAME = "name"; //$NON-NLS-1$
    
    /** Meta-data source system constant for selector use */
    public static final String SRC_SYSTEM = "srcSystem"; //$NON-NLS-1$
    
    /** Meta-data spec for selector use */
    public static final String SPEC = "spec"; //$NON-NLS-1$
    
    /** MapMessage table prefix for Envelopes */
    public static final String ENVELOPE_PREFIX = "envelope_"; //$NON-NLS-1$
    
    /** MapMessage table prefix for Payloads */
    public static final String PAYLOAD_PREFIX = "payload_"; //$NON-NLS-1$
   
    /**
     * Sets the W3C XML Schema instance namespace URI.
     *
     * @param schemaInstanceNSURI - W3C XML Schema Instance Namespace URI.
     */
    public void setSchemaInstanceNSURI(String schemaInstanceNSURI);

    /**
     * Returns the W3C XML Schema instance namespace URI.
     * @return String
     *
     */
    public String getSchemaInstanceNSURI();

    /**
     * Sets the no namespace XML Schema location.
     *
     * @param noNamespaceSchemaLocation - no namespace XML Schema location.
     */
    public void setNoNamespaceSchemaLocation(String noNamespaceSchemaLocation);

    /**
     * Returns the no namespace XML Schema location.
     * @return String
     *
     */
    public String getNoNamespaceSchemaLocation();

    /**
     * Setter for id
     * 
     * @param id
     */
    public void setId(final String id);

    /**
     * Getter for id
     * 
     * @return id
     */
    public String getId();

    /**
     * Setter for timestamp
     * 
     * @param timestamp
     */
    public void setTimestamp(final Long timestamp);

    /**
     * Getter for timestamp
     * 
     * @return timestamp
     */
    public Long getTimestamp();

    /**
     * @return the timestampFormat
     */
    public String getTimestampFormat();

    /**
     * @param timestampFormat the timestampFormat to set
     */
    public void setTimestampFormat(String timestampFormat);

    /**
     * Setter for timezone
     * 
     * @param timezone
     */
    public void setTimezone(final String timezone);

    /**
     * Getter for timezone
     * 
     * @return timezone
     */
    public String getTimezone();

    /**
     * Setter for priority
     * 
     * @param priority
     */
    public void setPriority(final Integer priority);

    /**
     * Getter for priority
     * 
     * @return Priority
     */
    public Integer getPriority();

    /**
     * Setter for name attribute
     * @param name 
     */
    public void setName(final String name);

    /**
     * Getter for name
     * @return String
     */
    public String getName();

    /**
     * Setter for spec attribute
     * @param spec 
     */
    public void setSpec(final String spec);

    /**
     * Getter for spec
     * @return String
     */
    public String getSpec();

    /**
     * Setter for encoding
     * @param encoding 
     */
    public void setEncoding(final String encoding);

    /**
     * Getter for encoding
     * 
     * @return Encoding
     */
    public String getEncoding();

    /**
     * Setter for format
     * @param format 
     */
    public void setFormat(final String format);

    /**
     * Getter for format
     * 
     * @return Format
     */
    public String getFormat();

    /**
     * Setter for character set
     * 
     * @param charset
     */
    public void setCharset(final String charset);

    /**
     * Getter for character set
     * 
     * @return Charset
     */
    public String getCharset();

//    /**
//     * Setter for size attribute. This can be used to set the size attribute to
//     * an explicit value.
//     * @param size 
//     */
//    public void setSize(final Long size);
//
//    /**
//     * Utility setter for <code>size</code>. When this setter is used, the
//     * size is automatically set to the size of the contained business data.
//     * 
//     */
//    public void setSize();

    /**
     * Getter for size
     * 
     * @return Long
     */
    public Long getSize();

    /**
     * Setter for <code>checksum</code>. This can be used to set checksum to
     * an explicit value as provided by the <code>checksum</code> parameter.
     * 
     * @param checksum 
     */
    public void setChecksum(final String checksum);

    /**
     * Utility setter for checksum. When this setter is used, the checksum is
     * calculated and set automatically. The checksum is calculated based on the
     * class default <code>MD5</code> algorithm.
     */
    public void setChecksum();

    /**
     * Getter for checksum
     * @return String
     */
    public String getChecksum();

    /**
     * Utility setter for checksumAlg. When this setter is used, the checksum 
     * algorithm is set. The checksumAlg default is <code>MD5</code> algorithm.
     * 
     * @param checksumAlg 
     */
    public void setChecksumAlg(final String checksumAlg);

    /**
     * Getter for checksumAlg
     * @return String
     */
    public String getChecksumAlg();

    /**
     * Setter for srcSystem
     * @param srcSystem
     */
    public void setSrcSystem(final String srcSystem);

    /**
     * Getter for srcSystem
     * @return String
     */
    public String getSrcSystem();

    /**
     * Getter for targetSystems
     * @return String
     */
    public String getTargetSystems();

    /**
     * Setter for targetSystems
     *
     * @param targetSystems
     */
    public void setTargetSystems(String targetSystems);

    /**
     * Getter for processIds
     * @return String
     */
    public String getProcessIds();

    /**
     * Setter for processIds
     *
     * @param processIds
     */
    public void setProcessIds(String processIds);

    /**
     * Setter for resubmissionInfo
     *
     * @param resubmissionInfo
     */
    public void setResubmissionInfo(String resubmissionInfo);

    /**
     * Getter for resubmissionInfo
     *
     * @return resubmissionInfo
     */
    public String getResubmissionInfo();

    /**
     * Returns the formatted timestamp
     * @return formatted timestamp
     */
    public String getFormattedTimestamp();
    
    /**
     * This method does not do anything - it is required to be implemenyted
     * to satisfy the need of the instrospection implementation supporting the 
     * brokering payloads/envelopes to and from JMS messages.
     * 
     * It will not set the formattedTimestamp!
     * 
     * @param formattedTimestamp 
     */
    public void setFormattedTimestamp(String formattedTimestamp);
    
    /**
     * String representation of the id(s)
     * 
     * @return String formatted representation of the id
     */
    public String idToString();
    
}
